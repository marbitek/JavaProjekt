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

//JPanel w GamePanel'u w ktorym odbywac sie bedzie symulacja
public class SimulationPanel extends JPanel
{
	int x_dim = 100;
	int y_dim = 100;
	//Pixel[][] pixels; 
	List<List<Pixel>> pixelGrid; //tablice wierszy siatki pixeli
	List<Pixel> pixelrow;
	int pixelSize = 2;
	Pixel onePxl;
	Random rand = new Random();
	float R, G, B;
	
	public SimulationPanel(int X, int Y, int size)
	{
		x_dim = X;
		y_dim = Y;
		pixelSize = size;
		this.setLayout(new GridLayout(x_dim,y_dim));
		pixelGrid = new ArrayList<>();
		
		for(int y = 0; y<y_dim; y++)
		{
			pixelrow = new ArrayList<>();
			for(int x = 0; x < x_dim; x++)
			{
				R=rand.nextFloat();
				G=rand.nextFloat();
				B=rand.nextFloat();
				onePxl = new Pixel(pixelSize, pixelSize, new Color(R,G,B));
				this.add(onePxl);
				pixelrow.add(onePxl);
			}
			pixelGrid.add(pixelrow);
		}
	}
	
	public Pixel getPixel(int x, int y)
	{
		Pixel p = pixelGrid.get(y).get(x);
		return p;
	}
}
