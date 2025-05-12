package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GamePanel extends JPanel implements GameInterface
{

	/**
	 * @author 48533
	 * @author Michał Kysiak
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel innerPanel, functional, data, p, p2, pSource, controlPanel, squareWrapper, genPanel;
	protected JButton back, onOff, exit, reset, generate; 
	private String teren[] = {"Sand", "Granite", "Limestone"};
	private String sources[] = {"One source", "Two sources", "Three sources", "Four sources"};
	protected double currentWsp;
	protected static double currentFreq;
	private int numbSource;
	private JComboBox<String> numbSrc, lista;
	private JLabel  field, freq, data1, data2, source, pow;
	protected JSlider slider, powerSlider, reduceSlider;
	private int size = 500, terrainClusters = 1, offshoots = 10;
	private SimulationPanel inner;
	private final TerrainGeneration terrainGen;
	private boolean gen = true; //flaga do generowania terenu
	private boolean on = false;  //flaga do przechowywania stanu
	private double parameterReduction = 2, clusterSizeParameter = 3;
	
	public GamePanel() {
		
		super(new BorderLayout());
		
		controlPanel = new JPanel(); 
		controlPanel.setLayout(new FlowLayout());
		back = new JButton("Back"); 
		back.setMinimumSize(new Dimension(70, 25));
		
		reset = new JButton("Reset");
		reset.setMinimumSize(new Dimension(70, 25));
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
		            if ("PAUSE".equals(onOff.getText())) {
		                onOff.doClick();  
		            }
				
				inner.getSources().clear();
				inner.pixelGrid.clear();
				inner.resetState();
				resetUiControls();

			    inner.setMaxSources(numbSource);
			    inner.setFreq(currentFreq);
			    
			    numbSrc.setEnabled(true);
			    lista.setEnabled(true);
			    inner.setAddEnabled(on);  
			    inner.setSimRunning(on);
			    inner.pauseSim(false);
			    
			    gen = true;
			    on = false;
			    
				} catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});

		
		exit = new JButton("Exit");
		exit.setMinimumSize(new Dimension(70, 25));
		exit.addActionListener(e -> System.exit(0));
		
		
		functional = new JPanel();
		functional.setLayout(new BoxLayout(functional, BoxLayout.Y_AXIS));
		functional.setPreferredSize(new Dimension(250, 400));
		functional.setBackground(Color.ORANGE);
		
		field = new JLabel("Field: ");
		functional.setBorder(BorderFactory.createTitledBorder(
	                BorderFactory.createLineBorder(Color.BLACK),
	                "Funcionalities",
	                TitledBorder.CENTER,
	                TitledBorder.TOP,
	                new Font("Arial", Font.BOLD, 14),
	                Color.BLACK
	        ));
		
		lista = new JComboBox<>(teren); //przeniesione tutaj
		//ustalamy renderer z wyśrodkowanym tekstem
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER); // wyśrodkuj poziomo
		lista.setRenderer(renderer);
		lista.setBackground(Color.WHITE);
		field.setLabelFor(lista);
		
		p = new JPanel();
		p.setLayout(new GridLayout(2,1));
		p.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		p.add(field);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setVerticalAlignment(SwingConstants.CENTER);
		p.add(lista);
		//functional.add(p);
		functional.add(Box.createRigidArea(new Dimension(250, 20)));

		//przycisk generowania terenu o wybranym aktualnie typie
		generate = new JButton("Generate chosen terrain");
		generate.setMinimumSize(new Dimension(70, 25));
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = (String) lista.getSelectedItem();
				String text_of_selected = selected.toString();
				terrainGen.generateTerrain(text_of_selected,terrainClusters,clusterSizeParameter, size, gen, parameterReduction, offshoots);		
			}
		});
		
		controlPanel.add(back);
		controlPanel.add(reset);
		controlPanel.add(exit);
		//controlPanel.add(generate);
		controlPanel.setBackground(new Color(245, 222, 179));
		
		this.add(controlPanel, BorderLayout.SOUTH);
		
		FunctAndConst.buttonStyling(back, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(reset, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(exit, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(generate, new Color(240, 248, 255), new Color(128, 0, 0));
		
		innerPanel = new JPanel();
		innerPanel.setBackground(Color.orange);
		innerPanel.setLayout(new BorderLayout());		
		innerPanel.setMinimumSize(new Dimension(500, 500));
		
		//OBIEKT TYPU SIMULATION PANEL!
		inner = new SimulationPanel(size, size, 1, 500, 500);  //500x500 pixeli
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
		freq = new JLabel("frequency: 0 Hz ");
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
		
		
		//ustawienia częstotliwości fali 
        slider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               currentFreq = slider.getValue();
               freq.setText(String.valueOf("frequency: " + currentFreq + " Hz"));
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
		
	
		//GUZIK RUN -> PAUSe
		onOff = new JButton("RUN");
		FunctAndConst.buttonStyling(onOff, Color.black, new Color(255, 248, 220));

		onOff.addActionListener(e -> {
			try {
				if(currentFreq == 0) throw new MyException("Frequency value is zero!");
				
			on = !on;
			gen = !gen;
		    inner.setSimRunning(on); //wlączamy symulacje
		    numbSrc.setEnabled(false);
		    lista.setEnabled(false);
		    //lista.setEnabled(false);
		    onOff.setText(on ? "PAUSE" : "RUN"); 
			
		    if(onOff.getText().equals("PAUSE")) inner.pauseSim(true);

		    
		    
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
		
		p.setMaximumSize(new Dimension(250, 60));
		pSource.setMaximumSize(new Dimension(250, 60));
		p2.setMaximumSize(new Dimension(250, 80));
		data.setMaximumSize(new Dimension(240, 250));
		
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
		
		
		//kod panelu po lewej z ustawieniami generacji terenu
		genPanel = new JPanel();
		genPanel.setLayout(new BoxLayout(genPanel, BoxLayout.Y_AXIS)); 
		genPanel.setOpaque(false);
		genPanel.setBorder(BorderFactory.createTitledBorder("Terrain Gen Parameters"));

		genPanel.add(p);

		JLabel clusterLabel = new JLabel("Generated clusters:");
		JSlider clusterSlider = new JSlider(1, 10, terrainClusters);
		clusterSlider.setMajorTickSpacing(1);
		clusterSlider.setPaintTicks(true);
		clusterSlider.setPaintLabels(true);
		clusterSlider.addChangeListener(e -> terrainClusters = clusterSlider.getValue());

		JLabel sizeLabel = new JLabel("Cluster Size:");
		JSlider sizeSlider = new JSlider(1, 5, (int) clusterSizeParameter);
		sizeSlider.setMajorTickSpacing(1);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);
		sizeSlider.addChangeListener(new ChangeListener() {
		    @Override
		    public void stateChanged(ChangeEvent e) {
		        clusterSizeParameter = sizeSlider.getValue();
		        if (parameterReduction >= clusterSizeParameter) {
		            parameterReduction = clusterSizeParameter - 1;
		            reduceSlider.setValue((int) parameterReduction);
		        }
		    }
		});

		// Number of offshoots (max 20)
		JLabel offshootsLabel = new JLabel("Number of offshoots:");
		JSlider offshootsSlider = new JSlider(0, 20, offshoots);
		offshootsSlider.setMajorTickSpacing(5);
		offshootsSlider.setPaintTicks(true);
		offshootsSlider.setPaintLabels(true);
		offshootsSlider.addChangeListener(new ChangeListener() {
		    @Override
		    public void stateChanged(ChangeEvent e) {
		        offshoots = offshootsSlider.getValue();
		    }
		});

		// Parameter reduction (max 3, must be < clusterSizeParameter)
		JLabel reduceLabel = new JLabel("Reduction of offshoots:");
		reduceSlider = new JSlider(1, 3, (int) parameterReduction);
		reduceSlider.setMajorTickSpacing(1);
		reduceSlider.setPaintTicks(true);
		reduceSlider.setPaintLabels(true);
		reduceSlider.addChangeListener(new ChangeListener() {
		    @Override
		    public void stateChanged(ChangeEvent e) {
		        int newVal = reduceSlider.getValue();
		        if (newVal >= clusterSizeParameter) {
		            newVal = Math.max(1, (int) clusterSizeParameter - 1);
		            reduceSlider.setValue(newVal);
		        }
		        parameterReduction = newVal;
		    }
		});

		genPanel.add(clusterLabel); 
		genPanel.add(clusterSlider);
		genPanel.add(sizeLabel);    
		genPanel.add(sizeSlider);
		genPanel.add(offshootsLabel);
		genPanel.add(offshootsSlider);
		genPanel.add(reduceLabel); 
		genPanel.add(reduceSlider);
		
		
		generate.setAlignmentX(Component.CENTER_ALIGNMENT); 

		generate.setMaximumSize(new Dimension(Integer.MAX_VALUE, generate.getPreferredSize().height));
		generate.setPreferredSize(new Dimension(180, 30));
		
		genPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		genPanel.add(generate);

		this.add(genPanel, BorderLayout.WEST);
		genPanel.setPreferredSize(new Dimension(220, 300));
	}
	
	private void resetUiControls() {

	    //suwaki, comboboxy, etykiety
	    lista.setSelectedIndex(0);    // „Sand”
	    lista.setEnabled(true);
	    numbSrc.setSelectedIndex(0);             // 1 źródło
	    numbSrc.setEnabled(true);
	    slider.setValue(0);                      // 0 Hz
	    powerSlider.setValue(50);                // 50 MW
	    freq.setText("frequency: 0 Hz");
	    pow.setText("Excavation power: 50 MW");
	    onOff.setText("RUN");
	    
	    //flagi symulacji
	    gen = true;                    // wyłącz generator terenu
	    on = false;                    // własna flaga ON/OFF

	    //aktualizacja bieżących zmiennych
	    numbSource   = 1;
	    currentFreq  = 0;
	}
	
}