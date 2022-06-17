package ArduinoCar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @author Ignacio Alvarez someoneigna@gmail.com
 * @class GUIConsole
 * A JFrame showing console output
 */
final public class GuiConsole {

	private static GuiConsole INSTANCE;

	private ArrayList<ConsoleInputListener> listeners = new ArrayList<ConsoleInputListener>();
	
	private JTextArea console;
	private JFrame frame;
	private JScrollPane scrollPane;
	private PrintStream outStream;
	private JTextField input;
	private JButton submit;
	private JLabel distanceHand, distance, currentSpeed, currentDirection, currentAngle;

	public GuiConsole(final String title,final int width, final int height) {
		INSTANCE = this;
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		console = new JTextArea("",20, 30);
		frame = new JFrame(title);
		input = new JTextField();
		submit = new JButton("Send");
		
		currentDirection = new JLabel("DIRECTION");
		currentSpeed = new JLabel("SPEED");
		distanceHand = new JLabel("DISTANCE WALL: cm");
		distance = new JLabel("DISTANCE: cm");
		currentAngle = new JLabel("SERVO ANGLE");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		console.setEditable(false);
		console.setLineWrap(true);

		//Default JTextFrame font colors
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		console.setAutoscrolls(true);

		scrollPane = new JScrollPane(console);
		scrollPane.setBorder(new EmptyBorder(8, 8, 8, 8));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setAutoscrolls(true);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(input, BorderLayout.CENTER);
		inputPanel.add(submit, BorderLayout.EAST);
		
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new GridLayout(1, 5));
		outputPanel.add(currentDirection);
		outputPanel.add(currentSpeed);
		outputPanel.add(currentAngle);
		outputPanel.add(distanceHand);
		outputPanel.add(distance);
		
		outputPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(scrollPane, BorderLayout.NORTH);
		frame.getContentPane().add(outputPanel, BorderLayout.CENTER);
		frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

		frame.setMinimumSize(new Dimension(width, height));
		frame.setResizable(false);
		frame.setLocation(200,100);
		
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource().equals(submit)) {
					handleInput();
				}
				
			}
		});

		KeyListener listen = new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {

				case KeyEvent.VK_F9:
					SwingUtilities.invokeLater(new Runnable(){

						@Override
						public void run() {
							Color foreground = JColorChooser.showDialog(new JPanel(), "Choose Foreground color", Color.BLACK);
							console.setForeground(foreground);
						}

					});
					break;
				case KeyEvent.VK_ENTER:
					if(!submit.isFocusOwner()) {
						handleInput();
					}
					break;

				case KeyEvent.VK_F10:

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							Color background = JColorChooser.showDialog(new JPanel(), "Choose Background color", Color.BLACK);
							console.setBackground(background);
						}

					});
					break;

				default:
					break;
				}//End of switch(KeyEvent e)

			}

			@Override
			public void keyReleased (KeyEvent e){}



		};

		input.addKeyListener(listen);
		//Listeners
		console.addKeyListener(listen);
		submit.addKeyListener(listen);

		try {
			outStream = new PrintStream(new OutputStream() {
				
				@Override
				public void write (int b) throws IOException {
					if(Character.isDefined(b)) {
						String s = new String(Character.toChars(b));
	//					if(emptySpacecheck(s)) {
	//						skipNext = true;
	//						return;
	//					}
						INSTANCE.append(s);
					}
						
	//				else {
	//					System.out.println("Illegal char");
	//					hasNext = false;
	//					input.setText("");
	//				}
				}
	
			}, false, StandardCharsets.UTF_16.displayName());
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.setOut(outStream);
		
		this.updateAngle(90);
		this.updateDistance(0, 0);
		this.updateInformation(Steering.STOP, 0);

		frame.pack();
		scrollPane.setVisible(true);
		frame.setVisible(true);
		
		this.input.requestFocus();
	}
	
	public void updateDistance(float distance, float distanceHand) {
		this.distance.setText("Distance: " + distance + "cm");
		this.distanceHand.setText("Distance Wall: " + distanceHand + "cm");
	}
	
	public void updateAngle(int angle) {
		this.currentAngle.setText("Angle: " +  angle + "°");
	}
	
	public void updateInformation(Steering s, int speed) {
		this.currentDirection.setText("Direction: " + s.name().toLowerCase());
		this.currentSpeed.setText("Speed: " + speed);
	}

	class Printer extends PrintStream {

		public Printer(OutputStream out) {
			super(out);
		}
		
	}
	
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void setText(String text){
		console.setText(text);
	}

	public void append(String text){
		console.append(text);
		console.setCaretPosition(console.getDocument().getLength());
	}

	public void append(int number){
		console.append("" + number);
	}

	public void append(double number){
		console.append("" + number);
	}

	public void append(boolean bool){
		console.append("" + bool);
	}

	public String getText(){
		return console.getText();
	}

	public boolean addInputListener(ConsoleInputListener listener) {
		return listeners.add(listener);
	}
	
	public boolean removeInputListener(ConsoleInputListener listener) {
		return listeners.remove(listener);
	}

	public void handleInput() {
		for(ConsoleInputListener listener: listeners) {
			String in = input.getText();
			input.setText("");
			for(char c: in.toCharArray()) {
				if(Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
					System.err.println("\"" + c + "\"");
					return;
				}
			}
			listener.handleInput(in);
		}
	}
	
//	private boolean emptySpacecheck(String msg){
//	    return msg.matches(".*\\s+.*");
//	}
	
	public static GuiConsole getInstance() {
		return INSTANCE;
	}
	
}