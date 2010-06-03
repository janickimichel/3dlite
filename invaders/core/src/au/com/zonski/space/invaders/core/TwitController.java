package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.math.FixedMath;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 9/05/2005
 * Time: 09:08:08
 */
public class TwitController extends FormationController implements Controller
{
    private boolean left;
    private static final int SHOOT_PERCENT = 2;
    private static final int MAX_SHOTS = 4;
    private int shots;

    public TwitController()
    {
    }

    public boolean isLeft()
    {
        return left;
    }

    public void setLeft(boolean left)
    {
        this.left = left;
    }

    protected void doUpdate(Body body, int elapsedTime)
    {
        Ship ship = (Ship)body;
        int dx;
        int dz;
        int speed = FixedMath.multiply(elapsedTime, ship.speed);
        if(this.left)
        {
            dx = -speed;
        }else{
            dx = speed;
        }
        int x = ship.center[0];
        Level currentLevel = ship.level;
        int radius = ship.shape.getMaxRadius();
        if(x + dx < currentLevel.getMinx() + radius)
        {
            dx = (currentLevel.getMinx() + radius) - (x + dx);
            dz = speed;
            this.left = !this.left;
        }else if(x + dx > currentLevel.getMaxx() - radius){
            dx = (currentLevel.getMaxx() - radius) - (x + dx);
            dz = speed;
            this.left = !this.left;
        }else{
            dz = 0;
        }
        body.center[0] += dx;
        body.center[2] += dz;

        if(ship.weapon != null && Math.abs(RANDOM.nextInt() % 100) < SHOOT_PERCENT)
        {
            ship.weapon.shoot(ship, ship.rotation);
            this.shots --;
            if(this.shots <= 0)
            {
                this.seekingFormation = true;
            }
        }
    }

    protected void preBreakFormation(Body body)
    {
        //body.rotation = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};
        body.rotation[0] = FixedMath.cos(FixedMath.toFixed(180)/2);
        body.rotation[1] = 0;
        body.rotation[2] = FixedMath.sin(FixedMath.toFixed(180)/2);
        body.rotation[3] = 0;
        this.shots = MAX_SHOTS;
    }

    public Controller copy()
    {
        TwitController twit = new TwitController();
        this.copyInto(twit);
        return twit;
    }
}
