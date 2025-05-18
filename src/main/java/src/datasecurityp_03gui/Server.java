package src.datasecurityp_03gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Server {

    @FXML private TextField keystorePathField;
    @FXML private TextField truststorePathField;
    @FXML private PasswordField keystorePasswordField;
    @FXML private PasswordField truststorePasswordField;
    @FXML private TextArea messageArea;
    @FXML private TextField inputField;

    private SSLSocket clientSocket;
    private SSLServerSocket serverSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    private void onBrowseKeystore(ActionEvent event) {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null) keystorePathField.setText(file.getAbsolutePath());
    }

    @FXML
    private void onBrowseTruststore(ActionEvent event) {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null) truststorePathField.setText(file.getAbsolutePath());
    }

    @FXML
    private void onStartServer(ActionEvent event) {
        executor.execute(() -> {
            try {
                String ksPath = keystorePathField.getText();
                String tsPath = truststorePathField.getText();
                String ksPassword = keystorePasswordField.getText();
                String tsPassword = truststorePasswordField.getText();

                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(new FileInputStream(ksPath), ksPassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(keyStore, ksPassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(new FileInputStream(tsPath), tsPassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(trustStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
                serverSocket = (SSLServerSocket) ssf.createServerSocket(5001);
                serverSocket.setNeedClientAuth(true);

                appendMessage("Server started. Waiting for client...");

                clientSocket = (SSLSocket) serverSocket.accept();
                appendMessage("Client connected.");

                SSLSession session = clientSocket.getSession();
                Certificate[] certs = session.getPeerCertificates();
                for (Certificate cert : certs) {
                    if (cert instanceof X509Certificate x509) {
                        appendMessage("Client certificate: " + x509.getSubjectX500Principal().getName());
                    }
                }

                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());

                listenToClient();

            } catch (Exception e) {
                appendMessage("Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void listenToClient() {}

    @FXML
    private void onSend(ActionEvent event) {}

    @FXML
    private void onClose(ActionEvent event) {}

    private void appendMessage(String msg) {}
    }