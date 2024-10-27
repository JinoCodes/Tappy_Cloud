import java.awt.*;  // Importiert die notwendigen Klassen für die grafische Darstellung.
import java.awt.event.*;  // Importiert die Klassen für Event-Handling (z.B. Tastatur, Maus).
import java.io.IOException;
import java.util.ArrayList;  // Importiert ArrayList, um Listen von Objekten zu verwalten.
import java.util.Random;  // Importiert Random, um zufällige Werte zu erzeugen.
import javax.swing.*;  // Importiert die Swing-Bibliothek für GUI-Komponenten.
import javax.sound.sampled.*; // Füge dies am Anfang der Datei hinzu


public class FlappyBird extends JPanel implements ActionListener, KeyListener {  
    // Die Klasse FlappyBird erbt von JPanel (für das GUI) und implementiert ActionListener (für Timer-Events) und KeyListener (für Tastatur-Events).


    boolean gameOverSoundPlayed = false;  // Neue Variable, um zu überprüfen, ob der Game Over-Sound gespielt wurde
    boolean mousePressed = false; // Variable, um zu überprüfen, ob die Maus gedrückt wurde
    int mouseX, mouseY; // Koordinaten der Maus

    int boardWidth = 360;  // Die Breite des Spielfelds.
    int boardHeight = 640;  // Die Höhe des Spielfelds.

    // Bilder für das Spiel (Hintergrund, Vogel, Rohre).
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image playButtonImg; // Bild für den Spiel-Button
    boolean gameStarted = false; // Spielstatus
    boolean startScreen = true; // Variable für den Startbildschirm
    ImageIcon gameOverGif = new ImageIcon(getClass().getResource("./Game_Over.gif"));
    



    // Lade das Bild für den Spiel-Button
    {
        playButtonImg = new ImageIcon(getClass().getResource("./playButton.png")).getImage(); 

    }

    // Vogel-Klasse: Initialisierung der Position und Größe des Vogels.
    int birdX = boardWidth / 8;  // Anfangsposition des Vogels in X.
    int birdY = boardWidth / 2;  // Anfangsposition des Vogels in Y.
    int birdWidth = 44;  // Breite des Vogels.
    int birdHeight = 34;  // Höhe des Vogels.
    
    class Bird {
        int x = birdX;  // X-Position des Vogels.
        int y = birdY;  // Y-Position des Vogels.
        int width = birdWidth;  // Breite des Vogels.
        int height = birdHeight;  // Höhe des Vogels.
        Image img;  // Bild des Vogels.
    
        Bird(Image img) {
            this.img = img;  // Konstruktor für die Bird-Klasse, setzt das Bild des Vogels.
        }
    }


    // Rohr-Klasse: Initialisierung der Position und Größe der Rohre.
    int pipeX = boardWidth;  // Anfangsposition des Rohrs in X.
    int pipeY = 0;  // Anfangsposition des Rohrs in Y.
    int pipeWidth = 64;  // Breite des Rohrs.
    int pipeHeight = 512;  // Höhe des Rohrs.

    class Pipe {
        int x = pipeX;  // X-Position des Rohrs.
        int y = pipeY;  // Y-Position des Rohrs.
        int width = pipeWidth;  // Breite des Rohrs.
        int height = pipeHeight;  // Höhe des Rohrs.
        Image img;  // Bild des Rohrs.
        boolean passed = false;  // Ob der Vogel das Rohr bereits passiert hat.
        Clip pointSound; // Variable für den Soundeffekt

        Pipe(Image img) {
            this.img = img;  // Konstruktor für die Pipe-Klasse, setzt das Bild des Rohrs.
        }
        
    }
    

    // Spiel-Logik
    Bird bird;  // Das Vogel-Objekt.
    int velocityX = -4;  // Geschwindigkeit, mit der sich die Rohre nach links bewegen (simuliert die Bewegung des Vogels nach rechts).
    int velocityY = 0;  // Vertikale Geschwindigkeit des Vogels.
    int gravity = 1;  // Schwerkraft, die den Vogel nach unten zieht.

    ArrayList<Pipe> pipes;  // Liste der Rohre.
    Random random = new Random();  // Zufallszahlengenerator für die Position der Rohre.

    Timer gameLoop;  // Timer für die Spielschleife.
    Timer placePipeTimer;  // Timer zum Erstellen neuer Rohre.
    boolean gameOver = false;  // Spielstatus: Ob das Spiel vorbei ist oder nicht.
    double score = 0;  // Spielstand.

    Clip pointSound; // Clip für den Soundeffekt
    Clip gameOverSound; // Clip für den Game Over-Sound
    Font daydreamFont;


    FlappyBird() {  
        // Konstruktor für die FlappyBird-Klasse. Hier werden die Spielkomponenten initialisiert.
        setPreferredSize(new Dimension(boardWidth, boardHeight));  // Setzt die bevorzugte Größe des Spielfelds.
        setFocusable(true);  // Ermöglicht, dass das Panel Tastatureingaben erhält.
        addKeyListener(this);  // Fügt einen KeyListener hinzu, um auf Tastatureingaben zu reagieren.
        addMouseListener(new MouseAdapter() {

            
        
            @Override
            public void mousePressed(MouseEvent e) {
                // Speichere die Koordinaten des Mausklicks
                mouseX = e.getX();
                mouseY = e.getY();
                
                // Prüfe, ob der Benutzer auf den Startbutton geklickt hat
                int buttonWidth = playButtonImg.getWidth(null) / 2;
                int buttonHeight = playButtonImg.getHeight(null) / 2;
                int buttonX = (boardWidth - buttonWidth) / 2;
                int buttonY = (boardHeight - buttonHeight) / 2;
                
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                // Der Benutzer hat den Startbutton gedrückt
                startScreen = false;  // Setzt den Startbildschirm auf false
                gameStarted = true;   // Das Spiel startet
                gameOver = false;     // Setzt Game Over zurück
                
                // Setze alle relevanten Werte zurück
                bird.y = birdY;           // Setzt die Position des Vogels zurück
                velocityY = 0;            // Setzt die Geschwindigkeit des Vogels zurück
                pipes.clear();            // Entfernt alle Rohre
                score = 0;                // Setzt den Punktestand zurück
                pipeSpeedMultiplier = 1.0; // Setzt die Geschwindigkeit der Rohre zurück
                
                // Starte Timer und Schleife neu
                placePipeTimer.start();    // Startet das Erstellen der Rohre
                gameLoop.start();          // Startet die Spielschleife
                }
            }
            
            

            
        });

        
        
        
        
        
    
        // Laden der Bilder für das Spiel.
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        playButtonImg = new ImageIcon(getClass().getResource("./PlayButton.png")).getImage(); // Spiel-Button
        
    
        // Initialisierung des Vogels.
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();  // Initialisierung der Liste der Rohre.
    
        // Timer zum Platzieren der Rohre.
        placePipeTimer = new Timer(1500, new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();  // Fügt alle 1,5 Sekunden ein neues Paar Rohre hinzu.
            }
        });
        placePipeTimer.start();  // Startet den Timer für die Rohre.
    
        // Spielschleifen-Timer (60 FPS).
        gameLoop = new Timer(1000 / 60, this);  // Aktualisiert das Spiel 60 Mal pro Sekunde.
        gameLoop.start();  // Dies wird nach dem Drücken des Startbuttons ausgeführt
    
        // Lade den Soundeffekt
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/PointSound.wav"));
    
            pointSound = AudioSystem.getClip();
            pointSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Lade den Game Over-Sound
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/OverSound.wav")); // Pfad zur Game Over-Datei
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Lade die Schriftart
        try {
            daydreamFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Daydream3.ttf")).deriveFont(32f); // Größe anpassen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(daydreamFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        

        

        
    }
    

    void placePipes() {  
        // Methode zum Platzieren neuer Rohre.
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));  
        // Berechnet eine zufällige Y-Position für das obere Rohr.
        int openingSpace = boardHeight / 4;  
        // Legt den Abstand zwischen dem oberen und unteren Rohr fest (Öffnung für den Vogel).

        Pipe topPipe = new Pipe(topPipeImg);  // Erstellt das obere Rohr.
        topPipe.y = randomPipeY;  // Setzt die Y-Position des oberen Rohrs.
        pipes.add(topPipe);  // Fügt das obere Rohr zur Liste der Rohre hinzu.

        Pipe bottomPipe = new Pipe(bottomPipeImg);  // Erstellt das untere Rohr.
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;  // Setzt die Y-Position des unteren Rohrs.
        pipes.add(bottomPipe);  // Fügt das untere Rohr zur Liste der Rohre hinzu.
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    
        if (startScreen) {
            // Startbildschirm mit Play-Button anzeigen
            int buttonWidth = playButtonImg.getWidth(null) / 2;
            int buttonHeight = playButtonImg.getHeight(null) / 2;
            g.drawImage(playButtonImg, (boardWidth - buttonWidth) / 2, (boardHeight - buttonHeight) / 2, buttonWidth, buttonHeight, null);
            return;
        }
    
        // Wenn Game Over, zeige das Game Over GIF
        if (gameOver) {
            int gameOverWidth = gameOverGif.getIconWidth() / 6;  // Größe anpassen
            int gameOverHeight = gameOverGif.getIconHeight() / 6; // Größe anpassen
    
            g.drawImage(gameOverGif.getImage(), (boardWidth - gameOverWidth) / 2, (boardHeight - gameOverHeight) / 4, gameOverWidth, gameOverHeight, this);
            return;
        }
    }
    
    
    

        
    

    public void draw(Graphics g) {  
        // Zeichnet den Hintergrund, den Vogel und die Rohre.
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);  // Zeichnet den Hintergrund.
        
        if (!gameStarted) {
            return; // verlässt die Methode, wenn das Spiel nicht gestartet wurde
        }
        
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);  // Zeichnet den Vogel.
        
        // Zeichnet alle Rohre.
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        
        // Punktestand oder Game Over anzeigen
        g.setFont(new Font("Daydream", Font.PLAIN, 24)); // Schriftgröße einstellen
        String scoreText;
        
        // Überprüfen, ob das Spiel vorbei ist
        if (gameOver) {
            scoreText = "Points: " + (int) score;  // Setzt den Text für "Game Over".
            g.setColor(Color.red);  // Farbe auf Rot für den "Game Over"-Text.
        
            // Zentrische Position des Textes berechnen
            int textWidth = g.getFontMetrics().stringWidth(scoreText);
            int textHeight = g.getFontMetrics().getHeight();
        
            // Y-Position etwas höher setzen
            int adjustedYPosition = (boardHeight + textHeight) / 2 - 90; // -90 hebt den Text um 90 Pixel
        
            g.drawString(scoreText, (boardWidth - textWidth) / 2, adjustedYPosition);
            return;
        }
        
        // Spiel-Punktestand zeichnen
        scoreText = String.valueOf((int) score);  // Zeigt den aktuellen Punktestand an.
        
        // Position für den Text
        int x = 10;
        int y = 35;
    
        // Rahmen zeichnen (schwarz)
        g.setColor(Color.black); // Farbe für den Rahmen
        g.drawString(scoreText, x - 1, y); // Rahmen links
        g.drawString(scoreText, x + 1, y); // Rahmen rechts
        g.drawString(scoreText, x, y - 1); // Rahmen oben
        g.drawString(scoreText, x, y + 1); // Rahmen unten
        
        // Punktestand zeichnen
        g.setColor(Color.white); // Setze die Farbe auf Weiß während des Spiels
        g.drawString(scoreText, x, y); // Zeichne den Punktestand
    }
    
    
    
    

    // Füge diese Variable am Anfang der FlappyBird-Klasse hinzu
double pipeSpeedMultiplier = 1.0;  // Geschwindigkeit der Rohre




// Move Methode      
public void move() {
    if (gameStarted) {
    // Schwerkraft auf die vertikale Geschwindigkeit des Vogels anwenden
    velocityY += gravity;  
    // Die Position des Vogels basierend auf seiner vertikalen Geschwindigkeit aktualisieren
    bird.y += velocityY;  
    // Verhindern, dass der Vogel über den oberen Bildschirmrand fliegt
    bird.y = Math.max(bird.y, 0);  

    // Durchlaufe die Liste der Rohre
    for (int i = 0; i < pipes.size(); i++) {  
        Pipe pipe = pipes.get(i);  // Hole das aktuelle Rohr
        // Bewege das Rohr nach links basierend auf der Geschwindigkeit und dem Multiplier
        pipe.x += (int)(velocityX * pipeSpeedMultiplier);  

        // Überprüfen, ob der Vogel unter den Bildschirm gefallen ist
        if (bird.y > boardHeight) {  
            gameOver = true;  // Setze den Spielstatus auf "Game Over"

            // Spiele den Game Over Sound ab, wenn der Vogel außerhalb des Spielfelds fällt
            if (!gameOverSound.isRunning()) { 
                gameOverSound.setFramePosition(0); // Setze den Sound auf den Anfang
                gameOverSound.start(); // Spiele den Game Over Sound ab

                // Wenn der Vogel unter den Bildschirm gefallen ist
if (bird.y > boardHeight) {  
    gameOver = true;  // Setze den Spielstatus auf "Game Over"
    
    // Spiele den Game Over-Sound nur einmal ab
    if (!gameOverSoundPlayed && !gameOverSound.isRunning()) { 
        gameOverSound.setFramePosition(0); // Setze den Sound auf den Anfang
        gameOverSound.start(); // Spiele den Game Over-Sound ab
        gameOverSoundPlayed = true;  // Markiere, dass der Sound gespielt wurde
    }
}


// Überprüfen auf Kollision mit dem Rohr
if (collision(bird, pipe)) {  
    gameOver = true;  // Setze den Spielstatus auf "Game Over"
    
    // Spiele den Game Over-Sound nur einmal ab
    if (!gameOverSoundPlayed && !gameOverSound.isRunning()) { 
        gameOverSound.setFramePosition(0); // Setze den Sound auf den Anfang
        gameOverSound.start(); // Spiele den Game Over-Sound ab
        gameOverSoundPlayed = true;  // Markiere, dass der Sound gespielt wurde
    }
}

            }
        }  
        
        // Überprüfen, ob das Rohr den linken Bildschirmrand verlassen hat
        if (pipe.x + pipeWidth < 0) {  
            pipes.remove(i);  // Entferne das Rohr aus der Liste
            i--;  // Verringere den Index, um keine Elemente zu überspringen
        }  

        // Überprüfen, ob der Vogel das Rohr passiert hat
        if (!pipe.passed && bird.x > pipe.x + pipeWidth - 50) {  
            score += 0.5;  // Erhöhe den Punktestand
            pipe.passed = true;  // Markiere das Rohr als passiert
            pipeSpeedMultiplier *= 1.01;  // Erhöhe die Geschwindigkeit der Rohre

            // Spiele den Punktesound ab
            if (!pointSound.isRunning()) {
                pointSound.setFramePosition(0); // Setze den Punktesound auf den Anfang
                pointSound.start(); // Spiele den Punktesound ab
            }
        }  
        
        // Überprüfen auf Kollision mit dem Rohr
        if (collision(bird, pipe)) {  
            gameOver = true;  // Setze den Spielstatus auf "Game Over"

            // Spiele den Game Over Sound ab, wenn eine Kollision mit dem Rohr stattfindet
            if (!gameOverSound.isRunning()) { 
                gameOverSound.setFramePosition(0); // Setze den Sound auf den Anfang
                gameOverSound.start(); // Spiele den Game Over Sound ab
            }
        }  
    }
}
}


    



 

boolean collision(Bird a, Pipe b) {
    // Füge eine Kollisionstoleranz hinzu

    int tolerance = 1;  // Toleranzwert
    

    return a.x < b.x + b.width - tolerance &&   
           a.x + a.width > b.x + tolerance &&   
           a.y < b.y + b.height - tolerance &&  
           a.y + a.height > b.y + tolerance;    
}


@Override
public void actionPerformed(ActionEvent e) {  
    move();  
    repaint();  

    if (gameOver) {
        placePipeTimer.stop();  
        gameLoop.stop();  
    }
}



@Override
public void keyPressed(KeyEvent e) {
    // Wenn das Spiel zu Ende ist und eine Taste gedrückt wird, setze das Spiel zurück
    if (gameOver) {
        resetGame();
        return;
    }

    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        // Verhindere das Drücken der Leertaste nach einem Game Over
        if (!gameStarted || gameOver) {
            return;  // Ignoriere das Drücken der Leertaste, wenn das Spiel nicht läuft oder Game Over ist
        }
        
        // Aktion bei Leertaste während des Spiels (zum Beispiel Springen des Vogels)
        velocityY = -8;  // Setzt die Geschwindigkeit, damit der Vogel springt
    }
}


public void resetGame() {
    // Setze alle relevanten Variablen und Zustände zurück
    gameOver = false;
    gameOverSoundPlayed = false;
    startScreen = true;
    gameStarted = false;
    score = 0;
    bird.y = birdY;
    velocityY = 0;
    pipes.clear();
    pipeSpeedMultiplier = 1.0;

    // Starte die Timers neu
    gameLoop.start();
    placePipeTimer.start();
}



    

    double speedIncreaseFactor = 1.01;  // 1% Geschwindigkeitssteigerung nach jedem Rohr-Paar

    

    @Override
    public void keyTyped(KeyEvent e) {}  // Nicht verwendet.

    @Override
    public void keyReleased(KeyEvent e) {}  // Nicht verwendet.
}
