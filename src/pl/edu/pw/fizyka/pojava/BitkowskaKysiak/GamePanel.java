package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel inner, functional, data, p, p2;
	protected JButton back; 
	private String teren[] = {"sand", "water", "air"};
	private double wspolczynniki[] = {1.333, 4.555, 6.77};
	private double currentWsp, currentFreq;
	private JLabel  field, freq, sliderOut;
	private JSlider slider;
	
	
	
	public GamePanel() {
		
		super(new BorderLayout());
		back = new JButton("Back");
		this.add(back, BorderLayout.SOUTH);
		
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
	                BorderFactory.createLineBorder(Color.GRAY),
	                "Funcionalities",
	                TitledBorder.CENTER,
	                TitledBorder.TOP,
	                new Font("Arial", Font.BOLD, 14),
	                Color.DARK_GRAY
	        ));

		//okienko zmiany terenu - JComboBox
		JComboBox<String> lista = new JComboBox<>(teren);
	
		
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
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		//p.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		p.add(field);
		p.add(lista);
		p.setPreferredSize(getMinimumSize());
		functional.add(p);
		//functional.add(Box.createRigidArea(new Dimension(150, 40)));

		//okienko z wyborem czestotliwosci
		freq = new JLabel("frequency: ");
		freq.setLabelFor(slider);
		
		p2 = new JPanel();
		p2.setLayout(new GridLayout(1,3));
		//p2.setOpaque(false);// przezroczysty, żeby kolor z tła był widoczny
		
		sliderOut = new JLabel();
		slider = new JSlider(JSlider.HORIZONTAL, 300, 1000, 300);
		//slider.setPreferredSize(new Dimension(250, 40)); // 👈 KLUCZOWE!

		//slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		//slider.setMinorTickSpacing(50);
		//slider.setMajorTickSpacing(200);
		
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                sliderOut.setText(String.valueOf(value)+ "Hz");
            }
        });
        p2.add(freq);
        p2.add(slider);
        p2.add(sliderOut);
        
        functional.add(p2);

		
		
		//panel z danymi
		data = new JPanel();
		data.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		functional.add(data);
		
		
		
		

		
		
	}
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	


