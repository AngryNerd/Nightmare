package net.amigocraft.Nightmare;

import java.util.ArrayList;
import java.util.List;

public class EntitySpawn {
	
	public static List<EntitySpawn> spawns = new ArrayList<EntitySpawn>();
	
	private Entity e;
	private int x;
	private int y;
	
	public EntitySpawn(Entity e, int x, int y){
		this.e = e;
		this.x = x;
		this.y = y;
		spawns.add(this);
	}
	
	public Entity getEntity(){
		return e;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setEntity(Entity e){
		this.e = e;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
}
