package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

//JPanel w GamePanel'u w ktorym odbywac sie bedzie symulacja
public class SimulationPanel extends JPanel
{
	int x_dim = 100;
	int y_dim = 100;
	//Pixel[][] pixels; 
	List<List<Pixel>> pixelGrid; //tablice wierszy siatki pixeli
	List<Pixel> pixelrow;
	int pixelSize = 2, imgW, imgH;
	Pixel onePxl; 
	Random rand = new Random(); //do losowania
	float R, G, B;
	BufferedImage panelImage = null;
	
	//for methods tied to terrain generation 
	int xModif, yModif, sidestepX, sidestepY, counter;
	
	float terrainChance = (float) 0.35; //chance that a random pixel will be a part of generated terrain
	
	Pixel[] adjacentPixels; //pixele sąsiadujące z onePxl
	int adjacentsInNewTerrain; //liczba pixeli w sąsiedztwie należąca do nowego terenu

	Color SAND = new Color(237, 201, 175);
	Color GRANITE = new Color(169, 169, 169);
	Color LIMESTONE = new Color(245, 245, 220);

	

	public SimulationPanel(int X, int Y, int size, int GamePanelX, int GamePanelY)
	{
		x_dim = X;
		y_dim = Y;
		//pixelSize = size;
		//pixelSize = (GamePanelX/X);
		//pixelSize = size > 0 ? size : Math.min(GamePanelX / X, GamePanelY / Y);
		pixelSize = size > 0 ? size : Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));

		//this.setLayout(new GridLayout(x_dim,y_dim));
		pixelGrid = new ArrayList<>();
		imgW = x_dim*pixelSize;
		imgH = y_dim*pixelSize;
		
		//naszym panelem bedzie obrazek, powinno to rozwiązać problemy z pamięcią
		panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(new Color(255, 228, 181));
        g2d.fillRect(0, 0, imgW, imgH);
        g2d.dispose();

        setMinimumSize(new Dimension(imgW, imgH));
        setPreferredSize(new Dimension(GamePanelX, GamePanelY));
		
		for(int y = 0; y<y_dim; y++)
		{
			pixelrow = new ArrayList<>();
			for(int x = 0; x < x_dim; x++)
			{
				onePxl = new Pixel(pixelSize, pixelSize, Color.white/*new Color(
						rand.nextInt(256),rand.nextInt(256),rand.nextInt(256)
						)*/, 0, x, y);
				
				//this.add(onePxl);
				pixelrow.add(onePxl);
				this.paintPxl(x, y, onePxl.getClr());
			}
			pixelGrid.add(pixelrow);
		}
		this.repaint();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting SimulationPanel " + getWidth() + "x" + getHeight());
        g.drawImage(panelImage, 0, 0, null);
        
        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, getWidth(), getHeight(), null); // scale to fit
        }
    }
    
    	public void resize(int GamePanelX, int GamePanelY) 
    	{
        pixelSize = Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));
        imgW = x_dim * pixelSize;
        imgH = y_dim * pixelSize;

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
    public void paintPxl(int Width, int Height, Color c)
    {
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(c);
        g2d.fillRect(Width*pixelSize, Height*pixelSize, pixelSize, pixelSize);
        g2d.dispose();
    }
	
    
    //otrzymanie pixela o zadanych koordynatach
	public Pixel getPxl(int x, int y) //get pixel
	{
		Pixel p = pixelGrid.get(y).get(x);
		return p;
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
	public void generateTerrain(String type, int clusterNumber, int clusterMaxSize, double wsp, int gridSize) {
	    Set<Pixel> newTerrain = new HashSet<>();
	    Set<Pixel> prevIteration = new HashSet<>();
	    Set<Pixel> thisIteration = new HashSet<>();

	    int randX, randY, pixelsAdded = 0, pixelsRemoved = 0;
	    Color terrainColor = switch (type) {
	        case "Sand" -> SAND;
	        case "Granite" -> GRANITE;
	        case "Limestone" -> LIMESTONE;
	        default -> {
	            System.out.println("Unknown terrain type selected.");
	            yield SAND;
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
	        paintPxl(p.getGX(), p.getGY(), p.getClr());
	    }

	    
        System.out.println("Pixels added: "+pixelsAdded);
        System.out.println("Pixels removed: "+pixelsRemoved);
	    revalidate();
	    repaint();
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
