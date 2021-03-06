package net.amigocraft.Nightmare;

import static net.amigocraft.Nightmare.Direction.LEFT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class LevelDesigner {

	public static boolean initialized = false;

	public static List<Rectangle> platforms = new ArrayList<Rectangle>();
	public static List<Entity> entities = new ArrayList<Entity>();
	public static Rectangle selectedPlatform = null;
	public static Entity selectedEntity = null;
	public static boolean selectedChar = false;
	public static boolean drag = false;

	public static File f = null;

	public static int xs = 0;
	public static int ys = 0;

	public static long lastClick = 0;

	public static boolean left, right, up, down = false;

	public static int[] offset = new int[2];

	public static Rectangle hotbar = new Rectangle(100, WindowManager.height - 95, WindowManager.width - 200, 75);
	public static Rectangle platformSpawn = new Rectangle(125, WindowManager.height - 75, 100, PlatformManager.floorHeight);
	public static Rectangle character = new Rectangle(285, WindowManager.height - 88,
			CharacterManager.characterWidth, CharacterManager.characterHeight);

	public static void checkMouse(){
		if (WindowManager.f.getMousePosition() != null){
			Point mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
			if (System.currentTimeMillis() - lastClick >= 100){
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
					if (character.contains(mousePos)){
						offset = new int[]{mousePos.x - (int)character.getX(), mousePos.y - (int)character.getY()};
						selectedChar = true;
						drag = true;
					}
				}
				lastClick = System.currentTimeMillis();
			}

			if (drag && (selectedPlatform != null || selectedEntity != null || selectedChar)){
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
				else if (selectedChar){
					character.x = mousePos.x - offset[0] + xs;
					character.y = mousePos.y - offset[1] + ys;
				}
			}
		}
	}

	public static boolean generateFile(){
		if (f != null){
			if (f.exists()){
				GameManager.state = State.LC_OW;
			}
			else {
				overwriteFile();
			}
		}
		return false;
	}

	public static boolean overwriteFile(){
		if (f != null){
			try {
				f.createNewFile();
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				StringBuilder content = new StringBuilder();
				BufferedWriter bw = new BufferedWriter(fw);

				content.append("p " + character.x + " " + character.y + "\n");

				for (Rectangle r : platforms)
					content.append("f " + r.x + " " + r.y + " " + r.width + " " + r.height + "\n");

				for (Entity e : entities)
					content.append("e " + e.getType() + " " + e.getX() + " " + e.getY() + "\n");

				bw.write(content.toString());
				bw.close();
				return true;
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
		f = null;
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
		g.drawImage(CharacterManager.boyStandFront, character.x - xs, character.y - ys, null);

		// draw static objects (spawns)
		g.fillRect(platformSpawn.x, platformSpawn.y, platformSpawn.width, platformSpawn.height);
		for (EntitySpawn es : EntitySpawn.spawns)
			g.drawImage(LivingEntityManager.enemySprites.get(es.getEntity().getType()).get(0),
					es.getX(), es.getY(), null);
		g.setColor(Color.WHITE);
		g.drawRect(hotbar.x, hotbar.y, hotbar.width, hotbar.height);
	}
	public static void initialize(){

		platforms = new ArrayList<Rectangle>();
		entities = new ArrayList<Entity>();

		GameManager.f.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if (e.getButton() == 1)
					checkMouse();
			}

			public void mouseReleased(MouseEvent e){
				if (e.getButton() == 1){
					selectedPlatform = null;
					selectedEntity = null;
					selectedChar = false;
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

	public static void chooseLoad(Component c){
		final JFileChooser fc = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("Nightmare Level File (*.nml)", new String[] {"nml"});
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(c);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			String fPath = fc.getSelectedFile().getAbsolutePath();
			if (!fPath.endsWith(".nml"))
				fPath += ".nml";
			f = new File(fPath);

			InputStream fis;
			BufferedReader br;
			String line;
			try {
				fis = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
				while ((line = br.readLine()) != null) {
					if (line.startsWith("f ")){
						String[] el = line.split(" ");
						platforms.add(new Rectangle(Integer.parseInt(el[1]),
								Integer.parseInt(el[2]),
								Integer.parseInt(el[3]),
								Integer.parseInt(el[4])));
					}
					else if (line.startsWith("e ")){
						String[] el = line.split(" ");
						entities.add(new Entity(Integer.parseInt(el[2]),
								Integer.parseInt(el[3]),
								el[1]));
					}
					else if (line.startsWith("p ")){
						String[] el = line.split(" ");
						character.x = Integer.parseInt(el[1]);
						character.y = Integer.parseInt(el[2]);
					}
					else if (line.startsWith("#")){}
					else {
						GameManager.state = State.LOAD_FAIL;
						break;
					}
				}
				br.close();
			}
			catch (Exception ex){
				ex.printStackTrace();
				GameManager.state = State.LOAD_FAIL;
			}
			br = null;
			fis = null;
		}
		//TODO: Handle cancellation
	}

	public static void chooseSave(Component c){
		final JFileChooser fc = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("Nightmare Level File (*.nml)", new String[] {"nml"});
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(c);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			String fPath = fc.getSelectedFile().getAbsolutePath();
			if (!fPath.endsWith(".nml"))
				fPath += ".nml";
			f = new File(fPath);
			generateFile();
		}
		//TODO: Handle cancellation
	}

	public static void loadLevel(Component c){
		final JFileChooser fc = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("Nightmare Level File (*.nml)", new String[] {"nml"});
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(c);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			int cx = 0;
			int cy = 0;
			String fPath = fc.getSelectedFile().getAbsolutePath();
			if (!fPath.endsWith(".nml"))
				fPath += ".nml";
			f = new File(fPath);

			for (Enemy e : LivingEntityManager.enemies)
				if (e.getLevel() == 0)
					LivingEntityManager.enemies.remove(e);

			List<Integer> removeIndices = new ArrayList<Integer>();
			for (int i = 0; i < PlatformManager.floorLevel.size(); i++)
				if (PlatformManager.floorLevel.get(i) == 0){
					PlatformManager.floors.remove(i);
					removeIndices.add(i);
				}
			for (Integer i : removeIndices){
				PlatformManager.floorLevel.remove(i.intValue());
			}

			InputStream fis;
			BufferedReader br;
			String line;
			try {
				fis = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
				while ((line = br.readLine()) != null) {
					if (line.startsWith("f ")){
						String[] el = line.split(" ");
						PlatformManager.createFloor(Integer.parseInt(el[1]),
								Integer.parseInt(el[2]),
								Integer.parseInt(el[3]),
								0);
					}
					else if (line.startsWith("e ")){
						System.out.println(line);
						String[] el = line.split(" ");
						LivingEntityManager.createEnemy(Integer.parseInt(el[2]),
								Integer.parseInt(el[3]),
								el[1], 0);
						LivingEntityManager.enemies.add(new Enemy(el[1], 0,
								LivingEntityManager.enemySprites.get(el[1]),
								LivingEntityManager.enemySpritesF.get(el[1]),
								new int[]{Integer.parseInt(el[2]) -
							(LivingEntityManager.enemyDim.get(el[1])[0] / 2),
							Integer.parseInt(el[3]) - (LivingEntityManager.enemyDim.get(el[1])[1])}, LEFT,
							LivingEntityManager.enemyDim.get(el[1])));
					}
					else if (line.startsWith("p ")){
						String[] el = line.split(" ");
						cx = Integer.parseInt(el[1]);
						cy = Integer.parseInt(el[2]);
					}
					else if (line.startsWith("#")){}
					else {
						GameManager.state = State.LOAD_FAIL;
						break;
					}
				}
				br.close();
			}
			catch (Exception ex){
				ex.printStackTrace();
				GameManager.state = State.LOAD_FAIL;
			}
			br = null;
			fis = null;
			GameManager.level = 0;
			GameManager.resetLevel();
			CharacterManager.character.x = cx;
			CharacterManager.character.y = cy;
			GameManager.state = State.GAME;
			//TODO: Handle cancellation
		}
	}

}
