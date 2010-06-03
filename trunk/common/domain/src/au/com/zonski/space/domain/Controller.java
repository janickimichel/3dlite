package au.com.zonski.space.domain;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 11:20:36
 * <p>
 * Interface for modifying the behavior of an object in space
 */
public interface Controller
{
    /**
     * Updates the body associated with this controller
     * @param body the body to update
     * @param elapsedTime the multiple of the time that was taken for the last move, helps smooth out slow downs
     */
    void update(Body body, int elapsedTime);

    /**
     * Indicates that the two bodies just collided, changes should only be made to the
     * controlled entity
     * @param controlled the entity that is controlled
     * @param with the entity that has been collided with
     */
    void collision(Body controlled, Body with);

    Controller copy();
}
