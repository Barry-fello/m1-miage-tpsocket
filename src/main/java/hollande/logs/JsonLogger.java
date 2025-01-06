package hollande.logs;

import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Classe Singleton qui permet de logger des requêtes vers un serveur de log sur le port 3244 de la machine locale.
 *
 * @author torguet
 */
public class JsonLogger {

    // Attribut pour gérer la connexion au serveur de logs
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3244;

    /**
     * Constructeur privé pour le singleton
     */
    private JsonLogger() {
        // Aucune initialisation spécifique pour le moment
    }

    /**
     * Transforme une requête en Json
     *
     * @param host machine client
     * @param port port sur la machine client
     * @param proto protocole de transport utilisé
     * @param type type de la requête
     * @param login login utilisé
     * @param result résultat de l'opération
     * @return un objet Json correspondant à la requête
     */
    private JsonObject reqToJson(String host, int port, String proto, String type, String login, String result) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("host", host)
                .add("port", port)
                .add("proto", proto)
                .add("type", type)
                .add("login", login)
                .add("result", result)
                .add("date", new Date().toString());
        return builder.build();
    }

    /**
     *  singleton
     */
    private static JsonLogger logger = null;

    /**
     * récupération du logger qui est créé si nécessaire
     *
     * @return le logger
     */
    private static JsonLogger getLogger() {
        if (logger == null) {
            logger = new JsonLogger();
        }
        return logger;
    }

    /**
     * méthode pour logger
     *
     * @param host machine client
     * @param port port sur la machine client
     * @param proto protocole de transport utilisé
     * @param type type de la requête
     * @param login login utilisé
     * @param result résultat de l'opération
     */
    public static void log(String host, int port, String proto, String type, String login, String result) {
        JsonLogger logger = getLogger();

        // Conversion de la requête en objet JSON
        JsonObject logJson = logger.reqToJson(host, port, proto, type, login, result);

        // Vérification préalable de la connexion
        if (!estConnecterLog()) {
            System.out.println("Serveur de logs non disponible. journalisation indisponible.");
            return;
        }
        // Envoie des logs au serveur de log
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("Connexion au serveur de log sur le port " + SERVER_PORT);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(logJson.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du log : " + e.getMessage());
        }
    }
    // Méthode qui permet de verifier si le serverlog est connecté
    private static boolean estConnecterLog() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
