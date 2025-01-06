package hollande.tcpEtUdp;

import hollande.metier.StartServer;

public class ServeurMulti {
    public static void main(String[] args) {

        // Ports pour TCP et UDP
        int tcpPortChecker = 28414;
        int tcpPortManager = 28415;
        int udpPortChecker = 28414;
        int udpPortManager = 28415;
        StartServer demarrer = new StartServer();
        // Démarrage des serveurs TCP et UDP pour chaque rôle
        new Thread(() -> demarrer.startTcpServer(tcpPortChecker, "CHECKER")).start();
        new Thread(() -> demarrer.startTcpServer(tcpPortManager, "MANAGER")).start();
        new Thread(() -> demarrer.startUdpServer(udpPortChecker, "CHECKER")).start();
        new Thread(() -> demarrer.startUdpServer(udpPortManager, "MANAGER")).start();
    }
}
