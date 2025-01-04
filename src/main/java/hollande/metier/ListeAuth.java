package hollande.metier;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Gère une Liste d'Authentification avec persistance dans une BD H2
 * @author torguet
 *
 */
public class ListeAuth {
    /**
     * HashMap pour stocker les couples login, mot de passe
     */
    private HashMap<String, String> authEntries;
    /**
     * constructeur
     */
    public ListeAuth() {
        authEntries = new HashMap<>();
        // ajoute des entrées de test
        authEntries.put("Toto", "Toto");
        authEntries.put("Titi", "Titi");
        authEntries.put("Tata", "Tata");
        authEntries.put("Tutu", "Tutu");
    }
    /**
     * création d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si ça c'est bien passé
     */
    public synchronized boolean creer(String login, String passwd) {
        if(authEntries.containsKey(login))
            return false; // le login est déjà présent
        authEntries.put(login, passwd); // on l'ajoute
        return true; // ça c'est bien passé
    }
    /**
     *  mise à jour d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si ça c'est bien passé
     */
    public synchronized boolean mettreAJour(String login, String passwd) {
        if(!authEntries.containsKey(login))
            return false; // le login n'est pas présent
        authEntries.put(login, passwd); // on remplace le mot de passe
        return true; // ça c'est bien passé
    }
    /**
     *  suppression d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si ça c'est bien passé
     */
    public synchronized boolean supprimer(String login, String passwd) {
        if(!tester(login, passwd))
            return false; // le login ou le mot de passe ne sont pas corrects
        authEntries.remove(login); // on supprime le couple
        return true; // ça c'est bien passé
    }
    /**
     *  test d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si ça c'est bien passé
     */
    public synchronized boolean tester(String login, String passwd) {
        if(!authEntries.containsKey(login))
            return false; // le login n'est pas présent
        return authEntries.get(login).equals(passwd); // le mot de passe est correct
// le mot de passe n'est pas correct
    }

}
