package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainPanel extends JFrame {

	private static final long serialVersionUID = 1L;

	static JPanel homePanel, centre;
	static JPanel homeContainer;
	static WelcomePanel welcomePanel;
	static GamePanel gamePanel;
	static CardLayout card;
	
	
	public MainPanel()  {
		JFrame frame = new JFrame("Dune Harmonics");	
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		//homeContainer.setMaximumSize(new Dimension(900, 600));
		
	    welcomePanel = new WelcomePanel();
	    homeContainer.add(welcomePanel, "Welcome Panel");
	    welcomePanel.start.addActionListener(e -> card.show(homeContainer, "Game Panel"));
	    
	    
	    welcomePanel.start.addActionListener(e -> {
	        String nick = welcomePanel.insertNick.getText().trim();
	        if (nick.isEmpty()) {
	            nick = "Stranger";
	        }

	        String info = String.format("""
	            Welcome %s in Dune Harmonics!
	            
	            In this game you can:
	            • Chose the type of field,
	            • Chose the number of sources,
	            • Set the frequency of the sources,
	            • Observe the data.

	            Click OK to start a game!
	            """, nick);

	        javax.swing.JOptionPane.showMessageDialog(
	            homeContainer,
	            info,
	            "Instruction",
	            javax.swing.JOptionPane.INFORMATION_MESSAGE
	        );

	        card.show(homeContainer, "Game Panel");
	    });

	    
	    gamePanel = new GamePanel();
	    homeContainer.add(gamePanel, "Game Panel");
	    gamePanel.back.addActionListener(e -> card.show(homeContainer, "Welcome Panel"));

	    
	    frame.add(homeContainer);
	    card.show(homeContainer, "Welcome Panel");
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setSize(900, 600);
	    frame.setLocationRelativeTo(null);
	    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //frame.pack();
	    frame.setVisible(true);
	}

	public static void main(String[] args)
	{

	    SwingUtilities.invokeLater(MainPanel::new);
	}

}
