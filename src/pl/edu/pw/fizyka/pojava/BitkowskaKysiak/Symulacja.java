package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Symulacja fali kulistej 
 */
public class Symulacja extends JPanel implements Runnable {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private double[][] previous, current, next; //bufory stanów fali 
    //private double damping = 0.996; //wygaszanie
    private static final double c = 343.0; //prędkość fali dzwiękowej

    // Siatka obiektów Pixel
    private Pixel[][] pixels;

    // Lista źródeł
    private final List<Source> sources = new ArrayList<Source>();

    // skalowania jasności
    private double brightnessFactor = 255.0 / 25.0;

    public Symulacja() {
    	
    	//ustawienia panelu 
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setOpaque(true);

        // Bufory fali
        previous = new double[WIDTH][HEIGHT];
        current = new double[WIDTH][HEIGHT];
        next = new double[WIDTH][HEIGHT];

        // Inicjalizacja Pixela
        pixels = new Pixel[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                pixels[x][y] = new Pixel(x, y, Color.WHITE, 0);
            }
        }

        // Dodawanie źródła po kliknięciu - w zależności od tego jaki guziczek jest wćiśnięty
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Source s = new Source(e.getX(), e.getY());
                sources.add(s);
                addImpulse(s, 20);//tworzenie impulsu po kliknięciu, amplituda 20
            }
        });

        new Thread(this).start();
    }

    // Początkowy impuls fali
    private void addImpulse(Source s, double amplitude) {
        int r = 3; //promień okręgu w pixrlach 
        long t = System.nanoTime();
        double freq = 500; //częstotliwość - do zmany, żeby w zależności od suwaczka
        double w = amplitude * Math.sin(2*Math.PI*freq*(t*1e-9));
        for (int dx = -r; dx <= r; dx++) for (int dy = -r; dy <= r; dy++) {
            int px = s.x+dx, py = s.y+dy;
            if (px>0 && px<WIDTH-1 && py>0 && py<HEIGHT-1 && dx*dx+dy*dy<=r*r) {
                current[px][py] += w;
                previous[px][py] += w;
            }
        }
    }

    // Aktualizacja symulacji
    private void updateSimulation() {
        double dx=0.01;
        double dt=1e-5;
        double coeff=(c*dt/dx)*(c*dt/dx);
        if (coeff>0.5) return;
        
        //zdyskretyzowany wzór rówanian falowego - to wsadzić do specyfikacji
        for (int x=1;x<WIDTH-1;x++) for (int y=1;y<HEIGHT-1;y++) {
            double lap = current[x-1][y]+current[x+1][y]
                       +current[x][y-1]+current[x][y+1]
                       -4*current[x][y];
            next[x][y] = 2*current[x][y] - previous[x][y] + coeff*lap;
            //next[x][y] *= damping; - jeśli zrobimy wygaszanie
        }
        // Zamiana
        double[][] tmp = previous; 
        previous=current; 
        current=next; 
        next=tmp;
    }

    // Aktualizacja kolorów w pixelach
    private void updatePixels() {
        for (int x=0;x<WIDTH;x++) for (int y=0;y<HEIGHT;y++) {
            double v = current[x][y];
            int shade = 255 - (int)(brightnessFactor*Math.abs(v));
            shade = Math.max(0, Math.min(255, shade));
            pixels[x][y].setClr(new Color(shade,shade,shade));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        for (int x=0;x<WIDTH;x++) for (int y=0;y<HEIGHT;y++) {
            pixels[x][y].draw(g2,1);
        }
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis();
        while (true) {
            updateSimulation();
            updatePixels();
            long now = System.currentTimeMillis();
            if (now-last>50) { 
            	for (Source s: sources) 
            		addImpulse(s,5); 
            	last=now; 
            	}
            repaint();
            try {
            	Thread.sleep(16);
            } catch(Exception e){}
        }
    }

    // Źródło fali  
    private static class Source{ 
    	int x,y; 
    	
    	Source(int x,int y){
    		this.x=x;this.y=y;
    		} 
    	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
            JFrame f=new JFrame("Symulacja");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new Symulacja());f.pack();f.setVisible(true);
        }});
    }
}
