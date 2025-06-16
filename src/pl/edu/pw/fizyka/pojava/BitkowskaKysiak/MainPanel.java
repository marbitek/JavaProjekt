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
	static protected BazaDanych baza;
	
	public MainPanel() throws SQLException  
	{
		JFrame frame = new JFrame("Dune Harmonics");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        JOptionPane.showMessageDialog(gamePanel,
	            MainPanel.baza.listOfUsers(),
	            "Users",
	            javax.swing.JOptionPane.INFORMATION_MESSAGE
	        );
		        System.exit(0);
		    }
		});
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		
		
		
		card = new CardLayout(5, 5);
		homeContainer = new JPanel(card);
		homeContainer.setBackground(Color.white);
		
	    welcomePanel = new WelcomePanel();
	    homeContainer.add(welcomePanel, "Welcome Panel");
	    
	    
	    //oddzielny wątek do łączenia z bazą żeby nie zacinać gui
        new SwingWorker<BazaDanych, Void>() {
            @Override
            protected BazaDanych doInBackground() throws Exception {
            
            	FunctAndConst.disableButton(welcomePanel.start);
                // tu dopiero tworzymy połączenie
                return new BazaDanych();
            }
            @Override
            protected void done() {
                try {
                    baza = get();//baza danych
                    welcomePanel.start.setEnabled(true);
                    FunctAndConst.enableButton(welcomePanel.start, new Color(240, 248, 255), new Color(128, 0, 0));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        MainPanel.this,"Nie udało się połączyć z bazą:\n" + ex.getMessage(),"Błąd połączenia",
                        JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            }
        }.execute();
	    
	    welcomePanel.start.addActionListener(e -> {
	        String nick = welcomePanel.insertNick.getText().trim();
	        
	        String nickFinal = nick.isEmpty() ? "Stranger" : nick;
	      
	       
	        FunctAndConst.disableButton(welcomePanel.start);

	        new SwingWorker<Void, Void>() {//anonimowa klasa dziedzicząca - używana do działania w tle
                @Override
                protected Void doInBackground() throws Exception {
                    baza.addUser(nickFinal);
                    return null;
                }
                @Override
                protected void done() {
                    
                    FunctAndConst.enableButton(welcomePanel.start, new Color(240, 248, 255), new Color(128, 0, 0));
                    // komunikat i przejście na panel gry
                    String info = String.format("""
                        Welcome %s in Dune Harmonics!

                        In this game you can:
                        • Chose the type of field,
                        • Chose the number of sources,
                        • Set the frequency of the sources,
                        • Set the time of simulation.

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
	    });
	    
	    frame.add(homeContainer);
	    card.show(homeContainer, "Welcome Panel");
	   
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
