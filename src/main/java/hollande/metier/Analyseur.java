package hollande.metier;

/**
 * Classe Analyseur pour analyser et traiter les demandes des utilisateurs.
 * Utilise la classe ListeAuth a cet effe.
 */
public class Analyseur {
    private final ListeAuth listeAuth;

    /**
     * Constructeur prenant une instance de ListeAuth.
     * @param listeAuth instance de ListeAuth utilisée pour les opérations.
     */
    public Analyseur(ListeAuth listeAuth) {
        this.listeAuth = listeAuth;
    }

    /**
     * Méthode principale pour analyser un message et exécuter la demande.
     * @param message le message à analyser, contenant une demande.
     * @param isManager  Si le client est un manager (avec des droits supplémentaires).
     * @return Le résultat de l'opération : "GOOD", "BAD", "DONE" ou "ERROR".
     */
    public String toParser(String message, boolean isManager) {
        String KO = "BAD";
        String OK = "GOOD";
        String DONE = "DONE";
        String ERROR = "ERROR";

        // Vérifie si le message est vide ou null
        if (message == null || message.trim().isEmpty()) {
            return KO;
        }

        // Sépare le message par espaces pour extraire la commande et ses arguments
        String[] messageAuth = message.split(" ");
        if (messageAuth.length == 0) {
            return KO;
        }

        // Analyse la commande (premier mot) et exécute l'opération correspondante
        switch (messageAuth[0].toUpperCase()) {
            case "CHK":  // Vérifie les informations de connexion
                if (messageAuth.length < 3) {
                    return KO;
                }
                // Appelle la méthode pour tester les identifiants
                return listeAuth.tester(messageAuth[1], messageAuth[2]) ? OK : KO;

            case "ADD":  // Ajoute un utilisateur (manager uniquement)
                if (isManager) {
                    return listeAuth.creer(messageAuth[1], messageAuth[2]) ? DONE : ERROR;
                }
                break;

            case "DEL":  // Supprime un utilisateur (manager uniquement)
                if (isManager) {
                    return listeAuth.supprimer(messageAuth[1], messageAuth[2]) ? DONE : ERROR;
                }
                break;

            case "MOD":  // Modifie les informations d'un utilisateur (manager uniquement)
                if (isManager) {
                    return listeAuth.mettreAJour(messageAuth[1], messageAuth[2]) ? DONE : ERROR;
                }
                break;

            default:  // Commande inconnue
                return KO;
        }

        // Retourne "BAD" si aucune des conditions précédentes n'a été satisfaite
        return KO;
    }
}
