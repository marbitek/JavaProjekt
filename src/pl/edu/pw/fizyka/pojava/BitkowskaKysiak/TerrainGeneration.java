package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TerrainGeneration {
	
	/**
	 * @author Michał Kysiak
	 */

	  	private final SimulationPanel panel;
		//for methods tied to terrain generation 
		protected int xModif, yModif, sidestepX, sidestepY, counter, n = 5;
		protected double a = 1;
		
		private Pixel[] adjacentPixels; //pixele sąsiadujące z onePxl
		private int adjacentsInNewTerrain; //liczba pixeli w sąsiedztwie należąca do nowego terenu
		
		private Pixel onePxl; 
		private Random rand = new Random(); //do losowania

		
	    public TerrainGeneration(SimulationPanel panel) {
	        this.panel = panel;
	    }
	    
	    
	    /**
		 * Funkcja generująca w losowych miejscach dany typ terenu
		 * @param type
		 * @param clusterNumber
		 * @param clusterSizeParameter
		 * @param gridSize
		 * @param generate
		 */
		public void generateTerrain(String type, int clusterNumber, double clusterSizeParameter, int gridSize, boolean generate, double parameterReduction, int offshoots) {
			
			
			
			if(generate) {
				System.out.println("Terrain generation called");
				Set<Pixel> newTerrain = new HashSet<>();
			    Set<Pixel> prevIteration = new HashSet<>();
			    Set<Pixel> thisIteration = new HashSet<>();
			    List<Pixel> periphery = new ArrayList<>();
			    List<Pixel> subSources = new ArrayList<>();
		
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
			    	a = clusterSizeParameter;
			    	
			        randX = rand.nextInt(gridSize);
			        randY = rand.nextInt(gridSize);
		
			        Pixel clusterCenter = panel.pixelGrid.get(randY).get(randX);
		
			        newTerrain.add(clusterCenter);
			        prevIteration.clear();
			        prevIteration.add(clusterCenter);
		
			        int counter = 1;
			        boolean stopCondition = false;
		

			        	
			        
			        do {
			            for (Pixel px : prevIteration) {   
		
			                Pixel[] adjacent = turnAdjacent(px, a/Math.sqrt(counter), 1);
			                for (Pixel p : adjacent) {
			                    if (p != null && !newTerrain.contains(p)) {
			                        thisIteration.add(p);
			                    }
			                }
			            }
		
			            boolean empty = thisIteration.isEmpty();
			            if (empty) 
			            	{
			            		stopCondition = true;
			            		periphery = new ArrayList<>(prevIteration);
			            		if (periphery.isEmpty()) continue;
			            	}
			            newTerrain.addAll(thisIteration);
			            prevIteration = new HashSet<>(thisIteration);
			            thisIteration.clear();
			            counter++;
		
			            
		
			        } while (!stopCondition);
			        
			        a -= parameterReduction;
			        
			        if(a > 0)
			        {
			        for(int u = 0; u < offshoots; u++)
			        {
			        	if (periphery.isEmpty()) continue;
			        	onePxl = periphery.get(rand.nextInt(periphery.size()));
				        prevIteration.clear();
				        prevIteration.add(onePxl);
				        stopCondition = false;
				        counter = 1;
				        
				        do {
				            for (Pixel px : prevIteration) {
				                Pixel[] adjacent = turnAdjacent(px, a / Math.sqrt(counter), 1);
				                for (Pixel p : adjacent) {
				                    if (p != null && !newTerrain.contains(p)) {
				                        thisIteration.add(p);
				                    }
				                }
				            }

				            if (thisIteration.isEmpty()) {
				                stopCondition = true;
				            }

				            newTerrain.addAll(thisIteration);
				            prevIteration = new HashSet<>(thisIteration);
				            thisIteration.clear();
				            counter++;

				        } while (!stopCondition);
				        
			        }
			        }
			        

			        
			        
			    }
			    
				//usuwamy pixele nowego terenu nieposiadające sąsiadów
				//pixele których wszyscy sąsiedzi są nowym terenem stają się pixelami nowego terenu
				for(int y = 0; y<gridSize; y++) {
					for(int x = 0; x < gridSize; x++) {
						onePxl = panel.pixelGrid.get(y).get(x);
						adjacentPixels = getNeigbours(onePxl, gridSize);
						adjacentsInNewTerrain = 0;
						
						for(Pixel p : adjacentPixels) 
						{
						if(p != null && newTerrain.contains(p)) 
							{
							adjacentsInNewTerrain++;
							}
						}
						
						if(adjacentsInNewTerrain == adjacentPixels.length-1 && !newTerrain.contains(onePxl)) {
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
			        panel.paintPxl(p.getGX(), p.getGY(), terrainColor);
			        p.setClr(terrainColor);
			        
			        int specifier = switch(type) {
			        case "Granite"   -> 1;
			        case "Limestone" -> 2;
			        default          -> 0;  // "Sand"
			    };
			    p.applyTerrainSpec(specifier);
			    }
			    
		        System.out.println("Pixels added: "+pixelsAdded);
		        System.out.println("Pixels removed: "+pixelsRemoved);
			    panel.revalidate();
			    panel.repaint();
				}
		}

		
	    
	
	//zwraca podaną kolumnę pixeli
	public List<Pixel> getPxlCol(int i)
	{
		List<Pixel> column = new ArrayList<>();
		for(int j = 0; j < panel.x_dim; j++)
		{
			onePxl = panel.pixelGrid.get(j).get(i);
			column.add(onePxl);
		}
		
		return column;
	}
	
	//metoda zwraca pixele sąsiadujące z naszym jedną ścianką, lista w kolejności: 
	//górny a potem zgodnie z wskazówkami zegara
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
			sidestepX = onePxl.getGX() + xModif;
			sidestepY = onePxl.getGY() + yModif;
				if(legal(sidestepX, sidestepY, size))
				{
					neighbours[j] = panel.pixelGrid.get(sidestepY).get(sidestepX);
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
			
			sidestepX = beginningPixel.getGX() + xModif;
			sidestepY = beginningPixel.getGY() + yModif;
			
			if(legal(sidestepX,sidestepY, panel.x_dim))
			{	
			onePxl = panel.pixelGrid.get(sidestepY).get(sidestepX);
			
			if (onePxl != null && /*!onePxl.getClr().equals(beginningPixel.getClr()) &&*/ rand.nextDouble() < probability)
			{
				adjacents[j] = onePxl;
			}
			}
		}
		return adjacents;
	}
	
	
	
	

}
