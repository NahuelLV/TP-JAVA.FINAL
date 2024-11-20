package bestia;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
    private boolean gameOver = false; // Para saber si el juego ha terminado

    private boolean moveUp, moveDown, moveLeft, moveRight;
    private GamePanel gamePanel;
    private boolean gameWon = false;

    // Paredes del nivel 1
    private ArrayList<Rectangle> walls = new ArrayList<>();

    // Definir límites del área jugable
    private static final int AREA_WIDTH_0 = 800; // Ancho del área jugable para nivel 0
    private static final int AREA_HEIGHT_0 = 200; // Alto del área jugable para nivel 0
    private static final int AREA_HEIGHT = 540; // Alto del área jugable para nivel 1
    private static final int AREA_WIDTH_1 = 740; // Ancho del área jugable para nivel 1

    // Calcular posición del área jugable en el centro de la pantalla
    private int areaX;
    private int areaY;

    public EscapaDeLaBestia() {
        setTitle("Escapa de la Bestia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700); // Tamaño de la ventana aumentado
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        gamePanel = new GamePanel();
        add(gamePanel);
        setupLevel();

        Timer timer = new Timer(16, this); // Temporizador para la lógica del juego
        timer.start();

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(this);
    }

    private void setupLevel() {
        Random rand = new Random();
        // Calcular posición del área jugable en el centro de la pantalla
        areaX = (getWidth() - (level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1)) / 2;
        areaY = (getHeight() - (level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT)) / 2;

        // Limpiar las paredes de la lista
        walls.clear();

        // Asignar posiciones para la llave y la puerta en el nivel 0
        if (level == 0) {
            keyX = areaX + AREA_WIDTH_0 - 35; // Llave en la parte derecha
            keyY = areaY + (AREA_HEIGHT_0 / 2) - 7; // Centro vertical
            doorX = areaX + AREA_WIDTH_0 - 35; // Puerta en la parte derecha
            doorY = areaY + (AREA_HEIGHT_0 / 2) + 10; // Justo debajo de la llave

            // Reaparecer el jugador en la parte izquierda del área jugable
            playerX = areaX + 10; // Posición inicial del jugador
            playerY = areaY + (AREA_HEIGHT_0 / 2) - 10; // Centro vertical
        } else {
            // Generar posiciones aleatorias para la llave y la puerta en el nivel 1
            keyX = rand.nextInt(AREA_WIDTH_1 - 15) + areaX;
            keyY = rand.nextInt(AREA_HEIGHT - 15) + areaY;
            doorX = rand.nextInt(AREA_WIDTH_1 - 15) + areaX;
            doorY = rand.nextInt(AREA_HEIGHT - 15) + areaY;

            // Reasignar posiciones de la bestia y el crucifijo
            beastActive = true; // Activar la bestia en el nivel 1
            beastX = rand.nextInt(AREA_WIDTH_1 - 30) + areaX; // Posición aleatoria dentro del área
            beastY = rand.nextInt(AREA_HEIGHT - 30) + areaY; // Posición aleatoria dentro del área
            crucifixX = rand.nextInt(AREA_WIDTH_1 - 30) + areaX; // Posición aleatoria para el crucifijo
            crucifixY = rand.nextInt(AREA_HEIGHT - 30) + areaY; // Posición aleatoria para el crucifijo
            hasCrucifix = false; // Asegurarse de que el crucifijo se recoja solo en el nivel 1

            // Agregar paredes en el nivel 1
            walls.add(new Rectangle(areaX + 200, areaY + 100, 20, 200)); // Pared 1
            walls.add(new Rectangle(areaX + 400, areaY + 200, 20, 200)); // Pared 2
            walls.add(new Rectangle(areaX + 600, areaY + 300, 20, 200)); // Pared 3
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight()); // Fondo negro

            // Dibujar borde alrededor del área jugable
            g.setColor(Color.WHITE); // Color del borde
            g.drawRect(areaX, areaY, level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1, level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT); // Área jugable

            // Dibujar el jugador
            g.setColor(Color.WHITE);
            g.fillRect(playerX, playerY, 20, 20);

            // Dibujar la llave
            g.setColor(Color.YELLOW);
            g.fillRect(keyX, keyY, 15, 15);

            // Dibujar la puerta
            g.setColor(new Color(139, 69, 19)); // Marrón
            g.fillRect(doorX, doorY, 15, 15);

            // Dibujar la bestia en el nivel 1
            if (beastActive) {
                g.setColor(Color.BLUE);
                g.fillRect(beastX, beastY, 30, 30);
            }

            // Dibujar el crucifijo en el nivel 1
            if (level == 1 && !hasCrucifix) {
                g.setColor(Color.CYAN); // Celeste para el crucifijo
                g.fillRect(crucifixX, crucifixY, 15, 15);
            }

            // Dibujar las paredes en el nivel 1
            if (level == 1) {
                g.setColor(Color.GRAY);
                for (Rectangle wall : walls) {
                    g.fillRect(wall.x, wall.y, wall.width, wall.height);
                }
            }

            // Mensaje si la bestia está congelada
            if (beastFrozen) {
                g.setColor(Color.WHITE);
                g.drawString("Bestia congelada!", 350, 50);
            }

            // Mensaje de victoria
            if (gameWon) {
                g.setColor(Color.GREEN);
                g.drawString("¡Has ganado!", 350, 300);
            }

            // Mensaje de derrota
            if (gameOver) {
                g.setColor(Color.RED);
                g.drawString("¡Perdiste! La bestia te atrapó!", 350, 300);
            }

            // Mostrar usos restantes del crucifijo si lo ha recogido y aún tiene usos
            if (hasCrucifix && crucifixUses > 0) {
                g.setColor(Color.WHITE);
                g.drawString("Usos de crucifijo: " + crucifixUses, 10, 50);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameWon && !gameOver) {
            // Movimiento del jugador
            if (moveUp && playerY > areaY) playerY -= 4;
            if (moveDown && playerY < areaY + (level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT) - 20) playerY += 4;
            if (moveLeft && playerX > areaX) playerX -= 4;
            if (moveRight && playerX < areaX + (level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1) - 20) playerX += 4;

            // Comprobar si el jugador colisiona con las paredes
            if (level == 1) {
                for (Rectangle wall : walls) {
                    if (wall.intersects(new Rectangle(playerX, playerY, 20, 20))) {
                        // Si el jugador toca una pared, no se mueve
                        if (moveUp) playerY += 4;
                        if (moveDown) playerY -= 4;
                        if (moveLeft) playerX += 4;
                        if (moveRight) playerX -= 4;
                    }
                }
            }

            // Movimiento de la bestia
            if (beastActive && !beastFrozen) {
                if (playerX < beastX) beastX -= 2;
                if (playerX > beastX) beastX += 2;
                if (playerY < beastY) beastY -= 2;
                if (playerY > beastY) beastY += 2;
            }

            // Comprobar si la bestia atrapa al jugador
            if (Math.abs(playerX - beastX) < 20 && Math.abs(playerY - beastY) < 20) {
                gameOver = true; // Fin del juego si la bestia toca al jugador
            }

            // Congelación de la bestia
            if (beastFrozen && System.currentTimeMillis() >= freezeEndTime) {
                beastFrozen = false;
            }

            // Comprobar si el jugador recoge la llave
            if (playerX < keyX + 15 && playerX + 20 > keyX && playerY < keyY + 15 && playerY + 20 > keyY) {
                hasKey = true; // Recoger la llave
                keyX = -100; // Sacar la llave de la pantalla
            }

            // Comprobar si el jugador recoge el crucifijo
            if (level == 1 && playerX < crucifixX + 15 && playerX + 20 > crucifixX && playerY < crucifixY + 15 && playerY + 20 > crucifixY) {
                hasCrucifix = true; // Recoger el crucifijo
                crucifixX = -100; // Sacar el crucifijo de la pantalla
            }

            // Comprobar si el jugador puede pasar la puerta
            if (level == 0 && hasKey && playerX < doorX + 15 && playerX + 20 > doorX && playerY < doorY + 15 && playerY + 20 > doorY) {
                level = 1; // Cambiar al nivel 1
                setupLevel(); // Configurar el nuevo nivel
            }
            if (level == 1 && playerX < doorX + 15 && playerX + 20 > doorX && playerY < doorY + 15 && playerY + 20 > doorY) {
                gameWon = true; // Ganar el juego
            }

            gamePanel.repaint(); // Actualizar la pantalla
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveUp = true; break;
            case KeyEvent.VK_S: moveDown = true; break;
            case KeyEvent.VK_A: moveLeft = true; break;
            case KeyEvent.VK_D: moveRight = true; break;
            case KeyEvent.VK_Q: // Usar el crucifijo
                if (hasCrucifix && crucifixUses > 0) {
                    beastFrozen = true; // Congelar la bestia
                    freezeEndTime = System.currentTimeMillis() + 10000; // Congela la bestia por 10 segundos
                    crucifixUses--; // Disminuir uso del crucifijo
                    if (crucifixUses == 0) {
                        hasCrucifix = false; // Quitar el crucifijo si no quedan usos
                    }
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
    public void keyTyped(KeyEvent e) {
        // No se necesita implementar
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EscapaDeLaBestia game = new EscapaDeLaBestia();
            game.setVisible(true);
        });
    }
}
