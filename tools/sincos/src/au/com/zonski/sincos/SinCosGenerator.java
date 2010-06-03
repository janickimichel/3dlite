package au.com.zonski.sincos;

import au.com.zonski.math.FixedMath;

import java.io.PrintStream;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 27/03/2005
 * Time: 09:21:39
 */
public class SinCosGenerator
{
    public static final void main(String[] args)
    {
        int increments;
        if(args.length < 1)
        {
            System.err.println("expected number of increments as argument");
            return;
        }else{
            try
            {
                increments = Integer.parseInt(args[0]);
            }catch(Exception ex){
                System.err.println("error parsing "+args[0]);
                return;
            }
        }
        PrintStream outs;
        if(args.length == 2)
        {
            try
            {
                outs = new PrintStream(new FileOutputStream(args[1]));
            }catch(Exception ex){
                System.err.println("error opening file "+args[1]);
                ex.printStackTrace(System.err);
                return;
            }
        }else{
            outs = System.out;
        }
        int[] fsin = new int[increments];
        int[] fcos = new int[increments];
        for(int angleIndex = 0; angleIndex < increments; angleIndex++)
        {
            double angle = ((Math.PI * (double)angleIndex)) / ((double)((increments-1)*2));
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            fsin[angleIndex] = toFixed(sin);
            fcos[angleIndex] = toFixed(cos);
        }
        outs.println("public static final int INCREMENTS = "+increments+";");
        outs.print("public static final int[] SIN = new int[]{");
        for(int i=0; i<fsin.length; i++)
        {
            if(i%5 == 0)
            {
                outs.println();
            }
            outs.print(fsin[i]);
            if(i < fsin.length - 1)
            {
                outs.print(", ");
            }
        }
        outs.println("};");
        outs.print("public static final int[] COS = new int[]{");
        for(int i=0; i<fcos.length; i++)
        {
            if(i%5 == 0)
            {
                outs.println();
            }
            outs.print(fcos[i]);
            if(i < fcos.length - 1)
            {
                outs.print(",");
            }
        }
        outs.println("};");
    }

    private static final int toFixed(double d)
    {
        return (int)Math.round(d * ((double)(FixedMath.DEFAULT_ONE)));
    }
}
