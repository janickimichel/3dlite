package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 20/05/2005
 * Time: 15:40:06
 */
public class TurretController extends FormationController
{
    private static final int MAX_ROTATION = FixedMath.toFixed(5);

    private Body target;

    public TurretController()
    {
    }

    protected void doUpdate(Body body, int multiplier)
    {
        Formation formation = ((Ship)body).getLevel().getFormation();
        int formationAngle = formation.position(body, body.center);
        // change the rotation
        if(this.target != null)
        {
            int[] target = this.target.center;
            int[] center = body.center;
            int dx = target[0] - center[0];
            int dz = target[2] - center[2];
            int angle = FixedMath.atan2(dz, dx, FixedMath.DEFAULT_PRECISION) + FixedMath.ANGLE_90;

            while(angle > FixedMath.ANGLE_180)
            {
                angle -= FixedMath.ANGLE_360;
            }
            //System.out.println("atan2("+FixedMath.getInteger(dz)+","+FixedMath.getInteger(dx)+") = "+FixedMath.getInteger(angle));

            int facing = face(body, angle, MAX_ROTATION);

            if(Math.abs(facing - angle) < MAX_ROTATION)
            {
                Ship ship = (Ship)body;
                if(ship.weapon.canShoot())
                {
                    ship.weapon.shoot(ship, body.rotation);
                    // TODO : multiple shots
                    this.target = null;
                }
            }
        }else{
            // rotate back to rotation angle
            //int angle = FixedMath.asin(rotation[2]) * 2;
            //System.out.println("return angle : "+FixedMath.getInteger(angle));

            int facing = face(body, formationAngle, MAX_ROTATION);
            if((Math.abs(facing - formationAngle) < MAX_ROTATION))
            {
                this.seekingFormation = true;
            }
        }
    }

    protected void preBreakFormation(Body body)
    {
        // pick an object not on this side and shoot at it
        Body target = getRandomEnemy(body);
        if(target != null)
        {
            this.target = target;
        }else{
            this.seekingFormation = true;
        }
    }

    public Controller copy()
    {
        TurretController turret = new TurretController();
        copyInto(turret);
        return turret;
    }
}
