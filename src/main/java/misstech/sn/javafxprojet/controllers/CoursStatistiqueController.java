package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.CoursPresenceStats;
import misstech.sn.javafxprojet.services.EmargementReportService;


import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CoursStatistiqueController implements Initializable {
    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Button exportExcelBtn;

    @FXML
    private Button exportPdfBtn;


    private EmargementReportService reportService;
    private void exportToExcel() {
        try {
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();

            if (dateDebut == null || dateFin == null) {
                showAlert("Erreur", "Veuillez sélectionner une période");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
            File file = fileChooser.showSaveDialog(exportExcelBtn.getScene().getWindow());

            if (file != null) {
                reportService.exportEmargementToExcel(dateDebut, dateFin, file.getAbsolutePath());
                showAlert("Succès", "Rapport Excel généré avec succès");
            }
        } catch (Exception ex) {
            showAlert("Erreur", "Erreur lors de l'exportation: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void exportToPdf() {
        try {
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();

            if (dateDebut == null || dateFin == null) {
                showAlert("Erreur", "Veuillez sélectionner une période");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(exportPdfBtn.getScene().getWindow());

            if (file != null) {
                reportService.exportEmargementToPDF(dateDebut, dateFin, file.getAbsolutePath());
                showAlert("Succès", "Rapport PDF généré avec succès");
            }
        } catch (Exception ex) {
            showAlert("Erreur", "Erreur lors de l'exportation: " + ex.getMessage());
            ex.printStackTrace();
        }
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        reportService = new EmargementReportService();
        exportExcelBtn.setOnAction(e -> exportToExcel());
        exportPdfBtn.setOnAction(e -> exportToPdf());

    }
}
