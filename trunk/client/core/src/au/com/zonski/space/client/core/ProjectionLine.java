package au.com.zonski.space.client.core;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 31/05/2005
 * Time: 16:08:45
 */
public class ProjectionLine
{
    public int[] from;
    public int[] to;

    public byte fromIndex;
    public byte toIndex;

    public int[] bounds2d;

    public boolean perimeter;


    public ProjectionLine(ProjectionMesh parent, byte fromIndex, byte toIndex, boolean perimeter)
    {
        this(parent.points[fromIndex], parent.points[toIndex], fromIndex, toIndex, perimeter);
    }

    public ProjectionLine(int[] from, int[] to, byte fromIndex, byte toIndex, boolean perimeter)
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
