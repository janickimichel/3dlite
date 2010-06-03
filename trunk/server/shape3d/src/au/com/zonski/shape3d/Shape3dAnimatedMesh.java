package au.com.zonski.shape3d;

import au.com.zonski.math.DoubleMath;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 10:45:50
 */
public class Shape3dAnimatedMesh extends AnimatedMesh
{
    public Shape3dAnimatedMesh()
    {
        super(new int[0][0], new byte[0][0]);
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    public void setAnimation(Animation animation)
    {
        this.animation = animation;
    }

    public int getMaxRadius()
    {
        return this.maxradius;
    }

    public Color getColor()
    {
        return new Color(this.color);
    }

    public void setColor(Color color)
    {
        this.color = color.getRGB();
    }

    public void addPoint(Point3d p)
    {
        int[] point = p.toArray();
        int[][] points = new int[this.baseMatrix.length+1][];
        if(this.baseMatrix.length > 0)
        {
            System.arraycopy(this.baseMatrix, 0, points, 0, this.baseMatrix.length);
        }
        points[this.baseMatrix.length] = point;
        this.baseMatrix = points;
        this.maxradius = calculateMaxRadius();
    }

    public void addFace(Face3d f)
    {
        byte[] face = f.toArray();
        byte[][] faces = new byte[this.faces.length+1][];
        if(this.faces.length > 0)
        {
            System.arraycopy(this.faces, 0, faces, 0, this.faces.length);
        }
        faces[this.faces.length] = face;
        this.faces = faces;
    }

    private int calculateMaxRadius()
    {
        int maxradius = 0;
        for(int row = 0; row < this.baseMatrix.length; row ++)
        {
            double x = DoubleMath.getDouble(this.baseMatrix[row][0]);
            double y = DoubleMath.getDouble(this.baseMatrix[row][1]);
            double z = DoubleMath.getDouble(this.baseMatrix[row][2]);
            int length = DoubleMath.toFixed(Math.sqrt(x*x + y*y + z*z));
            maxradius = Math.max(maxradius, length);
        }
        return maxradius;
    }
}
