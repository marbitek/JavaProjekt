package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MainPanel extends JFrame {

	private static final long serialVersionUID = 1L;

	static JPanel homePanel, centre;
	static JPanel homeContainer;
	static JPanel welcomePanel;
	static CardLayout card;
	JButton startButton;
	JButton backButton;
	JLabel welcome, nick;
	JTextArea username;
	
	
	
	public MainPanel()  {
		JFrame frame = new JFrame("Dune Harmonics");
		
		
		startButton = new JButton("Start");
		backButton = new JButton("Back");
		
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		
	    homePanel = new JPanel(new BorderLayout());
	    homePanel.setBackground(Color.white);
	    homePanel.add(startButton, BorderLayout.SOUTH);
	    welcome = new JLabel("Welcome to Dune Harmonics!");
	    homePanel.add(welcome, BorderLayout.PAGE_START);
	    nick = new JLabel("Please insert you nick");
	    username = new JTextArea();
	    centre = new JPanel();
	    centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
	    homePanel.add(centre, BorderLayout.CENTER);
	    centre.add(nick);
	    centre.add(username);
	    
	    
	    
	    
	    

	    homeContainer.add(homePanel, "Home");

	    welcomePanel = new JPanel();
	    welcomePanel.setBackground(Color.green);
	    welcomePanel.add(backButton);

	    homeContainer.add(welcomePanel, "Welcome Panel");

	    startButton.addActionListener(e -> card.show(homeContainer, "Welcome Panel"));
	    backButton.addActionListener(e -> card.show(homeContainer, "Home"));

	    frame.add(homeContainer);
	    card.show(homeContainer, "Home");
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setSize(640, 480);
	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //frame.pack();
	    frame.setVisible(true);
	}

	public static void main(String[] args)
	{
	    SwingUtilities.invokeLater(MainPanel::new);
	}
	
		
		
		
		
		
		

}
