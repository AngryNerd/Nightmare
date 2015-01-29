/** GRAPHICS AND KEY LISTENER **/
package net.amigocraft.nightmare;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class MainCanvas extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	public int fps = 1000;

	public static int konamiStage = 0;

	public static boolean objectsDefined = false;
	public static boolean running = true;
	public static boolean inGame = false;
	public static boolean menu = true;
	public static boolean paused = false;
	public static boolean konami = false;
	public static boolean hovering = false;
	public static boolean win = false;

	public static List<Integer> menuStarX = new ArrayList<Integer>();
	public static List<Integer> menuStarY = new ArrayList<Integer>();
	public static int menuBgFrame = 0;
	public static int menuBgLoc = 0;
	public static int menuSpeed = 250;
	public static int level = 1;

	// define main menu buttons
	Rectangle playBtn = new Rectangle(Main.width / 2 - 80, 175, 160, 40);
	Rectangle tutBtn = new Rectangle(Main.width / 2 - 80, 245, 160, 40);
	Rectangle lbBtn = new Rectangle(Main.width / 2 - 80, 315, 160, 40);
	Rectangle quitBtn = new Rectangle(Main.width / 2 - 80, Main.height - 90, 160, 40);

	// define pause menu buttons
	Rectangle resBtn = new Rectangle(Main.width / 2 - 80, 150, 160, 40);
	Rectangle restartBtn = new Rectangle(Main.width / 2 - 80, 220, 160, 40);
	Rectangle pauseQuitBtn = new Rectangle(Main.width / 2 - 80, 290, 160, 40);

	Font font = new Font("Verdana", Font.BOLD, 30);
	Font smallFont = new Font("Verdana", Font.BOLD, 10);
	Font titleFont = new Font("Verdana", Font.BOLD, 50);
	Font btnFont = new Font("Verdana", Font.BOLD, 15);

	public Thread game;

	public MainCanvas(MainWindow f){

		// Key Listener
		f.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == Character.keyLeft || e.getKeyCode() == Character.keyLeft2){
					Character.left = true;
					Character.right = false;
				}

				if (e.getKeyCode() == Character.keyRight || e.getKeyCode() == Character.keyRight2){
					Character.right = true;
					Character.left = false;
				}
				if (e.getKeyCode() == Character.keyJump || e.getKeyCode() == Character.keyJump2 || e.getKeyCode() == Character.keyJump3){
					if (!Character.falling){
						Character.jumping = true;
					}
				}
				if (e.getKeyCode() == Character.keyPause){
					if (inGame && !paused){
						inGame = false;
						paused = true;
					}
					else {
						inGame = true;
						paused = false;
					}
				}

				// Konami
				if (konamiStage != 0){
					if (konamiStage == 1 && e.getKeyCode() == KeyEvent.VK_UP)
						konamiStage += 1;
					else if (konamiStage == 2 && e.getKeyCode() == KeyEvent.VK_DOWN)
						konamiStage += 1;
					else if (konamiStage == 3 && e.getKeyCode() == KeyEvent.VK_DOWN)
						konamiStage += 1;
					else if (konamiStage == 4 && e.getKeyCode() == KeyEvent.VK_LEFT)
						konamiStage += 1;
					else if (konamiStage == 5 && e.getKeyCode() == KeyEvent.VK_RIGHT)
						konamiStage += 1;
					else if (konamiStage == 6 && e.getKeyCode() == KeyEvent.VK_LEFT)
						konamiStage += 1;
					else if (konamiStage == 7 && e.getKeyCode() == KeyEvent.VK_RIGHT)
						konamiStage += 1;
					else if (konamiStage == 8 && e.getKeyCode() == KeyEvent.VK_B)
						konamiStage += 1;
					else if (konamiStage == 9 && e.getKeyCode() == KeyEvent.VK_A)
						konami = true;
					else
						konamiStage = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP){
					if (konamiStage == 0 || konamiStage == 1)
						konamiStage = 1;
				}
			}

			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == Character.keyLeft || e.getKeyCode() == Character.keyLeft2){
					Character.left = false;
					if (!Character.right){
						Character.lastDir = 0;
					}
				}

				if (e.getKeyCode() == Character.keyRight || e.getKeyCode() == Character.keyRight2){
					Character.right = false;
					if (!Character.left){
						Character.lastDir = 1;
					}
				}

			}
		});

		// mouse listener
		f.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getButton() == 1){
					Point mousePos = new Point(Main.f.getMousePosition().x, Main.f.getMousePosition().y - 24);
					if (menu || paused){
						if (playBtn.contains(mousePos) && menu){
							menu = false;
							inGame = true;
							level = 1;
						}
						else if (quitBtn.contains(mousePos) && menu){
							inGame = false;
							Main.pullThePlug();
						}
						else if (resBtn.contains(mousePos) && paused){
							paused = false;
							inGame = true;
						}
						else if (restartBtn.contains(mousePos) && paused){
							paused = false;
							inGame = true;
							Character.character.x = Main.width / 2;
							Character.character.y = Main.height / 2;
							Character.xs = 0;
							Character.ys = 0;
							Character.backgroundLoc = 0;
							Character.centerX = Main.width / 2 + Character.xs;
							Character.centerY = Main.width / 2 + Character.ys;
							Enemies.defineEnemies();
						}
						else if (pauseQuitBtn.contains(mousePos) && paused){
							paused = false;
							menu = true;
						}
					}
				}
			}
		});

		//Color bgcolor = new Color(0x660000);
		Color bgcolor = new Color(0x000000);
		setBackground(bgcolor);
		defineObjects();
		game = new Thread(this);
		game.start();
	}

	// Define objects
	public void defineObjects(){
		Character.character = Character.defineChar();
		Platforms.defineFloors(level);
		Enemies.defineEnemies();
		// define star locations
		for (int i = 0; i < Character.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((Main.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((Main.height - 0) + 1));
			Character.starX.add(x);
			Character.starY.add(y);
		}
		for (int i = 0; i < Character.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((Main.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((Main.height - 0) + 1));
			menuStarX.add(x);
			menuStarY.add(y);
		}

		// define custom fonts
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, MainCanvas.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(45f);
			smallFont = Font.createFont(Font.TRUETYPE_FONT, MainCanvas.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(20f);
			titleFont = Font.createFont(Font.TRUETYPE_FONT, MainCanvas.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(85f);
			btnFont = Font.createFont(Font.TRUETYPE_FONT, MainCanvas.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(25f);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		objectsDefined = true;
		repaint();
		if (Character.aniFrame == 0)
			Character.aniFrame = 1;
		else
			Character.aniFrame = 0;
	}
	
	public static void resetLevel(){
		Character.character = Character.defineChar();
		Platforms.defineFloors(level);
		Enemies.defineEnemies();
		Character.xs = 0;
		Character.ys = 0;
		Character.starX.clear();
		Character.starY.clear();
		for (int i = 0; i < Character.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((Main.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((Main.height - 0) + 1));
			Character.starX.add(x);
			Character.starY.add(y);
		}
		Character.centerX = Main.width / 2 + Character.xs;
		Character.centerY = Main.width / 2 + Character.ys;
	}

	public static void colorFloors(Graphics g, Rectangle f){
		g.setColor(new Color(0x6600000));
		try {
			g.fillRect(f.x - Character.xs, f.y - Character.ys, f.width, f.height);
		}
		catch (Exception e){}
	}

	public static void colorChar(Graphics g, Rectangle f){
		g.setColor(Color.BLACK);
		g.fillRect(f.x - Character.xs, f.y - Character.ys, f.width, f.height);
	}

	public int centerText(Graphics g, String text){
		int stringLen = (int)
				g.getFontMetrics().getStringBounds(text, g).getWidth();
		return Main.width / 2 - stringLen / 2;
	}

	public void drawText(Graphics g){

		g.setFont(font);

		// draw lives
		g.setColor(Color.WHITE);
		g.drawImage(Character.boyStandFront, Main.width - 100, Main.height - 90, this);
		g.drawString("x", Main.width - 65, Main.height - 50);
		g.drawString(Integer.toString(Character.lives), Main.width - 40, Main.height - 50);

		// draw pause message
		/*if (!inGame && paused){
			g.setColor(Color.WHITE);
			g.drawString("Paused", centerText(g, "Paused"), Main.height / 2);
		}*/

		// draw death messages
		if (Character.dead){
			g.setColor(Color.RED);
			if (Character.lives > 0){
				g.drawString("FATALITY", centerText(g, "FATALITY"), Main.height / 2);
			}
			else {
				g.drawString("GAME OVER", centerText(g, "GAME OVER"), Main.height / 2);
			}
		}

		// KOOOOOOOONAAAAAAAMIIIIIIIIIIIII!!!!!!!!!!
		if (konami){
			g.setColor(Color.BLUE);
			String kText = "KOOOOOOOONAAAAAAAMIIIIIIIIIIIII!!!!!!!!!!";
			g.drawString(kText, centerText(g, kText), Main.height / 2);
		}
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		if (objectsDefined && !menu){

			// draw background
			for (int i = 0; i < Character.starX.size(); i++){
				int x = Character.starX.get(i);
				int y = Character.starY.get(i);
				int backgroundArea = Character.character.x / (Main.width * Character.starSpeed);
				g.setColor(new Color(0x666666));
				g.drawRect((x + (backgroundArea * Main.width)) + Character.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea - 1) * Main.width)) + Character.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea + 1) * Main.width)) + Character.backgroundLoc, y, 1, 1);
			}

			// character
			g.drawImage(Character.charSprite, Character.character.x - Character.xs, Character.character.y - Character.ys, this);

			// enemies
			g.setColor(Color.WHITE);
			for (Enemy e : Enemies.enemies){
				Image sprite = null;
				if (e.getDirection() == 0)
					sprite = e.getSpritesF().get(e.getAnimationStage());
				else
					sprite = e.getSprites().get(e.getAnimationStage());
				g.drawImage(sprite, e.getX() - Character.xs, e.getY() - Character.ys, this);
			}

			// floors
			for (int i = 0; i < Platforms.floors.size(); i++){
				if (Platforms.floorLevel.get(i) == level){
					Rectangle r = Platforms.floors.get(i);
					colorFloors(g, r);
				}
			}
			
			// level end
			g.setColor(Color.BLUE);
			g.fillRect(Platforms.levelEnd.get(level - 1).x - Character.xs, Platforms.levelEnd.get(level - 1).y - Character.ys, Platforms.levelEnd.get(level - 1).width, Platforms.levelEnd.get(level - 1).height);

			// draw health bar
			int space = 2;
			int width = 35;
			int height = 5;
			int x = 5;
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			g.drawString("Health", x, Main.height - 45);
			for (int i = 0; i < Character.health; i++){
				int y = Main.height - 70 - (i * height) - (space * i);
				Color color = Color.GREEN;
				if (Character.health <= 5)
					color = Color.RED;
				g.setColor(color);
				g.fillRect(x, y, width, height);
			}

			drawText(g);
		}

		// draw main menu
		else if (menu){
			// draw the moving star background
			for (int i = 0; i < menuStarX.size(); i++){
				int x = menuStarX.get(i);
				int y = menuStarY.get(i);
				g.setColor(new Color(0x666666));
				g.drawRect(x + menuBgLoc, y, 1, 1);
				g.drawRect(x + menuBgLoc + Main.width, y, 1, 1);
				if (menuBgFrame >= menuSpeed){
					if (menuBgLoc > Main.width * -1)
						menuBgLoc -= 1;
					else
						menuBgLoc = 0;
					menuBgFrame = 0;
				}
				else
					menuBgFrame += 1;
			}

			// draw the floor
			Rectangle menuFloor = new Rectangle(0, Main.height - 135, Main.width, Platforms.floorHeight);
			g.setColor(new Color(0x660000));
			g.fillRect(menuFloor.x, menuFloor.y, menuFloor.width, menuFloor.height);

			// draw the character
			if (Character.aniTickFrame >= Character.aniTicks - 1){
				if (Character.aniFrame < Character.walkRight.size() - 1){
					Character.aniFrame += 1;
				}
				else {
					Character.aniFrame = 0;
				}
				Character.aniTickFrame = 0;
			}
			else
				Character.aniTickFrame += 1;
			g.drawImage(Character.walkRight.get(Character.aniFrame), Main.width / 2 - Character.walkRight.get(Character.aniFrame).getWidth(this) / 2, menuFloor.y - Character.character.height + 2, this);

			// draw the title
			g.setColor(new Color(0x660000));
			g.setFont(titleFont);
			g.drawString("NIGHTMARE", centerText(g, "NIGHTMARE"), 100);

			// draw the buttons
			Color hoverColor = Color.YELLOW;
			Color defColor = new Color(0xBBBBBB);
			Color textColor = new Color(0x660000);

			createButton(g, playBtn, defColor, hoverColor, "Go to Sleep", textColor);
			createButton(g, tutBtn, defColor, hoverColor, "Tutorial", textColor, true);
			createButton(g, lbBtn, defColor, hoverColor, "Leaderboard", textColor, true);
			createButton(g, quitBtn, defColor, hoverColor, "Exit Game", textColor);
		}

		// draw the pause menu
		if (paused){
			Color hoverColor = Color.YELLOW;
			Color defColor = new Color(0xBBBBBB);
			Color textColor = new Color(0x660000);

			// draw buttons
			createButton(g, resBtn, defColor, hoverColor, "Resume Nightmare", textColor);
			createButton(g, restartBtn, defColor, hoverColor, "Restart Level", textColor);
			createButton(g, pauseQuitBtn, defColor, hoverColor, "Wake Up", textColor);
		}
		
		if (Character.endLevel){
			Character.endLevel = false;
			String text = "Level Complete!";
			g.drawString(text, centerText(g, text), Main.height / 2);
			paintImmediately(0, 0, Main.width, Main.height);
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}
			//level += 1;
			resetLevel();
			Character.dead = false;
		}
		
		if (win){
			win = false;
			String text = "You Win!";
			g.drawString(text, centerText(g, text), Main.height / 2);
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException ex){
				ex.printStackTrace();
			}
			inGame = false;
			menu = true;
		}
	}

	public void run(){
		while(running){
			if (menu){
				fpsSetter();
				repaint();
			}
			if (paused){
				fpsSetter();
				repaint();
			}
			if (inGame){
				Character.run();
				Enemies.run();
				fpsSetter();
				repaint();
			}
			/*if (hovering)
				Main.f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				Main.f.setCursor(Cursor.getDefaultCursor());*/
		}
	}
	
	public void createButton(Graphics g, Rectangle btn, Color def, Color hover, String text, Color tColor, boolean greyed){
		if (greyed) def = new Color(0x333333);
		int textOffset = 27;
		Point mousePos = new Point(0, 0);
		try {
			mousePos = new Point(Main.f.getMousePosition().x, Main.f.getMousePosition().y - 24);
		}
		catch (NullPointerException ex){}
		g.setColor(def);
		hovering = false;
		if (btn.contains(mousePos) && !greyed){
			g.setColor(hover);
			hovering = true;
		}
		g.fillRect(btn.x, btn.y, btn.width, btn.height);
		g.setColor(tColor);
		g.setFont(btnFont);
		g.drawString(text, centerText(g, text), btn.y + textOffset);
	}
	
	public void createButton(Graphics g, Rectangle btn, Color def, Color hover, String text, Color tColor){
		createButton(g, btn, def, hover, text, tColor, false);
	}

	public void fpsSetter(){
		try {
			Thread.sleep(fps / 1000);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
