package net.amigocraft.Nightmare;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;

public class Entity {
	
	private int x;
	private int y;
	private int width;
	private int height;
	private String type;
	private int level;
	private List<Image> sprites;
	private List<Image> spritesF;
	
	public int aniFrame = 0;
	public int aniTick = 0;
	public int aniDelay = 75;
	
	public Entity(int x, int y, int width, int height, String type, int level, List<Image> sprites, List<Image> spritesF){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		this.level = level;
		this.sprites = sprites;
		this.spritesF = spritesF;
		EntityManager.entities.add(this);
	}
	
	public Entity(int x, int y, String type, int level){
		this.x = x;
		this.y = y;
		this.width = EntityManager.coinSprites.get(0).getWidth(null);
		this.height = EntityManager.coinSprites.get(0).getHeight(null);
		this.type = type;
		this.level = level;
		EntityManager.entities.add(this);
	}
	
	public Entity(int x, int y, String type){
		this.x = x;
		this.y = y;
		this.width = EntityManager.coinSprites.get(0).getWidth(null);
		this.height = EntityManager.coinSprites.get(0).getHeight(null);
		this.type = type;
		this.level = -1;
		EntityManager.entities.add(this);
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
	
	public List<Image> getSprites(){
		return sprites;
	}
	
	public List<Image> getSpritesF(){
		return spritesF;
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
	
	public void setSprites(List<Image> sprites){
		this.sprites = sprites;
	}
	
	public void setSpritesF(List<Image> spritesF){
		this.spritesF = spritesF;
	}
	
	public void destroy(){
		EntityManager.entities.remove(this);
	}
	
	public boolean checkPlayerIntersect(){
		Rectangle r = new Rectangle(x, y, width, height);
		if (r.intersects(CharacterManager.character))
			return true;
		return false;
	}
}
