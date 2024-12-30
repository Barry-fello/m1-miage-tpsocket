package hollande.metier;

public class Analyseur {
    private final ListeAuth listeAuth;

    public Analyseur(ListeAuth listeAuth) {
        this.listeAuth = listeAuth;
    }

    public String toParser(String message,boolean isManager) {
        String KO = "BAD";
        String OK = "GOOD";
        String DONE = "DONE";
        String ERROR = "ERROR";
        if (message == null || message.trim().isEmpty()) {
            return KO;
        }
        String[] messageAuth = message.split(" ");
        if (messageAuth.length == 0) {
            return KO;
        }
        switch (messageAuth[0].toUpperCase()) {
            case "CHK":
                if (messageAuth.length < 3) {
                    return KO;
                }
                return listeAuth.tester(messageAuth[1], messageAuth[2]) ? OK : KO;
            case "ADD":
                if(isManager){
                    return listeAuth.creer(messageAuth[1], messageAuth[2]) ? DONE : ERROR;
                }
            case "DEL":
                if(isManager){
                    return listeAuth.supprimer(messageAuth[1], messageAuth[2]) ? DONE : ERROR;
                }
            case "MOD":
                if(isManager){
                    return listeAuth.mettreAJour(messageAuth[1],messageAuth[2]) ? DONE: ERROR;
                }
            default:
                return KO;
        }
    }
}

