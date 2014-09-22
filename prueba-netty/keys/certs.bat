rem Create the server and application client key stores and certificates
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -dname "CN=Server,OU=Development,O=Verifone,L=Montevideo,S=Montevideo,C=UY" -keypass password -storepass password -keystore server.jks -ext SAN=DNS:localhost,IP:127.0.0.1
keytool -genkeypair -alias clientkey -keyalg RSA -keysize 2048 -dname "CN=Client,OU=Development,O=Verifone,L=Montevideo,S=Montevideo,C=UY" -keypass password -storepass password -keystore client.jks -ext SAN=DNS:localhost,IP:127.0.0.1
 
rem Copy the client's public certificate to the server's keystore
keytool -exportcert -keystore client.jks -storepass password -file client-public.cer -alias clientkey
keytool -importcert -keystore server.jks -storepass password -file client-public.cer -alias clientcert -noprompt
 
rem Take a peek at the server's keystore to make sure that the client's certificate is there
keytool -v -list -keystore server.jks -storepass password
 
rem Copy the server's public certificate to the client's keystore
keytool -exportcert -keystore server.jks -storepass password -file server-public.cer -alias serverkey
keytool -importcert -keystore client.jks -storepass password -file server-public.cer -alias servercert -noprompt
 
rem Take a peek at the client's keystore to make sure that the client's certificate is there
keytool -v -list -keystore client.jks -storepass password
 
rem Create a browser keystore most browsers can read easily
keytool -importkeystore -srckeystore client.jks -srcstorepass password -srcalias clientkey -destkeystore client.p12 -deststoretype PKCS12 -deststorepass password -destalias clientkey -noprompt
 
rem Take a peek at the browser's keystore to make sure that the client's certificate is there
keytool -v -list -keystore client.p12 -storetype pkcs12 -storepass password