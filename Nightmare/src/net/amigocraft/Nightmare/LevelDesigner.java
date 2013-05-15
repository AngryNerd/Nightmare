package net.amigocraft.Nightmare;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelDesigner {

	public static List<Rectangle> platforms = new ArrayList<Rectangle>();
	public static HashMap<Rectangle, String> entities = new HashMap<Rectangle, String>();
	public static Rectangle selected = null;
	public static String selectedType = null;
	public static boolean drag = false;

	public static Rectangle hotbar = new Rectangle(100, WindowManager.height - 50, WindowManager.width - 200, 100);
	public static Rectangle platformSpawn = new Rectangle(200, WindowManager.height - 60, 100, 80);
	public static Rectangle skeletonSpawn = new Rectangle(2, WindowManager.height - 60,
			LivingEntityManager.enemyDim.get("skeleton")[0], LivingEntityManager.enemyDim.get("skeleton")[1]);

	public static void checkMouse(){
		Point mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
		if (platformSpawn.contains(mousePos)){
			Rectangle platform = new Rectangle(platformSpawn.x, platformSpawn.y, platformSpawn.width, platformSpawn.height);
			platforms.add(platform);
			selected = platform;
			selectedType = "platform";
		}
		else if (skeletonSpawn.contains(mousePos)){
			Rectangle entity = new Rectangle(skeletonSpawn.x, skeletonSpawn.y, skeletonSpawn.width, skeletonSpawn.height);
			entities.put(entity, "skeleton");
			selected = entity;
			selectedType = "platform";
		}
		else if (platformSpawn.contains(mousePos)){
			Rectangle platform = new Rectangle(platformSpawn.x, platformSpawn.y, platformSpawn.width, platformSpawn.height);
			platforms.add(platform);
			selected = platform;
			selectedType = "platform";
		}

		if (drag && selected != null){
			selected = new Rectangle(mousePos.x, mousePos.y, selected.width, selected.height);
		}
	}

	public boolean generateFile(File file){
		if (file.exists()){
			//TODO: Prompt user to overwrite
		}
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			StringBuilder content = new StringBuilder();
			BufferedWriter bw = new BufferedWriter(fw);

			for (Rectangle r : platforms){
				content.append("p " + r.x + " " + r.y + " " + r.width + " " + r.height);
			}
			for (Rectangle r : entities.keySet()){
				content.append("e " + entities.get(r) + r.x + r.y);
			}

			bw.write(content.toString());
			bw.close();
			return true;
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
}
