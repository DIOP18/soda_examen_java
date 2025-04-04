package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.Users;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class pageConnectionController implements Initializable {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    private EntityManager entityManager;




    private void openEmargementPage(ActionEvent event, Users connectedUser) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/emargement.fxml"));
            Parent dashboardView = loader.load();
            EmargementController emargementController = loader.getController();
            emargementController.setConnectedUser(connectedUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("ESPACE PROFESSEUR");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGestionCoursPage(ActionEvent event) throws IOException {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/cours.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("ESPACE GESTIONNAIRE");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGestionUtilisateursPage(ActionEvent event) throws IOException {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/dashboardAdmin.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("ESPACE ADMINISTRATEUR");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Veuillez remplir tous les champs");
            return;
        }

        try {
            // Rechercher l'utilisateur dans la base de données
            TypedQuery<Users> query = entityManager.createQuery(
                    "SELECT u FROM Users u WHERE u.email = :email AND u.password = :password",
                    Users.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            Users user = query.getSingleResult();

            if (user != null) {
                switch (user.getRole()) {
                    case PROFESSEUR:
                        // Vérifier si le professeur a des cours attribués
                        if (hasAssignedCourses(user.getId())) {
                            openEmargementPage(event, user);
                        } else {
                            showAlert(Alert.AlertType.ERROR,
                                    "Accès refusé",
                                    "Aucun cours n'est attribué à ce professeur. Contactez le gestionnaire.");

                        }
                        break;
                    case GESTIONNAIRE:
                        openGestionCoursPage(event);
                        break;
                    case ADMINISTRATEUR:
                        openGestionUtilisateursPage(event);
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Rôle non reconnu");
                }
            }
        } catch (NoResultException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Email ou mot de passe incorrect");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur système", "Une erreur s'est produite: " + e.getMessage());
        }
    }

    private boolean hasAssignedCourses(int professorId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Cours c WHERE c.professeur.id = :profId",
                Long.class);
        query.setParameter("profId", professorId);
        return query.getSingleResult() > 0;
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        this.entityManager = emf.createEntityManager();
    }
}
