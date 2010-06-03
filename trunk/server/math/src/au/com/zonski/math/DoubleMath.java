package au.com.zonski.math;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 15:03:11
 */
public class DoubleMath extends FixedMath
{

    public static final double getDouble(int f)
    {
        return getDouble(f, DEFAULT_PRECISION);
    }

    public static final double getDouble(int f, int precision)
    {
        return (double)getInteger(f, precision) + (double)getDecimal(f, precision) / (double)(0x01 << precision);
    }

    public static final int toFixed(double d)
    {
        return (int)Math.round(d * (double)DEFAULT_ONE);
    }
}
