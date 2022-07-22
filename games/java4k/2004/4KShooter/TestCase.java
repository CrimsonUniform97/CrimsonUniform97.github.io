import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
public class TestCase extends Frame implements Runnable
{
   
   static final int IMAGE_WIDTH = 32, IMAGE_HEIGHT = 32;
   static final int SHIP_WIDTH = 21, SHIP_HEIGHT = 25;

   static final int NUM_ROTATIONS = 90;
   static final float ANGLE_INC = (float)((Math.PI*2d) / NUM_ROTATIONS);
   
   static final String STRING_SHIPS_FILENAME = "image.gif";
   
   static final int SCREEN_WIDTH = 400,SCREEN_HEIGHT = 300;
   
   BufferedImage loadedImage;
   
   BufferedImage [] objectImages = new BufferedImage[NUM_ROTATIONS];
   
   public TestCase() throws Exception
   {
      super("ManagedImage/AffineTransform bug");
      
      setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
      show();
      GraphicsConfiguration gc = getGraphicsConfiguration();
      
      //load the image
      loadedImage = ImageIO.read(getClass().getResource(STRING_SHIPS_FILENAME));

      //copy it into the centre of the 1st element of the array
      BufferedImage src = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT, Transparency.BITMASK);
      {
         Graphics2D g = src.createGraphics();
         g.drawImage(loadedImage,(IMAGE_WIDTH-SHIP_WIDTH)/2,(IMAGE_HEIGHT-SHIP_HEIGHT)/2,null);
      }
         
      for(int i =0;i < NUM_ROTATIONS;i++)
      {
         //create rotations
         objectImages[i] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT,Transparency.BITMASK);
         Graphics2D g = objectImages[i].createGraphics();
         g.rotate(i*ANGLE_INC,IMAGE_WIDTH/2,IMAGE_HEIGHT/2); //combined, saved 1byte
         g.drawImage(src,0,0,null);
      }
         
      createBufferStrategy(2);
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      
      Thread me = new Thread(this);
      me.start();
   }
   
   boolean running = true;
   
   public void processWindowEvent(WindowEvent we)
   {
      if(we.getID()==WindowEvent.WINDOW_CLOSING)
      {
         running = false;
      }
   }
   
   public void run()
   {
      int angle = 80;
      while(running)
      {
         BufferStrategy bs = getBufferStrategy();
         Graphics g = bs.getDrawGraphics();
         g.setColor(Color.black);
         g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
         g.drawImage(objectImages[angle],40,40,null);
         g.setColor(Color.white);
         g.drawString(String.valueOf(angle*4),50,90);
         bs.show();
         
         
         angle=(angle+1)%NUM_ROTATIONS;
         try
         {
            Thread.sleep(50);
         }
         catch(Exception e)
         {
         }
      }
      dispose();
   }
   
   
   public static void main(String [] args)
   {
      try
      {
         new TestCase();
      }
      catch(Exception e)
      {
         System.out.println("Something went wrong");
      }
   }
}