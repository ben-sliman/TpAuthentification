package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.Inscription;

class TestInscription {

    @Test
    void testValiderEmail() {
        Inscription inscription = new Inscription();

        // Cas de test pour les emails valides
        assertTrue(inscription.validerEmail("exemple@domaine.fr"));


        // Cas de test pour les emails invalides
        assertFalse(inscription.validerEmail("utilisateur@domaine"));
        assertFalse(inscription.validerEmail("exempledomaine.com"));

    }


}
