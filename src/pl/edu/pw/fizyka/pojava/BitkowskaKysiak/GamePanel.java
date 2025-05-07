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

	private static final long serialVersionUID = 1L;
	
	private JPanel innerPanel, functional, data, p, p2, pSource, controlPanel, squareWrapper;
	protected JButton back, onOff, exit, reset, generate, genFlip; 
	private String teren[] = {"Sand", "Granite", "Limestone"};
	private String sources[] = {"One source", "Two sources", "Three sources", "Four sources"};
	private double wspolczynniki[] = {0.996, 0.700, 0.30}; //tłumienie
	protected double currentWsp;
	protected static double currentFreq;
	private int numbSource;
	private JComboBox<String> numbSrc, lista;
	private JLabel  field, freq, data1, data2, source, pow;
	protected JSlider slider, powerSlider;
	private int size = 100, terrainClusters = 1, clusterSize = 5;
	private SimulationPanel inner;
	private final TerrainGeneration terrainGen;
	private boolean gen = false;
	private boolean[] on = {false};  //flaga do przechowywania stanu
	
	public GamePanel() {
		
		super(new BorderLayout());
		
		//MK+
		lista = new JComboBox<>(teren); //przeniesione tutaj
		controlPanel = new JPanel(); 
		controlPanel.setLayout(new FlowLayout());
		back = new JButton("Back"); 
		back.setMinimumSize(new Dimension(70, 25));
		
		genFlip = new JButton("Enable/disable generation");
		genFlip.setMinimumSize(new Dimension(70, 25));
		genFlip.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gen = !gen;
			}	
		});
		
		
		reset = new JButton("Reset");
		reset.setMinimumSize(new Dimension(70, 25));
		reset.addActionListener(e -> {
			
			inner.resetState();
			resetUiControls();

		    //inner.setDamping(currentWsp);
		    inner.setMaxSources(numbSource);
		    inner.setFreq(currentFreq);
	
		    inner.setAddEnabled(on[0]);  
		    inner.setSimRunning(on[0]);
		});

		
		exit = new JButton("Exit");
		exit.setMinimumSize(new Dimension(70, 25));
		exit.addActionListener(e -> System.exit(0));
		
		//przycisk generowania terenu o wybranym aktualnie typie
		generate = new JButton("Generate chosen terrain");
		generate.setMinimumSize(new Dimension(70, 25));
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = (String) lista.getSelectedItem();
				String text_of_selected = selected.toString();
				terrainGen.generateTerrain(text_of_selected,terrainClusters,clusterSize, size, gen);		
			}
		});
		
		controlPanel.add(back);
		controlPanel.add(reset);
		controlPanel.add(exit);
		controlPanel.add(generate);
		controlPanel.add(genFlip);
		controlPanel.setBackground(new Color(245, 222, 179));
		
		this.add(controlPanel, BorderLayout.SOUTH);
		
		FunctAndConst.buttonStyling(back, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(reset, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(exit, new Color(240, 248, 255), new Color(128, 0, 0));
		FunctAndConst.buttonStyling(generate, new Color(240, 248, 255), new Color(128, 0, 0));
		
		//MK-
		
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
		
		functional = new JPanel();
		functional.setLayout(new BoxLayout(functional, BoxLayout.Y_AXIS));
		functional.setPreferredSize(new Dimension(250, 400));
		functional.setBackground(Color.ORANGE);
		
		

		//innerPanel.add(inner , BorderLayout.CENTER);
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
		
		field = new JLabel("Field: ");
		functional.setBorder(BorderFactory.createTitledBorder(
	                BorderFactory.createLineBorder(Color.BLACK),
	                "Funcionalities",
	                TitledBorder.CENTER,
	                TitledBorder.TOP,
	                new Font("Arial", Font.BOLD, 14),
	                Color.BLACK
	        ));

				
		//ustalamy renderer z wyśrodkowanym tekstem
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER); // wyśrodkuj poziomo
		lista.setRenderer(renderer);
		lista.setBackground(Color.WHITE);
	
		
		ActionListener terenListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = lista.getSelectedIndex(); //wybieram indeks z tablicy teren
				currentWsp = wspolczynniki[index]; //wybieram wspolczynnik odpowiadający indeksowi
				//((SimulationPanel)inner).setDamping(currentWsp);
				}
		};
		lista.addActionListener(terenListener);
		field.setLabelFor(lista);
		
		p = new JPanel();
		p.setLayout(new GridLayout(2,1));
		p.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		p.add(field);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setVerticalAlignment(SwingConstants.CENTER);
		p.add(lista);
		functional.add(p);
		functional.add(Box.createRigidArea(new Dimension(250, 20)));

		
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
				//int index = lista.getSelectedIndex(); //wybieram indeks z tablicy teren
				numbSource = index + 1; //wybieram wspolczynnik odpowiadający indeksowi
				((SimulationPanel)inner).setMaxSources(numbSource);
				
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
		
		
		//ustawienia częstotliwości - tu setFreq
        slider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               currentFreq = slider.getValue();
               freq.setText(String.valueOf("frequency: " + currentFreq + " Hz"));
               ((SimulationPanel)inner).setFreq(currentFreq);
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
		
		//MK+
		data1 = new JLabel("Time elapsed: 0.00 s");
		data2 = new JLabel("Harvested spice: 0.00 t");
		
		
		
		
		
		
		//GUZIK ON -> OFF
		onOff = new JButton("ON");
		FunctAndConst.buttonStyling(onOff, Color.black, new Color(255, 248, 220));

		onOff.addActionListener(e -> {
			try {
				if(currentFreq == 0) throw new MyException("Frequency value is zero!");
				
			on[0] = !on[0];
		    ((SimulationPanel)inner).setAddEnabled(on[0]); // włączamy/wyłączamy możliwość dodawania
		    ((SimulationPanel)inner).setSimRunning(on[0]); //wlączamy symulacje
		    onOff.setText(on[0] ? "OFF" : "ON"); 
				
		    
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
		
		//MK-
		
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
		
		
	}
	
	private void resetUiControls() {

	    // 1. GUI ‑ suwaki, comboboxy, etykiety
	    lista.setSelectedIndex(0);    // „Sand”
	    lista.setEnabled(true);
	    numbSrc.setSelectedIndex(0);             // 1 źródło
	    numbSrc.setEnabled(true);
	    slider.setValue(0);                      // 0 Hz
	    powerSlider.setValue(50);                // 50 MW
	    freq.setText("frequency: 0 Hz");
	    pow.setText("Excavation power: 50 MW");
	    onOff.setText("ON");
	    
	    // 2. flagi logiki symulacji
	    gen = false;                    // wyłącz generator terenu
	    on[0] = false;                    // własna flaga ON/OFF

	    // 3. aktualizacja bieżących zmiennych używanych przez SimulationPanel
	    currentWsp   = wspolczynniki[0];
	    numbSource   = 1;
	    currentFreq  = 0;
	}



	
}