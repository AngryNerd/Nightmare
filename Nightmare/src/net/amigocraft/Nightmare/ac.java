/** GRAPHICS AND KEY LISTENER **/
package net.amigocraft.Nightmare;

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

public class ac extends JPanel implements Runnable {

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
	Rectangle playBtn = new Rectangle(aa.width / 2 - 80, 175, 160, 40);
	Rectangle tutBtn = new Rectangle(aa.width / 2 - 80, 245, 160, 40);
	Rectangle lbBtn = new Rectangle(aa.width / 2 - 80, 315, 160, 40);
	Rectangle quitBtn = new Rectangle(aa.width / 2 - 80, aa.height - 90, 160, 40);

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
					ad.left = true;
					ad.right = false;
				}

				if (e.getKeyCode() == ad.keyRight || e.getKeyCode() == ad.keyRight2){
					ad.right = true;
					ad.left = false;
				}
				if (e.getKeyCode() == ad.keyJump || e.getKeyCode() == ad.keyJump2 || e.getKeyCode() == ad.keyJump3){
					if (!ad.falling){
						ad.jumping = true;
					}
				}
				if (e.getKeyCode() == ad.keyPause){
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
				if (e.getKeyCode() == ad.keyLeft || e.getKeyCode() == ad.keyLeft2){
					ad.left = false;
					if (!ad.right){
						ad.lastDir = 0;
					}
				}

				if (e.getKeyCode() == ad.keyRight || e.getKeyCode() == ad.keyRight2){
					ad.right = false;
					if (!ad.left){
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
					if (menu || paused){
						if (playBtn.contains(mousePos) && menu){
							menu = false;
							inGame = true;
							level = 1;
						}
						else if (quitBtn.contains(mousePos) && menu){
							inGame = false;
							aa.pullThePlug();
						}
						else if (resBtn.contains(mousePos) && paused){
							paused = false;
							inGame = true;
						}
						else if (restartBtn.contains(mousePos) && paused){
							paused = false;
							inGame = true;
							ad.character.x = aa.width / 2;
							ad.character.y = aa.height / 2;
							ad.xs = 0;
							ad.ys = 0;
							ad.backgroundLoc = 0;
							ad.centerX = aa.width / 2 + ad.xs;
							ad.centerY = aa.width / 2 + ad.ys;
							ba.defineEnemies();
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
		ad.character = ad.defineChar();
		bb.defineFloors(level);
		ba.defineEnemies();
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
			font = Font.createFont(Font.TRUETYPE_FONT, ac.class.getResourceAsStream("/fonts/dr.TTF")).deriveFont(45f);
			smallFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getResourceAsStream("/fonts/dr.TTF")).deriveFont(20f);
			titleFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getResourceAsStream("/fonts/dr.TTF")).deriveFont(85f);
			btnFont = Font.createFont(Font.TRUETYPE_FONT, ac.class.getResourceAsStream("/fonts/dr.TTF")).deriveFont(25f);
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
		ad.character = ad.defineChar();
		bb.defineFloors(level);
		ba.defineEnemies();
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

		if (objectsDefined && !menu){

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
			for (int i = 0; i < ba.enemies.size(); i++){
				String type = ba.enemyType.get(i);
				if (ba.aniFrame.get(i) >= ba.aniSpeed){
					List<Image> loop = null;
					if (ba.dir.get(i) == 0)
						loop = ba.enemySpritesF.get(type);
					else
						loop = ba.enemySprites.get(type);
					if (inGame){
						if (ba.aniStage.get(i) < loop.size() - 1){
							ba.aniStage.set(i, ba.aniStage.get(i) + 1);
						}
						else if (ba.aniStage.get(i) == loop.size() - 1){
							ba.aniStage.set(i, 0);
						}
						ba.aniFrame.set(i, 0);
					}
				}
				else
					ba.aniFrame.set(i, ba.aniFrame.get(i) + 1);
				Image sprite = null;
				if (ba.dir.get(i) == 0)
					sprite = ba.enemySpritesF.get(type).get(ba.aniStage.get(i));
				else
					sprite = ba.enemySprites.get(type).get(ba.aniStage.get(i));
				g.drawImage(sprite, ba.enemies.get(i).x - ad.xs, ba.enemies.get(i).y - ad.ys, this);
			}

			// floors
			for (int i = 0; i < bb.floors.size(); i++){
				if (bb.floorLevel.get(i) == level){
					Rectangle r = bb.floors.get(i);
					colorFloors(g, r);
				}
			}
			
			// level end
			g.setColor(Color.BLUE);
			g.fillRect(bb.levelEnd.get(level - 1).x - ad.xs, bb.levelEnd.get(level - 1).y - ad.ys, bb.levelEnd.get(level - 1).width, bb.levelEnd.get(level - 1).height);

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
			String text = "Level Complete!";
			g.drawString(text, centerText(g, text), aa.height / 2);
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}
			level += 1;
			resetLevel();
		}
		
		if (win){
			win = false;
			String text = "You Win!";
			g.drawString(text, centerText(g, text), aa.height / 2);
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
				ad.run();
				ba.run();
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