package misstech.sn.javafxprojet.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.dao.SalleImpl;
import misstech.sn.javafxprojet.entities.Salle;

import java.util.Objects;

public class SalleController {
    @FXML
    private TableView<Salle> tableauSalle;

    @FXML
    private TableColumn<Salle, Integer> tfid;

    @FXML
    private TableColumn<Salle, String> tfsalle;

    @FXML
    private TextField txtsalleee;

    private SalleImpl salleDao;
    private Salle salleSelectionnee;

    @FXML
    public void initialize() {
        salleDao = new SalleImpl();
        configurerTableau();
        chargerSalles();
    }

    private void configurerTableau() {
        tfid.setCellValueFactory(new PropertyValueFactory<>("id"));
        tfsalle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        tableauSalle.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionnerSalle(newValue));
    }

    private void chargerSalles() {
        ObservableList<Salle> salles = salleDao.getAll();
        tableauSalle.setItems(salles);
    }

    private void selectionnerSalle(Salle salle) {
        this.salleSelectionnee = salle;
        if (salle != null) {
            txtsalleee.setText(salle.getLibelle());
        }
    }



    @FXML
    void OnEnreg(ActionEvent event) {
        if (!validateInputs()) return;

        Salle salle = new Salle(txtsalleee.getText().trim());
        int result = salleDao.add(salle);

        if (result > 0) {
            showAlert("Succès", "Salle enregistrée avec succès", Alert.AlertType.INFORMATION);
            chargerSalles();
            viderChamps();
        } else {
            showAlert("Erreur", "Échec de l'enregistrement", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void OnMODIFIER(ActionEvent event) {
        if (salleSelectionnee == null) {
            showAlert("Avertissement", "Veuillez sélectionner une salle à modifier", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInputs()) return;

        salleSelectionnee.setLibelle(txtsalleee.getText().trim());
        int result = salleDao.update(salleSelectionnee);

        if (result > 0) {
            showAlert("Succès", "Salle modifiée avec succès", Alert.AlertType.INFORMATION);
            chargerSalles();
            viderChamps();
        } else {
            showAlert("Erreur", "Échec de la modification", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void OnSupprimer(ActionEvent event) {
        if (salleSelectionnee == null) {
            showAlert("Avertissement", "Veuillez sélectionner une salle à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette salle ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            int result = salleDao.delete(salleSelectionnee.getId());

            if (result > 0) {
                showAlert("Succès", "Salle supprimée avec succès", Alert.AlertType.INFORMATION);
                chargerSalles();
                viderChamps();
            } else {
                showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void load(MouseEvent event) {
    }

    private boolean validateInputs() {
        if (txtsalleee.getText() == null || txtsalleee.getText().trim().isEmpty()) {
            showAlert("Erreur de validation", "Le libellé de la salle est requis", Alert.AlertType.ERROR);
            return false;
        }

        if (txtsalleee.getText().trim().length() < 2) {
            showAlert("Erreur de validation", "Le libellé doit contenir au moins 2 caractères", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void viderChamps() {
        txtsalleee.clear();
        salleSelectionnee = null;
        tableauSalle.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}