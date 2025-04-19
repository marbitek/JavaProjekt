package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import javax.swing.*;
import java.awt.*;


public class utilityFunctions extends JFrame {
	
	private static final long serialVersionUID = 1L;

	//MK+
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
