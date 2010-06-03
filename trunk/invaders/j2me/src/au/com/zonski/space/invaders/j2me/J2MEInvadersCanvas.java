package au.com.zonski.space.invaders.j2me;

import au.com.zonski.space.invaders.core.Game;
import au.com.zonski.space.invaders.core.PlayerController;
import au.com.zonski.space.invaders.core.Level;
import au.com.zonski.space.client.core.ProjectionPlane;
import au.com.zonski.space.client.core.ProjectionLine;
import au.com.zonski.space.client.core.ProjectionMesh;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Vector;

import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 7/05/2005
 * Time: 17:57:39
 */
public class J2MEInvadersCanvas extends Canvas implements Runnable
{
    private static final int MARGIN = FixedMath.toFixed(200);

    private int dangle = 0;

    private Game game;
    private boolean running;
    private ProjectionPlane projectionPlane;
    private int projectionAngle = FixedMath.toFixed(70);

    private int[] viewRotation = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};
    private int vx;
    private int vy;
    private int vz;

    private Image buffer;
    private Graphics bufferGraphics;

    private int width;
    private int height;

    public J2MEInvadersCanvas(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    protected void keyPressed(int i)
    {
        super.keyPressed(i);
        int code = this.getGameAction(i);
        switch(code)
        {
            case Canvas.LEFT:
                this.game.currentPlayerController.setDown(PlayerController.LEFT);
                break;
            case Canvas.RIGHT:
                this.game.currentPlayerController.setDown(PlayerController.RIGHT);
                break;
            case Canvas.FIRE:
                this.game.currentPlayerController.setDown(PlayerController.SHOOT);
                break;
            case Canvas.UP:
                this.dangle = FixedMath.DEFAULT_ONE;
                break;
            case Canvas.DOWN:
                this.dangle = -FixedMath.DEFAULT_ONE;
                break;
        }
    }

    protected void keyReleased(int i)
    {
        super.keyReleased(i);
        int code = this.getGameAction(i);
        switch(code)
        {
            case Canvas.LEFT:
                this.game.currentPlayerController.setUp(PlayerController.LEFT);
                break;
            case Canvas.RIGHT:
                this.game.currentPlayerController.setUp(PlayerController.RIGHT);
                break;
            case Canvas.FIRE:
                this.game.currentPlayerController.setUp(PlayerController.SHOOT);
                break;
            case Canvas.UP:
            case Canvas.DOWN:
                this.dangle = 0;
                break;
        }
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    protected void paint(Graphics graphics)
    {
        try
        {
            // double buffering
            if(this.buffer == null)
            {
                if(this.width == 0)
                {
                    this.width = this.getWidth();
                }
                if(this.height == 0)
                {
                    this.height = this.getHeight();
                }
                this.buffer = Image.createImage(width, height);
                this.bufferGraphics = this.buffer.getGraphics();
            }
            draw(this.bufferGraphics);
            graphics.drawImage(this.buffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void draw(Graphics g)
    {
        if(this.projectionPlane == null)
        {
            this.projectionPlane = new ProjectionPlane(width, height);
        }
        g.setColor(0);
        g.fillRect(0, 0, g.getClipWidth(), g.getClipHeight());


        this.projectionPlane.reset(this.viewRotation, this.vx, this.vy, this.vz);
//        this.projectionPlane.reset(
//                player.rotation,
//                player.center[0], player.center[1] + 50 * FixedMath.DEFAULT_ONE, player.center[2] + 100 * FixedMath.DEFAULT_ONE        );
        Level level = this.game.getCurrentLevel();
        Vector[] sides = level.getBodies();
        for(int side = sides.length; side>0; )
        {
            side--;
            Vector bodies = sides[side];
            for(int i=bodies.size(); i>0; )
            {
                i--;
                Body body = (Body)bodies.elementAt(i);
                this.projectionPlane.project(body);
            }
        }
        this.projectionPlane.build();
        //this.projectionPlane.render(g);
        Vector meshes = this.projectionPlane.getProjected();
        for(int i=meshes.size(); i>0; )
        {
            i--;
            ProjectionMesh mesh = (ProjectionMesh)meshes.elementAt(i);
            g.setColor(mesh.color);
            Vector lines = mesh.lines;
            for(int j=lines.size(); j>0; )
            {
                j--;
                ProjectionLine line = (ProjectionLine)lines.elementAt(j);
                //if(line.perimeter)
                {
                    g.drawLine(
                            line.from[0] + this.width/2,
                            - line.from[1] + this.height/2,
                            line.to[0] + this.width/2,
                            -line.to[1] + this.height/2
                    );
                }
            }
        }
    }

    public void start()
    {
        if(!this.running)
        {
            this.running = true;
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    public void stop()
    {
        this.running = false;
    }

    public void run()
    {
        try
        {
            if(this.width == 0)
            {
                this.width = this.getWidth();
            }
            if(this.height == 0)
            {
                this.height = this.getHeight();
            }
            while(this.running)
            {
                if(this.game != null)
                {
                    this.game.iterate();

                    // do the view
                    this.projectionAngle += this.dangle;
                    int viewAngle = FixedMath.atan2(
                            this.height/2,
                            ProjectionPlane.DEFAULT_DEYE,
                            FixedMath.DEFAULT_PRECISION
                    ) * 2;

                    int projectionCenterAngle =  (-FixedMath.toFixed(90) + projectionAngle - viewAngle/2);
                    //int projectionCenterAngle =  - FixedMath.toFixed(30);
                    int[] viewRotation = new int[]{
                        FixedMath.cos(projectionCenterAngle/2),
                        FixedMath.sin(projectionCenterAngle/2), 0, 0, FixedMath.DEFAULT_PRECISION
                    };

                    Body player = this.game.getCurrentPlayer();
                    int[] playerCenter = player.center;
                    int radius = player.shape.getMaxRadius() * 2;
                    int depth = this.game.getCurrentLevel().getHeight() + MARGIN;

                    int vx = playerCenter[0];
                    // TODO : this should be able to be optimised, but I can't seem to get it to work
                    int vdz = MathFP.mul(
                            depth,
                            MathFP.div(
                                    MathFP.div(
                                            FixedMath.tan(projectionAngle),
                                            FixedMath.tan(viewAngle)) - FixedMath.DEFAULT_ONE
                                    ,
                                    MathFP.mul(
                                            FixedMath.tan(projectionAngle),
                                            FixedMath.tan(projectionAngle)
                                    ) + FixedMath.DEFAULT_ONE)
                            );


                    //int vy = playerCenter[1] + FixedMath.divide(depth + vdz, FixedMath.tan(projectionAngle));
                    int vy = MathFP.div(depth + vdz, FixedMath.tan(projectionAngle));

                    //int vz = playerCenter[2] + vdz + radius;
                    int vz = vdz + MARGIN;

                    this.viewRotation = viewRotation;
                    this.vx = vx;
                    this.vy = vy;
                    this.vz = vz;

                    repaint();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
