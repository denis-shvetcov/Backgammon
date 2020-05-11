package sample;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class BackgammonCreate extends Application {

    public static Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        window = primaryStage;

        window.setResizable(false);

        Scene scene = new Scene(new Backgammon(),Backgammon.FIELD_WIDTH + Backgammon.stuff.getPrefWidth(),Backgammon.FIELD_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("\\css\\Backgammon.css").toExternalForm());

        window.setScene(scene);
        window.setTitle("Long backgammon");

        window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
