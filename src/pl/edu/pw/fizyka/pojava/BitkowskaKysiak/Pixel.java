package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.*;
import java.util.List;

public class Pixel {
	private Color baseColor;
	private int x, y, gridX = 0, gridY = 0;
	private double terrainSpeedModifier = 1.0; //dla powierza
	protected double dampLocal = 1.0; //dla powietrza
	String[] terrainTypes = {"Sand", "Granite", "Limestone", "Air"};
	String thisTerrainType = terrainTypes[0];
	
    public Pixel(int x, int y, Color c, int terrainSpecificator, int gX, int gY) 
    {
        this.x = x;
        this.y = y;
        this.baseColor = c;
        gridX = gX;
        gridY = gY;
        
        if(terrainSpecificator >= 0 || terrainSpecificator <= 2)
        {
            thisTerrainType = terrainTypes[terrainSpecificator];
        } else {
        	thisTerrainType = terrainTypes[3];  // „Air”
        }
        
        //prędkość rozchodzenia się dzwięku i w danym ośrodku 
        if (thisTerrainType.equals("Sand")) {
            terrainSpeedModifier = 1.2;  // for example, slower wave speed on sand
            setDampLocal(0.996);
        } else if (thisTerrainType.equals("Granite")) {
            terrainSpeedModifier = 1.6;  // faster wave speed on granite
            setDampLocal(0.796);
        } else if (thisTerrainType.equals("Limestone")) {
            terrainSpeedModifier = 1.4;  // moderate wave speed on limestone
            setDampLocal(0.696);
        } else if (thisTerrainType.equals("Air")) {
            terrainSpeedModifier = 1;  // dzwięk w powietrzu
            setDampLocal(1);
        }
    }
    
    public void setDampLocal(double damp) {this.dampLocal = damp;}
    
    public double getDampLocal() { return dampLocal; }

    public void draw(Graphics g, int size) {
        if (thisTerrainType.equals("Sand")) {
            g.setColor(FunctAndConst.SAND);
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
    
    //otrzymanie pixela o zadanych koordynatach
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
