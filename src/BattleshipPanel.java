import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/** The Battleship GUI: title, 10x10 grid of JButtons, status counters, Play Again and Quit. */
public class BattleshipPanel extends JPanel {
    private static final int CELL_PX = 44;

    private final Board board = new Board();
    private final ScoreKeeper score = new ScoreKeeper();
    private final JButton[][] buttons = new JButton[Board.SIZE][Board.SIZE];

    private final ImageIcon blankIcon = makeIcon(new Color(173, 216, 230), "~", new Color(70, 130, 180));
    private final ImageIcon missIcon  = makeIcon(new Color(173, 216, 230), "M", new Color(235, 200, 0));
    private final ImageIcon hitIcon   = makeIcon(new Color(173, 216, 230), "X", Color.RED);

    private final JLabel missLabel = new JLabel();
    private final JLabel strikeLabel = new JLabel();
    private final JLabel totalMissLabel = new JLabel();
    private final JLabel totalHitLabel = new JLabel();

    public BattleshipPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel title = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        add(buildGrid(), BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(2, 1, 4, 4));
        south.add(buildStatusPanel());
        south.add(buildControlPanel());
        add(south, BorderLayout.SOUTH);

        newGame();
    }

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(Board.SIZE, Board.SIZE, 1, 1));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                JButton b = new JButton(blankIcon);
                b.setPreferredSize(new Dimension(CELL_PX, CELL_PX));
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setFocusPainted(false);
                final int row = r, col = c;
                b.addActionListener((ActionEvent e) -> cellClicked(row, col));
                buttons[r][c] = b;
                grid.add(b);
            }
        }
        return grid;
    }

    private JPanel buildStatusPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 2));
        p.setBorder(BorderFactory.createTitledBorder("Status"));
        p.add(missLabel);
        p.add(strikeLabel);
        p.add(totalMissLabel);
        p.add(totalHitLabel);
        return p;
    }

    private JPanel buildControlPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        JButton again = new JButton("Play Again");
        JButton quit = new JButton("Quit");
        again.addActionListener(e -> playAgainPressed());
        quit.addActionListener(e -> quitPressed());
        p.add(again);
        p.add(quit);
        return p;
    }

    /** Resets the model and the view for a brand new game. */
    private void newGame() {
        board.reset();
        score.reset();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                setCellLook(buttons[r][c], CellState.BLANK);
                buttons[r][c].setEnabled(true);
            }
        }
        updateStatus();
    }

    private void cellClicked(int row, int col) {
        JButton b = buttons[row][col];
        ShotResult result = board.fire(row, col);
        if (result == ShotResult.ALREADY_FIRED) {
            return;                                   // does not count as a move
        }
        b.setEnabled(false);                          // can't be clicked again this game

        if (result == ShotResult.MISS) {
            setCellLook(b, CellState.MISS);
            score.recordMiss();
            updateStatus();
            if (score.isOutOfStrikes()) {
                disableAll();
                askReplay("You got 3 strikes. You LOSE!");
            }
        } else {                                      // HIT, SUNK or WIN
            setCellLook(b, CellState.HIT);
            score.recordHit();
            updateStatus();
            if (result == ShotResult.WIN) {
                disableAll();
                askReplay("You sunk the final ship. You WIN!");
            } else if (result == ShotResult.SUNK) {
                inform("You sunk a ship!", "Sunk!");
            }
        }
    }

    private void setCellLook(JButton b, CellState s) {
        ImageIcon icon = s == CellState.HIT ? hitIcon : s == CellState.MISS ? missIcon : blankIcon;
        b.setIcon(icon);
        b.setDisabledIcon(icon);                      // keep the colors when disabled
    }

    private void disableAll() {
        for (JButton[] row : buttons) {
            for (JButton b : row) {
                b.setEnabled(false);
            }
        }
    }

    private void updateStatus() {
        missLabel.setText("Misses in a row: " + score.getMissCount() + " / " + ScoreKeeper.MAX_MISSES_IN_ROW);
        strikeLabel.setText("Strikes: " + score.getStrikeCount() + " / " + ScoreKeeper.MAX_STRIKES);
        totalMissLabel.setText("Total misses: " + score.getTotalMissCount());
        totalHitLabel.setText("Total hits: " + score.getTotalHitCount() + " / 17");
    }

    /** Game over: show the message and ask whether to play again. */
    private void askReplay(String message) {
        if (confirm(message + "\nPlay again?", "Game Over")) {
            newGame();
        } else {
            System.exit(0);
        }
    }

    private void playAgainPressed() {
        if (confirm("Are you sure you want to start a new game?", "Play Again")) {
            newGame();
        }                                             // NO: continue the current game
    }

    public void quitPressed() {
        if (confirm("Do you want to play again instead of quitting?", "Quit")) {
            newGame();
        } else {
            System.exit(0);
        }
    }

    /** Yes/No dialog. Returns true for Yes. */
    protected boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Plain information dialog. */
    protected void inform(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Draws a colored square with one centered character, used as the cell image. */
    private static ImageIcon makeIcon(Color bg, String text, Color fg) {
        BufferedImage img = new BufferedImage(CELL_PX, CELL_PX, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, CELL_PX, CELL_PX);
        g.setColor(fg);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (CELL_PX - fm.stringWidth(text)) / 2, (CELL_PX + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
        return new ImageIcon(img);
    }
}
