package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import javax.swing.*;
import java.awt.*;
import java.util.Random;



public class FunctAndConst {
	
	protected static final Color SAND_GROUND = new Color(213, 197, 157);
	protected static final Color SAND = new Color(237, 201, 175);
	protected static final Color GRANITE = new Color(169, 169, 169);
	protected static final Color LIMESTONE = new Color(245, 245, 220);
	protected static final Color AIR = Color.white;
	protected static final Color buttonBg = new Color(240, 248, 255);
	protected static final Color buttonFg = new Color(128, 0, 0);
	
	protected static final double c = 343.0 *1.6;   // prędkość dźwięku m/s
    protected static int amplitude = 100; //amplituda fali
    protected static final double brightnessFactor = 255.0 / amplitude;

	
    
	//MK+
    public static boolean isTerrain(Color c)
    {
    	return (c == GRANITE || c == LIMESTONE || c == SAND || c == SAND_GROUND);
    }
    
    public static int[] randomBorderPixel(int x_dim, int y_dim) {
        Random rand = new Random();
        int edge = rand.nextInt(4); 
        int x = 0, y = 0;

        switch (edge) {
            case 0: // Top edge
                x = rand.nextInt(x_dim);
                y = 0;
                break;
            case 1: // Bottom edge
                x = rand.nextInt(x_dim);
                y = y_dim - 1;
                break;
            case 2: // Left edge
                x = 0;
                y = rand.nextInt(y_dim);
                break;
            case 3: // Right edge
                x = x_dim - 1;
                y = rand.nextInt(y_dim);
                break;
        }

        return new int[]{x, y};
    }

    
    public static void buttonStyling(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    public static void disableButton(JButton button) {
        button.setEnabled(false);
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.DARK_GRAY);
    }
    
    public static void enableButton(JButton button, Color background, Color foreground) {
        button.setEnabled(true);
        button.setBackground(background);
        button.setForeground(foreground);
    }

    //KM-
    
    
    
    
}
