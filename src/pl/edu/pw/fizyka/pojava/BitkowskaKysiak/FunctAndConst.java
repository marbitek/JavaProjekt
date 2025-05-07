package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import javax.swing.*;
import java.awt.*;



public class FunctAndConst {
	
	protected static final Color SAND = new Color(237, 201, 175);
	protected static final Color GRANITE = new Color(169, 169, 169);
	protected static final Color LIMESTONE = new Color(245, 245, 220);
	protected static final Color AIR = Color.white;
	
	
	protected static final double c = 343.0;   // prędkość dźwięku m/s
    protected static int amplitude = 20; //amplituda fali
    protected static final double brightnessFactor = 255.0 / amplitude;

	
    
	//MK+
    public static boolean isTerrain(Color c)
    {
    	return (c == GRANITE || c == LIMESTONE || c == SAND);
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
    //KM-
    
    
    
    
}
