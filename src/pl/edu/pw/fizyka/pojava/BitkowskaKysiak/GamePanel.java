package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import pl.edu.pw.fizyka.pojava.BitkowskaKysiak.utilityFunctions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel inner, functional, data, p, p2, pSource, controlPanel;
	protected JButton back, onOff, exit, reset; 
	private String teren[] = {"sand", "water", "rock"};
	private String sources[] = {"One source", "Two sources", "Three sources", "Four sources"};
	private double wspolczynniki[] = {1.333, 4.555, 6.77};
	private double currentWsp, currentFreq, currentSource;
	private JLabel  field, freq, data1, data2, data3, source;
	private JSlider slider;
	
	public GamePanel() {
		
		super(new BorderLayout());
		
		//MK+
		controlPanel = new JPanel(); 
		controlPanel.setLayout(new FlowLayout());
		back = new JButton("Back"); 
		back.setMinimumSize(new Dimension(70, 25));
		
		
		reset = new JButton("Reset");
		reset.setMinimumSize(new Dimension(70, 25));
		
		exit = new JButton("Exit");
		exit.setMinimumSize(new Dimension(70, 25));
		
		exit.addActionListener(e -> System.exit(0));
		controlPanel.add(back);
		controlPanel.add(reset);
		controlPanel.add(exit);
		controlPanel.setBackground(new Color(188, 143, 143));
		
		this.add(controlPanel, BorderLayout.SOUTH);
		
		utilityFunctions.buttonStyling(back, new Color(255, 222, 173), new Color(128, 0, 0));
		utilityFunctions.buttonStyling(reset, new Color(255, 222, 173), new Color(128, 0, 0));
		utilityFunctions.buttonStyling(exit, new Color(255, 222, 173), new Color(128, 0, 0));
		//MK-
		
		inner = new JPanel();
	        
	    
		functional = new JPanel();
		functional.setLayout(new BoxLayout(functional, BoxLayout.Y_AXIS));
		functional.setPreferredSize(new Dimension(250, 400));
		
		inner.setBackground(Color.WHITE);
		functional.setBackground(Color.ORANGE);
		
		Border padding = BorderFactory.createMatteBorder(15, 15, 15, 15, Color.ORANGE);
		Border ramka = BorderFactory.createLineBorder(Color.black, 3);
		inner.setBorder(BorderFactory.createCompoundBorder(padding, ramka));
		
		this.add(inner, BorderLayout.CENTER);
		this.add(functional, BorderLayout.EAST);
		
		field = new JLabel("field: ");
		functional.setBorder(BorderFactory.createTitledBorder(
	                BorderFactory.createLineBorder(Color.BLACK),
	                "Funcionalities",
	                TitledBorder.CENTER,
	                TitledBorder.TOP,
	                new Font("Arial", Font.BOLD, 14),
	                Color.BLACK
	        ));

		//okienko zmiany terenu - JComboBox
		JComboBox<String> lista = new JComboBox<>(teren);
		
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
		//functional.add(Box.createRigidArea(new Dimension(150, 40)));
		functional.add(Box.createRigidArea(new Dimension(250, 20)));

		
		//okienko z wyborem zródeł
		pSource = new JPanel(new GridLayout(2,1));
		pSource.setOpaque(false);
		
		
		
		source = new JLabel("number of sources: ");
		source.setHorizontalAlignment(SwingConstants.CENTER);
		source.setVerticalAlignment(SwingConstants.CENTER);
		pSource.add(source);
		
		JComboBox<String> numbSrc = new JComboBox<>(sources);
		//numbSrc.setSize(new Dimension(250,20));
		numbSrc.setRenderer(renderer);
		numbSrc.setBackground(Color.WHITE);
				
		ActionListener sourceListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = lista.getSelectedIndex(); //wybieram indeks z tablicy teren
				currentSource = index + 1; //wybieram wspolczynnik odpowiadający indeksowi
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
		slider.setPreferredSize(new Dimension(250, 20)); // 👈 KLUCZOWE!
		slider.setBackground(Color.ORANGE);

		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(200);
		
        slider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               int value = slider.getValue();
               freq.setText(String.valueOf("frequency: " + value + " Hz"));
            }
        });
        p2.add(freq, BorderLayout.NORTH);
        p2.add(slider, BorderLayout.CENTER);
       
        functional.add(p2);
        functional.add(Box.createRigidArea(new Dimension(250,20)));
		
		//panel z danymi
		data = new JPanel(new GridLayout(4,1));
		data.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		data.setBackground(Color.WHITE);
		
		
		data1 = new JLabel("data1: ");
		data2 = new JLabel("data2: ");
		data3 = new JLabel("data3: ");
		onOff = new JButton("ON/OFF");
		
		data.add(data1);
		data.add(data2);
		data.add(data3);
		data.add(onOff);
		
		functional.add(data);
		
		p.setMaximumSize(new Dimension(250, 60));
		pSource.setMaximumSize(new Dimension(250, 60));
		p2.setMaximumSize(new Dimension(250, 80));
		data.setMaximumSize(new Dimension(240, 250));
	
		
	}


	
}
	
	
	
	
	
	
	
	
	
	
	


