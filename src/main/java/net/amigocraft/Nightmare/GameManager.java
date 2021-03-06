/** GRAPHICS AND KEY LISTENER **/
package net.amigocraft.Nightmare;

import static net.amigocraft.Nightmare.Direction.*;
import static net.amigocraft.Nightmare.State.*;

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
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.RescaleOp;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

public class GameManager extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	public int fps = 1000;

	public static State state = MENU;

	public static int konamiStage = 0;

	public static boolean objectsDefined = false;
	public static boolean running = true;
	public static boolean hovering = false;

	public static List<Integer> menuStarX = new ArrayList<Integer>();
	public static List<Integer> menuStarY = new ArrayList<Integer>();
	public static int menuBgFrame = 0;
	public static int menuBgLoc = 0;
	public static int menuSpeed = 250;
	public static int level = 1;
	public static int totalLevels = 1;

	public static int score = 0;
	public static int coinValue = 10;

	// define main menu buttons
	Rectangle playBtn = new Rectangle(WindowManager.width / 2 - 80, 175, 160, 40);
	Rectangle lcBtn = new Rectangle(WindowManager.width / 2 - 80, 245, 160, 40);
	Rectangle lbBtn = new Rectangle(WindowManager.width / 2 - 80, 315, 160, 40);
	Rectangle quitBtn = new Rectangle(WindowManager.width / 2 - 80, WindowManager.height - 90, 160, 40);

	// define inter-level buttons
	Rectangle nextBtn = playBtn;
	Rectangle repBtn = lcBtn;
	Rectangle endBtn = lbBtn;

	// define level creator buttons
	Rectangle newBtn = playBtn;
	Rectangle loadBtn = lcBtn;
	Rectangle lcPlayBtn = lbBtn;
	Rectangle lcResBtn = playBtn;
	Rectangle lcSaveBtn = lcBtn;
	Rectangle lcQuitBtn = lbBtn;
	Rectangle lcYesBtn = playBtn;
	Rectangle lcNoBtn = lcBtn;
	Rectangle lcCancelBtn = lbBtn;
	Rectangle backBtn = playBtn;

	// define pause menu buttons
	Rectangle resBtn = new Rectangle(WindowManager.width / 2 - 80, 150, 160, 40);
	Rectangle restartBtn = new Rectangle(WindowManager.width / 2 - 80, 220, 160, 40);
	Rectangle pauseQuitBtn = new Rectangle(WindowManager.width / 2 - 80, 290, 160, 40);

	// define konami code checkboxes
	int boxDim = 30;
	int sideOffset = 200;
	Rectangle moonJumpBox = new Rectangle(sideOffset, 150, boxDim, boxDim);
	public static boolean moonJump = false;
	Rectangle flyBox = new Rectangle(WindowManager.width - sideOffset - boxDim, 150, boxDim, boxDim);
	public static boolean fly = false;
	Rectangle infLivesBox = new Rectangle(sideOffset, 250, boxDim, boxDim);
	public static boolean infLives = false;
	Rectangle superCoinsBox = new Rectangle(WindowManager.width - sideOffset - boxDim, 250, boxDim, boxDim);
	public static boolean superCoins = false;
	Rectangle invBox = new Rectangle(sideOffset, 350, boxDim, boxDim);
	public static boolean inv = false;

	Font font = new Font("Verdana", Font.BOLD, 30);
	Font smallFont = new Font("Verdana", Font.BOLD, 10);
	Font titleFont = new Font("Verdana", Font.BOLD, 50);
	Font btnFont = new Font("Verdana", Font.BOLD, 15);

	public static FrameManager f;

	public Thread game;

	public GameManager(FrameManager f){

		GameManager.f = f;

		// Key Listener
		f.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == CharacterManager.keyLeft || e.getKeyCode() == CharacterManager.keyLeft2){
					if (CharacterManager.dir != LEFT)
						CharacterManager.aniTickFrame = CharacterManager.aniTicks;
					CharacterManager.dir = LEFT;
				}

				else if (e.getKeyCode() == CharacterManager.keyRight || e.getKeyCode() == CharacterManager.keyRight2){
					if (CharacterManager.dir != RIGHT)
						CharacterManager.aniTickFrame = CharacterManager.aniTicks;
					CharacterManager.dir = RIGHT;
				}
				else if (e.getKeyCode() == CharacterManager.keyJump || e.getKeyCode() == CharacterManager.keyJump2 || e.getKeyCode() == CharacterManager.keyJump3){
					if (!fly){
						if (!CharacterManager.falling && state == GAME){
							CharacterManager.jumping = true;
						}
					}
					else {
						CharacterManager.flyUp = true;
						CharacterManager.flyDown = false;
					}
				}
				else if (e.getKeyCode() == CharacterManager.keyFall || e.getKeyCode() == CharacterManager.keyFall2){
					if (fly){
						CharacterManager.flyDown = true;
						CharacterManager.flyUp = false;
					}
				}
				else if (e.getKeyCode() == CharacterManager.keyPause){
					System.out.println(state);
					if (state == GAME)
						state = PAUSED;
					else if (state == PAUSED)
						state = GAME;
					else if (state == KONAMI)
						state = GAME;
					else if (state == LEVEL_CREATOR)
						state = LC_MENU;
					else if (state == LC_MENU)
						state = LEVEL_CREATOR;
					else if (state == LEVEL_CREATOR)
						state = LC_MENU;
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
						state = KONAMI;
					else
						konamiStage = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP){
					if (konamiStage == 0 || konamiStage == 1)
						konamiStage = 1;
				}
			}

			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == CharacterManager.keyLeft || e.getKeyCode() == CharacterManager.keyLeft2){
					CharacterManager.dir = STILL;
					if (CharacterManager.dir != RIGHT){
						CharacterManager.lastDir = 0;
					}
				}

				if (e.getKeyCode() == CharacterManager.keyRight || e.getKeyCode() == CharacterManager.keyRight2){
					CharacterManager.dir = STILL;
					if (CharacterManager.dir != LEFT){
						CharacterManager.lastDir = 1;
					}
				}

				if (e.getKeyCode() == CharacterManager.keyJump || e.getKeyCode() == CharacterManager.keyJump2 || e.getKeyCode() == CharacterManager.keyJump3){
					CharacterManager.flyUp = false;
				}

				if (e.getKeyCode() == CharacterManager.keyFall || e.getKeyCode() == CharacterManager.keyFall2){
					CharacterManager.flyDown = false;
				}

			}
		});

		// mouse listener
		f.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if (e.getButton() == 1){
					if (WindowManager.f.getMousePosition() != null){
						Point mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
						if (state != GAME){
							if (state == MENU){
								if (playBtn.contains(mousePos)){
									state = GAME;
									level = 1;
									CharacterManager.invincible = true;
								}
								else if (quitBtn.contains(mousePos))
									WindowManager.pullThePlug();

								else if (lcBtn.contains(mousePos))
									state = LC_SELECT;
							}
							else if (state == PAUSED){
								if (resBtn.contains(mousePos)){
									state = GAME;
								}
								else if (restartBtn.contains(mousePos)){
									CharacterManager.lives = CharacterManager.prevLives;
									state = GAME;
									resetLevel();
								}
								else if (pauseQuitBtn.contains(mousePos)){
									state = MENU;
								}
							}
							else if (state == LEVEL_MENU){
								if (nextBtn.contains(mousePos)){
									CharacterManager.prevLives = CharacterManager.lives;
									level += 1;
									resetLevel();
									state = GAME;
								}
								else if (repBtn.contains(mousePos)){
									CharacterManager.lives = CharacterManager.prevLives;
									resetLevel();
									state = GAME;
								}
								else if (endBtn.contains(mousePos)){
									state = MENU;
								}
							}
							else if (state == KONAMI){
								if (moonJumpBox.contains(mousePos)){
									if (moonJump)
										moonJump = false;
									else
										moonJump = true;
								}
								else if (flyBox.contains(mousePos)){
									if (fly)
										fly = false;
									else
										fly = true;
								}
								else if (infLivesBox.contains(mousePos)){
									if (infLives)
										infLives = false;
									else
										infLives = true;
								}
								else if (superCoinsBox.contains(mousePos)){
									if (superCoins)
										superCoins = false;
									else
										superCoins = true;
								}
								else if (invBox.contains(mousePos)){
									if (inv){
										inv = false;
										CharacterManager.invincible = false;
									}
									else
										inv = true;
								}
							}
							else if (state == LC_SELECT){
								if (newBtn.contains(mousePos))
									state = LEVEL_CREATOR;
								else if (loadBtn.contains(mousePos)){
									LevelDesigner.initialize();
									LevelDesigner.chooseLoad(GameManager.this);
									state = LEVEL_CREATOR;
								}
								else if (lcPlayBtn.contains(mousePos))
									LevelDesigner.loadLevel(GameManager.this);
							}
							else if (state == LC_MENU){
								if (lcResBtn.contains(mousePos))
									state = LEVEL_CREATOR;
								else if (lcSaveBtn.contains(mousePos)){
									LevelDesigner.chooseSave(GameManager.this);
								}
								else if (lcQuitBtn.contains(mousePos))
									state = LC_CONFIRM;
							}
							else if (state == LC_CONFIRM){
								if (lcYesBtn.contains(mousePos))
									state = MENU;
								else if (lcNoBtn.contains(mousePos))
									state = LC_MENU;
							}
							else if (state == LC_OW){
								if (lcYesBtn.contains(mousePos)){
									LevelDesigner.overwriteFile();
									state = LC_MENU;
								}
								else if (lcNoBtn.contains(mousePos))
									LevelDesigner.chooseSave(GameManager.this);
								else if (lcCancelBtn.contains(mousePos))
									state = LC_MENU;
							}
							else if (state == LOAD_FAIL){
								if (backBtn.contains(mousePos))
									state = LC_SELECT;
							}
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

	// start background audio loop
	public void playMusic(){
		try {
			Clip clip = AudioSystem.getClip();
			InputStream is = new URL("http://amigocraft.net/assets/darklullaby.wav").openStream();
			BufferedInputStream bIs = new BufferedInputStream(is);
			AudioInputStream aIs = AudioSystem.getAudioInputStream(bIs);
			clip.open(aIs);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch (UnknownHostException ex){
			System.err.println("Failed to retrieve audio from online host due to UnknownHostException. Either I forgot to renew my site's domain, or you don't have an Internet connection.");
		}
		catch (UnsupportedAudioFileException ex){
			ex.printStackTrace();
			System.err.println("Failed to retrieve audio from online host due to UnsupportedAudioFileException. Perhaps the download was improperly executed?");
		}
		catch (IOException ex){
			ex.printStackTrace();
			System.err.println("Failed to retrieve audio from online host due to IOException. Perhaps the download was interrupted?");
		}
		catch (LineUnavailableException ex){
			ex.printStackTrace();
			System.err.println("Failed to retrieve audio from online host due to LineUnavailableException.");
		}
	}

	public static void playSound(String path){
		try {
			Clip clip = AudioSystem.getClip();
			InputStream is = GameManager.class.getResourceAsStream(path);
			BufferedInputStream bIs = new BufferedInputStream(is);
			AudioInputStream aIs = AudioSystem.getAudioInputStream(bIs);
			clip.open(aIs);
			clip.start();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	// Define objects
	public void defineObjects(){
		CharacterManager.character = CharacterManager.defineChar();
		PlatformManager.defineFloors(level);
		LivingEntityManager.defineEnemies(level);
		EntityManager.initialize();
		EntityManager.setupEntities();

		// define star locations
		for (int i = 0; i < CharacterManager.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((WindowManager.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((WindowManager.height - 0) + 1));
			CharacterManager.starX.add(x);
			CharacterManager.starY.add(y);
		}
		for (int i = 0; i < CharacterManager.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((WindowManager.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((WindowManager.height - 0) + 1));
			menuStarX.add(x);
			menuStarY.add(y);
		}

		// define custom fonts
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, GameManager.class.getClassLoader().getResourceAsStream("fonts/dr.ttf")).deriveFont(45f);
			smallFont = Font.createFont(Font.TRUETYPE_FONT, GameManager.class.getClassLoader().getResourceAsStream("fonts/dr.ttf")).deriveFont(20f);
			titleFont = Font.createFont(Font.TRUETYPE_FONT, GameManager.class.getClassLoader().getResourceAsStream("fonts/dr.ttf")).deriveFont(85f);
			btnFont = Font.createFont(Font.TRUETYPE_FONT, GameManager.class.getClassLoader().getResourceAsStream("fonts/dr.ttf")).deriveFont(25f);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		objectsDefined = true;
		repaint();
		if (CharacterManager.aniFrame == 0)
			CharacterManager.aniFrame = 1;
		else
			CharacterManager.aniFrame = 0;
	}

	public static void resetLevel(){
		CharacterManager.health = CharacterManager.defaultHealth;
		CharacterManager.invincibleTick = 0;
		CharacterManager.invincible = true;
		CharacterManager.character = CharacterManager.defineChar();
		PlatformManager.defineFloors(level);
		LivingEntityManager.defineEnemies(level);
		CharacterManager.xs = 0;
		CharacterManager.ys = 0;
		CharacterManager.starX.clear();
		CharacterManager.starY.clear();
		for (int i = 0; i < CharacterManager.starNumber; i++){
			int x = 0 + (int)(Math.random() * ((WindowManager.width - 0) + 1));
			int y = 0 + (int)(Math.random() * ((WindowManager.height - 0) + 1));
			CharacterManager.starX.add(x);
			CharacterManager.starY.add(y);
		}
		CharacterManager.centerX = WindowManager.width / 2 + CharacterManager.xs;
		CharacterManager.centerY = WindowManager.width / 2 + CharacterManager.ys;
	}

	public static void colorFloors(Graphics g, Rectangle f){
		g.setColor(PlatformManager.floorColor);
		try {
			g.fillRect(f.x - CharacterManager.xs, f.y - CharacterManager.ys, f.width, f.height);
		}
		catch (Exception e){}
	}

	public static void colorChar(Graphics g, Rectangle f){
		g.setColor(Color.BLACK);
		g.fillRect(f.x - CharacterManager.xs, f.y - CharacterManager.ys, f.width, f.height);
	}

	public int centerText(Graphics g, String text){
		int stringLen = (int)
				g.getFontMetrics().getStringBounds(text, g).getWidth();
		return WindowManager.width / 2 - stringLen / 2;
	}

	public void drawText(Graphics g){

		g.setFont(font);

		// draw lives
		g.setColor(Color.WHITE);
		g.drawImage(CharacterManager.boyStandFront, WindowManager.width - 100, WindowManager.height - 90, this);
		g.drawString("x", WindowManager.width - 65, WindowManager.height - 50);
		g.drawString(Integer.toString(CharacterManager.lives), WindowManager.width - 40, WindowManager.height - 50);

		// draw pause message
		/*if (!inGame && paused){
			g.setColor(Color.WHITE);
			g.drawString("Paused", centerText(g, "Paused"), WindowManager.height / 2);
		}*/

		// draw death messages
		if (CharacterManager.dead){
			g.setColor(Color.RED);
			if (CharacterManager.lives > 0){
				g.drawString("FATALITY", centerText(g, "FATALITY"), WindowManager.height / 2);
			}
			else {
				g.drawString("GAME OVER", centerText(g, "GAME OVER"), WindowManager.height / 2);
			}
		}

		// KOOOOOOOONAAAAAAAMIIIIIIIIIIIII!!!!!!!!!!
		if (state == KONAMI){
			g.setColor(new Color(0x660000));
			g.drawString("Cheat Menu", centerText(g, "Cheat Menu"), 75);
			drawCheckBox(g, moonJumpBox, moonJump, "Moon Jump");
			drawCheckBox(g, flyBox, fly, "Fly");
			drawCheckBox(g, infLivesBox, infLives, "Infinite Lives");
			drawCheckBox(g, superCoinsBox, superCoins, "Super Coins");
			drawCheckBox(g, invBox, inv, "Invincibility");
		}
	}

	public void drawCheckBox(Graphics g, Rectangle box, boolean checked, String name){
		g.setColor(Color.GRAY);
		g.fillRect(box.x, box.y, box.width, box.height);
		if (checked)
			g.setColor(Color.RED);
		else
			g.setColor(Color.WHITE);
		g.fillRect(box.x + 3, box.y + 3, box.width - 6, box.height - 6);
		g.setColor(new Color(0x660000));
		g.setFont(btnFont);
		g.drawString(name, box.x + box.width + 10, box.y + box.height - 8);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		Color hoverColor = Color.YELLOW;
		Color defColor = new Color(0xBBBBBB);
		Color textColor = new Color(0x660000);

		if (inv)
			CharacterManager.invincible = true;

		if (objectsDefined && state == GAME || state == PAUSED || state == KONAMI){

			// draw background
			for (int i = 0; i < CharacterManager.starX.size(); i++){
				int x = CharacterManager.starX.get(i);
				int y = CharacterManager.starY.get(i);
				int backgroundArea = CharacterManager.character.x / (WindowManager.width * CharacterManager.starSpeed);
				g.setColor(new Color(0x666666));
				g.drawRect((x + (backgroundArea * WindowManager.width)) + CharacterManager.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea - 1) * WindowManager.width)) + CharacterManager.backgroundLoc, y, 1, 1);
				g.drawRect((x + ((backgroundArea + 1) * WindowManager.width)) + CharacterManager.backgroundLoc, y, 1, 1);
			}

			// character
			BufferedImage bi = new BufferedImage(CharacterManager.characterWidth, CharacterManager.characterHeight, BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().drawImage(CharacterManager.charSprite, 0, 0 , null);
			if (CharacterManager.invincible && !inv){
				RasterOp rOp = new RescaleOp(new float[] {1.0f, 1.0f, 1.0f, 0.4f},
						new float[] {0.0f, 0.0f, 0.0f, 0.0f}, null);
				Raster r = rOp.filter(bi.getData(), null);
				bi.setData(r);
			}
			g.drawImage(bi, CharacterManager.character.x - CharacterManager.xs, CharacterManager.character.y - CharacterManager.ys, this);

			// enemies
			g.setColor(Color.WHITE);
			for (Enemy e : LivingEntityManager.enemies){
				if (e.getLevel() == level){
					Image sprite = null;
					if (e.getDirection() == LEFT)
						sprite = e.getSpritesF().get(e.getAnimationStage());
					else
						sprite = e.getSprites().get(e.getAnimationStage());
					g.drawImage(sprite, e.getX() - CharacterManager.xs, e.getY() - CharacterManager.ys, this);
				}
			}

			// floors
			for (int i = 0; i < PlatformManager.floors.size(); i++){
				if (PlatformManager.floorLevel.get(i) == level){
					Rectangle r = PlatformManager.floors.get(i);
					colorFloors(g, r);
				}
			}

			// coins
			List<Entity> drawEntities = new ArrayList<Entity>();
			for (Entity e : EntityManager.entities){
				if (e.getLevel() == level){
					drawEntities.add(e);
					if (state == GAME){
						if (e.aniTick < e.aniDelay)
							e.aniTick += 1;
						else {
							e.aniTick = 0;
							if (e.aniFrame < EntityManager.coinSprites.size() - 1)
								e.aniFrame += 1;
							else
								e.aniFrame = 0;
						}
					}
				}
			}

			for (Entity e : drawEntities){
				if (e.getType().equals("coin"))
					g.drawImage(EntityManager.coinSprites.get(e.aniFrame), e.getX() - CharacterManager.xs, e.getY() - CharacterManager.ys, this);
			}

			// level end
			g.setColor(Color.BLUE);
			g.fillRect(PlatformManager.levelEnd.x - CharacterManager.xs, PlatformManager.levelEnd.y - CharacterManager.ys, PlatformManager.levelEnd.width, PlatformManager.levelEnd.height);

			// draw health bar
			int space = 2;
			int width = 35;
			int height = 5;
			int x = 5;
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			g.drawString("Health", x, WindowManager.height - 45);
			for (int i = 0; i < CharacterManager.health; i++){
				int y = WindowManager.height - 70 - (i * height) - (space * i);
				Color color = Color.GREEN;
				if (CharacterManager.health <= 5)
					color = Color.RED;
				g.setColor(color);
				g.fillRect(x, y, width, height);
			}

			// score
			g.drawImage(EntityManager.coinSprites.get(0), WindowManager.width - 125, 5, null);
			g.setColor(Color.WHITE);
			g.setFont(btnFont);
			g.drawString(Integer.toString(score), WindowManager.width - 85, 27);

			drawText(g);
		}

		// draw main menu
		else if (state == MENU){
			// draw the moving star background
			for (int i = 0; i < menuStarX.size(); i++){
				int x = menuStarX.get(i);
				int y = menuStarY.get(i);
				g.setColor(new Color(0x666666));
				g.drawRect(x + menuBgLoc, y, 1, 1);
				g.drawRect(x + menuBgLoc + WindowManager.width, y, 1, 1);
				if (menuBgFrame >= menuSpeed){
					if (menuBgLoc > WindowManager.width * -1)
						menuBgLoc -= 1;
					else
						menuBgLoc = 0;
					menuBgFrame = 0;
				}
				else
					menuBgFrame += 1;
			}

			// draw the floor
			Rectangle menuFloor = new Rectangle(0, WindowManager.height - 135, WindowManager.width, PlatformManager.floorHeight);
			g.setColor(new Color(0x660000));
			g.fillRect(menuFloor.x, menuFloor.y, menuFloor.width, menuFloor.height);

			// draw the character
			if (CharacterManager.aniTickFrame >= CharacterManager.aniTicks - 1){
				if (CharacterManager.aniFrame < CharacterManager.walkRight.size() - 1){
					CharacterManager.aniFrame += 1;
				}
				else {
					CharacterManager.aniFrame = 0;
				}
				CharacterManager.aniTickFrame = 0;
			}
			else
				CharacterManager.aniTickFrame += 1;
			g.drawImage(CharacterManager.walkRight.get(CharacterManager.aniFrame), WindowManager.width / 2 - CharacterManager.walkRight.get(CharacterManager.aniFrame).getWidth(this) / 2, menuFloor.y - CharacterManager.character.height + 2, this);

			// draw the title
			g.setColor(new Color(0x660000));
			g.setFont(titleFont);
			g.drawString("NIGHTMARE", centerText(g, "NIGHTMARE"), 100);

			// draw the buttons

			createButton(g, playBtn, defColor, hoverColor, "Go to Sleep", textColor);
			createButton(g, lcBtn, defColor, hoverColor, "Level Creator", textColor);
			createButton(g, lbBtn, defColor, hoverColor, "Leaderboard", textColor, true);
			createButton(g, quitBtn, defColor, hoverColor, "Exit Game", textColor);
		}
		else if (state == LEVEL_MENU){

			g.setColor(textColor);
			g.setFont(font);
			if (level < totalLevels && level > 0){
				g.drawString("Level Complete!", centerText(g, "Level Complete!"), 150);
				createButton(g, nextBtn, defColor, hoverColor, "Next Level", textColor);
			}
			else
				g.drawString("You Win!", centerText(g, "You Win!"), 150);
			createButton(g, repBtn, defColor, hoverColor, "Replay Level", textColor);
			createButton(g, endBtn, defColor, hoverColor, "End Game", textColor);
		}
		else if (state == LC_SELECT){

			createButton(g, newBtn, defColor, hoverColor, "New Level", textColor);
			createButton(g, loadBtn, defColor, hoverColor, "Load Level", textColor);
			createButton(g, lcPlayBtn, defColor, hoverColor, "Play Level", textColor);

		}
		else if (state == LEVEL_CREATOR){
			if (!LevelDesigner.initialized)
				LevelDesigner.initialize();
			//LevelDesigner.checkClick();
			LevelDesigner.drawObjects(g);
		}

		else if (state == LC_MENU){
			createButton(g, lcResBtn, defColor, hoverColor, "Resume Editing", textColor);
			createButton(g, lcSaveBtn, defColor, hoverColor, "Save Level", textColor);
			createButton(g, lcQuitBtn, defColor, hoverColor, "Quit Editor", textColor);
		}

		else if (state == LC_OW){
			g.setColor(PlatformManager.floorColor);
			g.setFont(new Font("Verdana", Font.BOLD, 20));
			String s = "Do you wish to overwrite this file?";
			g.drawString(s, centerText(g, s), 100);
			createButton(g, lcYesBtn, defColor, hoverColor, "Overwrite", textColor);
			createButton(g, lcNoBtn, defColor, hoverColor, "Choose Another", textColor);
			createButton(g, lcCancelBtn, defColor, hoverColor, "Cancel", textColor);
		}

		else if (state == LC_CONFIRM){
			g.setColor(PlatformManager.floorColor);
			g.setFont(new Font("Verdana", Font.BOLD, 20));
			String s = "Do you wish to quit the editor? Any unsaved work will be lost.";
			g.drawString(s, centerText(g, s), 100);
			createButton(g, lcYesBtn, defColor, hoverColor, "Yes", textColor);
			createButton(g, lcNoBtn, defColor, hoverColor, "No", textColor);
		}

		else if (state == LOAD_FAIL){
			g.setColor(PlatformManager.floorColor);
			g.setFont(new Font("Verdana", Font.BOLD, 20));
			String s = "Failed to load level from file!";
			g.drawString(s, centerText(g, s), 100);
			createButton(g, backBtn, defColor, hoverColor, "Back", textColor);
		}

		// draw the pause menu
		if (state == PAUSED){

			// draw buttons
			createButton(g, resBtn, defColor, hoverColor, "Resume Nightmare", textColor);
			createButton(g, restartBtn, defColor, hoverColor, "Restart Level", textColor);
			createButton(g, pauseQuitBtn, defColor, hoverColor, "Wake Up", textColor);
		}

		if (CharacterManager.endLevel){
			CharacterManager.endLevel = false;
			state = LEVEL_MENU;
		}
	}

	public void run(){
		while (running){
			if (state == GAME){
				CharacterManager.manage();
				LivingEntityManager.manage();
			}

			fpsSetter();
			repaint();

			/*if (hovering)
				WindowManager.f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				WindowManager.f.setCursor(Cursor.getDefaultCursor());*/
		}
	}

	public void createButton(Graphics g, Rectangle btn, Color def, Color hover, String text, Color tColor, boolean greyed){
		if (greyed) def = new Color(0x333333);
		int textOffset = 27;
		Point mousePos = new Point(0, 0);
		try {
			mousePos = new Point(WindowManager.f.getMousePosition().x, WindowManager.f.getMousePosition().y - 24);
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