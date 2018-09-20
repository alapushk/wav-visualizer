import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import GUI.Wave;

public class DrawLine extends JPanel {
	public byte[] input; //array of sample values
	boolean flag = false;
	int numSamples = 0; //number of samples
	int max = 0;
	
	public void setBuffer(Wave audio) {
		input = new byte[audio.numSamples];
		System.arraycopy(audio.data, 0, input, 0, audio.numSamples);
		numSamples = audio.numSamples;
		max = audio.maxvalue;
		System.out.println("Wave max value: " + max);
		flag = true;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g); 
	
		//set the size of the screen:
		int width = getWidth(); //width of the screen
		int height = getHeight(); //height of the screen
		
		//AffineTransform for scaling:
		AffineTransform at = g2.getTransform();
	
		byte value = 0;
		if(flag == true && numSamples > width) {//the input has been copied
			//sample line:
			double sx = 0.1;
			double sy = 0.5;
			g2.scale(sx, sy);
			
			System.out.println("numSamples: " + numSamples);
			g.setColor(Color.BLACK);
			for(int i = 0; i < numSamples; i++) {
				if(i < width/sx) {
					value = input[i];
					g.drawLine(i, (int)(((height/sy)/6)-value), i, (int)((height/sy)/6));
				}else if(i < 2*(width/sx)) {
					value = input[i];
					g.drawLine((int)(i-width/sx), (int)((height/sy)/2)-value, (int)(i-width/sx), (int)((height/sy)/2));
				}else {
					value = input[i];
					g.drawLine((int)(i-2*(width/sx)), (int)(5*(height/sy)/6)-value, (int)(i-(2*width/sx)), (int)(5*(height/sy)/6));
				}
			}
			g.setColor(Color.BLUE);
			g.drawLine(0, (int)((height/sy)/6), (int)(width/sx), (int)((height/sy)/6));
			g.drawLine(0, (int)((height/sy)/2), (int)(width/sx), (int)((height/sy)/2));
			g.drawLine(0, (int)(5*(height/sy)/6), (int)(width/sx), (int)(5*(height/sy)/6));
			
			g.setColor(Color.DARK_GRAY);
			g.drawLine(0, (int)((height/sy)/3), (int)(width/sx), (int)((height/sy)/3));
			g.drawLine(0, (int)(2*(height/sy)/3), (int)(width/sx), (int)(2*(height/sy)/3));
			//g.drawLine(0, (int)(5*(height/sy)/6), (int)(width/sx), (int)(5*(height/sy)/6));
		}
		g2.setTransform(at);
	}
}

