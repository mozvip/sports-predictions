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
    
    public String getGoogleReCaptchaSecretKey() {
		return googleReCaptchaSecretKey;
	}
	
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();
    
    public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}
