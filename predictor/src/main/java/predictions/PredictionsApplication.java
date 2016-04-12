package predictions;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import predictions.auth.CommunityFilter;
import predictions.auth.PredictorAuthorizer;
import predictions.auth.PredictorBasicAuthenticator;
import predictions.auth.PredictorOAuthAuthenticator;
import predictions.model.ActualResultDAO;
import predictions.model.MatchPredictionDAO;
import predictions.model.User;
import predictions.model.UserDAO;
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
		
		environment.servlets().addFilter("communityFilter", CommunityFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		environment.jersey().register(new UserResource(userDAO, matchPredictionDAO, actualResultDAO));
		environment.jersey().register(new UserListResource(userDAO, matchPredictionDAO));
		environment.jersey().register(new ChangePasswordResource(userDAO));
		environment.jersey().register(new RankingsResource(userDAO));
		environment.jersey().register(new ScoreResource(actualResultDAO, matchPredictionDAO, userDAO));

		PredictorBasicAuthenticator basicAuthenticator = new PredictorBasicAuthenticator( userDAO );
		
	    environment.jersey().register(new AuthDynamicFeature(
	            new BasicCredentialAuthFilter.Builder<User>()
	                .setAuthenticator( basicAuthenticator )
	                .setAuthorizer(new PredictorAuthorizer())
	                .setRealm("Euro 2016 Application Realm")
	                .buildAuthFilter()));
	    
		PredictorOAuthAuthenticator oAuthAuthenticator = new PredictorOAuthAuthenticator( userDAO );

	    environment.jersey().register(new AuthDynamicFeature(
	            new OAuthCredentialAuthFilter.Builder<User>()
	            	.setAuthenticator( oAuthAuthenticator )
	                .setAuthorizer(new PredictorAuthorizer())
	                .setRealm("Euro 2016 Application Realm")
	                .buildAuthFilter()));

	    environment.jersey().register(RolesAllowedDynamicFeature.class);

	    environment.jersey().register(new AuthValueFactoryProvider.Binder<User>(User.class));

	}

	public static void main(String[] args) throws Exception {
		new PredictionsApplication().run(args);
	}

}
