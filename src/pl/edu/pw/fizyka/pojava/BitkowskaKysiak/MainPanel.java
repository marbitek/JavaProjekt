package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;


import java.awt.CardLayout;
import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		
		
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		
	    welcomePanel = new WelcomePanel();
	    homeContainer.add(welcomePanel, "Welcome Panel");
	    //welcomePanel.start.addActionListener(e -> card.show(homeContainer, "Game Panel"));
	    
	    //baza = new BazaDanych();
	    
        new SwingWorker<BazaDanych, Void>() {
            @Override
            protected BazaDanych doInBackground() throws Exception {
                // tu dopiero tworzymy połączenie (ciężka operacja)
                return new BazaDanych();
            }
            @Override
            protected void done() {
                try {
                    baza = get();                   // wynik new BazaDanych()
                    welcomePanel.start.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        MainPanel.this,
                        "Nie udało się połączyć z bazą:\n" + ex.getMessage(),
                        "Błąd połączenia",
                        JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            }
        }.execute();
	    
	    welcomePanel.start.addActionListener(e -> {
	        String nick = welcomePanel.insertNick.getText().trim();
	        
	        String nickFinal = nick.isEmpty() ? "Stranger" : nick;
	      
	        welcomePanel.start.setEnabled(false);

	        new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    baza.addUser(nickFinal);
                    return null;
                }
                @Override
                protected void done() {
                    welcomePanel.start.setEnabled(true);
                    // komunikat i przejście na panel gry
                    String info = String.format("""
                        Welcome %s in Dune Harmonics!

                        In this game you can:
                        • Chose the type of field,
                        • Chose the number of sources,
                        • Set the frequency of the sources,
                        • Observe the data.

                        Click OK to start the game!
                             """, nickFinal);
                    JOptionPane.showMessageDialog(
                        MainPanel.this,
                        info,
                        "Instruction",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    card.show(homeContainer, "Game Panel");
                }
            }.execute();
            
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
