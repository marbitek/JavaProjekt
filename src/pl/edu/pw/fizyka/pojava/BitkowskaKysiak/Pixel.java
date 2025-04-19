package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import java.awt.*;


public class Pixel //extends JPanel
{
	Color color = Color.white;
	int x, y, vx, vy;
	String[] terrainTypes = { "Sand", "Granite", "Limestone"};
	String thisTerrainType = terrainTypes[0];
	
    public Pixel(int x, int y, Color c, int terrainSpecificator) 
    {
        this.x = x;
        this.y = y;
        this.color = c;
        //this.setBackground(color);
        //this.setMinimumSize(new Dimension(x,y));
        //this.setPreferredSize(new Dimension(x,y));
        
        if(terrainSpecificator >= 0 || terrainSpecificator <= 2)
        {
        	thisTerrainType = terrainTypes[terrainSpecificator];
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
    
    public void draw(Graphics g, int size) 
    {
        g.setColor(color);
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

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public Color getClr() {return color;}
    public void setClr(Color c) {color = c;}
}
