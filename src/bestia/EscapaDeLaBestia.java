package bestia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.sound.sampled.*;

public class EscapaDeLaBestia extends JFrame implements ActionListener, KeyListener {
    private int playerX, playerY; // Posición del jugador
    private int playerDX, playerDY;
    private int keyX, keyY; // Posición de la llave
    private int doorX, doorY; // Posición de la puerta
    private int crucifixX, crucifixY; // Posición del crucifijo en nivel 1
    private boolean hasKey = false;
    private boolean hasCrucifix = false;
    private int level = 0; // Nivel actual
    private boolean beastActive = false;
    private int beastX, beastY; // Posición de la bestia
    private int beastWX, beastWY;
    private boolean beastFrozen = false;
    private long freezeEndTime = 0;
    private int crucifixUses = 1; // Usos restantes del crucifijo
    private int winx, winy;
    private int losex, losey;

    private boolean moveUp, moveDown, moveLeft, moveRight;
    private GamePanel gamePanel;
    private boolean gameWon = false;
    private boolean gameOver = false;

    // Variables para las imágenes
    private Image playerImage;
    private Image playerDeathImage;
    private Image keyImage;
    private Image doorImage;
    private Image beastImage;
    private Image beastImageW;
    private Image crucifixImage;
    private Image backgroundImageLevel0;
    private Image backgroundImageLevel1;
    private Image win;
    private Image lose;

    // Sonidos
    private Clip keySound;
    private Clip crucifixSound;
    private Clip doorSound;
    private Clip beastSound;
    private Clip beastAttackSound;
    private Clip musica0;
    private Clip musica1;
    private Clip wolfwon;

    // Dimensiones del área jugable
    private static final int AREA_WIDTH_0 = 1550;
    private static final int AREA_HEIGHT_0 = 800;
    private static final int AREA_WIDTH_1 = 1550;
    private static final int AREA_HEIGHT_1 = 800;

    private int areaX = 25;
    private int areaY = 25;

    public EscapaDeLaBestia() {
        setTitle("Escapa de la Bestia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900); // Tamaño de la ventana del juego
        setLocationRelativeTo(null);
        gamePanel = new GamePanel();
        add(gamePanel);

        // Mostrar el menú principal
        mostrarMenu();

        Timer timer = new Timer(16, this);
        timer.start();

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(this);

        // Cargar las texturas
        try {
            playerImage = ImageIO.read(new File("resources/player.png"));
            keyImage = ImageIO.read(new File("resources/key.png"));
            doorImage = ImageIO.read(new File("resources/door.png"));
            beastImage = ImageIO.read(new File("resources/beast.png"));
            beastImageW = ImageIO.read(new File("resources/beastwon.png"));
            crucifixImage = ImageIO.read(new File("resources/crucifix.png"));
            playerDeathImage = ImageIO.read(new File("resources/deathplayer.png"));
            backgroundImageLevel0 = ImageIO.read(new File("resources/background.png"));
            backgroundImageLevel1 = ImageIO.read(new File("resources/background1.png"));
            win = ImageIO.read(new File("resources/winmessage.png"));
            lose = ImageIO.read(new File("resources/losemessage.png"));

            // Cargar sonidos
            keySound = AudioSystem.getClip();
            keySound.open(AudioSystem.getAudioInputStream(new File("resources/keySound.wav")));
            crucifixSound = AudioSystem.getClip();
            crucifixSound.open(AudioSystem.getAudioInputStream(new File("resources/crucifixSound.wav")));
            doorSound = AudioSystem.getClip();
            doorSound.open(AudioSystem.getAudioInputStream(new File("resources/doorSound.wav")));
            beastSound = AudioSystem.getClip();
            beastSound.open(AudioSystem.getAudioInputStream(new File("resources/beastSound.wav")));
            beastAttackSound = AudioSystem.getClip();
            beastAttackSound.open(AudioSystem.getAudioInputStream(new File("resources/beastAttackSound.wav")));
            musica0 = AudioSystem.getClip();
            musica0.open(AudioSystem.getAudioInputStream(new File("resources/musica0.wav")));
            musica1 = AudioSystem.getClip();
            musica1.open(AudioSystem.getAudioInputStream(new File("resources/musica1.wav")));
            wolfwon = AudioSystem.getClip();
            wolfwon.open(AudioSystem.getAudioInputStream(new File("resources/wolfwon.wav")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMenu() {
        while (true) {
            String[] opciones = {"1. Iniciar Juego", "2. Ver Historia", "3. Cómo Jugar", "4. Salir"};
            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Selecciona una opción:",
                    "Menú Principal",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) {
                System.exit(0);
            }

            switch (seleccion) {
                case "1. Iniciar Juego":
                    setupLevel();
                    return; // Salir del menú e iniciar el juego
                case "2. Ver Historia":
                    JOptionPane.showMessageDialog(null, "Frank Murdock es un periodista noruego de 25 años que esta viviendo en Boznia y Herzegovina, Sarajevo,\n" +
                            "en 1994, donde un invierno desolador asola la capital.\n" +
                            "Frank esta designado a investigar unas desapariciones recientes que estuvieron sucediendo con un mismo modus opearani, drenaba la sangre de sus victimas y tambien todas las victimas cumplian con el mismo rango, eran catolicos.\n" +
                            "\n Despues de una ardua investigacion en 5 escenas de crimen en distintos lugares, la resolucion de las investigaciones lo llevan a una catedral a unos 69 kilometros de la capital, frank se adentra en la catedral decidido a encontrar mas pruebas. \n mientras mas se va adentrando mas frio y oscuro era el ambiente, hasta que un olor lo asquea, \n Frank sigue el olor y encuentra un cadaver, y no solo eso, sino que encuentra al asesino \n, un lobo de 2 metros listo para acecharlo"  
                    		);
                    break;
                case "3. Cómo Jugar":
                    JOptionPane.showMessageDialog(null, "Como jugar:\n" +
                    		 "Debes escapar, necesitas la llave para poder abrir las puertas, sin ellas no podras abrirlas\n" +
                    		 "Al usar el crucifijo, la bestia se congelara por 1 segundo para que puedas pensar tu estrategia, y al usarlo se gasta\n" +
                    		 "Si la bestia te atrapa, te mata, deberas arreglartelas para poder escapar intacto\n" +
                    		 "Controles: \n" +
                    		 "W - Mover hacia arriba\n" +
                             "A - Mover hacia la izquierda\n" +
                             "S - Mover hacia abajo\n" +
                             "D - Mover hacia la derecha\n" +
                             "Q - Usar el crucifijo\n");
                    break;
                case "4. Salir":
                    System.exit(0);
                    break;
            }
        }
    }
   
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Dibujar el fondo dentro del área jugable
            Image backgroundImage = (level == 0) ? backgroundImageLevel0 : backgroundImageLevel1;

            // Verificar si la imagen de fondo está cargada correctamente
            if (backgroundImage != null) {
                g.drawImage(
                    backgroundImage,
                    areaX, areaY,
                    level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1,
                    level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1,
                    this
                );
            } else {
                System.out.println("Fondo no cargado correctamente.");
            }

            // Dibujar el rectángulo del área jugable
            g.setColor(Color.WHITE);
            g.drawRect(areaX, areaY,
                level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1,
                level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1
            );

            // Dibujar imágenes en lugar de formas sólidas
            g.drawImage(playerImage, playerX, playerY, 70, 100, this);
            if (!hasKey) g.drawImage(keyImage, keyX, keyY, 25, 25, this); // La llave solo se muestra si no se ha recogido
            g.drawImage(doorImage, doorX, doorY, 140, 200, this);

            if (beastActive) {
                g.drawImage(beastImage, beastX, beastY, 100, 200, this);
            }

            if (level == 1 && !hasCrucifix) {
                g.drawImage(crucifixImage, crucifixX, crucifixY, 30, 30, this);
            }

            if (beastFrozen) {
                g.setColor(Color.WHITE);
                g.drawString("Bestia congelada!", 350, 50);
            }

            if (gameWon) {
                //g.setColor(Color.GREEN);
                //g.drawString("¡Has ganado!", 350, 300);
            	g.drawImage(win, winx, winy, 775, 350, this);
                g.drawImage(beastImageW, beastWX, beastWY, 200, 300, this);
                beastX = -100;
                beastY = -500;
                beastWX = 750;
                beastWY = 400;
                playerX = -100;
                playerY = -500;
             //   wolfwon.stop();
             //   wolfwon.setFramePosition(0);
              //  wolfwon.start();
            }

            if (hasCrucifix && crucifixUses > 0) {
                g.setColor(Color.WHITE);
                g.drawString("Usos de crucifijo: " + crucifixUses, 10, 50);
            }

            if (gameOver) {
                //g.setColor(Color.RED);
                //g.drawString("¡La bestia te ha atrapado!", 350, 300);
            	g.drawImage(lose, losex, losey, 775, 350, this);
                g.drawImage(playerDeathImage, playerDX, playerDY, 100, 100, this);
                playerX = -100;
                playerY = -500;
                playerDX = beastX - 35;
                playerDY = beastY - 15;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameWon && !gameOver) {
            if (moveUp && playerY > areaY) playerY -= 4;
            if (moveDown && playerY < areaY + (level == 0 ? AREA_HEIGHT_0 : AREA_HEIGHT_1) - 40) playerY += 4;
            if (moveLeft && playerX > areaX) playerX -= 4;
            if (moveRight && playerX < areaX + (level == 0 ? AREA_WIDTH_0 : AREA_WIDTH_1) - 40) playerX += 4;

            // Movimiento de la bestia
            if (beastActive && !beastFrozen) {
                if (playerX < beastX) beastX -= 2;
                if (playerX > beastX) beastX += 2;
                if (playerY < beastY) beastY -= 2;
                if (playerY > beastY) beastY += 2;
            }

            // Control de tiempo de congelación de la bestia
            if (beastFrozen && System.currentTimeMillis() >= freezeEndTime) {
                beastFrozen = false;
            }

            // Colisiones
            checkCollisions();
        }

        repaint();
    }

    private void checkCollisions() {
   
    		// Colisión con la llave en el nivel 0
        if (level == 0 && !hasKey && playerX < keyX + 25 && playerX + 70 > keyX && playerY < keyY + 25 && playerY + 100 > keyY) {
            hasKey = true;
            keyX = -100; // Mover la llave fuera de la pantalla
            keyY = -100;
            keySound.setFramePosition(0);
            keySound.start();
        }

        // Colisión con la puerta (nivel 0)
        if (level == 0 && hasKey && playerX < doorX + 140 && playerX + 40 > doorX && playerY < doorY + 200 && playerY + 100 > doorY) {
            level = 1;
            setupLevel();
            hasKey = false; // La llave se pierde al cambiar de nivel
            doorSound.setFramePosition(0);
            doorSound.start();
        }

        // Colisión con la bestia (si la bestia está activa y el jugador la toca)
        if (beastActive && playerX < beastX + 80 && playerX + 20 > beastX && playerY < beastY + 250 && playerY + 100 > beastY) {
            if (!beastFrozen) {
                gameOver = true;
                beastAttackSound.setFramePosition(0);
                beastAttackSound.start();
                
            }
        }

        // Colisión con el crucifijo (nivel 1)
        if (level == 1 && !hasCrucifix && playerX < crucifixX + 30 && playerX + 40 > crucifixX && playerY < crucifixY + 30 && playerY + 100 > crucifixY) {
            hasCrucifix = true;
            crucifixX = -100; // Mover el crucifijo fuera de la pantalla
            crucifixY = -100;
            crucifixSound.setFramePosition(0);
            crucifixSound.start();
        }
        
        //Colision con la llave nivel 1
        if (level == 1 && !hasKey && playerX < keyX + 25 && playerX + 70 > keyX && playerY < keyY + 25 && playerY + 100 > keyY) {
            hasKey = true;
            keyX = -100; // Mover la llave fuera de la pantalla
            keyY = -100;
            keySound.setFramePosition(0);
            keySound.start();
        }

        // Colisión con la puerta (nivel 1)
        if (level == 1 && hasKey && playerX < doorX + 140 && playerX + 40 > doorX && playerY < doorY + 200 && playerY + 100 > doorY) {
        	gameWon = true;
            doorSound.setFramePosition(0);
            doorSound.start();
        }
        
        
        
    }

   
    
   private void setupLevel() {
        // Resetear el estado del juego para el nuevo nivel
    	 if (musica0 != null) {
             musica0.stop();
    		 musica0.setFramePosition(0);
             musica0.start();
         } else {
             System.err.println("Error: musica0 no está inicializada.");
         }
    	 
        playerX = areaX + 100;
        playerY = areaY + 500;
        keyX = (int) 500+ areaX;// Posición aleatoria de la llave
        keyY = (int) 500 + areaY;  // Posición aleatoria de la llave
        doorX = areaX + 1350; // Posición de la puerta en el nivel 0
        doorY = areaY + 390;
        beastActive = false; // la bestia no esta en el nivel 0

        if (level == 1) {
        	  playerX = areaX + 500;
              playerY = areaY + 500;
        	crucifixX = (int) 700 + areaX; // Posición del crucifijo
            crucifixY = (int) 425 + areaY;
            keyX = (int) 800+ areaX; // Posición aleatoria de la llave
            keyY = (int) 425 + areaY; 
            beastX = (int) 1250 + areaX; // Posición inicial de la bestia
            beastY = (int) 100 + areaY;
            doorX = areaX + 1050; // Posición de la puerta en el nivel 0
            doorY = areaY + 400;
            beastActive = true;
        }

        gameWon = false;
        gameOver = false;
    }

    
   
   @Override
   public void keyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_W) {
           moveUp = true;
       }
       if (e.getKeyCode() == KeyEvent.VK_S) {
           moveDown = true;
       }
       if (e.getKeyCode() == KeyEvent.VK_A) {
           moveLeft = true;
       }
       if (e.getKeyCode() == KeyEvent.VK_D) {
           moveRight = true;
       }

       // Tomar foto (usar el crucifijo en el nivel 1)
       if (e.getKeyCode() == KeyEvent.VK_Q && hasCrucifix && crucifixUses > 0) {
           useCrucifix();
       }

       // Si el jugador está cerca de la bestia y presiona 'R', tomar una foto de la bestia
       if (e.getKeyCode() == KeyEvent.VK_R && beastActive && !beastFrozen) {
           beastFrozen = true;
           freezeEndTime = System.currentTimeMillis() + 1000; // Congela la bestia por 1 segundo
       }
   }

   private void useCrucifix() {
       if (hasCrucifix && crucifixUses > 0) {
           crucifixUses--; // Usar el crucifijo
           beastFrozen = true;
           freezeEndTime = System.currentTimeMillis() + 1000; // Congelar a la bestia por 1 segundo
           beastSound.stop();
           beastSound.setFramePosition(0);
           beastSound.start();
       }
   }

   @Override
   public void keyReleased(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_W) {
           moveUp = false;
       }
       if (e.getKeyCode() == KeyEvent.VK_S) {
           moveDown = false;
       }
       if (e.getKeyCode() == KeyEvent.VK_A) {
           moveLeft = false;
       }
       if (e.getKeyCode() == KeyEvent.VK_D) {
           moveRight = false;
       }
   }

   @Override
   public void keyTyped(KeyEvent e) {
       // No se usa
   }

   public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
           EscapaDeLaBestia game = new EscapaDeLaBestia();
           game.setVisible(true);
       });
   }
}
