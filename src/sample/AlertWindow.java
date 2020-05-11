package sample;

import sample.enums.CheckType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AlertWindow {
    static private boolean play;

    public static boolean display(CheckType type) {

        Stage window = new Stage();

        window.setTitle("New window");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox messageAndButtons = new VBox(10);
        messageAndButtons.setAlignment(Pos.CENTER);
        messageAndButtons.getStyleClass().add("message-and-buttons");

        Label message = new Label();
        message.setText(String.format("Победил игрок, играющий %s фишками",
                type == CheckType.LIGHT ? "светлыми" : "темными"));
        message.getStyleClass().add("message");

        Button newGame = new Button("Новая игра");
        newGame.setOnAction(event -> {
            play = true;
            window.close();
            ((Backgammon) BackgammonCreate.window.getScene().getRoot()).refresh();

        });

        Button close = new Button("Выйти из игры");
        close.setOnAction(event -> {
            play = false;
            window.close();
            BackgammonCreate.window.close();
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(newGame,close);

        messageAndButtons.getChildren().addAll(message,buttons);

        Scene scene = new Scene(messageAndButtons,400,150);
        scene.getStylesheets().add(AlertWindow.class.getResource("\\css\\Alert.css").toString());

        window.setScene(scene);
        window.showAndWait();

        return play;
    }
}
