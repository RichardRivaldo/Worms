package za.co.entelect.challenge.command;

// New Command Class to do Snowballs
public class Snowball implements Command{
    private final int x;
    private final int y;

    public Snowball(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String render(){
        return String.format("snowball %d %d", x, y);
    }
}