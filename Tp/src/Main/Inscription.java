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
 
public class Inscription extends JFrame {
 
    private static final long serialVersionUID = 1L;
    private JTextField champTexteLogin;
    private JPasswordField champMotDePasse;
 
    public Inscription() {
        setTitle("Inscription");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        JPanel panneauContenu = new JPanel();
        setContentPane(panneauContenu);
        panneauContenu.setLayout(null);
 
        JLabel lblLogin = new JLabel("Identifiant (email) :");
        lblLogin.setBounds(50, 50, 120, 25);
        panneauContenu.add(lblLogin);
 
        champTexteLogin = new JTextField();
        champTexteLogin.setBounds(180, 50, 200, 25);
        panneauContenu.add(champTexteLogin);
        champTexteLogin.setColumns(10);
 
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setBounds(50, 100, 100, 25);
        panneauContenu.add(lblMotDePasse);
 
        champMotDePasse = new JPasswordField();
        champMotDePasse.setBounds(180, 100, 200, 25);
        panneauContenu.add(champMotDePasse);
 
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
 
                    // Vérification de la longueur du mot de passe
                    if (motDePasse.length() < 12) {
                        throw new CustomException("Le mot de passe doit contenir au moins 12 caractères, une majuscule, un chiffre et un caractère spécial");
                    }
 
                    String motDePasseHache = hacherMotDePasse(motDePasse);
                    enregistrerUtilisateur(email, motDePasseHache);
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
 
    private void enregistrerUtilisateur(String email, String motDePasse) throws CustomException {
        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write("Login: " + email + ", Mot de passe: " + motDePasse + "\n");
        } catch (IOException e) {
            throw new CustomException("Erreur lors de l'enregistrement des informations.", e);
        }
    }
 
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
 
    public boolean validerEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
 
 