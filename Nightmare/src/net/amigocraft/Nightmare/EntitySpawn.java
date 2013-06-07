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
	
}
