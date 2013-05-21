package net.amigocraft.Nightmare;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LevelDesigner {

	public static List<Rectangle> platforms = new ArrayList<Rectangle>();
	public static List<Entity> entities = new ArrayList<Entity>();
	public static Rectangle selectedPlatform = null;
	public static Entity selectedEntity = null;
	public static String selectedType = null;
	public static boolean drag = false;

	public static Rectangle hotbar = new Rectangle(100, WindowManager.height - 95, WindowManager.width - 200, 75);
	public static Rectangle platformSpawn = new Rectangle(125, WindowManager.height - 85, 100, 25);
	public static Rectangle skeletonSpawn = new Rectangle(225, WindowManager.height - 85,
			LivingEntityManager.enemyDim.get("skeleton")[0], LivingEntityManager.enemyDim.get("skeleton")[1]);

	public static void checkMouse(){
		if (WindowManager.f.getMousePosition() != null){
			Point mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
			if (platformSpawn.contains(mousePos) && !drag){
				Rectangle platform = new Rectangle(mousePos.x, mousePos.y, platformSpawn.width, platformSpawn.height);
				platforms.add(platform);
				selectedPlatform = platform;
				drag = true;
			}
			else if (skeletonSpawn.contains(mousePos) && !drag){
				entities.add(new Entity(skeletonSpawn.x, skeletonSpawn.x, "skeleton"));
				selectedEntity = new Entity(skeletonSpawn.x, skeletonSpawn.x, "skeleton");
				drag = true;
			}
			if (!drag){
				for (Rectangle r : platforms){
					if (r.contains(mousePos)){
						selectedPlatform = r;
						drag = true;
						break;
					}
				}
				for (Entity e : entities){
					if (new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight()).contains(mousePos)){
						selectedEntity = e;
						drag = true;
						break;
					}
				}
			}

			if (drag && (selectedPlatform != null || selectedEntity != null)){
				if (selectedPlatform != null){
					platforms.remove(selectedPlatform);
					selectedPlatform = new Rectangle(mousePos.x, mousePos.y, selectedPlatform.width, selectedPlatform.height);
					platforms.add(selectedPlatform);
				}
				else if (selectedEntity != null){
					entities.remove(selectedEntity);
					selectedEntity.setX(mousePos.x);
					selectedEntity.setY(mousePos.y);
					entities.add(selectedEntity);
				}
			}
		}
	}

	public static boolean generateFile(File file){
		if (file.exists()){
			//TODO: Prompt user to overwrite
		}
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			StringBuilder content = new StringBuilder();
			BufferedWriter bw = new BufferedWriter(fw);

			for (Rectangle r : platforms)
				content.append("p " + r.x + " " + r.y + " " + r.width + " " + r.height);

			for (Entity e : entities)
				content.append("e " + e.getType() + e.getX() + e.getY());

			bw.write(content.toString());
			bw.close();
			return true;
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	public static void drawObjects(Graphics g){

		if (drag){
			checkMouse();
		}

		g.setColor(Color.RED);

		// draw dynamic objects
		for (Rectangle p : platforms)
			g.fillRect(p.x, p.y, p.width, p.height);
		for (Entity e : entities)
			g.drawImage(LivingEntityManager.enemySprites.get(e.getType()).get(e.aniFrame), e.getX(), e.getY(), null);

		// draw static objects (spawns)
		g.fillRect(platformSpawn.x, platformSpawn.y, platformSpawn.width, platformSpawn.height);
		g.drawImage(LivingEntityManager.enemySprites.get("skeleton").get(0), skeletonSpawn.x, skeletonSpawn.y, null);
		g.setColor(Color.WHITE);
		g.drawRect(hotbar.x, hotbar.y, hotbar.width, hotbar.height);
	}
	public static void checkClick(){
		GameManager.f.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				System.out.println("clicked");
				if (e.getButton() == 1)
					checkMouse();
			}

			public void mouseReleased(MouseEvent e){
				System.out.println("released");
				if (e.getButton() == 1){
					selectedPlatform = null;
					selectedEntity = null;
					drag = false;
				}
			}
		});
	}

}
