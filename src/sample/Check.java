package sample;

import enums.CheckType;
import enums.Side;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;


public class Check extends Pane {
    private int taleSize = Backgammon.TALE_SIZE;
    private double halfTaleSize = taleSize / 2;
    private int columnHeight = Backgammon.COLUMN_HEIGHT;

    private Circle check;
    private CheckType type;
    private int columnInd;
    private Side side;


    public boolean canMoveOut;
    public int toMoveOut;

    boolean kushFirst = false;
    boolean home;

    int moved;

    private Column oldColumn;

    private Player player;

    public Check(CheckType type, int columnInd, Side side, int index, Player player) {
        this.columnInd = columnInd;
        this.type = type;
        this.side = side;
        this.player = player;
        this.home = false;
        this.oldColumn = side == Side.DARK ? Backgammon.COLUMNS[1][0] : Backgammon.COLUMNS[0][0];
        this.moved = 0;
        this.canMoveOut = false;

        setPrefHeight(halfTaleSize);
        setPadding(new Insets(taleSize * 0.03));

        setTranslateX(side == Side.DARK ? columnInd * taleSize : (11 - columnInd) * taleSize);
        setTranslateY(side == Side.LIGHT ? index * (halfTaleSize) : columnHeight * 2 - index * (halfTaleSize) - taleSize);

        setDisable(true);

        check = new Circle(halfTaleSize, halfTaleSize, halfTaleSize - taleSize * 0.03);

        check.getStyleClass().add("check");
        check.getStyleClass().add(type == CheckType.LIGHT ? "light-check" : "dark-check");

        setOnMousePressed(event -> {
            if (canMoveOut) Backgammon.outHome.lightUp();

            Backgammon.lightUpColumns(oldColumn, this);
        });

        setOnMouseDragged(event -> {
            if (side == Side.LIGHT) {
                setTranslateX(event.getSceneX() - halfTaleSize);
                setTranslateY(event.getSceneY() - halfTaleSize);
            } else {
                setTranslateX(rotatedX(event.getSceneX()) - halfTaleSize);
                setTranslateY(rotatedY(event.getSceneY()) - halfTaleSize);
            }
        });


        getChildren().add(check);
    }

    public void move(Column newColumn) {
        oldColumn.removeCheck();
        if (moved != 0 || kushFirst) {
            oldColumn.disableLast(false);
            kushFirst = false;
        }

        newColumn.disableLast(true);
        newColumn.addCheck(this);

        int move = Backgammon.columnDelta(newColumn, oldColumn);
        moved += move;
        player.move(move);


        columnInd = newColumn.getColumnInd();
        side = newColumn.getSide();
        int newCheckIndex = newColumn.getCheckNum() - 1;


        setTranslateX(side == Side.DARK ? columnInd * taleSize : (11 - columnInd) * taleSize);
        setTranslateY(side == Side.LIGHT ? newCheckIndex * (halfTaleSize) : columnHeight * 2 - newCheckIndex * (halfTaleSize) - taleSize);

        checkHome();
        oldColumn = newColumn;
    }

    public void dontMove() {
        setTranslateX(side == Side.DARK ? oldColumn.getColumnInd() * taleSize : (11 - oldColumn.getColumnInd()) * taleSize);
        setTranslateY(side == Side.LIGHT ? (oldColumn.getCheckNum() - 1) * (halfTaleSize) : columnHeight * 2 - (oldColumn.getCheckNum() - 1) * (halfTaleSize) - taleSize);
    }

    public CheckType getType() {
        return type;
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    private double rotatedX(double x) {
        return taleSize * 12 - x;
    }

    private double rotatedY(double y) {
        return columnHeight * 2 - y;
    }

    private void checkHome() { home = moved > 17; }


    public void lightUpMovable() {
        ObservableList<String> styles = check.getStyleClass();
        if (!styles.contains("movable-check")) styles.add("movable-check");
    }

    public void lightUpMoveOut(int move) {
        ObservableList<String> styles = check.getStyleClass();
        toMoveOut = move;
        if (!styles.contains("can-move-out")){
            styles.add("can-move-out");
            canMoveOut = true;
        }
    }

    public void lightOff() {
        check.getStyleClass().remove("movable-check");
        check.getStyleClass().remove("can-move-out");
        canMoveOut = false;
    }

//    public int getColumndInd() {
//        return columnInd;
//    }

//    public Side getSide() { return side; }

    public boolean isHome() {
        return home;
    }


    public void moveOut() {
        player.move(toMoveOut);
        oldColumn.removeCheck();
        oldColumn.disableLast(false);
        Backgammon.field.getChildren().remove(this);
    }
}
