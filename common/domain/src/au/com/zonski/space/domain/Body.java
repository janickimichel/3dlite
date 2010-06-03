package au.com.zonski.space.domain;

import au.com.zonski.shape3d.Shape;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 11:16:34
 */
public class Body
{
    public Shape shape;
    public int[] center;
    public int[] rotation;
    public Controller controller;
    public int[][] rotatedMatrix;
    boolean remove;

    public Body(Shape shape, int[] center, int[] rotation)
    {
        this.shape = shape;
        this.center = center;
        this.rotation = rotation;
    }

    public boolean isRemove()
    {
        return remove;
    }

    public void setRemove(boolean remove)
    {
        this.remove = remove;
    }

    public Shape getShape()
    {
        return shape;
    }

    public void setShape(Shape shape)
    {
        this.shape = shape;
    }

    public Controller getController()
    {
        return controller;
    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    public Body copy()
    {
        Body body = new Body(null, null, null);
        copyInto(body);
        return body;
    }

    public void update(int multiplier)
    {
        if(this.controller != null)
        {
            this.controller.update(this, multiplier);
        }
    }

    protected void copyInto(Body body)
    {
        int[] rotation = new int[this.rotation.length];
        int[] center = new int[this.center.length];
        System.arraycopy(this.rotation, 0, rotation, 0, this.rotation.length);
        System.arraycopy(this.center, 0, center, 0, this.center.length);
        body.rotation = rotation;
        body.center = center;
        body.shape = this.shape;
        if(this.controller != null)
        {
            body.controller = this.controller.copy();
        }
    }
}
