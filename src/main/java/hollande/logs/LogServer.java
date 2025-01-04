package hollande.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class LogServer {
    private static final int PORT = 3244;
    private static final String LOG_FILE = "server_logs.json";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de journalisation démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connexion acceptée depuis " + clientSocket.getInetAddress());

                // Gérer la connexion dans un thread séparé
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le serveur : " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message reçu : " + message);
                writer.write(message);
                writer.newLine();
                writer.flush();
            }

        } catch (Exception e) {
            System.err.println("Erreur avec le client : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture de la connexion client : " + e.getMessage());
            }
        }
    }
}
