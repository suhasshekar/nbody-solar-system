package usb_java;
import java.awt.*;
import java.util.*;

class BallPicture implements Runnable {

    int naptime = 30;                                // please the eye
    Balls balls;
    
    public 
    BallPicture(Balls context) {
        balls = context;
    }
        
    public void 
    nap(int duration) {                              // run in real time
        try {Thread.sleep(duration);}
        catch (InterruptedException waken) {
            System.out.println("Picture.nap -- botched");
        }
    }
    
    public void
    run() {                                          // picture thread
        while (true) {
            nap(naptime);
            balls.osPaint();
        }
    }
}

class BallWind implements Runnable {

    public static double vx, vy;                     // zero initial wind
    int naptime;  
        
    public void 
    nap(int duration) {                              // run in real time
        try {Thread.sleep(duration);}
        catch (InterruptedException waken) {
            System.out.println("Wind.nap -- botched");
        }
    }
    
    public void
    run() {                                          // wind thread
        while (true) {
            nap(naptime);
            vx = Math.random() - 0.5;                // new wind speeds
            vy = Math.random() - 0.5;
            naptime = (int)(Math.random()*2000);     // next wind change
        }
    }
}

class Ball implements Runnable {

   double x, y;                                      // ball position
   double dx, dy;                                    // ball velocity
   double ddx, ddy;                                  // ball acceleration;
 
   double r;                                         // ball size
   Color  c;                                         // ball color
   Balls context;                                    // the master routine
   int naptime = 30;                                 // 30 ms
 
   public
   Ball(Balls context) {                             // ctor: a random ball
       double w = context.size().width;
       double h = context.size().height;
       r  = Math.random()*Math.max(w, 500)/5;        // at most 1/5 a box
       x  = Math.random()*w;                         // anywhere in box
       y  = Math.random()*h;
       float r = (float)Math.random();               // red
       float g = (float)Math.random();               // green
       float b = (float)Math.random();               // blue
       switch ((int) (Math.random()*4)) {            // pretty choices
       case 0:
           c  = new Color(r, g, b);                  // random color
           break;
       case 1:
           c  = new Color(0, g, b);                  // no red
           break;
       case 2:
           c  = new Color(r, 0, b);                  // no green
           break;
       case 3:
           c  = new Color(r, g, 0);                  // no blue
           break;
       }
       this.context = context;
       move();                                       // check edge effects
   }
   
   private synchronized void
   move() {                                          // Object, move thyself!
       double w = context.size().width;
       double h = context.size().height;
       ddx = BallWind.vx/r*10;                       // small acceleration
       ddy = BallWind.vy/r*10;                       // smallest balls fastest
       dx += ddx;                                    // velocity
       dy += ddy;
       x  += dx;                                     // position
       y  += dy;
       // compute bounces at box edge
       if (x+r > w) { x = w - r; dx = -dx*.9; }      // bounce, lose energy
       if (x-r < 0) { x = r;     dx = -dx*.9; }
       if (y+r > h) { y = h - r; dy = -dy*.9; }
       if (y-r < 0) { y = r;     dy = -dy*.9; }
   }
 
   public synchronized void
   draw(Graphics g) {                                // Ball, draw thyself!
       int ix = (int)(x-r);
       int iy = (int)(y-r);
       int id = (int)(2*r);
       g.setColor(c);
       g.fillOval(ix, iy, id, id);                   // circle, diameter id
       g.setColor(Color.black);
   }

    public void 
    nap(int duration) {                              // run in real time
        try {Thread.sleep(duration);}
        catch (InterruptedException waken) {
            System.out.println("Ball.nap -- botched");
        }
    }
    
    public void 
    run() {                                          // bouncing ball
        while (true) {
            nap(naptime);
            move();
        }
    }
}
   
   
public class Balls extends java.applet.Applet {

    Vector balls    = new Vector();
    Vector controls = new Vector();
    Button addBall  = new Button("insert new ball");
    Button subBall  = new Button("remove oldest ball");
    Label  howMany  = new Label();

    int         count = 0;                           // no balls to start
    Dimension   screen;                              // actual screen size
    Graphics    osg;                                 // off screen graphics
    Image       osi;                                 // off screen image
    BallWind    w = new BallWind();                  // a capricious wind
    Thread      wind;                                // its thread
    BallPicture p = new BallPicture(this);           // the presentation
    Thread      picture;                             // its thread
 
    private String
    ballReport() {                                   // visible label
        String t;
        if (count == 0)       t = "  no balls active "; 
        else if (count == 1)  t = "   1 ball active ";
        else if (count < 10)  t = "  " + count + " balls active ";
        else if (count < 100) t = " " + count + " balls active ";
        else                  t = count + " balls active "; 
        return t;
    }
     
    private void
    insert() {
        Ball b = new Ball(this);                     // create a ball
        Thread t = new Thread(b);
        balls.addElement(b);                         // insert in list
        controls.addElement(t);
        count++;                                     // keep track
        howMany.setText(ballReport());
        t.start();                                   // start new ball
        showStatus("inserted a new ball");
    }
    
    private void
    remove() {
        Thread t = (Thread) controls.elementAt(0);   // oldest ball
        t.stop();                                    // thread done
        controls.removeElement(t);                   // thread gone
        balls.removeElementAt(0);                    // ball gone
        count--;                                     // one less ball
        howMany.setText(ballReport());
        showStatus("removed oldest ball");
    }
    
    public void
    init() {
        trace("begin Balls.init()");
        showStatus("balls initializing");
        add(addBall);                                // place buttons
        add(subBall);
        add(howMany);                                // place counter
        
        screen = this.size();                        // display area
        osi = createImage(screen.width, screen.height);
        osg = osi.getGraphics();                     // off screen graphics
        
        insert();                                    // first ball
        controls.removeElementAt(0);                 // start() adds control
        trace("end   Balls.init()");
    }
    
    public boolean
    action(Event e, Object o) {                      // buttons
        if (e.target == addBall) {                   // insert a ball
            insert();
        } else if (e.target == subBall) {            // delete a ball
            if (!controls.isEmpty()) {               // if there is one
                 remove();
            }
        }
        return super.action(e,o);                    // all done
    }
    
    public void
    osPaint() {
        Dimension s = this.size();                  // display area
        if (s.width != screen.width || s.height != screen.height) {
            screen = s;                             // user grabbed applet
            osi = createImage(screen.width, screen.height);
            osg = osi.getGraphics();                // off screen graphics
        }
        synchronized (osg) {                        // paint picture
            osg.setColor(Color.white);
            osg.fillRect(0,0,screen.width, screen.height);
            for (Enumeration e=balls.elements(); e.hasMoreElements(); ) {
                ((Ball)e.nextElement()).draw(osg);
            }
        }
        repaint();                                   // schedule an update
    }
    
    public void
    paint(Graphics g) {                              // blit
        synchronized (osg) {
            g.drawImage(osi,0,0,null);
        }
    }
    
    public void
    update(Graphics g) {                             // avoid screen clear
        paint(g);                                    // latest balls
    }
    
    public void 
    trace(String msg) {                              // debug aid
        //System.out.println(msg);
    }
    
    public void
    start() {                                        // (re)start all threads
        trace("begin Balls.start()");                // for the wind
        wind = new Thread(w);
        wind.start();                                // start the wind
        picture = new Thread(p);
        picture.start();                             // start the presentation
        picture.setPriority(picture.getPriority()+1);
        
        for (Enumeration b=balls.elements(); b.hasMoreElements(); ) {
            Thread t = new Thread((Ball)b.nextElement());
            controls.addElement(t);                  // thread per ball
        }
        for (Enumeration c=controls.elements(); c.hasMoreElements(); ) {
            Thread t = (Thread)c.nextElement();
            t.start();                               // start a ball
        }
        showStatus("balls started");
        trace("end   Balls.start()");
    }
    
    public void
    stop() {                                         // stop all threads
        trace("begin Balls.stop()");
        wind.stop();                                 // stop the wind
        picture.stop();                              // stop the presentation
        
        for (Enumeration e=controls.elements(); e.hasMoreElements(); ) {
            Thread t = (Thread)e.nextElement();
            t.stop();                                // stop a ball
        }                                            // but keep the balls
        controls.removeAllElements();                // discard threads
        showStatus("balls stopped");
        trace("end   Balls.stop()");
    }
    
}  