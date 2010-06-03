package au.com.zonski.space.invaders.core;

import au.com.zonski.shape3d.Animation;
import au.com.zonski.math.FixedMath;
import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 13:55:47
 */
public class FlapAnimation implements Animation
{
    private int rotationAngle;
    private int minRotationAngle = MathFP.toFP(-70);
    private int maxRotationAngle = MathFP.toFP(30);
    private int rotationIncrement = MathFP.toFP(5);
    private boolean reverse;

    private int[] rotation = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};

    public FlapAnimation()
    {
    }

    public int getMinRotationAngle()
    {
        return minRotationAngle;
    }

    public void setMinRotationAngle(int minRotationAngle)
    {
        this.minRotationAngle = minRotationAngle;
    }

    public int getMaxRotationAngle()
    {
        return maxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle)
    {
        this.maxRotationAngle = maxRotationAngle;
    }

    public int getRotationIncrement()
    {
        return rotationIncrement;
    }

    public void setRotationIncrement(int rotationIncrement)
    {
        this.rotationIncrement = rotationIncrement;
    }

    public void update(int multiplier)
    {
        int increment;
        if(this.reverse)
        {
            increment = -this.rotationIncrement;
        }else{
            increment = this.rotationIncrement;
        }
        this.rotationAngle += increment;
        if(increment > 0)
        {
            if(this.rotationAngle >= maxRotationAngle)
            {
                this.rotationAngle = this.maxRotationAngle;
                this.reverse = !this.reverse;
            }
        }else{
            if(this.rotationAngle <= minRotationAngle)
            {
                this.rotationAngle = this.minRotationAngle;
                this.reverse = !this.reverse;
            }
        }
        this.rotation[0] = FixedMath.cos(this.rotationAngle/2);
        this.rotation[3] = FixedMath.sin(this.rotationAngle/2);
    }

    public int[] getRotation()
    {
        return this.rotation;
    }
}
