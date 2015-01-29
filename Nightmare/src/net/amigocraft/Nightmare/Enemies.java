/** ENEMIES **/
package net.amigocraft.nightmare;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Enemies extends JPanel {

	private static final long serialVersionUID = 1L;

	public static HashMap<String, int[]> enemyDim = new HashMap<String, int[]>();
	public static List<String> enemyType = new ArrayList<String>();
	public static final int moveSpeed = 2;
	public static final int eaMovementSpeed = moveSpeed * 4;
	public static final int aniSpeed = 200;
	public static List<Image> skeleton = new ArrayList<Image>();
	public static List<Image> skeletonF = new ArrayList<Image>();
	
	public static HashMap<String, List<Image>> enemySprites = new HashMap<String, List<Image>>();
	public static HashMap<String, List<Image>> enemySpritesF = new HashMap<String, List<Image>>();
	
	public static List<Enemy> enemies = new ArrayList<Enemy>();
	

	public static boolean objectsDefined = false;

	// Define enemies
	public static void defineEnemies(){
		enemyDim.clear();
		enemyDim.put("skeleton", new int[]{26, 46});
		try {
			skeleton.add(ImageIO.read(Character.class.getResourceAsStream("/images/Skeleton1.png")));
			skeleton.add(ImageIO.read(Character.class.getResourceAsStream("/images/Skeleton2.png")));
			skeleton.add(ImageIO.read(Character.class.getResourceAsStream("/images/Skeleton3.png")));
			skeleton.add(ImageIO.read(Character.class.getResourceAsStream("/images/Skeleton4.png")));
			skeletonF.add(ImageIO.read(Character.class.getResourceAsStream("/images/SkeletonF1.png")));
			skeletonF.add(ImageIO.read(Character.class.getResourceAsStream("/images/SkeletonF2.png")));
			skeletonF.add(ImageIO.read(Character.class.getResourceAsStream("/images/SkeletonF3.png")));
			skeletonF.add(ImageIO.read(Character.class.getResourceAsStream("/images/SkeletonF4.png")));
			enemySprites.put("skeleton", skeleton);
			enemySpritesF.put("skeleton", skeletonF);
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		enemies.clear();
		createEnemy(posRel(0, 1, 3), Platforms.floors.get(0).y, "skeleton", 1);
		createEnemy(posRel(1, 1, 2), Platforms.floors.get(1).y, "skeleton", 1);
	}
	
	public static void createEnemy(int x, int y, String type, int level){
		enemies.add(new Enemy(type, level, enemySprites.get(type), enemySpritesF.get(type), new int[]{x - (enemyDim.get(type)[0] / 2), y - (enemyDim.get(type)[1])}, 0, enemyDim.get(type)));
	}

	public static void run(){

		// Feet
		for (Enemy e : enemies){
			boolean left = false;
			boolean right = false;
			boolean falling = false;
			if (e.getDirection() == 0)
				left = true;
			else if (e.getDirection() == 1)
				right = true;
			else if (e.getDirection() == 2)
				falling = true;
			Point foot1 = new Point();
			Point foot2 = new Point();
			foot1.setLocation(e.getX(), e.getY() + e.getHeight());
			foot2.setLocation(e.getX() + e.getWidth(), e.getY() + e.getHeight());

			// Edge check
			if (!containsFeet(foot1)){
				if (left){
					e.setDirection(1);
					right = true;
					left = false;
				}
				else {
					e.setDirection(0);
					left = true;
					right = false;
				}	
			}
			else if (!containsFeet(foot2)){
				if (!left){
					e.setDirection(0);
					left = true;
					right = false;
				}
				else {
					e.setDirection(1);
					right = true;
					left = false;
				}	
			}

			if (!containsFeet(foot1) && !containsFeet(foot2)){
				left = false;
				right = false;
				falling = true;
				e.setDirection(2);
			}

			if (falling){
				e.setY(e.getY() + 1);
			}

			// Movement
			if (e.getMovementFrame() >= eaMovementSpeed){
				if (right){
					e.setX(e.getX() + 1);
				}
				else if (left){
					e.setX(e.getX() - 1);
				}
				e.setMovementFrame(0);
			}
			else {
				e.setMovementFrame(e.getMovementFrame() + 1);
			}
			
			// animation
			if (e.getAnimationFrame() >= Enemies.aniSpeed){
				List<Image> loop = null;
				if (e.getDirection() == 0)
					loop = e.getSprites();
				else
					loop = e.getSpritesF();
				if (MainCanvas.inGame){
					if (e.getAnimationStage() < loop.size() - 1){
						e.setAnimationStage(e.getAnimationStage() + 1);
					}
					else
						e.setAnimationStage(0);
				}
				e.setAnimationFrame(0);
			}
			else
				e.setAnimationFrame(e.getAnimationFrame() + 1);

			if (new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight()).intersects(Character.character)){
				if (!Character.invincible){
					Character.health -= 3;
					Character.knockback = true;
					if (Character.health > 0)
						Character.invincible = true;
					else
						Character.dead = true;
				}
			}
		}
	}

	public static boolean containsFeet(Point f1){
		for (int i = 0; i < Platforms.floors.size(); i++){
			if (Platforms.floors.get(i).contains(f1)){
				return true;
			}
		}
		return false;
	}
	
	public static int posRel(int floorIndex, int num, int den){
		return (int)(Platforms.floors.get(floorIndex).x + (Platforms.floors.get(floorIndex).width * (num / (double)den)));
	}
}