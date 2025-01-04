package hollande.udp;
import hollande.metier.StartServer;



public class ServeurUDP {
    public static void main(String[] args) throws Exception {
        int portC = 28414;
        int portM = 28415;
        StartServer server = new StartServer();

        // Lancer des threads pour chaque rôle
        new Thread(() -> server.startUdpServer(portC, "CHECKER")).start();
        new Thread(() -> server.startUdpServer(portM, "MANAGER")).start();

        System.out.println("Serveur en écoute : Manager sur le port 28414, Checker sur le port 28415");
    }
}
