package za.co.entelect.challenge;

import javafx.geometry.Pos;
import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

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

    public Command run(){
        // Get enemy worms in:
        Worm enemyWorm = getFirstWormInRange(currentWorm, currentWorm.weapon.range, false); // Weapon Range
        Worm enemyBananaSnowball = getFirstWormInRange(currentWorm, 5, true); // Snowball and Banana Range

        if(onevone()){
            Worm aliveenemy = null;

            for(Worm worm: opponent.worms){
                if(worm.health > 0){
                    aliveenemy = worm;
                }
            }
            if(aliveenemy.health > currentWorm.health)
                return lonewolf();
        }

        else if(Alone()){
            return lonewolf();
        }

        if(enemyWorm == null && enemyBananaSnowball == null){
            return following();
        }

        // Check if there is any power up available
        if(getPowerupCell() != null){
            // Go to nearest power up cells if available
            Position PowerUp = getPowerupCell();
            Direction toPowerUp = resolveDirection(currentWorm.position, PowerUp);

            // Check if there are enemies found while going to the power up cells
            if(enemyWorm != null || enemyBananaSnowball != null){
                return AttackCommand(currentWorm, enemyWorm, enemyBananaSnowball);
            }
            return digAndMove(currentWorm, toPowerUp);
        }

        return AttackCommand(currentWorm, enemyWorm, enemyBananaSnowball);
    }

    // Attack Strategy
    public Command AttackCommand(MyWorm currentWorm, Worm enemyWorm, Worm enemyBananaSnowball){
        // Self-Defense another worm
        if (gameState.myPlayer.remainingWormSelections>0) {
            // Iterate over worms
            for (MyWorm another : gameState.myPlayer.worms) {
                // Find another alive worm
                if (another.id == gameState.currentWormId) continue;
                if (another.health > 0) {
                    // Get enemy worms in:
                    Worm enemy = getFirstWormInRange(another, another.weapon.range,false); // Weapon Range
                    Worm enemySpecial = getFirstWormInRange(another, 5,true); // Snowball and Banana Range

                    // Check if any enemy is in range
                    // Also check if health is > 0 to avoid shooting dead enemies
                    if((enemy != null || enemySpecial != null)){
                        // Get Distance
                        // Only need to get CW to ES
                        float distCWtoEBS=0;
                        Direction direction=null;
                        if (enemy!=null) {
                            distCWtoEBS = euclideanDistance(another.position.x, another.position.y, enemy.position.x, enemy.position.y);
                            // Resolve Direction
                            direction = resolveDirection(another.position, enemy.position);
                        }

                        // Do Attack Strategies
                        // Same intuitive with AttackCommand, but with Select Command
                        if (enemySpecial != null && another.id == 2 && another.bananas.count > 0) {
                            // Worm = Agent
                            // Can Banana
                            return new SelectCommand(another.id, null, enemySpecial.position.x, enemySpecial.position.y, false, true, false, false, false);
                        }
                        else if (enemySpecial != null && another.id == 3 && another.snowballs.count > 0) {
                            // Worm = Technologist
                            // Can Snowball

                            // Detect if any enemy's worm is frozen
                            boolean foundFrozen = false;
                            int i = 0;
                            while (i < 3 && !foundFrozen) {
                                if (opponent.worms[i].notFrozen != 0) {
                                    foundFrozen = true;
                                }
                                i++;
                            }
                            if (enemy != null && foundFrozen) {
                                return new SelectCommand(another.id, direction, -1, -1, false, false, false, false, true);
                            }
                            else {
                                return new SelectCommand(another.id, null, enemySpecial.position.x, enemySpecial.position.y, true, false, false, false, false);
                            }
                        }
                        // Ordinary Shoot Command with Select Command
                        // Check distance first so not out of range
                        else if(enemy != null && distCWtoEBS <= currentWorm.weapon.range){
                            // Worms = All
                            // Can't Banana or Snowball
                            return new SelectCommand(another.id, direction, -1, -1, false, false, false, false, true);
                        }
                    }
                }
            }
        }

        // Check if any enemy is in range
        // Also check if health is > 0 to avoid shooting dead enemies
        if((enemyWorm != null || enemyBananaSnowball != null)) {
            float distCWtoEBS = 0;
            Direction direction = null;
            if (enemyWorm!=null) {
                // Get Distance
                // Only need to get CW to ES
                distCWtoEBS = euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
                // Find direction between my worm and enemy's worm
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
            }
            // Shooting Strategy
            // Maximizing Damage and Utility
            // Check my worm id first before deciding
            // Also check inventory for special trained attacks -> Banana or Snowball
            if (enemyBananaSnowball != null && currentWorm.id == 2 && currentWorm.bananas.count > 0) {
                // Maximizing Damage
                // Worm = Agent
                // Can Use Banana
                return new BananaBomb(enemyBananaSnowball.position.x, enemyBananaSnowball.position.y);
            }
            else if (enemyBananaSnowball != null && currentWorm.id == 3 && currentWorm.snowballs.count > 0) {
                // Maximizing Utility
                // Worm = Technologist
                // Can Use Snowball

                // Detect if any enemy's worm is frozen
                boolean foundFrozen = false;
                int i = 0;
                while (i < 3 && !foundFrozen) {
                    if (opponent.worms[i].notFrozen != 0) {
                        foundFrozen = true;
                    }
                    i++;
                }
                if (enemyWorm != null && foundFrozen) {
                    // Maximizing Freeze Duration between each snowball used
                    // One or more enemy's worm is frozen
                    // Do ordinary shoot before using another snowball
                    return new ShootCommand(direction);
                }
                else {
                    // No enemy frozen -> If missed snowball or before finding any enemies
                    // Use snowball to freeze enemies
                    return new Snowball(enemyBananaSnowball.position.x, enemyBananaSnowball.position.y);
                }
            }
            // Get Distance
            // Only need to get CW to EBS
            else if(enemyWorm != null &&distCWtoEBS <= currentWorm.weapon.range){
                // Worm = Commando, Agent, Technologist
                // No Banana for Agent
                // No Snowball for Technologist
                // Still do shoot command as many as possible to maximize damage output
                return new ShootCommand(direction);
            }
        }
        return following();
    }

    // Last worm standing
    private Boolean Alone(){
        int count = 0;
        for(MyWorm worm: gameState.myPlayer.worms){
            if(worm.health > 0){
                count += 1;
            }
        }
        return count == 1;
    }

    // 1v1 Condition
    private Boolean onevone(){
        int count = 0;
        for(Worm worm: opponent.worms){
            if(worm.health > 0){
                count += 1;
            }
        }
        return count == 1 && Alone();
    }

    // Get Center Map
    private Direction getCenterMap(){
        List<Cell> AllSurroundingBlocks = getSurroundingCells(17,17);
        int cellIdx = random.nextInt(AllSurroundingBlocks.size());
        Cell randomCenterCell = AllSurroundingBlocks.get(cellIdx);
        int x = randomCenterCell.x;
        int y = randomCenterCell.y;

        Position CellPosition = new Position(x, y);

        Boolean occupied = false;
        for(Worm worm: opponent.worms){
            if(worm.position.x == CellPosition.x && worm.position.y == CellPosition.y && !occupied){
                occupied = true;
            }
        }

        if(occupied){
            return getCenterMap();
        }
        return resolveDirection(currentWorm.position, CellPosition);
    }

    // Endgame mechanism
    public Command lonewolf(){
        while(true) {
            Direction CellDirection = getCenterMap();
            return digAndMove(currentWorm, CellDirection);
        }
    }

    // Function to follow another worm
    public Command following(){
        Worm commando = findCommando();
        Worm enemies = getClosestEnemies(currentWorm);
        if(commando != null && currentWorm.id != 1){ // If the commando worm is alive, the others will follow the commando
            Direction commandoDirection = resolveDirection(currentWorm.position, commando.position);
            return digAndMove(currentWorm, commandoDirection);
        }
        else if(commando == null && currentWorm.id != 1){ // If the commando worm is dead
            Worm friends = getClosestFriend(currentWorm);
            Direction friendDirection = resolveDirection(currentWorm.position, friends.position);
            return digAndMove(currentWorm, friendDirection);
        }
        else if(Alone()){
            return lonewolf();
        }
        else { // Commando strategy
            Direction huntDirection = resolveDirection(currentWorm.position, enemies.position);
            return digAndMove(currentWorm, huntDirection);
        }
    }

    // Function to follow Commando worm
    private Worm findCommando(){
        Worm commando= null;

        for(Worm friend : gameState.myPlayer.worms){
            if((friend.id == 1) && (friend.health > 0)){
                commando = friend;
            }
        }
        return commando;
    }

    // Function to get closest worm friends
    private Worm getClosestFriend(Worm currentWorm){
        // Init
        float currRange = 99999;
        float range;
        Worm nearestFriend = null;

        // Check and Assign
        for(Worm friend : gameState.myPlayer.worms){
            if((friend.health > 0) && (friend.id != currentWorm.id)){
                range = euclideanDistance(currentWorm.position.x, currentWorm.position.y, friend.position.x, friend.position.y);
                if(range <= currRange){
                    nearestFriend = friend;
                    currRange = range;
                }
            }
        }
        return nearestFriend;
    }

    // Function to get closest enemy worm
    private Worm getClosestEnemies(Worm currentWorm){
        // Init
        float minRange = 99999;
        float Range;
        Worm result = opponent.worms[0];

        // Check and Assign
        for(Worm enemies : opponent.worms){
            Range = euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemies.position.x, enemies.position.y);
            if(Range <= minRange && enemies.health > 0){
                minRange = Range;
                result = enemies;
            }
        }
        return result;
    }

    // Function to get Power Up Cell
    private Position getPowerupCell(){
        // Init ArrayList of Cell
        ArrayList<Cell> cells = new ArrayList<>();

        // Add cells in map
        for (int i = 0; i < gameState.mapSize; i++) {
            for (int j = 0; j < gameState.mapSize; j++) {
                // Don't include the current position
                if (isValidCoordinate(i, j)) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }

        // Random Init
        Cell pospUp = cells.get(0);

        // Get filtered cells containing Power Up
        // Else return null
        List<Cell> ListCellpUp = cells.stream().filter(cell -> cell.powerUp != null).collect(Collectors.toList());

        // Check to determine return
        float min = 99999;
        if(ListCellpUp.size() > 0) {
            for (Cell cell : ListCellpUp) {
                float range = euclideanDistance(currentWorm.position.x, currentWorm.position.y, cell.x, cell.y);
                if (range <= min) {
                    min = range;
                    pospUp = cell;
                }
                // Find power up cell with minimum distance
                // Return position of that power up cell
                return new Position(pospUp.x, pospUp.y);
            }
        }
        return null;
    }

    // Move Intuition
    public Command digAndMove(Worm currentWorm, Direction dir){
        // New Coordinate
        int newX = currentWorm.position.x + dir.x;
        int newY = currentWorm.position.y + dir.y;

        // Surrounding Cells
        List<Cell> AllSurroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        int cellIdx = random.nextInt(AllSurroundingBlocks.size());

        // Random Init
        Cell block = AllSurroundingBlocks.get(cellIdx);

        // Find a block with same coordinate to check
        for(Cell Blocks: AllSurroundingBlocks){
            if(Blocks.x == newX && Blocks.y == newY){
                block = Blocks;
            }
        }

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
            // Find other random surrounding cell if deep space
            Cell blockNotDS = AllSurroundingBlocks.get(cellIdx);
            if(blockNotDS.type != CellType.DEEP_SPACE){
                if(blockNotDS.type != CellType.DIRT || blockNotDS.type == CellType.AIR){
                    return new MoveCommand(blockNotDS.x, blockNotDS.y);
                }
                else if(blockNotDS.type == CellType.DIRT){
                    return new DigCommand(blockNotDS.x, blockNotDS.y);
                }
            }
        }

        // Find other random surrounding cell if lava
        Cell block1 = AllSurroundingBlocks.get(cellIdx);
        if(block1.type == CellType.LAVA) {
            Cell block2 = AllSurroundingBlocks.get(cellIdx);
            if (block2.type == CellType.AIR) {
                return new MoveCommand(block2.x, block2.y);
            } else if (block2.type == CellType.DIRT) {
                return new DigCommand(block2.x, block2.y);
            }
        }
        return new MoveCommand(block1.x, block1.y);
    }

    // Modified getFirstWormInRange
    // Receive current worm + max range to detect enemies
    private Worm getFirstWormInRange(MyWorm current, int range, Boolean specialMove) {
        // Use the range and current worm
        Set<String> cells = constructFireDirectionLines(current, range, specialMove)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition)) {
                // Check enemy health
                // Should be alive enemy
                if(enemyWorm.health > 0){
                    return enemyWorm;
                }
            }
        }

        return null;
    }

    private List<List<Cell>> constructFireDirectionLines(MyWorm current, int range, Boolean specialMove) {
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
                if (specialMove) {
                    if (!(cell.type == CellType.AIR|| cell.type == CellType.DIRT)) {
                        break;
                    }
                }
                else {
                    if (cell.type != CellType.AIR) {
                        break;
                    }
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

    // Modified return type to float to get better precision
    private float euclideanDistance(int aX, int aY, int bX, int bY) {
        return (float) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
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
