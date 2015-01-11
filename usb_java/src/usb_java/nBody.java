package usb_java;

import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
/**
* Self-contained example (within a single class only to keep it simple)
* displaying a rotating quad
*/
class JOGLQuad_4 implements GLEventListener, KeyListener {

float viewAngle = -4.0e12f;
double radius;

//@Override
public void display(GLAutoDrawable gLDrawable) {
	final GL2 gl = gLDrawable.getGL().getGL2();
	GLU glu = GLU.createGLU(gl);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	
	gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	
	if(viewAngle <= -5.0e12f){
		radius = 4.5e10f;
	}else if(viewAngle > -5.0e12f && viewAngle <= -4.0e12f){
		radius = 3.7e10f;
	}else if(viewAngle > -4.0e12f && viewAngle <= -3.0e12f){
		radius = 3.0e10f;
	}else if(viewAngle > -3.0e12f && viewAngle <= -2.0e12f){
		radius = 1.0e10f;
	}else if(viewAngle > -2.0e12f){
		radius = 8.0e9f;
	}
	
	float colorx [] = { 1.0f, 0.5f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 0.6f, 1.0f, 0.6f, 1.0f, 0.5f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 0.6f, 1.0f, 0.6f, 1.0f, 1.0f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 0.6f, 1.0f, 0.6f, 0.6f, 1.0f, 0.6f};
	float colory [] = { 0.5f, 0.5f, 1.0f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 1.0f, 0.6f, 0.5f, 0.5f, 1.0f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 1.0f, 0.6f, 0.5f, 0.2f, 1.0f, 1.0f, 0.6f, 0.2f, 1.0f, 0.2f, 1.0f, 0.6f, 0.2f, 1.0f, 0.6f};
	float colorz [] = { 0.0f, 0.5f, 0.2f, 0.2f, 1.0f, 0.6f, 0.6f, 0.1f, 1.0f, 1.0f, 0.0f, 0.5f, 0.2f, 0.2f, 1.0f, 0.6f, 0.6f, 0.1f, 1.0f, 1.0f, 0.0f, 0.2f, 0.2f, 0.2f, 1.0f, 0.6f, 0.6f, 0.1f, 1.0f, 1.0f, 0.1f, 1.0f, 1.0f};
	
	for(int i = 0; i < SimpleSyncTransfer.numOfPlanets; i++){
		GLUT glut = new GLUT();
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, viewAngle);
		gl.glPushMatrix();
		gl.glTranslatef(SimpleSyncTransfer.pX[i], SimpleSyncTransfer.pY[i], 0.0f);
		drawCircle(gl, radius, 0.0f, 0.5f, 0.5f);
		//drawCircle(gl, radius, colorx[i], colory[i], colorz[i]);
        gl.glColor3d(1.0, 0.2, 0.2);
        gl.glRasterPos3d(1000,12,0);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Body "+(i+1));
		gl.glPopMatrix();
		//System.out.println("Px: " + SimpleSyncTransfer.pX[i] + " ,Py: " +  SimpleSyncTransfer.pX[i] + " ,NOfPla: " + SimpleSyncTransfer.numOfPlanets);
	}

}

public void drawCircle(GL2 globj, double radius, float colorx, float colory, float colorz){
	GL2 gllocal = globj;
	gllocal.glBegin(GL2.GL_TRIANGLE_FAN);
	//gllocal.glColor3f(0.0f, 0.5f, 0.5f); // set the color of the quad
	gllocal.glColor3f(colorx, colory, colorz); // set the color of the quad
	 double radius2 = radius;
	 for(int angle = 0; angle < 360 ; angle++ ){
		 gllocal.glVertex2d(Math.sin(angle) * radius2, Math.cos(angle) * radius2);
	 }
	// Done Drawing The Quad
	gllocal.glEnd();
	
}

public void drawText(GL2 globj, int font, int number){
	GL2 gllocal = globj;
	//gllocal.glBegin(GL2.);
	GLUT glut = new GLUT();
	//glut.glutStrokeString(font, Integer.toString(number));
	glut.glutStrokeCharacter(font, Integer.toString(number).charAt(0));
}
//@Override
public void init(GLAutoDrawable glDrawable) {
	GL2 gl = glDrawable.getGL().getGL2();
	gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	gl.glClearDepth(1.0f);
	gl.glEnable(GL.GL_DEPTH_TEST);
	gl.glDepthFunc(GL.GL_LEQUAL);
	gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
}
//@Override
public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
	GL2 gl = gLDrawable.getGL().getGL2();
	final float aspect = (float) width / (float) height;
	gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
	gl.glLoadIdentity();
	final float fh = 0.5f;
	final float fw = fh * aspect;
	gl.glFrustumf(-fw, fw, -fh, fh, 1.0f, 5.0e14f);
	gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	gl.glLoadIdentity();

}
//@Override
public void dispose(GLAutoDrawable gLDrawable) {
}

@Override
public void keyPressed(KeyEvent e) {
	if(e.getKeyCode() == KeyEvent.VK_DOWN){
		viewAngle = viewAngle - 1.0e11f; 
		if(viewAngle < -4.8e14f){
			viewAngle = -4.8e14f;
		}
	}else if(e.getKeyCode() == KeyEvent.VK_UP){
		viewAngle = viewAngle + 1.0e11f; 
	}else if(e.getKeyCode() == KeyEvent.VK_1){
        
       SimpleSyncTransfer.frameRate = SimpleSyncTransfer.frameRate + 1;
       if(SimpleSyncTransfer.frameRate < 30)
       {
           SimpleSyncTransfer.frameRate = 30;
       }
       
	}else if(e.getKeyCode() == KeyEvent.VK_2){
       
	   SimpleSyncTransfer.frameRate = SimpleSyncTransfer.frameRate - 1;
       if(SimpleSyncTransfer.frameRate < 1)
       {
           SimpleSyncTransfer.frameRate = 1;
       }
        
   }
	
}

@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}

}

public class nBody implements Runnable {
	
	public void run() {
			// TODO Auto-generated method stub
			JOGLQuad_4 solarSystem = new JOGLQuad_4();
		    final GLCanvas canvas = new GLCanvas();
			final Frame frame = new Frame("Jogl Quad drawing_2");
			final Animator animator = new Animator(canvas);
			canvas.addGLEventListener(solarSystem);
			canvas.addKeyListener(solarSystem);
			frame.add(canvas);
			frame.setSize(1366, 768);
			frame.setResizable(false);
			frame.addWindowListener(new WindowAdapter() {
	
			public void windowClosing(WindowEvent e) {
			animator.stop();
			frame.dispose();
			System.exit(0);
			}
			});
			frame.setVisible(true);
			animator.start();
			canvas.requestFocus();
		}
}


