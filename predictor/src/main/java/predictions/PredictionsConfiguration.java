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
	@NotNull
	private String smtpHost;

	@Valid
	@NotNull
	private Integer smtpPort;

	@Valid
	private String smtpLogin;

	@Valid
	private String smtpPassword;

	public String getGoogleReCaptchaSecretKey() {
		return googleReCaptchaSecretKey;
	}

	public String getSmtpHost() {
		return smtpHost;
	}
	
	public Integer getSmtpPort() {
		return smtpPort;
	}

	public String getSmtpLogin() {
		return smtpLogin;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}
