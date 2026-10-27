/** One square of the board. Knows whether a ship is under it and what the player sees. */
public class Cell {
    private Ship ship = null;              // null means open water
    private CellState state = CellState.BLANK;

    public boolean hasShip() {
        return ship != null;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public boolean isFired() {
        return state != CellState.BLANK;
    }
}
