package predictions;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class PredictionsConfiguration extends Configuration {

    @Valid
    @NotNull
    private String eventName = "World Cup 2018";

	@Valid
	@NotNull
	private String publicDomain = "pronostics2016.com";

    @Valid
    @NotNull
	private String defaultCommunity = "grand-est";

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

    public String getPublicDomain() {
        return publicDomain;
    }

    public void setPublicDomain(String publicDomain) {
        this.publicDomain = publicDomain;
    }

    public String getDefaultCommunity() {
        return defaultCommunity;
    }

    public void setDefaultCommunity(String defaultCommunity) {
        this.defaultCommunity = defaultCommunity;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
