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

public class Pixel extends JPanel
{
	Color color = Color.white;
	int x, y;
	
    public Pixel(int x, int y, Color c) 
    {
        this.x = x;
        this.y = y;
        this.color = c;
        this.setBackground(color);
        this.setMinimumSize(new Dimension(x,y));
        this.setPreferredSize(new Dimension(x,y));
    }
    
    //public Color getColor() {return color;}
    
    public void setColor(Color color) 
    {
        this.color = color;
        setBackground(color);
        repaint();
    }
    
    public void draw(Graphics g, int size) 
    {
        g.setColor(color);
        g.fillRect(x * size, y * size, size, size);
    }
}
