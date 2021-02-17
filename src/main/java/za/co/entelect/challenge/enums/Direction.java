package za.co.entelect.challenge.enums;

public enum Direction {

    N(0, -1),
    NE(1, -1),
    E(1, 0),
    SE(1, 1),
    S(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(-1, -1);

    // Modified into non final attributes
    public int x;
    public int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
