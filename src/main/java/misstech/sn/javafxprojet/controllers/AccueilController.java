package misstech.sn.javafxprojet.controllers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;


public class AccueilController {
    @FXML
    void OpenCours(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/cours.fxml")));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ESPACE GESTIONNAIRE");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setContentText("Impossible de charger la page de gestion des cours.");
            alert.showAndWait();
        }

    }

    @FXML
    void OpenEmargement(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/emargement.fxml")));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ESPACE PROFESSEUR");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setContentText("Impossible de charger la page de gestion des Emargements.");
            alert.showAndWait();
        }

    }





    @FXML
    void OpenUsers(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/users.fxml")));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ESPACE ADMINISTRATEUR");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setContentText("Impossible de charger la page de gestion des utilisateurs.");
            alert.showAndWait();
        }

    }

}
