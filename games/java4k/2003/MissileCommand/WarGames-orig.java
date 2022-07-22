/*
 * WarGames.java
 *
 * Created on November 5, 2002, 10:16 PM
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class WarGames extends MouseAdapter implements Runnable, KeyListener
{
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final int FPS = 40;
    private static final int BASE_SIZE = 80;
    private static final int EXPLOSION = 50;
    
    private static Frame frame = new Frame();
    private static WarGames war = new WarGames();

    private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static Image target;

    private boolean running = false;
    
    private boolean[] baseAlive = new boolean[6];
    private Point2D[] points;
    private double[] stepx;
    
    private int level = 0;
    private int score = 0;
    private Point fire = null;
    private int fireSize = 0;
    private int dir = 1;
    private int spacing = (int)((WIDTH-(BASE_SIZE*baseAlive.length))/(baseAlive.length-1));
    
    public WarGames() 
    {
        frame.addMouseListener(this);
        frame.addKeyListener(this);
    }

    public static void main(String[] args)
    {
        Dimension dim;
        Cursor cursor;
        
        try 
        {
            frame.setUndecorated(true);
            frame.setIgnoreRepaint(true);

            device.setFullScreenWindow(frame);
            device.setDisplayMode(new DisplayMode(640, 480, 8, 0));
            
            
            target = Toolkit.getDefaultToolkit().getImage(WarGames.class.getResource("/target.png"));
            while(target.getWidth(null) < 0);
            
            dim = Toolkit.getDefaultToolkit().getBestCursorSize(target.getWidth(null), target.getHeight(null));
            cursor = Toolkit.getDefaultToolkit().createCustomCursor(target, new Point(8*dim.width/target.getWidth(null), 8*dim.height/target.getHeight(null)), "target");
            frame.setCursor(cursor);         
            
            war.run();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    private void drawBases(Graphics g)
    {
        for(int i=0; i<baseAlive.length; i++)
        {
            if(baseAlive[i])
            {
                g.setColor(Color.white);
                g.fillOval(i*(spacing+BASE_SIZE), HEIGHT-10-BASE_SIZE/2, BASE_SIZE, BASE_SIZE);
                g.setColor(Color.blue);
                g.fillOval(i*(spacing+BASE_SIZE)+3, HEIGHT-10-BASE_SIZE/2+3, BASE_SIZE-6, BASE_SIZE-6);
            }
        }
        
        g.setColor(Color.blue);
        g.fillRect(0, HEIGHT-10, WIDTH, 10);
        g.setColor(Color.white);
        g.fillRect(0, HEIGHT-10, WIDTH, 2);
    }
    
    private void checkCollisions(Graphics g)
    {
        for(int i=0; i<points.length; i++)
        {
            if(fire != null && points[i] != null && new Ellipse2D.Double(fire.x-fireSize/2, fire.y-fireSize/2, fireSize, fireSize).contains(points[i]))
            {
                points[i] = null;
                score += 100;
            }
            
            for(int j=0; j<baseAlive.length; j++)
            {
                if(baseAlive[j] && points[i] != null && new Ellipse2D.Double(j*(spacing+BASE_SIZE), HEIGHT-10-BASE_SIZE/2, BASE_SIZE, BASE_SIZE).contains(points[i]))
                {
                    baseAlive[j] = false;
                    points[i] = null;
                    
                    g.setColor(Color.black);
                    g.fillRect(j*(spacing+BASE_SIZE), HEIGHT-10-BASE_SIZE/2, BASE_SIZE, BASE_SIZE/2);
                }
            }
        }
    }
    
    public void mousePressed(MouseEvent e)
    {
        if(fire == null)
        {
            fireSize = 1;
            dir = 1;
            fire = e.getPoint();
        }
    }
    
    private void checkLevel(Graphics g)
    {
        boolean alive = false;
        
        for(int i=0; i<baseAlive.length; i++)
        {
            if(baseAlive[i]) alive = true;
        }
        
        if(!alive)
        {
            running = false;
            level = 0;
            
            g.setColor(Color.black);
            g.fillRect(220, 190, 200, 20);
            g.setColor(Color.white);
            g.drawString("All your bases are destroyed!", 240, 200);
            try{Thread.sleep(5000);} catch(InterruptedException e) {}
            
            g.setColor(Color.black);
            g.fillRect(220, 190, 240, 20);
        }
    }
    
    public void run()
    {
        Graphics g = frame.getGraphics();
        
        long timer = System.currentTimeMillis();
        long sleep = 0;
        
        int left = 0;
        int basesLeft = 0;
            
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Courier", Font.PLAIN, 12));
        
        while(true)
        {
            if(running) 
            {
                g.setColor(Color.black);
                g.fillRect(0, 0, WIDTH, HEIGHT);
                drawBases(g);
                
                points = new Point2D[level];
                stepx = new double[level];
                left = level * 8;
            }
            else
            {
                g.setColor(Color.white);
                if(!running) g.drawString("Press Space Bar to Start", 240, 200);
            }
        
            while(running)
            {
                g.setColor(Color.black);
                g.fillRect(2, 4, 75, 25);
                g.setColor(Color.white);
                g.drawString("Score: "+Integer.toString(score), 2, 20);

                checkCollisions(g);
                checkLevel(g);

                for(int i=0; i<points.length; i++)
                {
                    if(points[i] == null)
                    {
                        if(left == 0) 
                        {
                            running = false;
                        }
                        else
                        {
                            points[i] = new Point2D.Double((int)(Math.random()*WIDTH), 0);
                            stepx[i] = (((int)(Math.random()*WIDTH)) - points[i].getX()) / (HEIGHT - points[i].getY());
                            
                            left--;
                        }
                    }
                    else
                    {
                        for(int j=0; j<3; j++)
                        {
                            g.drawLine((int)points[i].getX(), (int)points[i].getY(), (int)points[i].getX(), (int)points[i].getY());
                            
                            points[i].setLocation((double)points[i].getX() + stepx[i], (double)points[i].getY()+1);
                            
                            if(points[i].getY() > HEIGHT-10) points[i] = null;
                        }
                    } 
                }

                if(fire != null)
                {
                    g.setColor(Color.orange);
                    g.fillOval(fire.x-fireSize/2, fire.y-fireSize/2, fireSize, fireSize);

                    fireSize += dir*2;

                    if(fireSize > EXPLOSION)
                    {
                        g.setColor(Color.black);
                        g.fillOval(fire.x-fireSize/2, fire.y-fireSize/2, fireSize, fireSize);

                        fire = null;
                    }
                }

                sleep = (long)(1000/FPS)-(System.currentTimeMillis()-timer);

                if(sleep > 0) try{Thread.sleep(sleep);} catch(InterruptedException e) {}

                timer = System.currentTimeMillis();
            }
            
            basesLeft = 0;
            for(int i=0; i<baseAlive.length; i++)
            {
                if(baseAlive[i]) basesLeft++;
            }
            
            for(int i=0; i<baseAlive.length && !running; i++)
            {
                if(baseAlive[i]) 
                {
                    running = true;
                    
                    if(level > 0)
                    {
                        g.setColor(Color.black);
                        g.fillRect(260, 190, 110, 20);
                        g.setColor(Color.white);
                        g.drawString("Level Complete!", 260, 200);

                        try{Thread.sleep(2000);} catch(InterruptedException e) {}
                        
                        g.setColor(Color.black);
                        g.fillRect(260, 190, 110, 20);
                        g.setColor(Color.white);
                        g.drawString("Bonus "+basesLeft+" x 1000!", 260, 200);
                        
                        score += (basesLeft*1000);
                        try{Thread.sleep(2000);} catch(InterruptedException e) {}
                        
                        g.setColor(Color.black);
                        g.fillRect(260, 190, 110, 20);
                        g.setColor(Color.white);
                        g.drawString("Level "+(level+1), 260, 200);
                        
                        try{Thread.sleep(2000);} catch(InterruptedException e) {}
                    }

                    level++;
                }
            }
        }
    }
    
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == e.VK_ESCAPE) System.exit(0);
        
        if(e.getKeyCode() == e.VK_SPACE && ! running) 
        {
            for(int i=0; i<baseAlive.length; i++) baseAlive[i] = true;
            score = 0;
        }
    }
    
    public void keyReleased(KeyEvent e) {}
    
    public void keyTyped(KeyEvent e) {}
    
}