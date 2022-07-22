import javax.imageio.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;
//will making the class private decrease size?
public class A extends Frame implements KeyListener
{
   static final int STARTING = -2;
   static final int RUNNING = -1;

   static final int PERIOD = 20;
   static final int HTZ = 1000/PERIOD; //not used

   static final int CAR_WIDTH = 11;
   static final int CAR_WIDTH_2 = CAR_WIDTH/2;
   static final int CAR_HEIGHT = 20;
   static final int CAR_HEIGHT_2 = CAR_HEIGHT/2;
   static final int CAR_RADIUS = (CAR_HEIGHT+2)/2;
   static final int CAR_RADIUS2 = CAR_RADIUS*CAR_RADIUS;

   static final int CAR_SIZE = 32; //CAR_SIZE must be greater than Math.sqrt(CAR_WIDTH*CAR_WIDTH+CAR_HEIGHT*CAR_HEIGHT);
   // ^^ i've rounded it to the nearest power of 2
   static final int CAR_ROTATIONS = 360;

   static final int NUM_PLAYERS = 8;

   static final int HUMAN_PLAYER = NUM_PLAYERS-1;

   static final int START_LAPS = 5;

   static final int AI_FORWARD_SEARCH_LIMIT = 2; //not used anymore

   static final double TURN_RATE = Math.PI/32;

   static final int RIGHT = 0;
   static final int LEFT = 1;

   static final int SEGMENT_COUNT = 45; //hard coded track length

   static final int TRACK_EDGE_FADE = 20;//20.0f;

   static final int WIDTH = 640, HEIGHT = 480;

   static final int X = 0;
   static final int Y = 1;

   //this method is inlined by jax, so has no overhead anyway
   public static double dot(double ax, double ay, double bx, double by)
   {
      return ax*bx+ay*by;
   }

   byte [] controls = new byte[256];//[] for storing the states of the keys

   {
      try
      {
         GraphicsConfiguration gc = getGraphicsConfiguration();

         BufferedImage carImage = ImageIO.read(getClass().getResource("c")); //load the car Image
         BufferedImage [] carFrames = new BufferedImage[CAR_ROTATIONS];
         for(int i = 0;i < CAR_ROTATIONS;i++)
         {
            carFrames[i] = gc.createCompatibleImage(CAR_SIZE,CAR_SIZE,Transparency.BITMASK);
            Graphics2D cfg2d = carFrames[i].createGraphics();
            //cfg2d.setComposite(AlphaComposite.Src); //<<not needed, images default to entirely transparent
            //cfg2d means carFramesGraphics2D
            cfg2d.rotate(((Math.PI*2)*i)/CAR_ROTATIONS, CAR_SIZE/2,CAR_SIZE/2);
            cfg2d.drawImage(carImage,(CAR_SIZE-CAR_WIDTH)/2,(CAR_SIZE-CAR_HEIGHT)/2,null);
            // I can save a few bytes here (eliminate the caching of *carImage*)
            // though it may slow loading down considerably (depending on whether ImageIO caches files)
         }

         setUndecorated(true);
         GraphicsDevice gd = gc.getDevice();
         gd.setFullScreenWindow(this);
         gd.setDisplayMode(new DisplayMode(WIDTH,HEIGHT,32,DisplayMode.REFRESH_RATE_UNKNOWN));
         createBufferStrategy(2);

         //hmm, will changing these from doubles to floats decrease size..... perhaps (though it may force me to add some castings)
         //player stuff (this will all need re-initing each game)
         double [] carX = new double[NUM_PLAYERS];
         double [] carY = new double[NUM_PLAYERS];
         double [] carVel = new double[NUM_PLAYERS];
         double [] carAngle = new double[NUM_PLAYERS];

         int [] nextSector = new int[NUM_PLAYERS];
         int [] prevSector = new int[NUM_PLAYERS];
         int [] laps = new int[NUM_PLAYERS];


         //track stuff
         int [][] trackX = new int[2][SEGMENT_COUNT+1];
         int [][] trackY = new int[2][SEGMENT_COUNT+1];
         double [][] trackAng = new double[2][SEGMENT_COUNT+1];
         int [] aiX = new int[SEGMENT_COUNT+1];
         int [] aiY = new int[SEGMENT_COUNT+1];

         //variables for car<->car collisions
         //defined up here so they arn't recreated every game loop
         double [][][] carCollision = new double[2][4][2]; //[carIndex][cornerIndex][X or Y]
         final double [][] cornerMulti = {{1.0,-1.0},{1.0,1.0},{-1.0,1.0},{-1.0,-1.0}};


         //track creation
         DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("t"));
         //double minAngle = dis.readFloat();
         //double angleRange = dis.readFloat();
         int [] prevX = {trackX[RIGHT][0] = dis.readShort(),trackX[LEFT][0] = dis.readShort()};
         int [] prevY = {trackY[RIGHT][0] = dis.readShort(),trackY[LEFT][0] = dis.readShort()};

         for(int i = 1;i< SEGMENT_COUNT+1;i++)
         {
            double [] len = new double [2];
            for(int n = 0; n < 2;n++)
            {
               int secDx = dis.readByte(), secDy = dis.readByte();
               prevX[n] = (trackX[n][i] = (prevX[n] + secDx));
               prevY[n] = (trackY[n][i] = (prevY[n] + secDy));
               len[n] = Point2D.distance(0,0,secDx,secDy);
               trackAng[n][i-1] = Math.acos(dot(0,1,secDx,-secDy)/len[n]);
               if(secDx<0) trackAng[n][i-1]=Math.PI*2-trackAng[n][i-1];
            }
            aiX[i%SEGMENT_COUNT] = (int)((prevX[0]*len[1] + prevX[1]*len[0])/(len[0]+len[1]));
            aiY[i%SEGMENT_COUNT] = (int)((prevY[0]*len[1] + prevY[1]*len[0])/(len[0]+len[1]));
         }

         Polygon leftPolygon = new Polygon(trackX[LEFT],trackY[LEFT],SEGMENT_COUNT);
         Polygon rightPolygon = new Polygon(trackX[RIGHT],trackY[RIGHT],SEGMENT_COUNT);
         //track creation end

         //background creation
         BufferedImage background = gc.createCompatibleImage(WIDTH,HEIGHT, Transparency.OPAQUE); //is this faster??
         Graphics2D bgg = background.createGraphics();

         for(int x = 0; x<WIDTH;x++)
         {
            for(int y = 0;y<HEIGHT;y++)
            {
               int color = (int)(Math.random()*4)*12; //random.nextInt(4)
               if(leftPolygon.contains(x,y) & !rightPolygon.contains(x,y))
               {
                  color|=(color<<8 | color<<16);
               }
               else
               {
                  color = ((color/12+1)*30)<<8;
               }
               background.setRGB(x,y,color);// |0xFF000000); //now im using createCompatibleImage(width,height,OPAQUE) I don't think this is needed
            }
         }

         bgg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));
         //bgg.setStroke(new BasicStroke(6,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND));
         bgg.drawLine(trackX[RIGHT][1],trackY[RIGHT][1],trackX[LEFT][1],trackY[LEFT][1]);
         bgg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.04f));
         for(int i = TRACK_EDGE_FADE; i>0;i-=2)
         {
            bgg.setStroke(new BasicStroke(i, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            bgg.drawPolygon(rightPolygon);
            bgg.drawPolygon(leftPolygon);
         }
         //background creation end


         //tmp variables, used as temp storage in the game loop
         int state = STARTING;
         //state has several meanings, if it is STARTING, then the game hasn't begun yet
         //if it is RUNNING, then the game is currently playing
         //if it is neither of the previous 2, then the race is over, and the winner is the value of state

         addKeyListener(this); //having this as the very last thing to do in init makes sense (until the game loop starts, I don't care about key input)

         //game loop
         while(controls[KeyEvent.VK_ESCAPE]==0)
         {
            Graphics bg = getBufferStrategy().getDrawGraphics();
            bg.drawImage(background,0,0,null);
            bg.setColor(Color.white);
            if(state!=RUNNING)
            {
               bg.drawString("Press Space",320,220);
               if(state!=STARTING)
               {
                  bg.drawString(state==HUMAN_PLAYER?"You Won!":"You Lost",325,200);
               }
               if(controls[KeyEvent.VK_SPACE]==1)
               {
                  for(int i = 0;i < NUM_PLAYERS;i++)
                  {
                     //would Arrays.fillXXXX decrease size here??
                     carX[i] = trackX[RIGHT][1]-CAR_HEIGHT_2-(CAR_HEIGHT+4)*(i/2);
                     carY[i] = (trackY[RIGHT][1]+ trackY[LEFT][1])/2-CAR_WIDTH+(i%2)*(CAR_WIDTH*2);
                     carVel[i]=0; //size optimisation possible here (combine with prevSector[i] = 0)
                     carAngle[i] = Math.PI/2;

                     prevSector[i] = 0;
                     nextSector[i] = 1;
                     laps[i] = START_LAPS;
                  }
                  state=RUNNING;
               }
            }
            else
            {
               for(int i = 0;i<NUM_PLAYERS;i++)
               {
                  double leftRight, upDown=-1.0;
                  //OPTIMISATION
                  //leftRight is infact rightLeft, because it reqires 1 less '-'
                  // saving, 1byte

                  //optimisation, moved default ai assumption of accelerating, from the ai block, onto the declaration line
                  //saving ZERO


                  // each cars attributes are moved from the array
                  // into local variables (decreases code size, and speeds up access)
                  double x = carX[i];
                  double y = carY[i];
                  double vel = carVel[i];
                  double angle = carAngle[i];

                  //the dx/dy for this cycle
                  double dx = Math.sin(angle)*vel;
                  double dy = -Math.cos(angle)*vel;
                  //the x/y that the car will occupy next cycle, IF no collisions occur
                  double newX = x+dx;
                  double newY = y+dy;

                  // current next/prev sectors, these are the values that are modified in the cycle
                  // if no collision occurs, the new updated sector next/prev will be copied back into the array
                  int newPrev = prevSector[i];
                  int newNext = nextSector[i];

                  //lap changes arn't dealt with until the enf of the cycle (incase a collision occurs)
                  int lapChange=0;

                  // if the car passes into the 'next' sector, then this flag will be set to false
                  // and the car will not be checked for passing into the previous sector
                  boolean checkPrevious = true;
                  //if a collision occurs, this flag will be set to false
                  boolean updatePosition = true;


                  //optimisation
                  //1) eliminated 2 variable
                  //2) which allowed assignemnt of variables to be moved onto the declartion line
                  // ZERO SAVING!!!! jax must have already been performing that optimisation

                  if(i==HUMAN_PLAYER) //human control stuff
                  {
                     leftRight = controls[37]-controls[39];
                     upDown = (controls[40]-controls[38])*0.6;//handicap the human players acceleration
                  }
                  else //ai code
                  {
                     //optimisations,
                     //1) compound if statement into assignment, saves 2 bytes
                     //2) removed tmp varible as it is only used once, saves 4 bytes
                     leftRight= (Math.acos(dot(dx,dy,aiX[newNext]-newX,aiX[newNext]-newX)/(vel*Point2D.distance(newX,newY,aiX[newNext],aiY[newNext])))<TURN_RATE
                                 || laps[i]>=START_LAPS)? 0:
                                 Line2D.relativeCCW(newX, newY, dx*10000, dy*10000, aiX[newNext],aiY[newNext]);
                      //upDown=0;
                      //leftRight=0;
                  }

                  vel-=0.15*upDown; //velocity change
                  angle+=leftRight*(vel<0?TURN_RATE:-TURN_RATE); //turning
                  vel*= (leftRight!=0?0.97:0.995); //this is the simple friction modelling, don't turn, you slow a little, turn, and you slow alot.

                  //check for entry into the next sector
                  while(Line2D.linesIntersect(x,y,newX,newY,
                        trackX[RIGHT][newNext], trackY[RIGHT][newNext],
                        trackX[LEFT][newNext], trackY[LEFT][newNext]))
                  {
                     // if you do enter the next sector, this must loop, until you stop going forward in sectors
                     // if this loop wasn't here, a fast traveling car could potencially jump out of its current sector
                     if(newNext==1) lapChange--;//if the car passes the start/finish line, set the lapChange flag
                     newPrev = newNext; //newPrevious sector is now the old Next sector
                     newNext = (newNext+1)%SEGMENT_COUNT; //and the new next sector is the old next sector +1 (modded for track looping)
                     checkPrevious=false; //the car is going forward, so don't check previous sector gate line
                  }

                  if(checkPrevious)
                  {
                     //does same as above, but for previous sector
                     while(Line2D.linesIntersect(x,y,newX,newY,
                           trackX[RIGHT][newPrev], trackY[RIGHT][newPrev],
                           trackX[LEFT][newPrev], trackY[LEFT][newPrev]))
                     {
                        if(newPrev==1) lapChange++; //if the car is going backward, increment the lapCounter
                        newNext = newPrev;
                        newPrev = (newPrev + (SEGMENT_COUNT-1))%SEGMENT_COUNT;//backward looping by using mod as well :) didn't work this out until now!!
                     }
                  }

                  out:
                  for(int n = 0;n < NUM_PLAYERS;n++) // car<->car collision loop
                  {
                     //collision is first checked against a collision circle
                     //Point2D.distance() is used instead of Point2D.distanceSq()
                     // because it is used elseware, hence saving space.(less referenced methods==smaller class size)
                     if(n!=i && Point2D.distance(newX,newY, carX[n], carY[n]) < CAR_RADIUS*2)
                     {

                        double [] carsSin = {Math.sin(angle),Math.sin(carAngle[n])}; //size optimisation
                        double [] carsCos = {Math.cos(angle),Math.cos(carAngle[n])};
                        double [] carsXY = {newX,carX[n],newY,carY[n]}; //optimisation , saves 3bytes

                        double [][] tmpCar;//used for size optimisation (it should also be a little faster!)

                        //these loops calculate the 4 corners of the 2 cars involved in the collision
                        for(int carIndex = 0; carIndex < 2;carIndex++)
                        {
                           for(int cornerIndex = 0; cornerIndex < 4;cornerIndex++)
                           {
                              carCollision[carIndex][cornerIndex][X] = carsXY[carIndex] + carsSin[carIndex]*CAR_HEIGHT_2*cornerMulti[cornerIndex][0] - carsCos[carIndex]*CAR_WIDTH_2*cornerMulti[cornerIndex][1];
                              carCollision[carIndex][cornerIndex][Y] = carsXY[carIndex+2] - carsCos[carIndex]*CAR_HEIGHT_2*cornerMulti[cornerIndex][0] - carsSin[carIndex]*CAR_WIDTH_2*cornerMulti[cornerIndex][1];
                           }
                        }


                        //and these loops check for collisions between the corners, by using line Intersection
                        for(int cornerIndex = 0;cornerIndex<4;cornerIndex++)
                        {
                           for(int cornerIndex2 = 0;cornerIndex2<4;cornerIndex2++)
                           {
   //                           bg.setColor(Color.blue);
                              //bg.drawLine((int)(tmpCar=carCollision[0])[cornerIndex][X],(int)tmpCar[cornerIndex][Y],(int)tmpCar[(cornerIndex+1)%4][X],(int)tmpCar[(cornerIndex+1)%4][Y]);
                              //bg.setColor(Color.red);
                              //bg.drawLine((int)(tmpCar=carCollision[1])[cornerIndex][X],(int)tmpCar[cornerIndex][Y],(int)tmpCar[(cornerIndex+1)%4][X],(int)tmpCar[(cornerIndex+1)%4][Y]);
                              if(Line2D.linesIntersect((tmpCar=carCollision[0])[cornerIndex][X],        tmpCar[cornerIndex][Y],
                                                       tmpCar[(cornerIndex+1)%4][X],                      tmpCar[(cornerIndex+1)%4][Y],
                                                       (tmpCar=carCollision[1])[cornerIndex2][X],         tmpCar[cornerIndex2][Y],
                                                       tmpCar[(cornerIndex2+1)%4][X],                     tmpCar[(cornerIndex2+1)%4][Y]))
                              {
                                 //OPTIMISATION, introduced a tmpvar 'tmpCar' which is used to hold a reference to the [0] and [1] car arrays
                                 // saves 4 bytes

                                 //only consider collisions if it is the front of the car hitting the other car
                                 // OR if the car is travelling backwards
                                 if(cornerIndex==0 || vel<0)
                                 {
                                    //angle update is cancelled (if you hit another car, it prevents you from turning in that cycle)
                                    angle = carAngle[i];

                                    // if the other car is hit on the back or side (but NOT the front)
                                    // a small ammount of momentum is transfered
                                    if(cornerIndex2!=0)carVel[n]+=vel*0.1;
                                    vel = carVel[n]*0.7; //this car is slowed by 30%
                                    updatePosition = false; //and its position update is cancelled for this cycle

                                    break out;//once a car has collided, it cannot hit anything else
                                 }
                              }
                           }
                        }
                     }
                  }


                  //track edge collision detection
                  for(int n = 0;n< 2;n++)
                  {
                     if(Line2D.linesIntersect(x, y, newX,newY, trackX[n][newPrev], trackY[n][newPrev],
                                                               trackX[n][newNext], trackY[n][newNext]))
                     {
                        double trackAngle = trackAng[n][newPrev];
                        if(n==RIGHT)
                        {
                           if(trackAngle>angle)
                           {
                              angle+=Math.PI*2;
                           }
                        }
                        else //n==LEFT
                        {
                           if(trackAngle<angle)
                           {
                              trackAngle+=Math.PI*2;
                           }
                        }
                        //OPTIMISATION,
                        // eliminated 'double angleChange= trackAngle-angle'
                        // saving, 1 byte
                        double absAngleChange = Math.abs(trackAngle-angle);
                        if(absAngleChange>Math.PI-Math.PI/6) absAngleChange = Math.abs(Math.PI-absAngleChange);
                        if(absAngleChange<Math.PI/6)
                        {
                           //the resultant angle, is the cars velocity vector mirrored in the track edges vector
                           angle = trackAngle+trackAngle-angle;//*(Math.abs(vel)>6?1.0:Math.abs(vel)/6);

                           //there is a bug here, it is theoretically possible, to bounce off a wall
                           // and have the cars angle corrected, and placed inside the bounds of another car
                           // this is highly unlikely, (ive never had it happen) but it is possible
                           // the solution would require quite abit of code change

                           //velocity is reduced proportionally to the collision angle
                           vel*=(Math.PI/6 -absAngleChange)/(Math.PI/6);
                        }
                        else
                        {
                           vel = 0;//if the collision angle is > Math.PI/6, then the car will come to a stop
                        }
                        updatePosition = false;//if the car hits the side of the track, don't update its position
                        break;
                     }
                  }
                  if(updatePosition)
                  {
                     if((laps[i]+=lapChange)==0)
                     {
                        state=i;
                     }
                     nextSector[i] = newNext;
                     prevSector[i] = newPrev;
                     carX[i]=x=newX;
                     carY[i]=y=newY;
                  }

                  carVel[i] = vel;
                  carAngle[i] = (angle+(Math.PI*2))%(Math.PI*2);

                  bg.drawImage(carFrames[(int)((carAngle[i]/(Math.PI*2))*CAR_ROTATIONS)],(int)x-CAR_SIZE/2,(int)y-CAR_SIZE/2,null);

               }
               bg.drawString(Integer.toString(laps[HUMAN_PLAYER]),420,150);//draws the number of player laps remaining
            }
            getBufferStrategy().show();//causes the BufferStrategy to page flip
            synchronized(this)
            {
               wait(PERIOD);
            }
         }
      }
      catch(Exception e)
      {
         //System.out.println(e);
         e.printStackTrace();
      }
      dispose();
   }

   //(byte casting doesn't work [bytes are signed in java :S]
   // &0xFF will work, though its an extra 4bytes rather than 2 :S
   public void keyPressed(KeyEvent ke)
   {
      controls[ke.getKeyCode()&0xFF] = 1;
   }

   public void keyReleased(KeyEvent ke)
   {
      controls[ke.getKeyCode()&0xFF] = 0;
   }

   public void keyTyped(KeyEvent ke){}

   static
   {
      new A();
   }

/*   public static void main(String [] args)
   {
      new A();
   }*/
}