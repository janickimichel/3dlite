package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 23/05/2005
 * Time: 08:52:37
 */
public class BomberController extends FighterController
{
    public BomberController()
    {

    }

    protected void doUpdate(Body body, int multiplier)
    {
        seek(body, multiplier, this.seeking);
        if(body.center[2] > this.seeking[2] - body.shape.getMaxRadius())
        {
            this.seekingFormation = true;
        }
    }

    protected void preBreakFormation(Body body)
    {
        Body enemy = getRandomEnemy(body);
        if(enemy != null)
        {
            this.seeking = enemy.center;
        }else{
            this.seekingFormation = true;
        }
    }

    public Controller copy()
    {
        BomberController controller = new BomberController();
        copyInto(controller);
        return controller;
    }
}
