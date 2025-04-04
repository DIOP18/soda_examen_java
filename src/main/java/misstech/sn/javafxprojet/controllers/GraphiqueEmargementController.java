package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.CoursPresenceStats;
import misstech.sn.javafxprojet.services.EmargementReportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GraphiqueEmargementController implements Initializable {
    @FXML
    private BarChart<String, Number> BarcharProfesseur;
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    private EmargementReportService emargementService;
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
    void OnPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("Graphique emargements_professeurs_" + LocalDate.now() + ".pdf");

        Stage stage = (Stage) BarcharProfesseur.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PDDocument document = new PDDocument()) {

                SnapshotParameters params = new SnapshotParameters();
                WritableImage image = BarcharProfesseur.snapshot(params, null);


                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // Créer la page PDF
                PDPage page = new PDPage();
                document.addPage(page);

                // Créer l'image PDF
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "chart");

                    // Calculer les dimensions pour garder les proportions
                    float scale = 0.5f; // Réduction à 50%
                    float width = pdImage.getWidth() * scale;
                    float height = pdImage.getHeight() * scale;

                    // Positionner l'image (50 unités depuis la gauche, 100 depuis le bas)
                    contentStream.drawImage(pdImage, 50, 100, width, height);

                    // Ajouter un titre
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 80); // Position au-dessus de l'image
                    contentStream.showText("Émargements par professeur - " + LocalDate.now());
                    contentStream.endText();

                    // Ajouter un pied de page
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(50, 30);
                    contentStream.showText("Généré le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    contentStream.endText();
                }

                // Sauvegarder le PDF
                document.save(file);

                showAlert("Succès", "Graphique exporté en PDF avec succès", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Erreur", "Échec de l'export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void chargerDonneesGraphique() {
        List<CoursPresenceStats> statsData = emargementService.getPresenceStatsByProfessor();

        // Grouper les données par professeur
        Map<String, Long> emargementsByProf = statsData.stream()
                .collect(Collectors.groupingBy(
                        CoursPresenceStats::getProfesseurNomComplet,
                        Collectors.summingLong(CoursPresenceStats::getPresences)));

        // Créer la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Émargements");


        emargementsByProf.forEach((profName, count) -> {
            series.getData().add(new XYChart.Data<>(profName, count));
        });

        // Ajouter la série au graphique
        BarcharProfesseur.getData().add(series);
        for (XYChart.Series<String, Number> s : BarcharProfesseur.getData()) {
            for (XYChart.Data<String, Number> data : s.getData()) {
                Tooltip tooltip = new Tooltip(
                        data.getXValue() + " : " + data.YValueProperty().getValue() + " émargements");
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emargementService = new EmargementReportService();
        xAxis.setLabel("Professeur");
        yAxis.setLabel("Nombre d'émargements");
        BarcharProfesseur.setTitle("Nombre d'émargements par professeur");
        BarcharProfesseur.setAnimated(false);
        chargerDonneesGraphique();

    }
}
