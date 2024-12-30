package hollande.metier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientSession implements Runnable {
    private final Socket clientSocket;
    private final ListeAuth listeAuth;
    private final String role;

    public ClientSession(Socket socket, ListeAuth listeAuth, String role) {
        this.clientSocket = socket;
        this.listeAuth = listeAuth;
        this.role = role;
    }

    @Override
    public void run() {
        try (
                BufferedReader entreeSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream sortieSocket = new PrintStream(clientSocket.getOutputStream())
        ) {
            Analyseur analyseur = new Analyseur(listeAuth);
            String chaine;
            //boolean isManager = false;

            // Lecture des messages du client
            while ((chaine = entreeSocket.readLine()) != null) {
                boolean isManager = "MANAGER".equalsIgnoreCase(role);
                // System.out.println("Reçu du client : " + chaine);

                // Analyse de la chaîne et envoi de la réponse
                //String response = analyseur.toParser(chaine, isManager);
                sortieSocket.println(analyseur.toParser(chaine,isManager));
            }

        } catch (Exception e) {
            System.err.println("Erreur avec le client " + clientSocket.getInetAddress() + " : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connexion fermée pour le client : " + clientSocket.getInetAddress());
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}