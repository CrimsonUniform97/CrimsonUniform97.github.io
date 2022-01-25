/*
 * Pjong, a Pong clone in 4k by ME 2002
 * Contact: s8n@spray.se
 */
import java.applet.*;
import java.awt.*;
import java.lang.Math.*;
import java.awt.event.*;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

public class Pjong extends Applet implements Runnable, KeyListener
{
	// All globals
	private Thread activity;
	private Image paddle1, paddle2, ball, bgimage, backBuffer, powerUp; 
	private int playerPos, computerPos, ballX, ballY, bx, by, scoreComputer, scorePlayer, oplayerPos, ocomputerPos, gameState;
	private Graphics bBuffer;
	private boolean upPressed, downPressed;
	private int[] powerUps, puX, puY;
	private String[] pointTable;
	/* Gamestates
			-1 user playing
			0 demo mode
			1 player won
			2 comp won 
	*/
	
	// Generate a new direction
	int randomDirection(double i)
	{
		return (int)(Math.cos(Math.random()*Math.PI)*i);
	}		
	
	// random things that happends when a powerup is collected
	void collectPu(int pu)
	{
		switch(pu)
		{
			case 0:
				by = 4;
				break;
				
			case 1:
				bx = 4;
				break;
				
			case 2:
				do
					by = randomDirection(3d);
				while(by == 0);
				do
					bx = randomDirection(3d);
				while(bx == 0);
				break;
				
			case 3:
				bx = -bx;
				by = -by;
				break;
				
			case 4:
				ballX = 193 + randomDirection(100d);
				ballY = 143 + randomDirection(100d);
				break;
				
			default:
				break;
			}
	}
	// resets all infact, ball, powerups and paddles		
	void resetBall()
	{
		ballX = 193;
		ballY = 143;
		
		do
			by = randomDirection(3d);
		while(by == 0);
				
		do
			bx = randomDirection(3d);
		while(bx == 0);
		
		playerPos = computerPos = 130;
		
		for(int i = 0; i < 5; i++)
		{
			powerUps[i] = (int)(Math.random()*6.d);
			puX[i] = 193 + randomDirection(100d);
			puY[i] = 143 + randomDirection(100d);
		}
	}
	// make the ball react too the movement of the paddle
	void phake(int x, int ox)
	{
		if(ox != x)
		{
			if(by < 4 && by > -4)
				by += (x - ox)/2;
		}
	}
	// start the thread
	public void start()
	{
		if(activity == null)
		{
			activity = new Thread(this);
			activity.start();
		}
	}
	// stop the thread, could be removed for smaller applet
	public void stop()
	{
		if(activity != null)
		{
			activity.interrupt();
			activity = null;
		}
	}
	// run the thread :)
	public void run()
	{
		while(true)
		{
			// try locking at constant speed
			try
			{
				if(gameState < 1)
					activity.sleep(20);
				else
				{
					// pause for a mom so that the win/loose text is readable
					activity.sleep(4000);
					gameState = scorePlayer = scoreComputer = 0;
				}
			}
			catch (Throwable i){} // ahem..
			
			//ball movement
			if(ballY < 0)
			{
				by = -by;
				ballY = 0;
			}
			else if(ballY > 285)
			{
				by = -by;
				ballY = 285;
			}
			
			// Computer "ai"
			if(bx > 0)
			{
				ocomputerPos = computerPos;
				if((ballY - computerPos - 13) > 0 && computerPos < 260 && ballX > 193)
				{
					computerPos += 2;
				}
				else if((ballY - computerPos - 13) < 0 && computerPos > 0 && ballX > 193)
				{
					computerPos -= 2;
				}			
			}
			else
			{
				if(computerPos > 130)
				{
					computerPos-=2;
				}
				else if(computerPos < 130)
				{
					computerPos+=2;
				}
			}

			// player "ai"
			if(gameState == 0)
			{
				oplayerPos = playerPos;
				if((ballY - playerPos - 13) > 0 && playerPos < 260)
				{
					playerPos += 2;
				}
				else if((ballY - playerPos - 13) < 0 && playerPos > 0)
				{
					playerPos -= 2;
				}
			}
			// player controlled paddle
			else
			{
				oplayerPos = playerPos;
				if(downPressed && playerPos < 260)
				{
					playerPos += 2;
				}
				else if(upPressed && playerPos > 0)
				{
					playerPos -= 2;
				}
			}
			
			// collision detection ball->paddle
			if(ballX > 365 && ballX < 375 && (ballY - computerPos) < 40 && (ballY - computerPos) > -20)
			{
				ballX = 365;
				bx = -bx;
				phake(computerPos, ocomputerPos);
			}
			else if(ballX > 10 && ballX < 20 && (ballY - playerPos) < 40 && (ballY - playerPos) > -20)
			{
				ballX = 20;
				bx = -bx;
				phake(playerPos, oplayerPos);
			}
			// collision detection ball->edges
			else if(ballX < 0)
			{
				scoreComputer++;
				gameState = scoreComputer > 9 ? 2 : gameState;
				resetBall();
			}
			else if(ballX > 385)
			{
				scorePlayer++;
				gameState = scorePlayer > 9 ? 1 : gameState;
				resetBall();
			}
			// collision detection ball->powerups
			for(int i =0; i < 5; i++)
			{
				if(powerUps[i] != -1 && ((puX[i]-ballX) < 15) && ((ballX-puX[i]) < 15) && ((puY[i]-ballY) < 15) && ((ballY-puY[i]) < 15))
				{
					collectPu(powerUps[i]);
					powerUps[i] = -1;
				}
			}
			// move the ball and draw
			ballX += bx;
			ballY += by;
			repaint();
		}
	}

	public void init()
	{
		// make background
		int buffer[] = new int[120000];
		for(int i = 0; i < 120000; i++)
		{
			int c = (int)((float)(i*255) / 120000.f);
			buffer[i] = ((255-c)<<16) | 128;
		}
		setForeground(Color.green);
		bgimage = createImage(new MemoryImageSource(400, 300, new DirectColorModel(32, 0xff0000, 0xff00, 0xff), buffer, 0, 400));
		// init the backbuffer
		backBuffer = createImage(400, 300);
		bBuffer = backBuffer.getGraphics();
		// load images
		paddle1 = getImage(getCodeBase(), "r.gif");
		paddle2 = getImage(getCodeBase(), "g.gif");
		ball = getImage(getCodeBase(), "b.gif");
		powerUp = getImage(getCodeBase(), "p.gif");
		// init structures and setup keyboard control
		powerUps = new int[5];
		puX = new int[5];
		puY = new int[5];
		resetBall();
		// precalc stringlist to avoid making objects during mainloop 
		pointTable = new String[22];
		for(int i = 0; i < 11; i++)
			pointTable[i] = "Red: " + i;
		for(int i = 11; i < 22; i++)
			pointTable[i] = "Green: " + (i - 11);
		
		addKeyListener(this);
	}

	// just paint
	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		// draw paddles, ball and background image
		bBuffer.drawImage(bgimage, 0, 0, this);
		bBuffer.drawImage(paddle1, 5, playerPos, this);
		bBuffer.drawImage(paddle2, 380, computerPos, this);
		bBuffer.drawImage(ball, ballX, ballY, this);
		// draw active powerups
		for(int i = 0; i < 5; i++)
		{
			if(powerUps[i] != -1)
				bBuffer.drawImage(powerUp, puX[i], puY[i], this);
		}
		if(gameState == 1)
			bBuffer.drawString("Red won!", 175, 150);
		else if(gameState == 2)
			bBuffer.drawString("Green won!", 165, 150);
		else if(gameState == 0)
				bBuffer.drawString("Hit space to play", 150, 150);
		// draw the score and demo message
		bBuffer.drawString(pointTable[scorePlayer], 0, 280);
		bBuffer.drawString(pointTable[scoreComputer+11], 0, 295);
		
		g.drawImage(backBuffer, 0, 0, this);
	}
	// keyboard callback
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_UP)
			upPressed = true;
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			downPressed = true;
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			resetBall();
			if(gameState == 0)
				gameState = -1;
			else
				gameState = 0;
			scorePlayer = scoreComputer = 0;
		}
	}
	// keyboard callback
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_UP)
			upPressed = false;
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			downPressed = false;
	}
	// keyboard callback, should be empty
	public void keyTyped(KeyEvent e) {}
};
// EOF