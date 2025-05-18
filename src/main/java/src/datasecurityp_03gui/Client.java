package src.ds_faza3_gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.scene.control.*;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class Client {

    @FXML private TextField keystorePathField;
    @FXML private PasswordField keystorePasswordField;
    @FXML private TextField truststorePathField;
    @FXML private PasswordField truststorePasswordField;
    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private TextArea messageArea;
    @FXML private TextField inputField;
    @FXML private Button sendButton;
    @FXML private Button closeButton;
    @FXML private Button disconnectButton;

    private SSLSocket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread listenerThread;

    @FXML
    private void onBrowseKeystore() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Client Keystore (JKS)");
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            keystorePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onBrowseTruststore() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Client Truststore (JKS)");
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            truststorePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onConnect() {
        String keystorePath = keystorePathField.getText().trim();
        String keystorePassword = keystorePasswordField.getText();
        String truststorePath = truststorePathField.getText().trim();
        String truststorePassword = truststorePasswordField.getText();
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            appendMessage("Invalid port number.");
            return;
        }

        if (keystorePath.isEmpty() || truststorePath.isEmpty() || keystorePassword.isEmpty() || truststorePassword.isEmpty()) {
            appendMessage("Please select keystore, truststore and enter passwords.");
            return;
        }

        appendMessage("Connecting to server...");

        new Thread(() -> {
            try {
                KeyStore keyStore = KeyStore.getInstance("JKS");
                try (FileInputStream ksStream = new FileInputStream(keystorePath)) {
                    keyStore.load(ksStream, keystorePassword.toCharArray());
                }

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(keyStore, keystorePassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance("JKS");
                try (FileInputStream tsStream = new FileInputStream(truststorePath)) {
                    trustStore.load(tsStream, truststorePassword.toCharArray());
                }

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(trustStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                SSLSocketFactory factory = sslContext.getSocketFactory();
                socket = (SSLSocket) factory.createSocket(host, port);

                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                Platform.runLater(() -> {
                    appendMessage("Connected to server.");
                    toggleControls(true);
                });

                listenerThread = new Thread(this::listenForMessages);
                listenerThread.setDaemon(true);
                listenerThread.start();

            } catch (Exception e) {
                Platform.runLater(() -> appendMessage("Failed to connect: " + e.getMessage()));
            }
        }).start();
    }
