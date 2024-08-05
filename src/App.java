import javax.swing.*;

public class App {
    public static void main(String[] args) {

        int boardWidth = 600;
        int boardHeight = 600;

        JFrame frame = new JFrame("Black Jack");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BlackJack blackJack = new BlackJack(boardWidth, boardHeight);
        frame.add(blackJack);
        frame.pack();
    }
}
