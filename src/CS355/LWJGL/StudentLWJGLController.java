package CS355.LWJGL;


//You might notice a lot of imports here.
//You are probably wondering why I didn't just import org.lwjgl.opengl.GL11.*
//Well, I did it as a hint to you.
//OpenGL has a lot of commands, and it can be kind of intimidating.
//This is a list of all the commands I used when I implemented my project.
//Therefore, if a command appears in this list, you probably need it.
//If it doesn't appear in this list, you probably don't.
//Of course, your milage may vary. Don't feel restricted by this list of imports.
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author Craig
 */
public class StudentLWJGLController implements CS355LWJGLController 
{
  
  //This is a model of a house.
  //It has a single method that returns an iterator full of Line3Ds.
  //A "Line3D" is a wrapper class around two Point2Ds.
  //It should all be fairly intuitive if you look at those classes.
  //If not, I apologize.
  private WireFrame model = new HouseModel();
  private ObjectTransformation[] houses = new ObjectTransformation[]{
	  new ObjectTransformation(),
	  (new ObjectTransformation(-15.0, 0.0, 15.0, Math.PI/2.0)).setColor(255, 0, 0),
	  (new ObjectTransformation(15.0, 0.0, 15.0, -Math.PI/2.0)).setColor(0, 255, 0),
	  (new ObjectTransformation(0.0, 0.0, 30.0, Math.PI)).setColor(0, 0, 255),
	  (new ObjectTransformation(-15.0, 0.0, -2.5, Math.PI/4.0)).setColor(255, 0, 255),
	  (new ObjectTransformation(15.0, 0.0, -2.5, -Math.PI/4.0)).setColor(255, 255, 0),
	  (new ObjectTransformation(-15.0, 0.0, 32.5, Math.PI*3.0/4.0)).setColor(0, 255, 255),
	  (new ObjectTransformation(15.0, 0.0, 32.5, -Math.PI*3.0/4.0)).setColor(255, 255, 255),
  };
  
  private static final double TWO_PI = Math.PI*2.0;
  private static final double X_START = 0.0;
  private static final double Y_START = 0.0;
  private static final double Z_START = 10.0;
  private static final double XZ_ANGLE_START = 0.0;
  private static final double LR_INCR = 0.2;
  private static final double UD_INCR = 0.2;
  private static final double FB_INCR = 0.2;
  private static final double LR_ANGLE_INCR = (2*Math.PI)/360.0;
  
  private double x = StudentLWJGLController.X_START;
  private double y = StudentLWJGLController.Y_START;
  private double z = StudentLWJGLController.Z_START;
  private double xzAngle = StudentLWJGLController.XZ_ANGLE_START; // Radians.
  
  private double fovyAngle = Math.PI/3; // Radians.
  private double viewWidth = LWJGLSandbox.DISPLAY_WIDTH;
  private double viewHeight = LWJGLSandbox.DISPLAY_HEIGHT;
  private double nearClipping = 1.0;
  private double farClipping = 100.0;
  
  private double orthoLeft = -8.0;
  private double orthoRight = 8.0;
  private double orthoBottom = -8.0;
  private double orthoTop = 8.0;

  //This method is called to "resize" the viewport to match the screen.
  //When you first start, have it be in perspective mode.
  @Override
    public void resizeGL() {
	  	glViewport(0, 0, (int)this.viewWidth, (int)this.viewHeight);
	  	this.changeToPerspective();
	  	this.updateModelViewMatrix();
    }

    @Override
    public void update() {
        return;
    }
    
    public void changeToOrthographic() {
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
    	glOrtho(this.orthoLeft, this.orthoRight, this.orthoBottom, this.orthoTop, this.nearClipping, this.farClipping);
    	glMatrixMode(GL_MODELVIEW);
    }
    
    public void changeToPerspective() {
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
    	gluPerspective((float)this.convertRadiansToDegrees(this.fovyAngle), 
    					(float)(this.viewWidth/this.viewHeight), 
    					(float)this.nearClipping, 
    					(float)this.farClipping);
    	glMatrixMode(GL_MODELVIEW);
    }
    
    private void updateModelViewMatrix() {
    	glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glRotatef((float)this.convertRadiansToDegrees(-this.xzAngle), 0.0f, 1.0f, 0.0f);
        glTranslatef(-(float)this.x, -(float)this.y, -(float)this.z);
    }

    //This is called every frame, and should be responsible for keyboard updates.
    //An example keyboard event is captured below.
    //The "Keyboard" static class should contain everything you need to finish
    // this up.
    @Override
    public void updateKeyboard() 
    {
    	double fbInc = 0.0; // Forward backward change.
    	double lrInc = 0.0; // Left right change.
    	double udInc = 0.0; // Up down change.
    	double lrAngleInc = 0.0; // Angle change.
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            lrInc -= StudentLWJGLController.LR_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            lrInc += StudentLWJGLController.LR_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            fbInc += StudentLWJGLController.FB_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
        	fbInc -= StudentLWJGLController.FB_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
        	lrAngleInc += StudentLWJGLController.LR_ANGLE_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
        	lrAngleInc -= StudentLWJGLController.LR_ANGLE_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
        	udInc += StudentLWJGLController.UD_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_F)) {
        	udInc -= StudentLWJGLController.UD_INCR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_H)) {
            this.x = StudentLWJGLController.X_START;
            this.y = StudentLWJGLController.Y_START;
            this.z = StudentLWJGLController.Z_START;
            this.xzAngle = StudentLWJGLController.XZ_ANGLE_START;
            this.updateModelViewMatrix();
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_O)) {
            this.changeToOrthographic();
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_P)) {
            this.changeToPerspective();
        }
        if(lrInc != 0.0 || fbInc != 0.0 || udInc != 0.0 || lrAngleInc != 0.0) {
        	double sin = Math.sin(-this.xzAngle);
        	double cos = Math.cos(-this.xzAngle);
        	this.x += (fbInc*sin + lrInc*cos);
        	this.y += udInc;
        	this.z -= (fbInc*cos - lrInc*sin);
        	this.xzAngle += lrAngleInc;
        	
        	// Wrap angles.
        	if(this.xzAngle > TWO_PI) {
        		this.xzAngle -= TWO_PI;
        	}
        	if(this.xzAngle < -TWO_PI) {
        		this.xzAngle += TWO_PI;
        	}
        	
        	// Update matrix.
        	this.updateModelViewMatrix();
        }
    }
    
    private double convertRadiansToDegrees(double rad) {
    	return ((rad*360.0)/(2.0*Math.PI));
    }

    //This method is the one that actually draws to the screen.
    @Override
    public void render() 
    {
        // This clears the screen.
        glClear(GL_COLOR_BUFFER_BIT);
        
        glColor3f(1.0f, 0.0f, 0.0f);
        
        // Change model view matrix.
        glMatrixMode(GL_MODELVIEW);
        
        // Do your drawing here.
        for(int i = 0; i < this.houses.length; i++) {
        	ObjectTransformation ot = this.houses[i];
        	glColor3f(ot.R(), ot.G(), ot.B());
        	glPushMatrix();
        	glTranslatef(ot.X(), ot.Y(), ot.Z());
        	glRotatef((float)this.convertRadiansToDegrees(ot.angle), 0.0f, 1.0f, 0.0f);
	        Iterator<Line3D> lines = this.model.getLines();
	        while(lines.hasNext()) {
	        	Line3D l = lines.next();
	        	glBegin(GL_LINES);
	        	glVertex3d(l.start.x, l.start.y, l.start.z);
	        	glVertex3d(l.end.x, l.end.y, l.end.z);
	        	glEnd();
	        }
	        glPopMatrix();
        }
    }
    
}
