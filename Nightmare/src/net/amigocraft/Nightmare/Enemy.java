package net.amigocraft.Nightmare;

import java.awt.Image;
import java.util.List;

public class Enemy {
	
	private String type;
	private int level;
	private int aniStage = 0;
	private int aniFrame = 0;
	private int movementFrame = 0;
	private List<Image> sprites;
	private List<Image> spritesF;
	private int[] position;
	private int direction;
	private int[] dim;
	
	public Enemy(String type, int level, List<Image> sprites, List<Image> spritesF, int[] position, int direction, int[] dim){
		this.type = type;
		this.sprites = sprites;
		this.spritesF = spritesF;
		this.direction = direction;
		this.dim = dim;
		this.position = position;
		this.level = level;
	}
	
	public String getType(){
		return type;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getAnimationStage(){
		return aniStage;
	}
	
	public int getAnimationFrame(){
		return aniFrame;
	}
	
	public int getMovementFrame(){
		return movementFrame;
	}
	
	public List<Image> getSprites(){
		return sprites;
	}
	
	public List<Image> getSpritesF(){
		return spritesF;
	}
	
	public int[] getPosition(){
		return position;
	}
	
	public int getX(){
		return this.position[0];
	}
	
	public int getY(){
		return this.position[1];
	}
	
	public int getDirection(){
		return direction;
	}
	
	public int[] getDimensions(){
		return dim;
	}
	
	public int getWidth(){
		return dim[0];
	}
	
	public int getHeight(){
		return dim[1];
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void setAnimationStage(int stage){
		this.aniStage = stage;
	}
	
	public void setAnimationFrame(int frame){
		this.aniFrame = frame;
	}
	
	public void setMovementFrame(int frame){
		this.movementFrame = frame;
	}
	
	public void setSprites(List<Image> sprites){
		this.sprites = sprites;
	}
	
	public void setSpritesF(List<Image> spritesF){
		this.spritesF = spritesF;
	}
	
	public void setDimensions(int[] dim){
		this.dim = dim;
	}
	
	public void setDimensions(int x, int y){
		this.dim = new int[]{x, y};
	}
	
	public void setWidth(int width){
		this.dim[0] = width;
	}
	
	public void setHeight(int height){
		this.dim[1] = height;
	}
	
	public void setPosition(int[] pos){
		this.position = pos;
	}
	
	public void setPosition(int x, int y){
		this.position = new int[]{x, y};
	}
	
	public void setX(int x){
		this.position[0] = x;
	}
	
	public void setY(int y){
		this.position[1] = y;
	}
	
	public void setDirection(int dir){
		this.direction = dir;
	}
	
}
