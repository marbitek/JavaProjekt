package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel z zaimplementowaną symulacją kulistej fali dźwiękowej.
 * Wykorzystuje klasę Pixel do wizualizacji siatki.
 */
public class Simulation extends JPanel implements Runnable {
    private static final long serialVersionUID = 1L;
    private final int x_dim;    // liczba pikseli w poziomie
    private final int y_dim;    // liczba pikseli w pionie
    private final int pixelSize;

    // Buffory stanu fali
    private double[][] previous;
    private double[][] current;
    private double[][] next;

    // Siatka obiektów Pixel
    private Pixel[][] pixels;
    
    

    // Lista źródeł fal
    private final List<Source> sources = new ArrayList<>();

    // Parametry symulacji
    protected double freq;
    protected double damping;
    private static final double c = 343.0;         // prędkość dźwięku m/s
    private final double brightnessFactor = 255.0 / 25.0;

    public Simulation(int x_dim, int y_dim, int pixelSize) {
        this.x_dim = x_dim;
        this.y_dim = y_dim;
        this.pixelSize = pixelSize;
        setPreferredSize(new Dimension(x_dim * pixelSize, y_dim * pixelSize));
        setBackground(Color.WHITE);
        setOpaque(true);

        // Inicjalizacja buforów
        previous = new double[x_dim][y_dim];
        current  = new double[x_dim][y_dim];
        next     = new double[x_dim][y_dim];

        // Inicjalizacja siatki pikseli
        
        pixels = new Pixel[x_dim][y_dim];
        for (int x = 0; x < x_dim; x++) {
            for (int y = 0; y < y_dim; y++) {
                pixels[x][y] = new Pixel(x, y, Color.WHITE, 0, x, y);
            }
        }

        

        // Obsługa kliknięcia - dodanie źródła fali
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / pixelSize;
                int y = e.getY() / pixelSize;
                if (x >= 0 && x < x_dim && y >= 0 && y < y_dim) {
                    Source s = new Source(x, y);
                    sources.add(s);
                    addImpulse(s, 20.0); //amplituda 20
                }
            }
        });

        // Start wątku symulacji
        new Thread(this).start();
    }
    
    /**
     * Ustawia częstotliwość
     * @param freq
     */
    
    public void setFreq(double freq) {
    	this.freq = freq;
    }

    /**
     * Dodaje impuls fali w promieniu r wokół źródła.
     */
    private void addImpulse(Source s, double amplitude) {
        int r = 3;
        long t = System.nanoTime();
        setFreq(500); // Hz
        double wave = amplitude * Math.sin(2 * Math.PI * freq * (t * 1e-9));
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                int px = s.x + dx;
                int py = s.y + dy;
                if (px >= 1 && px < x_dim - 1 && py >= 1 && py < y_dim - 1 && dx*dx + dy*dy <= r*r) {
                    current[px][py] += wave;
                    previous[px][py] += wave;
                }
            }
        }
    }
    
    /**
     * Ustawia wygaszanie
     * @param damping
     */
    
    public void setDamping(double damping) {
    	this.damping = damping;
    }

    /**
     * Aktualizuje stan fali metodą różnic skończonych.
     */
    private void updateSimulation() {
        double dx = 0.01;
        double dt = 1e-5;
        double coeff = (c * dt / dx) * (c * dt / dx);
        if (coeff > 0.5) return;
        
        setDamping(0.996); //ustawienie wygaszania

        
        
        //
        for (int x = 1; x < x_dim - 1; x++) {
            for (int y = 1; y < y_dim - 1; y++) {
                double lap = current[x-1][y] + current[x+1][y]
                           + current[x][y-1] + current[x][y+1]
                           - 4 * current[x][y];
                next[x][y] = 2 * current[x][y] - previous[x][y] + coeff * lap;
                next[x][y] *= damping;
            }
        }

        // Zamiana buforów
        double[][] tmp = previous;
        previous = current;
        current = next;
        next = tmp;
        
        
    }

    /**
     * Aktualizuje kolory obiektów Pixel na podstawie amplitudy.
     */
    private void updatePixels() {
        for (int x = 0; x < x_dim; x++) {
            for (int y = 0; y < y_dim; y++) {
                double v = current[x][y];
                int shade = 255 - (int)(brightnessFactor * Math.abs(v));
                shade = Math.max(0, Math.min(255, shade));
                pixels[x][y].setClr(new Color(shade, shade, shade));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int x = 0; x < x_dim; x++) {
            for (int y = 0; y < y_dim; y++) {
                pixels[x][y].draw(g2, pixelSize);
            }
        }
    }

    @Override
    public void run() {
        long lastImpulse = System.currentTimeMillis();
        while (true) {
            updateSimulation();
            updatePixels();
            long now = System.currentTimeMillis();
            if (now - lastImpulse > 50) {
                for (Source s : sources) addImpulse(s, 5.0);
                lastImpulse = now;
            }
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reprezentuje źródło fali.
     */
    private static class Source {
        final int x, y;
        Source(int x, int y) { this.x = x; this.y = y; }
    }
}

