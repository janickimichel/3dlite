package au.com.zonski.space.client.j2me;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 7/05/2005
 * Time: 17:08:07
 */
public class J2MEProjectionMesh
{
    int[][] points;
    Vector lines;
    int[] bounds2d;
    byte[] hull;
    int hullLength;
    int color;

    public J2MEProjectionMesh()
    {
        this.bounds2d = new int[4];
        this.lines = new Vector(0);
    }

    public void setPoints(int[][] points)
    {
        this.lines.removeAllElements();
        this.lines.ensureCapacity(points.length);

        int index = points.length - 1;
        this.bounds2d[0] = points[index][0];
        this.bounds2d[1] = points[index][1];
        this.bounds2d[2] = points[index][0];
        this.bounds2d[3] = points[index][1];
        for(int i=index; i>0; )
        {
            i--;
            int x = points[i][0];
            int y = points[i][1];
            this.bounds2d[0] = Math.min(this.bounds2d[0], x);
            this.bounds2d[1] = Math.min(this.bounds2d[1], y);
            this.bounds2d[2] = Math.max(this.bounds2d[2], x);
            this.bounds2d[3] = Math.max(this.bounds2d[3], y);
        }
        this.points = points;
    }
}
