package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.*;
import java.util.List;

public class Pixel {
	private Color baseColor;
	private int x, y, vx, vy, gridX = 0, gridY = 0;
	private double terrainSpeedModifier = 1.0; //dla powierza
	protected double dampLocal = 1.0; //dla powietrza
	String[] terrainTypes = {"Sand", "Granite", "Limestone"};
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
        }
        
        //prędkość rozchodzenia się dzwięku i w danym ośrodku 
        if (thisTerrainType.equals("Sand")) {
            terrainSpeedModifier = 1.2;  // for example, slower wave speed on sand
        } else if (thisTerrainType.equals("Granite")) {
            terrainSpeedModifier = 1.6;  // faster wave speed on granite
        } else if (thisTerrainType.equals("Limestone")) {
            terrainSpeedModifier = 1.4;  // moderate wave speed on limestone
        }
    }
    
    public void setDampLocal(double damp) {
    	this.dampLocal = damp;
    }
    
    public double getDampLocal() {
    	return dampLocal; 
    }

    public void draw(Graphics g, int size) {
        if (thisTerrainType.equals("Sand")) {
            //g.setColor(new Color(255, 255, 100));  // light yellow for sand
            g.setColor(FunctAndConst.SAND);
        } else if (thisTerrainType.equals("Granite")) {
            //g.setColor(new Color(150, 150, 150));  // grey for granite
            g.setColor(FunctAndConst.GRANITE);
        } else if (thisTerrainType.equals("Limestone")) {
            //g.setColor(new Color(200, 200, 150));  // light gray for limestone
            g.setColor(FunctAndConst.LIMESTONE);
        }
        g.fillRect(x * size, y * size, size, size);
    }

    public int getX() {return x;}

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {return y;}

    public void setY(int y) {
        this.y = y;
    }

    public int getVx() { return vx;}

    public void setVx(int vx) {
        this.vx = vx;
    }
    
    //otrzymanie pixela o zadanych koordynatach
  	public Pixel getPxl(int x, int y, List<List<Pixel>> pixelGrid ) //get pixel
  	{
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
    
    public int getVy() { return vy;}

    public void setVy(int vy) {this.vy = vy;}

    public Color getClr() {return baseColor;}
    
    
    public void setClr(Color c) {
    	if(FunctAndConst.isTerrain(c)) baseColor = c;
    }
    
    public int getGX() {return gridX;}

    public int getGY() { return gridY;}
}
