package sample;

import enums.CheckType;
import enums.Side;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Column extends VBox {
    private int columnInd;
    private CheckType checkType;
    private Side side;
    List<Check> checks = new ArrayList<>();


    Column(int columnInd, CheckType checkType, Side side) {
        this.columnInd = columnInd;
        this.checkType = checkType;
        this.side = side;

        setPrefHeight(Backgammon.COLUMN_HEIGHT);
        setPrefWidth(Backgammon.TALE_SIZE);

        setTranslateX(side == Side.LIGHT ? (11 - columnInd) * Backgammon.TALE_SIZE : columnInd * Backgammon.TALE_SIZE);
        setTranslateY(side == Side.LIGHT ? 0 : Backgammon.COLUMN_HEIGHT);

        getStyleClass().add(columnInd<6 ? "simple-column" : "home-column");

    }

    public int getColumnInd() {
        return columnInd;
    }

    public int getCheckNum() {
        return checks.size();
    }

    public Side getSide() {
        return side;
    }

    public CheckType getType() { return checkType; }


    public void addCheck(Check check) {
        if (checkType == CheckType.EMPTY) checkType = check.getType();
        checks.add(check);
    }

    public void removeCheck() {
        checks.remove(checks.size() - 1);
        if (checks.size() == 0) checkType = CheckType.EMPTY;
    }

    public Check getLastCheck() {
        return getCheckNum()>0 ? checks.get(getCheckNum()-1) : null;
    }

    public void disableLast(boolean value) {
        if (checks.size()>0) getLastCheck().setDisable(value);
    }

    public void lightUp() { getStyleClass().add("lightup-column");}

    public void lightOff() { getStyleClass().remove("lightup-column");}
}
