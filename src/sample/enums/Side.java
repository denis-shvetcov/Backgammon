package sample.enums;

public enum Side {
    DARK (1), LIGHT(0);
    int value;

    Side (int value) {
        this.value = value;
    }

    public int getValue() {return value;}

   static public Side getByValue(int value) {
        return value==0 ? LIGHT : DARK;
    }
}
