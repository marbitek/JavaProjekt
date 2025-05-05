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
	 * @author Michał Kysiak (głównie na razie)
	 * @author 48533 (troszkę)
	 * 
	 * Klasa odpowiadająca z panel symulacyjny - symulacja fal i generacja terenu.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int x_dim;
	private final int y_dim;
	private int pixelSize;
	
	//siatka pixeli
	private List<List<Pixel>> pixelGrid; //tablice wierszy siatki pixeli
	private List<Pixel> pixelRow; //rządek pixeli do inicjalizacji
		
	//Obrazek panelu 
	private BufferedImage panelImage = null;
	
	
	private int imgW, imgH;
	private Pixel onePxl; 
	private Random rand = new Random(); //do losowania
	protected float R, G, B;
	
	// Lista źródeł fali
    private final List<Source> sources = new ArrayList<>();
	
	 // Buffory stanu fali
    private double[][] previous, current, next;
    private double damping = 0.996;
    private double freq;               // domyślna częstotliwość Hz
    private int maxSources = 1;   // ile w ogóle pozwalamy tworzyć
    
    
    
	
	//for methods tied to terrain generation 
	protected int xModif, yModif, sidestepX, sidestepY, counter;
	protected float terrainChance = (float) 0.35; //chance that a random pixel will be a part of generated terrain
	
	private Pixel[] adjacentPixels; //pixele sąsiadujące z onePxl
	private int adjacentsInNewTerrain; //liczba pixeli w sąsiedztwie należąca do nowego terenu

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
    
    //ustawienia wygaszania
    public void setDamping(double damping) {
    	this.damping = damping;
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
		
		/*pixelGrid = new ArrayList<>();
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(new Color(255, 228, 181));
        g2d.fillRect(0, 0, imgW, imgH);
        g2d.dispose();*/
        
     // Inicjalizacja Pixel grid na biało
        pixelGrid = new ArrayList<>();
        for (int y = 0; y <y_dim; y++) {
        	pixelRow = new ArrayList<>();
            for (int x = 0; x <x_dim; x++) {
                Pixel p = new Pixel(x, y, Color.WHITE, 0, x, y);
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
            	
                int gx = e.getX()/pixelSize;
                int gy = e.getY()/pixelSize;
                if (gx >= 0 && gx <x_dim && gy >= 0 && gy <y_dim) {
                    Source s = new Source(gx, gy);
                    sources.add(s);
                    addImpulse(s, FunctAndConst.amplitude);
                }
            }
        });

        new Thread(this).start();
		this.repaint();
	}
	/*
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting SimulationPanel " + getWidth() + "x" + getHeight());
        g.drawImage(panelImage, 0, 0, null);
        
        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, getWidth(), getHeight(), null); // scale to fit
        }
    }*/
	
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
            int px = s.x + dx, py = s.y + dy;
            if(px > 0 && px < x_dim-1 && py > 0 && py <y_dim-1 && dx * dx + dy * dy <= r * r) {
                current[px][py] += w;
                previous[px][py] += w;
            }
        }
    }
    
    
    // Aktualizacja symulacji fali
    private void updateSimulation() {
        double dx = 0.01;
        double dt = 1e-5;
        double coeff = (FunctAndConst.c * dt / dx) * (FunctAndConst.c * dt / dx);
        
        if(coeff > 0.5) return;
        
        for(int x = 1; x < x_dim-1; x++) 
        	for(int y = 1; y < y_dim-1; y++){
            double laplasjan = current[x-1][y] + current[x+1][y]
                       + current[x][y-1] + current[x][y+1] - 4 * current[x][y];
            next[x][y] = 2 * current[x][y] - previous[x][y] + coeff * laplasjan;
            next[x][y] *= damping;
        }
        double[][] tmp = previous; 
        previous = current; 
        current = next; 
        next = tmp;
    }

    // Aktualizacja kolorów pixel
    private void updatePixels() {
        //clearPanel();
        
        for (int y = 0; y < y_dim; y++) {
            for (int x = 0; x < x_dim; x++) {
                double v = current[x][y];
                int shade = 255 - (int)(FunctAndConst.brightnessFactor * Math.abs(v));
                shade = Math.max(0, Math.min(255, shade));
                
                Pixel p = pixelGrid.get(y).get(x);
                if (!FunctAndConst.isTerrain(p.getClr())) {
                    p.setClr(new Color(shade, shade, shade));
                    paintPxl(x, y, p.getClr());
                }

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
    private void paintPxl(int gx, int gy, Color c) {
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


    //zmienia kolor jednego pixela
    	
   /* public void paintPxl(int Width, int Height, Color c)
    {
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(c);
        g2d.fillRect(Width*pixelSize, Height*pixelSize, pixelSize, pixelSize);
        g2d.dispose();
    }*/
	
    
    //otrzymanie pixela o zadanych koordynatach
	public Pixel getPxl(int x, int y) //get pixel
	{
		return pixelGrid.get(y).get(x);
	}
	
	
	//zwraca siatkę pixeli
	public List<List<Pixel>> getPxlGrid()
	{
		return pixelGrid;
	}
	
	//zwraca podany rząd pixeli
	public List<Pixel> getPxlRow(int i)
	{
		return pixelGrid.get(i);
	}
	
	//zwraca podaną kolumnę pixeli
	public List<Pixel> getPxlCol(int i)
	{
		List<Pixel> column = new ArrayList<>();
		for(int j = 0; j < x_dim; j++)
		{
			onePxl = getPxlRow(j).get(i);
			column.add(onePxl);
		}
		
		return column;
	}
	
	//metoda zwraca pixele sąsiadujące z naszym jedną ścianką, lista w kolejności: górny a potem zgodnie z wskazówkami zegara
	public Pixel[] getNeigbours(Pixel onePxl, int size)
	{
		Pixel[] neighbours = new Pixel[4];
		
		for(int j = 0; j < 4; j++)
		{
			switch(j)
			{
				case 0:
					xModif = 1; yModif=0;
					break;
				case 1:
					xModif = -1; yModif = 0;
					break;
				case 2:
					xModif = 0; yModif = 1;
					break;
				case 3:
					xModif = 0; yModif = -1;
					break;
				default:
					xModif = 0; yModif = 0;
					break;
				
			}
			sidestepX = onePxl.getGX()+xModif;
			sidestepY = onePxl.getGY()+yModif;
				if(legal(sidestepX, sidestepY, size))
				{
					neighbours[j] = getPxl(sidestepX, sidestepY);
				} else neighbours[j] = null;
					
	}
		return neighbours;
	}
	
	
	
	//metoda sprawdza czy pixel o wskazanych koordynatach nie wychodzi poza granice panelu pixeli
	public boolean legal(int x, int y, int size) {
	    return x >= 0 && x < size && y >= 0 && y < size;
	}
	
	//metoda zwraca pixele sąsiadujące z podanym o innym kolorze
	public Pixel[] turnAdjacent(Pixel beginningPixel, double probability, double max)
	{
		Pixel[] adjacents = new Pixel[4];
		if (beginningPixel == null) return new Pixel[0];

		
		for(int j = 0; j < 4; j++)
		{
			switch(j)
			{
				case 0:
					xModif = 1; yModif=0;
					break;
				case 1:
					xModif = -1; yModif = 0;
					break;
				case 2:
					xModif = 0; yModif = 1;
					break;
				case 3:
					xModif = 0; yModif = -1;
					break;
				default:
					xModif = 0; yModif = 0;
					break;
				
			}
			
			sidestepX = beginningPixel.getGX()+xModif;
			sidestepY = beginningPixel.getGY()+yModif;
			
			if(legal(sidestepX,sidestepY, x_dim))
			{	
			onePxl = getPxl(sidestepX, sidestepY);
			
			if (onePxl != null && /*!onePxl.getClr().equals(beginningPixel.getClr()) &&*/ rand.nextDouble() < probability)
			{
				adjacents[j] = onePxl;
			}
			}
		}
		return adjacents;
	}
	
	
	
	/**
	 * Funkcja generująca w losowych miejscach dany typ terenu
	 * @param type
	 * @param clusterNumber
	 * @param clusterMaxSize
	 * @param wsp
	 * @param gridSize
	 * @param generate
	 */
	public void generateTerrain(String type, int clusterNumber, int clusterMaxSize, double wsp, int gridSize, boolean generate) {
	   
		this.generate = generate;
		
		if(generate) {
			System.out.println("Terrain generation called");
			Set<Pixel> newTerrain = new HashSet<>();
		    Set<Pixel> prevIteration = new HashSet<>();
		    Set<Pixel> thisIteration = new HashSet<>();
	
		    int randX, randY, pixelsAdded = 0, pixelsRemoved = 0;
		    Color terrainColor = switch (type) {
		        case "Sand" -> FunctAndConst.SAND;
		        case "Granite" -> FunctAndConst.GRANITE;   
		        case "Limestone" -> FunctAndConst.LIMESTONE;
		        default -> {
		            System.out.println("Unknown terrain type selected.");
		            yield FunctAndConst.SAND;
		        }
		    };
		    
		    
	
		    for (int i = 0; i < clusterNumber; i++) {
		        randX = rand.nextInt(gridSize);
		        randY = rand.nextInt(gridSize);
	
		        Pixel clusterCenter = getPxl(randX, randY);
	
		        newTerrain.add(clusterCenter);
		        prevIteration.clear();
		        prevIteration.add(clusterCenter);
	
		        int counter = 1;
		        boolean stopCondition = false;
	
		        do {
		            for (Pixel px : prevIteration) {   
	
		                Pixel[] adjacent = turnAdjacent(px, 1/Math.sqrt(counter), 1);
		                for (Pixel p : adjacent) {
		                    if (p != null && !newTerrain.contains(p)) {
		                        thisIteration.add(p);
		                    }
		                }
		            }
	
		            boolean empty = thisIteration.isEmpty();
		            newTerrain.addAll(thisIteration);
		            prevIteration = new HashSet<>(thisIteration);
		            thisIteration.clear();
		            counter++;
	
		            if (empty) stopCondition = true;
	
		        } while (!stopCondition);
		    }
		    
			//usuwamy pixele nowego terenu nieposiadające sąsiadów
			//pixele których wszyscy sąsiedzi są nowym terenem stają się pixelami nowego terenu
			for(int y = 0; y<gridSize; y++)
			{
				for(int x = 0; x < gridSize; x++)
				{
					onePxl = getPxl(x,y);
					adjacentPixels = getNeigbours(onePxl, gridSize);
					adjacentsInNewTerrain = 0;
					
					for(Pixel p : adjacentPixels) 
					{
					if(p != null && newTerrain.contains(p)) 
						{
						adjacentsInNewTerrain++;
						}
					}
					
					if(adjacentsInNewTerrain == adjacentPixels.length-1 && !newTerrain.contains(onePxl))
					{
						newTerrain.add(onePxl);
						pixelsAdded++;
					} else if(adjacentsInNewTerrain < 2 && newTerrain.contains(onePxl))
						{
							newTerrain.remove(onePxl);
							pixelsRemoved++;
						}
					
					
			}
			}
	
		    for (Pixel p : newTerrain) {
		        p.setClr(terrainColor);
		        p.setTSM(wsp);
		        paintPxl(p.getGX(), p.getGY(), terrainColor);
		    }
	
		    
	        System.out.println("Pixels added: "+pixelsAdded);
	        System.out.println("Pixels removed: "+pixelsRemoved);
		    revalidate();
		    repaint();
			}
	}

	
	/*
	public void generateTerrain(String type, int clusterNumber, int clusterMaxSize, double wsp, int gridSize)
	{
		List<Pixel> newTerrain = new ArrayList<>(); //kontener na pixele które pod koniec przekształcimy w żądany typ
		List<Pixel> prevIteration = new ArrayList<>();
		List<Pixel> thisIteration = new ArrayList<>();
		
		int randX, randY, pixelsAdded = 0, pixelsRemoved = 0;
		Color terrainColor = SAND;
		switch (type) {
		    case "Sand":
		    	terrainColor = SAND;  // Light sandy color

		        System.out.println("Generating sand terrain...");
		        break;
		    case "Granite":
		        System.out.println("Generating granite terrain...");
		        terrainColor = GRANITE;  // Dark gray, like granite
		        break;
		    case "Limestone":
		        System.out.println("Generating limestone terrain...");
		        terrainColor = LIMESTONE;  // Beige / off-white (like limestone)

		        break;
		    default:
		        System.out.println("Unknown terrain type selected.");
		        break;
		}

		//Pixel[] clusterCentre = new Pixel[clusterNumber];
		Pixel clusterCenter;  // wylosowane punkty z ktorych bedziemy generować skupiska terenu
		//int counter = 1;
		
		
		
		//zmienne dyktujące rośnięcie pojedynczego skupiska terenu

		boolean stopCondition = false;
		int counter = 1;
		
		for(int i = 0; i < clusterNumber; i++) 
		{
			randX = rand.nextInt(gridSize); randY = rand.nextInt(gridSize);;
			//clusterCentre[i] = getPxl(randX, randY);
			clusterCenter = getPxl(randX, randY);
			if (clusterCenter == null) {
			    System.out.println("Warning: clusterCenter was null at (" + randX + ", " + randY + ")");
			    i--; // try again for this cluster
			    continue;
			}
			
			newTerrain.add(clusterCenter);
			
			prevIteration.add(clusterCenter);
			
			counter = 1;
			stopCondition = false;
			
			do {
				for(int j = 0; j < prevIteration.size(); j++)
				{
					//double dynamicProbability = Math.max(0.1, 1.0 / (1 + counter)); // Reduce probability over time, min value 0.1
					//double dynamicProbability = Math.max(0.1, Math.pow(0.9, counter - 1));
					//double k = 10.0;
					//double dynamicProbability = 1.0 - (Math.pow(counter, 1.5) / (Math.pow(counter, 1.5) + k));
					double decayRate = 0.3; // Try 0.2 - 0.4 range
					double dynamicProbability = 1.0 - Math.exp(-decayRate * counter);

					
					Pixel[] adjacent = turnAdjacent(prevIteration.get(j), dynamicProbability, 1);
					for (Pixel p : adjacent) {
						if (p != null && !newTerrain.contains(p) && !thisIteration.contains(p)) {
						    thisIteration.add(p);
						}
						}
				}
				System.out.println("Prev Iteration Size: " + prevIteration.size());
				System.out.println("This Iteration Size: " + thisIteration.size());
				
				newTerrain.addAll( thisIteration);
				
				boolean empty = thisIteration.isEmpty();
				prevIteration = new ArrayList<>(thisIteration);
				
				thisIteration.clear();
				
				counter++;
				if (empty) stopCondition = true;
				
				
				
			} while(!stopCondition);
			

		}
		System.out.println("New Terrain Size: " + newTerrain.size());
		//zmmieniamy teren wszystkich zapisanych pixeli
		for(int m =0; m < newTerrain.size(); m++)
		{

			newTerrain.get(m).setClr(terrainColor);
			
			newTerrain.get(m).setTSM(wsp);
			paintPxl(newTerrain.get(m).getGX(), newTerrain.get(m).getGY(), newTerrain.get(m).getClr());
			
		}
		
    revalidate();
    repaint();
	}
	//pierwszy pomysł
	
	//generowanie wybranego terenu
	public void generateTerrain(String type, int clusterNumber, int clusterMaxSize, double wsp, int gridSize)
	{
		List<Pixel> newTerrain = new ArrayList<>(); //kontener na pixele które pod koniec przekształcimy w żądany typ
		int randX, randY, pixelsAdded = 0, pixelsRemoved = 0;
		Color terrainColor = SAND;
		switch (type) {
		    case "Sand":
		    	terrainColor = SAND;  // Light sandy color

		        System.out.println("Generating sand terrain...");
		        break;
		    case "Granite":
		        System.out.println("Generating granite terrain...");
		        terrainColor = GRANITE;  // Dark gray, like granite
		        break;
		    case "Limestone":
		        System.out.println("Generating limestone terrain...");
		        terrainColor = LIMESTONE;  // Beige / off-white (like limestone)

		        break;
		    default:
		        System.out.println("Unknown terrain type selected.");
		        break;
		}

		//Pixel[] clusterCentre = new Pixel[clusterNumber];
		Pixel clusterCenter;  // wylosowane punkty z ktorych bedziemy generować skupiska terenu
		//int counter = 1;
		
		//zmienne dyktujące rośnięcie pojedynczego skupiska terenu

		boolean stopCondition = false;
		
		for(int i = 0; i < clusterNumber; i++) 
		{
			randX = rand.nextInt(gridSize); randY = rand.nextInt(gridSize);;
			//clusterCentre[i] = getPxl(randX, randY);
			clusterCenter = getPxl(randX, randY);
			newTerrain.add(clusterCenter);
			
			for(int j = 0; j < 4; j++)
			{
				//ustalamy kieunek generacji wzdluz osi x/y od punktu wylosowanego
				switch(j)
				{
					case 0:
						xModif = 1; yModif = 0;
						break;
					case 1:
						xModif = -1; yModif = 0;
						break;
					case 2:
						xModif = 0; yModif = 1;
						break;
					case 3:
						xModif = 0; yModif = -1;
						break;
					default:
						xModif = 0; yModif = 0;
						break;
					
				}
				
				sidestepX = clusterCenter.getGX()+xModif;
				sidestepY = clusterCenter.getGY()+yModif;
				counter = 1;
				if(legal(sidestepX, sidestepY, gridSize))
				{
				//generacja wzdluz konkretnej osi
				//for(int counter = 1; stopCondition = true; counter++)
				while(!stopCondition && counter < clusterMaxSize)
				{
					sidestepX = clusterCenter.getGX()+xModif*counter;
					sidestepY = clusterCenter.getGY()+yModif*counter;
					
					if(!legal(sidestepX, sidestepY, gridSize)) break;
					onePxl = getPxl(sidestepX, sidestepY);
					newTerrain.add(onePxl);
					//onePxl.setClr(terrainColor);
					
					//System.out.println(counter/clusterMaxSize);
					
					stopCondition = (rand.nextFloat(1)<=Math.pow((counter/(double)clusterMaxSize), 4)); //średnia odległość na którą pociągnie to 3/4 maxa
					counter++;
					if(counter == 5) stopCondition = false;
				}
				
				stopCondition = false;
				}
			}
			
			//generacja losowych punktów w obszarze 
			for(int x = -clusterMaxSize; x <= clusterMaxSize; x++)
			{
				
				for(int y = -clusterMaxSize; y <= clusterMaxSize; y++)
				{
					sidestepX = clusterCenter.getGX() + x;
					sidestepY = clusterCenter.getGY() + y;
					
					//sprawdzamy czy pixel nie wychodzi poza skalę
					if(legal(sidestepX, sidestepY, gridSize))
					{
						onePxl = getPxl(sidestepX, sidestepY);
						
						
						
						
						//sprawdzamy czy pixel już nie jest częścią wygenerowanego terenu
						//i losulemy czy będzie dp niego należeć 
						if((!newTerrain.contains(onePxl))&&(rand.nextFloat(1)<=terrainChance))
						{
							newTerrain.add(onePxl);
							//onePxl.setClr(terrainColor);
						}
					}
				}
				
			}
			

			};
			
			//usuwamy pixele nowego terenu nieposiadające sąsiadów
			//pixele których wszyscy sąsiedzi są nowym terenem stają się pixelami nowego terenu
			for(int y = 0; y<gridSize; y++)
			{
				for(int x = 0; x < gridSize; x++)
				{
					onePxl = getPxl(x,y);
					adjacentPixels = getNeigbours(onePxl, gridSize);
					adjacentsInNewTerrain = 0;
					
					for(Pixel p : adjacentPixels) 
					{
					if(p != null && newTerrain.contains(p)) 
						{
						adjacentsInNewTerrain++;
						}
					}
					
					if(adjacentsInNewTerrain == adjacentPixels.length && !newTerrain.contains(onePxl))
					{
						newTerrain.add(onePxl);
						pixelsAdded++;
					} else if(adjacentsInNewTerrain == 0 && newTerrain.contains(onePxl))
						{
							newTerrain.remove(onePxl);
							pixelsRemoved++;
						}
					
					
			}
			}
			
			//zmmieniamy teren wszystkich zapisanych pixeli
			for(int m =0; m < newTerrain.size(); m++)
			{
				newTerrain.get(m).setClr(terrainColor);
				newTerrain.get(m).setTSM(wsp);
				paintPxl(newTerrain.get(m).getGX(), newTerrain.get(m).getGY(), newTerrain.get(m).getClr());
				
			}
			
        revalidate();
        repaint();
        
        System.out.println("Pixels added: "+pixelsAdded);
        System.out.println("Pixels removed: "+pixelsRemoved);
        
        
			}
	*/
	
	
}
