package net.amigocraft.Nightmare;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class EntityManager {

	public static List<Image> coinSprites = new ArrayList<Image>();
	public static List<Entity> entities = new ArrayList<Entity>();
	
	public static void initialize(){
		try {
			coinSprites.clear();
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin1.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin2.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin3.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin4.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin5.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin6.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin7.png")));
			coinSprites.add(ImageIO.read(Entity.class.getResourceAsStream("/images/coin8.png")));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void setupEntities(){
		new Entity(150, PlatformManager.floors.get(0).y - PlatformManager.floorHeight - 10, "coin", 1);
		new Entity(200, PlatformManager.floors.get(0).y - PlatformManager.floorHeight - 10, "coin", 1);
		new Entity(250, PlatformManager.floors.get(0).y - PlatformManager.floorHeight - 10, "coin", 1);
	}
	
}
