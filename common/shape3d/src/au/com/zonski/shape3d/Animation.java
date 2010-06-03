package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 10:40:11
 * <p>
 * Simple animation class for animating meshes
 */
public interface Animation
{
    /**
     * Updates the animation
     * @param multiplier
     */
    void update(int multiplier);

    /**
     * Obtains a quaternion for rotation of a mesh
     * @return the rotation quaternion
     */
    int[] getRotation();
}
