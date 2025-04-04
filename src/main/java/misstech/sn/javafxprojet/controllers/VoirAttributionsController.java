package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.entities.Cours;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class VoirAttributionsController implements Initializable {
    @FXML
    private TableColumn<Cours, String> colCours;

    @FXML
    private TableColumn<Cours, String> colDescription;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureDebut;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureFin;

    @FXML
    private TableColumn<Cours, Integer> colId;

    @FXML
    private TableColumn<Cours, String> colProfesseur;
    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<Cours, String> colSalle;

    @FXML
    private TableView<Cours> tableAttributions;

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
        configurerColonnes();
        chargerAttributions();
    }
    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCours.setCellValueFactory(new PropertyValueFactory<>("nom")) ;
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date")); // Colonne date

        colSalle.setCellValueFactory(cellData -> {
            Cours cours = cellData.getValue();
            return new SimpleStringProperty(
                    cours.getSalle() != null ? cours.getSalle().getLibelle() : "Non assignée");
        });

        colProfesseur.setCellValueFactory(cellData -> {
            Cours cours = cellData.getValue();
            if (cours.getProfesseur() != null) {
                return new SimpleStringProperty(
                        cours.getProfesseur().getNom() + " " + cours.getProfesseur().getPrenom());
            }
            return new SimpleStringProperty("Non assigné");
        });
    }

    private void chargerAttributions() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            // Requête pour ne récupérer que les cours assignés à un professeur
            TypedQuery<Cours> query = em.createQuery(
                    "SELECT c FROM Cours c " +
                            "LEFT JOIN FETCH c.professeur " +
                            "LEFT JOIN FETCH c.salle " +
                            "WHERE c.professeur IS NOT NULL " + // Filtre pour les cours assignés
                            "ORDER BY c.date DESC, c.heureDebut ASC", Cours.class);

            List<Cours> cours = query.getResultList();
            tableAttributions.getItems().setAll(cours);

            if (cours.isEmpty()) {
                System.out.println("Aucun cours assigné trouvé");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des attributions: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
