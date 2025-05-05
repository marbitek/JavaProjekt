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
import java.util.List;

public class Pixel //extends JPanel
{
	Color color = Color.white;
	private int x, y, vx, vy, gridX = 0, gridY = 0;
	private double terrainSpeedModifier;
	String[] terrainTypes = {"Sand", "Granite", "Limestone"};
	String thisTerrainType = terrainTypes[0];
	
    public Pixel(int x, int y, Color c, int terrainSpecificator, int gX, int gY) 
    {
        this.x = x;
        this.y = y;
        this.color = c;
        gridX = gX;
        gridY = gY;
        //this.setBackground(color);
        //this.setMinimumSize(new Dimension(x,y));
        //this.setPreferredSize(new Dimension(x,y));
        
        if(terrainSpecificator >= 0 || terrainSpecificator <= 2)
        {
            thisTerrainType = terrainTypes[terrainSpecificator];
        }


        
        
        if (thisTerrainType.equals("Sand")) {
            terrainSpeedModifier = 0.5;  // for example, slower wave speed on sand
        } else if (thisTerrainType.equals("Granite")) {
            terrainSpeedModifier = 1.5;  // faster wave speed on granite
        } else if (thisTerrainType.equals("Limestone")) {
            terrainSpeedModifier = 1.2;  // moderate wave speed on limestone
        }
    }
    
    //public Color getColor() {return color;}
    
    
    /*
    public void setColor(Color color) 
    {
        this.color = color;
        setBackground(color);
        repaint();
    }*/
    /*
    public void draw(Graphics g, int size) 
    {
        g.setColor(color);
        g.fillRect(x * size, y * size, size, size);
    }
    */

    public void draw(Graphics g, int size) {
        if (thisTerrainType.equals("Sand")) {
            g.setColor(new Color(255, 255, 100));  // light yellow for sand
        } else if (thisTerrainType.equals("Granite")) {
            g.setColor(new Color(150, 150, 150));  // grey for granite
        } else if (thisTerrainType.equals("Limestone")) {
            g.setColor(new Color(200, 200, 150));  // light gray for limestone
        }
        g.fillRect(x * size, y * size, size, size);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getVx() {
        return vx;
    }

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
    
    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public Color getClr() {return color;}
    public void setClr(Color c) 
    {
    	if(FunctAndConst.isTerrain(c))
    	{
    		color = c;
    	}
    	}
    

public int getGX() {
    return gridX;
}

public int getGY() {
    return gridY;
}
}
