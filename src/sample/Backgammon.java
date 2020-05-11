package sample;

import sample.enums.CheckType;
import sample.enums.MoveType;
import sample.enums.Side;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Backgammon extends Pane {
    //Константы
    final static int TALE_SIZE = 60;
    final static int COLUMN_HEIGHT = TALE_SIZE / 2 * 16;
    final static int FIELD_WIDTH = TALE_SIZE * 12;
    final static int FIELD_HEIGHT = COLUMN_HEIGHT * 2;

    //Колонны
    final static Column[][] COLUMNS = new Column[2][12];
    private final static List<Column> lightColumns = new ArrayList<>();

    // Картинки
    private final static Image[] DICE_SIDES = new Image[6];
    private final static ImageView arrowsImg = new ImageView();
    private final static ImageView dice1 = new ImageView();
    private final static ImageView dice2 = new ImageView();

    //Фишки
    private final static HashSet<Check> MOVABLE_CHECKS = new HashSet<>(15);
    private final static Set<Check> LIGHT_HOME = new HashSet<>(15);
    private final static Set<Check> DARK_HOME = new HashSet<>(15);

    //Панели
    final static OutHome outHome = new OutHome();
    final static Pane field = new Pane();
    final static Pane stuff = new Pane();

    // Флаги
    private static boolean lightFirstRoll ;
    private static boolean darkFirstRoll  ;
    private static boolean movableExists ;
    private static boolean fullLightHome ;
    private static boolean fullDarkHome ;

    private static Button play = new Button("Play"); ;

    private static int dice1Value;
    private static int dice2Value;

    private final static Rotate rotate = new Rotate();
    private static Player player;


    public Backgammon() throws FileNotFoundException {
        refresh();

        arrowsImg.setImage(new Image(new FileInputStream("src\\images\\arrows.png")));
        arrowsImg.setFitHeight(25);
        arrowsImg.setFitWidth(25);

        for (int i = 0; i < 6; i++) {
            DICE_SIDES[i] = new Image(new FileInputStream(String.format("src\\images\\dice%d.png", i + 1)));
        }

        rotate.setPivotX(FIELD_WIDTH / 2);
        rotate.setPivotY(COLUMN_HEIGHT);

        field.getTransforms().add(rotate);

        stuff.setTranslateX(FIELD_WIDTH);
        stuff.setPrefWidth(280);
        stuff.setPrefHeight(FIELD_HEIGHT);
        stuff.getStyleClass().add("stuff");

        VBox infoField = new VBox(20);

        infoField.setTranslateY(FIELD_HEIGHT / 2 - 200);
        infoField.setAlignment(Pos.CENTER);
        infoField.setPrefWidth(stuff.getPrefWidth());


        play.getStyleClass().add("play-btn");
        play.setOnAction(event -> rollDice());

        dice1.setFitWidth(80);
        dice1.setPreserveRatio(true);
        dice2.setFitWidth(80);
        dice2.setPreserveRatio(true);
        HBox diceBox = new HBox(10);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.getChildren().addAll(dice1, dice2);

        infoField.getChildren().addAll(play, diceBox);

        outHome.setTranslateX((stuff.getPrefWidth() - outHome.getPrefWidth()) / 2);
        outHome.setTranslateY(FIELD_HEIGHT / 2 );

        stuff.getChildren().addAll(outHome, infoField);

        getChildren().addAll(stuff, field);

    }

    public void refresh() {
        lightFirstRoll = true;
        darkFirstRoll = true;
        movableExists = true;
        fullLightHome = false;
        fullDarkHome = false;

        MOVABLE_CHECKS.clear();
        LIGHT_HOME.clear();
        DARK_HOME.clear();
        field.getChildren().clear();
        outHome.clear();
        dice1.setImage(null);
        dice2.setImage(null);
        rotate.setAngle(0);

        player = new Player();
        play.setText("Новая игра");
        play.setGraphic(null);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 12; j++) {
                Column column = new Column(j, CheckType.EMPTY, Side.getByValue(i));
                COLUMNS[i][j] = column;
                field.getChildren().add(column);
            }
        }

        //Создание фишек
        for (int i = 0; i < 2; i++) {

            for (int k = 0; k < 15; k++) {
                //добавляем фишки в первый столб
                Check check = new Check(CheckType.getByValue(i), 0, Side.getByValue(i), k, player);
                COLUMNS[i][0].addCheck(check);


                // Реализация перемещения фишки
                check.setOnMouseReleased(event -> {
                    if (checkIntersectsOutHome(event.getSceneX(), event.getSceneY()) && check.canMoveOut) {
                        check.moveOut();
                        outHome.add(check);

                        lightOffColumns();
                        lightUpMovable();
                        outHome.lightOff();

                        return;
                    }

                    Column newColumn = findColumn(event.getSceneX(), event.getSceneY());
                    newColumn = newColumn == null ? check.getOldColumn() : newColumn; // чтобы предотвратить выход за поле

                    // проверка куша первым ходом
                    if ((lightFirstRoll || darkFirstRoll) && kushFirstRoll())
                        check.kushFirst = true;

                    // проверка, что фишка может передвинуться в колонну
                    if (tryMove(newColumn, check) == MoveType.NORMAL)
                        check.move(newColumn);
                    else
                        check.dontMove();

                    //пополнение фишек в доме
                    if (check.isHome()) {
                        if (check.getType() == CheckType.LIGHT)
                            LIGHT_HOME.add(check);
                        else
                            DARK_HOME.add(check);
                    }

                    setFullHome();

                    lightOffColumns();
                    lightUpMovable();

                    outHome.lightOff();
                });

                field.getChildren().add(check);
            }
        }

    }

    private boolean checkIntersectsOutHome(double x, double y) {
        return x >= FIELD_WIDTH + outHome.getTranslateX() &&
                x <= FIELD_WIDTH + outHome.getTranslateX() + outHome.getPrefWidth() &&
                y >= outHome.getTranslateY() &&
                y <= outHome.getTranslateY() + outHome.getPrefHeight();
    }

    public static void rollDice() {
        // первый бросок
        if (player.getType() == null) {
            player.switchType();
            play.setText("Передать ход");
            play.setGraphic(arrowsImg);
            play.setContentDisplay(ContentDisplay.LEFT);

            prepareField();
        } else {
            // Если есть возможность походить, то разворачиваем доску и передаем ход, иначе оповещаем, что не все ходы использованы
            if (!movableExists) {
                player.switchType();
                rotate.setAngle((rotate.getAngle() + 180) % 360);

                prepareField();
            }
        }
    }


    private static void prepareField() {

        dice1Value = ((int) (Math.random() * 6)) + 1;
        dice2Value = ((int) (Math.random() * 6)) + 1;

        dice1.setImage(DICE_SIDES[dice1Value - 1]);
        dice2.setImage(DICE_SIDES[dice2Value - 1]);

        player.setMoves(dice1Value, dice2Value);

        //"блокируем" фишки соперника
        Arrays.stream(COLUMNS).forEach(array -> Arrays.stream(array).forEach(column -> {
            if (column.getType() == player.getType()) {
                column.disableLast(false);
            } else {
                column.disableLast(true);
            }
        }));

        lightUpMovable();
    }


    private static Column findColumn(double x, double y) {
        Side side;
        int columnInd;
        if (x >= 0 && x <= FIELD_WIDTH && y >= 0 && y <= FIELD_HEIGHT) {
            if (player.getType() == CheckType.LIGHT) {
                side = y >= COLUMN_HEIGHT ? Side.DARK : Side.LIGHT; // Сторона определяется в зависимости от поворота
                columnInd = side == Side.LIGHT ? 11 - ((int) x) / TALE_SIZE : ((int) x) / TALE_SIZE;
            } else {
                side = y < COLUMN_HEIGHT ? Side.DARK : Side.LIGHT;// Сторона определяется в зависимости от поворота
                columnInd = side == Side.DARK ? (int) rotatedX(x) / TALE_SIZE : 11 - (int) rotatedX(x) / TALE_SIZE;
            }
            return COLUMNS[side.getValue()][columnInd];

        }
        return null; // чтобы предотвратить выход за поле
    }

    private static double rotatedX(double x) {
        return FIELD_WIDTH - x;
    }


    private static MoveType tryMove(Column newColumn, Check check) {
        if (check != null) {
            Column oldColumn = check.getOldColumn();
            CheckType checkType = check.getType();
            CheckType newColumnType = newColumn.getType();

            if ((newColumnType == checkType || newColumnType == CheckType.EMPTY) &&
                    newColumn != oldColumn && player.hasMoves()) {

                int columnDelta = columnDelta(newColumn, oldColumn);

                if (player.contains(columnDelta)) {
                    //проверка, что фишка не может быть передвинута на другую сторону, иначе ход по полю зациклится
                    if (check.moved + columnDelta <= 23) {
                        return MoveType.NORMAL;
                    } else
                        return MoveType.NONE;
                }
            }
        }
        return MoveType.NONE;

    }

    public static int columnDelta(Column newColumn, Column oldColumn) {
        if (newColumn.getSide() == oldColumn.getSide())
            return newColumn.getColumnInd() - oldColumn.getColumnInd();
        else
            return newColumn.getColumnInd() + 12 - oldColumn.getColumnInd();
    }


    public static void lightUpColumns(Column from, Check check) {

        Side fromSide = from.getSide();
        int fromColumnInd = from.getColumnInd();
        for (int move : player.getMoves()) {
            Column to;

            if (fromSide == Side.DARK) {
                if (from.getColumnInd() + move < 12)
                    to = COLUMNS[1][fromColumnInd + move];
                else
                    to = COLUMNS[0][(fromColumnInd + move) % 12];
            } else {
                if (from.getColumnInd() + move < 12)
                    to = COLUMNS[0][fromColumnInd + move];
                else
                    to = COLUMNS[1][(fromColumnInd + move) % 12];
            }

            if (tryMove(to, check) == MoveType.NORMAL && (move == player.move1 || move == player.move2)) {
                lightColumns.add(to);
                to.lightUp();
                continue;
            }

            if (tryMove(to, check) == MoveType.NORMAL && !lightColumns.isEmpty()) {
                lightColumns.add(to);
                to.lightUp();
            }
        }

    }

    //подсвечивает фишки, которые могут куда-то сдвинуться, также устанавливается флаг, существуют ли они вообще
    // подсвечиваются в начале хода и каждый раз, когда игрок ставит фишку
    // ходы игрока сортируются для удобства
    private static void lightUpMovable() {
        removeMovable(); // удаляем светящиеся фишки

        if (player.hasMoves()) {
            Arrays.stream(COLUMNS).forEach(array -> Arrays.stream(array).forEach(column -> {

                Check lastCheck = column.getLastCheck();
                Side fromSide = column.getSide();
                int fromColumnInd = column.getColumnInd();

                if (lastCheck != null && !lastCheck.isDisabled()) {

                    for (int move : player.getMoves()) {
                        Column to;

                        if (fromSide == Side.DARK) {
                            if (fromColumnInd + move < 12)
                                to = COLUMNS[1][fromColumnInd + move];
                            else
                                to = COLUMNS[0][(fromColumnInd + move) % 12];
                        } else {
                            if (fromColumnInd + move < 12)
                                to = COLUMNS[0][fromColumnInd + move];
                            else
                                to = COLUMNS[1][(fromColumnInd + move) % 12];
                        }
                        if (tryMove(to, lastCheck) == MoveType.NORMAL && (move == player.move1 || move == player.move2)) {
                            lastCheck.lightUpMovable();
                            MOVABLE_CHECKS.add(lastCheck);
                            continue;
                        }

                        // если в списке movable уже есть эта фишка, значит можно реализовать "двойной ход", т.к. путь свободен
                        if (tryMove(to, lastCheck) == MoveType.NORMAL && MOVABLE_CHECKS.contains(lastCheck)) {
                            lastCheck.lightUpMovable();
                            MOVABLE_CHECKS.add(lastCheck);
                        }
                    }
                }
            }));
            //подсветим фишки, которые можно вывести из дома
            lightUpMoveOut();
        }
        movableExists = MOVABLE_CHECKS.size() > 0;
    }

    private static void lightUpMoveOut() {
        if (checkFullHome()) {
            CheckType oppositeCheckType = player.getType() == CheckType.LIGHT ? CheckType.DARK : CheckType.LIGHT;

            List<Integer> moves = player.getMoves();
            moves.remove(Integer.valueOf(player.multyMove));

            boolean isFound = false;
            //проверяется наличие фишек на позициях, соответствующих значениям кубика
            for (int move : moves) {
                Check check = COLUMNS[oppositeCheckType.getValue()][12 - move].getLastCheck();

                if (check != null && check.getType() == player.getType()) {
                    check.lightUpMoveOut(move);
                    MOVABLE_CHECKS.add(check);
                    isFound = true;
                }
            }
            int toMoveOut = Collections.max(moves);
            //если фишка не найдена, то нужно проверить наличие фишек до позиции, соответствующей максимальному значению
            //кубиков, так как при их наличии нельзя будет  выводить шашки с полей низшего разряда, если в полях
            // высшего разряда шашек нет
            if (!isFound) {
                for (int i = 6; i > toMoveOut; i--) {
                    Check check = COLUMNS[oppositeCheckType.getValue()][12 - i].getLastCheck();
                    if (check != null && check.getType() == player.getType()) {
                        isFound = true;
                    }
                }
            }

            if (!isFound) {
                for (int i = toMoveOut; i > 0; i--) {
                    Check check = COLUMNS[oppositeCheckType.getValue()][12 - i].getLastCheck();
                    if (check != null && check.getType() == player.getType()) {
                        check.lightUpMoveOut(toMoveOut);
                        MOVABLE_CHECKS.add(check);
                        return;
                    }
                }
            }
        }
    }

    private static void removeMovable() {
        MOVABLE_CHECKS.forEach(Check::lightOff);
        MOVABLE_CHECKS.clear();
    }

    private static void lightOffColumns() {
        lightColumns.forEach(Column::lightOff);
        lightColumns.clear();
    }

    // Проверка куша первым броском, при котором на костях либо 3, либо 4, либо 6
    private boolean kushFirstRoll() {
        boolean result = false;
        if (player.getType() == CheckType.LIGHT) {
            if (lightFirstRoll && dice1Value == dice2Value && (dice1Value == 3 || dice1Value == 4 || dice1Value == 6)) {
                result = true;
            }
            lightFirstRoll = false;
        } else {
            if (darkFirstRoll && dice1Value == dice2Value && (dice1Value == 3 || dice1Value == 4 || dice1Value == 6)) {
                result = true;
            }
            darkFirstRoll = false;
        }
        return result;
    }

    private static boolean checkFullHome() {
        return player.getType() == CheckType.LIGHT ? fullLightHome : fullDarkHome;
    }

    private void setFullHome() {
        if (LIGHT_HOME.size() == 15) fullLightHome = true;
        if (DARK_HOME.size() == 15) fullDarkHome = true;
    }

}
