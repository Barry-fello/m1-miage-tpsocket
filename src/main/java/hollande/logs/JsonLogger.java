package hollande.logs;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class JsonLogger {
    // Attributs
    private static JsonLogger logger = null;
    private static final String LOG_FILE = "logs.json";
    /**
     * Constructeur privé pour le singleton
     */
    private JsonLogger() {
    }
    /**
     * Transforme une requête en JSON
     *
     * @param host machine client
     * @param port port sur la machine client
     * @param proto protocole de transport utilisé
     * @param type type de la requête
     * @param login login utilisé
     * @param result résultat de l'opération
     * @return un objet JSON correspondant à la requête
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
     * Récupération du singleton logger
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
     * Méthode pour logger une requête
     * @param host machine client
     * @param port port sur la machine client
     * @param proto protocole de transport utilisé
     * @param type type de la requête
     * @param login login utilisé
     * @param result résultat de l'opération
     */
    public static void log(String host, int port, String proto, String type, String login, String result) {
        JsonLogger logger = getLogger();
        JsonObject jsonLog = logger.reqToJson(host, port, proto, type, login, result);
        logger.writeLogToFile(jsonLog);
    }
    /**
     * Écrit le log dans un fichier
     *
     * @param jsonLog le log au format JSON
     */
    private void writeLogToFile(JsonObject jsonLog) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(jsonLog.toString());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du log : " + e.getMessage());
        }
    }
}
