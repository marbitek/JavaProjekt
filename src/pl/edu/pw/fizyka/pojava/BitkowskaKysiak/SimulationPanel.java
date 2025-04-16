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
	Random rand = new Random();
	float R, G, B;
	BufferedImage panelImage = null;
	
	public SimulationPanel(int X, int Y, int size)
	{
		x_dim = X;
		y_dim = Y;
		pixelSize = size;
		
		//this.setLayout(new GridLayout(x_dim,y_dim));
		pixelGrid = new ArrayList<>();
		imgW = x_dim*pixelSize;
		imgH = y_dim*pixelSize;
		
		//naszym panelem bedzie obrazek, powinno to rozwiązać problemy z pamięcią
		//przy torzeniu 10000 paneli XD
		panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(new Color(255, 228, 181));
        g2d.fillRect(0, 0, imgW, imgH);
        g2d.dispose();

        setPreferredSize(new Dimension(imgW, imgH));
		
		for(int y = 0; y<y_dim; y++)
		{
			pixelrow = new ArrayList<>();
			for(int x = 0; x < x_dim; x++)
			{
				onePxl = new Pixel(pixelSize, pixelSize, new Color(
						rand.nextInt(),rand.nextInt(),rand.nextInt()
						), 0);
				
				//this.add(onePxl);
				pixelrow.add(onePxl);
				this.paintPxl(onePxl.getX(), onePxl.getY(), onePxl.getClr());
			}
			pixelGrid.add(pixelrow);
		}
		this.repaint();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(panelImage, 0, 0, null);
    }
    
    public void paintPxl(int Width, int Height, Color c)
    {
        Graphics2D g2d = panelImage.createGraphics();
        g2d.setColor(c);
        g2d.fillRect(Width*pixelSize, Height*pixelSize, pixelSize, pixelSize);
        g2d.dispose();
    }
	
	public Pixel getPxl(int x, int y) //get pixel
	{
		Pixel p = pixelGrid.get(y).get(x);
		return p;
	}
}
