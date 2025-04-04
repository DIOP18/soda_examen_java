package misstech.sn.javafxprojet.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.Emargement;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EvolutionDesEmargementsController implements Initializable {
    @FXML
    private LineChart<String, Number> LineChartEvolution;

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
    private void chargerDonneesGraphique() {
        try {
            // Récupérer tous les émargements
            List<Emargement> emargements = emargementService.getAllEmargements();

            // Ajouter du log pour vérifier si vous récupérez des données
            System.out.println("Nombre total d'émargements: " + emargements.size());

            // Vérifier combien sont présents
            long presentCount = emargements.stream()
                    .filter(e -> "PRESENT".equals(e.getStatut()))
                    .count();
            System.out.println("Nombre d'émargements avec statut PRESENT: " + presentCount);

            // Vérifier si le graphique est initialisé
            if (LineChartEvolution == null) {
                System.err.println("Erreur: LineChartEvolution n'est pas initialisé");
                return;
            }

            // Effacer les données existantes avant d'ajouter de nouvelles séries
            LineChartEvolution.getData().clear();

            // Grouper les émargements par date - avec filtre amélioré pour gérer les différents cas
            Map<LocalDate, Long> countByDate = emargements.stream()
                    .filter(e -> e.getStatut() != null &&
                            ("PRESENT".equals(e.getStatut()) ||
                                    "Present".equals(e.getStatut()) ||
                                    "present".equals(e.getStatut())))
                    .collect(Collectors.groupingBy(
                            Emargement::getDate,
                            Collectors.counting()));

            // Formater les dates pour affichage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Afficher les données groupées
            System.out.println("Données groupées par date:");
            countByDate.forEach((date, count) ->
                    System.out.println(date.format(formatter) + ": " + count));

            // Trier les dates pour affichage chronologique
            List<LocalDate> sortedDates = new ArrayList<>(countByDate.keySet());
            Collections.sort(sortedDates);

            // Créer la série de données
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Présences");

            // Si aucune données n'est trouvée, ajouter des données fictives pour tester
            if (countByDate.isEmpty()) {
                System.out.println("Aucune données trouvée, ajout de données fictives pour test");
                LocalDate today = LocalDate.now();
                series.getData().add(new XYChart.Data<>(today.format(formatter), 5));
                series.getData().add(new XYChart.Data<>(today.minusDays(1).format(formatter), 3));
                series.getData().add(new XYChart.Data<>(today.minusDays(2).format(formatter), 7));
            } else {
                // Ajouter les données réelles à la série
                for (LocalDate date : sortedDates) {
                    series.getData().add(new XYChart.Data<>(date.format(formatter), countByDate.get(date)));
                }
            }

            // Ajouter la série au graphique
            LineChartEvolution.getData().add(series);

            System.out.println("Série ajoutée au graphique avec " + series.getData().size() + " points de données");

            // Ajouter des tooltips pour améliorer l'expérience utilisateur
            for (XYChart.Series<String, Number> s : LineChartEvolution.getData()) {
                for (XYChart.Data<String, Number> data : s.getData()) {
                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + " : " + data.YValueProperty().getValue() + " présences");
                    Tooltip.install(data.getNode(), tooltip);

                    // Mettre en évidence les points de données
                    data.getNode().setStyle("-fx-background-color: #4682B4, white;");
                }
            }

            // Vérifier que le graphique est visible
            LineChartEvolution.setVisible(true);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données du graphique: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    void OnPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("evolution_emargements_" + LocalDate.now() + ".pdf");

        Stage stage = (Stage) LineChartEvolution.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                // Créer un snapshot haute résolution
                SnapshotParameters params = new SnapshotParameters();
                WritableImage image = LineChartEvolution.snapshot(params, null);

                // Convertir en BufferedImage
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                // Créer un flux mémoire pour l'image PNG
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
                    contentStream.showText("Évolution des émargements - " + LocalDate.now());
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emargementService = new EmargementReportService();
        // Configurer les axes
        xAxis.setLabel("Date");
        yAxis.setLabel("Nombre d'émargements");

        // Configurer le graphique
        LineChartEvolution.setTitle("Évolution des émargements");
        LineChartEvolution.setCreateSymbols(true);
        LineChartEvolution.setAnimated(false);

        // Charger les données et créer le graphique
        chargerDonneesGraphique();

    }
}
