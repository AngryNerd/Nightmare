/** JFRAME **/
package net.amigocraft.Nightmare;

import java.awt.*;
import javax.swing.*;

public class ab extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public ac panel;
	
	public ab(){
		panel = new ac(this);
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
}
