package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import misstech.sn.javafxprojet.dao.CoursImpl;
import misstech.sn.javafxprojet.entities.Cours;
import misstech.sn.javafxprojet.entities.Role;
import misstech.sn.javafxprojet.entities.Users;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AttribProf implements Initializable {

    private Cours selectedCours;
    private CoursImpl coursDao = new CoursImpl();
    @FXML
    private ComboBox<Users> cmbAttribProf;

    @FXML
    void OnAnnuler(ActionEvent event) {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/cours.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    void OnAttribuer(ActionEvent event) {
        Users selectedProfesseur = cmbAttribProf.getValue();
        if (selectedProfesseur == null) {
            showAlert("Erreur", "Veuillez sélectionner un professeur", Alert.AlertType.ERROR);

        }
        if (selectedCours == null) {
            showAlert("Erreur", "Aucun cours sélectionné", Alert.AlertType.ERROR);

        }
        if (verifierConflitsHoraires(selectedProfesseur, selectedCours)) {
            showAlert("Conflit d'horaires",
                    "Le professeur a déjà un cours prévu à cette heure",
                    Alert.AlertType.WARNING);
            return;
        }



        selectedCours.setProfesseur(selectedProfesseur);
        coursDao.update(selectedCours);


        showAlert("Succès", "Le professeur a été attribué au cours avec succès",
                Alert.AlertType.INFORMATION);
        ouvrirNotification(event, selectedProfesseur, "Vous avez été attribué à un nouveau cours.");
        //OnReturn(event);
    }


    private boolean verifierConflitsHoraires(Users professeur, Cours nouveauCours) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            // Requête optimisée qui vérifie directement le chevauchement horaire
            String jpql = "SELECT COUNT(c) > 0 FROM Cours c " +
                    "WHERE c.professeur.id = :profId " +
                    "AND c.date = :date " +
                    "AND c.id != :coursId " + // Exclure le cours actuel si c'est une modification
                    "AND ((c.heureDebut < :heureFin AND c.heureFin > :heureDebut))";

            TypedQuery<Boolean> query = em.createQuery(jpql, Boolean.class);
            query.setParameter("profId", professeur.getId());
            query.setParameter("date", nouveauCours.getDate());
            query.setParameter("coursId", nouveauCours.getId());
            query.setParameter("heureDebut", nouveauCours.getHeureDebut());
            query.setParameter("heureFin", nouveauCours.getHeureFin());

            return query.getSingleResult();
        } finally {
            em.close();
            emf.close();
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
    public void loadProfesseur(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Users> query = em.createQuery("SELECT u FROM Users u WHERE u.role = :role", Users.class);
            query.setParameter("role", Role.PROFESSEUR);
            List<Users> professeurs = query.getResultList();
            cmbAttribProf.getItems().addAll(professeurs);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
        cmbAttribProf.setCellFactory(param -> new ListCell<Users>() {
            @Override
            protected void updateItem(Users item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " " + item.getPrenom());
                }
            }
        });
        cmbAttribProf.setConverter(new StringConverter<Users>() {
            @Override
            public String toString(Users user) {
                return user == null ? null : user.getNom() + " " + user.getPrenom();
            }

            @Override
            public Users fromString(String string) {
                return null;
            }
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProfesseur();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/Cours.fxml"));
            Parent root = loader.load();
            CoursController coursController = loader.getController();
            selectedCours = coursController.getSelectedCours();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ouvrirNotification(ActionEvent event, Users professeur, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/notification.fxml"));
            Parent notificationView = loader.load();
            NotificationController notificationController = loader.getController();
            notificationController.setNotificationData(
                    professeur,
                    message,
                    java.time.LocalDate.now()
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(notificationView));
            stage.setTitle("NOTIFICATION");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setSelectedCours(Cours cours) {
        this.selectedCours = cours;
    }
}
