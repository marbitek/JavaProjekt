package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainPanel extends JFrame implements Runnable {

	//MB+
	private static final long serialVersionUID = 1L;

	static JPanel homePanel, centre;
	static JPanel homeContainer;
	static WelcomePanel welcomePanel;
	static GamePanel gamePanel;
	static CardLayout card;
	
	public static void main(String[] args) {
		//MainPanel implementuje runnable więc invoke odrazu go uruchamia
		 SwingUtilities.invokeLater(new MainPanel());
		 }
			
	//konstruktor w metodzie run
	@Override
	public void run() 
	{
		JFrame frame = new JFrame("Dune Harmonics");	
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		//homeContainer.setMaximumSize(new Dimension(900, 600));
		
	    welcomePanel = new WelcomePanel();
	    homeContainer.add(welcomePanel, "Welcome Panel");
	    
	    //start->jdialog->GamePanel
	    welcomePanel.start.addActionListener(e -> {
	        String nick = WelcomePanel.insertNick.getText().trim();
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

	            Click OK to start the game!
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
	    
	    //gamePanel.back.addActionListener(e -> card.show(homeContainer, "Welcome Panel"));
	    //MK - modyfikuje zeby slidery w gamepanel wracaly do poczatkowych wartosci po powrocie do poczatku
	    gamePanel.back.addActionListener(e -> {
	        card.show(homeContainer, "Welcome Panel");
	        WelcomePanel.insertNick.setText("");
	        gamePanel.slider.setValue(0);
	        gamePanel.powerSlider.setValue(50);
	    });


	    
	    frame.add(homeContainer);
	    card.show(homeContainer, "Welcome Panel");
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setSize(900, 600);
	    frame.setLocationRelativeTo(null);
	    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //frame.pack();
	    frame.setVisible(true);
	}


	   
	
	//MB-

	
}
