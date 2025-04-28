package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Symulacja kulistej fali dźwiękowej na siatce 2D.
 * Wykorzystuje BufferedImage do płynnego rysowania.
 */
public class Symulacja extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private double[][] previous;
    private double[][] current;
    private double[][] next;
    private BufferedImage image;

    private double damping = 0.996;
    private static final double c = 343.0; // prędkość dźwięku w powietrzu, m/s
    double maxVal = 25.0;
    double brightnessFactor = 255.0 / maxVal;  // ≈ 10.2



    private boolean running = true;
    private final List<Source> sources = new ArrayList<Source>();

    public Symulacja() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setDoubleBuffered(true);

        current = new double[WIDTH][HEIGHT];
        previous = new double[WIDTH][HEIGHT];
        next = new double[WIDTH][HEIGHT];
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Source src = new Source(e.getX(), e.getY());
                sources.add(src);
                addImpulse(src, 20.0);
            }
        });

        new Thread(this).start();
    }

    private void addImpulse(Source source, double amplitude) {
        int radius = 3;
        long time = System.nanoTime();
        double frequency = 500; // Hz
        double wave = amplitude * Math.sin(2 * Math.PI * frequency * (time * 1e-9));
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int px = source.x + dx;
                int py = source.y + dy;
                if (px >= 1 && px < WIDTH-1 && py >= 1 && py < HEIGHT-1 && dx*dx+dy*dy <= radius*radius) {
                    current[px][py] += wave;
                    previous[px][py] += wave;
                }
            }
        }
    }

    private void updateSimulation() {
        double dx = 0.01;
        double dt = 1e-5;
        double coeff = (c*dt/dx)*(c*dt/dx);
        if (coeff > 0.5) return;

        for (int x = 1; x < WIDTH-1; x++) {
            for (int y = 1; y < HEIGHT-1; y++) {
                double lap = current[x-1][y] + current[x+1][y]
                           + current[x][y-1] + current[x][y+1]
                           - 4*current[x][y];
                next[x][y] = 2*current[x][y] - previous[x][y] + coeff*lap;
                next[x][y] *= damping;
            }
        }
        double[][] tmp = previous;
        previous = current;
        current = next;
        next = tmp;
    }

    private void updateImage() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
            	double val = current[x][y];
            	// Jeżeli amplituda = 0, shade = 255 (biały). W miarę wzrostu |val|, shade maleje.
            	int shade = 255 - (int)(brightnessFactor * Math.abs(val));
            	shade = Math.max(0, Math.min(255, shade));
            	image.setRGB(x, y, new Color(shade, shade, shade).getRGB());

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, null);
    }

    @Override
    public void run() {
        long lastImpulse = System.currentTimeMillis();
        while (running) {
            updateSimulation();
            updateImage();

            long now = System.currentTimeMillis();
            if (now - lastImpulse >= 50) {
                for (Source s : sources) addImpulse(s, 5.0);
                lastImpulse = now;
            }

            repaint();
            try { Thread.sleep(16); } catch (InterruptedException ignored) {}
        }
    }

    private static class Source { int x, y; Source(int x,int y){this.x=x;this.y=y;} }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Symulacja Kulistej Fali Dźwiękowej");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new Symulacja()); f.pack(); f.setVisible(true);
        });
    }
}
