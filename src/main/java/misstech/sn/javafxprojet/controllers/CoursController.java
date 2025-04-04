package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import misstech.sn.javafxprojet.dao.CoursImpl;
import misstech.sn.javafxprojet.entities.Cours;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import misstech.sn.javafxprojet.entities.Salle;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import javafx.scene.control.ListCell;

import java.util.ResourceBundle;


public class CoursController implements Initializable {
    CoursImpl cours = new CoursImpl();
    @FXML
    private TableView<Cours> TableCours;

    @FXML
    private ComboBox<Salle> cmbSalle;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> coldebut;

    @FXML
    private TableColumn<?, ?> coldescription;

    @FXML
    private TableColumn<?, ?> colfin;

    @FXML
    private TableColumn<?, ?> colnom;

    @FXML
    private TableColumn<?, ?> colsalle;
    @FXML
    private TableColumn<?, ?> coldate;

    @FXML
    private ComboBox<LocalTime> cmbDebut;

    @FXML
    private ComboBox<LocalTime> cmbFin;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;

    @FXML
    private TextField txtId;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button btnStatProfesseurs;
    private Cours selectedCours;
    @FXML
    void OnShowEvolutionEmargement(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/EvolutionDesEmargements.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Évolution des émargements (Graphique en ligne)");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void OnShowTauxdePresence(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/TauXdePresence.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Taux de présence par cours (Graphique en doughnut)");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void handleStatProfesseursClick(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/GraphiqueEmargement.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Nombre d’émargements par professeur (Graphique en barres)");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void OnSeeTaux(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/coursStatistique.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("RAPPORTS DES EMARGEMENTS DES PROFESSEURS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public Cours getSelectedCours() {
        return TableCours.getSelectionModel().getSelectedItem();
    }



    @FXML
    void AttribCoursProf(ActionEvent event) {
        Cours selectedCours = TableCours.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours d'abord", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/AttribProf.fxml"));
            Parent root = loader.load();
            AttribProf controller = loader.getController();
            controller.setSelectedCours(selectedCours);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Choisir Professeurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setContentText("Impossible de charger la page d'attribution de Professeur'.");
            alert.showAndWait();
        }
    }

    @FXML
    void OnVoirAttribution(ActionEvent event) {
        try {
            Parent attributionsView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/VoirAttribution.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(attributionsView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void OnDelete(ActionEvent event) {
        Cours deleteCours = TableCours.getSelectionModel().getSelectedItem();
        if(deleteCours != null) {
            // Détacher le professeur du cours avant suppression
            deleteCours.setProfesseur(null);
            cours.update(deleteCours); // Mettre à jour pour enlever la référence

            // Maintenant supprimer le cours
            cours.delete(deleteCours.getId());

            showAlert("SUPPRESSION", "Cours et attribution supprimés avec succès", Alert.AlertType.CONFIRMATION);
            Actualise();
            clear();
        }
    }
    @FXML
    void OnEdit(ActionEvent event) {
        if (validateInputs()) {
            Cours editCours = TableCours.getSelectionModel().getSelectedItem();
            if (editCours != null) {
                editCours.setNom(tfNom.getText());
                editCours.setDescription(tfPrenom.getText());
                editCours.setHeureDebut(cmbDebut.getValue());
                editCours.setHeureFin(cmbFin.getValue());
                editCours.setDate(datePicker.getValue()); // Mise à jour de la date
                editCours.setSalle(cmbSalle.getValue());
                cours.update(editCours);
                showAlert("MODIFICATION", "Cours MODIFIÉ AVEC SUCCÈS", Alert.AlertType.CONFIRMATION);
                Actualise();
                clear();
            }
        }
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean hasSalleConflict(LocalDate date, LocalTime debut, LocalTime fin, Salle salle, Integer coursIdToExclude) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            String queryStr = "SELECT c FROM Cours c WHERE c.salle = :salle AND c.date = :date " +
                    "AND ((c.heureDebut < :fin AND c.heureFin > :debut) " +
                    "OR (c.heureDebut = :debut AND c.heureFin = :fin))";

            if (coursIdToExclude != null) {
                queryStr += " AND c.id != :excludeId";
            }

            TypedQuery<Cours> query = em.createQuery(queryStr, Cours.class);
            query.setParameter("salle", salle);
            query.setParameter("date", date);
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);

            if (coursIdToExclude != null) {
                query.setParameter("excludeId", coursIdToExclude);
            }

            List<Cours> conflictingCours = query.getResultList();
            return !conflictingCours.isEmpty();
        } finally {
            em.close();
            emf.close();
        }
    }

    private boolean validateInputs() {
        if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }

        LocalTime debut = cmbDebut.getValue();
        LocalTime fin = cmbFin.getValue();
        Salle salle = cmbSalle.getValue();
        LocalDate date = datePicker.getValue(); // Récupération de la date

        if (debut == null || fin == null || salle == null) {
            showAlert("Erreur", "Veuillez sélectionner une heure de début, une heure de fin et une salle.", Alert.AlertType.ERROR);
            return false;
        }

        if (debut.isAfter(fin)) {
            showAlert("Erreur", "L'heure de début doit être inférieure à l'heure de fin.", Alert.AlertType.ERROR);
            return false;
        }

        if (date == null) {
            showAlert("Erreur", "Veuillez sélectionner une date", Alert.AlertType.ERROR);
            return false;
        }

        // Pour l'ajout (pas d'ID à exclure)
        Integer coursIdToExclude = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());

        // Appel corrigé avec les paramètres dans le bon ordre
        if (hasSalleConflict(date, debut, fin, salle, coursIdToExclude)) {
            showAlert("Conflit de salle", "La salle est déjà occupée pendant cette période.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }
    void clear(){
        tfPrenom.setText("");
        tfNom.setText("");
        cmbSalle.setValue(null);
        cmbDebut.setValue(null);
        cmbFin.setValue(null);
    }

    @FXML
    void OnSave(ActionEvent event) {
        if (validateInputs()) {
            Cours Nouveaucours = new Cours(
                                tfNom.getText(),
                                tfPrenom.getText(),
                                cmbDebut.getValue(),
                                cmbFin.getValue(),
                    cmbSalle.getValue(),
                    datePicker.getValue()
                        );

            cours.add(Nouveaucours);
            showAlert("Succes","Cours ajouté avec succes", Alert.AlertType.INFORMATION);
            Actualise();
            clear();
        }
    }

    void Actualise(){
        ObservableList<Cours> listCours = cours.getAll();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        coldebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colfin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colsalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableCours.setItems(listCours);
    }



    @FXML
    void load(MouseEvent event) {
        Cours cours = TableCours.getSelectionModel().getSelectedItem();
        if(cours != null) {
            tfNom.setText(cours.getNom());
            tfPrenom.setText(cours.getDescription());
            cmbDebut.setValue(cours.getHeureDebut());
            cmbFin.setValue(cours.getHeureFin());
            datePicker.setValue(cours.getDate()); // Charger la date
            cmbSalle.setValue(cours.getSalle());
        }
    }
    @FXML
    void onReturn(ActionEvent event) {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/ConnectionPage.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void loadSalles(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Salle> query = em.createQuery("SELECT s FROM Salle s", Salle.class);
            List<Salle> salles = query.getResultList();
            cmbSalle.getItems().addAll(salles);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }


        cmbSalle.setCellFactory(param -> new ListCell<Salle>() {
            @Override
            protected void updateItem(Salle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLibelle());
                }
            }
        });

        cmbSalle.setConverter(new StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle == null ? null : salle.getLibelle();
            }

            @Override
            public Salle fromString(String string) {
                return null;
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSalles();
        Actualise();
        datePicker.setValue(LocalDate.now());

        for (int i = 8; i < 19; i++) {
            cmbDebut.getItems().add(LocalTime.of(i, 0));
        }

        for (int i = 9; i < 22; i++) {
            cmbFin.getItems().add(LocalTime.of(i, 0));
        }
        for (int i = 8; i < 19; i++) {
            cmbDebut.getItems().add(LocalTime.of(i, 0));
        }

        for (int i = 9; i < 22; i++) {
            cmbFin.getItems().add(LocalTime.of(i, 0));
        }


    }
}
