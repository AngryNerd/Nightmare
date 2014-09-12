/** JFRAME **/
package net.amigocraft.Nightmare;

import java.awt.*;
import javax.swing.*;

public class FrameManager extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public GameManager panel;
	
	public FrameManager(){
		panel = new GameManager(this);
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
}
