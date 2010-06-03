package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 14:39:20
 * <p>
 * Composite shape implementation
 */
public class Shape3dComposite extends Composite
{
    public Shape3dComposite()
    {
        super(3);
    }

    public Shape3dComposite(int size)
    {
        super(size);
    }

    public int calculateMaxRadius()
    {
        int maxRadius = 0;
        for(int i=this.shapes.size(); i>0; )
        {
            i--;
            Shape shape = (Shape)this.shapes.elementAt(i);
            maxRadius = Math.max(maxRadius, shape.getMaxRadius());
        }
        return maxRadius;
    }

    public int getMaxRadius()
    {
        return this.maxradius;
    }

    public void addShape(Shape shape)
    {
        this.shapes.addElement(shape);
        this.maxradius = calculateMaxRadius();
    }
}
