package sample.enums;

public enum CheckType {
    DARK(1), LIGHT(0), EMPTY(-1);

    int value;

    CheckType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CheckType getByValue(int value) {
        return value == 0 ? LIGHT : DARK;
    }
}
