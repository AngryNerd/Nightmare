/** WINDOW SETUP **/
package net.amigocraft.nightmare;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
	
	public static MainWindow f;
	//public static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	//public static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static int width = 800;
	public static int height = 600;
	
	public static void main(String[] args){
		f = new MainWindow();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		f.setSize(width, height);
		f.setTitle("Nightmare");
		f.setLocationRelativeTo(null);
		Image icon;
		try {
			icon = ImageIO.read(Main.class.getResourceAsStream("/images/BoyStandFront.gif"));
			f.setIconImage(icon);
		}
		catch (IOException e){
			e.printStackTrace();
		}
                
		f.setVisible(true);
	}

	public static void pullThePlug(){
		WindowEvent wev = new WindowEvent(f, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		f.setVisible(false);
		f.dispose();
		System.exit(0); 
	}
}
