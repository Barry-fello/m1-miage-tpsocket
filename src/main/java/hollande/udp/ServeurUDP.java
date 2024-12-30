package hollande.udp;

import hollande.metier.Analyseur;
import hollande.metier.ListeAuth;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServeurUDP {
    public static void main(String[] args) throws Exception {
        // Création de deux sockets UDP pour différents ports
        DatagramSocket socketManager = new DatagramSocket(28415);
        DatagramSocket socketChecker = new DatagramSocket(28414);

        // Gestionnaire d'authentification et analyseur
        ListeAuth listeAuth = new ListeAuth();
        Analyseur analyseur = new Analyseur(listeAuth);

        // Lancer des threads pour chaque rôle
        new Thread(() -> handleSocket(socketManager, true, analyseur)).start();
        new Thread(() -> handleSocket(socketChecker, false, analyseur)).start();

        System.out.println("Serveur en écoute : Manager sur le port 28414, Checker sur le port 28415");
    }

    private static void handleSocket(DatagramSocket socket, boolean isManager, Analyseur analyseur) {
        final byte[] tampon = new byte[1024];
        DatagramPacket dgram = new DatagramPacket(tampon, tampon.length);

        try {
            while (true) {
                // Réception d'un datagramme
                socket.receive(dgram);

                // Extraction et traitement du message
                String chaine = new String(dgram.getData(), 0, dgram.getLength());
                System.out.println("Message reçu sur le port " + socket.getLocalPort() + ": " + chaine);

                // Utilisation de l'analyseur pour traiter le message
                String response = analyseur.toParser(chaine, isManager);

                // Préparation de la réponse
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length, dgram.getAddress(), dgram.getPort()
                );

                // Envoi de la réponse
                socket.send(responsePacket);

                // Réinitialisation de la longueur du tampon
                dgram.setLength(tampon.length);
            }
        } catch (Exception e) {
            System.err.println("Erreur sur le port " + socket.getLocalPort() + ": " + e.getMessage());
        }
    }
}
