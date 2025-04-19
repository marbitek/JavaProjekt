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
	//MB+
	private static final long serialVersionUID = 1L;

	protected JButton start;
	private JPanel center, nickPanel, startPanel;
	private JLabel welcome, nickname;
	protected static JTextField insertNick;
	static int fontSize = 45;
	
	public WelcomePanel() {
		super(new BorderLayout());
		this.setBackground(Color.orange);
		this.setLayout(new BorderLayout());
		
		start = new JButton("Start");
		start.setPreferredSize(new Dimension(100, 45)); 
		start.setMinimumSize(new Dimension(80, 45));
		
		welcome = new JLabel("Welcome to Dune Harmonics!");
		welcome.setFont(new Font("Monotype Corsiva", Font.BOLD, fontSize));
		welcome.setVerticalAlignment(SwingConstants.CENTER);
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		
		center = new JPanel();
		center.setLayout(new BorderLayout()); //MK grid layout powinien zalatwic sprawe
		center.setOpaque(false); 
		//MB-
		
		
		
		//MB+
		BufferedImage dune = null;
		try {
			dune = ImageIO.read(new File("src\\dune3.jpg")); //MK
			//dune = ImageIO.read(new File("C:\\Users\\48533\\Desktop\\Studia\\dune2.jpg"));
			//dune = ImageIO.read(new File("C:\\Users\\48533\\Desktop\\Studia\\dune.jpg"));
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		JLabel picLabel = new JLabel(new ImageIcon(dune));
		center.add(picLabel, BorderLayout.CENTER);  //MK
		insertNick = new JTextField(20);
		insertNick.setPreferredSize(new Dimension(200, 30)); 
		
		nickname = new JLabel("Please enter your nickname:");
		
		//MK+
		nickname.setPreferredSize(new Dimension(165, 20)); 
		nickname.setMinimumSize(new Dimension(165, 20));
		
		nickPanel = new JPanel();
		nickPanel.setLayout(new FlowLayout());
		nickPanel.setBackground(new Color(245, 222, 179));
		
		nickPanel.add(nickname);
		nickPanel.add(insertNick);
		center.add(nickPanel, BorderLayout.SOUTH);
		
		startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout());
		startPanel.setBackground(new Color(245, 222, 179));
		startPanel.add(start);
		
		start.setPreferredSize(new Dimension(120,40)); 
		start.setMinimumSize(new Dimension(120,40));
		start.setFont(new Font("Lucida Handwriting", Font.ITALIC, 24));
		utilityFunctions.buttonStyling(start, new Color(240, 248, 255), new Color(128, 0, 0));
		
		//MK
		nickname.setAlignmentX(Component.CENTER_ALIGNMENT);
		insertNick.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(welcome, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
		this.add(startPanel, BorderLayout.SOUTH); 
	}

	public static void main(String[] args) 
	{
		
	}
//MB-
}
