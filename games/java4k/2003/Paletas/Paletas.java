import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.MemoryImageSource;
import java.util.Random;
import javax.swing.JApplet;

public class Paletas extends JApplet implements KeyListener{
    
    boolean left_down = false,up_down = false,down_down = false,right_down = false;
    boolean shift_down = false, ctrl_down = false;
    boolean a_down = false,w_down = false,s_down = false,d_down = false;
        
    /*	public static void main(String[] args)
	{
		new Paletas();
	}
*/
    
    /** Creates a new instance of Paletas */
    public void init() {
       
        final Color shadow = new Color(0, 0, 0, 127);
        final int w = 650, h = 500;
        
        int p1points= 0, p2points=0;
        int player_x = 5, player_y = h*75/200, ball_x = 10, ball_y = 60; 
        int player2_x = w-15, player2_y = h*75/200;
        //last position of players to calcule appropiate collision
        int last_x = 5, last_y = h*75/200, last2_x=w-15, last2_y = h*75/200;
        //last ball position to recalculate if collision 
        int last_ball_x, last_ball_y;
        int current_angle = 90,last_angle = 90, ball_angle = 75, ball_speed = 10;
        int current_angle2 = 90, last_angle2 = 90;
        
        setBackground(Color.black);
        //setResizable(false);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setUndecorated(true);
        addKeyListener(this);
        setVisible(true);
        this.setBounds(0,0,650, 500);
        //hide cursor
        final int[] pixels = new int[1600], pixels2 = new int[1600];
	for(int i = 1600; --i >= 0; )
		pixels[i] = pixels2[i] = 0;
        setCursor(getToolkit().createCustomCursor(createImage(new MemoryImageSource(1, 1, pixels, 0, 1)), new Point(), null));
        
       
        Random random = new Random();
        Image background = createImage(w,h);
        Graphics g = background.getGraphics();
        
        for(int i = w*h/500, x, y; --i >= 0; )
		{
			g.setColor(new Color(0x010101*random.nextInt(256)));
			g.drawLine(x=random.nextInt(w), y=random.nextInt(h), x, y);
		}
		
                g.setColor(Color.black);
                g.drawImage(background, 0, 0, this);
		g.setColor(shadow);
		g.fillRect(0, 0, w, h);
		

        // create the player image
        
        Polygon p = new Polygon();
        Polygon p2 = new Polygon();
        Polygon ball = new Polygon();
        //to work with normal angles.
        double a = Math.PI/180.0;
        
        int i;
        
        Image Temp = createImage(w,h);
        Graphics gTemp;
        long time = 0;
        while(true){
            while(System.currentTimeMillis() < time+50)
		Thread.currentThread().yield();
	    time = System.currentTimeMillis();
            
            
            if (current_angle == 360) current_angle = 0;
            if (current_angle2 == 360) current_angle2 = 0;
            
            //control player1
            last_angle = current_angle;
            last_x = player_x;
            last_y = player_y;
            if((left_down) && (player_x>10) && !(shift_down)){
                player_x-=10;
            }
            if((left_down) && (player_x>10) && (shift_down)){
                current_angle += 30;
            }
            if(right_down && (player_x < w-20) && !(shift_down)){
                player_x+=10;
            }
            if(right_down && (player_x < w-20) && (shift_down)){
                current_angle -= 30;
                if(current_angle < 0) current_angle += 360;
            }
            if(up_down && (player_y>0)){
                player_y-=10;
            }
            if(down_down && (player_y < h)){
                player_y+=10;
            }
            
            //control player2
            last_angle2 = current_angle2;
            last2_x = player2_x;
            last2_y = player2_y;
            if((a_down) && (player2_x>10) && !(ctrl_down)){
                player2_x-=10;
            }
            if((a_down) && (player2_x>10) && (ctrl_down)){
                current_angle2 += 30;
            }
            if(d_down && (player2_x < w-20) && !(ctrl_down)){
                player2_x+=10;
            }
            if(d_down && (player2_x < w-20) && (ctrl_down)){
                current_angle2 -= 30;
                if(current_angle2 < 0) current_angle2 += 360;
            }
            if(w_down && (player2_y>0)){
                player2_y-=10;
            }
            if(s_down && (player2_y < h)){
                player2_y+=10;
            }
                        
            //players points
            if (ball_x<0+ball_speed){
                player_x = w/2-30;
                player_y = h/2;
                current_angle = 180;
                player2_x = w-20;
                player2_y = h/2;
                current_angle2 = 90;
                p2points++;
            }
            if (ball_x>w-10-ball_speed){
                player2_x = w/2-30;
                player2_y = h/2;
                current_angle2 = 180;
                player_x = 20;
                player_y = h/2;
                current_angle = 90;
                p1points++;
            }
            
            //walls collision
            if ((ball_y < 0+ball_speed) || (ball_y > h-10-ball_speed)){
                if (ball_speed > 2) ball_speed -= 2;
                ball_angle = 360 - ball_angle;
            }
            if ((ball_x > w-10-ball_speed) || (ball_x < 0+ball_speed)){
                ball_speed = 0;
                ball_x = w/2;
                ball_y = h/2 - 20;
                ball_angle = 0;
            }
            
            //ball movement update
            last_ball_x = ball_x;
            last_ball_y = ball_y;
            ball_x += (int)(ball_speed*Math.cos(a*ball_angle));
            ball_y += -(int)(ball_speed*Math.sin(a*ball_angle));
            
            //polygons for collision
            p.reset();
            p.addPoint(player_x + (int)(5*Math.sin(a*current_angle)+30*Math.cos(a*current_angle)),player_y + (int)(5*Math.cos(a*current_angle)-30*Math.sin(a*current_angle)));
            p.addPoint(last_x + (int)(-5*Math.sin(a*last_angle)+30*Math.cos(a*last_angle)),last_y + (int)(-5*Math.cos(a*last_angle)-30*Math.sin(a*last_angle)));
            p.addPoint(last_x + (int)(-5*Math.sin(a*last_angle)-30*Math.cos(a*last_angle)),last_y + (int)(-5*Math.cos(a*last_angle)+30*Math.sin(a*last_angle)));
            p.addPoint(player_x + (int)(5*Math.sin(a*current_angle)-30*Math.cos(a*current_angle)),player_y + (int)(5*Math.cos(a*current_angle)+30*Math.sin(a*current_angle)));
            
            p2.reset();
            if(player2_x < last2_x){
                p2.addPoint(player2_x + (int)(5*Math.sin(a*current_angle2)+30*Math.cos(a*current_angle2)),player2_y + (int)(5*Math.cos(a*current_angle2)-30*Math.sin(a*current_angle2)));
                p2.addPoint(last2_x + (int)(-5*Math.sin(a*last_angle2)+30*Math.cos(a*current_angle2)),last2_y + (int)(-5*Math.cos(a*last_angle2)-30*Math.sin(a*last_angle2)));
                p2.addPoint(last2_x + (int)(-5*Math.sin(a*last_angle2)-30*Math.cos(a*last_angle2)),last2_y + (int)(-5*Math.cos(a*last_angle2)+30*Math.sin(a*last_angle2)));
                p2.addPoint(player2_x + (int)(5*Math.sin(a*current_angle2)-30*Math.cos(a*current_angle2)),player2_y + (int)(5*Math.cos(a*current_angle2)+30*Math.sin(a*current_angle2)));
            
            }else{ 
                p2.addPoint(last2_x + (int)(5*Math.sin(a*last_angle2)+30*Math.cos(a*last_angle2)),last2_y + (int)(5*Math.cos(a*last_angle2)-30*Math.sin(a*last_angle2)));
                p2.addPoint(player2_x + (int)(-5*Math.sin(a*current_angle2)+30*Math.cos(a*current_angle2)),player2_y + (int)(-5*Math.cos(a*current_angle2)-30*Math.sin(a*current_angle2)));
                p2.addPoint(player2_x + (int)(-5*Math.sin(a*current_angle2)-30*Math.cos(a*current_angle2)),player2_y + (int)(-5*Math.cos(a*current_angle2)+30*Math.sin(a*current_angle2)));
                p2.addPoint(last2_x + (int)(5*Math.sin(a*last_angle2)-30*Math.cos(a*last_angle2)),last2_y + (int)(5*Math.cos(a*last_angle2)+30*Math.sin(a*last_angle2)));
            }
            
            
            ball.reset();
            ball.addPoint(ball_x,ball_y);
            ball.addPoint(last_ball_x+10,last_ball_y);
            ball.addPoint(last_ball_x+10,last_ball_y+10);
            ball.addPoint(ball_x,ball_y+10);
            
            //lets see if ball must change direction
            
            if (p.intersects(ball.getBounds2D())){
                
                if(last_angle != current_angle) ball_speed +=2;
                ball_angle = 360-ball_angle - 2*current_angle;
                ball_x = last_ball_x + (int)(ball_speed*Math.cos(a*ball_angle));
                ball_y = last_ball_y -(int)(ball_speed*Math.sin(a*ball_angle));
            
            }
            if (p2.intersects(ball.getBounds2D())){
                
                if(last_angle2 != current_angle2) ball_speed +=2;
                ball_angle = 360-ball_angle - 2*current_angle2;
                ball_x = last_ball_x + (int)(ball_speed*Math.cos(a*ball_angle));
                ball_y = last_ball_y -(int)(ball_speed*Math.sin(a*ball_angle));
                
            }
            ball.reset();
            ball.addPoint(ball_x,ball_y);
            ball.addPoint(ball_x+10,ball_y);
            ball.addPoint(ball_x+10,ball_y+10);
            ball.addPoint(ball_x,ball_y+10);
            
            p.reset();
            p.addPoint(player_x + (int)(5*Math.sin(a*current_angle)+30*Math.cos(a*current_angle)),player_y + (int)(5*Math.cos(a*current_angle)-30*Math.sin(a*current_angle)));
            p.addPoint(player_x + (int)(-5*Math.sin(a*current_angle)+30*Math.cos(a*current_angle)),player_y + (int)(-5*Math.cos(a*current_angle)-30*Math.sin(a*current_angle)));
            p.addPoint(player_x + (int)(-5*Math.sin(a*current_angle)-30*Math.cos(a*current_angle)),player_y + (int)(-5*Math.cos(a*current_angle)+30*Math.sin(a*current_angle)));
            p.addPoint(player_x + (int)(5*Math.sin(a*current_angle)-30*Math.cos(a*current_angle)),player_y + (int)(5*Math.cos(a*current_angle)+30*Math.sin(a*current_angle)));
            
            p2.reset();
            p2.addPoint(player2_x + (int)(5*Math.sin(a*current_angle2)+30*Math.cos(a*current_angle2)),player2_y + (int)(5*Math.cos(a*current_angle2)-30*Math.sin(a*current_angle2)));
            p2.addPoint(player2_x + (int)(-5*Math.sin(a*current_angle2)+30*Math.cos(a*current_angle2)),player2_y + (int)(-5*Math.cos(a*current_angle2)-30*Math.sin(a*current_angle2)));
            p2.addPoint(player2_x + (int)(-5*Math.sin(a*current_angle2)-30*Math.cos(a*current_angle2)),player2_y + (int)(-5*Math.cos(a*current_angle2)+30*Math.sin(a*current_angle2)));
            p2.addPoint(player2_x + (int)(5*Math.sin(a*current_angle2)-30*Math.cos(a*current_angle2)),player2_y + (int)(5*Math.cos(a*current_angle2)+30*Math.sin(a*current_angle2)));
            
            gTemp = Temp.getGraphics();
            gTemp.drawImage(background, 0, 0, this);

            gTemp.setColor(Color.BLUE);
            gTemp.fillPolygon(p);
            
            gTemp.setColor(Color.GREEN);
            gTemp.fillPolygon(p2);
            
            gTemp.setColor(Color.RED);
            gTemp.fillPolygon(ball);
            gTemp.drawString("Player 1 = "+p1points, 10,50);
            gTemp.drawString("Player 2 = "+p2points,500,50);
            g = this.getGraphics();
            g.drawImage(Temp,0,0,this);
            
            
        }
    }
    
// KeyListener implementation *******************************************************************************************
    	public void keyPressed(KeyEvent e)
	{
            //player1
            left_down |= (e.getKeyCode() == KeyEvent.VK_LEFT);
            right_down |= (e.getKeyCode() == KeyEvent.VK_RIGHT);
            up_down |= (e.getKeyCode() == KeyEvent.VK_UP);
            down_down |= (e.getKeyCode() == KeyEvent.VK_DOWN);
            shift_down |= (e.getKeyCode() == KeyEvent.VK_SHIFT);
            
            //player2
            a_down |= (e.getKeyCode() == KeyEvent.VK_A);
            d_down |= (e.getKeyCode() == KeyEvent.VK_D);
            w_down |= (e.getKeyCode() == KeyEvent.VK_W);
            s_down |= (e.getKeyCode() == KeyEvent.VK_S);
            ctrl_down |= (e.getKeyCode() == KeyEvent.VK_CONTROL);
                       
	}

       
	public void keyReleased(KeyEvent e)
	{
            left_down &= (e.getKeyCode() != KeyEvent.VK_LEFT);
	    right_down &= (e.getKeyCode() != KeyEvent.VK_RIGHT);
            up_down &= (e.getKeyCode() != KeyEvent.VK_UP);
            down_down &= (e.getKeyCode() != KeyEvent.VK_DOWN);
            shift_down &= (e.getKeyCode() != KeyEvent.VK_SHIFT);
                     
            //player2
            a_down &= (e.getKeyCode() != KeyEvent.VK_A);
            d_down &= (e.getKeyCode() != KeyEvent.VK_D);
            w_down &= (e.getKeyCode() != KeyEvent.VK_W);
            s_down &= (e.getKeyCode() != KeyEvent.VK_S);
            ctrl_down &= (e.getKeyCode() != KeyEvent.VK_CONTROL);
         }


	public void keyTyped(KeyEvent e) {}
}
