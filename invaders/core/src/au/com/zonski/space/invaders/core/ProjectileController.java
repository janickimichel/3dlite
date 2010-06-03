package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.MatrixMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 6/05/2005
 * Time: 17:24:54
 */
public class ProjectileController extends BaseController
{
    public ProjectileController()
    {
    }

    public void collision(Body controlled, Body with)
    {
        Ship controlledShip = (Ship)controlled;
        if(with instanceof Ship)
        {
            int withHealth = ((Ship)with).preCollisionHealth;
            controlledShip.health -= withHealth;
            if(controlledShip.health < 0)
            {
                controlled.setRemove(true);
                explode(controlledShip);
            }else if(controlledShip.health == 0){
                controlled.setRemove(true);
            }
        }else{
            controlled.setRemove(true);
        }
    }

    public Controller copy()
    {
        return this;
    }
}
