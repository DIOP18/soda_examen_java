package misstech.sn.javafxprojet.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.Users;
import misstech.sn.javafxprojet.services.EmailService;
import misstech.sn.javafxprojet.services.GmailService;

import java.time.LocalDate;
import java.util.Objects;

public class NotificationController {
    @FXML
    private DatePicker dtDate;

    @FXML
    private TextArea tfMessage;

    @FXML
    private TextField tfProf;
    private String emailProfesseur;
    public void setNotificationData(Users professeur, String message, LocalDate dateDuCours) {
        if (professeur != null) {
            this.emailProfesseur = professeur.getEmail();
            tfProf.setText(professeur.getNom() + " " + professeur.getPrenom());
        }
        tfMessage.setText(message);
        dtDate.setValue(dateDuCours); // Stocke la date du cours (pas la date actuelle)
    }

        @FXML
    void OnReturn(ActionEvent event) {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/cours.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onSend(ActionEvent event) {
        if (emailProfesseur == null || emailProfesseur.isEmpty()) {
            showAlert("Erreur", "Aucun destinataire défini", Alert.AlertType.ERROR);
            return;
        }
        String message = tfMessage.getText();
        if (message.isEmpty()) {
            showAlert("Erreur", "Le message ne peut pas être vide", Alert.AlertType.ERROR);
            return;
        }
        String nomProf = tfProf.getText();
        String dateCours = dtDate.getValue().toString();
        String contenu = "Cher(e) " + nomProf + ",\n\n" +
                message + "\n\n" +
                "Date du cours : " + dateCours + "\n\n" +
                "<b>Cordialement LE GESTIONNAIRE.</b>";
        try {
            // Utilisation de l'API Gmail au lieu de SMTP
            EmailService.envoyerEmail(emailProfesseur, "MISSTECH'S SCHOOL Notification de Cours", contenu);

            showAlert("Succès", "Le professeur a été notifié avec succès par email", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "L'envoi de l'email a échoué: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        OnReturn(event);
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
