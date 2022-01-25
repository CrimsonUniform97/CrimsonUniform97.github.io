/*

   filename: BrownianMotionGame.java

   Programmed by Yau Yat Fai.

    8 Sep 2002  Project started
    9 Sep 2002  Migration from multi-threaded to single-threaded operation
   10 Sep 2002  Game rules (multi-level) implemented
   11 Sep 2002  Physics of colliding bodies implemented
   12 Sep 2002  Some values hard-coded to save code
   13 Sep 2002  Class renamed to BrownianMotionGame
                An inner circle is added to the red balls
   14 Sep 2002  Some redundant codes and variables removed
                Additional collision criteria checking added to improve the
                  efficiency of collision-checking.

   This applet demonstrates the manipulation of a "self" thread.

   The aim of the game is to hit the required number of times on
   balls appearing red in color.  Color of the balls changes over
   time.

   Once the required number of red balls are hit, the level is
   raised. The balls will be smaller, faster and the red balls
   will flash faster (on average).  The number of required hits
   will increase with increasing level.

   The player wins the game upon finishing off level 9; the player
   loses upon scoring 10 mis-hits.  A mis-hit is hitting a ball
   which is not red in color.

*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Random;

public class BrownianMotionGame extends Applet implements Runnable
{
  final int CNT  = 16;             // no of balls
  final int ICC  = 500;            // initial color change timeout
  final int CCT  = 200;            // stochastic color change period
  final float IR = 17;             // initial radius of balls
        int MC   = 150;            // minimum red-period count down
  final String BS= "Start";

  float r;
  int cct;

  static float Mx, mx, My, my;     // margins of the sail-able area
  Random rand;
  int lvl  = 9;                    // play level 
  int hit  = 0;                    // player hit
  int need = 5;                    // no of hit needed to go up a level
  int mh   = 0;                    // player mis-hits
  boolean is_won = true;

  Image sb;                        // double buffering
  Graphics gb;

  Thread t;                        // Applet embedded thread of the balls
  Color c[];                       // color of the balls
  float px[], py[], vx[], vy[];    // position and velocities of the balls
  boolean h[];                     // is-hit flag
  int mc[];                        // minimum red-period count down

  Button B;

  public void init()
  {
    resize(300, 350);
    setLayout (new BorderLayout());
    B = new Button(BS);
    add("South", B);

    // Initialize the random number machine
    rand = new Random();

    // Initialize the variables
    px = new float[CNT];
    py = new float[CNT];
    vx = new float[CNT];
    vy = new float[CNT];
    c  = new Color[CNT];
    h  = new boolean[CNT];
    mc = new int[CNT];

    // Initialize the double buffer
    sb = createImage(300,350);
    gb = sb.getGraphics();
    gb.setColor(Color.cyan);
    outText("Press Start to begin");

    MC   = 150;
    r    = 6;
    cct  = CCT;

    // fill in initial values for the balls
    ii((float)2.5);

    t = new Thread(this);
    t.start();
  }

  public void run()
  {
    int ic = 0;

    while (true)
    {
      for (int s=CNT-1; s>=0; s--)
      {
        // displace the ball
        px[s] = px[s] + vx[s];
        py[s] = py[s] + vy[s];

        // out-of-boundary check and correction
        if(px[s]>Mx)
        {
          px[s] = 2*Mx - px[s];
          vx[s] = -vx[s];
        }
        if(px[s]<mx)
        {
          px[s] = 2*mx - px[s];
          vx[s] = -vx[s];
        }
        if(py[s]>My)
        {
          py[s] = 2*My - py[s];
          vy[s] = -vy[s];
        }
        if(py[s]<my)
        {
          py[s] = 2*my - py[s];
          vy[s] = -vy[s];
        }

        // next random color
        if ((ic>ICC)&&((rand.nextInt() % cct)==0)&&(mc[s]==0))
        switch (Math.abs(rand.nextInt() % 6))
        {
          case 0:
            c[s] = Color.blue;
            break;
          case 1:
            c[s] = Color.cyan;
            break;
          case 2:
            c[s] = Color.orange;
            break;
          case 3:
            c[s] = Color.magenta;
            break;
          case 5:
            c[s] = Color.red;
            mc[s] = MC;       // set the "gauranteed" period of redness
            break;
        }
        // count for timeout of initial color change
        if (ic<=ICC) ic++;
        // Override the color change if it is hit!
        if (h[s])
        {
          c[s] = Color.white;
          h[s] = false;
          ic = 0;
          mc[s] = MC;
        }

        // red, "gauranteed" period count down
        if (mc[s]>0) { mc[s]--; }

      }

      // repaint all balls
      checkCollision();
      repaint();
      try { Thread.sleep(5); }
      catch(Exception e) {}
    }
  }

  synchronized public void paint(Graphics g)
  {
    // clear screen
    gb.setColor(Color.white);
    gb.fillRect(0,0,300,300);
    gb.setColor(Color.black);
    gb.drawRect(1,1,298,298);

    // draw the balls
    for (int i=0; i<CNT; i++)
    {
       // Draw the ball;
       gb.setColor(c[i]);
       gb.fillOval((int)(px[i]-r),(int)(py[i]-r),(int)(2*r),(int)(2*r));

       // Add another circle so that color-blinded can still play
       if (c[i]==Color.red)
       {
         gb.setColor(Color.black);
         gb.drawOval((int)(px[i]-r/2),(int)(py[i]-r/2),(int)r,(int)r);
       }

       // Anti-aliasing
       gb.setColor(Color.black);
       gb.drawOval((int)(px[i]-r),(int)(py[i]-r),(int)(2*r),(int)(2*r));

    }

    // update the screen
    g.drawImage(sb,0,0,this);
  }

  synchronized public void checkCollision()
  {
    for (int i=0; i<CNT-1; i++)
      for (int j=i+1; j<CNT; j++)
      {
        float dy = py[i] - py[j];
        float dx = px[i] - px[j];
        float r2 = 2*r;

        if (((dy<r2)&&(dy>-r2))||((dx<r2)&&(dx>-r2)))
        {

          // distance^2 between ball i and ball j
          float d = (px[i]-px[j])*(px[i]-px[j])+(py[i]-py[j])*(py[i]-py[j]);

          // too close!
          if (d<(r2*r2))
          {
            // There is "real" collision only if they approach each other
            if (((vx[i]*dx+vy[i]*dy)-(vx[j]*dx+vy[j]*dy))<0)
            {
              // Physics of collision
              float txi = (vx[j]*dx*dx + vy[j]*dx*dy + vx[i]*dy*dy - vy[i]*dx*dy)/d;
              float tyi = (vx[j]*dx*dy + vy[j]*dy*dy - vx[i]*dx*dy + vy[i]*dx*dx)/d;
              float txj = (vx[i]*dx*dx + vy[i]*dx*dy + vx[j]*dy*dy - vy[j]*dx*dy)/d;
              vy[j]     = (vx[i]*dx*dy + vy[i]*dy*dy - vx[j]*dx*dy + vy[j]*dx*dx)/d;
              vx[i] = txi; vy[i] = tyi;
              vx[j] = txj;
            }
          }
        }
      }
  }

  synchronized public void update(Graphics g)
  {
    paint(g);
  }

  synchronized public boolean action(Event e, Object ob)
  {
    if ((e.target instanceof Button)&&(ob.toString()==BS))
    {
      // try to stop the thread if it is already (always) started
      try { t.stop(); } catch (Exception ex) {}

      outText("Click the red balls");
      B.disable();
      MC   = 150;
      r    = IR;
      cct  = CCT;
      hit  = 0;
      need = 5;
      lvl  = 1;
      mh   = 0;
      is_won = false;

      // fill in initial values for the balls
      ii((float)0.7);

      // Initialize the balls and start the game
      System.gc();
      t = new Thread(this);
      t.start();
    }
    return false;
  }

  synchronized public boolean mouseUp(Event e, int x, int y)
  {
    if((y<300)&&(!is_won))
    {
      // check if hit
      boolean ih = false;
      int i = 0;
      while ((i<CNT)&&(!ih))
      {
         float d = ((float)x - px[i])*((float)x - px[i])
                 + ((float)y - py[i])*((float)y - py[i])
                 // with anti-aliasing, r should be decreased by one
                 - (r - 1)*(r - 1);
         if (d<=0)
         {
           // Hit!
           if (c[i]==Color.red) { ih = true; hit++; h[i] = true; }
           // Mis-hit!
           else { mh++; i++; }
         }
         else i++;
      }

      // Check if the player wins or loses
      if (mh>=10)
      {
        outText("You lose!");
        is_won = true;
        B.enable();
      }
      else
      if (hit>=need)
      {
        if (lvl==9)
        {
          outText("You win!  mis-hit = "+Integer.toString(mh));
          is_won = true;
          B.enable();
        }
        else
        {
          lvl++;
          rt();   // reset thread t for a new level
          ss();   // update the status line
        }
      }
      else
      {
        ss();     // update the status line
      }
      return true;
    }
    return false;
  }

  synchronized public void ii(float va)
  {
    // Initialize the margins
    Mx = 299 - r;
    mx = r + 1;
    My = 299 - r;
    my = r + 1;

    // Initialize the balls
    float x1 = (float)0.5;
    float y1 = (float)0.5;
    for (int i=0; i<CNT; i++)
    {
      if(x1>4){ x1 = (float)0.5; y1 = y1 + 1; }
      px[i] = x1 * 75;
      py[i] = y1 * 75;
      vx[i] = (rand.nextFloat() - (float)0.5) * va;
      vy[i] = (rand.nextFloat() - (float)0.5) * va;
      x1 = x1 + 1;
      c[i] = Color.blue;
      h[i] = false;
      mc[i] = 0;
    }
  }

  synchronized public void rt()
  {
    // clear the thread
    t.stop();
    t = null;  // redundant??

    // re-initialize the variables to a new level
    r = r - 1;
    ii((float)lvl/(float)5.0+(float)0.7);
    hit = 0; need = need + 2;
    MC = MC - 5;

    // dump garbage and start a new thread
    System.gc();
    t = new Thread(this);
    t.start();
  }

  synchronized public void ss()
  {
    outText("Level = "+Integer.toString(lvl)
           +"  Hit = "+Integer.toString(hit)
           +"  mis-hit = "+Integer.toString(mh)
           +"  Next level = "+Integer.toString(need));
  }

  synchronized public void outText(String S)
  {
    gb.setColor(Color.white);
    gb.fillRect(0,300,300,40);
    gb.setColor(Color.blue);
    gb.drawString(S, 5, 312);
  }

}