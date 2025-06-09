package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.*;
import java.util.List;

public class Pixel {
	
	/**
	 * @author Michał Kysiak - głównie
	 * @author 48533 - drobne poprawki
	 */
	private Color baseColor;
	private int x, y, gridX = 0, gridY = 0;
	private double terrainSpeedModifier = 1.0; //dla powierza
	protected double dampLocal = 1.0; //dla powietrza
	String[] terrainTypes = {"Sand", "Granite", "Limestone"};
	String thisTerrainType = terrainTypes[0];
	private int terrainSpecificator;  
	
	
	/**
	 * konstruktor klasy Pixel
	 * @param x
	 * @param y
	 * @param c
	 * @param terrainSpecificator
	 * @param gX
	 * @param gY
	 */
    public Pixel(int x, int y, Color c, int terrainSpecificator, int gX, int gY) 
    {
        this.x = x;
        this.y = y;
        this.baseColor = c;
        gridX = gX;
        gridY = gY;
        
        if(terrainSpecificator >= 0 && terrainSpecificator <= 2)
        {
            thisTerrainType = terrainTypes[terrainSpecificator];
        } else {
        	thisTerrainType = terrainTypes[0];  // „Piasek”
        }
        
       /* //prędkość rozchodzenia się dzwięku w danym ośrodku i jego tłumienie
        if (thisTerrainType.equals("Sand")) {
            terrainSpeedModifier = 1.2; 
            setDampLocal(0.996);
        } else if (thisTerrainType.equals("Granite")) {
            terrainSpeedModifier = 1.6;  
            setDampLocal(0.796);
        } else if (thisTerrainType.equals("Limestone")) {
            terrainSpeedModifier = 1.4; 
            setDampLocal(0.596);
        }*/
    }
    
    /** Ustawia nowy typ terenu i przelicza fizykę. */
    public void applyTerrainSpec(int newSpec) {
    	this.terrainSpecificator = newSpec;
        this.thisTerrainType = terrainTypes[newSpec];
        // teraz recalc fizyki:
        switch (newSpec) {
            case 0: // Sand
                terrainSpeedModifier = 1.1;
                dampLocal = 0.896;
                break;
            case 1: // Granite
                terrainSpeedModifier = 1.3;
                dampLocal = 0.696;
                break;
            case 2: // Limestone
                terrainSpeedModifier = 1.2;
                dampLocal = 0.796;
                break;
        }
    }

    
    /**
     * Setter wygaszania
     * @param damp
     */
    public void setDampLocal(double damp) {this.dampLocal = damp;}
    
    /**
     * Getter wygaszania
     * @return dampLocal
     */
    public double getDampLocal() { return dampLocal; }

    /**
     * Metoda kolorowania pixela
     * @param g
     * @param size
     */
    public void draw(Graphics g, int size) {
        if (thisTerrainType.equals("Sand")) {
            g.setColor(FunctAndConst.SAND_GROUND);
        } else if (thisTerrainType.equals("Granite")) {
            g.setColor(FunctAndConst.GRANITE);
        } else if (thisTerrainType.equals("Limestone")) {
            g.setColor(FunctAndConst.LIMESTONE);
        }
        g.fillRect(x * size, y * size, size, size);
    }

    public int getX() {return x;}

    public void setX(int x) { this.x = x;}

    public int getY() {return y;}

    public void setY(int y) { this.y = y;}
    
    
    /**
     * otrzymanie pixela o zadanych koordynatach
     * @param x
     * @param y
     * @param pixelGrid
     * @return
     */
  	public Pixel getPxl(int x, int y, List<List<Pixel>> pixelGrid ) {
  		Pixel p = pixelGrid.get(y).get(x);
  		return p;
  	}

    //terrain speed modifier getters and setters
    public double getTSM() {
    	return terrainSpeedModifier;
    }

    public void setTSM(double tsm) {
        this.terrainSpeedModifier = tsm;
    }

    public Color getClr() {return baseColor;}
    
    public void setClr(Color c) {
    	if(FunctAndConst.isTerrain(c)) baseColor = c;
    }
    
    public int getGX() {return gridX;}

    public int getGY() { return gridY;}
}
