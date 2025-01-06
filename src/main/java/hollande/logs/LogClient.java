package hollande.logs;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client de test pour envoyer des messages de journalisation au serveur de logs.
 * Ce client est utilisé pour tester la communication avec le serveur de journalisation
 * et vérifier que les messages sont correctement envoyés au serveur.
 */
public class LogClient {

    // Adresse et port du serveur de journalisation
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3244;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket
                     .getOutputStream()), true)) {

            // Création de messages JSON en dur pour les tester
            JsonObject log1 = Json.createObjectBuilder()
                    .add("host", "localhost")
                    .add("port", 3244)
                    .add("proto", "HTTP")
                    .add("type", "GET")
                    .add("login", "fello")
                    .add("result", "success")
                    .add("date", new java.util.Date().toString())
                    .build();

            // Envoi des messages JSON au serveur de logs
            writer.println(log1.toString());

            System.out.println("Messages envoyés au serveur.");
        } catch (Exception e) {
            // En cas d'erreur de connexion ou autre, afficher l'exception
            System.err.println("Erreur dans le client : " + e.getMessage());
        }
    }
}
