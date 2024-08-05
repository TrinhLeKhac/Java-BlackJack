import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class BlackJack extends JPanel implements ActionListener{

    // *********************************** attribute of class *********************************************

    // Card
    private static class Card {
        String value;
        String type;
        public Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;  //  A
                }
                return 10;  //  J, Q, K
            }
            return Integer.parseInt(value);  // 2 - 10
        }

        boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + this.toString() + ".png";
        }
    }

    // deck
    ArrayList<Card> deck;

    // shuffle deck
    Random random = new Random();

    // dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // window
    int boardWidth = 600;
    int boardHeight = 600;

    // component
    JPanel buttonPanel;
    JButton hitButton;
    JButton stayButton;
    JButton clearButton;

    // card
    int cardWidth = 110;
    int cardHeight = 154;

    // *********************************** end of attribute of class *********************************************


    // *********************************** constructor *********************************************

    public BlackJack(int boardWidth, int boardHeight) {

        // JPanel configuration
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(53, 101, 77));
        this.setFocusable(true);

        // control components
        buttonPanel = new JPanel();
        hitButton = new JButton("Hit");
        stayButton = new JButton("Stay");
        clearButton = new JButton("Clear");

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);

        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);

        clearButton.setFocusable(false);
        buttonPanel.add(clearButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // start
        startGame();

        // Handle event
        hitButton.addActionListener(this);
        stayButton.addActionListener(this);
        clearButton.addActionListener(this);
    }

    // *********************************** end of constructor *********************************************


    // *********************************** paint component *********************************************

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            draw(g);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics g) throws IOException {

        // draw hidden card
        Image hiddenCardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./cards/BACK.png"))).getImage();
        if (!stayButton.isEnabled() && hiddenCard != null) {
            hiddenCardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource(hiddenCard.getImagePath()))).getImage();
        }
        g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

        Card tmpCard;
        Image tmpImage;
        // draw dealer's hand
        for (int i = 0; i < dealerHand.size(); i++) {
            tmpCard = dealerHand.get(i);
            tmpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(tmpCard.getImagePath()))).getImage();
            g.drawImage(tmpImage, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
        }

        // draw player's hand
        for (int i = 0; i < playerHand.size(); i++) {
            tmpCard = playerHand.get(i);
            tmpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(tmpCard.getImagePath()))).getImage();
            g.drawImage(tmpImage, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
        }

        // show message
        String message = getMessage();
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.setColor(Color.WHITE);
        g.drawString(message, 220, 250);

    }

    // *********************************** end of paint component *********************************************

    // *********************************** support methods *********************************************

    public void buildDeck() {
        deck = new ArrayList<>();

        String[] values = {"A", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String value : values) {
            for (String type : types) {
                deck.add(new Card(value, type));
            }
        }
        System.out.println("BUILD DECK: ");
        System.out.println(deck);
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currentCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currentCard);
        }
        System.out.println("AFTER SHUFFLE: ");
        System.out.println(deck);
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    public String getMessage() {

        String message = "";

        if (!stayButton.isEnabled()) {
            dealerSum = reduceDealerAce();
            playerSum = reducePlayerAce();
            System.out.println("STAY: ");
            System.out.println("DEALER score: " + dealerSum);
            System.out.println("PLAYER score: " + playerSum);

            if (playerSum > 21 && dealerSum > 21) {
                message = "Tie!";
            }
            else if (playerSum > 21) {
                message = "You lose!";
            } else if (dealerSum > 21) {
                message = "You win!";
            } else if (playerSum == dealerSum) {
                message = "Tie!";
            } else if (playerSum > dealerSum) {
                message = "You win!";
            } else {
                message = "You lose!";
            }
        }

        return message;
    }

    // *********************************** end of support methods *********************************************

    // *********************************** main method (logic of Black Jack) *********************************************
    public void startGame() {

        // deck
        buildDeck();
        shuffleDeck();

        // dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.removeLast();
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1: 0;

        Card dealerCard = deck.removeLast();
        dealerSum += dealerCard.getValue();
        dealerAceCount += dealerCard.isAce() ? 1: 0;
        dealerHand.add(dealerCard);

        System.out.println("DEALER: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        // player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            Card playerCard = deck.removeLast();
            playerSum += playerCard.getValue();
            playerAceCount += playerCard.isAce() ? 1: 0;
            playerHand.add(playerCard);
        }

        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

    }

    // *********************************** re-render by interval (loop game) *********************************************

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hitButton) {
            handleHitButton();
        } else if (e.getSource() == stayButton) {
            handleStayButton();
        } else if (e.getSource() == clearButton) {
            handleClearButton();
        }
    }

    public void handleHitButton() {

        Card card = deck.removeLast();
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1: 0;
        playerHand.add(card);

        // total score > 21, do not get more cards by disabled hitButton
        if (reducePlayerAce() > 21) {
            hitButton.setEnabled(false);
        }
        repaint();
    }

    public void handleStayButton() {

        hitButton.setEnabled(false);
        stayButton.setEnabled(false);

        while (dealerSum < 17) {
            Card card = deck.removeLast();
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1: 0;
            dealerHand.add(card);
        }
        repaint();
    }

    public void handleClearButton() {
        startGame();
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        repaint();
    }

    // *********************************** end of re-render *********************************************
}
