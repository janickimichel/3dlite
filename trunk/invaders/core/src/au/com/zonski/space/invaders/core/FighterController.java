package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.space.domain.Controller;
import au.com.zonski.math.FixedMath;
import au.com.zonski.math.MatrixMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 19/05/2005
 * Time: 11:03:15
 */
public class FighterController extends FormationController
{
    private static final int MAX_TURN_ANGLE = FixedMath.toFixed(10);

    protected int[] seeking;

    public FighterController()
    {

    }

    protected void doUpdate(Body body, int multiplier)
    {
        seek(body, multiplier, this.seeking);
        Ship ship = (Ship)body;
        ship.weapon.shoot(ship, ship.rotation);
        if(body.center[0] == this.seeking[0] && body.center[2] == this.seeking[2])
        {
            this.seekingFormation = true;
        }
//        super.doUpdate(body, multiplier);
    }

    protected void preBreakFormation(Body body)
    {
        Ship ship = (Ship)body;

        Body enemy = getRandomEnemy(body);
        if(enemy != null)
        {
            int x = enemy.center[0];

            this.seeking = new int[]{x, 0, -ship.getLevel().getHeight()/5};
            super.preBreakFormation(body);
        }else{
            this.seekingFormation = true;
        }
    }

    protected void seek(Body body, int multiplier, int[] target)
    {
        Ship ship = (Ship)body;
        // ignore the rotation
        // seek out the specified point
        int[] center = body.center;
        int dx = target[0] - center[0];
        int dz = target[2] - center[2];
        int speed = FixedMath.multiply(ship.getSpeed(), multiplier);

        if(Math.abs(dx) < speed && Math.abs(dz) < speed)
        {
            //speed = Math.max(Math.abs(dx), Math.abs(dz));
            System.arraycopy(target, 0, center, 0, center.length);
        }else{
            try
            {
                int targetAngle = FixedMath.atan2(dz, dx, FixedMath.DEFAULT_PRECISION) + FixedMath.ANGLE_90;
                face(body, targetAngle, MAX_TURN_ANGLE);
                // rotate the speed
                int[] p = new int[]{0, 0, -speed};
                int[][] rotationMatrix = new int[3][3];
                MatrixMath.getRotationMatrix(rotationMatrix, body.rotation);
                MatrixMath.multiplyVector(p, p, rotationMatrix);

                center[0] += p[0];
                center[2] += p[2];
            }catch(Exception ex){
                //System.out.println("atan2("+dz+"("+FixedMath.getInteger(dz)+"),"+dx+"("+FixedMath.getInteger(dx)+"))");
                ex.printStackTrace();
            }
        }
    }

    public Controller copy()
    {
        FighterController copy = new FighterController();
        copyInto(copy);
        return copy;
    }
}
