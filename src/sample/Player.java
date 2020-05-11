package sample;

import enums.CheckType;

import java.util.ArrayList;
import java.util.List;

public class Player {
    CheckType checkType;

    int move1;
    int move2;
    int multyMove;

    int moveNum1 = 0;
    int moveNum2 = 0;
    int multyMoveNum = 0;


    public List<Integer> moves = new ArrayList<>();

    public CheckType getType() {
        return checkType;
    }

    public void switchType() {
        checkType = checkType == CheckType.DARK || checkType == null ? CheckType.LIGHT : CheckType.DARK;
    }


    public List<Integer> getMoves() {
        List<Integer> moves = new ArrayList<>();
        if (moveNum1 > 0) moves.add(move1);
        if (moveNum2 > 0) moves.add(move2);
        if (multyMoveNum > 0) moves.add(multyMove);
        return moves;
    }


    public void setMoves(int move1, int move2) {
        this.move1 = move1;
        this.move2 = move2;
        this.multyMove = move1 + move2;
        if (move1 == move2) {
            moveNum1 = 2;
            moveNum2 = 2;
            multyMoveNum = 1;
        } else {
            moveNum1 = 1;
            moveNum2 = 1;
            multyMoveNum = 1;
        }
    }

    public boolean hasMoves() {
        return moveNum1 > 0 || moveNum2 > 0;
    }

    public boolean contains(int move) {
        return move == move1 || move == move2 || move == multyMove;
    }

    public void move(int move) {
        if (move == multyMove) {
            multyMoveNum--;

            moveNum1--;
            moveNum2--;
        } else {
            if (move == move1 && moveNum1>0) {
                multyMoveNum--;
                moveNum1--;
            } else {
                multyMoveNum--;
                moveNum2--;
            }
        }

        //так как сначала сокращается moveNum1, то в случае куша может оказаться, что moveNum1=0 , а moveNum2=2
        if ((moveNum1==0 && moveNum2==2) || (moveNum1>0 && moveNum2>0)) multyMoveNum=1;
    }

}
