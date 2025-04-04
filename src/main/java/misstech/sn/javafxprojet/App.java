package misstech.sn.javafxprojet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


import java.util.Objects;

import static org.hibernate.Hibernate.getClass;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/ConnectionPage.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MISSTECH'S SCHOOL");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
