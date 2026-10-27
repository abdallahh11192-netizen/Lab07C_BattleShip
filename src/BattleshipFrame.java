import javax.swing.*;

/** The application window. All of the game UI lives in BattleshipPanel. */
public class BattleshipFrame extends JFrame {
    public BattleshipFrame() {
        super("Battleship");
        BattleshipPanel panel = new BattleshipPanel();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { panel.quitPressed(); }
        });
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
