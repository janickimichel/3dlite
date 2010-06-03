package au.com.zonski.space.invaders.application;

import au.com.zonski.space.invaders.core.*;
import au.com.zonski.space.domain.Body;
import au.com.zonski.space.client.core.ProjectionPlane;
import au.com.zonski.space.client.core.ProjectionMesh;
import au.com.zonski.space.client.core.ProjectionLine;
import au.com.zonski.math.FixedMath;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.shape3d.Composite;
import au.com.zonski.shape3d.Shape;
import au.com.zonski.config.xml.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 3/05/2005
 * Time: 16:00:00
 */
public class InvadersCanvas extends Canvas implements Runnable
{
    private Game game;
    private Image bufferedImage;
    private Graphics bufferedGraphics;
    private boolean running;

    private int projectionAngle = FixedMath.toFixed(70);

    private static final int MARGIN = FixedMath.toFixed(200);

    private Stroke outerStroke = new BasicStroke(2f);
    private Stroke innerStroke = new BasicStroke(1f);

    private static final int VISIBLE_UNITS = 16;

//    private Image healthConsole;
//    private Image weaponConsole;
    private Image[] images;
//    private Image laser;

    public InvadersCanvas(Game game)
    {
        this.game = game;

        this.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(InvadersCanvas.this.game.currentPlayerController != null)
                {
                    switch(e.getKeyCode())
                    {
                        case KeyEvent.VK_LEFT:
                            InvadersCanvas.this.game.currentPlayerController.setDown(PlayerController.LEFT);
                            break;
                        case KeyEvent.VK_RIGHT:
                            InvadersCanvas.this.game.currentPlayerController.setDown(PlayerController.RIGHT);
                            break;
                        case KeyEvent.VK_UP:
                            InvadersCanvas.this.projectionAngle += FixedMath.DEFAULT_ONE;
                            break;
                        case KeyEvent.VK_DOWN:
                            InvadersCanvas.this.projectionAngle -= FixedMath.DEFAULT_ONE;
                            break;
                        case KeyEvent.VK_SPACE:
                            InvadersCanvas.this.game.currentPlayerController.setDown(PlayerController.SHOOT);
                            break;
                    }
                }
            }

            public void keyReleased(KeyEvent e)
            {
                if(InvadersCanvas.this.game.currentPlayerController != null)
                {
                    switch(e.getKeyCode())
                    {
                        case KeyEvent.VK_LEFT:
                            InvadersCanvas.this.game.currentPlayerController.setUp(PlayerController.LEFT);
                            break;
                        case KeyEvent.VK_RIGHT:
                            InvadersCanvas.this.game.currentPlayerController.setUp(PlayerController.RIGHT);
                            break;
                        case KeyEvent.VK_SPACE:
                            InvadersCanvas.this.game.currentPlayerController.setUp(PlayerController.SHOOT);
                            break;
                    }
                }
            }
        });

//        this.weaponConsole = this.getToolkit().getImage("d:/project/space/invaders/res/image/application/left.png");
//        this.healthConsole = this.getToolkit().getImage("d:/project/space/invaders/res/image/application/right.png");
//        this.twinLaser = this.getToolkit().getImage("d:/project/space/invaders/res/image/application/twin_laser.png");
//        this.player = this.getToolkit().getImage("d:/project/space/invaders/res/image/application/player.png");
//        this.laser = this.getToolkit().getImage("d:/project/space/invaders/res/image/application/laser.png");
    }

    private void render(Graphics g)
    {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Ship player = this.game.getCurrentPlayer();
        int[] playerCenter = player.center;

        // tilt the camera down to include the player and all the invaders vertically
        //int[] viewRotation = playerRotation;
        int viewAngle = FixedMath.atan2(
                this.getHeight()/2,
                ProjectionPlane.DEFAULT_DEYE,
                FixedMath.DEFAULT_PRECISION
        ) * 2;
        int projectionCenterAngle =  (-FixedMath.toFixed(90) + projectionAngle - viewAngle/2);
        //int projectionCenterAngle =  - FixedMath.toFixed(30);
        int[] viewRotation = new int[]{
            FixedMath.cos(projectionCenterAngle/2),
            FixedMath.sin(projectionCenterAngle/2), 0, 0, FixedMath.DEFAULT_PRECISION
        };

        int radius = player.shape.getMaxRadius() * 2;
        int depth = this.game.getCurrentLevel().getHeight() + MARGIN;

        int vx = playerCenter[0];
        int vdz = FixedMath.multiply(
                depth,
                FixedMath.divide(
                        FixedMath.divide(
                                FixedMath.tan(projectionAngle),
                                FixedMath.tan(viewAngle)) - FixedMath.DEFAULT_ONE
                        ,
                        FixedMath.multiply(
                                FixedMath.tan(projectionAngle),
                                FixedMath.tan(projectionAngle)
                        ) + FixedMath.DEFAULT_ONE)
                );
        //int vy = playerCenter[1] + FixedMath.divide(depth + vdz, FixedMath.tan(projectionAngle));
        int vy = 0 + FixedMath.divide(depth + vdz, FixedMath.tan(projectionAngle));
        //int vz = playerCenter[2] + vdz + radius;
        int vz = 0 + vdz + MARGIN;

//        System.out.println("vdz : "+FixedMath.getInteger(vdz));
//        System.out.println("view angle : "+FixedMath.getInteger(viewAngle));
//        System.out.println("center angle : "+FixedMath.getInteger(projectionCenterAngle));
//        System.out.println("("+FixedMath.getInteger(vx)+","+FixedMath.getInteger(vy)+","+FixedMath.getInteger(vz)+")");

        ProjectionPlane projection = new ProjectionPlane(
                this.getWidth(), this.getHeight()
        );
        projection.reset(viewRotation, vx, vy, vz);

//        ProjectionPlane projection = new ProjectionPlane(
//                this.getWidth(), this.getHeight(),
//                player.rotation,
//                player.center[0], player.center[1] + 50 * FixedMath.DEFAULT_ONE, player.center[2] + 100 * FixedMath.DEFAULT_ONE
//        );
        Level level = this.game.getCurrentLevel();
        Vector[] bodies = level.getBodies();
        for(int i=bodies.length; i>0; )
        {
            i--;
            Vector sideBodies = bodies[i];
            for(int j=sideBodies.size(); j>0; )
            {
                j--;
                Body body = (Body)sideBodies.elementAt(j);
                projection.project(body);
            }
        }
        projection.build();

        Vector projected = projection.getProjected();

        int xadj = this.getWidth()/2;
        int yadj = this.getHeight()/2;

        Stroke s = ((Graphics2D)g).getStroke();
        for(int i=projected.size(); i>0; )
        {
            i--;
            ProjectionMesh mesh = (ProjectionMesh)projected.elementAt(i);
            Vector lines = mesh.lines;
            g.setColor(new Color(mesh.color));
            for(int j=lines.size(); j>0; )
            {
                j--;
                ProjectionLine line = (ProjectionLine)lines.elementAt(j);
                if(line.perimeter)
                {
                    ((Graphics2D)g).setStroke(this.outerStroke);
                }else{
                    ((Graphics2D)g).setStroke(this.innerStroke);
                }
                g.drawLine(line.from[0] + xadj, -line.from[1] + yadj, line.to[0] + xadj, -line.to[1] + yadj);
//                int[] bounds = line.bounds2d;
//                int h = bounds[3] - bounds[1];
//                g.drawRect(bounds[0] + xadj, -bounds[1] + yadj - h, bounds[2] - bounds[0], h);
            }
        }
        ((Graphics2D)g).setStroke(s);

        if(!player.isRemove())
        {
            // render the players health
            Shape shape = player.getWeapon().getProjectile().getShape();
            g.setColor(getColor(shape));

            int width = 15;
            int height = 15;
            int cwx = width/2;
            int cpx = this.getWidth() - width/2 - width%2;
            int cy = this.getHeight() - height/2;

            byte weaponType = ((Ship)this.game.getCurrentPlayer().getWeapon().getProjectile()).getTypeId();
            Image weaponImage = this.images[weaponType];

            g.drawImage(weaponImage,
                    cwx - width/2, cy - height/2, this);
            PlayerController playerController = this.game.currentPlayerController;
            int imagex = cpx;
            for(int i=0; i<playerController.lives.size(); i++)
            {
                Ship life = (Ship)playerController.lives.elementAt(i);
                byte lifeTypeId = life.getTypeId();
                g.drawImage(this.images[lifeTypeId],
                        imagex - width/2, cy - height/2, this);
                imagex -= width;
            }
            int hw = 3;
            int hx = this.getWidth() - (width + hw)/2;
            int px = (width - hw)/2;
            int statusHeight = this.getHeight() - height;
            int powerHeight = (this.game.getCurrentPlayer().getPower() * statusHeight) / (Ship.POWER_PER_UPGRADE * VISIBLE_UNITS);
            int health = this.game.getCurrentPlayer().getHealth() - 1;
            int healthHeight = (health * statusHeight) / VISIBLE_UNITS;
            g.fillRect(px, statusHeight - powerHeight, hw, powerHeight);
            g.setColor(getColor(this.game.getCurrentPlayer().getShape()));
            g.fillRect(hx, statusHeight - healthHeight, hw, healthHeight);
            g.setColor(Color.black);
            for(int i=Math.min(this.game.getCurrentPlayer().getUpgrades(), this.game.getCurrentPlayer().getWeapon().getMaxUpgrades()); i>0; i--)
            {
                int py = statusHeight - (i * statusHeight) / VISIBLE_UNITS;
                g.drawLine(px, py, px + hw, py);
            }
            for(int i=health; i>0; i--)
            {
                int py = statusHeight - (i * statusHeight) / VISIBLE_UNITS;
                g.drawLine(hx, py, hx + hw, py);
            }
        }
    }

    private Color getColor(Shape shape)
    {
        int color;
        if(shape instanceof Composite)
        {
            Composite composite = (Composite)shape;
            if(composite.shapes.size() > 0)
            {
                Mesh mesh = (Mesh)composite.shapes.elementAt(0);
                color = mesh.color;
            }else{
                color = 0;
            }
        }else{
            color = ((Mesh)shape).color;
        }
        Color c = new Color(color);
        return c;
    }

    public void update(Graphics g)
    {
        if(this.bufferedImage == null ||
                this.bufferedImage.getWidth(this) != this.getWidth() ||
                this.bufferedImage.getHeight(this) != this.getHeight())
        {
            this.bufferedImage = this.createImage(this.getWidth(), this.getHeight());
            this.bufferedGraphics = this.bufferedImage.getGraphics();
        }
        render(this.bufferedGraphics);
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(this.bufferedImage != null)
        {
            g.drawImage(this.bufferedImage, 0, 0, this);
        }else{
            g.setColor(Color.black);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    public static final void main(String[] args) throws Exception
    {
        Game game;
        BinaryLevelFactory levelFactory;
        final InvadersCanvas canvas;
//        try
        {
            levelFactory = new BinaryLevelFactory("/");
            game = levelFactory.getGame();
            game.start();

            canvas = new InvadersCanvas(game);

            String[] imageLocations = levelFactory.getImageLocations();
            Image[] images;
            images = new Image[imageLocations.length];
            for(int i=imageLocations.length; i>0; )
            {
                i--;
                String imageLocation = imageLocations[i];
                if(imageLocation != null)
                {
                    URL imageURL = levelFactory.getClass().getResource(imageLocation);
                    images[i] = canvas.getToolkit().getImage(imageURL);
                }
            }
            canvas.images = images;
            canvas.setSize(320, 320);
//        }catch(Exception ex){
//            ex.printStackTrace();
//            System.exit(2);
//            levelFactory = null;
//            canvas = null;
        }

        final Frame frame = new Frame("Invaders");
        frame.add(canvas, BorderLayout.CENTER);
        try
        {
            SwingUtilities.invokeAndWait(new Runnable(){
                public void run()
                {
                    frame.pack();
                    frame.show();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
            System.exit(2);
        }
        frame.addWindowListener( new WindowAdapter() {

            public void windowClosing( WindowEvent e ) {
                System.out.println( e );
                canvas.running = false;
                frame.dispose();
            }
        });

        Thread thread = new Thread(canvas);
        thread.start();

        try
        {
            synchronized(InvadersCanvas.class)
            {
                InvadersCanvas.class.wait();
            }
        }catch(Exception ex){
            // do nothing
        }
    }

    public void run()
    {
        this.running = true;
        this.repaint();
        while(this.running)
        {
            this.game.iterate();
            this.repaint();
        }
    }
}
