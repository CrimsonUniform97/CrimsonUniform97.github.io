import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import javax.sound.midi.*;
class A extends Frame
{
   static final int WINDOWS = 0;
   static final int LINUX = 1;
   static final int MAC = 2;
   
   static final int PLATFORM = WINDOWS;
   
   static final boolean INCLUDE_PROPERTIES = true; //include flags for experimental acceleration
   static final boolean DEBUG = false;       //gives framerate info and other output
   
   static final boolean JARRED = true;       //generating the jarred version (makes a difference to the resource names)
   
   static final boolean FULLSCREEN = true;     //FULLSCREEN
   
   static final boolean EXPERIMENTAL = true; //toggle this for Experimental acceleration on or off

   static final boolean SOUND_ON = true;
   static final boolean CUSTOM_CURSOR = false;
   static final boolean BLUR_CURSOR = false; //reduce code size
   static final boolean RADAR = true;       //reduce code size
   
   static final boolean USE_MILLITIME_OVERRIDE = false; //use millitime on windows (even though it is SHIIIIIIIIIIIT)
   // ^^ set this for 1.4.x compatibility
   static final boolean USE_NANOTIME_OVERRIDE = false; //use nanotime on mac and linux (even though it isn't realy needed)
   // ^^ set this for 1.5.x requirement (even on linux/mac)
   
   static final boolean USE_MILLITIME = USE_MILLITIME_OVERRIDE || (PLATFORM!=WINDOWS && !USE_NANOTIME_OVERRIDE);
   
   static final boolean BULLET_CREATION_BUG_FIX = true; //so bullets are created in exactly the correct place (not necessary, but its nice :P)
   
   static final boolean SHIELD = true; //reduce code size
   
   static final boolean QUALITY_RENDERING = false; //reduce code size
   
   static final boolean PREDEFINED_SCREENSIZE = PLATFORM==WINDOWS || !FULLSCREEN;

/********************* START String Constants  *********************/
   //static final String STRING_CURSOR_FILENAME = "cursor.gif";
   
   static final String STRING_SHIPS_FILENAME = JARRED?"0":"a.gif";
   
//   static final String STRING_YOU_LOST = "You Lost";
//   static final String STRING_YOU_WON = "You Won!";
   static final String STRING_START = "Press Space";
   static final int STRING_START_WIDTH = 69/2;
   static final int STRING_START_HEIGHT = 11/2;
/********************* END String Constants  *********************/

/********************* START Ship Dimensions  *********************/
   static final int IMAGE_WIDTH = 32,IMAGE_HEIGHT = 32;

   static final int SHIP_WIDTH = 21; //used for offsets
   static final int SHIP_HEIGHT = 25;
   static final int SHIP_DIAMETER = 25+4; //Math.max(SHIP_WIDTH,SHIP_HEIGHT); - used for collision detection
   
   static final int NME_SHIP_WIDTH = 19;
   static final int NME_SHIP_HEIGHT = 19;
   static final int NME_IMAGE_X_OFFSET = 0;
   static final int NME_IMAGE_Y_OFFSET = 31;
   static final int NME_SHIP_DIAMETER = 20;  //Math.max(NME_SHIP_WIDTH,NME_SHIP_HEIGHT);
   
   static final int NUM_ROTATIONS = 90;
   
   static final float ANGLE_INC = (float)((Math.PI*2d) / NUM_ROTATIONS);

   static final int SHIP_X_OFFSET = SHIP_WIDTH/2-1;
   //static final int SHIP_Y_OFFSET = 0; //not used on initialisation (cos its zero)
   
   static final int NME_SHIP_X_OFFSET = 0; //not used on initialisation (cos its zero)
   //static final int NME_SHIP_Y_OFFSET = 6; //not used (save code)

   static final float MAX_SHIP_ROT = (float)(Math.PI/40d);
   
   static final int SHIP_FIRE_DELAY = 6;
   static final int NME_SHIP_FIRE_DELAY = 22;
   
/********************* END Ship Dimensions  *********************/

/********************* START bullet Texture Dimensions  *********************/

   static final int BULLET_WIDTH = 2;
   static final int BULLET_HEIGHT = 22; //20 changed to 22 so (IMAGE_HEIGHT-BULLET_HEIGHT)/2=5 (rather than 6); this makes for a 1byte saving (iconst_5 rather than bipush 6)
   static final int BULLET_RADIUS = 1;
   static final int BULLET_SPEED = 10;

/********************* END bullet Texture Dimensions  *********************/

/********************* START particle dimensions *********************/

   static final int DEBRIS_WIDTH = 3;
   static final int DEBRIS_HEIGHT = 3;
   static final int DEBRIS_RADIUS = 3;
   
   static final int EXPLOSION_SIZE = 50;

/********************* END particle dimensions *********************/

/********************* START Array Sizes  *********************/
   static final int MAX_NUM_OBJECTS = 10000;

   static final int NUM_STARS = 100;
/********************* END Array Sizes  *********************/

/********************* START Object Attribute indexs  *********************/
   static final int NUM_ATTRIBUTES = SHIELD?16:15; //if shields are enabled, an extra attribute must be added.(LAST_HIT)

   static final int X = 0;
   static final int Y = 1;
   static final int DX = 2,COLOR = 2; //COLOR used by the stars
   static final int DY = 3;
   static final int TYPE = 4;
   static final int ANGLE = 5;
   static final int TARGET_X = 6;
   static final int TARGET_Y = 7;
   static final int RADIUS = 8;
   static final int LIFE = 9;
   static final int RELOAD = 10;
   static final int FIRE_DELAY = 11;
   static final int STATE = 12;
   static final int X_OFFSET = 13;
   //static final int Y_OFFSET = 14;
   static final int DAMAGE = 14;
   static final int LAST_HIT = 15;//countdown for shield impact effect. always the last attribute
   
/********************* END Object Attribute indexs  *********************/

   // values for 'STATE'
   static final int NOT_FIRING = 0;
   static final int FIRING = InputEvent.BUTTON1_DOWN_MASK;
   
   // values for 'TYPE'
   static final int SHIP = 0, HUMAN_PLAYER = 0;
   static final int NME_SHIP = 1;
   static final int BULLET = 2;
   static final int NME_BULLET = 3;
   static final int DEBRIS = 4;
   
   //values for 'LIFE'
   static final int SHIP_LIFE = 500;
   static final int NME_SHIP_LIFE = 100;
   static final int BULLET_LIFE = 100;
   static final int DEBRIS_LIFE = 400;
   
   static final float HIT_DURATION = 30;//used for the LAST_HIT attribute when SHIELD is enabled

   //values for 'DAMAGE'
   static final int SHIP_COLLISION_DAMAGE = DEBRIS_LIFE; //damage done by player ship when it hits NME
   static final int NME_SHIP_COLLISION_DAMAGE = BULLET_LIFE*3; //damage done by NME ship when it hits player
   static final int BULLET_COLLISION_DAMAGE = NME_SHIP_LIFE/2; //damage done by player bullet when it hits NME
   static final int DEBRIS_LIFE_GIVE = -5; //life u get for touching debris from dead ships
   
   //DEBRIS_LIFE_GIVE * EXPLOSION_SIZE should never be > NME_SHIP_COLLISION_DAMAGE
   
/********************* START Game Area Statics  *********************/
   static final int SCREEN_WIDTH = 1024;
   static final int SCREEN_HEIGHT = 768;
   
   static final int MAP_WIDTH = SCREEN_WIDTH*5,MAP_HEIGHT = SCREEN_HEIGHT*5;
/********************* END Game Area Statics  *********************/

/********************* START Starfield Statics  *********************/
   static final int STAR_WIDTH = 2, STAR_HEIGHT = 2;
   static final float STAR_MAX_BRIGHTNESS = 0.85f;
   static final float STAR_MIN_BRIGHTNESS = 0.15f;
/********************* END Starfield Statics  *********************/

/*******************************************************************************
 ************************** Misc utility functions *****************************
 ******************************************************************************/
 
   //unused
/*   private static float dot(float ax, float ay, float bx, float by)
   {
      return ax*bx+ay*by;
   }*/
   
/*******************************************************************************
 **************************** Control/Input Code *******************************
 ******************************************************************************/
   int [] controls = new int[256+KEY_START_INDEX]; //made static - quite abit smaller



   static final int MOUSE_MODIFIERS = 0;
   static final int MOUSE_X = 1;
   static final int MOUSE_Y = 2;
   //it is better to have the explicitly defined indexes in the 1st 5 elements of the array
   //because 0,1,2,3 and 4 all have special byte codes, so accessing them will require less bytecode space
   
   static final int KEY_START_INDEX = 0; //because no keys on the keyboard map to ASCII 0,1 or 2 this can be safely set to 0 (saves 2 bytes)
   public final void processEvent(AWTEvent e) //thx rag :D
   {
      int [] ctrls = controls;
      if(e instanceof KeyEvent)
      {
         ctrls[((KeyEvent)e).getKeyCode()] = e.getID()&1; //+KEY_START_INDEX FGS, JAX doesn't remove '+0'!!!! lame POS obfuscator!
         return;
      }
      else
      {
         MouseEvent mouse = (MouseEvent)e;
         ctrls[MOUSE_MODIFIERS]=mouse.getModifiersEx();
         ctrls[MOUSE_X]=mouse.getX();
         ctrls[MOUSE_Y]=mouse.getY();
         return;
      }
   }

/*******************************************************************************
 ************************** Application Entry Point ****************************
 ******************************************************************************/
   static
   {
      if(EXPERIMENTAL && INCLUDE_PROPERTIES)
      {
         if(PLATFORM==WINDOWS)
         {
            //System.setProperty("sun.java2d.ddforcevram","true"); //alternative to threshold 0 (3bytes smaller)
            System.setProperty("sun.java2d.translaccel", "true");
            System.setProperty("sun.java2d.accthreshold", "0");
         }
         else if(PLATFORM==LINUX)
         {
            System.setProperty("sun.java2d.opengl","true");
         }
      }
      
      if(DEBUG)
      {
         System.out.println(System.getProperty("java.version"));
      }
      new A();
   }

/*******************************************************************************
 ********************************** Constructor ********************************
 ******************************************************************************/
   public A()
   {
/********************* START Frame initialisation *****************************/
      try
      {
         int [] ctrls = controls;
         final int DISPLAY_WIDTH,DISPLAY_HEIGHT;
         MidiChannel [] mc;
         /****** Sound creation ********/
         if(SOUND_ON)
         {
            Synthesizer synthesizer;
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            
            //synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank()); //not needed

            final int VOLUME = 7;
            final int SUSTAIN = 64;
            final int REVERB = 91;
            
            final int INSTR_MUSIC_BOX = 10;
            // ^^ possibility instead of CRYSTAL
            //(but it leaves a horrible ringing sound :( )
            final int INSTR_MARIMBA = 12;    //MAP EDGE COLLISION - not that good, waste of code
            final int INSTR_SYN_DRUM = 118;  //SHOOTING SOUND
            final int INSTR_GUNSHOT = 127;   //SHIELD HIT SOUND
            final int INSTR_CRYSTAL = 98;    //POWERUP SOUND
            final int INSTR_TAIKO_DRUM = 116;//EXPLOSION SOUND
            
            (mc = synthesizer.getChannels())
            
              [0].programChange(INSTR_SYN_DRUM);
            mc[0].controlChange(REVERB,0);
//            mc[0].controlChange(SUSTAIN,127);
            
            mc[1].programChange(INSTR_GUNSHOT);
            mc[1].controlChange(VOLUME,127);
            
            mc[2].programChange(INSTR_CRYSTAL); 
            
            mc[3].programChange(INSTR_TAIKO_DRUM); 
            mc[3].controlChange(VOLUME,127);
         }
         /****** End sound creation *******/
         
         if(FULLSCREEN) setUndecorated(true);
         //not needed in fullscreen, and windowed mode looks better without it
   
         GraphicsConfiguration gc = getGraphicsConfiguration();
         
         Color white = new Color(1.0f,1.0f,1.0f,1.0f); //white is used 3 times, precalc is worthwhile
         Color blue = new Color(0.0f,0.0f,1.0f,1.0f); //blue is used 2 times, precalc is worthwhile
         //Color red = new Color(1.0f,0.0f,0.0f,1.0f); //red is used 2 times, precalc is worthwhile
         
         if(FULLSCREEN)
         {
            GraphicsDevice gd = gc.getDevice();
            gd.setFullScreenWindow(this);
            if(PLATFORM==WINDOWS)
            {
               gd.setDisplayMode(new DisplayMode(SCREEN_WIDTH,SCREEN_HEIGHT,16,60));
            }
            else
            {
               DISPLAY_WIDTH = getWidth();
               DISPLAY_HEIGHT = getHeight();
            }
         }
         else
         {
            setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
            show();
         }
         
         
/*********************** END Frame initialisation *****************************/

         /*********** Game Variables ************/
   
         //float [][] star = new float[][3];//x,y,color
         float [][] object = new float[MAX_NUM_OBJECTS+NUM_STARS][NUM_ATTRIBUTES];
   
         int numObjects=0;
         Graphics2D g;
         float tx=0,ty=0;

         float [] tmpAfloat, tmpBfloat;
         

/********************* START Image Loading and Generation *****************/

         BufferedImage [][] objectImages = new BufferedImage[4][NUM_ROTATIONS];

         //code reordered so 'blackImage is used twice' (saved 6 bytes)
         BufferedImage blackImage = ImageIO.read(getClass().getResource(STRING_SHIPS_FILENAME));
         //create ship image
         (objectImages[0][0] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT, EXPERIMENTAL?Transparency.TRANSLUCENT:Transparency.BITMASK))
         .createGraphics().drawImage(blackImage,(IMAGE_WIDTH-SHIP_WIDTH)/2,(IMAGE_HEIGHT-SHIP_HEIGHT)/2,null);
         
         //create nme ship image
         (objectImages[1][0] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT, EXPERIMENTAL?Transparency.TRANSLUCENT:Transparency.BITMASK))
         .createGraphics().drawImage(blackImage,(IMAGE_WIDTH-NME_SHIP_WIDTH)/2-NME_IMAGE_X_OFFSET,(IMAGE_HEIGHT-NME_SHIP_HEIGHT)/2-NME_IMAGE_Y_OFFSET,null);
         
         //create player bullet
         (g = (objectImages[2][0] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT,EXPERIMENTAL?Transparency.TRANSLUCENT:Transparency.BITMASK))
         .createGraphics())
         .setColor(blue);//new Color(0.0f,0.0f,1.0f,1.0f));//color blue
         g.fillRect((IMAGE_WIDTH-BULLET_WIDTH)/2,(IMAGE_HEIGHT-BULLET_HEIGHT)/2,BULLET_WIDTH,BULLET_HEIGHT);

         //create nme bullet
         (g = (objectImages[3][0] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT,EXPERIMENTAL?Transparency.TRANSLUCENT:Transparency.BITMASK))
         .createGraphics())
         .setColor(new Color(1.0f,1.0f,0.0f,1.0f));//yellow
         g.fillRect((IMAGE_WIDTH-BULLET_WIDTH)/2,(IMAGE_HEIGHT-BULLET_HEIGHT)/2,BULLET_WIDTH,BULLET_HEIGHT);

         //the image used for creating the fade to black blur effect
         if(EXPERIMENTAL)
         {
            if(PREDEFINED_SCREENSIZE)
            {
               (g = (blackImage = gc.createCompatibleImage(SCREEN_WIDTH,SCREEN_HEIGHT,Transparency.TRANSLUCENT)).createGraphics())
               .setColor(new Color(0.0f,0.0f,0.0f,0.13f));
               
               g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
            }
            else
            {
               (g = (blackImage = gc.createCompatibleImage(DISPLAY_WIDTH,DISPLAY_HEIGHT,Transparency.TRANSLUCENT)).createGraphics())
               .setColor(new Color(0.0f,0.0f,0.0f,0.13f));
               
               g.fillRect(0,0,DISPLAY_WIDTH,DISPLAY_HEIGHT);
            }
         }
/********************* END Image Loading and Generation *******************/

/********************* START Laser Generation *********************/
/*         for(int n = 0;n <2;n++)
         {
            g = objectImages[BULLET+n][0].createGraphics();
            for(int i = 0;i < BULLET_HEIGHT-1;i++)
            {
               float color = i/(BULLET_HEIGHT-1f);
               g.setColor(new Color(color,
                                    color* (1-n) + n,
                                    color* n + (1-n),
                                    color)
                                    );
               g.fillRect(0,i,BULLET_WIDTH,1);
            }
            g.fillRect(1,BULLET_HEIGHT-1,BULLET_WIDTH-2,1);
         }*/
         
         

/*         for(int n = 0;n <2;n++)//I can get rid of this for loop prolly
         {
            g = objectImages[BULLET+n][0].createGraphics();
            for(int i = 0;i < BULLET_WIDTH;i++)
            {
               float color = 1f-Math.abs(i-BULLET_WIDTH/2)/(BULLET_WIDTH/2f);
               Color c = new Color(color * (1-n) + n,
                                   color,
                                   color * n + (1-n));
               g.setColor(c);
               g.fillRect(i,0,1,BULLET_HEIGHT);
            }
         }*/
         
         //BUG discovered :p

/********************* END Laser Generation *********************/

/************* START rotation precalc Generation *************/
         for(int n = 0;n<4;n++)
         {
            BufferedImage src = objectImages[n][0];
            for(int i =0;i < NUM_ROTATIONS;i++)
            {
               (g = (objectImages[n][i] = gc.createCompatibleImage(IMAGE_WIDTH,IMAGE_HEIGHT,EXPERIMENTAL?Transparency.TRANSLUCENT:Transparency.BITMASK)).createGraphics())
               .rotate(i*ANGLE_INC,IMAGE_WIDTH/2,IMAGE_HEIGHT/2); //combined, saved 1byte
               if(QUALITY_RENDERING)
               {
                  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
               }
               g.drawImage(src,0,0,null);
            }
         }
/************* END rotation precalc Generation *************/

/*********************** START Background Generation **************************/
         for(int i =0;i<NUM_STARS;i++)
         {
            if(PREDEFINED_SCREENSIZE)
            {
               (tmpAfloat = object[i+MAX_NUM_OBJECTS])
                        [X] = (SCREEN_WIDTH*(float)Math.random());
               tmpAfloat[Y] = (SCREEN_HEIGHT*(float)Math.random());
               tmpAfloat[COLOR]= STAR_MIN_BRIGHTNESS + (STAR_MAX_BRIGHTNESS-STAR_MIN_BRIGHTNESS)/NUM_STARS*i;
            }
            else
            {
               (tmpAfloat = object[i+MAX_NUM_OBJECTS])
                        [X] = (DISPLAY_WIDTH*(float)Math.random());
               tmpAfloat[Y] = (DISPLAY_HEIGHT*(float)Math.random());
               tmpAfloat[COLOR]= STAR_MIN_BRIGHTNESS + (STAR_MAX_BRIGHTNESS-STAR_MIN_BRIGHTNESS)/NUM_STARS*i;
            }
         }
/*********************** END Background Generation ****************************/

/********************* START Cursor Loading *****************/
         BufferedImage cursorImage;
         
         if(CUSTOM_CURSOR)
         {
            cursorImage = gc.createCompatibleImage(32,32,Transparency.BITMASK);
            (g = cursorImage.createGraphics()).setColor(new Color(1.0f,0.0f,0.0f,1.0f));//color red
         
            g.fillRect(0,   16,16-4,1); //left
            g.fillRect(16+5,16,16-4,1); //right
         
            g.fillRect(16,0   ,1,16-4); //up
            g.fillRect(16,16+5,1,16-4); //down
            
            setCursor(getToolkit().createCustomCursor(cursorImage,new Point(16,16),null));
         }
         else
         {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); //much smaller than the alternative static method
         }
         
/********************* END Cursor Loading *****************/

/*********************** START Final initialisations **************************/

         createBufferStrategy(2);//if this line is anywhere near setDisplayMode() - it totally shags up
         VolatileImage blurBuffer;
         if(EXPERIMENTAL)
         {
            if(PREDEFINED_SCREENSIZE)
            {
               blurBuffer = createVolatileImage(SCREEN_WIDTH,SCREEN_HEIGHT); //the blur buffer, used to render all effects that will be blurred
            }
            else
            {
               blurBuffer = createVolatileImage(DISPLAY_WIDTH,DISPLAY_HEIGHT); //the blur buffer, used to render all effects that will be blurred
            }
         }

         enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.KEY_EVENT_MASK);
         //enabled mouse, mouseMotion and key events

         int playing = 0;
         
         int enemyCount;
         int runningTime=0;
         int score = 0;
/*********************** END Final initialisations ****************************/
                 
         
/****************************** START Game Loop *******************************/
         long startTime;
         int frameCount = 0; //DEBUG - will be removed by the obfuscator when DEBUG is set to false
   
         if(PLATFORM!=WINDOWS || !FULLSCREEN)
         {
            if(USE_MILLITIME)
            {
               startTime = System.currentTimeMillis();
            }
            else
            {
               startTime = System.nanoTime();
            }
         }
            
         
         int fpsCount = 0; //DEBUG - will be removed by the obfuscator when DEBUG is set to false
         String fps; //DEBUG - ""
         long prevTime; //DEBUG - ""
         
         if(DEBUG)
         {
            prevTime = System.currentTimeMillis();
            fps = "N/A";
            fpsCount = 0;            
         }
         
                  
         while(ctrls[KeyEvent.VK_ESCAPE+KEY_START_INDEX]==0)
         { 
            BufferStrategy bs;
            if(PLATFORM!=WINDOWS || !FULLSCREEN) frameCount++;
            enemyCount=0;
            g = (Graphics2D)((bs = getBufferStrategy()).getDrawGraphics());
            if(EXPERIMENTAL)
            {
               g.drawImage(blackImage,0,0,null);
            }
            else
            {
               g.setColor(new Color(0.0f,0.0f,0.0f,1.0f));//color black
               if(PREDEFINED_SCREENSIZE)
               {
                  g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
               }
               else
               {
                  g.fillRect(0,0,DISPLAY_WIDTH,DISPLAY_HEIGHT);
               }
            }
            g.setColor(white);//new Color(1.0f,1.0f,1.0f,1.0f));//white

            if(playing==0)
//START game starting block
            {
               if(PREDEFINED_SCREENSIZE)
               {
                  g.drawString(STRING_START, SCREEN_WIDTH/2-STRING_START_WIDTH,
                                             SCREEN_HEIGHT/2-STRING_START_HEIGHT);
               }
               else
               {
                  g.drawString(STRING_START, DISPLAY_WIDTH/2-STRING_START_WIDTH,
                                             DISPLAY_HEIGHT/2-STRING_START_HEIGHT);
               }
                              
               if((playing=ctrls[KeyEvent.VK_SPACE+KEY_START_INDEX])!=0) score=0;
               numObjects = 1;
               runningTime=0;
               (tmpAfloat = object[0])
                         [X] = MAP_WIDTH/2;
               tmpAfloat[Y] = MAP_HEIGHT/2;
               //tmpAfloat[DX] = 0; //not that important (5bytes)
               //tmpAfloat[DY] = 0; //not that important (5bytes)
               //tmpAfloat[ANGLE] = 0; //not that important (5bytes)
               tmpAfloat[RADIUS] = SHIP_DIAMETER/2;
               tmpAfloat[TYPE] = SHIP;
               tmpAfloat[LIFE] = SHIP_LIFE;
               tmpAfloat[X_OFFSET] = SHIP_X_OFFSET;
               //tmpAfloat[Y_OFFSET] = SHIP_Y_OFFSET;
               tmpAfloat[FIRE_DELAY]= SHIP_FIRE_DELAY;
               tmpAfloat[DAMAGE] = SHIP_COLLISION_DAMAGE;
            }
//END game starting block
            else
            {
/*************** START object update loop ******************/
               for(int i = 0;i<numObjects;i++)
               {
                  tmpBfloat = object[HUMAN_PLAYER];
                  
                  float x = (tmpAfloat = object[i])[X],
                         y = tmpAfloat[Y],
                         dx =tmpAfloat[DX],
                         dy=tmpAfloat[DY]; 
                  int type = (int)tmpAfloat[TYPE];
                  float radius = tmpAfloat[RADIUS];
                  float angle = tmpAfloat[ANGLE];
                  float targetX = tmpAfloat[TARGET_X];
                  float targetY = tmpAfloat[TARGET_Y];
                  //locally cache commonly used variables - life might be worth caching also - not sure
                  
                  x+=dx;
                  y+=dy;
                  
                  //START edge collision
                  
                  if(type<=NME_SHIP) //TYPE is either nme or player SHIP
                  {
                     float upDown,leftRight;

                     //float angleDif = tmpAfloat[TARGET_ANGLE]-tmpAfloat[ANGLE]; //   combine with \/
                     //angleDif = (angleDif+Math.PI*3)%(Math.PI*2)-Math.PI; //  this can be optimised  /\
                     //angleDif = Math.max(Math.min(angleDif,MAX_SHIP_ROT),-MAX_SHIP_ROT); // and this /\
                        
                     //float angleDif = tmpAfloat[TARGET_ANGLE]-tmpAfloat[ANGLE]; //   combine with \/
                     //angleDif = ; //  this can be optimised  /\
                     //angleDif = Math.max(Math.min((tmpAfloat[TARGET_ANGLE]-tmpAfloat[ANGLE]+Math.PI*3)%(Math.PI*2)-Math.PI,MAX_SHIP_ROT),-MAX_SHIP_ROT); // and this /\
   
                     //if(angleDif>MAX_SHIP_ROT) angleDif = MAX_SHIP_ROT;
                     //if(angleDif<-MAX_SHIP_ROT) angleDif = -MAX_SHIP_ROT;
                     float targetAngle = (float)(Math.PI)-(float)Math.atan2(x-targetX,y-targetY);

                     angle=(angle+Math.max(Math.min((targetAngle-angle+(float)(Math.PI*3))%((float)(Math.PI*2))-(float)Math.PI,MAX_SHIP_ROT),-MAX_SHIP_ROT)+(float)(Math.PI*2))%(float)(Math.PI*2);
                        //tmpAfloat[ANGLE]=(tmpAfloat[ANGLE]+angleDif+Math.PI*2)%(Math.PI*2);
                        
                     float shipSin = (float)Math.sin(angle);
                     float shipCos = (float)Math.cos(angle);
                           
                     if(i==HUMAN_PLAYER)
                     {
                        //START SEXEH edge collision detection
                        float edge;
                        if(x<(edge=radius) || x>(edge=(MAP_WIDTH-radius)))
                        {
                           //mc[4].noteOn(40,127);
                           x =2*edge-x;
                           dx=-dx; //smaller than *=-1;
                        }
                        if(y<(edge=radius) || y>(edge=(MAP_HEIGHT-radius)))
                        {
                           //mc[4].noteOn(40,127);
                           y = 2*edge-y;
                           dy=-dy;
                        }
                        //END SEXEH edge collision detection
                        
                        //this needs optimising
                        if(PREDEFINED_SCREENSIZE)
                        {
                           tx = Math.max(
                                         Math.min((SCREEN_WIDTH/2)-x-dx*8+shipSin*64,0),
                                                  -(MAP_WIDTH-(SCREEN_WIDTH))
                                         );
                           ty = Math.max(
                                         Math.min((SCREEN_HEIGHT/2)-y-dy*8-shipCos*64,0),
                                                  -(MAP_HEIGHT-(SCREEN_HEIGHT))
                                         );
                        }
                        else
                        {
                           tx = Math.max(
                                         Math.min((DISPLAY_WIDTH/2)-x-dx*8+shipSin*64,0),
                                                  -(MAP_WIDTH-(DISPLAY_WIDTH))
                                         );
                           ty = Math.max(
                                         Math.min((DISPLAY_HEIGHT/2)-y-dy*8-shipCos*64,0),
                                                  -(MAP_HEIGHT-(DISPLAY_HEIGHT))
                                         );
                        }
                        
                        tmpAfloat[STATE] = ctrls[MOUSE_MODIFIERS]&InputEvent.BUTTON1_DOWN_MASK;
                        targetX = ctrls[MOUSE_X]-tx;
                        targetY = ctrls[MOUSE_Y]-ty;
                        upDown = (ctrls[KeyEvent.VK_DOWN+KEY_START_INDEX]-ctrls[KeyEvent.VK_UP+KEY_START_INDEX]-(ctrls[MOUSE_MODIFIERS]/InputEvent.BUTTON3_DOWN_MASK))*0.3f;
                        
                        //a slight cheat here :p
                        //if the user presses the ALT_GRAPH_DOWN_MASK key (whatever the fuk that is :LOL:)
                        //the afterburner will be twice as powerful
                        leftRight = (ctrls[KeyEvent.VK_RIGHT+KEY_START_INDEX]-ctrls[KeyEvent.VK_LEFT+KEY_START_INDEX])*0.2f;
                     }
                     else
                     {
                        enemyCount++;
                        float distanceToPlayer = (float)Line2D.ptSegDist(x,y,x,y,tmpBfloat[X],tmpBfloat[Y]);//using ptSegDist because i've already used it for collision detection.

                        leftRight=0f;
                        upDown=-0.3f;
                        if(distanceToPlayer<600) //close to player, so target him, and start firing
                        {
                           if(distanceToPlayer<150) //too close to player, take evasive action
                           {
                              leftRight=i%2-0.5f;//-0.5 to 0.5
                           }
                           targetX = tmpBfloat[X]+tmpBfloat[DX]*distanceToPlayer/50;
                           targetY = tmpBfloat[Y]+tmpBfloat[DY]*distanceToPlayer/50;
                           tmpAfloat[STATE] = FIRING;
                        }
                        else if((float)Line2D.ptSegDist(x,y,x,y,targetX,targetY) < 50) //distance to target < 100
                        {
                           tmpAfloat[STATE] = NOT_FIRING;
                           targetX = MAP_WIDTH*(float)Math.random();//(numObjects*i*66571)%MAP_WIDTH;//instead of Math.random());
                           targetY = MAP_HEIGHT*(float)Math.random();//(numObjects*i*66931)%MAP_HEIGHT;//instead of Math.random());
                        }
                        // END AI code
                     }

                     dx=dx*0.95f+shipSin*upDown-shipCos*leftRight;
                     dy=dy*0.95f-shipCos*upDown-shipSin*leftRight;

                     tmpAfloat[LAST_HIT]--;
                     
                     if((--tmpAfloat[RELOAD])<0 && tmpAfloat[STATE]==FIRING) //ship is ready to fire, and is wanting to fire
                     {
                        if(SOUND_ON)
                        {
                           if(i==HUMAN_PLAYER) mc[0].noteOn(80, 100);
                        }
                        //START BULLET creation
                        tmpBfloat = object[numObjects++];
                        //Y_OFFSET has been removed (smaller code)
                        //-dx and -dy on [X] and [Y] have been removed (smaller code)
                        tmpBfloat[X] = x-shipCos*tmpAfloat[X_OFFSET]-(BULLET_CREATION_BUG_FIX?dx:0);
                        tmpBfloat[Y] = y-shipSin*tmpAfloat[X_OFFSET]-(BULLET_CREATION_BUG_FIX?dy:0);
                        tmpBfloat[DX] = dx+shipSin*-BULLET_SPEED; //added inheritance of parents velocity for bullets - its cooler :p
                        tmpBfloat[DY] = dy+shipCos*BULLET_SPEED;
                        tmpBfloat[ANGLE] = angle;
                        tmpBfloat[RADIUS] = BULLET_RADIUS;
                        tmpBfloat[TYPE] = type+2; //SHIP becomes BULLET, NME_SHIP becomes NME_BULLET
                        tmpBfloat[DAMAGE] = BULLET_COLLISION_DAMAGE;
                        tmpBfloat[LIFE] = BULLET_LIFE;
                        tmpBfloat[LAST_HIT] = 0;
                        //END BULLET creation
                        
                        tmpAfloat[X_OFFSET]=-tmpAfloat[X_OFFSET]; //toggle the firing weapon (makes no difference for the nme ship)
                        //sign toggling ^^ is much smaller than *=-1
                        tmpAfloat[RELOAD] = tmpAfloat[FIRE_DELAY]; //begin reload cycle
                     }
                  }
                  else
                  {
                     //TYPE is BULLET(or DEBRIS), so decrement life
                     tmpAfloat[LIFE]--;
                  }
                 //END edge collision
                  
/*************** START object<->object COLLISION DETECTION ******************/
                  for(int n = 0;n<numObjects;n++)
                  {
                     tmpBfloat = object[n];
                     //player SHIP can hit NME_BULLET, NME_SHIP and DEBRIS(gives life)
                     //NME_SHIP can hit player BULLET only
                     if((type==SHIP && (tmpBfloat[TYPE]==NME_SHIP || tmpBfloat[TYPE]>=NME_BULLET)) ||
                        (type==NME_SHIP && tmpBfloat[TYPE]==BULLET))
                     {
                        if((float)Line2D.ptSegDist(x,y,x-dx,y-dy,tmpBfloat[X],tmpBfloat[Y])
                           < radius+tmpBfloat[RADIUS])
                        {
                           if(SOUND_ON)
                           {
                              if(tmpAfloat[LAST_HIT]<=HIT_DURATION-2)
                              {
                                 if(tmpBfloat[TYPE]>NME_BULLET)
                                 mc[2].noteOn(89,50);
                                 else
                                 mc[1].noteOn(55, 127);
                              }
                           }
                           tmpAfloat[LIFE]-=tmpBfloat[DAMAGE];
                           tmpBfloat[LIFE]-=tmpAfloat[DAMAGE];
                           if(SHIELD) tmpAfloat[LAST_HIT]=HIT_DURATION;
                        }
                     }
                  }
   /*************** END object<->object COLLISION DETECTION ******************/
   
                  //copy object variables back
                  tmpAfloat[X]=x;
                  tmpAfloat[Y]=y;
                  tmpAfloat[DX]=dx;
                  tmpAfloat[DY]=dy;
                  tmpAfloat[ANGLE]=angle;
                  tmpAfloat[TARGET_X]=targetX;
                  tmpAfloat[TARGET_Y]=targetY;
                  
                  //if the object is dead, remove it
                  if(tmpAfloat[LIFE]<=0)
                  {
                     if(type==SHIP) playing = 0; //game over
                     if(type==NME_SHIP)
                     {
                        if(SOUND_ON) mc[3].noteOn(40,127);
                        for(int n = 0;n<EXPLOSION_SIZE;n++)
                        {
                           tmpBfloat = object[numObjects++];
                           tmpBfloat[X] = x;//tmpAfloat[X];
                           tmpBfloat[Y] = y;//tmpAfloat[Y];
                           tmpBfloat[DX] = (float)Math.random()*6f-3f;//(i*numObjects*66571)%6-3; //instead of Math.random()
                           tmpBfloat[DY] = (float)Math.random()*6f-3f;//(i*numObjects*66931)%6-3; //instead of Math.random()
                           tmpBfloat[TYPE] = DEBRIS;
                           tmpBfloat[LIFE] = DEBRIS_LIFE;
                           tmpBfloat[RADIUS] = DEBRIS_RADIUS;
                           tmpBfloat[DAMAGE] = DEBRIS_LIFE_GIVE;
                        }
                        score++;
                     }
                     
                     object[i--] = object[--numObjects];
                     object[numObjects] = tmpAfloat;
                  }
               }
/*************** END object update loop ******************/

/*************** START enemy spawning ********************/
               if((++runningTime)/600>=enemyCount) //new and superior spawn timing (num enemies on screen at once increases by 1 every 10 seconds)
               {
                  (tmpAfloat = object[numObjects++])
                            [TYPE] = NME_SHIP;
                  //tmpAfloat[DX] = 0; //not strictly necessary - removed (saving 5bytes each!)
                  //tmpAfloat[DY] = 0; //not strictly necessary - removed (saving 5bytes each!)
                  tmpAfloat[RADIUS] = NME_SHIP_DIAMETER/2;
                  tmpAfloat[LIFE] = NME_SHIP_LIFE;
                  //tmpAfloat[X_OFFSET] = NME_SHIP_X_OFFSET;
                  //tmpAfloat[Y_OFFSET] = NME_SHIP_Y_OFFSET;
                  tmpAfloat[FIRE_DELAY]= NME_SHIP_FIRE_DELAY;
                  tmpAfloat[DAMAGE] = NME_SHIP_COLLISION_DAMAGE;

                  int edge = numObjects%4;//instead of Math.random()
                  
                  //optimisation possible here i *think* (change edge to float to remove 2 autocasts)
                  if(edge%2==0) //edge is 0 or 2 (left or right)
                  {
                     tmpAfloat[X] = edge*(MAP_WIDTH/2); //won't get rounding errors cos the map width must be divisable by 2
                     tmpAfloat[Y] = MAP_HEIGHT*(float)Math.random();//(numObjects*66571)%MAP_HEIGHT; //instead of Math.random()
                     //tmpAfloat[ANGLE] = (Math.PI*1.5); //not strictly necessary
                  }
                  else //edge is 1 or 3 (top or bottom)
                  {
                     //edge--; //moved this down 2 lines (no saving though)
                     tmpAfloat[X] = MAP_WIDTH*(float)Math.random();//(numObjects*66931)%MAP_WIDTH;//instead of Math.random()
                     tmpAfloat[Y] = --edge*(MAP_HEIGHT/2); //won't get rounding errors cos the map width must be divisable by 2
                     //tmpAfloat[ANGLE] = 2*Math.PI; //not strictly necessary
                  }
                  //tmpAfloat[ANGLE]-=(Math.PI/2)*edge;//moved this from the above if's (saved 2bytes)
                  // now totally removed ^^ (46 bytes in total!)
                  tmpAfloat[TARGET_X] = tmpAfloat[X];
                  tmpAfloat[TARGET_Y] = tmpAfloat[Y];
               }
/*************** END enemy spawning ********************/
               
/* START Star Rendering to blur layer */
               
               if(EXPERIMENTAL)
               {
                  g = blurBuffer.createGraphics();
                  g.drawImage(blackImage,0,0,null);
               }
                              
               for(int i = MAX_NUM_OBJECTS;i<MAX_NUM_OBJECTS+NUM_STARS;i++)
               {
                  tmpAfloat = object[i];
                  float color = (float)tmpAfloat[COLOR];
                  g.setColor(new Color(color,color,color,1.0f));
                  if(PREDEFINED_SCREENSIZE)
                  {
                     g.fillRect(((int)((MAP_WIDTH+tx)*color+tmpAfloat[X]))%(SCREEN_WIDTH),
                                ((int)((MAP_HEIGHT+ty)*color+tmpAfloat[Y]))%(SCREEN_HEIGHT),
                                STAR_WIDTH,STAR_HEIGHT);
                  }
                  else
                  {
                     g.fillRect(((int)((MAP_WIDTH+tx)*color+tmpAfloat[X]))%(DISPLAY_WIDTH),
                                ((int)((MAP_HEIGHT+ty)*color+tmpAfloat[Y]))%(DISPLAY_HEIGHT),
                                STAR_WIDTH,STAR_HEIGHT);
                  }
               }

/* END Star Rendering to blur layer */

/* START other rendering */
               
               //this loop does a ping-pong over the objects.
               //on the way forward it uses the blurGraphics object, and renders all blurred effects.
               //it then renders the blur image to the bufferStrategy, and...
               //on the way back it uses the BufferStrategy Graphics, and renders all non-blurred stuff. 
               for(int i = 0,di=1;i>=0;i+=di)
               {
                  if(i>=numObjects)
                  {
                     di=-1;
                     //i--;
                     if(EXPERIMENTAL)
                     {
                        if(CUSTOM_CURSOR && BLUR_CURSOR) g.drawImage(cursorImage,ctrls[MOUSE_X]-16,ctrls[MOUSE_Y]-16,null);//<<to blur the mouse
                        g = (Graphics2D)(bs.getDrawGraphics());
                        g.drawImage(blurBuffer,0,0,null);
                     }
                     continue; //i-- changed to continue (clearer meaning; no byte saving though :()
                     
                  }
                  tmpAfloat = object[i];
                  int x = (int)(tx+tmpAfloat[X]);
                  int y = (int)(ty+tmpAfloat[Y]);
                  int type = (int)tmpAfloat[TYPE];
                  
                  if((di==1 && type>NME_SHIP) || (di==-1 && type<=NME_SHIP)) //draw DEBRIS & BULLETS on the forward loop, and the the Ships on the backward loop
                  {
                     if(type==DEBRIS)
                     {
                        g.setColor(new Color(1.0f,0.0f,0.0f,1.0f));//color red
                        g.fillRect(x,y,DEBRIS_WIDTH,DEBRIS_HEIGHT);
                     }
                     else
                     {
                        if(SHIELD)
                        {
                           if(tmpAfloat[LAST_HIT] >0)
                           {
                              //unoptimized version
                              //float darkness = (float)(tmpAfloat[LAST_HIT]/HIT_DURATION);
                              //float lifeness = (float)(Math.min(tmpAfloat[LIFE],SHIP_LIFE)/SHIP_LIFE); //this needs optimising
                              //g.setColor(new Color((1.0f-lifeness)*darkness,0f,lifeness*darkness,1.0f));
                              //g.fillOval(x-SHIP_DIAMETER,y-SHIP_DIAMETER,SHIP_DIAMETER*2,SHIP_DIAMETER*2); //this will have a performance hit, but its a single oval, so it wont matter 2much
                              
                              //new version, 2bytes smaller
                              float darkness = tmpAfloat[LAST_HIT]/HIT_DURATION;
                              float lifedarkproduct = Math.min(tmpAfloat[LIFE],SHIP_LIFE)/SHIP_LIFE*darkness; //this needs optimising
                              g.setColor(new Color(darkness-lifedarkproduct,0f,lifedarkproduct,1.0f));//Color(float,float,float,float)
                              g.fillOval(x-SHIP_DIAMETER,y-SHIP_DIAMETER,SHIP_DIAMETER*2,SHIP_DIAMETER*2); //this will have a performance hit, but its a single oval, so it wont matter 2much                              
                           }
                        }
                        //refactored NUM_ROTATIONS/(Math.PI*2), 2bytes saving
                        g.drawImage(objectImages[type][(int)(NUM_ROTATIONS/(float)(Math.PI*2d) * tmpAfloat[ANGLE])],x-IMAGE_WIDTH/2,y-IMAGE_HEIGHT/2,null);
                     }
                  }
               }
            }
            
            final int RADAR_WIDTH = SCREEN_WIDTH/8 -1; //so it is 127 not 128 (byte not short)
            final int RADAR_HEIGHT = SCREEN_HEIGHT/8;
            
            final int RADAR_LEFT = PREDEFINED_SCREENSIZE ? SCREEN_WIDTH-RADAR_WIDTH-10 : 10;
            final int RADAR_TOP = PREDEFINED_SCREENSIZE ? SCREEN_HEIGHT-RADAR_HEIGHT-10 : 10;
            final int RADAR_RIGHT = PREDEFINED_SCREENSIZE ? SCREEN_WIDTH-10 : RADAR_LEFT+RADAR_WIDTH;
            final int RADAR_BOTTOM = PREDEFINED_SCREENSIZE ? SCREEN_HEIGHT-10 : RADAR_TOP+RADAR_HEIGHT;
            
            final int RADAR_BLIP_WIDTH = 2;
            final int RADAR_BLIP_HEIGHT = 2;
            
            g.setColor(white);//new Color(1.0f,1.0f,1.0f,1.0f));//white

            if(PREDEFINED_SCREENSIZE) g.drawString(String.valueOf(score),RADAR_LEFT,RADAR_TOP-10);//draw the score
            else g.drawString(String.valueOf(score),RADAR_LEFT,RADAR_BOTTOM+20);//draw the score
         
            if(RADAR)
            {
               //g.drawRect(RADAR_TOP,RADAR_LEFT,RADAR_WIDTH,RADAR_HEIGHT); 
               
               //drawRect is broken when experimental acceleration is used
               //so i have to do it with 4 'lines' (actually filled rectangles) :(
               
               g.fillRect(RADAR_LEFT,RADAR_TOP,RADAR_WIDTH,1);//TOP
               g.fillRect(RADAR_RIGHT,RADAR_TOP,1,RADAR_HEIGHT);//RIGHT
               g.fillRect(RADAR_LEFT+1,RADAR_BOTTOM,RADAR_WIDTH,1);//BOTTOM
               g.fillRect(RADAR_LEFT,RADAR_TOP+1,1,RADAR_HEIGHT);//LEFT
               
               for(int i = 0;i < numObjects;i++)
               {
                  float type;
                  if((type = (tmpAfloat = object[i])[TYPE])<=NME_SHIP)
                  {
                     if(type==SHIP) g.setColor(blue);//new Color(0.0f,0.0f,1.0f,1.0f));//color blue
                     else g.setColor(new Color(0.0f,1.0f,0.0f,1.0f));//color green
                     g.fillRect(RADAR_LEFT + (int)(tmpAfloat[X]*((float)(RADAR_WIDTH)/MAP_WIDTH)),
                                RADAR_TOP + (int)(tmpAfloat[Y]*((float)(RADAR_HEIGHT)/MAP_HEIGHT)),
                                RADAR_BLIP_WIDTH,RADAR_BLIP_HEIGHT);
                  }
               }
            }
            
            if(DEBUG)
            {
               g.setColor(white);//new Color(1.0f,1.0f,1.0f,1.0f));//white
               g.drawString(fps,50,SCREEN_HEIGHT-50);
               fpsCount++;
               if(System.currentTimeMillis()-prevTime>1000)
               {
                  prevTime+=1000;
                  fps=String.valueOf(fpsCount);
                  fpsCount=0;
               }
            }
            //Thread.sleep(10);
            bs.show();
            final int NANO_FRAME_LENGTH = 1000000000/60;
            final int MILLI_FRAME_LENGTH = 1000/60;
            if(PLATFORM!=WINDOWS || !FULLSCREEN) //only have a timer delay when vsync is not available (thats eveything but fullscreen windows ;P)
            {
               if(USE_MILLITIME)
               {
                  while((System.currentTimeMillis()-startTime)/MILLI_FRAME_LENGTH <frameCount);
               }
               else
               {
                  while((System.nanoTime()-startTime)/NANO_FRAME_LENGTH <frameCount);
               }
            }
         }//END game running block
/****************************** END Game Loop *******************************/
      }
      catch(Exception e)
      {
         if(DEBUG) e.printStackTrace();
      }
      System.exit(0);
      //dispose();
      
      
      
   }
   
   //DEBUG only, remove for final version
   //public static void main(String [] args) {}
}