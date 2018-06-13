package predictions;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class PredictionsConfiguration extends Configuration {

	private boolean liveWebSite = false;

    @Valid
    @NotNull
    private String eventName = "World Cup 2018";

	@Valid
	@NotNull
	private String publicDomain = "pronostics2016.com";

	@Valid
	@NotNull
	private String smtpServer = "smtp." + publicDomain;

	@Valid
	@NotNull
	private int smtpPort = 465;

    @Valid
    @NotNull
	private String defaultCommunity = "grand-est";

	@Valid
	@NotNull
	private String footballDataApiKey;

	@Valid
	@NotNull
	private String swaggerApiHost = "http://locahost:9000";

	@Valid
	@NotNull
	private String googleReCaptchaSecretKey;

	@Valid
	private boolean googleReCaptchaEnabled = true;

	@Valid
	@NotNull
	private Path dataFolder = Paths.get("data");

	private Set<String> administratorAccounts;

	public String getGoogleReCaptchaSecretKey() {
		return googleReCaptchaSecretKey;
	}
	
	public boolean isGoogleReCaptchaEnabled() {
		return googleReCaptchaEnabled;
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

	public boolean isLiveWebSite() {
		return liveWebSite;
	}

	public void setLiveWebSite(boolean liveWebSite) {
		this.liveWebSite = liveWebSite;
	}

	public String getFootballDataApiKey() {
		return footballDataApiKey;
	}

	public void setFootballDataApiKey(String footballDataApiKey) {
		this.footballDataApiKey = footballDataApiKey;
	}

	public String getSwaggerApiHost() {
		return swaggerApiHost;
	}

	public void setSwaggerApiHost(String swaggerApiHost) {
		this.swaggerApiHost = swaggerApiHost;
	}

	public void setGoogleReCaptchaSecretKey(String googleReCaptchaSecretKey) {
		this.googleReCaptchaSecretKey = googleReCaptchaSecretKey;
	}

	public void setGoogleReCaptchaEnabled(boolean googleReCaptchaEnabled) {
		this.googleReCaptchaEnabled = googleReCaptchaEnabled;
	}

	public DataSourceFactory getDatabase() {
		return database;
	}

	public void setDatabase(DataSourceFactory database) {
		this.database = database;
	}

	public Set<String> getAdministratorAccounts() {
		return administratorAccounts;
	}

	public void setAdministratorAccounts(Set<String> administratorAccounts) {
		this.administratorAccounts = administratorAccounts;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public Path getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(Path dataFolder) {
		this.dataFolder = dataFolder;
	}
}
