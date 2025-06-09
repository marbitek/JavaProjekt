package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel implements GameInterface
{

	/**
	 * @author 48533
	 * @author Michał Kysiak
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel innerPanel, functional, data, p, p2, p3, p4, p5, p6, pSource, controlPanel, squareWrapper, genPanel;
	protected JButton back, onOff, exit, reset, generate; 
	private String teren[] = {"Sand", "Granite", "Limestone"};
	private String sources[] = {"One source", "Two sources", "Three sources", "Four sources"};
	protected double currentWsp;
	protected static double currentFreq = 500;
	private int numbSource = 1;
	private JComboBox<String> numbSrc, lista;
	private JLabel  field, freq, data1, data2, source, pow;
	protected JSlider slider, powerSlider, reduceSlider, clusterSlider, sizeSlider, offshootsSlider;
	private int size = 500, terrainClusters = 1, offshoots = 10, startPauseCounter = 0;
	private SimulationPanel inner;
	private final TerrainGeneration terrainGen;
	private boolean gen = true; //flaga do generowania terenu
	private boolean on = false;  //flaga do przechowywania stanu
	private double parameterReduction = 2, clusterSizeParameter = 1, number = 0;
	private JTextField tField;
	private Timer countdownTimer;

	
	
	public GamePanel() {
		
		super(new BorderLayout());
		
		//ustalamy renderer z wyśrodkowanym tekstem DLA JList lub JComboBox
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER); // wyśrodkuj poziomo
		
		
		//CONTROL PANEL
		controlPanel = new JPanel(); 
		controlPanel.setLayout(new FlowLayout());
		
		//guzik back
		back = new JButton("Back"); 
		back.setMinimumSize(new Dimension(70, 25));
		
		
		//guzik reset
		reset = new JButton("Reset");
		reset.setMinimumSize(new Dimension(70, 25));
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
		            if ("PAUSE".equals(onOff.getText())) {
		                //onOff.doClick();  
		    			on = !on;
		    			gen = !gen;
		    		    inner.setSimRunning(on); 
		    			
		    		    if(onOff.getText().equals("PAUSE")) inner.pauseSim(true);
		            	onOff.setText("RUN");
		            }
				
		        inner.resetElapsedTime();
		        data1.setText("Time elapsed: 0.00 s");
		        tField.setEnabled(true);
		        tField.setText("");
		        number = 0;
		            
				inner.getSources().clear();
				inner.resetState();
				resetUiControls();

			    inner.setMaxSources(numbSource);
			    inner.setFreq(currentFreq);
			    inner.respawnWorm();
			    
			    numbSrc.setEnabled(true);
			    lista.setEnabled(true);
			    inner.setAddEnabled(on);  
			    inner.setSimRunning(on);
			    inner.pauseSim(false);
			    
			    gen = true;
			    on = false;
			    
			    FunctAndConst.enableButton(generate, FunctAndConst.buttonBg, FunctAndConst.buttonFg);
			    FunctAndConst.enableButton(back, FunctAndConst.buttonBg, FunctAndConst.buttonFg);
			    
			    
	            clusterSlider.setValue(clusterSlider.getMinimum());   
	            sizeSlider.setValue(sizeSlider.getMinimum()); 
	            
	            startPauseCounter = 0;
	         
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		
		//guzik exit
		exit = new JButton("Exit");
		exit.setMinimumSize(new Dimension(70, 25));
		exit.addActionListener(e -> System.exit(0));
		
		
		//dodanie guziczków do controlPanel
		controlPanel.add(back);
		controlPanel.add(reset);
		controlPanel.add(exit);
		controlPanel.setBackground(new Color(245, 222, 179));
		
		this.add(controlPanel, BorderLayout.SOUTH);
		
		
		//sources funcionalities panel
		functional = new JPanel();
		functional.setLayout(new BoxLayout(functional, BoxLayout.Y_AXIS));
		functional.setPreferredSize(new Dimension(250, 400));
		functional.setBackground(Color.ORANGE);
		functional.setBorder(BorderFactory.createTitledBorder(
	                BorderFactory.createLineBorder(Color.BLACK),
	                "Sources funcionalities",
	                TitledBorder.CENTER,
	                TitledBorder.TOP,
	                new Font("Arial", Font.BOLD, 14),
	                Color.BLACK
	        ));
		

		
		innerPanel = new JPanel();
		innerPanel.setBackground(Color.orange);
		innerPanel.setLayout(new BorderLayout());		
		innerPanel.setMinimumSize(new Dimension(500, 500));
		
		//OBIEKT TYPU SIMULATION PANEL!
		inner = new SimulationPanel(size, size, 1, 500, 500);  //500x500 pixeli
		
		Thread simThread = new Thread(inner);
		simThread.setDaemon(true);
		simThread.start();
		
		terrainGen = new TerrainGeneration(inner);
		Border padding = BorderFactory.createMatteBorder(15, 15, 15, 15, Color.ORANGE);
		Border ramka = BorderFactory.createLineBorder(Color.black, 3);
		inner.setBorder(BorderFactory.createCompoundBorder(padding, ramka));
		

		squareWrapper = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
		    public Dimension getPreferredSize() {
		        int size = Math.min(innerPanel.getWidth(), innerPanel.getHeight());
		        return new Dimension(size, size);
		    }

		    @Override
		    public void doLayout() {
		        int size = Math.min(getWidth(), getHeight());
		        int x = (getWidth() - size) / 2;
		        int y = (getHeight() - size) / 2;
		        inner.setBounds(x, y, size, size);
		    }
		};
		squareWrapper.add(inner);
		squareWrapper.setBackground(Color.orange); 
		innerPanel.add(squareWrapper, BorderLayout.CENTER);

		
		this.add(innerPanel, BorderLayout.CENTER);
		this.add(functional, BorderLayout.EAST);
		
		
		//okienko z wyborem zródeł
		pSource = new JPanel(new GridLayout(2,1));
		pSource.setOpaque(false);
		
		source = new JLabel("Number of sources: ");
		source.setHorizontalAlignment(SwingConstants.CENTER);
		source.setVerticalAlignment(SwingConstants.CENTER);
		pSource.add(source);
		
		numbSrc = new JComboBox<>(sources);
		numbSrc.setRenderer(renderer);
		numbSrc.setBackground(Color.WHITE);
				
		ActionListener sourceListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = ((JComboBox<?>) e.getSource()).getSelectedIndex();
				numbSource = index + 1; //wybieram wspolczynnik odpowiadający indeksowi
				inner.setMaxSources(numbSource);
				
				}
			};
			
		numbSrc.addActionListener(sourceListener);
		source.setLabelFor(numbSrc);
		pSource.add(numbSrc);
		
		functional.add(pSource);
		functional.add(Box.createRigidArea(new Dimension(250, 20)));
		
		
		//okienko z wyborem czestotliwosci
		freq = new JLabel("Frequency: 500 Hz ");
		freq.setLabelFor(slider);
		freq.setHorizontalAlignment(SwingConstants.CENTER);
		freq.setVerticalAlignment(SwingConstants.CENTER);
		
		p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.setSize(new Dimension(250, 20));
		p2.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		
		slider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
		slider.setPreferredSize(new Dimension(250, 20)); 
		slider.setBackground(Color.ORANGE);

		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(200);
		slider.setValue(500);
		
		
		//ustawienia częstotliwości fali 
        slider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               currentFreq = slider.getValue();
               freq.setText(String.valueOf("Frequency: " + currentFreq + " Hz"));
               inner.setFreq(currentFreq);
            }
        });
        p2.add(freq, BorderLayout.NORTH);
        p2.add(slider, BorderLayout.CENTER);
       
        functional.add(p2);
        functional.add(Box.createRigidArea(new Dimension(250,20)));
		
		//panel z danymi
		data = new JPanel(new GridLayout(5,1));
		data.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		data.setBackground(Color.WHITE);
		
		data1 = new JLabel("Time elapsed: 0.00 s");
		data2 = new JLabel("Harvested spice: 0.00 t");
		
	
		/*Timer countdownTimer = new Timer(100, e -> {
		    if (number > 0) {
		        data1.setText(String.format("Time elapsed: %.2f s", number));
		        number -= 10 / 1000.0;
		    } else {
		        ((Timer)e.getSource()).stop();    // zatrzymaj timer
		        data1.setText("Time elapsed: 0 s");
		        reset.doClick();                  // kliknij reset
		        javax.swing.JOptionPane.showMessageDialog(
			            this,
			            MainPanel.baza.listOfUsers(),
			            "Users",
			            javax.swing.JOptionPane.INFORMATION_MESSAGE
			        );
		    }
		});*/
		

	
		
		
		//GUZIK RUN -> PAUSe
		onOff = new JButton("RUN");
		onOff.addActionListener(e -> {
			try {
				if(currentFreq == 0) throw new MyException("Frequency value is zero!");
				

				if (!on && startPauseCounter ==0)
				{ 
				    if (inner.getSelectedSources().size() != numbSource)
				        throw new MyException("Invalid number of sources!");
				}

				startPauseCounter++;
				
		        String input = tField.getText().trim();
		        
		        if (countdownTimer != null) {
		            countdownTimer.cancel();
		        }
		        

		        // If the input matches a valid decimal number
		        if (input.matches("\\d+(\\.\\d+)?") && input != null) {
		            double temp = Double.parseDouble(input);
		            if (temp > 0) {
		                number = temp; // valid input, update number
		        	    countdownTimer = new Timer();
		        	    countdownTimer.scheduleAtFixedRate(new TimerTask() {
		        	        @Override public void run() {
		        	            number -= 0.1;
		        	            if (number > 0) {
		        	                // aktualizacja UI tylko przez EDT
		        	                SwingUtilities.invokeLater(() ->
		        	                    data1.setText(String.format("Time elapsed: %.1f s", number))
		        	                );
		        	            } else {
		        	                countdownTimer.cancel();
		        	                SwingUtilities.invokeLater(() -> {
		        	                    data1.setText("Time elapsed: 0.0 s");
		        	                    reset.doClick();
		        	                    // ewentualnie pokaz listę userów:
		        	                    JOptionPane.showMessageDialog(GamePanel.this,
		        	                        MainPanel.baza.listOfUsers(), "Users",
		        	                        JOptionPane.INFORMATION_MESSAGE);
		        	                });
		        	            }
		        	        }
		        	    }, 0, 100);  // co 100 ms
		            } else {
		                number = 0; // invalid (zero or negative), reset to 0
		            }
		        } else {
		            number = 0; // not a valid number, reset to 0
		            throw new MyException("Time of simulation is not set!");
		        }
		        
			tField.setEnabled(false);
			on = !on;
			gen = !gen;
		    inner.setSimRunning(on); //wlączamy symulacje
		    numbSrc.setEnabled(false);
		    lista.setEnabled(false);
		    onOff.setText(on ? "PAUSE" : "RUN"); 
		    FunctAndConst.disableButton(generate);
		    FunctAndConst.disableButton(back);
		    
		    
			
		    if(onOff.getText().equals("PAUSE")) {
		    	inner.pauseSim(true);
		    	

		    }
		    
			} catch (MyException ex) {
				JOptionPane.showMessageDialog(GamePanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		
		
		//SUWAK MOCY
		powerSlider = new JSlider(JSlider.HORIZONTAL, 25, 75, 50);
		powerSlider.setPreferredSize(new Dimension(250, 20)); 
		powerSlider.setBackground(Color.ORANGE);
		pow = new JLabel("Excavation power: 50 MW");
		pow.setOpaque(true);
		pow.setBackground(Color.orange);
		pow.setLabelFor(powerSlider);
		pow.setHorizontalAlignment(SwingConstants.CENTER);
		pow.setVerticalAlignment(SwingConstants.CENTER);

		powerSlider.setPaintTicks(true);
		
		powerSlider.setPaintLabels(true);
		powerSlider.setMinorTickSpacing(5);
		powerSlider.setMajorTickSpacing(25);
		
		powerSlider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               int Pvalue = powerSlider.getValue();
               pow.setText(String.valueOf("Excavation power: " + Pvalue + " MW"));
            }
        });
		
		data.add(pow);
		data.add(powerSlider);
		data.add(data1);
		data.add(data2);
		data.add(onOff);
		functional.add(data);
		
		
		
		//RERRAIN FUNCIONALITIES
		genPanel = new JPanel();
		genPanel.setLayout(new BoxLayout(genPanel, BoxLayout.Y_AXIS)); 
		genPanel.setPreferredSize(new Dimension(250, 400));
		genPanel.setBackground(Color.ORANGE);
		genPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Terrain funcionalities",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK
        ));
		
		
		lista = new JComboBox<>(teren); //lista z teren do generowania
		lista.setRenderer(renderer);
		lista.setBackground(Color.WHITE);
		//lista.setMaximumSize(new Dimension(Integer.MAX_VALUE, lista.getPreferredSize().height));
		lista.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		field = new JLabel("Field: ");
		field.setLabelFor(lista);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setVerticalAlignment(SwingConstants.CENTER);
		
		p = new JPanel();
		p.setLayout(new GridLayout(2,1));
		p.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		p.add(field);
		p.add(lista);
		genPanel.add(p);
		
		genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		
		//clusterSlider
		p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		//p.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		clusterSlider = new JSlider(1, 10, 1);
		clusterSlider.setMajorTickSpacing(1);
		clusterSlider.setPaintTicks(true);
		clusterSlider.setPaintLabels(true);
		clusterSlider.setBackground(Color.ORANGE);
		clusterSlider.addChangeListener(e -> terrainClusters = clusterSlider.getValue());
		
		JLabel clusterLabel = new JLabel("Generated clusters:");
		clusterLabel.setLabelFor(clusterSlider);
		clusterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		clusterLabel.setVerticalAlignment(SwingConstants.CENTER);
		p3.add(clusterLabel, BorderLayout.NORTH);
		p3.add(clusterSlider, BorderLayout.CENTER);
		genPanel.add(p3);
		genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		

		p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		p4.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		
		sizeSlider = new JSlider(1, 5, 1);
		sizeSlider.setBackground(Color.ORANGE);
		sizeSlider.setMajorTickSpacing(1);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);
		sizeSlider.addChangeListener(new ChangeListener() {
		    @Override
		    public void stateChanged(ChangeEvent e) {
		        clusterSizeParameter = sizeSlider.getValue();
		        if (parameterReduction >= clusterSizeParameter) {
		            parameterReduction = clusterSizeParameter - 1;
		            //reduceSlider.setValue((int) parameterReduction);
		        }
		    }
		});
		
		JLabel sizeLabel = new JLabel("Cluster Size:");
		sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sizeLabel.setVerticalAlignment(SwingConstants.CENTER);
		sizeLabel.setLabelFor(sizeSlider);
		
		//genPanel.add(sizeLabel);    
		//genPanel.add(sizeSlider);
		//generate.setAlignmentX(Component.CENTER_ALIGNMENT); 

		p4.add(sizeLabel, BorderLayout.NORTH);
		p4.add(sizeSlider, BorderLayout.CENTER);
		genPanel.add(p4);
		genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		
		
		p5 = new JPanel();
		p5.setLayout(new GridLayout(1,1));
		p5.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		
		//przycisk generowania terenu o wybranym aktualnie typie
		generate = new JButton("Generate chosen terrain");
		generate.setMaximumSize(new Dimension(250, 25));
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = (String) lista.getSelectedItem();
				String text_of_selected = selected.toString();
				terrainGen.generateTerrain(text_of_selected,terrainClusters,clusterSizeParameter, size, gen, parameterReduction, 0);		
			}
		});
		p5.add(generate);
		genPanel.add(p5);
		genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		//genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		
		p6 = new JPanel();
		p6.setLayout(new GridLayout(2,1));
		p6.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		
		tField = new JTextField();
		JLabel tFieldLabel1 = new JLabel("End simulation after (s):");
		tFieldLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
		

		
		
		p6.add(tFieldLabel1);
		p6.add(tField);
		genPanel.add(p6);
		genPanel.add(Box.createRigidArea(new Dimension(250, 20)));
		
		JLabel tFieldLabel2 = new JLabel("seconds");
/*
		tField.setMinimumSize(new Dimension(100, 25)); 
		tField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); 
		tField.setPreferredSize(new Dimension(150, 25));
		tField.setAlignmentX(Component.CENTER_ALIGNMENT);
		*/
		//p5.add(generate);
		
		
	
/*
		genPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
		genPanel.add(tFieldLabel1);
		genPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		genPanel.add(tField);
		genPanel.add(tFieldLabel2);*/
		
		this.add(genPanel, BorderLayout.WEST);
		
		/*//timer
		Timer elapsedTimer = new Timer(100, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        double seconds = inner.getElapsedMs() / 1000.0;
		        if (number != 0 && seconds >= number)
		        {
		        	SwingUtilities.invokeLater(() -> reset.doClick());

		        }
		        data1.setText(String.format("Time elapsed: %.2f s", seconds));
		    }
		});
		elapsedTimer.start();
		*/

		
		//ESTETYKA
		FunctAndConst.buttonStyling(back, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(reset, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(exit, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(generate, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(onOff, Color.black, new Color(255, 248, 220));
		
		p.setMaximumSize(new Dimension(250, 60));
		p3.setMaximumSize(new Dimension(250, 80));
		p4.setMaximumSize(new Dimension(250, 80));
		pSource.setMaximumSize(new Dimension(250, 60));
		p2.setMaximumSize(new Dimension(250, 80));
		data.setMaximumSize(new Dimension(240, 250));
		p5.setMaximumSize(new Dimension(250, 40));
		p6.setMaximumSize(new Dimension(250,50));
		
		//kod poniżej zapewnia stałe proporcje panelu symulacji
				this.addComponentListener(new java.awt.event.ComponentAdapter() {
				    @Override
				    public void componentResized(java.awt.event.ComponentEvent e) 
				    {
				    	
				        int size = (int)(Math.min(getHeight(), getWidth()) * 0.9); // keep square
				        ((SimulationPanel)inner).resize(size, size);

				        inner.revalidate();
				        inner.repaint();
				    }
				});
				

	}
	
	private void resetUiControls() {

	    //suwaki, comboboxy, etykiety
	    lista.setSelectedIndex(0);    // „Sand”
	    lista.setEnabled(true);
	    numbSrc.setSelectedIndex(0);             // 1 źródło
	    numbSrc.setEnabled(true);
	    slider.setValue(500);                      // 0 Hz
	    powerSlider.setValue(50);                // 50 MW
	    freq.setText("frequency: 500 Hz");
	    pow.setText("Excavation power: 50 MW");
	    onOff.setText("RUN");
	    
	    //flagi symulacji
	    gen = true;                    // wyłącz generator terenu
	    on = false;                    // własna flaga ON/OFF

	    //aktualizacja bieżących zmiennych
	    numbSource   = 1;
	    currentFreq  = 500;
	}
	
}