import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class EscapaDeLaBestia extends JFrame implements ActionListener, KeyListener {
    private int playerX, playerY; // Posición del jugador
    private int keyX, keyY; // Posición de la llave
    private int doorX, doorY; // Posición de la puerta
    private int crucifixX, crucifixY; // Posición del crucifijo en nivel 1
    private boolean hasKey = false;
    private boolean hasCrucifix = false;
    private int level = 0; // Nivel actual
    private boolean beastActive = false;
    private int beastX, beastY; // Posición de la bestia
    private boolean beastFrozen = false;
    private long freezeEndTime = 0;
    private int crucifixUses = 3; // Usos restantes del crucifijo

    private boolean moveUp, moveDown, moveLeft, moveRight;
    private GamePanel gamePanel;
    private boolean gameWon = false;
    private boolean gameOver = false;

    // Dimensiones del área jugable
    private static final int AREA_WIDTH_0 = 1550; // Ancho del área jugable para nivel 0
    private static final int AREA_HEIGHT_0 = 300; // Alto del área jugable para nivel 0
    private static final int AREA_WIDTH_1 = 1550; // Ancho del área jugable para nivel 1
    private static final int AREA_HEIGHT_1 = 800; // Alto del área jugable para nivel 1

    private int areaX;
    private int areaY;

    public EscapaDeLaBestia() {
        setTitle("Escapa de la Bestia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900); // Tamaño de la ventana del juego
        setLocationRelativeTo(null);
        gamePanel = new GamePanel();
        add(gamePanel);
        setupLevel();

        Timer timer = new Timer(16, this);
        timer.start();

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(this);
    }

    private void setupLevel() {
        Random rand = new Random();
        areaX = (getWidth() - (level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1)) / 2;
        areaY = (getHeight() - (level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1)) / 2;

        if (level == 0) {
            // Nivel 0: Establecer posiciones para la llave, la puerta y el jugador
            keyX = areaX + AREA_WIDTH_0 - 35;
            keyY = areaY + (AREA_HEIGHT_0 / 2) - 7;
            doorX = areaX + AREA_WIDTH_0 - 35;
            doorY = areaY + (AREA_HEIGHT_0 / 2) + 10;
            playerX = areaX + 10;
            playerY = areaY + (AREA_HEIGHT_0 / 2) - 10;
        } else {
            // Nivel 1: Establecer posiciones para la llave, la puerta, la bestia y el crucifijo
            keyX = rand.nextInt(AREA_WIDTH_1 - 15) + areaX;
            keyY = rand.nextInt(AREA_HEIGHT_1 - 15) + areaY;
            doorX = rand.nextInt(AREA_WIDTH_1 - 15) + areaX;
            doorY = rand.nextInt(AREA_HEIGHT_1 - 15) + areaY;

            beastActive = true;
            beastX = rand.nextInt(AREA_WIDTH_1 - 30) + areaX;
            beastY = rand.nextInt(AREA_HEIGHT_1 - 30) + areaY;
            crucifixX = rand.nextInt(AREA_WIDTH_1 - 30) + areaX;
            crucifixY = rand.nextInt(AREA_HEIGHT_1 - 30) + areaY;
            hasCrucifix = false;

            // Resetear la llave para que el jugador tenga que recogerla de nuevo en el nivel 1
            hasKey = false;
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.drawRect(areaX, areaY, level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1, level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1);

            g.setColor(Color.WHITE);
            g.fillRect(playerX, playerY, 20, 20);

            g.setColor(Color.YELLOW);
            g.fillRect(keyX, keyY, 15, 15);

            g.setColor(new Color(139, 69, 19));
            g.fillRect(doorX, doorY, 15, 15);

            if (beastActive) {
                g.setColor(Color.RED);
                g.fillRect(beastX, beastY, 30, 30);
            }

            if (level == 1 && !hasCrucifix) {
                g.setColor(Color.CYAN);
                g.fillRect(crucifixX, crucifixY, 15, 15);
            }

            if (beastFrozen) {
                g.setColor(Color.WHITE);
                g.drawString("Bestia congelada!", 350, 50);
            }

            if (gameWon) {
                g.setColor(Color.GREEN);
                g.drawString("¡Has ganado!", 350, 300);
            }

            if (hasCrucifix && crucifixUses > 0) {
                g.setColor(Color.WHITE);
                g.drawString("Usos de crucifijo: " + crucifixUses, 10, 50);
            }

            if (gameOver) {
                g.setColor(Color.RED);
                g.drawString("¡La bestia te ha atrapado!", 350, 300);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameWon && !gameOver) {
            if (moveUp && playerY > areaY) playerY -= 4;
            if (moveDown && playerY < areaY + (level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1) - 20) playerY += 4;
            if (moveLeft && playerX > areaX) playerX -= 4;
            if (moveRight && playerX < areaX + (level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1) - 20) playerX += 4;

            if (beastActive && !beastFrozen) {
                if (playerX < beastX) beastX -= 2;
                if (playerX > beastX) beastX += 2;
                if (playerY < beastY) beastY -= 2;
                if (playerY > beastY) beastY += 2;
            }

            if (beastFrozen && System.currentTimeMillis() >= freezeEndTime) {
                beastFrozen = false;
            }

            // Detectar si el jugador colisiona con la bestia
            if (!beastFrozen && playerX < beastX + 30 && playerX + 20 > beastX && playerY < beastY + 30 && playerY + 20 > beastY) {
                gameOver = true;
            }

            // Si el jugador recoge la llave
            if (playerX < keyX + 15 && playerX + 20 > keyX && playerY < keyY + 15 && playerY + 20 > keyY) {
                hasKey = true;
                keyX = -100; // Mover la llave fuera de la pantalla
            }

            // Si el jugador recoge el crucifijo
            if (level == 1 && !hasCrucifix && playerX < crucifixX + 15 && playerX + 20 > crucifixX && playerY < crucifixY + 15 && playerY + 20 > crucifixY) {
                hasCrucifix = true;
                crucifixX = -100; // Mover el crucifijo fuera de la pantalla
            }

            // Comprobar si el jugador pasa a la siguiente fase o gana
            if (level == 0 && hasKey && playerX < doorX + 15 && playerX + 20 > doorX && playerY < doorY + 15 && playerY + 20 > doorY) {
                level = 1;
                setupLevel();
            }

            if (level == 1 && hasKey && playerX < doorX + 15 && playerX + 20 > doorX && playerY < doorY + 15 && playerY + 20 > doorY) {
                gameWon = true;
            }

            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveUp = true; break;
            case KeyEvent.VK_S: moveDown = true; break;
            case KeyEvent.VK_A: moveLeft = true; break;
            case KeyEvent.VK_D: moveRight = true; break;
            case KeyEvent.VK_Q:
                if (level == 1 && hasCrucifix && crucifixUses > 0) {
                    beastFrozen = true;
                    freezeEndTime = System.currentTimeMillis() + 5000;
                    crucifixUses--;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveUp = false; break;
            case KeyEvent.VK_S: moveDown = false; break;
            case KeyEvent.VK_A: moveLeft = false; break;
            case KeyEvent.VK_D: moveRight = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EscapaDeLaBestia game = new EscapaDeLaBestia();
            game.setVisible(true);
        });
    }
}