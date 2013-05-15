/** CHARACTER **/
package net.amigocraft.Nightmare;

import static net.amigocraft.Nightmare.Direction.*;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class CharacterManager {

	public static Rectangle character;

	static List<Image> walkLeft = new ArrayList<Image>();
	static List<Image> walkRight = new ArrayList<Image>();
	static Image boyStandFront = null;
	static Image boyStandLeft = null;
	static Image boyWalkLeft1 = null;
	static Image boyWalkLeft2 = null;
	static Image boyStandRight = null;
	static Image boyWalkRight1 = null;
	static Image boyWalkRight2 = null;
	static Image charSprite = null;
	public static int aniFrame = 0;
	public static int aniTickFrame = 0;

	public static int fallFrame = 0;
	public static int movementFrame = 0;
	public static int keyJump = KeyEvent.VK_UP;
	public static int keyFall = KeyEvent.VK_DOWN;
	public static int keyLeft = KeyEvent.VK_LEFT;
	public static int keyRight = KeyEvent.VK_RIGHT;
	public static int keyJump2 = KeyEvent.VK_SPACE;
	public static int keyLeft2 = KeyEvent.VK_A;
	public static int keyRight2 = KeyEvent.VK_D;
	public static int keyJump3 = KeyEvent.VK_W;
	public static int keyFall2 = KeyEvent.VK_S;
	public static int keyPause = KeyEvent.VK_ESCAPE;
	public static int jumpFrame = 0;
	public static int jumpSpeedFrame = 0;
	public static int jumpFreezeFrame = 0;
	public static int xs = 0;
	public static int ys = 0;
	public static int centerX = WindowManager.width / 2 + xs;
	public static int centerY = WindowManager.width / 2 + ys;
	public static int invincibleTick = 0;
	public static int knockbackFrame = 0;
	public static int knockbackTick = 0;
	public static int backgroundFrame = 0;
	public static int backgroundLoc = 0;
	public static List<Integer> starX = new ArrayList<Integer>();
	public static List<Integer> starY = new ArrayList<Integer>();

	// configurables
	public static int movementSpeed = 1;
	public static int fallSpeed = 0;
	public static int jumpSpeed = 0;
	public static int jumpLength = 200;
	public static int jumpFreezeLength = 75;
	public static int scrollDelay = 100;
	public static int respawnDelay = 2000;
	public static int characterWidth = 30;
	public static int characterHeight = 50;
	public static int defaultLives = 3;
	public static int aniTicks = 200;
	public static int lastDir = 1;
	public static int defaultHealth = 20;
	public static int invincibleTime = 3000;
	public static int knockbackDistance = 150;
	public static int knockbackTime = 1;
	public static int knockbackFreeze = 75;
	public static int starSpeed = 5;
	public static int starNumber = 100;

	public static boolean falling = true;
	public static Direction dir = STILL;
	public static boolean flyUp = false;
	public static boolean flyDown = false;
	public static boolean jumping = false;
	public static boolean dead = false;
	public static boolean invincible = false;
	public static Direction knockback = STILL;
	public static boolean endLevel = false;
	public static int health = defaultHealth;
	public static int backgroundSpeed = CharacterManager.movementSpeed * starSpeed;

	public static int lives = defaultLives;

	public static int prevLives = defaultLives;

	public static Rectangle defineChar(){
		try {
			boyStandFront = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyStandFront.gif"));
			boyStandLeft = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyStandLeft.gif"));
			boyWalkLeft1 = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyWalkLeft1.gif"));
			boyWalkLeft2 = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyWalkLeft2.gif"));
			boyStandRight = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyStandRight.gif"));
			boyWalkRight1 = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyWalkRight1.gif"));
			boyWalkRight2 = ImageIO.read(CharacterManager.class.getClassLoader().getResourceAsStream("images/BoyWalkRight2.gif"));
			walkLeft.add(boyWalkLeft1);
			walkLeft.add(boyWalkLeft2);
			walkRight.add(boyWalkRight1);
			walkRight.add(boyWalkRight2);
			charSprite = boyStandRight;
		}
		catch (IOException ex){
			System.out.println(ex);
		}
		return character = new Rectangle((WindowManager.width / 2) - (characterWidth / 2), (WindowManager.height / 2) - (characterHeight / 2), characterWidth, characterHeight);
	}

	public static void run(){
		Point foot1 = new Point(character.x, character.y + character.height);
		Point foot2 = new Point(character.x + character.width, character.y + character.height);

		if (dead){
			knockback = STILL;
			try {
				Thread.sleep(respawnDelay);
			}
			catch (Exception e){
				e.printStackTrace();
			}
			GameManager.resetLevel();
			if (!GameManager.infLives){
				if (lives > 0)
					lives -= 1;
				else {
					lives = CharacterManager.defaultLives;
					GameManager.state = State.MENU;
				}
			}
			dead = false;
		}

		if (jumping){
			if (!falling && knockback == STILL){
				if (jumpSpeedFrame >= jumpSpeed){
					jumpSpeedFrame = 0;
					int len = jumpLength;
					int dis = 1;
					if (GameManager.moonJump){
						len = len * 2;
						//dis = dis / 2;
					}
					if (jumpFrame <= len){
						character.y -= dis;
						ys -= dis;
						centerY -= dis;
						jumpFrame += dis;
					}
					else {
						if (jumpFreezeFrame >= jumpFreezeLength){
							falling = true;
							jumping = false;
							jumpFrame = 0;
							jumpFreezeFrame = 0;
						}
						else
							jumpFreezeFrame += 1;
					}
				}
				else
					jumpSpeedFrame += 1;
			}
		}
		if ((LivingEntityManager.containsFeet(foot1) || LivingEntityManager.containsFeet(foot2)) || jumping)
			falling = false;
		else
			falling = true;

		if (falling){
			if (!GameManager.fly){
				if (knockback == STILL){
					if (fallFrame >= fallSpeed){
						character.y += 1;
						if (character.y <= 700){
							ys += 1;
							centerY += 1;
						}
						else if (character.y >= 1100){
							dead = true;
						}
						falling = true;
						fallFrame = 0;
					}
					else
						fallFrame += 1;
				}
			}
		}

		if (dir == LEFT && knockback == STILL){
			if (movementFrame >= movementSpeed){
				character.x -= 1;
				if (character.x <= centerX - scrollDelay){
					xs -= 1;
					centerX -= 1;
					// background
					if (backgroundFrame >= backgroundSpeed){
						backgroundLoc += 1;
						backgroundFrame = 0;
					}
					else
						backgroundFrame += 1;
				}
				movementFrame = 0;
			}
			else
				movementFrame += 1;

			// update animation
			if (aniTickFrame >= aniTicks - 1){
				if (aniFrame < walkLeft.size() - 1){
					aniFrame += 1;
					charSprite = walkLeft.get(aniFrame);
				}
				else {
					charSprite = walkLeft.get(0);
					aniFrame = 0;
				}
				aniTickFrame = 0;
			}
			else
				aniTickFrame += 1;
		}
		else if (dir == RIGHT && knockback == STILL){
			if (movementFrame >= movementSpeed){
				character.x += 1;
				if (character.x >= centerX + scrollDelay){
					xs += 1;
					centerX += 1;
					// background
					if (backgroundFrame >= backgroundSpeed){
						backgroundLoc -= 1;
						backgroundFrame = 0;
					}
					else
						backgroundFrame += 1;
				}
				movementFrame = 0;
			}
			else
				movementFrame += 1;

			// update the animation
			if (aniTickFrame >= aniTicks - 1){
				if (aniFrame < walkRight.size() - 1){ 
					aniFrame += 1;
					charSprite = walkRight.get(aniFrame);
				}
				else {
					charSprite = walkRight.get(0);
					aniFrame = 0;
				}
				aniTickFrame = 0;
			}
			aniTickFrame += 1;
		}
		else {
			if (lastDir == 0)
				charSprite = boyStandLeft;
			else if (lastDir == 1)
				charSprite = boyStandRight;
		}
		
		if (flyUp){
			character.y -= 1;
			ys -= 1;
			centerY -= 1;
		}
		
		else if (flyDown){
			character.y += 1;
			ys += 1;
			centerY += 1;
		}

		// handle invincibility
		if (invincibleTick >= invincibleTime && invincible){
			invincibleTick = 0;
			invincible = false;
		}
		else
			invincibleTick += 1;

		// handle knockback
		if (knockback != STILL){
			if (knockbackFrame >= knockbackTime){
				if (knockbackTick < knockbackDistance){
					if (knockback == LEFT){
						character.x -= 1;
						if (character.x <= centerX - scrollDelay){
							xs -= 1;
							centerX -= 1;
							// background
							if (backgroundFrame >= backgroundSpeed){
								backgroundLoc += 1;
								backgroundFrame = 0;
							}
							else
								backgroundFrame += 1;
						}
					}
					else {
						character.x += 1;
						if (character.x >= centerX + scrollDelay){
							xs += 1;
							centerX += 1;
							if (backgroundFrame >= backgroundSpeed){
								backgroundLoc -= 1;
								backgroundFrame = 0;
							}
							else
								backgroundFrame += 1;
						}
					}
					if (knockbackTick < (knockbackDistance / 2) - (knockbackFreeze / 2)){
						character.y -= 1;
						ys -= 1;
					}
					else if (knockbackTick >= (knockbackDistance / 2) + (knockbackFreeze / 2)){
						character.y += 1;
						ys += 1;
					}
					falling = false;
					knockbackTick += 1;
				}
				else {
					knockback = STILL;
					knockbackTick = 0;
				}
				knockbackFrame = 0;
			}
			else
				knockbackFrame += 1;
		}

		// check if character is at level end
		if (PlatformManager.levelEnd.contains(new Point(character.x + character.width, character.y + character.height))){
			endLevel = true;
		}

		// check if character intersects coins
		List<Entity> destroyEntities = new ArrayList<Entity>();
		for (Entity e : Entity.entities){
			if (e.checkPlayerIntersect() && e.getType().equals("coin")){
				if (GameManager.superCoins)
					GameManager.score += GameManager.coinValue * 10;
				else
					GameManager.score += GameManager.coinValue;
				GameManager.playSound("/sounds/coin.wav");
				destroyEntities.add(e);
			}
		}
		for (Entity e : destroyEntities){
			e.destroy();
		}
	}
}