package hollande.tcp;

import hollande.metier.ClientSession;
import hollande.metier.ListeAuth;

import java.net.ServerSocket;
import java.net.Socket;


public class ServeurASTCP {

    private static void startServer(int port,String role) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port : " + port);

            //
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connexion acceptée sur le port " + port + " (Rôle : " + role + ")");

                // Gestion de chaque client dans un thread séparé
                Thread clientThread = new Thread(new ClientSession(clientSocket, new ListeAuth(),role));
                clientThread.start();
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite sur le port " + port + " : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int portC = 28414;
        int portM = 28415;

        new Thread(() -> startServer(portC, "CHECKER")).start();
        new Thread(() -> startServer(portM, "MANAGER")).start();

    }
}


