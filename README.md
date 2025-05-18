# JavaFX SSL Client-Server Messaging Application

Overview

This project is a secure messaging application built with JavaFX that demonstrates mutual TLS (mTLS) authentication between a server and a client. Both server and client authenticate each other using SSL certificates, ensuring secure communication over a network.

The server and client can send messages back and forth in real-time. Both have a GUI built with JavaFX that supports:

Browsing and selecting keystore and truststore files
Entering passwords for keystores and truststores
Starting the server and connecting the client securely with SSL/TLS
Sending and receiving messages
Closing the connection gracefully via a Close button.

Thia peogram requires the Client and Server to have X509 certificaties, if you do not have them on your computer,
you need to create keystores and truststores for both client and server with certificates signed by a trusted CA (or self-signed for testing).

To do that, open terminal and enter these commands in order:

1. keytool -genkeypair -alias servercert -keyalg RSA -keysize 2048 \
  -keystore serverkeystore.jks -validity 365 -storepass serverpass

2. keytool -exportcert -alias servercert -file servercert.cer -keystore serverkeystore.jks -storepass serverpass

3. keytool -genkeypair -alias clientcert -keyalg RSA -keysize 2048 \
  -keystore clientkeystore.jks -validity 365 -storepass clientpass

4. keytool -exportcert -alias clientcert -file clientcert.cer -keystore clientkeystore.jks -storepass clientpass

5. keytool -importcert -alias servercert -file servercert.cer -keystore clienttruststore.jks -storepass clienttrustpass -noprompt

6. keytool -importcert -alias clientcert -file clientcert.cer -keystore servertruststore.jks -storepass servertrustpass -noprompt

Launch the server application (run Server).
Browse and select your keystore and truststore files.
Enter the passwords.
Click Start Server.
The server will wait for client connections.
Running the Client

Launch the client application (run Client).
Browse and select your client keystore and truststore files.
Enter the passwords.
Enter the server hostname (e.g., localhost) and port (e.g., 5001).
Click Connect to establish a secure connection.
Use the input field to send messages.
Click Close to disconnect.
Usage

Use the Send button or press Enter (if implemented) to send messages.
Received messages appear in the main text area.
Clicking Close disconnects the socket cleanly on both client and server sides.
Troubleshooting

Make sure the keystore and truststore files are correctly generated and contain the appropriate certificates.
Passwords must match those used when creating keystores.
Check firewall settings if client and server are on different machines.
Verify the server is running before connecting the client.
Notes

This project is for educational/demo purposes. For production use, ensure certificates are signed by trusted CAs.
TLS versions and algorithms can be configured inside the code if needed.
Error handling is minimal and can be improved for robustness.
