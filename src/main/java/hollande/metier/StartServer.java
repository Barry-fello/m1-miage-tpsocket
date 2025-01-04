package hollande.metier;

import hollande.logs.JsonLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
// Metier de point de vue service
public class StartServer {
    // Méthode pour démarrer le serveur TCP
    public  void startTcpServer(int port, String role) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur TCP démarré sur le port : " + port + "(Rôle : " + role + ")");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientHost = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();

                System.out.println("Connexion TCP acceptée sur le port " + port + "(Rôle : " + role + ")");
                // Journalisation de l'événement
                JsonLogger.log(clientHost, clientPort, "TCP", "CONNECT", "N/A",
                        "accepted");
                // Gestion de chaque client TCP dans un thread séparé
                Thread clientThread = new Thread(new ClientSession(clientSocket, new ListeAuth(), role));
                clientThread.start();
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite sur le port TCP " + port + " : " + e
                    .getMessage());

            // Journalisation de l'erreur
            JsonLogger.log("localhost", port, "TCP", "ERROR", "N/A", e
                    .getMessage());
        }
    }
    // Méthode pour gérer les sockets UDP avec port et rôle
    public void startUdpServer(int port, String role) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            Analyseur analyseur = new Analyseur(new ListeAuth());
            final byte[] tampon = new byte[1024];
            DatagramPacket dgram = new DatagramPacket(tampon, tampon.length);

            System.out.println("Gestion des sockets démarrée sur le port : " + port + " (Rôle : " + role + ")");

            while (true) {
                // Réception d'un datagramme
                socket.receive(dgram);
                String clientHost = dgram.getAddress().getHostAddress();
                int clientPort = dgram.getPort();
                String chaine = new String(dgram.getData(), 0, dgram.getLength());

                System.out.println("Message reçu sur le port " + port + ": " + chaine);
                JsonLogger.log(clientHost, clientPort, "UDP", "RECEIVE", "N/A", chaine);

                // Utilisation de l'analyseur pour traiter le message
                String response = analyseur.toParser(chaine, "MANAGER".equalsIgnoreCase(role));

                // Préparation de la réponse
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length, dgram.getAddress(), dgram.getPort()
                );

                // Envoi de la réponse
                socket.send(responsePacket);
                JsonLogger.log(clientHost, clientPort, "UDP", "SEND", "N/A", response);

                // Réinitialisation de la longueur du tampon
                dgram.setLength(tampon.length);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            System.err.println("Erreur sur le port " + port + ": " + errorMessage);
            JsonLogger.log("localhost", port, "UDP", "ERROR", "N/A", errorMessage);
        }
    }



}
