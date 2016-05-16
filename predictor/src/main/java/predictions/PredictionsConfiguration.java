package predictions;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class PredictionsConfiguration extends Configuration {

	@Valid
	@NotNull
	private String googleReCaptchaSecretKey;

	@Valid
	private boolean googleReCaptchaEnabled = true;
	
	@Valid
	@NotNull
	private String oAuth2CredentialsFolder;

	public String getGoogleReCaptchaSecretKey() {
		return googleReCaptchaSecretKey;
	}
	
	public boolean isGoogleReCaptchaEnabled() {
		return googleReCaptchaEnabled;
	}
	
	public String getoAuth2CredentialsFolder() {
		return oAuth2CredentialsFolder;
	}

	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}
