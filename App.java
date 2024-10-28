import javax.swing.*;  // Importiert die Swing-Bibliothek, um grafische Benutzeroberflächen (GUI) zu erstellen.

public class App {  // Definiert die Hauptklasse "App", die den Einstiegspunkt des Programms enthält.
    public static void main(String[] args) throws Exception {  // Hauptmethode, die das Programm startet.
        int boardWidth = 360;  // Legt die Breite (360 Pixel) des Spielfelds fest.
        int boardHeight = 640;  // Legt die Höhe (640 Pixel) des Spielfelds fest.

        JFrame frame = new JFrame("Flappy Bird");  // Erstellt ein neues Fenster mit dem Titel "Flappy Bird".
        frame.setSize(boardWidth, boardHeight);  // Setzt die Größe des Fensters auf die festgelegten Breite und Höhe.
        frame.setLocationRelativeTo(null);  // Platziert das Fenster in der Mitte des Bildschirms.
        frame.setResizable(false);  // Verhindert, dass die Größe des Fensters geändert werden kann.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Stellt sicher, dass das Programm beendet wird, wenn das Fenster geschlossen wird.

        FlappyBird flappyBird = new FlappyBird();  // Erstellt ein neues Objekt der Klasse "FlappyBird", das das Spiel repräsentiert.
        frame.add(flappyBird);  // Fügt das "FlappyBird"-Objekt zum Fenster (JFrame) hinzu.
        frame.pack();  // Passt die Größe des Fensters an, damit alle Komponenten optimal angezeigt werden.
        flappyBird.requestFocus();  // Setzt den Fokus auf das FlappyBird-Objekt, damit es Eingaben (z.B. Tastatur) empfangen kann.
        frame.setVisible(true);  // Macht das Fenster sichtbar und startet das Spiel.
    }
}


