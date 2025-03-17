package Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe représentant une fenêtre pour l'inscription d'utilisateurs.
 * Cette classe permet de valider un identifiant (email), de hacher un mot de passe
 * et de stocker les informations dans un fichier texte.
 */
public class Inscription extends JFrame {

    private static final long serialVersionUID = 1L; // ID de sérialisation pour JFrame
    private JTextField champTexteLogin; // Champ de texte pour l'adresse email
    private JPasswordField champMotDePasse; // Champ pour le mot de passe

    /**
     * Constructeur pour initialiser la fenêtre d'inscription.
     */
    public Inscription() {
        setTitle("Inscription");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        JPanel panneauContenu = new JPanel();
        setContentPane(panneauContenu);
        panneauContenu.setLayout(null);

        // Ajouter une étiquette pour l'adresse email
        JLabel lblLogin = new JLabel("Identifiant (email) :");
        lblLogin.setBounds(50, 50, 120, 25);
        panneauContenu.add(lblLogin);

        // Champ de texte pour saisir l'adresse email
        champTexteLogin = new JTextField();
        champTexteLogin.setBounds(180, 50, 200, 25);
        panneauContenu.add(champTexteLogin);
        champTexteLogin.setColumns(10);

        // Ajouter une étiquette pour le mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setBounds(50, 100, 100, 25);
        panneauContenu.add(lblMotDePasse);

        // Champ pour saisir le mot de passe
        champMotDePasse = new JPasswordField();
        champMotDePasse.setBounds(180, 100, 200, 25);
        panneauContenu.add(champMotDePasse);

        // Bouton d'inscription
        JButton btnEnregistrer = new JButton("S'inscrire");
        btnEnregistrer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = champTexteLogin.getText();
                    String motDePasse = new String(champMotDePasse.getPassword());

                    // Vérification si les champs ne sont pas vides
                    if (email.isEmpty() || motDePasse.isEmpty()) {
                        throw new CustomException("Veuillez remplir tous les champs.");
                    }

                    // Vérification de la validité de l'email
                    if (!validerEmail(email)) {
                        throw new CustomException("Veuillez entrer une adresse email valide (exemple@domaine.com).");
                    }

                    // Vérification si l'email est déjà utilisé
                    if (emailExistant(email)) {
                        throw new CustomException("Cet identifiant (email) est déjà utilisé.");
                    }

                    // Vérification de la longueur et des critères du mot de passe
                    if (motDePasse.length() < 12) {
                        throw new CustomException("Le mot de passe doit contenir au moins 12 caractères.");
                    }

                    // Hachage et enregistrement des informations
                    String motDePasseHache = hacherMotDePasse(motDePasse);
                    enregistrerUtilisateur(email, motDePasseHache);

                    // Message de succès et fermeture de la fenêtre
                    JOptionPane.showMessageDialog(null, "Inscription réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                } catch (CustomException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Une erreur inattendue s'est produite lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnEnregistrer.setBounds(230, 150, 100, 25);
        panneauContenu.add(btnEnregistrer);
    }

    /**
     * Vérifie si un email existe déjà dans le fichier "users.txt".
     *
     * @param email L'adresse email à vérifier.
     * @return true si l'email existe, false sinon.
     * @throws CustomException si une erreur de lecture se produit.
     */
    private boolean emailExistant(String email) throws CustomException {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] identifiants = ligne.split(", ");
                String emailFichier = identifiants[0].split(": ")[1];
                if (email.equals(emailFichier)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new CustomException("Erreur lors de la vérification de l'email existant.", e);
        }
        return false;
    }

    /**
     * Enregistre un nouvel utilisateur dans le fichier "users.txt".
     *
     * @param email        L'adresse email de l'utilisateur.
     * @param motDePasse   Le mot de passe haché de l'utilisateur.
     * @throws CustomException si une erreur d'écriture se produit.
     */
    private void enregistrerUtilisateur(String email, String motDePasse) throws CustomException {
        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write("Login: " + email + ", Mot de passe: " + motDePasse + "\n");
        } catch (IOException e) {
            throw new CustomException("Erreur lors de l'enregistrement des informations.", e);
        }
    }

    /**
     * Hache un mot de passe à l'aide de l'algorithme SHA-256.
     *
     * @param motDePasse Le mot de passe en clair.
     * @return Le mot de passe haché sous forme de chaîne hexadécimale.
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

    /**
     * Valide le format d'une adresse email.
     *
     * @param email L'adresse email à valider.
     * @return true si le format est valide, false sinon.
     */
    public boolean validerEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
