package predictions.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class GoogleSigninConfiguration {

    @Valid
    @NotNull
    private String clientId;

    @Valid
    @NotNull
    private String clientSecret;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
