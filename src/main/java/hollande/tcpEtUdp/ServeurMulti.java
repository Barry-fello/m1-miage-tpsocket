package hollande.tcpEtUdp;

import hollande.metier.Analyseur;
import hollande.metier.ClientSession;
import hollande.metier.ListeAuth;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMulti {
    // Méthode pour démarrer le serveur TCP
    private static void startTcpServer(int port, String role) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur TCP démarré sur le port : " + port + " (Rôle : " + role + ")");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connexion TCP acceptée sur le port " + port + " (Rôle : " + role + ")");

                // Gestion de chaque client TCP dans un thread séparé
                Thread clientThread = new Thread(new ClientSession(clientSocket, new ListeAuth(), role));
                clientThread.start();
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite sur le port TCP " + port + " : " + e.getMessage());
        }
    }

    // Méthode pour démarrer le serveur UDP
    private static void startUdpServer(int port, String role) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Serveur UDP démarré sur le port : " + port + " (Rôle : " + role + ")");

            final byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet); // Réception d'un message UDP
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message UDP reçu sur le port " + port + " (Rôle : " + role + ") : " + message);

                // Traiter la requête dans un thread séparé
                Thread udpThread = new Thread(() -> {
                    try {
                        ListeAuth listeAuth = new ListeAuth();
                        Analyseur analyseur = new Analyseur(listeAuth);
                        String response = analyseur.toParser(message, "MANAGER".equalsIgnoreCase(role));

                        byte[] responseBytes = response.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(
                                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort()
                        );

                        socket.send(responsePacket); // Envoyer la réponse
                    } catch (Exception e) {
                        System.err.println("Erreur lors du traitement UDP : " + e.getMessage());
                    }
                });
                udpThread.start();
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite sur le port UDP " + port + " : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Ports pour TCP et UDP
        int tcpPortChecker = 28414;
        int tcpPortManager = 28415;
        int udpPortChecker = 28414;
        int udpPortManager = 28415;

        // Démarrage des serveurs TCP et UDP pour chaque rôle
        new Thread(() -> startTcpServer(tcpPortChecker, "CHECKER")).start();
        new Thread(() -> startTcpServer(tcpPortManager, "MANAGER")).start();
        new Thread(() -> startUdpServer(udpPortChecker, "CHECKER")).start();
        new Thread(() -> startUdpServer(udpPortManager, "MANAGER")).start();
    }

}
