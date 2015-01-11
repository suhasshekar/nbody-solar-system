package usb_java;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class move extends JPanel{
  Timer timer;
  int x,y,width=40,height=40;
  int radius=(width/2);
  int frXPos;  
  int speedX=1;

  void move1() {
     ActionListener taskPerformer = new ActionListener () {
       
    public void actionPerformed(ActionEvent ae) {
            if( x > (frXPos-width) )  {
               speedX=-1;
            }     
              else if ( x == 0 ) {
                  speedX=1;
              }                           
             x=x+speedX;
             repaint();                  
         
          }
     };
    timer= new Timer(0,taskPerformer);
    timer.start();
  }           
  
  move(int x, int y, int frXPos){
	  this.x = x;
	  this.y = y;
	  this.frXPos = frXPos;
	  move1();
  }
  public void paintComponent(Graphics g) {
     super.paintComponent(g);
     g.setColor(Color.red);
     g.fillOval(x,y,width,height);
 } 
}

class MovingBall {
  MovingBall() {
      JFrame fr=new JFrame("Moving Ball 1D");
      move o=new move(0, 10, 500);
      fr.add(o);
      move o1 = new move(1, 10, 400);
      fr.add(o1);
     fr.setVisible(true);
     fr.setSize(500,500);          // width of the frame is 500
     fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     
  }
 public static void main(String args[]) {
   new MovingBall();
 }
}