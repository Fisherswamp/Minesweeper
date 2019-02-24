import java.awt.Dimension;

import javax.swing.JFrame;
/**
 * @author Itai Rivkin-Fish
 * @version 9/12/17
 * 
 * Entire window class code borrowed(and possibly modified) from Zachary Berenger using his tutorial https://www.codingmadesimple.com/wizards-intermediate-java-course/ 
 */
public class Window{
	
	public Window(int width, int height, String title,Game game) {
		JFrame frame = new JFrame(title);
		
		Dimension size = new Dimension(width,height);
		frame.setPreferredSize(size);
		frame.setMaximumSize(size);
		frame.setMinimumSize(size);
		frame.add(game);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}
	
}
