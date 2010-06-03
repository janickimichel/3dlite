package au.com.zonski.space.client.core;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 31/05/2005
 * Time: 16:09:39
 */
public class ProjectionMesh
{
    public int[][] points;
    public Vector lines;
    public int[] bounds2d;
    public byte[] hull;
    public int hullLength;
    public int color;

    public ProjectionMesh(int[][] points)
    {
        this.points = points;
        this.lines = new Vector(points.length);
        int index = points.length - 1;
        this.bounds2d = new int[]{points[index][0], points[index][1], points[index][0], points[index][1]};
        for(int i=points.length-1; i>0; )
        {
            i--;
            int x = points[i][0];
            int y = points[i][1];
            this.bounds2d[0] = Math.min(this.bounds2d[0], x);
            this.bounds2d[1] = Math.min(this.bounds2d[1], y);
            this.bounds2d[2] = Math.max(this.bounds2d[2], x);
            this.bounds2d[3] = Math.max(this.bounds2d[3], y);
        }
    }
}
