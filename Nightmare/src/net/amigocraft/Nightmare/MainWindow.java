/** JFRAME **/
package net.amigocraft.nightmare;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public MainCanvas panel;
	
	public MainWindow(){
		panel = new MainCanvas(this);
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
}
