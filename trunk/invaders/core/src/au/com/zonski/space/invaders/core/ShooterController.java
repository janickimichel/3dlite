package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.space.domain.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 24/05/2005
 * Time: 08:21:13
 */
public class ShooterController extends FormationController
{
    public void update(Body body, int elapsedTime)
    {
        super.update(body, elapsedTime);
        if(this.inFormation)
        {
            Ship ship = (Ship)body;
            ship.weapon.shoot(ship, ship.rotation);
        }
    }

    public Controller copy()
    {
        ShooterController controller = new ShooterController();
        copyInto(controller);
        return controller;
    }
}
