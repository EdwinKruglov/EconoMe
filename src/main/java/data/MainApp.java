package main.java.data;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application{
    @Override
    public void start(Stage stage) throws Exception{
        Parent root = (Pane)FXMLLoader.load(getClass().getClassLoader().getResource("EconoMe.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("EconoMe");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
