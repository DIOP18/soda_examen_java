package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.util.Objects;


import java.net.URL;
import javafx.scene.control.ComboBox;

import misstech.sn.javafxprojet.dao.UserImpl;
import misstech.sn.javafxprojet.entities.Role;
import misstech.sn.javafxprojet.entities.Users;



import java.util.ResourceBundle;



public class UserController implements Initializable {
    UserImpl user = new UserImpl();
    @FXML
    private TableView<Users> UserTable;

    @FXML
    private ComboBox<Role> cmbRole;

    @FXML
    private TableColumn<?, ?> colEmail;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colPassword;

    @FXML
    private TableColumn<?, ?> colRole;

    @FXML
    private TableColumn<?, ?> colnom;

    @FXML
    private TableColumn<?, ?> colprenom;

    @FXML
    private PasswordField pwfMotDPasse;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;

    private boolean validateInputs() {
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || pwfMotDPasse.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$")) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide (ex: soda@gmail.com ou contact@misstech.sn)", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void onSave(ActionEvent event) {
        if (validateInputs()) {
            Users NouveauUser = new Users(
                    txtNom.getText(),
                    txtPrenom.getText(),
                    txtEmail.getText(),
                    pwfMotDPasse.getText(),
                    cmbRole.getValue()
            );

            user.add(NouveauUser);
            showAlert("Succes","Utilisateur ajouté avec succes", Alert.AlertType.INFORMATION);
            refreshTable();
            clear();
        }
    }

    void clear(){
        txtPrenom.setText("");
        txtNom.setText("");
        txtEmail.setText("");
        pwfMotDPasse.setText("");
        cmbRole.setValue(null);

    }
    void refreshTable(){
        ObservableList<Users> listUsers = user.getAll();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        UserTable.setItems(listUsers);
    }
    @FXML
    void getRecharge(MouseEvent event) {
        if (event.getClickCount()== 2) {
            Users selection = UserTable.getSelectionModel().getSelectedItem();
            if (selection != null) {
                txtNom.setText(selection.getNom());
                txtPrenom.setText(selection.getPrenom());
                txtEmail.setText(String.valueOf(selection.getEmail()));
                pwfMotDPasse.setText(selection.getPassword());
                cmbRole.setValue(selection.getRole());
            }
        }


    }
    @FXML
    void onDelete(ActionEvent event) {
        Users selectionUtil = UserTable.getSelectionModel().getSelectedItem();
        if (selectionUtil != null) {
            // Vérifier si l'utilisateur est un professeur lié à des émargements ou cours
            if (selectionUtil.getRole() == Role.PROFESSEUR) {
                if (isProfessorAssociatedWithRecords(selectionUtil.getId())) {
                    showAlert("ERREUR",
                            "Ce professeur est associé à des émargements ou cours et ne peut pas être supprimé",
                            Alert.AlertType.ERROR);
                    return;
                }
            }

            try {
                user.delete(selectionUtil.getId());
                showAlert("INFO", "Utilisateur supprimé avec succès", Alert.AlertType.CONFIRMATION);
                refreshTable();
                clear();
            } catch (Exception e) {
                showAlert("ERREUR", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean isProfessorAssociatedWithRecords(int professorId) {
        EntityManager em = Persistence.createEntityManagerFactory("PERSISTENCE").createEntityManager();
        try {
            // Vérifier les émargements
            Long emargementCount = em.createQuery(
                            "SELECT COUNT(e) FROM Emargement e WHERE e.professeur.id = :profId", Long.class)
                    .setParameter("profId", professorId)
                    .getSingleResult();

            // Vérifier les cours
            Long coursCount = em.createQuery(
                            "SELECT COUNT(c) FROM Cours c WHERE c.professeur.id = :profId", Long.class)
                    .setParameter("profId", professorId)
                    .getSingleResult();

            return emargementCount > 0 || coursCount > 0;
        } finally {
            em.close();
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        if (validateInputs()) {
            Users selectUtil = (Users) UserTable.getSelectionModel().getSelectedItem();
            if (selectUtil != null) {
                selectUtil.setNom(txtNom.getText());
                selectUtil.setPrenom(txtPrenom.getText());
                selectUtil.setEmail(txtEmail.getText());
                selectUtil.setPassword(pwfMotDPasse.getText());
                selectUtil.setRole(cmbRole.getValue());
                user.update(selectUtil);
                showAlert("Succès", "Utilisateur modifié avec succès", Alert.AlertType.INFORMATION);
                refreshTable();
                clear();
            }
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbRole.getItems().addAll(Role.values());
        refreshTable();

    }
    @FXML
    void OnTableaudeBord(ActionEvent event) {
        try {

            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/dashboardAdmin.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
