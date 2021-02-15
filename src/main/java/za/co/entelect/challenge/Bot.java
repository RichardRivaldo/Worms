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

    // Added function to get an alive enemy worm
    private Worm getEnemyWorm(GameState gameState){
        return Arrays.stream(opponent.worms)
                .filter(worm -> worm.health > 0)
                .findFirst()
                .get();
    }

    public Command run(){

        // Get enemy worms in:
        Worm enemyWorm = getFirstWormInRange(currentWorm, currentWorm.weapon.range); // Weapon Range
        Worm enemyBananaSnowball = getFirstWormInRange(currentWorm, 5); // Snowball and Banana Range

        // self Defense another Worm
        if (gameState.myPlayer.remainingWormSelections>0) {
            for (MyWorm another : gameState.myPlayer.worms) {
                if (another.id == gameState.currentWormId) continue;
                if (another.health > 0) {
                    Worm enemy = getFirstWormInRange(another, another.weapon.range);
                    Worm enemySpecial = getFirstWormInRange(another, 5);
                    if((enemy != null || enemySpecial != null)){
                        if(enemy == null){
                            enemy = enemySpecial;
                        }
                        Direction direction = resolveDirection(another.position, enemy.position);
                        if (another.id == 2 && another.bananas.count > 0) {
                            return new SelectCommand(another.id, null, enemySpecial.position.x, enemySpecial.position.y, false, true, false, false, false);
                        } else if (another.id == 3 && another.snowballs.count > 0) {
                            boolean foundFrozen = false;
                            int i = 0;
                            while (i < 3 && !foundFrozen) {
                                if (opponent.worms[i].notFrozen != 0) {
                                    return new SelectCommand(another.id, direction, -1, -1, false, false, false, false, true);
                                }
                                i++;
                            }
                            if (foundFrozen) {
                                return new SelectCommand(another.id, direction, -1, -1, false, false, false, false, true);
                            } else {
                                return new SelectCommand(another.id, null, enemySpecial.position.x, enemySpecial.position.y, true, false, false, false, false);
                            }
                        } else {
                            return new SelectCommand(another.id, direction, -1, -1, false, false, false, false, true);
                        }
                    }
                }
            }
        }


        // Attack Strategy

        // Check if any enemy is in range
        if((enemyWorm != null || enemyBananaSnowball != null)){
            if(enemyWorm == null){
                // Maximizing Detection Range and Ability
                // Weapon Range < Banana or Snowball Range
                // Detected enemy on Banana or Snowball range first
                // Set enemyWorm as enemyBananaSnowball
                enemyWorm = enemyBananaSnowball;
            }

            // Find direction between my worm and enemy's worm
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);

            // Shooting Strategy
            // Maximizing Damage and Utility
            // Check my worm id first before deciding
            // Also check inventory for special trained attacks -> Banana or Snowball
            if (currentWorm.id == 2 && currentWorm.bananas.count > 0){
                // Maximizing Damage
                // Worm = Agent
                // Can Use Banana
                return new BananaBomb(enemyWorm.position.x, enemyWorm.position.y);
            }
            else if (currentWorm.id == 3 && currentWorm.snowballs.count > 0){
                // Maximizing Utility
                // Worm = Technologist
                // Can Use Snowball

                // Detect if any enemy's worm is frozen
                boolean foundFrozen = false;
                int i = 0;
                while(i < 3 && !foundFrozen){
                    if(opponent.worms[i].notFrozen != 0){
                        foundFrozen = true;
                    }
                    i++;
                }
                if(foundFrozen){
                    // Maximizing Freeze Duration between each snowball used
                    // One or more enemy's worm is frozen
                    // Do ordinary shoot before using another snowball
                    return new ShootCommand(direction);
                }
                else{
                    // No enemy frozen -> If missed snowball or before finding any enemies
                    // Use snowball to freeze enemies
                    return new Snowball(enemyWorm.position.x, enemyWorm.position.y);
                }
            }
            else{
                // Worm = Commando, Agent, Technologist
                // No Banana for Agent
                // No Snowball for Technologist
                // Still do shoot command as many as possible to maximize damage output
                return new ShootCommand(direction);
            }
        }
        else{
            // Movement code here
            Worm enemy = opponent.worms[random.nextInt(3)];
            Direction dir = resolveDirection(currentWorm.position, enemy.position);
            return digAndMove(currentWorm);
        }
    }

    // Move Intuition Idea
    private Command digAndMove(Worm currentWorm){
        // Get surrounding cells of current wormr
        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        int cellIdx = random.nextInt(surroundingBlocks.size());

        // Choose random block cells
        Cell block = surroundingBlocks.get(cellIdx);

        // Check cell type
        if (block.type == CellType.DIRT) {
            // Dig if dirt
            return new DigCommand(block.x, block.y);
        }
        else if (block.type == CellType.AIR) {
            // Move if air
            return new MoveCommand(block.x, block.y);
        }
        else if (block.type == CellType.DEEP_SPACE){
            // Find other cell if deep space
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
            // Find other cell if lava
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

    // Modified getFirstWormInRange
    // Receive current worm + max range to detect enemies
    private Worm getFirstWormInRange(MyWorm current, int range) {
        // Use the range and current worm
        Set<String> cells = constructFireDirectionLines(current, range)
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

    private List<List<Cell>> constructFireDirectionLines(MyWorm current, int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {
                
                int coordinateX = current.position.x + (directionMultiplier * direction.x);
                int coordinateY = current.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(current.position.x, current.position.y, coordinateX, coordinateY) > range) {
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
