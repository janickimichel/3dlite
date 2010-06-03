package au.com.zonski.math;

import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 09:18:23
 */
public class MatrixMath
{
    public static final void rotate(int[][] target, int[][] matrix, int[][] working, int ax, int ay, int az)
    {
        int sinax = FixedMath.sin(ax);
        int cosax = FixedMath.cos(ax);
        int sinay = FixedMath.sin(ay);
        int cosay = FixedMath.cos(ay);
        int sinaz = FixedMath.sin(az);
        int cosaz = FixedMath.cos(az);

        for(int i=0; i<working.length; i++)
        {
            for(int j=0; j<working[i].length; j++)
            {
                if(j==i)
                {
                    working[i][j] = FixedMath.DEFAULT_ONE;
                }else{
                    working[i][j] = 0;
                }
            }
        }
        /*
        working[0][0] = FixedMath.multiply(sinax, FixedMath.multiply(sinay, sinaz)) + FixedMath.multiply(cosaz, cosay);
        working[0][1] = FixedMath.multiply(sinaz, cosax);
        working[0][2] = FixedMath.multiply(sinaz, FixedMath.multiply(sinax, cosay)) - FixedMath.multiply(cosaz, sinay);
        working[1][0] = FixedMath.multiply(cosaz, FixedMath.multiply(sinax, sinay)) - FixedMath.multiply(cosay, sinaz);
        working[1][1] = FixedMath.multiply(cosaz, cosax);
        working[1][2] = FixedMath.multiply(cosaz, FixedMath.multiply(sinax, cosay)) + FixedMath.multiply(sinaz, sinay);
        working[2][0] = FixedMath.multiply(cosax, sinay);
        working[2][1] = -sinax;
        working[2][2] = FixedMath.multiply(cosax, cosay);
        */

        working[0][0] = MathFP.mul(cosaz, cosay);
        working[0][1] =
                MathFP.mul(cosaz, FixedMath.multiply(sinay, sinax)) +
                MathFP.mul(sinaz, cosax);
        working[0][2] =
                -MathFP.mul(cosax, MathFP.mul(cosaz, sinay)) +
                MathFP.mul(sinaz, sinax);
        working[1][0] = -MathFP.mul(cosay, sinaz);
        working[1][1] =
                -MathFP.mul(sinaz, MathFP.mul(sinay, sinax)) +
                MathFP.mul(cosaz, cosax);
        working[1][2] =
                MathFP.mul(cosax, MathFP.mul(sinaz, sinay)) +
                MathFP.mul(cosaz, sinax);

        working[2][0] = sinay;
        working[2][1] = -MathFP.mul(cosay, sinax);
        working[2][2] = MathFP.mul(cosay, cosax);

        multiply(target, matrix, working);
    }

    public static final void translate(int[][] matrix, int[][] working, int x, int y, int z)
    {
        for(int row = working.length; row > 0; )
        {
            row --;
            working[row][0] = x;
            working[row][1] = y;
            working[row][2] = z;
            for(int col = working[row].length; col > 3; )
            {
                col --;
                working[row][col] = 0;
            }
        }
        add(matrix, working, matrix);
    }

    public static final int[][] add(int[][] m1, int[][] m2)
    {
        checkAdd(m1, m2);
        int[][] target = new int[m1.length][m1[0].length];
        uncheckedAdd(target, m1, m2);
        return target;
    }

    public static final int[][] add(int[][] target, int[][] m1, int[][] m2)
    {
        checkAdd(m1, m2);
        checkAdd(target, m1);
        uncheckedAdd(target, m1, m2);
        return target;
    }

    private static final void uncheckedAdd(int[][] target, int[][] m1, int[][] m2)
    {
        for(int row=m1.length; row>0; )
        {
            row--;
            for(int col=m1[row].length; col>0; )
            {
                col--;
                target[row][col] = m1[row][col] + m2[row][col];
            }
        }
    }

    private static final void checkAdd(int[][] m1, int[][] m2)
    {
        if(m1.length != m2.length)
        {
            throw new IllegalArgumentException("unable to add matrices, m1.rows != m2.rows");
        }
        if(m1[0].length != m2[0].length)
        {
            throw new IllegalArgumentException("unable to add matrices, m1.cols != m2.cols");
        }
    }

    public static final int[][] multiply(int[][] m1, int[][] m2)
    {
        checkMultiply(m1, m2);
        int[][] target = new int[m1.length][m2[0].length];
        uncheckedMultiply(target, m1, m2);
        return target;
    }

    public static final int[][] multiply(int[][] target, int[][] m1, int[][] m2)
    {
        checkMultiply(m1, m2);
        uncheckedMultiply(target, m1, m2);
        return target;
    }

    public static final int[] multiplyVector(int[] result, int[] vector, int[][] m)
    {
        int x = MathFP.mul(vector[0], m[0][0]) + MathFP.mul(vector[1], m[1][0]) + MathFP.mul(vector[2], m[2][0]);
        int y = MathFP.mul(vector[0], m[0][1]) + MathFP.mul(vector[1], m[1][1]) + MathFP.mul(vector[2], m[2][1]);
        int z = MathFP.mul(vector[0], m[0][2]) + MathFP.mul(vector[1], m[1][2]) + MathFP.mul(vector[2], m[2][2]);
        result[0] = x;
        result[1] = y;
        result[2] = z;
        return result;
    }

    private static final void uncheckedMultiply(int[][] target, int[][] m1, int[][] m2)
    {
//        System.out.println(toString(m1));
//        System.out.println("*");
//        System.out.println(toString(m2));

        int rows = target.length;
        int cols = target[0].length;
        int n = m2.length;
        for(int row = rows; row>0; )
        {
            row --;
            for(int col = cols; col>0; )
            {
                col--;
                int result = FixedMath.ZERO;
                for(int k = n; k>0; )
                {
                    k--;
                    result = result + MathFP.mul(m1[row][k], m2[k][col]);
                }
                target[row][col] = result;
            }
        }
    }

    private static final void checkMultiply(int[][] m1, int[][] m2)
    {
        if(m1[0].length != m2.length)
        {
            throw new IllegalArgumentException("cannot multiply matrices, m1.columns ("+m1[0].length+") != m2.rows ("+m2.length+")");
        }
    }

    public static final String toString(int[][] m)
    {
        StringBuffer sb = new StringBuffer(m.length * 4 * 5);
        for(int row = 0; row<m.length; row++)
        {
            sb.append("[");
            int[] v = m[row];
            fillString(v, sb);
            sb.append("]\n");
        }
        return sb.toString();
    }

    public static final void fillString(int[] q, StringBuffer sb)
    {
        for(int i=0; i<q.length; i++)
        {
            sb.append(q[i]);
            if(i < q.length-1)
            {
                sb.append(",");
            }
        }
    }

    public static final String toString(int[] q)
    {
        StringBuffer sb = new StringBuffer(q.length*4);
        sb.append("<");
        fillString(q, sb);
        sb.append(">");
        return sb.toString();
    }

    public static final void multiplyQuaternion(int[] result, int[] q1, int[] q2)
    {
        int w1 = q1[0];
        int x1 = q1[1];
        int y1 = q1[2];
        int z1 = q1[3];
        int p1 = q1[4];

        int w2 = q2[0];
        int x2 = q2[1];
        int y2 = q2[2];
        int z2 = q2[3];
        int p2 = q2[4];

        if(p1 != p2)
        {
            throw new IllegalArgumentException("expected same precision on quaternions "+p1+" != "+p2);
        }

//        System.out.print(toString(q1)+"*"+toString(q2));
        result[0] = FixedMath.multiply(w1,w2,p1) -
                FixedMath.multiply(x1,x2,p1) -
                FixedMath.multiply(y1,y2,p1) -
                FixedMath.multiply(z1,z2,p1);
        result[1] = FixedMath.multiply(w1,x2,p1) +
                FixedMath.multiply(x1,w2,p1) +
                FixedMath.multiply(y1,z2,p1) -
                FixedMath.multiply(z1,y2,p1);
        result[2] = FixedMath.multiply(w1,y2,p1) -
                FixedMath.multiply(x1,z2,p1) +
                FixedMath.multiply(y1,w2,p1) +
                FixedMath.multiply(z1,x2,p1);
        result[3] = FixedMath.multiply(w1,z2,p1) +
                FixedMath.multiply(x1,y2,p1) -
                FixedMath.multiply(y1,x2,p1) +
                FixedMath.multiply(z1,w2,p1);
        result[4] = p1;
//        System.out.println("="+toString(result));
    }

    public static final void normalizeQuaternion(int[] quaternion)
    {
        int w = quaternion[0];
        int x = quaternion[1];
        int y = quaternion[2];
        int z = quaternion[3];
        int p = quaternion[4];

        int length_sq =
                FixedMath.multiply(w, w, p) +
                FixedMath.multiply(x, x, p) +
                FixedMath.multiply(y, y, p) +
                FixedMath.multiply(z, z, p);
        int length = FixedMath.sqrt(length_sq, p);
        quaternion[0] = FixedMath.divide(w, length, p);
        quaternion[1] = FixedMath.divide(x, length, p);
        quaternion[2] = FixedMath.divide(y, length, p);
        quaternion[3] = FixedMath.divide(z, length, p);
        //quaternion[4] = p;
    }

    public static final void adjustPrecision(int[] result, int[] q, int precision)
    {
        int w = q[0];
        int x = q[1];
        int y = q[2];
        int z = q[3];
        int p = q[4];

        int diff = p - precision;

        if(diff > 0)
        {
            result[0] = w >> diff;
            result[1] = x >> diff;
            result[2] = y >> diff;
            result[3] = z >> diff;
        }else{
            result[0] = w << -diff;
            result[1] = x << -diff;
            result[2] = y << -diff;
            result[3] = z << -diff;
        }
        result[4] = precision;
    }

    public static final void getRotationMatrix(int[][] result, int[] q)
    {
        int rw = q[0];
        int rx = q[1];
        int ry = q[2];
        int rz = q[3];
        int rp = q[4];

        int one = 0x01 << rp;

        result[0][0] = one-2*FixedMath.multiply(ry,ry,rp)-2*FixedMath.multiply(rz,rz,rp);
        result[0][1] = 2*FixedMath.multiply(rx,ry,rp)-2*FixedMath.multiply(rw,rz,rp);
        result[0][2] = 2*FixedMath.multiply(rx,rz,rp)+2*FixedMath.multiply(rw,ry,rp);
        result[1][0] = 2*FixedMath.multiply(rx,ry,rp)+2*FixedMath.multiply(rw,rz,rp);
        result[1][1] = one-2*FixedMath.multiply(rx,rx,rp)-2*FixedMath.multiply(rz,rz,rp);
        result[1][2] = 2*FixedMath.multiply(ry,rz,rp)-2*FixedMath.multiply(rw,rx,rp);
        result[2][0] = 2*FixedMath.multiply(rx,rz,rp)-2*FixedMath.multiply(rw,ry,rp);
        result[2][1] = 2*FixedMath.multiply(ry,rz,rp)+2*FixedMath.multiply(rw,rx,rp);
        result[2][2] = one-2*FixedMath.multiply(rx,rx,rp)-2*FixedMath.multiply(ry,ry,rp);
    }
}
