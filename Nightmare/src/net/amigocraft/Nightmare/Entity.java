package net.amigocraft.Nightmare;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Entity {
	
	private int x;
	private int y;
	private int width;
	private int height;
	private String type;
	private int level;
	
	public static int aniFrame = 0;
	public static int aniTick = 0;
	public static int aniDelay = 75;
	
	public static List<Image> coinSprites = new ArrayList<Image>();
	
	public static List<Entity> entities = new ArrayList<Entity>();
	
	public Entity(int x, int y, int width, int height, String type, int level){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		this.level = level;
		entities.add(this);
	}
	
	public Entity(int x, int y, String type, int level){
		this.x = x;
		this.y = y;
		this.width = coinSprites.get(0).getWidth(null);
		this.height = coinSprites.get(0).getHeight(null);
		this.type = type;
		this.level = level;
		entities.add(this);
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public String getType(){
		return type;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public void setHeight(int height){
		this.height = height;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void destroy(){
		entities.remove(this);
	}
	
	public boolean checkPlayerIntersect(){
		Rectangle r = new Rectangle(x, y, width, height);
		if (r.intersects(CharacterManager.character))
			return true;
		return false;
	}
	
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
