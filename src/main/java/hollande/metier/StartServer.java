package hollande.metier;

import hollande.logs.JsonLogger;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

// Classe principale pour démarrer les serveurs TCP et UDP.
// Elle utilise une instance partagée de ListeAuth.
public class StartServer {
    // Instance partagée de ListeAuth pour stocker les informations.
    private final ListeAuth listeAuth = new ListeAuth();

    /**
     * Méthode pour démarrer un serveur TCP.
     * @param port Le port sur lequel le serveur écoutera.
     * @param role Le rôle du serveur, par exemple "MANAGER" ou "CHECKER".
     */
        public void startTcpServer(int port, String role) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Serveur TCP en cours d'exécution sur le port " + port + " : " + role);

                while (true) {
                    // Accepter une connexion entrante
                    Socket clientSocket = serverSocket.accept();
                    String clientHost = clientSocket.getInetAddress().getHostAddress();
                    int clientPort = clientSocket.getPort();

                    System.out.println("Connexion TCP acceptée depuis " + clientHost + " : " + clientPort);

                    // Crée un nouveau thread pour gérer la session client
                    Thread clientThread = new Thread(new ClientSession(clientSocket, listeAuth, role));
                    clientThread.start();
                }
            } catch (Exception e) {
                System.err.println("Une erreur s'est produite sur le port TCP " + port + " : " +
                        e.getMessage());
            }
        }


    /**
     * Méthode pour démarrer un serveur UDP.
     * @param port Le port sur lequel le serveur écoutera.
     * @param role Le rôle du serveur, par exemple "MANAGER" ou "CHECKER".
     */
    public void startUdpServer(int port, String role) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            // Utilise l'instance partagée de ListeAuth dans un Analyseur.
            Analyseur analyseur = new Analyseur(listeAuth);
            final byte[] tampon = new byte[1024];
            DatagramPacket dgram = new DatagramPacket(tampon, tampon.length);

            System.out.println("Serveur udp run : " + port +" "+ role);

            while (true) {
                // Réception d'un datagramme de la part d'un client.
                socket.receive(dgram);
                String clientHost = dgram.getAddress().getHostAddress();
                int clientPort = dgram.getPort();
                String chaine = new String(dgram.getData(), 0, dgram.getLength());

                System.out.println("Message reçu sur le port " + port + ": " + chaine);
                // Journalisation de la réception du message.
                JsonLogger.log(clientHost, clientPort, "udp", "receive","login", chaine);

                // Traitement du message avec l'Analyseur.
                String response = analyseur.toParser(chaine, "MANAGER".equalsIgnoreCase(role));

                // Préparation et envoi de la réponse au client.
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length, dgram.getAddress(), dgram.getPort()
                );
                socket.send(responsePacket);

                // Journalisation de l'envoi de la réponse.
                JsonLogger.log(clientHost, clientPort, "udp", "send", "login", response);

                // Réinitialisation de la longueur du tampon pour le prochain message.
                dgram.setLength(tampon.length);
            }
        } catch (Exception e) {
            // Gestion et journalisation des erreurs.
            String errorMessage = e.getMessage();
            System.err.println("Erreur sur le port " + port + ": " + errorMessage);
            JsonLogger.log("localhost", port, "udp", "ERROR", "login", errorMessage);
        }
    }
}
