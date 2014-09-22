import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.verifone.hashserver.ssl.SecureSslContextFactory;

public class SSLTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureSslContextFactory.class);

    private static final String PROTOCOL = "TLS";

    private static final SSLContext CLIENT_CONTEXT;
    
    public static final int READ_TIMEOUT = 5;

    static {

        SSLContext clientContext = null;

        // get keystore and trustore locations and passwords
        String keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        try {

            if (keyStoreLocation == null) {
                throw new NullPointerException("keyStoreLocation");
            }

            if (keyStorePassword == null) {
                throw new NullPointerException("keyStorePassword");
            }

            if (trustStoreLocation == null) {
                throw new NullPointerException("trustStoreLocation");
            }

            if (trustStorePassword == null) {
                throw new NullPointerException("trustStorePassword");
            }

            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream keystoreInputStream = new FileInputStream(keyStoreLocation);
            // FIXME cerrar stream
            ks.load(keystoreInputStream, keyStorePassword.toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());

            LOGGER.debug("Keystore manager factory initalized");

            // truststore
            KeyStore ts = KeyStore.getInstance("JKS");
            InputStream trustStoreInputStream = new FileInputStream(trustStoreLocation);
            // FIXME cerrar stream
            ts.load(trustStoreInputStream, trustStorePassword.toCharArray());

            // set up trust manager factory to use our trust store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            LOGGER.debug("TrustStore manager factory initalized");

            // Initialize the SSLContext to work with our key managers.
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            LOGGER.info("Server-side SSLContext initialized");

        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        CLIENT_CONTEXT = clientContext;
    }


    public static void main(String[] args)
    {

        // trustStore has the certificates that are presented by the server that
        // this application is to trust
        System.setProperty("javax.net.ssl.trustStore", "keys/client.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        // keystore has the certificates presented to the server when a server
        // requests one to authenticate this application to the server
        System.setProperty("javax.net.ssl.keyStore", "keys/client.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        try {
            
            RequestConfig config = RequestConfig.custom()
              .setConnectTimeout(READ_TIMEOUT * 1000)
              .setConnectionRequestTimeout(READ_TIMEOUT * 1000)
              .setSocketTimeout(READ_TIMEOUT * 1000).build();
            
            HttpClient client = HttpClientBuilder.create()
                .setSslcontext(CLIENT_CONTEXT)
                .setDefaultRequestConfig(config)
                .build();
            
            HttpPost post = new HttpPost();
            post.setURI(new URI("https://localhost:8443"));
            post.setHeader("Content-Type", "application/xml");
            
            String xml = "<VHTS>" +
  "<REQ>" +
    "<VAID>VA998098</VAID>" +
    "<VPWD>NvJ9K13WoyYeQGuBeAH1tB6hBTBoJio8</VPWD>" +
    "<CMD>TOKENIZE</CMD>" +
    "<EID>E342</EID>" +
    "<PAN>4005550000000019</PAN>" +
    "<EXP>0918</EXP>" +
    "<CNAME>Test Card</CNAME>" +
    "<CADDR>1234 Test RD</CADDR>" +
    "<CPCODE>32596</CPCODE>" +
    "<CTYPE>VISA</CTYPE>" +
  "</REQ>" +
"</VHTS>";
            
            HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            
            HttpResponse resp = client.execute(post);
            if (resp != null) {
                String result = EntityUtils.toString(resp.getEntity());
                System.out.println("Response: [" + result + "]");
            }
        } catch (Exception e) {
            LOGGER.error("Exception caught", e);
        }
    }
    

    public static SSLContext getClientContext()
    {
        return CLIENT_CONTEXT;
    }
}
