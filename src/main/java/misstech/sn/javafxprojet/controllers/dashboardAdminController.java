package misstech.sn.javafxprojet.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class dashboardAdminController {
    @FXML
    private StackPane contentArea;
    private void loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Parent view = loader.load();
        Object controller = loader.getController();
        if (controller instanceof EmargementController) {
            ((EmargementController) controller).cacherBoutonDeconnexion();
        }

        contentArea.getChildren().setAll(view);
    }

    @FXML
    void OpenEmargements(ActionEvent event) {
        try {
            loadView("/pages/emargement.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void OpenSalles(ActionEvent event) {
        try {
            loadView("/pages/salle.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OpenUsers(ActionEvent event) {
        try {
            loadView("/pages/users.fxml");
        } catch (IOException e) {
            e.printStackTrace();
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
}
