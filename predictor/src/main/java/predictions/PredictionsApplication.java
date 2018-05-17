package predictions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.mozvip.footballdata.FootballDataClient;
import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;
import predictions.auth.*;
import predictions.model.db.*;
import predictions.phases.PhaseFilter;
import predictions.phases.PhaseManager;
import predictions.resources.*;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import java.util.EnumSet;
import java.util.List;

public class PredictionsApplication extends Application<PredictionsConfiguration> {

    @Override
	public String getName() {
		return "Sports Predictions";
	}

	@Override
	public void initialize(Bootstrap<PredictionsConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
		bootstrap.addBundle(new MigrationsBundle<PredictionsConfiguration>() {
			public DataSourceFactory getDataSourceFactory(
					PredictionsConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
	}

	@Override
	public void run(PredictionsConfiguration configuration,
			Environment environment) throws Exception {

		environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		final JdbiFactory factory = new JdbiFactory();
		final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");

		final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
		final MatchPredictionDAO matchPredictionDAO = jdbi.onDemand(MatchPredictionDAO.class);
		final ActualResultDAO actualResultDAO = jdbi.onDemand(ActualResultDAO.class);
		final CompetitionDAO competitionDAO = jdbi.onDemand(CompetitionDAO.class);
		final CommunityDAO communityDAO = jdbi.onDemand(CommunityDAO.class);

		PhaseManager phaseManager = new PhaseManager();
		environment.lifecycle().manage(phaseManager);

		environment.jersey().setUrlPattern("/api");

		CommunityFilter communityFilter = new CommunityFilter(configuration.getDefaultCommunity());
		
		environment.servlets().addFilter("CommunityFilter", communityFilter).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		if (configuration.isLiveWebSite()) {
			PhaseFilter phaseFilter = new PhaseFilter(phaseManager, communityDAO);
			environment.servlets().addFilter("SitePhaseFilter", phaseFilter).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		}

		// swagger
		environment.jersey().register(new ApiListingResource());
		environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		DefaultHttpClient client = new DefaultHttpClient();
	    ClientConnectionManager mgr = client.getConnectionManager();
	    HttpParams params = client.getParams();
	    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);		

		environment.jersey().register(new GoogleSigninResource(configuration.getGoogleSignin().getClientId()));

		environment.jersey().register(new UserResource(phaseManager, userDAO, matchPredictionDAO, actualResultDAO, communityDAO, client, configuration));
		environment.jersey().register(new ChangePasswordResource(userDAO));
		environment.jersey().register(new ValidateEmailResource(userDAO));
		environment.jersey().register(new AdminResource(userDAO));
		environment.jersey().register(new GameResource( matchPredictionDAO ));
		environment.jersey().register(new CommunityResource( communityDAO ));
		environment.jersey().register(new ScoreResource(actualResultDAO, matchPredictionDAO, userDAO));

		FootballDataClient footballDataClient = FootballDataClient.Builder(configuration.getFootballDataApiKey()).build();

		environment.jersey().register(new CompetitionResource(footballDataClient, competitionDAO, actualResultDAO));

		Dynamic corsFilter = environment.servlets().addFilter("CrossOriginFilter", CrossOriginFilter.class);
		corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		corsFilter.setInitParameter("allowedOrigins", "*");
		corsFilter.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin,Authorization");
		corsFilter.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

		PredictorBasicAuthenticator basicAuthenticator = new PredictorBasicAuthenticator( userDAO );
		
		CommunityBasicCredentialAuthFilter<User> basicCredentialAuthFilter = new CommunityBasicCredentialAuthFilter.Builder<User>(configuration.getDefaultCommunity())
            .setAuthenticator( basicAuthenticator )
            .setAuthorizer(new PredictorAuthorizer(configuration.getAdministratorAccounts()))
            .setRealm(configuration.getEventName() + " Application Realm")
            .buildAuthFilter();
	    
		PredictorGoogleAuthenticator oAuthAuthenticator = new PredictorGoogleAuthenticator(configuration.getGoogleSignin().getClientId(), userDAO);

		CommunityOAuthCredentialAuthFilter oauthCredentialAuthFilter = new CommunityOAuthCredentialAuthFilter.Builder(configuration.getDefaultCommunity())
        	.setAuthenticator( oAuthAuthenticator )
            .setAuthorizer(new PredictorAuthorizer(configuration.getAdministratorAccounts()))
			.setPrefix("Bearer")
            .setRealm(configuration.getEventName() + " Application Realm")
            .buildAuthFilter();
	    
	    List<AuthFilter<? extends Object, User>> filters = Lists.newArrayList(basicCredentialAuthFilter, oauthCredentialAuthFilter);
	    environment.jersey().register(new AuthDynamicFeature(new ChainedAuthFilter(filters)));
	    environment.jersey().register(RolesAllowedDynamicFeature.class);
	    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
	    
	    BeanConfig config = new BeanConfig();
	    config.setTitle("Sports Predictions Application");
	    config.setVersion("1.0.0");
	    config.setHost(configuration.getSwaggerApiHost());
	    config.setBasePath("/api");
	    config.setResourcePackage("predictions");
	    config.setScan(true);

		for (String email : configuration.getAdministratorAccounts()) {
			userDAO.setAdmin(email, true);
		}
	}

	public static void main(String[] args) throws Exception {
		new PredictionsApplication().run(args);
	}

}
