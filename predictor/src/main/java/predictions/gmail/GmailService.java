package predictions.gmail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.PredictionsConfiguration;

public class GmailService implements Managed {

    private final static Logger LOGGER = LoggerFactory.getLogger(GmailService.class);
    
    /** Application name. */
	private String applicationName;

    Path credentialsFolder;
    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory dataStoreFactory;

    private Gmail service;

    public GmailService( String applicationName, PredictionsConfiguration configuration ) throws IOException {
		this.applicationName = applicationName;
    	credentialsFolder = Paths.get( configuration.getoAuth2CredentialsFolder() );
    	dataStoreFactory = new FileDataStoreFactory( credentialsFolder.toFile() );
	}
   
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this service.
     *
     * If modifying these scopes, delete your previously saved credentials
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        // Load client secrets.
        Path clientSecretFile = credentialsFolder.resolve("client_secret.json");

        if (!Files.exists(clientSecretFile)) {
            LOGGER.error("File {} could not be read : mail sending disabled", clientSecretFile.toAbsolutePath().toString());
            return null;
        }

        try (InputStream in = Files.newInputStream(clientSecretFile)) {
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                            .setDataStoreFactory(dataStoreFactory)
                            .setAccessType("offline")
                            .build();
            Credential credential = new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");
            LOGGER.info("Credentials saved to " + credentialsFolder.toAbsolutePath().toString());
            return credential;
        }
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName( applicationName )
                .build();
    }
    
    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage email)
        throws MessagingException, IOException {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
          email.writeTo(baos);
          String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
          Message message = new Message();
          message.setRaw(encodedEmail);
          return message;
      }
    }
    
    public void sendEmail( String from, String to, String subject, String htmlMessage ) throws IOException, MessagingException {
    	MimeMessage email = new MimeMessage( (Session) null );
		email.setSubject( subject );
		email.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to, false));
		email.setContent(htmlMessage, "text/html; charset=utf-8");
		Message message = createMessageWithEmail( email );
		service.users().messages().send(from!=null ? from : "me", message).execute();
    }

    @Override
    public void start() throws Exception {
        service = getGmailService();
    }

    @Override
    public void stop() throws Exception {

    }
}