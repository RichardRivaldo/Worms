package za.co.entelect.challenge.command;

import za.co.entelect.challenge.entities.Position;

public class Snowball implements Command{
    private Position position;

    public Snowball(Position position){
        this.position = position;
    }

    @Override
    public String render(){
        return String.format("snowball %d %d", position.x, position.y);
    }
}