package hollande.metier;

import java.io.*;
import java.net.Socket;
import hollande.logs.JsonLogger;  // Assurez-vous d'importer la classe JsonLogger

import javax.json.JsonObject;

import static javax.json.Json.createObjectBuilder;

/**
 * Classe représentant une session client.
 * Cette classe implémente {@link Runnable} pour gérer les connexions client
 * dans un thread séparé. Elle lit les messages envoyés par le client et utilise
 * un {@link Analyseur} pour traiter les commandes et envoyer les réponses.
 */
public class ClientSession implements Runnable {
    private final Socket clientSocket;
    private final ListeAuth listeAuth;
    private final String role;

    /**
     * Constructeur pour initialiser la session du client.
     * @param socket le socket associé à la connexion client.
     * @param listeAuth Référence de liste auth
     * @param role le rôle de l'utilisateur (par exemple, "MANAGER").
     */
    public ClientSession(Socket socket, ListeAuth listeAuth, String role) {
        this.clientSocket = socket;
        this.listeAuth = listeAuth;
        this.role = role;
    }

    /**
     * Méthode exécutée par le thread pour gérer la communication avec le client.
     * Cette méthode lit les messages envoyés par le client, les analyse via un
     * {@link Analyseur}, et renvoie la réponse correspondante.
     */
    @Override
    public void run() {
        try (
                BufferedReader entreeSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream sortieSocket = new PrintStream(clientSocket.getOutputStream())
        ) {
            Analyseur analyseur = new Analyseur(listeAuth);
            String chaine;
    /* Probleme d'obtimisation car sendLog utilise JsonLogger qui pour chaque envoie
    créé une connection avec le server log meme si ce client (serverMulti) est connéct au server log. A l'instant c'est la solution qui me reste pour
    pouvoir journaliser tcp et maintenir le multiServer avec ou sans le server log
    * */
            while ((chaine = entreeSocket.readLine()) != null) {
                boolean isManager = "MANAGER".equalsIgnoreCase(role);
                JsonObject logRequest = createObjectBuilder()
                        .add("host", clientSocket.getInetAddress().getHostAddress())
                        .add("port", clientSocket.getPort())
                        .add("proto", "TCP")
                        .add("type", "REQUEST")
                        .add("role", role)
                        .add("message", chaine)
                        .add("date", new java.util.Date().toString())
                        .build();

                // Journalisation avec gestion des erreurs
                sendLog(logRequest);

                // Analyse et réponse
                String reponse = analyseur.toParser(chaine, isManager);
                sortieSocket.println(reponse);
            }
        } catch (IOException e) {
            System.err.println("Deconnexion du client ");
        }
    }
    // Méthode pour journaliser avec sendLog
    private void sendLog(JsonObject log) {
        try {
            JsonLogger.log(log.getString("host"), log.getInt("port"),
                    log.getString("proto"), log.getString("type"),
                    log.getString("role"), log.getString("message"));
        } catch (Exception e) {
            System.out.println("Aucune solution envisagée ");
        }
    }

}
