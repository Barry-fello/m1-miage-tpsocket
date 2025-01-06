package hollande.tcp;
import hollande.metier.StartServer;
/*
Serveur pour les versions intermediaires
**/
public class ServeurASTCP {

    public static void main(String[] args) {
        StartServer serveur = new StartServer();
        int portC = 28414;
        int portM = 28415;

        new Thread(() -> serveur.startTcpServer(portC, "CHECKER")).start();
        new Thread(() -> serveur.startTcpServer(portM, "MANAGER")).start();

    }
}


