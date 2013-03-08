/** ENEMIES **/
package net.amigocraft.Nightmare;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ba extends JPanel {

	private static final long serialVersionUID = 1L;

	public static HashMap<String, Integer[]> enemyDim = new HashMap<String, Integer[]>();
	public static List<String> enemyType = new ArrayList<String>();
	public static int eaMovementFrame = 0;
	public static int moveSpeed = 2;
	public static int eaMovementSpeed = moveSpeed * 4;
	public static List<Integer> aniStage = new ArrayList<Integer>();
	public static List<Integer> aniFrame = new ArrayList<Integer>();
	public static int aniSpeed = 200;
	public static Image skeleton1 = null;
	public static Image skeleton2 = null;
	public static Image skeleton3 = null;
	public static Image skeleton4 = null;
	public static Image skeletonF1 = null;
	public static Image skeletonF2 = null;
	public static Image skeletonF3 = null;
	public static Image skeletonF4 = null;
	public static List<Image> skeleton = new ArrayList<Image>();
	public static List<Image> skeletonF = new ArrayList<Image>();
	public static HashMap<String, List<Image>> enemySprites = new HashMap<String, List<Image>>();
	public static HashMap<String, List<Image>> enemySpritesF = new HashMap<String, List<Image>>();

	public static List<Rectangle> enemies = new ArrayList<Rectangle>();
	public static List<Integer> dir = new ArrayList<Integer>();

	public static boolean objectsDefined = false;

	// Define enemies
	public static void defineEnemies(){
		enemyDim.clear();
		enemyDim.put("skeleton", new Integer[]{26, 46});
		try {
			skeleton1 = ImageIO.read(ad.class.getResourceAsStream("/images/Skeleton1.png"));
			skeleton2 = ImageIO.read(ad.class.getResourceAsStream("/images/Skeleton2.png"));
			skeleton3 = ImageIO.read(ad.class.getResourceAsStream("/images/Skeleton3.png"));
			skeleton4 = ImageIO.read(ad.class.getResourceAsStream("/images/Skeleton4.png"));
			skeletonF1 = ImageIO.read(ad.class.getResourceAsStream("/images/SkeletonF1.png"));
			skeletonF2 = ImageIO.read(ad.class.getResourceAsStream("/images/SkeletonF2.png"));
			skeletonF3 = ImageIO.read(ad.class.getResourceAsStream("/images/SkeletonF3.png"));
			skeletonF4 = ImageIO.read(ad.class.getResourceAsStream("/images/SkeletonF4.png"));
			skeleton.add(skeleton1);
			skeleton.add(skeleton2);
			skeleton.add(skeleton3);
			skeleton.add(skeleton4);
			skeletonF.add(skeletonF1);
			skeletonF.add(skeletonF2);
			skeletonF.add(skeletonF3);
			skeletonF.add(skeletonF4);
			enemySprites.put("skeleton", skeleton);
			enemySpritesF.put("skeleton", skeletonF);
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		enemies.clear();
		dir.clear();
		aniStage.clear();
		defineEnemy(posRel(0, 1, 3), bb.floors.get(0).y, "skeleton");
		defineEnemy(posRel(1, 1, 2), bb.floors.get(1).y, "skeleton");
	}
	
	public static void defineEnemy(int x, int y, String type){
		enemies.add(new Rectangle(x - (enemyDim.get(type)[0] / 2), y - (enemyDim.get(type)[1]), enemyDim.get(type)[0], enemyDim.get(type)[1]));
		dir.add(0);
		aniStage.add(0);
		aniFrame.add(0);
		enemyType.add(type);
	}

	public static void run(){

		// Feet
		for (int i = 0; i < enemies.size(); i++){
			boolean left = false;
			boolean right = false;
			boolean falling = false;
			if (dir.get(i) == 0)
				left = true;
			else if (dir.get(i) == 1)
				right = true;
			else if (dir.get(i) == 2)
				falling = true;
			Rectangle enemy = enemies.get(i);
			Point foot1 = new Point();
			Point foot2 = new Point();
			foot1.setLocation(enemy.getX(), enemy.getY() + enemy.height);
			foot2.setLocation(enemy.getX() + enemy.width, enemy.getY() + enemy.height);

			// Edge check
			if (!containsFeet(foot1)){
				if (left){
					dir.set(i, 1);
					right = true;
					left = false;
				}
				else {
					dir.set(i, 0);
					left = true;
					right = false;
				}	
			}
			else if (!containsFeet(foot2)){
				if (!left){
					dir.set(i, 0);
					left = true;
					right = false;
				}
				else {
					dir.set(i, 1);
					right = true;
					left = false;
				}	
			}

			if (!containsFeet(foot1) && !containsFeet(foot2)){
				left = false;
				right = false;
				falling = true;
				dir.set(i, 2);
			}

			if (falling){
				enemy.y += 1;
			}

			// Movement
			if (eaMovementFrame >= eaMovementSpeed){
				if (right){
					enemy.x += 1;
				}
				else if (left){
					enemy.x -= 1;
				}
				eaMovementFrame = 0;
			}
			else {
				eaMovementFrame += 1;
			}

			if (enemy.intersects(ad.character)){
				if (!ad.invincible){
					ad.health -= 3;
					ad.knockback = true;
					if (ad.health > 0)
						ad.invincible = true;
					else
						ad.dead = true;
				}
			}
		}
	}

	public static boolean containsFeet(Point f1){
		for (int i = 0; i < bb.floors.size(); i++){
			if (bb.floors.get(i).contains(f1)){
				return true;
			}
		}
		return false;
	}
	
	public static int posRel(int floorIndex, int num, int den){
		return (int)(bb.floors.get(floorIndex).x + (bb.floors.get(floorIndex).width * (num / (double)den)));
	}
}