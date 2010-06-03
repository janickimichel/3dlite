package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.space.domain.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 17/05/2005
 * Time: 13:41:06
 */
public class UpgradeController extends BaseController
{
    public UpgradeController()
    {
    }

    public void collision(Body controlled, Body with)
    {
        controlled.setRemove(true);
    }

    public Controller copy()
    {
        UpgradeController controller = new UpgradeController();
        return controller;
    }
}
