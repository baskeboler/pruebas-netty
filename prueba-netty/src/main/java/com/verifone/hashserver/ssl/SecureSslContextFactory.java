package com.verifone.hashserver.ssl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SecureSslContextFactory
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureSslContextFactory.class);

    private static final String PROTOCOL = "TLS";

    private static final SSLContext SERVER_CONTEXT;

    //private static final SSLContext CLIENT_CONTEXT;

    static {

        SSLContext serverContext = null;
        //SSLContext clientContext = null;

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
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            
            LOGGER.info("Server-side SSLContext initialized");

        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        /*
        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                .getTrustManagers(), null);
            LOGGER.info("Client-side SSLContext initialized");
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }*/

        SERVER_CONTEXT = serverContext;
        //CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getServerContext()
    {
        return SERVER_CONTEXT;
    }

    /*
    public static SSLContext getClientContext()
    {
        return CLIENT_CONTEXT;
    }
*/
    private SecureSslContextFactory()
    {
        // Unused
    }
}
