package za.co.entelect.challenge.command;

import za.co.entelect.challenge.entities.Position;
import za.co.entelect.challenge.enums.Direction;

public class BananaBomb implements Command{
    private Position position;

    public BananaBomb(Position pos){
        this.position = pos;
    }

    @Override
    public String render(){
        return String.format("banana %d %d", position.x, position.y);
    }
}
