package au.com.zonski.space.invaders.core;

import au.com.zonski.math.FixedMath;
import au.com.zonski.math.MatrixMath;
import au.com.zonski.shape3d.Animation;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 10:42:33
 */
public class RotationAnimation implements Animation
{
    public static final int X_AXIS = 1;
    public static final int Y_AXIS = 2;
    public static final int Z_AXIS = 3;

    private static final int DEFAULT_ANGLE = FixedMath.toFixed(1);
    public int[] rotation = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};
    public int[] multiplier = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};

    public RotationAnimation()
    {

    }

    public int[] getMultiplier()
    {
        return multiplier;
    }

    public void setMultiplier(int[] multiplier)
    {
        this.multiplier = multiplier;
    }

    public void update(int multiplier)
    {
        // TODO : scale the rotation for the multiplier
        MatrixMath.multiplyQuaternion(this.rotation, this.rotation, this.multiplier);
        // TODO : do this infrequently
        MatrixMath.normalizeQuaternion(this.rotation);
    }

    public int[] getRotation()
    {
        return this.rotation;
    }

    public void addAxis(int axis)
    {
        int angle = this.DEFAULT_ANGLE;
        if(axis < 0)
        {
            angle = -angle;
            axis = -axis;
        }
        int[] multiplier;
        switch(axis)
        {
            case X_AXIS:
                multiplier = new int[]{FixedMath.cos(angle/2), FixedMath.sin(angle/2), 0, 0, FixedMath.DEFAULT_PRECISION};
                break;
            case Y_AXIS:
                multiplier = new int[]{FixedMath.cos(angle/2), 0, FixedMath.sin(angle/2), 0, FixedMath.DEFAULT_PRECISION};
                break;
            case Z_AXIS:
                multiplier = new int[]{FixedMath.cos(angle/2), 0, 0, FixedMath.sin(angle/2), FixedMath.DEFAULT_PRECISION};
                break;
            default:
                multiplier = null;
                break;
        }
        MatrixMath.multiplyQuaternion(this.multiplier, this.multiplier, multiplier);
    }
}
