package au.com.zonski.space.client.j2me;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 7/05/2005
 * Time: 17:07:16
 */
public class J2MEProjectionLine
{
    int[] from;
    int[] to;

    byte fromIndex;
    byte toIndex;

    int[] bounds2d;

    boolean perimeter;


    public J2MEProjectionLine(J2MEProjectionMesh parent, byte fromIndex, byte toIndex, boolean perimeter)
    {
        this(parent.points[fromIndex], parent.points[toIndex], fromIndex, toIndex, perimeter);
    }

    public J2MEProjectionLine(int[] from, int[] to, byte fromIndex, byte toIndex, boolean perimeter)
    {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.from = from;
        this.to = to;
        this.perimeter = perimeter;
        reload();
    }

    public void reload()
    {
        this.bounds2d = new int[]{
            Math.min(from[0], to[0]),
            Math.min(from[1], to[1]),
            Math.max(from[0], to[0]),
            Math.max(from[1], to[1])
        };
    }

    public String toString()
    {
        return "("+
                (this.from[0])+","+
                (this.from[1])+","+
                (this.from[2])+
            ")->("+
                (this.to[0])+","+
                (this.to[1])+","+
                (this.to[2])+")";
    }
}
