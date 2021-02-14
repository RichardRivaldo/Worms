package za.co.entelect.challenge.command;

import za.co.entelect.challenge.enums.Direction;

public class SelectCommand implements Command {

    private final int id;
    private final int x;
    private final int y;
    private final Boolean snowball;
    private final Boolean banana;
    private final Boolean dig;
    private final Boolean move;
    private final Boolean shoot;
    private final Direction direction;


    public SelectCommand(int id, Direction direction, int x, int y, Boolean snowball, Boolean banana, Boolean dig, Boolean move, Boolean shoot) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.snowball = snowball;
        this.banana = banana;
        this.dig = dig;
        this.move = move;
        this.shoot = shoot;
        this.direction = direction;
    }

    @Override
    public String render() {
        if (snowball) {
            return String.format("select %d;snowball %d %d", id, x, y);
        }
        else if (banana) {
            return String.format("select %d;banana %d %d", id, x, y);
        }
        else if (dig) {
            return String.format("select %d;dig %d %d", id, x, y);
        }
        else if (move) {
            return String.format("select %d;move %d %d", id, x, y);
        }
        else if (shoot) {
            return String.format("select %d;shoot %s", id, direction.name());
        }
        return "nothing";
    }
}
