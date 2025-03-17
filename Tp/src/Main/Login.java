package Main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe Login représentant l'interface de connexion.
 * Extends JFrame pour créer une interface graphique de connexion.
 */
public class Login extends JFrame {

    private static final long serialVersionUID = 1L; // Identifiant de version pour la sérialisation
    private JPanel panneauContenu; // Panneau de contenu de la fenêtre
    private JTextField champTexteLogin; // Champ de texte pour le login
    private JPasswordField champMotDePasse; // Champ de mot de passe

    /**
     * Point d'entrée de l'application.
     * 
     * @param args arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login cadre = new Login();
                    cadre.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructeur de la classe Login.
     * Initialise la fenêtre avec les composants nécessaires.
     */
    public Login() {
        // Définir le comportement de fermeture de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Définir la taille et la position de la fenêtre
        setBounds(100, 100, 450, 300);

        // Définir le titre de la fenêtre
        setTitle("Connexion");

        // Créer le panneau de contenu et définir ses bordures
        panneauContenu = new JPanel();
        panneauContenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panneauContenu);
        panneauContenu.setLayout(null);

        // Créer et ajouter le label pour le login
        JLabel lblLogin = new JLabel("Identifiant (email) :");
        lblLogin.setBounds(50, 50, 120, 25);
        panneauContenu.add(lblLogin);

        // Créer et ajouter le champ de texte pour le login
        champTexteLogin = new JTextField();
        champTexteLogin.setBounds(180, 50, 200, 25);
        panneauContenu.add(champTexteLogin);
        champTexteLogin.setColumns(10);

        // Créer et ajouter le label pour le mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setBounds(50, 100, 100, 25);
        panneauContenu.add(lblMotDePasse);

        // Créer et ajouter le champ de mot de passe
        champMotDePasse = new JPasswordField();
        champMotDePasse.setBounds(180, 100, 200, 25);
        panneauContenu.add(champMotDePasse);

        // Créer et ajouter le bouton de connexion
        JButton btnConnexion = new JButton("Connexion");
        btnConnexion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Action à effectuer lors du clic sur le bouton
                    String login = champTexteLogin.getText().trim();
                    String motDePasse = new String(champMotDePasse.getPassword()).trim();

                    // Vérification si les champs sont vides
                    if (login.isEmpty()) {
                        throw new CustomException("Le champ de l'identifiant ne peut pas être vide.");
                    }
                    if (motDePasse.isEmpty()) {
                        throw new CustomException("Le champ du mot de passe ne peut pas être vide.");
                    }

                    // Vérifier si le format de l'email est correct
                    if (!validerEmail(login)) {
                        throw new CustomException("Veuillez entrer une adresse email valide (exemple@domaine.com).");
                    }

                    // Validation des informations de connexion à partir du fichier users.txt
                    String motDePasseHache = hacherMotDePasse(motDePasse);
                    if (validerLogin(login, motDePasseHache)) {
                        JOptionPane.showMessageDialog(null, "Accès autorisé !", "Authentification", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        throw new CustomException("Identifiant ou mot de passe incorrect.");
                    }
                } catch (CustomException ex) {
                    // Affiche un message d'erreur avec le message approprié
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    // Affiche un message d'erreur générique en cas de problème inattendu
                    JOptionPane.showMessageDialog(null, "Une erreur inattendue s'est produite. Veuillez réessayer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        btnConnexion.setBounds(210, 150, 100, 25);
        panneauContenu.add(btnConnexion);

        // Ajout du bouton d'inscription
        JButton btnInscription = new JButton("S'inscrire");
        btnInscription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Ouvrir la fenêtre d'inscription
                Inscription inscriptionFrame = new Inscription();
                inscriptionFrame.setVisible(true);
            }
        });
        btnInscription.setBounds(210, 190, 100, 25); // Positionner le bouton en dessous du bouton de connexion
        panneauContenu.add(btnInscription);
    }

    /**
     * Valide le login et le mot de passe haché à partir du fichier users.txt.
     * 
     * @param login     Le login entré par l'utilisateur.
     * @param motDePasseHache Le mot de passe haché entré par l'utilisateur.
     * @return true si les informations sont correctes, false sinon.
     * @throws CustomException si une erreur se produit lors de la validation.
     */
    private boolean validerLogin(String login, String motDePasseHache) throws CustomException {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String ligne;

            // Parcourt chaque ligne du fichier
            while ((ligne = reader.readLine()) != null) {
                // Divise la ligne en utilisant ", " comme séparateur
                String[] identifiants = ligne.split(", ");

                // Récupère le login et le mot de passe haché du fichier
                String loginFichier = identifiants[0].split(": ")[1];
                String motDePasseFichier = identifiants[1].split(": ")[1];

                // Compare les identifiants de l'utilisateur avec ceux du fichier
                if (login.equals(loginFichier) && motDePasseHache.equals(motDePasseFichier)) {
                    return true; // Si les identifiants correspondent, retourne true
                }
            }
        } catch (IOException e) {
            // Lever une CustomException en cas d'erreur de lecture du fichier
            throw new CustomException("Erreur lors de la lecture du fichier users.txt.", e);
        }

        return false; // Retourne false si les identifiants ne correspondent pas ou si une erreur se produit
    }

    /**
     * Valide le format de l'adresse email.
     * 
     * @param email L'adresse email à valider.
     * @return true si l'email est valide, false sinon.
     */
    public boolean validerEmail(String email) {
        // Définir l'expression régulière pour un email valide
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Hache un mot de passe en utilisant SHA-256.
     * @param motDePasse Le mot de passe à hacher.
     * @return Le mot de passe haché en hexadécimal.
     * @throws NoSuchAlgorithmException si l'algorithme SHA-256 n'est pas disponible.
     */
    private String hacherMotDePasse(String motDePasse) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(motDePasse.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
