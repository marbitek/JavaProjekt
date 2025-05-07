package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

//JPanel w GamePanel'u w ktorym odbywac sie bedzie symulacja
public class SimulationPanel extends JPanel implements Runnable
{
	/**
	 * @author Michał Kysiak - generacja terenu
	 * @author Maria Bitkowska - propagacja fali
	 * 
	 * Klasa odpowiadająca z panel symulacyjny - symulacja fal i generacja terenu.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final int x_dim;
	protected final int y_dim;
	private int pixelSize;
	
	//siatka pixeli
	protected List<List<Pixel>> pixelGrid; //tablice wierszy siatki pixeli
	protected List<Pixel> pixelRow; //rządek pixeli do inicjalizacji
		
	//Obrazek panelu 
	private BufferedImage panelImage = null;
	
	
	private int imgW, imgH;
	protected float R, G, B;
	
	// Lista źródeł fali
    private final List<Source> sources = new ArrayList<>();
	
	 // Buffory stanu fali
    private double[][] previous, current, next;
    private double damping = 1; //domyślnie brak tłumienia
    private double freq = 0;               // domyślna częstotliwość Hz
    private int maxSources = 1;   // ile w ogóle pozwalamy tworzyć
  
	//flagi 
	protected boolean addEnabled = false; //flaga czy dodawać zródło 
	protected boolean generate = false; //flaga czy generować teren, na razie
	protected boolean simRunning = true; //flaga czy symulacja wogole działa
	
	// Klasa Źródło fali
    private static class Source {
    	int x,y; 
    	
    	Source(int x,int y){
    		this.x=x;
    		this.y=y;
    		} 
    	}
    
    //maksymalna liczba xrodel
    public void setMaxSources(int max) {
        this.maxSources = max;
      }
     
     //ustawienia częstotliwości
    public void setFreq(double freq) {
    	this.freq = freq;
    }
    
   //flaga dodawania zrodla
    public void setAddEnabled(boolean addEnabled) {
        this.addEnabled = addEnabled;
    }
    
    //ustawianie flagi SimRunning
    public void setSimRunning(boolean simRunning) {
    	this.simRunning = simRunning;
    }

	
    //konstruktor SimulationPanel
	public SimulationPanel(int X, int Y, int size, int GamePanelX, int GamePanelY)
	{
		this.x_dim = X;
		this.y_dim = Y;
		//this.pixelSize = size;
		
		if (x_dim <= 0 || y_dim <= 0) {
		    throw new IllegalArgumentException("x_dim and y_dim must be > 0");
		}
		
		//pixelSize = size;
		//pixelSize = (GamePanelX/X);
		//pixelSize = size > 0 ? size : Math.min(GamePanelX / X, GamePanelY / Y);
		pixelSize = size > 0 ? size : Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));

		
		// Inicjalizacja buforów fali
        previous = new double[x_dim][y_dim];
        current  = new double[x_dim][y_dim];
        next     = new double[x_dim][y_dim];
		
		//Inicjalizacja PanelImage
		//naszym panelem bedzie obrazek, powinno to rozwiązać problemy z pamięcią
		imgW = x_dim*pixelSize; //szerokosc obrazka
		imgH = y_dim*pixelSize; //wysokosc obrazka
		panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
		clearPanel();
		
		
		setMinimumSize(new Dimension(imgW, imgH));
		setPreferredSize(new Dimension(imgW, imgH));
	    setBackground(Color.WHITE);
	    setOpaque(true);
        
     // Inicjalizacja Pixel grid na biało
        pixelGrid = new ArrayList<>();
        for (int y = 0; y < y_dim; y++) {
        	pixelRow = new ArrayList<>();
            for (int x = 0; x < x_dim; x++) {
                Pixel p = new Pixel(x, y, Color.WHITE, 3, x, y);
                pixelRow.add(p);
                paintPxl(x, y, p.getClr());
            }
            pixelGrid.add(pixelRow);
        }
		
        
        // Obsługa kliknięć – dodaj źródło impulsu
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	if (!addEnabled) return;
            	if (sources.size() >= maxSources) return; //warunek na liczbe zrodel
            	
            	//poprawne mapowanie kliknięcia
                int gx = e.getX() * x_dim / getWidth();
                int gy = e.getY() * y_dim / getHeight();
                
                if (gx >= 0 && gx < x_dim && gy >= 0 && gy < y_dim) {
                    Source s = new Source(gx, gy);
                    sources.add(s);
                    addImpulse(s, FunctAndConst.amplitude);
                }
            }
        });

        new Thread(this).start();
		this.repaint();
	}
	
	 // Wyczyść panelImage do białego tła
    private void clearPanel() {
        Graphics2D g2 = panelImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0 , 0, imgW, imgH);
        g2.dispose();
    }
    
    // Początkowy impuls fali w promieniu r
    private void addImpulse(Source s, double amplitude) {
    	
        int r = 3;
        long t = System.nanoTime();
        double w = amplitude * Math.sin(2 * Math.PI * freq * (t * 1e-9));
        
        for(int dx = -r; dx <= r; dx++) 
        	for(int dy = -r; dy <= r; dy++){
        		
        		//delta
	            int px = s.x + dx; 
	            int py = s.y + dy;
	            
	            if(px > 0 && px < x_dim - 1 && py > 0 && py <y_dim - 1 && dx * dx + dy * dy <= r * r) {
	                current[px][py] += w;
	                previous[px][py] += w;
            }
        }
    }
    
    
    // Aktualizacja symulacji fali
    private void updateSimulation() {
        double dx = 0.01;
        double dt = 1e-5;
        final double baseCoeff = (FunctAndConst.c * dt / dx) * (FunctAndConst.c * dt / dx);
        
        if(baseCoeff > 0.5) return;
        
       /* // weź realny rozmiar siatki:
        int rows = pixelGrid.size();                // liczba wierszy = y_dim
        if (rows == 0) return;
        int cols = pixelGrid.get(0).size();         // liczba kolumn = x_dim

        // upewnij się, że buffory mają te same wymiary:
        cols = Math.min(cols, current.length);
        rows = Math.min(rows, current[0].length);

        // iteruj tylko do rows-1, cols-1
        for (int y = 1; y < rows - 1; y++) {
            for (int x = 1; x < cols - 1; x++) {
                Pixel px = pixelGrid.get(y).get(x);
                double k = px.getTSM();
                double coeffLocal = baseCoeff * k * k;

	            double laplasjan = current[x-1][y] + current[x+1][y]
	                       + current[x][y-1] + current[x][y+1] - 4 * current[x][y];
	            
	            next[x][y] = 2 * current[x][y] - previous[x][y] + coeffLocal * laplasjan;
	            next[x][y] *= pixelGrid.get(y).get(x).getDampLocal();
            }
        }*/

        int cols = current.length;
        int rows = current[0].length;

        // 1) obliczenia wnętrza
        for (int x = 1; x < cols - 1; x++) {
            for (int y = 1; y < rows - 1; y++) {
                Pixel px = pixelGrid.get(y).get(x);
                double k = px.getTSM();
                double coeffLocal = baseCoeff * k * k;
                double laplasjan =
                      current[x-1][y] + current[x+1][y]
                    + current[x][y-1] + current[x][y+1]
                    - 4*current[x][y];
                next[x][y] = 2*current[x][y] - previous[x][y] + coeffLocal*laplasjan;
                next[x][y] *= damping;  // lub px.getDampLocal()
            }
        }
    
        //  Absorbing boundary zero: fala ginie na krawędzi
        for (int y = 0; y < rows; y++) {
            next[0       ][y] = current[1       ][y];  // lewy brzeg = druga kolumna
            next[cols-1  ][y] = current[cols-2  ][y];  // prawy brzeg = przedostatnia kolumna
        }
        for (int x = 0; x < cols; x++) {
            next[x][0       ] = current[x][1       ];  // górny brzeg = drugi wiersz
            next[x][rows-1  ] = current[x][rows-2  ];  // dolny brzeg = przedostatni wiersz
        }

        double[][] tmp = previous; 
        previous = current; 
        current = next; 
        next = tmp;
    }
    
    

    // Aktualizacja kolorów pixel
    private void updatePixels() {
        //clearPanel();
        
    	for (int y = 0; y < pixelGrid.size(); y++) {
            List<Pixel> row = pixelGrid.get(y);
            for (int x = 0; x < row.size(); x++) {
                double v = current[x][y];
                float amp = (float) Math.min(1.0, Math.abs(v) * FunctAndConst.brightnessFactor / 255.0);
                
                Pixel p = pixelGrid.get(y).get(x);
                Color base = p.getClr();
                float[] hsb  = Color.RGBtoHSB(base.getRed(),
                        base.getGreen(),
                        base.getBlue(), null);

             // przy dodatniej amplitudzie rozjaśniamy, przy ujemnej ściemniamy
                hsb[2] = Math.max(0f, Math.min(1f,
                         hsb[2] + (v >= 0 ? amp : -amp)));

                Color shaded = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                p.setClr(shaded);
                paintPxl(x, y, shaded);
                /*
                if (!FunctAndConst.isTerrain(p.getClr())) {
                    p.setClr(new Color(shade, shade, shade));
                    paintPxl(x, y, p.getClr());
                }
*/
                //p.setClr(new Color(shade, shade, shade));
                //paintPxl(x, y, p.getClr());
                
                if (pixelGrid == null || pixelGrid.size() != y_dim || pixelGrid.get(0).size() != x_dim) {
                    System.err.println("PixelGrid not initialized or invalid dimensions. Skipping update.");
                    return;
                }

                
                //pixelGrid.get(y).get(x).setClr(new Color(shade, shade, shade));
            }
        }

        
        /*
        for(int x = 0; x < x_dim; x++) 
        	for(int y = 0; y < y_dim; y++){
            double v = current[x][y];
            int shade = 255 - (int)(FunctAndConst.brightnessFactor*Math.abs(v));
            shade = Math.max(0, Math.min(255, shade));
            Pixel p = pixelGrid.get(y).get(x);
            p.setClr(new Color(shade,shade,shade));
            paintPxl(x,y,p.getClr());
        }
        */
    }

    // Rysowanie jednego pixela na panelImage
    public void paintPxl(int gx, int gy, Color c) {
        Graphics2D g2 = panelImage.createGraphics();
        g2.setColor(c);
        g2.fillRect(gx*pixelSize, gy*pixelSize, pixelSize, pixelSize);
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(panelImage, 0,0, getWidth(), getHeight(), null);
    }

    @Override
    public void run() {
        long last=System.currentTimeMillis();
        while(true) {
        	if (simRunning) {
        		updateSimulation();
                updatePixels();
                long now = System.currentTimeMillis();
                if(now-last > 50){ 
                 	for(Source s: sources) addImpulse(s,5.0); 
                 	last=now; }	
        	}
           
            repaint();
            try{Thread.sleep(16);}catch(Exception e){}
        }
    }

  
 // w SimulationPanel.java, w klasie SimulationPanel:

    /**
     * Przywraca panel i całą symulację do stanu początkowego:
     * - czyści bufor fali
     * - czyści źródła
     * - maluje wszystkie pixele na biało
     */
    public void resetState() {
        // czyszczenie bufory
        for (int i = 0; i < x_dim; i++) {
            Arrays.fill(previous[i], 0);
            Arrays.fill(current[i],  0);
            Arrays.fill(next[i],     0);
        }
        
        panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        clearPanel();

        // pixelGrid na biało
        pixelGrid.clear();
        for (int y = 0; y < y_dim; y++) {
            List<Pixel> row = new ArrayList<>();
            for (int x = 0; x < x_dim; x++) {
                Pixel p = new Pixel(x, y, Color.WHITE, 3, x, y);
                row.add(p);
                // od razu nanieś biały piksel
                paintPxl(x, y, Color.WHITE);
            }
            pixelGrid.add(row);
        }
        
        sources.clear();
        this.generate = false;
        revalidate();
        repaint();
    }

    
    	public void resize(int GamePanelX, int GamePanelY) 
    	{
        pixelSize = Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));
        imgW = x_dim * pixelSize;
        imgH = y_dim * pixelSize;
        
        if (pixelGrid == null || pixelGrid.size() != y_dim || pixelGrid.get(0).size() != x_dim) {
            System.err.println("Pixel grid is not properly initialized or does not match dimensions.");
            return;
        }


        if (imgW > 0 && imgH > 0) {
            panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = panelImage.createGraphics();
            g2d.setColor(new Color(255, 228, 181));
            g2d.fillRect(0, 0, imgW, imgH);
            g2d.dispose();

            for (int y = 0; y < y_dim; y++) {
                for (int x = 0; x < x_dim; x++) {
                    Pixel p = pixelGrid.get(y).get(x);
                    paintPxl(x, y, p.getClr());
                }
            }

            setPreferredSize(new Dimension(GamePanelX, GamePanelY));
            revalidate();
            repaint();
        } else {
            System.err.println("Resize failed due to zero dimension: " + imgW + "x" + imgH);
        }
    }

	
}
