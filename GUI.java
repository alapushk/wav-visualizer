import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;

import GUI.Wave;

import java.io.IOException;

public class GUI{
	JFrame window; //main window on the screen
	JPanel holder; //holds basic functionality together
	JLabel header; //window header
	JLabel status; //label for the status of choosing a file
	JLabel info; //label to hold the info about 
	DrawLine panel; //panel for waveform
	JButton button; //button to choose a file
	
	public int numSamples; 
	public byte[] input; //buffer for the input
	public int max;
	int resolution; //screen resolution
	
	//constructor:
	public GUI() {
		setWindow();
	}
	
	
	//window setup:
	private void setWindow() {
	
		//create a frame:
		window = new JFrame("Wav File Visualizer");
		window.setSize(900, 600); //default size 
		window.setLayout(new BorderLayout()); //set layout to border
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 //create components
		header = new JLabel("Choose a .wav file...", JLabel.CENTER);
		status = new JLabel(" ", JLabel.CENTER);
		info = new JLabel(" ", JLabel.CENTER);
		holder = new JPanel();
		panel = new DrawLine();
		panel.setBackground(Color.gray); //set background
		button = new JButton("Browse");
		
		
		//set up the holder panel (Header–>Button–>Status):
		GridLayout layout = new GridLayout(0,3);
		holder.setLayout(layout); 
		holder.add(header);
		holder.add(button);
		holder.add(status);
		
		input = new byte[65536]; //initialize input buffer
		
		
		//file chooser button and dialog setup:
		final JFileChooser dialog = new JFileChooser();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = dialog.showOpenDialog(window);
				if(result == JFileChooser.APPROVE_OPTION) {
					File file = dialog.getSelectedFile();
					status.setText("File Chosen: " + file.getName());
					Wave wavHeader = new Wave();
					String name = file.getPath();
					try {
						wavHeader.readWaveHead(name);//read the wave and store the relevant data
						panel.setBuffer(wavHeader);
						info.setText("Number of samples: " + wavHeader.numSamples + ". Maximum value: " + wavHeader.maxvalue);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					status.setText("Cancelled File Selection.");
				}
			} //actionPerformed
		}); //addActionListener
		
		//add elements to the frame:
		window.add(holder, BorderLayout.NORTH);
		window.add(panel, BorderLayout.CENTER);
		window.add(info, BorderLayout.SOUTH);
		window.setVisible(true);
		
	}
	
	//main function:
    public static void main(String args[]){
    	GUI layout = new GUI(); //create a new layout object
    }
}