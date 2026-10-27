/** Outcome of firing a missile at a cell. */
public enum ShotResult {
    MISS,        // nothing there
    HIT,         // hit a ship that is still afloat
    SUNK,        // final hit on a ship
    WIN,         // final hit on the final ship
    STRIKE,      // a miss that completed 5 misses in a row (strike added)
    LOSS,        // a miss that caused the 3rd strike
    ALREADY_FIRED // cell was already clicked (does not count as a move)
}
