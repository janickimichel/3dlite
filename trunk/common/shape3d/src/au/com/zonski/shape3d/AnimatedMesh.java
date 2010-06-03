package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 10:40:02
 */
public class AnimatedMesh extends Mesh implements Shape
{
    public Animation animation;

    public AnimatedMesh(int[][] baseMatrix, byte[][] faces)
    {
        super(baseMatrix, faces);
    }
}
