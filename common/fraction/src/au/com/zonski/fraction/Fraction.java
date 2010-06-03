package au.com.zonski.fraction;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 23/03/2005
 * Time: 10:22:02
 */
public class Fraction
{
    public int multiplier;
    public int divisor;

    public static final int getLargestDivisor(int a, int b)
    {
        // eulers method (look it up)
        int remainder;
        int big;
        int small;
        if(a > b)
        {
            small = b;
            big = a;
        }else{
            small = a;
            big = b;
        }

        int divisor;
        do
        {
            divisor = small;
            remainder = big % small;
            big = small;
            small = remainder;
        }while(remainder != 0);
        return divisor;
    }

    public Fraction(int multiplier, int divisor)
    {
        this.multiplier = multiplier;
        this.divisor = divisor;
    }

    public void add(Fraction f)
    {
        // a/b + c/d = ad/bd + cb/db = (ad + db)/db
        int divisor = f.divisor * this.divisor;
        int myMultiplier = this.multiplier * f.divisor;
        int fMultiplier = f.multiplier * this.divisor;
        int multiplier = fMultiplier + myMultiplier;

        this.multiplier = multiplier;
        this.divisor = divisor;
        normalize();
    }

    public void subtract(Fraction f)
    {
        int divisor = f.divisor * this.divisor;
        int myMultiplier = this.multiplier * f.divisor;
        int fMultiplier = f.multiplier * this.divisor;
        int multiplier = myMultiplier - fMultiplier;

        this.multiplier = multiplier;
        this.divisor = divisor;
        normalize();
    }

    public void multiply(Fraction f)
    {
        // a/b * c/d = ac/db
        int multiplier = f.multiplier * this.multiplier;
        int divisor = f.divisor * this.divisor;

        this.multiplier = multiplier;
        this.divisor = divisor;
        normalize();
    }

    public void divide(Fraction f)
    {
        // (a/b)/(c/d) = ad/cb
        int multiplier = this.multiplier * f.divisor;
        int divisor = this.divisor * f.multiplier;

        this.multiplier = multiplier;
        this.divisor = divisor;
        normalize();
    }

    public void normalize()
    {
        int largestCommonDivisor = getLargestDivisor(Math.abs(this.multiplier), this.divisor);
        this.multiplier /= largestCommonDivisor;
        this.divisor /= largestCommonDivisor;
    }

    public Fraction copy()
    {
        return new Fraction(this.multiplier, this.divisor);
    }

    public int roundDown()
    {
        return this.multiplier / this.divisor;
    }

    public boolean equals(Object o)
    {
        boolean b;
        if(o.getClass().equals(this.getClass()))
        {
            b = equals((Fraction)o);
        }else{
            b = false;
        }
        return b;
    }

    public boolean equals(Fraction f)
    {
        // assume that it's already normalised
        return f.multiplier == this.multiplier && f.divisor == this.divisor;
    }

    public int hashCode()
    {
        return this.roundDown();
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.multiplier);
        sb.append("/");
        sb.append(this.divisor);
        return sb.toString();
    }
}
