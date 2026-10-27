import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** The 10x10 game board. Places the ships randomly and resolves shots. */
public class Board {
    public static final int SIZE = 10;
    public static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};   // descending order is more efficient

    private final Cell[][] cells = new Cell[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();
    private final Random rand = new Random();

    public Board() {
        reset();
    }

    /** Clears the board and randomly places all five ships. */
    public void reset() {
        ships.clear();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = new Cell();
            }
        }
        for (int size : SHIP_SIZES) {
            placeShip(new Ship(size));
        }
    }

    /**
     * Scans every row and column for a run of open cells long enough for the ship,
     * collects all the legal placements, then randomly picks one of them.
     */
    private void placeShip(Ship ship) {
        List<int[]> options = new ArrayList<>();   // {row, col, horizontal(1)/vertical(0)}
        int len = ship.getSize();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (fits(r, c, len, true)) {
                    options.add(new int[]{r, c, 1});
                }
                if (fits(r, c, len, false)) {
                    options.add(new int[]{r, c, 0});
                }
            }
        }

        int[] pick = options.get(rand.nextInt(options.size()));
        for (int i = 0; i < len; i++) {
            int r = pick[0] + (pick[2] == 0 ? i : 0);
            int c = pick[1] + (pick[2] == 1 ? i : 0);
            cells[r][c].setShip(ship);
        }
        ships.add(ship);
    }

    /** True if len consecutive cells starting at (row,col) are on the board and have no ship (ships may touch, not overlap). */
    private boolean fits(int row, int col, int len, boolean horizontal) {
        for (int i = 0; i < len; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= SIZE || c >= SIZE || cells[r][c].hasShip()) {
                return false;
            }
        }
        return true;
    }

    /** Fires at a cell. Returns HIT, SUNK, WIN, MISS, or ALREADY_FIRED. */
    public ShotResult fire(int row, int col) {
        Cell cell = cells[row][col];
        if (cell.isFired()) {
            return ShotResult.ALREADY_FIRED;
        }
        if (!cell.hasShip()) {
            cell.setState(CellState.MISS);
            return ShotResult.MISS;
        }
        cell.setState(CellState.HIT);
        cell.getShip().registerHit();
        if (!cell.getShip().isSunk()) {
            return ShotResult.HIT;
        }
        return allShipsSunk() ? ShotResult.WIN : ShotResult.SUNK;
    }

    public boolean allShipsSunk() {
        for (Ship s : ships) {
            if (!s.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public CellState getState(int row, int col) {
        return cells[row][col].getState();
    }

    /** Test/debug helper: is there a ship under this cell? */
    public boolean hasShipAt(int row, int col) {
        return cells[row][col].hasShip();
    }
}
