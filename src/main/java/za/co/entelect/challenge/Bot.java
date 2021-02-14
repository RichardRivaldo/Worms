package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;
import za.co.entelect.challenge.enums.PowerUpType;

import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private Random random;
    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
    }

    private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    private Worm getEnemyWorm(GameState gameState){
        return Arrays.stream(opponent.worms)
                .filter(worm -> worm.health > 0)
                .findFirst()
                .get();
    }

    public Command run(){
        Worm enemyWorm = getFirstWormInRange();

        if(enemyWorm != null){
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
            if (currentWorm.id == 2 && currentWorm.bananas.count > 0){
                return new BananaBomb(enemyWorm.position);
            }
            else if (currentWorm.id == 3 && currentWorm.snowballs.count > 0){
                boolean foundFrozen = false;
                int i = 0;
                while(i < 3 && !foundFrozen){
                    if(opponent.worms[i].notFrozen != 0){
                        foundFrozen = true;
                    }
                    i++;
                }
                if(foundFrozen){
                    return new ShootCommand(direction);
                }
                else{
                    return new Snowball(enemyWorm.position);
                }
            }
            else{
                return new ShootCommand(direction);
            }
        }
        else{
            return digAndMove(currentWorm);
        }
    }

    private Command digAndMove(Worm currentWorm){
        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        int cellIdx = random.nextInt(surroundingBlocks.size());

        Cell block = surroundingBlocks.get(cellIdx);
        if (block.type == CellType.DIRT) {
            return new DigCommand(block.x, block.y);
        }
        else if (block.type == CellType.AIR) {
            return new MoveCommand(block.x, block.y);
        }
        else if (block.type == CellType.DEEP_SPACE){
            Cell blockNotDS = surroundingBlocks.get(cellIdx);
            if(blockNotDS.type != CellType.DEEP_SPACE){
                if(blockNotDS.type != CellType.DIRT || blockNotDS.type == CellType.AIR){
                    return new MoveCommand(blockNotDS.x, blockNotDS.y);
                }
                else if(blockNotDS.type == CellType.DIRT){
                    return new DigCommand(blockNotDS.x, blockNotDS.y);
                }
            }
        }
        else{
            Cell block1 = surroundingBlocks.get(cellIdx);
            if(block1.type == CellType.LAVA) {
                Cell block2 = surroundingBlocks.get(cellIdx);
                if(block2.type == CellType.AIR) {
                    return new MoveCommand(block2.x, block2.y);
                }
                else if(block2.type == CellType.DIRT){
                    return new DigCommand(block2.x, block2.y);
                }
            }
        }
        return new DoNothingCommand();
    }

    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition)) {
                return enemyWorm;
            }
        }

        return null;
    }

    private List<List<Cell>> constructFireDirectionLines(int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = currentWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = currentWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range) {
                    break;
                }

                Cell cell = gameState.map[coordinateY][coordinateX];
                if (cell.type != CellType.AIR) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    private List<Cell> getSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Don't include the current position
                if (i != x && j != y && isValidCoordinate(i, j)) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }

        return cells;
    }

    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    private Direction resolveDirection(Position a, Position b) {
        StringBuilder builder = new StringBuilder();

        int verticalComponent = b.y - a.y;
        int horizontalComponent = b.x - a.x;

        if (verticalComponent < 0) {
            builder.append('N');
        } else if (verticalComponent > 0) {
            builder.append('S');
        }

        if (horizontalComponent < 0) {
            builder.append('W');
        } else if (horizontalComponent > 0) {
            builder.append('E');
        }

        return Direction.valueOf(builder.toString());
    }
}
