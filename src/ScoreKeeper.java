/** Holds the four counters and applies the MISS / STRIKE / HIT rules. */
public class ScoreKeeper {
    public static final int MAX_MISSES_IN_ROW = 5;
    public static final int MAX_STRIKES = 3;

    private int missCount;       // misses in a row [0-5]
    private int strikeCount;     // [0-3]
    private int totalMissCount;  // [0-83]
    private int totalHitCount;   // [0-17]

    public ScoreKeeper() {
        reset();
    }

    public void reset() {
        missCount = 0;
        strikeCount = 0;
        totalMissCount = 0;
        totalHitCount = 0;
    }

    /** A hit bumps the total hit counter and clears the misses-in-a-row counter. */
    public void recordHit() {
        totalHitCount++;
        missCount = 0;
    }

    /**
     * A miss bumps the miss and total miss counters. On the 5th miss in a row a strike is
     * added and the miss counter goes back to zero.
     * @return true if this miss produced a strike
     */
    public boolean recordMiss() {
        missCount++;
        totalMissCount++;
        if (missCount >= MAX_MISSES_IN_ROW) {
            strikeCount++;
            missCount = 0;
            return true;
        }
        return false;
    }

    public boolean isOutOfStrikes() {
        return strikeCount >= MAX_STRIKES;
    }

    public int getMissCount()      { return missCount; }
    public int getStrikeCount()    { return strikeCount; }
    public int getTotalMissCount() { return totalMissCount; }
    public int getTotalHitCount()  { return totalHitCount; }
}
