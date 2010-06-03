package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 10:50:50
 */
public class Mesh implements Shape
{
    /**
     * the base matrix for this mesh
     */
    public int[][] baseMatrix;

    /**
     * index of points of this matrix that make a face, typically each entry will only have three
     * values, however non-triangular faces are supported
     */
    public byte[][] faces;

    /**
     * The maximum radius around the point (0, 0, 0) that this mesh has at
     * any possible angle of rotation
     */
    public int maxradius;

    /**
     * The color of the mesh
     */
    public int color = 0x0FFFFFF;

    /**
     * bit map indicating the visibility of the faces of the mesh, faces over
     * 32 are all visible 0 = visible, 1 = invisible
     */
    public int visibility;

    public Mesh(int[][] baseMatrix, byte[][] faces)
    {
        this.baseMatrix = baseMatrix;
        this.faces = faces;
    }

    public int getMaxRadius()
    {
        return this.maxradius;
    }

    public Mesh copy()
    {
        int[][] baseMatrix = new int[this.baseMatrix.length][];
        for(int i=this.baseMatrix.length; i>0; )
        {
            i--;
            int length = this.baseMatrix[i].length;
            baseMatrix[i] = new int[length];
            System.arraycopy(this.baseMatrix[i], 0, baseMatrix[i], 0, length);
        }
        byte[][] faces = new byte[this.faces.length][];
        for(int i=this.faces.length; i>0; )
        {
            i--;
            int length = this.faces[i].length;
            faces[i] = new byte[length];
            System.arraycopy(this.faces[i], 0, faces[i], 0, length);
        }
        Mesh mesh = new Mesh(baseMatrix, faces);
        mesh.maxradius = this.maxradius;
        mesh.color = this.color;
        return mesh;
    }
}
