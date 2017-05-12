package be.i8c.wso2.msf4j.lora.services.proximus;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by yanglin on 12/05/17.
 */

@Component
@Profile("proximus")
public class ProximusAuthenticator {

    private static final Logger logger = LogManager.getLogger(ProximusAuthenticator.class);

    private HttpTransport httpTransport;
    private JsonFactory jsonFactory;
    private GenericUrl tokenApiUrl;
    private TokenRequest tokenRequest;
    private String currentAccessToken;
    private LocalDateTime accessTokenExpire;

    @Value("${proximus.tokenAPIUrl}")
    private String tokenAPIUrl;

    @Value("${proximus.APIKey}")
    private String apiKey;

    @Value("${proximus.APISecret}")
    private String apiSecret;




    @PostConstruct
    private void init()
    {
        httpTransport = new NetHttpTransport();
        jsonFactory = new GsonFactory();
        tokenApiUrl = new GenericUrl(this.tokenAPIUrl);
        this.tokenRequest = new TokenRequest(this.httpTransport,this.jsonFactory,this.tokenApiUrl,"client_credentials")
                .setClientAuthentication(new ClientParametersAuthentication(this.apiKey,this.apiSecret));
    }


    private void requestToken() throws TokenResponseException
    {
        try {
            logger.debug("try to request access token");
            TokenResponse tokenResponse = this.tokenRequest.execute();
            this.currentAccessToken = tokenResponse.getAccessToken();
            this.accessTokenExpire = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds());
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                logger.error(e.getDetails());
                throw e;
            } else {
                logger.error(e.getMessage());
            }
        } catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    public String getAccessToken() throws TokenResponseException
    {
        if (LocalDateTime.now().isAfter(this.accessTokenExpire))
            this.requestToken();
        return this.currentAccessToken;
    }
}
