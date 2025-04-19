package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

//JPanel w GamePanel'u w ktorym odbywac sie bedzie symulacja
public class SimulationPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
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
	
	public SimulationPanel(int X, int Y, int size, int GamePanelX, int GamePanelY)
	{
		x_dim = X;
		y_dim = Y;
		//pixelSize = size;
		//pixelSize = (GamePanelX/X);
		//pixelSize = size > 0 ? size : Math.min(GamePanelX / X, GamePanelY / Y);
		pixelSize = size > 0 ? size : Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));

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

        setMinimumSize(new Dimension(imgW, imgH));
        setPreferredSize(new Dimension(GamePanelX, GamePanelY));
		
		for(int y = 0; y<y_dim; y++)
		{
			pixelrow = new ArrayList<>();
			for(int x = 0; x < x_dim; x++)
			{
				onePxl = new Pixel(pixelSize, pixelSize, new Color(
						rand.nextInt(256),rand.nextInt(256),rand.nextInt(256)
						), 0);
				
				//this.add(onePxl);
				pixelrow.add(onePxl);
				this.paintPxl(x, y, onePxl.getClr());
			}
			pixelGrid.add(pixelrow);
		}
		this.repaint();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting SimulationPanel " + getWidth() + "x" + getHeight());
        g.drawImage(panelImage, 0, 0, null);
        
        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, getWidth(), getHeight(), null); // scale to fit
        }
    }
    
    	public void resize(int GamePanelX, int GamePanelY) {
        pixelSize = Math.max(1, Math.min(GamePanelX / x_dim, GamePanelY / y_dim));
        imgW = x_dim * pixelSize;
        imgH = y_dim * pixelSize;

        if (imgW > 0 && imgH > 0) {
            panelImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = panelImage.createGraphics();
            g2d.setColor(new Color(255, 228, 181));
            g2d.fillRect(0, 0, imgW, imgH);
            g2d.dispose();

            for (int y = 0; y < y_dim; y++) {
                for (int x = 0; x < x_dim; x++) {
                    Pixel p = pixelGrid.get(y).get(x);
                    paintPxl(x, y, p.getClr());
                }
            }

            setPreferredSize(new Dimension(GamePanelX, GamePanelY));
            revalidate();
            repaint();
        } else {
            System.err.println("Resize failed due to zero dimension: " + imgW + "x" + imgH);
        }
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
