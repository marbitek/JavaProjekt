package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class WelcomePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	
	protected JButton start;
	private JPanel center;
	private JLabel welcome, nickname;
	protected JTextField insertNick;
	static int fontSize = 32;
	
	
	public WelcomePanel() {
		super(new BorderLayout());
		this.setBackground(Color.orange);
		
		start = new JButton("Start");
		
		welcome = new JLabel("Welcome in Dune Harmonics!");
		welcome.setFont(new Font("Helvetica", Font.BOLD, fontSize));
		welcome.setVerticalAlignment(SwingConstants.CENTER);
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		center = new JPanel();
		center.setLayout(new FlowLayout(FlowLayout.CENTER));
		center.setOpaque(false); 
		
		

		BufferedImage dune = null;
		try {
			dune = ImageIO.read(new File("src\\dune3.jpg"));
			//dune = ImageIO.read(new File("C:\\Users\\48533\\Desktop\\Studia\\dune2.jpg"));
			//dune = ImageIO.read(new File("C:\\Users\\48533\\Desktop\\Studia\\dune.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel picLabel = new JLabel(new ImageIcon(dune));
		center.add(picLabel);
		insertNick = new JTextField(20);
		insertNick.setPreferredSize(new Dimension(200, 30)); 

		nickname = new JLabel("Please enter your nickname:");
		center.add(nickname);
		center.add(insertNick);

		
		center.add(nickname);
		center.add(insertNick);
		nickname.setAlignmentX(Component.CENTER_ALIGNMENT);
		insertNick.setAlignmentX(Component.CENTER_ALIGNMENT);

		
		this.add(welcome, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
		this.add(start, BorderLayout.SOUTH);
		
		
		 
		
		
		
		
	}


	

	



	public static void main(String[] args) {

	}

}
