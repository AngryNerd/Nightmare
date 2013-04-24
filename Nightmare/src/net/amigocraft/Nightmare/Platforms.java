/** PLATFORMS **/
package net.amigocraft.nightmare;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Platforms extends JPanel {

	private static final long serialVersionUID = 1L;

	public static int floorHeight = 20;
	public static int centerX = Character.centerX;
	public static int centerY = Character.centerY;

	public static boolean objectsDefined = false;

	public static List<Rectangle> floors = new ArrayList<Rectangle>();
	public static List<Integer> floorLevel = new ArrayList<Integer>();
	public static List<Rectangle> levelEnd = new ArrayList<Rectangle>();

	// Define floors
	public static void defineFloors(int level){
		if (level == 1){
			createFloor(0, 600, 800, level);
			createFloor(700, 475, 800, level);
			createFloor(1650, 500, 500, level);
			createFloor(2300, 650, 1000, level);
			createFloor(3400, 500, 600, level);
			createFloor(4050, 600, 900, level);
			levelEnd.add(new Rectangle(4950, 600 - 150 + floorHeight, 50, 150));
		}
	}

	public static Rectangle createFloor(int x, int y, int l, int level){
		Rectangle r = new Rectangle(x, y, l, floorHeight);
		floors.add(r);
		floorLevel.add(level);
		return r;
	}
}
