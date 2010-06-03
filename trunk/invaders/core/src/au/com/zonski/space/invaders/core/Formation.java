package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/05/2005
 * Time: 08:49:25
 */
public interface Formation
{
    /**
     * Indicates whether the formation can be broken at this point
     * @return whether the formation is breakable
     */
    boolean isBreakable();

    /**
     * Positions the body in formation
     * @param body the body to put into formation
     * @param position the position parameters, may be the bodys center, or something else
     * @return the rotation of the body
     */
    int position(Body body, int[] position);

    /**
     * updates the formation
     * @param multiplier the amount to update the formation by
     */
    void update(int multiplier);

    /**
     * Adds the body to the formation if it's the right kind of body
     * @param body the body to add to the formation
     */
    void add(Body body);

    /**
     * Copies this formation in a neutral state (nothing added or updated)
     * @return the copy
     */
    Formation copy();
}
