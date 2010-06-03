package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 15:30:06
 */
public class Face3d
{
    private byte[] indices;

    public Face3d()
    {
        this.indices = new byte[0];
    }

    public void addIndex(byte index)
    {
        byte[] indices = new byte[this.indices.length+1];
        if(this.indices.length > 0)
        {
            System.arraycopy(this.indices, 0, indices, 0, this.indices.length);
        }
        indices[this.indices.length] = index;
        this.indices = indices;
    }

    public byte[] toArray()
    {
        return this.indices;
    }
}
