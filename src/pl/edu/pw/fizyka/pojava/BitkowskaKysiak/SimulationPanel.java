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
import java.util.*;
import java.util.List;
import java.util.ArrayList;

//JPanel w GamePanel'u w ktorym odbywac sie bedzie symulacja
public class SimulationPanel extends JPanel
{
	int x_dim = 100;
	int y_dim = 100;
	Pixel[][] pixels; 
	List<List<Pixel>> pixelGrid; //tablice wierszy siatki pixeli
	List<Pixel> pixelrow;
	int pixelSize = 2;
	
	public SimulationPanel()
	{
		this.setMinimumSize(new Dimension(x_dim*pixelSize, y_dim*pixelSize));
		List<List<Pixel>> pixelGrid = new ArrayList<>();
		
		for(int y = 0; y < y_dim/pixelSize; y++)
		{
			pixelrow = new ArrayList<>();
				for(int x = 0; x < x_dim/pixelSize; x++)
				{
					int X = x * pixelSize;
		            int Y = y * pixelSize;
		            pixelrow.add(new Pixel(X, Y));
				}
		}
		
		/*
		pixels = new Pixel[x_dim][y_dim];
		
		
		for(int x = 0; x < x_dim; x++) 
		{
			for(int y = 0; y < y_dim; y++)
			{
				pixels[x][y] = new Pixel(x, y);
			};
		};
		*/
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw each pixel in the grid
        for (int x = 0; x < x_dim; x++) {
            for (int y = 0; y < y_dim; y++) {
                pixels[x][y].draw(g, pixelSize);
            }
        }
    }

    // Method to change the color of a specific pixel
    /*
    public void setPixelColor(int x, int y, Color color) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            grid[y][x].setColor(color);
            repaint(); // Repaint the panel to reflect the change
        }
    }
    */
}
