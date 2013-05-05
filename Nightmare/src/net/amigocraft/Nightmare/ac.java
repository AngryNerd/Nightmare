/** GRAPHICS AND KEY LISTENER **/
package net.amigocraft.Nightmare;

import static net.amigocraft.Nightmare.Direction.*;

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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

public class ac extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	public int fps = 1000;

	public static int konamiStage = 0;

	public static boolean objectsDefined = false;
	public static boolean running = true;
	public static boolean konami = false;
	public static boolean hovering = false;
	public static boolean win = false;
	public static boolean inGame = false;
	public static boolean menu = true;
	public static boolean paused = false;
	public static boolean endLevelMenu = false;

	public static List<Integer> menuStarX = new ArrayList<Integer>();
	public static List<Integer> menuStarY = new ArrayList<Integer>();
	public static int menuBgFrame = 0;
	public static int menuBgLoc = 0;
	public static int menuSpeed = 250;
	public static int level = 1;
	public static int totalLevels = 1;

	public static int score = 0;
	public static int coinValue = 1;

	// define main menu buttons
	Rectangle playBtn = new Rectangle(aa.width / 2 - 80, 175, 160, 40);
	Rectangle tutBtn = new Rectangle(aa.width / 2 - 80, 245, 160, 40);
	Rectangle lbBtn = new Rectangle(aa.width / 2 - 80, 315, 160, 40);
	Rectangle quitBtn = new Rectangle(aa.width / 2 - 80, aa.height - 90, 160, 40);

	// define inter-level buttons
	Rectangle nextBtn = playBtn;
	Rectangle repBtn = tutBtn;
	Rectangle endBtn = lbBtn;

	// define pause menu buttons
	Rectangle resBtn = new Rectangle(aa.width / 2 - 80, 150, 160, 40);
	Rectangle restartBtn = new Rectangle(aa.width / 2 - 80, 220, 160, 40);
	Rectangle pauseQuitBtn = new Rectangle(aa.width / 2 - 80, 290, 160, 40);

	Font font = new Font("Verdana", Font.BOLD, 30);
	Font smallFont = new Font("Verdana", Font.BOLD, 10);
	Font titleFont = new Font("Verdana", Font.BOLD, 50);
	Font btnFont = new Font("Verdana", Font.BOLD, 15);

	public Thread game;

	public ac(ab f){

		// Key Listener
		f.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == ad.keyLeft || e.getKeyCode() == ad.keyLeft2){
					ad.dir = LEFT;
					ad.aniTickFrame = 0;
				}

				if (e.getKeyCode() == ad.keyRight || e.getKeyCode() == ad.keyRight2){
					ad.dir = RIGHT;
					ad.aniTickFrame = 0;
				}
				if (e.getKeyCode() == ad.keyJump || e.getKeyCode() == ad.keyJump2 || e.getKeyCode() == ad.keyJump3){
					if (!ad.falling){
						ad.jumping = true;
					}
				}
				if (e.getKeyCode() == ad.keyPause && !endLevelMenu){
					if (inGame){
						inGame = false;
						paused = true;
					}
					else {
						paused = false;
						inGame = true;
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
				if (e.getKeyCode() == ad.keyLeft || e.getKeyCode() == ad.keyLeft2){
					ad.dir = STILL;
					if (ad.dir != RIGHT){
						ad.lastDir = 0;
					}
				}

				if (e.getKeyCode() == ad.keyRight || e.getKeyCode() == ad.keyRight2){
					ad.dir = STILL;
					if (ad.dir != LEFT){
						ad.lastDir = 1;
					}
				}

			}
		});

		// mouse listener
		f.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getButton() == 1){
					Point mousePos = new Point(aa.f.getMousePosition().x, aa.f.getMousePosition().y - 24);
					if (!inGame){
						if (playBtn.contains(mousePos) && menu){
							menu = false;
							inGame = true;
							level = 1;
						}
						else if (quitBtn.contains(mousePos) && menu){
							aa.pullThePlug();
						}
						else if (resBtn.contains(mousePos) && paused){
							paused = false;
							inGame = true;
						}
						else if (restartBtn.contains(mousePos) && paused){
							ad.lives = ad.prevLives;
							paused = false;
							inGame = true;
							resetLevel();
						}
						else if (pauseQuitBtn.contains(mousePos) && paused){
							paused = false;
							menu = true;
						}
						else if (nextBtn.contains(mousePos) && endLevelMenu){
							ad.prevLives = ad.lives;
							level += 1;
							resetLevel();
							endLevelMenu = false;
							inGame = true;
						}
						else if (repBtn.contains(mousePos) && endLevelMenu){
							ad.lives = ad.prevLives;
							resetLevel();
							endLevelMenu = false;
							inGame = true;
						}
						else if (endBtn.contains(mousePos) && endLevelMenu){
							endLevelMenu = false;
							inGame = false;
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
		playMusic();
		game = new Thread(this);
		game.start();
	}

	public void playMusic(){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream aIs = AudioSystem.getAudioInputStream
					(ac.class.getResourceAsStream("/sounds/music.wav"));
			clip.open(aIs);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	// Define objects
	public void defineObjects(){
		ad.character = ad.defineChar();
		bb.defineFloors(level);
		ba.defineEnemies(level);
		Entity.initialize();
		Entity.setupEntities();

		// define star locations
		for (int i = 0; i < ad.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((aa.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((aa.height - 0) + 1));
			ad.starX.add(x);
			ad.starY.add(y);
		}
		for (int i = 0; i < ad.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((aa.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((aa.height - 0) + 1));
			menuStarX.add(x);
			menuStarY.add(y);
		}

		// define custom fonts
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, ac.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(45f);
			smallFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(20f);
			titleFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(85f);
			btnFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getClassLoader().getResourceAsStream("fonts/dr.TTF")).deriveFont(25f);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		objectsDefined = true;
		repaint();
		if (ad.aniFrame == 0)
			ad.aniFrame = 1;
		else
			ad.aniFrame = 0;
	}

	public static void resetLevel(){
		ad.health = ad.defaultHealth;
		ad.character = ad.defineChar();
		bb.defineFloors(level);
		ba.defineEnemies(level);
		ad.xs = 0;
		ad.ys = 0;
		ad.starX.clear();
		ad.starY.clear();
		for (int i = 0; i < ad.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((aa.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((aa.height - 0) + 1));
			ad.starX.add(x);
			ad.starY.add(y);
		}
		ad.centerX = aa.width / 2 + ad.xs;
		ad.centerY = aa.width / 2 + ad.ys;
	}
	
	public static void playCoinSound(){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream aIs = AudioSystem.getAudioInputStream
					(ac.class.getResourceAsStream("/sounds/coin.wav"));
			clip.open(aIs);
			clip.start();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public static void colorFloors(Graphics g, Rectangle f){
		g.setColor(new Color(0x6600000));
		try {
			g.fillRect(f.x - ad.xs, f.y - ad.ys, f.width, f.height);
		}
		catch (Exception e){}
	}

	public static void colorChar(Graphics g, Rectangle f){
		g.setColor(Color.BLACK);
		g.fillRect(f.x - ad.xs, f.y - ad.ys, f.width, f.height);
	}

	public int centerText(Graphics g, String text){
		int stringLen = (int)
				g.getFontMetrics().getStringBounds(text, g).getWidth();
		return aa.width / 2 - stringLen / 2;
	}

	public void drawText(Graphics g){

		g.setFont(font);

		// draw lives
		g.setColor(Color.WHITE);
		g.drawImage(ad.boyStandFront, aa.width - 100, aa.height - 90, this);
		g.drawString("x", aa.width - 65, aa.height - 50);
		g.drawString(Integer.toString(ad.lives), aa.width - 40, aa.height - 50);

		// draw pause message
		/*if (!inGame && paused){
			g.setColor(Color.WHITE);
			g.drawString("Paused", centerText(g, "Paused"), aa.height / 2);
		}*/

		// draw death messages
		if (ad.dead){
			g.setColor(Color.RED);
			if (ad.lives > 0){
				g.drawString("FATALITY", centerText(g, "FATALITY"), aa.height / 2);
			}
			else {
				g.drawString("GAME OVER", centerText(g, "GAME OVER"), aa.height / 2);
			}
		}

		// KOOOOOOOONAAAAAAAMIIIIIIIIIIIII!!!!!!!!!!
		if (konami){
			g.setColor(Color.BLUE);
			String kText = "KOOOOOOOONAAAAAAAMIIIIIIIIIIIII!!!!!!!!!!";
			g.drawString(kText, centerText(g, kText), aa.height / 2);
		}
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		if (objectsDefined && !menu && !endLevelMenu){

			// draw background
			for (int i = 0; i < ad.starX.size(); i++){
				int x = ad.starX.get(i);
				int y = ad.starY.get(i);
				int backgroundArea = ad.character.x / (aa.width * ad.starSpeed);
				g.setColor(new Color(0x666666));
				g.drawRect((x + (backgroundArea * aa.width)) + ad.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea - 1) * aa.width)) + ad.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea + 1) * aa.width)) + ad.backgroundLoc, y, 1, 1);
			}

			// character
			g.drawImage(ad.charSprite, ad.character.x - ad.xs, ad.character.y - ad.ys, this);

			// enemies
			g.setColor(Color.WHITE);
			for (Enemy e : ba.enemies){
				Image sprite = null;
				if (e.getDirection() == LEFT)
					sprite = e.getSpritesF().get(e.getAnimationStage());
				else
					sprite = e.getSprites().get(e.getAnimationStage());
				g.drawImage(sprite, e.getX() - ad.xs, e.getY() - ad.ys, this);
			}

			// floors
			for (int i = 0; i < bb.floors.size(); i++){
				if (bb.floorLevel.get(i) == level){
					Rectangle r = bb.floors.get(i);
					colorFloors(g, r);
				}
			}

			// coins
			for (Entity e : Entity.entities){
				if (e.getType().equals("coin")){
					g.drawImage(Entity.coinSprites.get(Entity.aniFrame), e.getX() - ad.xs, e.getY() - ad.ys, this);
				}
				if (Entity.aniTick < Entity.aniDelay)
					Entity.aniTick += 1;
				else {
					Entity.aniTick = 0;
					if (Entity.aniFrame < Entity.coinSprites.size() - 1)
						Entity.aniFrame += 1;
					else
						Entity.aniFrame = 0;
				}
			}

			// level end
			g.setColor(Color.BLUE);
			g.fillRect(bb.levelEnd.x - ad.xs, bb.levelEnd.y - ad.ys, bb.levelEnd.width, bb.levelEnd.height);

			// draw health bar
			int space = 2;
			int width = 35;
			int height = 5;
			int x = 5;
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			g.drawString("Health", x, aa.height - 45);
			for (int i = 0; i < ad.health; i++){
				int y = aa.height - 70 - (i * height) - (space * i);
				Color color = Color.GREEN;
				if (ad.health <= 5)
					color = Color.RED;
				g.setColor(color);
				g.fillRect(x, y, width, height);
			}

			// score
			g.drawImage(Entity.coinSprites.get(0), aa.width - 75, 5, null);
			g.setColor(Color.WHITE);
			g.setFont(btnFont);
			g.drawString(Integer.toString(score), aa.width - 35, 27);

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
				g.drawRect(x + menuBgLoc + aa.width, y, 1, 1);
				if (menuBgFrame >= menuSpeed){
					if (menuBgLoc > aa.width * -1)
						menuBgLoc -= 1;
					else
						menuBgLoc = 0;
					menuBgFrame = 0;
				}
				else
					menuBgFrame += 1;
			}

			// draw the floor
			Rectangle menuFloor = new Rectangle(0, aa.height - 135, aa.width, bb.floorHeight);
			g.setColor(new Color(0x660000));
			g.fillRect(menuFloor.x, menuFloor.y, menuFloor.width, menuFloor.height);

			// draw the character
			if (ad.aniTickFrame >= ad.aniTicks - 1){
				if (ad.aniFrame < ad.walkRight.size() - 1){
					ad.aniFrame += 1;
				}
				else {
					ad.aniFrame = 0;
				}
				ad.aniTickFrame = 0;
			}
			else
				ad.aniTickFrame += 1;
			g.drawImage(ad.walkRight.get(ad.aniFrame), aa.width / 2 - ad.walkRight.get(ad.aniFrame).getWidth(this) / 2, menuFloor.y - ad.character.height + 2, this);

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
		else if (endLevelMenu){

			Color hoverColor = Color.YELLOW;
			Color defColor = new Color(0xBBBBBB);
			Color textColor = new Color(0x660000);

			g.setColor(textColor);
			g.setFont(font);
			if (level < totalLevels){
				g.drawString("Level Complete!", centerText(g, "Level Complete!"), 150);
				createButton(g, nextBtn, defColor, hoverColor, "Next Level", textColor);
			}
			else
				g.drawString("You Win!", centerText(g, "You Win!"), 150);
			createButton(g, repBtn, defColor, hoverColor, "Replay Level", textColor);
			createButton(g, endBtn, defColor, hoverColor, "End Game", textColor);
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

		if (ad.endLevel){
			ad.endLevel = false;
			inGame = false;
			endLevelMenu = true;
		}
	}

	public void run(){
		while (running){
			if (inGame){
				ad.run();
				ba.run();
				fpsSetter();
				repaint();
			}
			else if (menu){
				fpsSetter();
				repaint();
			}
			else if (paused){
				fpsSetter();
				repaint();
			}
			else if (endLevelMenu){
				fpsSetter();
				repaint();
			}
			/*if (hovering)
				aa.f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				aa.f.setCursor(Cursor.getDefaultCursor());*/
		}
	}

	public void createButton(Graphics g, Rectangle btn, Color def, Color hover, String text, Color tColor, boolean greyed){
		if (greyed) def = new Color(0x333333);
		int textOffset = 27;
		Point mousePos = new Point(0, 0);
		try {
			mousePos = new Point(aa.f.getMousePosition().x, aa.f.getMousePosition().y - 24);
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