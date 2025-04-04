package misstech.sn.javafxprojet.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class TauxDePresenceController implements Initializable {
    @FXML
    private PieChart PieChartTauxPresence;
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
            fileChooser.setInitialFileName("Taux de Présence_" + LocalDate.now() + ".pdf");

            Stage stage = (Stage) PieChartTauxPresence.getScene().getWindow(); // Remplacez "pieChart" par le nom de votre PieChart
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (PDDocument document = new PDDocument()) {
                    // 1. Création d'un snapshot haute résolution
                    SnapshotParameters params = new SnapshotParameters();
                    params.setTransform(Transform.scale(2, 2)); // Double la résolution
                    WritableImage image = PieChartTauxPresence.snapshot(params, null);

                    // 2. Conversion en BufferedImage
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                    // 3. Conversion en byte array (format PNG)
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    // 4. Création de la page PDF
                    PDPage page = new PDPage();
                    document.addPage(page);

                    // 5. Ajout du contenu au PDF
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        // Insertion de l'image
                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "chart");

                        // Dimensions (ajustez selon vos besoins)
                        float scale = 0.6f; // Réduction à 60%
                        float width = pdImage.getWidth() * scale;
                        float height = pdImage.getHeight() * scale;
                        float x = 50; // Position X
                        float y = 150; // Position Y (plus bas pour laisser de la place au titre)

                        contentStream.drawImage(pdImage, x, y, width, height);

                        // Titre principal
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y + height + 20);
                        contentStream.showText("Répartition des émargements par professeur");
                        contentStream.endText();

                        // Sous-titre (date)
                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y + height + 5);
                        contentStream.showText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        contentStream.endText();

                        // Pied de page
                        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 30);
                        contentStream.showText("Généré le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                        contentStream.endText();
                    }

                    // 6. Sauvegarde du PDF
                    document.save(file);

                    showAlert("Succès", "Le graphique a été exporté en PDF avec succès", Alert.AlertType.INFORMATION);
                } catch (IOException e) {
                    showAlert("Erreur", "Erreur lors de l'export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
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
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (CoursPresenceStats stat : statsData) {
            String label = stat.getCoursNom() + " - " + String.format("%.1f%%", stat.getTauxPresence());
            pieChartData.add(new PieChart.Data(label, stat.getTauxPresence()));
        }
        PieChartTauxPresence.setData(pieChartData);

        // Ajouter des tooltips pour améliorer l'expérience utilisateur
        for (PieChart.Data data : PieChartTauxPresence.getData()) {
            Tooltip tooltip = new Tooltip(
                    data.getName() + "\n" +
                            "Taux de présence : " + String.format("%.1f%%", data.getPieValue()));
            Tooltip.install(data.getNode(), tooltip);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emargementService = new EmargementReportService();
        PieChartTauxPresence.setTitle("Taux de présence par cours");
        PieChartTauxPresence.setLabelsVisible(true);
        PieChartTauxPresence.setStyle("-fx-pie-label-visible: true; -fx-start-angle: 90;");
        chargerDonneesGraphique();
    }
}
