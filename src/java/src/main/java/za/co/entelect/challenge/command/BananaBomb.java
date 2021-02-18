package za.co.entelect.challenge.command;

// New Command Class to do BananaBombs
public class BananaBomb implements Command{
    private final int x;
    private final int y;

    public BananaBomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String render(){
        return String.format("banana %d %d", x, y);
    }
}
