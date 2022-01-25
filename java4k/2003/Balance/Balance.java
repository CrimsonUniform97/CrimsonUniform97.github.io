// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 24-11-2002 16:06:01
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Balance.java

import java.awt.*;
import java.awt.event.*;

public class Balance extends Frame
    implements MouseListener, MouseMotionListener, Runnable
{

    public Balance()
    {
        gameOver = false;
        font = new Font("a", 0, 25);
        rect = new Rectangle(20, 30, 600, 400);
        score = 0;
        startTime = 0L;
        time = 0L;
        speed = 6;
        points = new int[15][2];
        straal = 12;
        mouseX = 100;
        mouseY = 100;
        kwadrant1 = 0;
        kwadrant2 = 0;
        kwadrant3 = 0;
        kwadrant4 = 0;
        last13 = 0;
        last24 = 0;
        kw13 = false;
        kw24 = false;
        setBounds(50, 50, 640, 450);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                System.exit(0);
            }

        });
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        for(int i = 0; i < points.length; i++)
        {
            points[i][0] = random(rect.x + straal, (rect.x + rect.width) - straal);
            points[i][1] = random(rect.y + straal, (rect.y + rect.height) - straal);
        }

        setVisible(true);
        setDB();
        t = new Thread(this);
        t.start();
    }

    void setDB()
    {
        img = createImage(getWidth(), getHeight());
        imgGr =(Graphics2D)img.getGraphics();
		RenderingHints rh=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		imgGr.addRenderingHints(rh);
    }

    public void paint(Graphics g)
    {
        g.drawImage(img, 0, 0, this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void run()
    {
        startTime = System.currentTimeMillis() / 1000L;
        while(!gameOver) 
        {
            time = System.currentTimeMillis() / 1000L - startTime;
            if(time >= 300L)
                gameOver = true;
            kw13 = false;
            kw24 = false;
            countPoints();
            kw13 = kwadrant1 == kwadrant3 && kwadrant1 != 0 && kwadrant1 != last13;
            kw24 = kwadrant2 == kwadrant4 && kwadrant2 != 0 && kwadrant2 != last24;
            imgGr.setColor(getBackground());
            imgGr.fillRect(0, 0, getWidth(), getHeight());
            if(gameOver)
            {
                imgGr.setFont(font);
                imgGr.setColor(Color.red);
                imgGr.drawString("Your score is : " + score, getWidth() / 3, getHeight() / 2);
            } else
            {
                if(kw13)
                {
                    imgGr.setColor(new Color(235, 255, 235));
                    imgGr.fillRect(rect.x, mouseY, mouseX - rect.x, (rect.y + rect.height) - mouseY);
                    imgGr.fillRect(mouseX, rect.y, (rect.x + rect.width) - mouseX, mouseY - rect.y);
                }
                if(kw24)
                {
                    imgGr.setColor(new Color(235, 255, 235));
                    imgGr.fillRect(rect.x, rect.y, mouseX - rect.x, mouseY - rect.y);
                    imgGr.fillRect(mouseX, mouseY, (rect.x + rect.width) - mouseX, (rect.y + rect.height) - mouseY);
                }
                imgGr.setColor(Color.CYAN);
                for(int i = 0; i < points.length; i++)
                {
                    int j = points[i][0];
                    int k = points[i][1];
                    points[i][0] += random(-speed, speed + 1);
                    points[i][1] += random(-speed, speed + 1);
                    if(points[i][0] - straal <= rect.x || points[i][0] + straal >= rect.x + rect.width)
                        points[i][0] = j;
                    if(points[i][1] - straal <= rect.y || points[i][1] + straal >= rect.y + rect.height)
                        points[i][1] = k;
                    imgGr.setColor(Color.black);
                    imgGr.fillOval(points[i][0] - straal, points[i][1] - straal, 2 * straal, 2 * straal);
                    imgGr.setColor(Color.yellow);
                    imgGr.fillOval(points[i][0] - straal+1, points[i][1] - straal+1, 2 * straal-2, 2 * straal-2);
                }

                imgGr.setColor(Color.RED);
                imgGr.drawLine(rect.x, mouseY, rect.x + rect.width, mouseY);
                imgGr.drawLine(mouseX, rect.y, mouseX, rect.y + rect.height);
                imgGr.drawString(kwadrant2 + "   " + kwadrant1, (rect.x + rect.width) - 40, (rect.y + rect.height) - 20);
                imgGr.drawString(kwadrant3 + "   " + kwadrant4, (rect.x + rect.width) - 40, (rect.y + rect.height) - 5);
                imgGr.drawString("Score : " + score, rect.x + 5, (rect.y + rect.height) - 5);
                imgGr.drawString("Time : " + time, (rect.x + rect.width) - 65, rect.y + 20);
            }
            imgGr.drawRect(rect.x, rect.y, rect.width, rect.height);
            repaint();
            try
            {
                Thread.sleep(100L);
            }
            catch(InterruptedException interruptedexception) { }
        }
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
        if(kw13 && kw24)
        {
            score += 10 * (kwadrant1 + kwadrant2);
            last13 = kwadrant1;
            last24 = kwadrant2;
        } else
        if(kw13)
        {
            score += 5 * kwadrant1;
            last13 = kwadrant1;
        } else
        if(kw24)
        {
            score += 5 * kwadrant2;
            last24 = kwadrant2;
        } else
        {
            boolean flag = false;
            for(int i = 0; i < points.length; i++)
                flag = flag || collision(points[i]);

            if(!flag)
                score -= 2;
        }
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mouseDragged(MouseEvent mouseevent)
    {
        setMouse(mouseevent);
    }

    public void mouseMoved(MouseEvent mouseevent)
    {
        setMouse(mouseevent);
    }

    public void setMouse(MouseEvent mouseevent)
    {
        int i = mouseevent.getX();
        int j = mouseevent.getY();
        if(i > rect.x && i < rect.x + rect.width)
            mouseX = i;
        if(j > rect.y && j < rect.y + rect.height)
            mouseY = j;
    }

    static int random(int i, int j)
    {
        return i + (int)((double)(j - i) * Math.random());
    }

    boolean collision(int ai[])
    {
        if(mouseX > ai[0] - straal && mouseX < ai[0] + straal && mouseY > ai[1] - straal && mouseY < ai[1] + straal)
        {
            ai[0] = random(rect.x + straal, (rect.x + rect.width) - straal);
            ai[1] = random(rect.y + straal, (rect.y + rect.height) - straal);
            score += 4;
            return true;
        } else
        {
            return false;
        }
    }

    void countPoints()
    {
        kwadrant1 = 0;
        kwadrant2 = 0;
        kwadrant3 = 0;
        kwadrant4 = 0;
        for(int i = 0; i < points.length; i++)
        {
            boolean flag = points[i][0] > mouseX;
            boolean flag1 = points[i][1] < mouseY;
            if(flag1 && flag)
                kwadrant1++;
            else
            if(flag1 && !flag)
                kwadrant2++;
            else
            if(!flag1 && !flag)
                kwadrant3++;
            else
                kwadrant4++;
        }

    }

    public static void main(String args[])
    {
        new Balance();
    }

    boolean gameOver;
    Font font;
    Rectangle rect;
    int score;
    long startTime;
    long time;
    int speed;
    int points[][];
    int straal;
    int mouseX;
    int mouseY;
    int kwadrant1;
    int kwadrant2;
    int kwadrant3;
    int kwadrant4;
    int last13;
    int last24;
    boolean kw13;
    boolean kw24;
    Image img;
    Graphics2D imgGr;
    Thread t;
}