package hollande.udp;
import hollande.metier.StartServer;

/*
Serveur pour les versions intermediaires
**/

public class ServeurASUDP {

    // Méthode principale pour démarrer le serveur
    public static void main(String[] args) throws Exception {
        // Définition des ports pour les rôles "CHECKER" et "MANAGER"
        int portC = 28414;
        int portM = 28415;

        // Création d'une instance du serveur StartServer
        StartServer server = new StartServer();

        // Lancer un thread pour chaque rôle (CHECKER et MANAGER)
        // Le thread pour "CHECKER" démarre le serveur UDP sur le portC
        new Thread(() -> server.startUdpServer(portC, "CHECKER")).start();

        // Le thread pour "MANAGER" démarre le serveur UDP sur le portM
        new Thread(() -> server.startUdpServer(portM, "MANAGER")).start();

        // Affichage dans la console pour indiquer que les serveurs sont en écoute
        System.out.println("Serveur en écoute : Manager sur le port 28414, Checker sur le port 28415");
    }
}
