import javax.swing.SwingUtilities;

/** Entry point for Lab07C_BattleShip. */
public class BattleshipRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipFrame().setVisible(true));
    }
}
