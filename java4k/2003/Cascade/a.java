import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Title:        Cascade
 * Description:  Cascade Game
 * Copyright:    Copyright (c) 2002
 * Company:      n/a
 * @author Mike
 * @version 1.1
 */

public class a extends Canvas implements MouseListener {
  private JLabel scoreL = new JLabel("Score: ");

  private BufferedImage bi;
  private Graphics2D big;
  private Rectangle area;


  private final static byte EMPTY = 3;
  private final static byte WHITE = 1;
  private final static byte GREY = 2;
  private final static byte BLACK = 0;

  private final static byte BALL_SIZE = 20;
  private final static byte X = 25;
  private final static byte Y = 10;

  private byte[][] balls = new byte[X][Y];
  private int score = 0;

  private JFrame myFrame = new JFrame("Cascade");

  public static void main(String argv[]) {
    new a();
  }

  private a() {
    addMouseListener( this );
    newGame();

    myFrame.setDefaultCloseOperation(myFrame.EXIT_ON_CLOSE);

    myFrame.setIconImage(Toolkit.getDefaultToolkit().createImage(a.class.getResource("a")));

    myFrame.getContentPane().setLayout( new BorderLayout() );
    setBackground( scoreL.getBackground() );
    myFrame.getContentPane().add( BorderLayout.NORTH, scoreL );
    myFrame.getContentPane().add( BorderLayout.CENTER, this );
    setSize( new Dimension(X * BALL_SIZE, Y*BALL_SIZE) );

    myFrame.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    myFrame.setLocation((screenSize.width - myFrame.getWidth()) / 2,
      (screenSize.height - myFrame.getHeight()) / 2);
    myFrame.show();
  }

  private void newGame() {
    java.util.Random rnd = new java.util.Random();

    setScore(0);
    for(int x = 0; x < X; x++){
      for(int y = 0; y < Y; y++){
        balls[x][y] = (byte)(rnd.nextInt(3));
      }
    }
    repaint();
  }

  public void paint(Graphics g){
    update(g);
  }

  public void update(Graphics g){
    Graphics2D g2 = (Graphics2D)g;
    if( bi == null) {
      Dimension dim = getSize();
      int w = dim.width;
      int h = dim.height;
      area = new Rectangle(dim);
      bi = (BufferedImage)createImage(w, h);
      big = bi.createGraphics();
    }

    big.setColor(this.getBackground());
    big.clearRect(0, 0, area.width, area.height);

    for(int x = 0; x < X; x++){
      for(int y = 0; y < Y; y++){
        big.setColor( this.getBackground() );
        switch(balls[x][y]) {
          case WHITE: big.setColor( Color.white );  break;
          case GREY: big.setColor( Color.gray );  break;
          case BLACK:big.setColor( Color.black ); break;
        }
        big.fillArc(x*BALL_SIZE, y*BALL_SIZE, BALL_SIZE-1, BALL_SIZE-1, 0, 360);
      }
    }
    if( hasWon() ) {
      big.setColor( getBackground().darker() );
      big.setFont(  new Font("Monospaced", Font.BOLD, 20) );
      big.drawString("Click to start a new game", 30, 30);
    }
    g2.drawImage(bi, 0, 0, this);
  }

  private void setScore(int n) {
    scoreL.setText("Score: "  + (score = n));
  }

  private boolean canRemove(int x, int y) {
    return ((x < 0) || (y < 0)) || (( x < X-1) ? balls[x][y] == balls[x+1][y] : false) ||
          (( x > 0) ? balls[x][y] == balls[x-1][y] : false) ||
          (( y < Y-1) ? balls[x][y] == balls[x][y+1] : false) ||
          (( y > 0) ? balls[x][y] == balls[x][y-1] : false);
  }

  private int internal_remove(int x, int y, int colour, int noRemoved) {
    if(!((x < 0) || (x > X-1) || (y < 0) || (y > Y-1)) && balls[x][y] == colour ) {
      balls[x][y] = EMPTY;
      noRemoved++;
      noRemoved += internal_remove(x-1, y, colour, 0);
      noRemoved += internal_remove(x+1, y, colour, 0);
      noRemoved += internal_remove(x, y-1, colour, 0);
      noRemoved += internal_remove(x, y+1, colour, 0);
    }
    return noRemoved;
  }


  private boolean hasWon() {
    for(int x = 0; x < X; x++){
      for(int y = 0; y < Y; y++){
        if(balls[x][y] != EMPTY) {
           if(canRemove(x, y)) return false;
        }
      }
    }
    return true;
  }

  public synchronized void mouseReleased(MouseEvent e) {
    if( hasWon() ) { newGame(); return; }

    int tx = e.getX() / (getWidth() / X), ty = e.getY() / (getHeight() / Y);
    if( canRemove(tx, ty) && balls[tx][ty] != EMPTY ) {
    int noRemoved = internal_remove(tx, ty, balls[tx][ty], 0);

    for(int x = 0; x<X; x++) {
      for(int i = 0; i<Y; i++){
        for(int y = Y-1; y > 0; y--) {
          if(balls[x][y] == EMPTY) {
            balls[x][y] = balls[x][y-1];
            balls[x][y-1] = EMPTY;
          }
        }
      }
    }
    for(int i = 0; i<X-1; i++) {
      for(int x = 0; x<X-1; x++) {
        if(balls[x][Y-1] == EMPTY) {
          for(int y = 0; y<Y;y++) {
            balls[x][y] = balls[x+1][y];
            balls[x+1][y] = EMPTY;
            }
          }
        }
      }
      setScore( score + (int)((noRemoved * noRemoved)/1.5) );
      repaint();
      if(hasWon()) {
        String xtr = "";
        if(balls[0][Y-1] == EMPTY) {
          setScore( score + 500 );
          xtr = "You have cleared the board!\n";
        }
        String name = JOptionPane.showInputDialog(null, "Congratulations!.\n" + xtr
          + "Do you want to upload your score of " + score + "?\nEnter your name below:", "Winner!",
          JOptionPane.QUESTION_MESSAGE);
        if( name != null ) {
          name = java.net.URLEncoder.encode(name.replace('<', '_').replace('>', '_').replace('\'', '_').replace('\"', '_').replace('&', '_'));

          try {
            JDialog d = new JDialog(myFrame, "Scoreboard", true);
            d.getContentPane().add(new JScrollPane( new JEditorPane("http://forum.java.sun.com" ) ),
              BorderLayout.CENTER);
            d.setSize(300, 300);
            d.show();
          } catch( java.io.IOException ioe) {
            JOptionPane.showMessageDialog(null, "No Net Connection Detected!");
          }
        }
      }
    }
  }
  public void mouseClicked(MouseEvent e) {
  }
  public void mousePressed(MouseEvent e) {
  }
  public void mouseEntered(MouseEvent e) {
  }
  public void mouseExited(MouseEvent e) {
  }
}