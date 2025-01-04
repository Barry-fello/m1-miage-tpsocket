package hollande.logs;


import javax.json.Json;
import javax.json.JsonObject;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class LogClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3244;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // Messages JSON en dur
            JsonObject log1 = Json.createObjectBuilder()
                    .add("host", "127.0.0.1")
                    .add("port", 8080)
                    .add("proto", "HTTP")
                    .add("type", "GET")
                    .add("login", "user123")
                    .add("result", "success")
                    .add("date", new java.util.Date().toString())
                    .build();

            // Envoi des messages
            writer.println(log1.toString());

            System.out.println("Messages envoyés au serveur.");
        } catch (Exception e) {
            System.err.println("Erreur dans le client : " + e.getMessage());
        }
    }
}
