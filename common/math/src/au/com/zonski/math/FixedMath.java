package au.com.zonski.math;

import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 27/03/2005
 * Time: 09:51:29
 */
public class FixedMath
{
    public static final int ANGLE_90  = toFixed(90);
    public static final int ANGLE_180 = toFixed(180);
    public static final int ANGLE_360 = toFixed(360);

    public static final int ZERO        = 0;

    public static final int DEFAULT_PRECISION = 12;
    public static final int DEFAULT_ONE = 0x01 << DEFAULT_PRECISION;

    public static final int MAX_PRECISION       = 15;
    public static final int MAX_ONE             = 0x01 << MAX_PRECISION;

    public static final int add(int a, int b)
    {
        //return a + b;
        return MathFP.add(a, b);
    }

    public static final int multiply(int a, int b)
    {
        long result = (((long)a * (long)b) >> DEFAULT_PRECISION);
        //long result = ((long)a * (long)b)/DEFAULT_ONE;
//        checkResult(result, ""+a+"*"+b);
        return (int)result;
    }

    public static final int multiply(int a, int b, int precision)
    {
        return (a * b) >> precision;
        //long result = ((long)a * (long)b) >> precision;
        //checkResult(result, ""+a+"*"+b);
        //return (int)result;
//        return MathFP.mul(a, b);
    }

    public static final int divide(int a, int b)
    {
        long result = (((long)a)*DEFAULT_ONE)/((long)b);
//        checkResult(result, ""+a+"/"+b);
        return (int)result;
//        return MathFP.div(a, b);
    }

    public static final int divide(int a, int b, int precision)
    {
        long result = ((long)a << precision)/(long)b;
        //checkResult(result, ""+a+"/"+b);
        return (int)result;
//        return MathFP.div(a, b);
    }

    public static final int sin(int degrees)
    {
        // obtain the closest index
//        int totalRange = ((INCREMENTS-1) * 4);
//        int index = ((INCREMENTS-1) * degrees)/(90*DEFAULT_ONE);
//        index = index % totalRange;
//        if(index < 0)
//        {
//            index += totalRange;
//        }
//        int sin;
//        if(index < INCREMENTS)
//        {
//            // do nothing, our index is spot on
//            sin = SIN[index];
//        }else if(index >= INCREMENTS && index < totalRange/2){
//            sin = SIN[(totalRange/2) - index];
//        }else if(index >= totalRange/2 && index < (totalRange*3)/4){
//            sin = -SIN[index - totalRange/2];
//        }else{ // if index >= totalRange*3/4 && index < totalRange
//            sin = -SIN[totalRange - index];
//        }
//        return sin;
        return MathFP.sin(multiply(MathFP.PI, degrees)/180);
//        return sin(degrees, DEFAULT_PRECISION);
    }

    public static final int sin(int degrees, int precision)
    {
//        double radians = (Math.PI * degrees) / (double)(180 << precision);
//        System.out.println(""+(degrees/DEFAULT_ONE)+" = "+radians);
//        double sin = Math.sin(radians);
//        return (int)Math.round(sin * (double)(0x01 << precision));
        return adjustPrecision(MathFP.sin(multiply(MathFP.PI, degrees)/180), precision);
    }


    public static final int cos(int degrees, int precision)
    {
//        double radians = (Math.PI * degrees) / (double)(180 << precision);
//        System.out.println(""+(degrees/DEFAULT_ONE)+" = "+radians);
//        double cos = Math.cos(radians);
//        return (int)Math.round(cos * (double)(0x01 << precision));
        return adjustPrecision(MathFP.cos(multiply(MathFP.PI, degrees)/180), precision);
    }

    public static final int cos(int degrees)
    {
        return MathFP.cos(multiply(MathFP.PI, degrees)/180);
//        return cos(degrees, DEFAULT_PRECISION);
    }

    public static final int tan(int degrees)
    {
        return tan(degrees, DEFAULT_PRECISION);
    }

    public static final int tan(int degrees, int precision)
    {
//        double radians = (Math.PI * degrees) / (double)(180 << precision);
//        System.out.println(""+(degrees/DEFAULT_ONE)+" = "+radians);
//        double tan = Math.tan(radians);
//        return (int)Math.round(tan * (double)(0x01 << precision));
        return adjustPrecision(MathFP.tan(multiply(MathFP.PI, degrees)/180), precision);
    }

    private static final int ATAN2_MAX_PRECISION = 128;

    public static final int atan2(int y, int x, int precision)
    {
//        double atan2 = Math.atan2(y, x);
//        return (int)Math.round((atan2 * (double)(0x01 << precision) * 180D) / Math.PI);

        int yi = Math.abs(y);
        int xi = Math.abs(x);
        if(xi < yi / ATAN2_MAX_PRECISION)
        {
            xi = 0;
        }

        int result = adjustPrecision(divide((180 * MathFP.atan2(yi, xi)), MathFP.PI), precision);

        if(x >= 0 && y < 0)
        {
            // adjust the result
            result = -result;
        }else if(x < 0 && y >= 0){
            result = ANGLE_180 - result;
        }else if(x < 0 && y < 0){
            result = -ANGLE_180 + result;
        }
        return result;
    }

    public static final int getInteger(int f)
    {
        int remainder = f % DEFAULT_ONE;
        int adj;
        if(remainder > DEFAULT_ONE / 2)
        {
            adj = 1;
        }else{
            adj = 0;
        }
        //return MathFP.toInt(f);
        //return f / DEFAULT_ONE;
        return (f >> DEFAULT_PRECISION) + adj;
    }

    public static final int getInteger(int f, int precision)
    {
        int one = 0x01 << precision;
        int remainder = f % one;
        int adj;
        if(remainder > one / 2)
        {
            adj = 1;
        }else{
            adj = 0;
        }
        return f >> precision + adj;
        //return MathFP.toInt(f);
    }

    public static final int getDecimal(int f)
    {
        return f % DEFAULT_ONE;
    }

    public static final int getDecimal(int f, int precision)
    {
        return f % (0x01 << precision);
    }

    public static final int toFixed(int i)
    {
        //return i * DEFAULT_ONE;
        return MathFP.toFP(i);
    }


    public static final int toFixed(int i, int precision)
    {
        return i << precision;
        //return MathFP.toFP(i);
    }

    public static final int toFixed(int i, int d, int precision)
    {
        return (i << precision) + d;
        //return MathFP.toFP(i);
    }

    private static final void checkResult(long result)
    {
        checkResult(result, "");
    }

    private static final void checkResult(long result, String message)
    {
        if(result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
        {
            throw new Error("over/underflow : "+message+" = "+result);
        }
    }

    public static final int sqrt(int f, int p)
    {
        int diff = p - DEFAULT_PRECISION;
        return sqrt(f >> diff) << diff;
    }

    public static final int sqrt(int f)
    {
        return MathFP.sqrt(f);
    }

    public static final int adjustPrecision(int f, int to)
    {
        return adjustPrecision(f, DEFAULT_PRECISION, to);
    }

    public static final int adjustPrecision(int f, int from, int to)
    {
        int diff = from - to;
        int result;
        if(diff > 0)
        {
            result = f >> diff;
        }else if(diff < 0){
            result = f << -diff;
        }else{
            result = f;
        }
        return result;
    }

    public static final int asin(int f)
    {
        return divide(180 * MathFP.asin(f), MathFP.PI);
    }

    public static final int acos(int f)
    {
        return divide(180 * MathFP.acos(f), MathFP.PI);
    }
}
