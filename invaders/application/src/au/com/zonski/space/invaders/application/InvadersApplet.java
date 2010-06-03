package au.com.zonski.space.invaders.application;

import java.applet.Applet;

public class InvadersApplet extends Applet {

  public InvadersApplet() {
  }

  public void start() {
     try {
	InvadersCanvas.main( null );
     } catch( Exception ex ) {
        ex.printStackTrace();
     }
  }

}