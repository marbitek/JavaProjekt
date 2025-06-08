package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import java.awt.CardLayout;
import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainPanel extends JFrame {

	private static final long serialVersionUID = 12L;

	static JPanel homePanel, centre;
	static JPanel homeContainer;
	static WelcomePanel welcomePanel;
	static GamePanel gamePanel;
	static CardLayout card;
	static BazaDanych baza;
	
	public MainPanel() throws SQLException  
	{
		JFrame frame = new JFrame("Dune Harmonics");	
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		
	    welcomePanel = new WelcomePanel();
	    homeContainer.add(welcomePanel, "Welcome Panel");
	    //welcomePanel.start.addActionListener(e -> card.show(homeContainer, "Game Panel"));
	    
	    baza = new BazaDanych();
	    
	    welcomePanel.start.addActionListener(e -> {
	        String nick = welcomePanel.insertNick.getText().trim();
	        if (nick.isEmpty()) 
	        {
	            nick = "Stranger";
	        }

	        String info = String.format("""
	            Welcome %s in Dune Harmonics!
	            
	            In this game you can:
	            • Chose the type of field,
	            • Chose the number of sources,
	            • Set the frequency of the sources,
	            • Observe the data.

	            Click OK to start the game!
	                 """, nick);


	        javax.swing.JOptionPane.showMessageDialog(
	            homeContainer,
	            info,
	            "Instruction",
	            javax.swing.JOptionPane.INFORMATION_MESSAGE
	        );
	        
	        try {
				baza.addUser(nick);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	        card.show(homeContainer, "Game Panel");
	    });

	    
	    gamePanel = new GamePanel();
	    homeContainer.add(gamePanel, "Game Panel");

	    //MK - modyfikuje zeby slidery w gamepanel wracaly do poczatkowych wartosci po powrocie do poczatku
	    gamePanel.back.addActionListener(e -> {
	        card.show(homeContainer, "Welcome Panel");
	        WelcomePanel.insertNick.setText("");
	        gamePanel.reset.doClick(); //aktywacja resetu przy powrocie
	        javax.swing.JOptionPane.showMessageDialog(
		            this,
		            baza.listOfUsers(),
		            "Users",
		            javax.swing.JOptionPane.INFORMATION_MESSAGE
		        );});
	    
	    frame.add(homeContainer);
	    card.show(homeContainer, "Welcome Panel");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(900, 600);
	    frame.setLocationRelativeTo(null);
	    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //frame.pack();
	    frame.setVisible(true);
	}

	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
			try {
				new MainPanel();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
