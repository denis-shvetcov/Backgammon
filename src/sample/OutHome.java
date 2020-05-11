package sample;

import enums.CheckType;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OutHome extends Pane {

    HBox root = new HBox(0);

    VBox LIGHT_STACK = new VBox(0);
    VBox DARK_STACK = new VBox(0);

    final int STACK_HEIGHT = 250;
    final int STACK_WIDTH = 200;

    final int CHECK_HEIGHT = STACK_HEIGHT /15;
    final int CHECK_WIDTH = STACK_WIDTH /2;

    final Rectangle LIGHT_CHECK = new Rectangle(CHECK_WIDTH,CHECK_HEIGHT);
    final Rectangle DARK_CHECK = new Rectangle(CHECK_WIDTH,CHECK_HEIGHT);



    OutHome() {
        setPrefWidth(STACK_WIDTH);
        setPrefHeight(STACK_HEIGHT);

        LIGHT_STACK.setPrefWidth(CHECK_WIDTH);
        DARK_STACK.setPrefWidth(CHECK_WIDTH);

        LIGHT_STACK.setPrefHeight(STACK_HEIGHT);
        DARK_STACK.setPrefHeight(STACK_HEIGHT);

        LIGHT_STACK.getStyleClass().add("outhome");
        DARK_STACK.getStyleClass().add("outhome");

        LIGHT_CHECK.getStyleClass().add("light-check");
        DARK_CHECK.getStyleClass().add("dark-check");

        LIGHT_CHECK.setStroke(Color.BLACK);
        DARK_CHECK.setStroke(Color.BLACK);

        root.getChildren().addAll(LIGHT_STACK,DARK_STACK);

        getChildren().add(root);
    }

    public void add(Check check) {
        if (check.getType() == CheckType.LIGHT){
            ObservableList<Node> children = LIGHT_STACK.getChildren();
            children.add(createCheck(CheckType.LIGHT));
            if (children.size() == 15) AlertWindow.display(CheckType.LIGHT);
        }
        else{
            ObservableList<Node> children = DARK_STACK.getChildren();
            children.add(createCheck(CheckType.DARK));
            if (children.size() == 15) AlertWindow.display(CheckType.DARK);
        }


    }

    private Rectangle createCheck(CheckType type) {
        Rectangle check = new Rectangle(CHECK_WIDTH,CHECK_HEIGHT);

        check.getStyleClass().add(type == CheckType.LIGHT ? "light-check" : "dark-check");
        check.setStroke(Color.BLACK);
        return check;
    }

    public void lightUp() {
        DARK_STACK.getStyleClass().add("lightup-outhome");
        LIGHT_STACK.getStyleClass().add("lightup-outhome");
    }

    public void lightOff() {
        DARK_STACK.getStyleClass().remove("lightup-outhome");
        LIGHT_STACK.getStyleClass().remove("lightup-outhome");
    }

    public void clear() {
        LIGHT_STACK.getChildren().clear();
        DARK_STACK.getChildren().clear();
    }


}
