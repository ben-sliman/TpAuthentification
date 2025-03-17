package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.Login;

class TestLogin {

    @Test
    void testValiderEmail() {
        Login login = new Login();

        // Cas de test pour les emails valides
        assertTrue(login.validerEmail("test@test.fr"));
        assertTrue(login.validerEmail("ben@ben.fr"));

        // Cas de test pour les emails invalides
        assertFalse(login.validerEmail("utilisateur@domaine"));
        assertFalse(login.validerEmail("utilisateurdomaine.com"));
        assertFalse(login.validerEmail("@domaine.com"));
    }
}
