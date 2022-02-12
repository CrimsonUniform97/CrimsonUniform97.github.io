import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.MemoryImageSource;
import java.awt.image.VolatileImage;
import java.util.Random;
import javax.swing.JFrame;

class TubeBlazer extends JFrame implements KeyListener
{
	private boolean left_down, right_down, jump;
	private int height, jump_count, game_over;


	public static void main(String[] args)
	{
		new TubeBlazer();
	}


	TubeBlazer()
	{
		super("Tube Blazer");

		final int[][] tube = new int[20][12];
		final Polygon[][] polygon = new Polygon[322][48];
		final Color[][] color = new Color[318][7], dark_color = new Color[318][7];
		int player_x = 1200000;
		int player_y = 14;
		int score = 0;
		final Color shadow = new Color(0, 0, 0, 127);

		int[] highscore = new int[10];

final long[] frame_time = new long[20];
int frame_count = 0;



		final int w = getToolkit().getScreenSize().width, h = getToolkit().getScreenSize().height;
		setBackground(Color.black);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setUndecorated(true);
		addKeyListener(this);
		setVisible(true);
		if(getGraphicsConfiguration().getDevice().isFullScreenSupported())
			getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
		else
			setBounds(0, 0, w, h);
		createBufferStrategy(3);

		Random random = new Random();

		// hide the mouse cursor by assigning an invisible image to it
		final int[] pixels = new int[1600], pixels2 = new int[1600];
// not sure whether initializing the array is necessary
		for(int i = 1600; --i >= 0; )
			pixels[i] = pixels2[i] = 0;

		setCursor(getToolkit().createCustomCursor(createImage(new MemoryImageSource(1, 1, pixels, 0, 1)), new Point(), null));

		// create the ball image
		for(int y = 40; --y >= 0; )
			for(int x = (int)(20.5-Math.sqrt(400-(20-y)*(20-y))); x < (19.5+Math.sqrt(400-(20-y)*(20-y))); x++)
				pixels[y*40+x] = 0xff000000+(int)(0x010101*(210-((20-x)*(20-x)+(14-y)*(14-y))/4));
		final Image ball = createImage(new MemoryImageSource(40, 40, pixels, 0, 40));
		for(int y = 40; --y >= 0; )
			for(int x = (int)(20.5-Math.sqrt(400-(20-y)*(20-y))); x < (19.5+Math.sqrt(400-(20-y)*(20-y))); x++)
				pixels2[y*40+x] = 0xff000000+(int)(0x010101*(105-((20-x)*(20-x)+(14-y)*(14-y))/8));
		final Image dark_ball = createImage(new MemoryImageSource(40, 40, pixels2, 0, 40));

		// create the background image
		Image background = createImage(w,h);

		Graphics g = background.getGraphics();
		for(int i = w*h/500, x, y; --i >= 0; )
		{
			g.setColor(new Color(0x010101*random.nextInt(256)));
			g.drawLine(x=random.nextInt(w), y=random.nextInt(h), x, y);
		}
		g.setColor(Color.black);
		g.fillOval(w/2-h/20,h/2-h/20,h/10,h/10);

		VolatileImage volatile_background = createVolatileImage(w,h), dark_background = createVolatileImage(w,h);
		volatile_background.getGraphics().drawImage(background, 0, 0, this);
		volatile_background.validate(getGraphicsConfiguration());
		g = dark_background.getGraphics();
		g.drawImage(background, 0, 0, this);
		g.setColor(shadow);
		g.fillRect(0, 0, w, h);
		dark_background.validate(getGraphicsConfiguration());

		// generate and store all the polygons and colors needed for the tube to speed up drawing and to decrease garbage
		final int[] coords_x = new int[10], coords_y = new int[10];
		final Rectangle screen_bounds = new Rectangle(0, 0, w, h);
		for(int y = 0; y < 161; y++)
		{
polygon_creation:
			for(int x = 0; x < 48; x++)
			{
				for(int i = 0; i < 5; i++)
				{
					coords_x[i] = (int)(-Math.sin(((x+i)%48-.50001)*Math.PI/24)*7.5*h/(y+1)+w/2+.5);
					coords_y[i] = (int)(Math.cos(((x+i)%48-.50001)*Math.PI/24)*7.5*h/(y+1)+h/2+.5);
					coords_x[9-i] = (int)(-Math.sin(((x+i)%48-.50001)*Math.PI/24)*7.5*h/(y+2)+w/2+.5);
					coords_y[9-i] = (int)(Math.cos(((x+i)%48-.50001)*Math.PI/24)*7.5*h/(y+2)+h/2+.5);
				}
				polygon[y<<1][x] = new Polygon(coords_x, coords_y, 10);

				// check whether the polygon is visible
				for(int i = 10; --i >= 0; )
					if(screen_bounds.contains(coords_x[i], coords_y[i]))
						continue polygon_creation;
				if(polygon[y<<1][x].contains(0,0) || polygon[y<<1][x].contains(w-1,0) || polygon[y<<1][x].contains(0,h-1) || polygon[y<<1][x].contains(w-1,h-1))
					continue polygon_creation;
				polygon[y<<1][x] = null;
			}
		}

		for(int y = 0; y < 159; y++)
			for(int c = 1; c < 7; c++)
			{
				color[(y<<1)][c] = new Color((((c&4)<<14)+((c&2)<<7)+(c&1))*(255-(int)(Math.pow(y, .3)*55)));
				dark_color[(y<<1)][c] = new Color((((c&4)<<14)+((c&2)<<7)+(c&1))*(127-(int)(Math.pow(y, .3)*27)));
			}

		// initialize the tube
		for(int y = 20; --y >= 0; )
			for(int x = 0; x < 12; x++)
				tube[y][x] = (y<5||random.nextInt((int)(2+10/Math.sqrt(x)))!=0)?random.nextInt(6)+1:0;


 		// turn this thread into the game thread
		long time = 0;
		Polygon p;
		while(isVisible())
		{
			// draw the tube
			do{
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

				g = getBufferStrategy().getDrawGraphics();

				if(game_over > 0)
				{
					// draw the background
					if(volatile_background.contentsLost())
					{
						volatile_background.validate(getGraphicsConfiguration());
						volatile_background.getGraphics().drawImage(background, 0, 0, this);
					}

					g.drawImage(volatile_background, 0, 0, this);

					// draw the tube
					for(int y = 304; (y-=2) >= 0 && y+player_y >= 14; )
						for(int x = 12; --x >= 0; )
							if(tube[y>>4][x] > 0 && (p=polygon[y+player_y][((x<<2)+player_x)%48]) != null)
							{
								g.setColor(color[y+player_y][tube[y>>4][x]]);
								g.fillPolygon(p);
							}
				}
				else
				{
					// draw the background
					if(dark_background.contentsLost())
					{
						dark_background.validate(getGraphicsConfiguration());
						Graphics g2 = dark_background.getGraphics();
						g2.drawImage(background, 0, 0, this);
						g2.setColor(shadow);
						g2.fillRect(0, 0, w, h);
					}

					g.drawImage(dark_background, 0, 0, this);

					// draw the tube
					for(int y = 304; (y-=2) >= 0 && y+player_y >= 14; )
						for(int x = 12; --x >= 0; )
							if(tube[y>>4][x] > 0 && (p=polygon[y+player_y][((x<<2)+player_x)%48]) != null)
							{
								g.setColor(dark_color[y+player_y][tube[y>>4][x]]);
								g.fillPolygon(p);
							}

					// draw the text displays
					g.setColor(Color.white);
					g.drawString("press a key to start the game", w/2-79, h/2+2);
					if(highscore[0] > 0)
					{
						g.drawString("Today's Highscores", w/2-55, h/2+40);
						for(int i = 0; i < 10 && highscore[i] > 0; i++)
						{
							g.drawString((i+1)+"", w/2-25, h/2+60+i*20);
							g.drawString(highscore[i]+"", w/2+10, h/2+60+i*20);
						}
					}
				}

				// draw the ball and it's shadow
				g.setColor(shadow);
				g.fillOval(w/2-15, h-39, 30, 15);
				g.drawImage((game_over>0)?ball:dark_ball, w/2-20, h-120+(5-height)*(5-height)*2, this);

				// draw the current score
				g.setColor(Color.white);
				g.drawString((score-jump_count)+"", 20, 40);

// draw the frames per second
frame_count++;
if(Math.round(20000.0/(System.currentTimeMillis()-frame_time[frame_count%20])) < 18)
g.drawString(Math.round(200000.0/(System.currentTimeMillis()-frame_time[frame_count%20]))/10.0+"", w-50, 40);
frame_time[frame_count%20] = System.currentTimeMillis();

				getBufferStrategy().show();

				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			} while(game_over == 0 && isVisible());

			if(game_over == 3) // start new game
			{
				game_over = 2;
				score = 0;
				jump_count = 0;
				jump = false;
				height = 0;
				for(int y = 20; --y >= 0; )
					for(int x = 0; x < 12; x++)
						tube[y][x] = (y<5||random.nextInt((int)(2+10/Math.sqrt(x)))!=0)?random.nextInt(6)+1:0;

for(int i = 0; i < 20; i++)
frame_time[i] = System.currentTimeMillis();
			}
			else
			{
				// delay the painting of the next picture if necessary
				while(System.currentTimeMillis() < time+50)
					Thread.currentThread().yield();
				time = System.currentTimeMillis();

				// move the player deeper into the tube
				player_y -= 2;
				if(player_y == -2)
				{
					score++;
					for(int i = 0; i < 19; i++)
						System.arraycopy(tube[i+1], 0, tube[i], 0, 12);
					for(int x = 12; --x >= 0; )
						if(random.nextInt((int)(2+10/Math.sqrt(score))) == 0)
							tube[19][x] = 0;
						else
							tube[19][x] = random.nextInt(6)+1;
					player_y = 14;
				}

				// apply the keyboard state to the ball movement
				if(left_down)
					player_x--;
				if(right_down)
					player_x++;

				// jump
				if(jump)
				{
					height+=2;
					jump &= height < 6;
				}
				if(height > 0)
				{
					height--;
					game_over = 1;
				}

				// check whether the ball ran into a hole. If it's on top of a hole two times in a row, or after jumping, it's dead
				if(height == 0 && tube[1+(player_y>>4)][((int)(2400000.5-player_x))%48/4] == 0)
					game_over--;
				else
					game_over = 2;
				if(game_over == 0)
					for(int i = 10; --i >= 0; )
						if(highscore[i] < score-jump_count)
						{
							if(i < 9)
								highscore[i+1] = highscore[i];
							highscore[i] = score-jump_count;
						}
			}
		}

		System.exit(0);
	}


// KeyListener implementation *******************************************************************************************


	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE && height == 0 && game_over > 0)
		{
			jump = true;
			jump_count++;
		}
		left_down |= (e.getKeyCode() == KeyEvent.VK_LEFT);
		right_down |= (e.getKeyCode() == KeyEvent.VK_RIGHT);
		if(game_over > 0 || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_SPACE)
			return;
		game_over = 3;
	}


	public void keyReleased(KeyEvent e)
	{
		left_down &= (e.getKeyCode() != KeyEvent.VK_LEFT);
		right_down &= (e.getKeyCode() != KeyEvent.VK_RIGHT);
	}


	public void keyTyped(KeyEvent e) {}
}