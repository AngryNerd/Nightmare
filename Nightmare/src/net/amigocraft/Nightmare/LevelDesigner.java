package net.amigocraft.Nightmare;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LevelDesigner {

	public static boolean initialized = false;

	public static List<Rectangle> platforms = new ArrayList<Rectangle>();
	public static List<Entity> entities = new ArrayList<Entity>();
	public static Rectangle selectedPlatform = null;
	public static Entity selectedEntity = null;
	public static String selectedType = null;
	public static boolean drag = false;

	public static int xs = 0;
	public static int ys = 0;
	
	public static boolean left, right, up, down = false;

	public static int[] offset = new int[2];

	public static Rectangle hotbar = new Rectangle(100, WindowManager.height - 95, WindowManager.width - 200, 75);
	public static Rectangle platformSpawn = new Rectangle(125, WindowManager.height - 75, 100, PlatformManager.floorHeight);

	public static void checkMouse(){
		if (WindowManager.f.getMousePosition() != null){
			Point mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
			if (platformSpawn.contains(mousePos) && !drag){
				offset = new int[]{mousePos.x - (int)platformSpawn.getX(), mousePos.y - (int)platformSpawn.getY()};
				Rectangle platform = new Rectangle(mousePos.x - offset[0], mousePos.y - offset[1],
						platformSpawn.width, platformSpawn.height);
				platforms.add(platform);
				selectedPlatform = platform;
				drag = true;
			}
			else {
				for (EntitySpawn es : EntitySpawn.spawns){
					if (new Rectangle(es.getX(), es.getY(),
							es.getEntity().getWidth(), es.getEntity().getHeight() + 20).contains(mousePos)){
						offset = new int[]{mousePos.x - (int)es.getX(),
								mousePos.y - (int)es.getY()};
						Entity e = new Entity(es.getX() - offset[0], es.getX() - offset[1],
								es.getEntity().getType());
						entities.add(e);
						selectedEntity = e;
						drag = true;
					}
				}
			}
			if (!drag){
				for (Rectangle r : platforms){
					if (new Rectangle(r.x - xs, r.y - ys, r.width, r.height).contains(mousePos)){
						offset = new int[]{mousePos.x - r.x + xs, mousePos.y - r.y + ys};
						selectedPlatform = r;
						drag = true;
						break;
					}
				}
				for (Entity e : entities){
					if (new Rectangle(e.getX() - xs, e.getY() - ys, e.getWidth(), e.getHeight()).contains(mousePos)){
						offset = new int[]{mousePos.x - e.getX() + xs, mousePos.y - e.getY() + ys};
						selectedEntity = e;
						drag = true;
						break;
					}
				}
			}

			if (drag && (selectedPlatform != null || selectedEntity != null)){
				if (selectedPlatform != null){
					platforms.remove(selectedPlatform);
					selectedPlatform = new Rectangle(mousePos.x - offset[0] + xs, mousePos.y - offset[1] + ys,
							selectedPlatform.width, selectedPlatform.height);
					platforms.add(selectedPlatform);
				}
				else if (selectedEntity != null){
					entities.remove(selectedEntity);
					selectedEntity.setX(mousePos.x - offset[0] + xs);
					selectedEntity.setY(mousePos.y - offset[1] + ys);
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
		
		if (left)
			xs -= 1;
		else if (right)
			xs += 1;
		if (up)
			ys -= 1;
		else if (down)
			ys += 1;

		if (drag){
			checkMouse();
		}

		g.setColor(PlatformManager.floorColor);

		// draw dynamic objects
		for (Rectangle p : platforms)
			g.fillRect(p.x - xs, p.y - ys, p.width, p.height);
		for (Entity e : entities)
			g.drawImage(LivingEntityManager.enemySprites.get(e.getType()).get(e.aniFrame),
					e.getX() - xs, e.getY() - ys, null);

		// draw static objects (spawns)
		g.fillRect(platformSpawn.x, platformSpawn.y, platformSpawn.width, platformSpawn.height);
		for (EntitySpawn es : EntitySpawn.spawns)
			g.drawImage(LivingEntityManager.enemySprites.get(es.getEntity().getType()).get(0),
					es.getX(), es.getY(), null);
		g.setColor(Color.WHITE);
		g.drawRect(hotbar.x, hotbar.y, hotbar.width, hotbar.height);
	}
	public static void initialize(){
		GameManager.f.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if (e.getButton() == 1)
					checkMouse();
			}

			public void mouseReleased(MouseEvent e){
				if (e.getButton() == 1){
					selectedPlatform = null;
					selectedEntity = null;
					drag = false;
				}
			}
		});
		GameManager.f.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == CharacterManager.keyLeft || e.getKeyCode() == CharacterManager.keyLeft2)
					left = true;
				else if (e.getKeyCode() == CharacterManager.keyRight || e.getKeyCode() == CharacterManager.keyRight2)
					right = true;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
					down = true;
				else if (e.getKeyCode() == CharacterManager.keyJump || e.getKeyCode() == CharacterManager.keyJump2)
					up = true;
			}
			
			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == CharacterManager.keyLeft || e.getKeyCode() == CharacterManager.keyLeft2)
					left = false;
				else if (e.getKeyCode() == CharacterManager.keyRight || e.getKeyCode() == CharacterManager.keyRight2)
					right = false;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
					down = false;
				else if (e.getKeyCode() == CharacterManager.keyJump || e.getKeyCode() == CharacterManager.keyJump2)
					up = false;
			}
		});

		createEnemySpawn("skeleton", 235, WindowManager.height - 85);

		initialized = true;
	}

	public static EntitySpawn createEnemySpawn(String type, int x, int y){
		return new EntitySpawn(
				new Entity(
						x, y, LivingEntityManager.enemyDim.get(type)[0],
						LivingEntityManager.enemyDim.get(type)[0], type, 0,
						LivingEntityManager.enemySprites.get(type),
						LivingEntityManager.enemySpritesF.get(type)
						),
						x, y
				);
	}

}
