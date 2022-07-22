
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.awt.geom.*;
/**
 * some kind of space-invaders. i've done this very quickly, so it's not really
 * perfect.
 * don't blame me for the fact that it's not object-oriented. but it's just not
 * possible in 4kb ;-)
 *
 * @author  Thomas Scheuchzer thomas.scheuchzer@gmx.net http://tigarr.ch
 */
public class SI extends Applet implements Runnable, KeyListener, MouseListener {
    int h=400, w=500;
    int x, y, l = 1, r, lx = -15, ly, slx, sly, po, m;
    boolean bll, ml, mr, sshot, shot, win, st, ov,go;
    Random ra = new Random();
    Thread a,t;
    BufferedImage os = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    BufferedImage bgr = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    boolean[][] s = new boolean[8][4];
    Polygon p;

    public void init() {
        ll();
        t = new Thread( new Runnable() {
            public void run() {
                try{
                    while(true){
                        Thread.sleep(500- l*30);
                        if( ! st ) {
                            if( bll ) {
                                x -= 5;
                            } else {
                                x += 5;
                            }
                            if( x > 150 || x <= 10 ) {
                                bll = !bll;
                                y += 10;
                                if(y >380 ) ov = true;
                            }
                        }
                    }
                }catch( InterruptedException e){}
            }
        });
        addKeyListener( this );
        addMouseListener(this);
        setSize( w, h );
        a = new Thread( this );
    }
    void ll() {
        x=20;
        y=100;
        bll = false;
        st = true;
        ov = false;
        slx = -15;
        for( int i=0; i < s.length; i++ ){
            for( int j=0; j < s[0].length; j++ ) {
                s[i][j] = true;
            }
        }
        Graphics2D bg = (Graphics2D)bgr.getGraphics();
        bg.setColor( Color.black );
        bg.fillRect( 0, 0, w, h );
        Random r = new Random();
        bg.setColor( Color.white );
        for( int i = 0; i < 100; i++ ) {
            bg.fillRect( r.nextInt(w), r.nextInt(h), 1, 1 );
        }
        bg.setColor( Color.lightGray );
        p = new Polygon( new int[]{0,30, 30, 0}, new int[]{0,0, 15, 15}, 4 );
        p.translate(250,375);
    }
    
    public void run() {
        while( true ) {
            try{
                Thread.sleep(30);
            if(!go) continue;
                m+=30;
                repaint();
                if( win ) {
                    Thread.sleep(3000);
                    l++;
                    win = false;
                    st = true;
                    ll();
                } else if( st ) {
                    Thread.sleep(3000);
                    st = false;                   
                } else if( ov ) {
                    Thread.sleep(5000);
                    st = true;
                    win = false;
                    ov = false;
                    l = 1;
                    ll();
                }
                if( m > 3000-l*30) {
                    m=0;
                    r = ra.nextInt(256);
                    while(true) {
                        int a = ra.nextInt(s.length);
                        int b = ra.nextInt(s[0].length);
                        if( s[a][b] ){
                            slx = a*40+x+10;
                            sly = b*30+y+20;
                            sshot= true;
                            break;
                        }
                    }
                }
            }catch( InterruptedException e){}
        }
    }
    
    public void start() {
        a.start();
        t.start();
    }
    
    public void update( Graphics g ) {
        if( !go ) {
            paint(g);
            return;
        }
        if( ml ) p.translate( -4, 0 );
        else if( mr ) p.translate( 4, 0 );
        if( shot ) {
            ly -= 8;
            if( ly < -20 ) shot = false;
        }
        if( sshot ) {
            sly +=10;
            if( sly > 400 ) sshot = false;
        }
        win = true;
        for( int i=0; i < s.length; i++ ){
            for( int j=0; j < s[0].length; j++ ) {
                if( s[i][j]) {
                    Ellipse2D e = new Ellipse2D.Double( i*40+x, j*30+y, 30, 20 );
                    if( e.contains( lx, ly ) ) {
                        s[i][j] = false;
                        shot = false;
                        lx=-10;
                        po += 10;
                    } else if ( e.intersects( p.getBounds() ) ) {
                        ov = true;
                    }
                    win = false;
                }
            }
        }
        if( new Rectangle2D.Double( slx, sly, 4, 8 ).intersects(p.getBounds2D()) ) {
            ov = true;
        }
        
        paint( g );
    }
    
    public void paint( Graphics g ) {
        Graphics2D g2d = (Graphics2D)os.getGraphics();
        int bx = p.getBounds().x;
        int by = p.getBounds().y;
        g2d.drawImage( bgr, null, 0, 0 );
        g2d.setColor( Color.black );
        g2d.fill( p );
        g2d.setColor( Color.lightGray );
        g2d.fillOval( p.getBounds().x, p.getBounds().y, 30, 20 );
        g2d.setColor( Color.green );
        g2d.fillOval( p.getBounds().x+5, p.getBounds().y, 18, 18 );
        g2d.setColor( Color.white );
        g2d.fillRect( p.getBounds().x+13, p.getBounds().y-5, 3, 10 );

        g2d.setPaint( new GradientPaint( lx, ly, Color.yellow, lx, ly+15, Color.red ) );
        g2d.fillRoundRect( lx, ly, 4, 15,2,2);
        g2d.setColor( Color.yellow );
        for( int i = 0; i < 10; i++ ) {
            g2d.fillRect( lx-1 + ra.nextInt(6), ly+15 + ra.nextInt(10), 1, 1 );
        }
        g2d.setPaint( new GradientPaint( slx, sly, Color.yellow, slx, sly+15, Color.red ) );
        g2d.fillRoundRect( slx, sly, 4, 8,2,2);

        
        g2d.translate(x,y);
        for( int i=0; i < s.length; i++ ){
            for( int j=0; j < s[0].length; j++ ) {
                if( s[i][j] ) {
                    g2d.setColor( Color.lightGray );
                    g2d.fillOval( i*40, j*30, 30, 20 );
                    g2d.setColor( new Color( r, 0, 0) );
                    g2d.fillOval( i*40+6, j*30+1, 18, 18 );
                    g2d.setColor( Color.white );
                    g2d.fillRect( i*40+5, j*30+15, 3, 10 );
                    g2d.fillRect( i*40+20, j*30+15, 3, 10 );
                }
            }
        }
        if( st ) {
        }
        Paint pa = new GradientPaint( 0, 0, Color.yellow, 500, 400, Color.red );
        g2d.setFont( new Font("Helvetica",Font.BOLD, 40) );
        g2d.translate(-x,-y);
        if( win ) {
            g2d.setColor( Color.yellow );
            g2d.drawString( "GOOD JOB!", getWidth()/2-122, 148 );
            g2d.setPaint( pa );
            g2d.drawString( "GOOD JOB!", getWidth()/2-120, 150 );
        }else if( !go ) {
            g2d.setColor( Color.yellow );
            g2d.drawString( "Click to", getWidth()/2-72, 98 );
            g2d.drawString( "start", getWidth()/2-52, 178 );
            g2d.setPaint( pa );
            g2d.drawString( "Click to", getWidth()/2-70, 100 );
            g2d.drawString( "start", getWidth()/2-50, 180 );
        }else if( st ) {
            g2d.setColor( Color.yellow );
            g2d.drawString( "Get ready for", getWidth()/2-127, 98 );
            g2d.drawString( "level " + l, getWidth()/2-52, 178 );
            g2d.setPaint( pa );
            g2d.drawString( "Get ready for", getWidth()/2-125, 100 );
            g2d.drawString( "level " + l, getWidth()/2-50, 180 );
        }else if( ov ) {
            g2d.setColor( Color.yellow );
            g2d.drawString( "Game over!", getWidth()/2-122, 98 );
            g2d.drawString( "points: " + po, getWidth()/2-102, 178 );
            g2d.setPaint( pa );
            g2d.drawString( "Game over!", getWidth()/2-120, 100 );
            g2d.drawString( "points: " + po, getWidth()/2-100, 180 );
        }
        g2d.setFont( new Font( "Helvetica", Font.BOLD, 40 ) );
        g2d.drawString( String.valueOf(po), getWidth()/2,30 );
        ((Graphics2D)g).drawImage( os, null, 0, 0);
    }
    
    public void keyPressed(KeyEvent keyEvent) {
        if( keyEvent.getKeyCode() == KeyEvent.VK_LEFT ) ml = true;
        else if( keyEvent.getKeyCode() == KeyEvent.VK_RIGHT ) mr = true;
        else if( keyEvent.getKeyCode() == KeyEvent.VK_SPACE ) {
            if( shot ) return;
            shot = true;
            lx = p.getBounds().x+13;
            ly = p.getBounds().y;
        }       
    }
    
    public void keyReleased(KeyEvent keyEvent) {
        if( keyEvent.getKeyCode() == KeyEvent.VK_LEFT ) ml = false;
        else if( keyEvent.getKeyCode() == KeyEvent.VK_RIGHT ) mr = false;
    }
    
    public void keyTyped(KeyEvent keyEvent) {
    }
    
    public void mouseClicked(MouseEvent mouseEvent) {
        go=true;
    }
    
    public void mouseEntered(MouseEvent mouseEvent) {
    }
    
    public void mouseExited(MouseEvent mouseEvent) {
    }
    
    public void mousePressed(MouseEvent mouseEvent) {
    }
    
    public void mouseReleased(MouseEvent mouseEvent) {
    }    
}