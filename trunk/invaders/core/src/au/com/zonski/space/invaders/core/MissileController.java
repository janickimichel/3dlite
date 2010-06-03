package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.space.domain.Controller;
import au.com.zonski.math.FixedMath;
import au.com.zonski.shape3d.Mesh;
import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 17/05/2005
 * Time: 16:38:27
 */
public class MissileController extends BaseController
{
    private static final int START_SPEED            = MathFP.toFP(0);
    private static final int ACCELERATION           = MathFP.toFP(20);
    private int speed = START_SPEED;

    private static final byte MISSILE_FRAGMENT      = 0;
    private static final int NUM_FRAGMENTS          = 5;
    private static final int TIME                   = 5;

    public void update(Body body, int elapsedTime)
    {
        this.speed = Math.min(((Ship)body).getSpeed(), this.speed + ACCELERATION);
        update((Ship)body, elapsedTime, this.speed);
    }

    public void collision(Body controlled, Body with)
    {
        super.collision(controlled, with);
    }

    protected void explode(Ship body)
    {
        // missiles explode differently
        Game game = body.getLevel().getGame();
        Body fragment = game.getBody(MISSILE_FRAGMENT);
        Mesh mesh = (Mesh)fragment.getShape();
        for(int i=NUM_FRAGMENTS; i>0; )
        {
            i--;
            Ship copy = (Ship)fragment.copy();
            System.arraycopy(body.center, 0, copy.center, 0, body.center.length);
            int angle = MathFP.toFP((i * 360)/NUM_FRAGMENTS);
            copy.rotation[0] = FixedMath.cos(angle/2);
            copy.rotation[2] = FixedMath.sin(angle/2);
            copy.shape = mesh.copy();
            FragmentController fragmentController = (FragmentController)copy.controller;
            fragmentController.init(
                    FixedMath.multiply(FixedMath.cos(angle + MathFP.toFP(90)), copy.getSpeed()),
                    0,
                    FixedMath.multiply(FixedMath.sin(angle + MathFP.toFP(90)), copy.getSpeed()),
                    new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION},
                    TIME, mesh.color
            );
            body.getLevel().addBody(copy, body.getSide());
        }
    }

    public Controller copy()
    {
        MissileController controller = new MissileController();
        return controller;
    }
}
