package au.com.zonski.space.invaders.j2me;

import au.com.zonski.space.invaders.core.*;
import au.com.zonski.space.domain.Body;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.math.FixedMath;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Display;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 7/05/2005
 * Time: 17:57:55
 */
public class J2MEInvaders extends MIDlet
{
    private J2MEInvadersCanvas canvas;

    public static final String SCREEN_WIDTH = "screen.width";
    public static final String SCREEN_HEIGHT = "screen.height";

    public J2MEInvaders()
    {
        int width;
        try
        {
            width = Integer.parseInt(this.getAppProperty(SCREEN_WIDTH));
        }catch(Exception ex){
            width = 0;
        }
        int height;
        try
        {
            height = Integer.parseInt(this.getAppProperty(SCREEN_HEIGHT));
        }catch(Exception ex){
            height = 0;
        }

        this.canvas = new J2MEInvadersCanvas(width, height);

        try
        {
            BinaryLevelFactory levelFactory = new BinaryLevelFactory("/");
            Game game = levelFactory.getGame();
            game.start();
            this.canvas.setGame(game);
        }catch(Exception ex){
            ex.printStackTrace();
            // TODO ; handle elegantly
        }
    }

    protected void startApp() throws MIDletStateChangeException
    {
        try
        {
            Display.getDisplay(this).setCurrent(this.canvas);
            this.canvas.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    protected void pauseApp()
    {
        this.canvas.stop();
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
        this.canvas.stop();
        this.canvas = null;
    }
}
