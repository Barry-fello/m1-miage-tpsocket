package hollande.logs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class LogServer {

    // Port d'écoute du serveur
    private static final int PORT = 3244;

    public static void main(String[] args) {
        // Démarrer le serveur de journalisation
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de journalisation démarré sur le port " + PORT);

            // Boucle infinie pour accepter les connexions des clients
            while (true) {
                // Accepter une connexion entrante depuis un client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connexion acceptée depuis " + clientSocket.getInetAddress());

                // Gérer la connexion dans un thread séparé pour permettre à plusieurs clients de se connecter simultanément
                new Thread(() -> gestclient(clientSocket)).start();
            }
        } catch (Exception e) {
            // En cas d'erreur dans le serveur, afficher un message d'erreur
            System.err.println("Erreur dans le serveur : " + e.getMessage());
        }
    }

    /**
     * Gère la communication avec un client spécifique.
     * Les messages reçus du client sont affichés directement dans la console.
     *
     * @param clientSocket Socket représentant la connexion avec le client
     */
    private static void gestclient(Socket clientSocket) {
        // Essayer de lire les messages du client et les afficher dans la console
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))) {

            String message;

            // Lire chaque ligne envoyée par le client et afficher dans la console
            while ((message = reader.readLine()) != null) {
                // Afficher le message dans la console
                System.out.println("Message reçu de " +
                        clientSocket.getInetAddress() + ": " + message);
            }

        } catch (Exception e) {
            // En cas d'erreur lors de la gestion du client, afficher un message d'erreur
            System.err.println("Erreur avec le client : " + e.getMessage());
        }
    }
}
