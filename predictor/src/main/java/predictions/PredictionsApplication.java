package predictions;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import predictions.auth.CommunityBasicCredentialAuthFilter;
import predictions.auth.CommunityFilter;
import predictions.auth.PredictorAuthorizer;
import predictions.auth.PredictorBasicAuthenticator;
import predictions.auth.PredictorOAuthAuthenticator;
import predictions.model.ActualResultDAO;
import predictions.model.MatchPredictionDAO;
import predictions.model.User;
import predictions.model.UserDAO;
import predictions.phases.PhaseFilter;
import predictions.resources.ChangePasswordResource;
import predictions.resources.RankingsResource;
import predictions.resources.ScoreResource;
import predictions.resources.UserListResource;
import predictions.resources.UserResource;

public class PredictionsApplication extends Application<PredictionsConfiguration> {

	@Override
	public String getName() {
		return "Euro 2016 Predictions";
	}

	@Override
	public void initialize(Bootstrap<PredictionsConfiguration> bootstrap) {

		bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
		bootstrap.addBundle(new ViewBundle());

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

		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
		final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
		final MatchPredictionDAO matchPredictionDAO = jdbi.onDemand(MatchPredictionDAO.class);
		final ActualResultDAO actualResultDAO = jdbi.onDemand(ActualResultDAO.class);
		
		environment.jersey().setUrlPattern("/api");
		
		environment.servlets().addFilter("CommunityFilter", CommunityFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		environment.servlets().addFilter("SitePhaseFilter", PhaseFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		// swagger
		environment.jersey().register(new ApiListingResource());
		environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		environment.jersey().register(new UserResource(userDAO, matchPredictionDAO, actualResultDAO));
		environment.jersey().register(new UserListResource(userDAO, matchPredictionDAO));
		environment.jersey().register(new ChangePasswordResource(userDAO));
		environment.jersey().register(new RankingsResource(userDAO));
		environment.jersey().register(new ScoreResource(actualResultDAO, matchPredictionDAO, userDAO));
		
		Dynamic corsFilter = environment.servlets().addFilter("CrossOriginFilter", CrossOriginFilter.class);
		corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		corsFilter.setInitParameter("allowedOrigins", "*");
		corsFilter.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		corsFilter.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

		PredictorBasicAuthenticator basicAuthenticator = new PredictorBasicAuthenticator( userDAO );
		
		CommunityBasicCredentialAuthFilter<User> basicCredentialAuthFilter = new CommunityBasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator( basicAuthenticator )
            .setAuthorizer(new PredictorAuthorizer())
            .setRealm("Euro 2016 Application Realm")
            .buildAuthFilter();
	    
		PredictorOAuthAuthenticator oAuthAuthenticator = new PredictorOAuthAuthenticator( userDAO );

        OAuthCredentialAuthFilter<User> oauthCredentialAuthFilter = new OAuthCredentialAuthFilter.Builder<User>()
        	.setAuthenticator( oAuthAuthenticator )
            .setAuthorizer(new PredictorAuthorizer())
            .setRealm("Euro 2016 Application Realm")
            .buildAuthFilter();
	    
	    List<AuthFilter<? extends Object, User>> filters = Lists.newArrayList(basicCredentialAuthFilter, oauthCredentialAuthFilter);
	    environment.jersey().register(new AuthDynamicFeature(new ChainedAuthFilter(filters)));
	    environment.jersey().register(RolesAllowedDynamicFeature.class);
	    environment.jersey().register(new AuthValueFactoryProvider.Binder<User>(User.class));
	    
	    BeanConfig config = new BeanConfig();
	    config.setTitle("Sports Predictions Application");
	    config.setVersion("1.0.0");
	    config.setBasePath("/api");
	    config.setResourcePackage("predictions");
	    config.setScan(true);

	}

	public static void main(String[] args) throws Exception {
		new PredictionsApplication().run(args);
	}

}
