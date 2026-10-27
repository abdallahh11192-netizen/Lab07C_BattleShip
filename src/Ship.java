/** A ship of a given size that tracks how many of its cells have been hit. */
public class Ship {
    private final int size;
    private int hits = 0;

    public Ship(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void registerHit() {
        if (hits < size) {
            hits++;
        }
    }

    public boolean isSunk() {
        return hits >= size;
    }
}
