package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;
import au.com.zonski.math.MatrixMath;
import au.com.zonski.shape3d.Mesh;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 9/05/2005
 * Time: 11:04:42
 */
public class FragmentController extends BaseController
{
    private int dx;
    private int dy;
    private int dz;

    private int[] rotation;
    private int time;
    private int r;
    private int g;
    private int b;
    private int maxtime;

    public FragmentController()
    {

    }

    public FragmentController(int dx, int dy, int dz, int[] rotation, int time, int color)
    {
        init(dx, dy, dz, rotation, time, color);
    }

    public void init(int dx, int dy, int dz, int[] rotation, int time, int color)
    {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.rotation = rotation;
        this.time = time;
        this.maxtime = time;
        this.r = (color & 0xFF0000) >> 16;
        this.g = (color & 0x00FF00) >> 8;
        this.b = (color & 0x0000FF) >> 0;
    }

    public void update(Body body, int elapsedTime)
    {
        this.time --;
        if(time < 0)
        {
            body.setRemove(true);
        }else{
            body.center[0] += this.dx;
            body.center[1] += this.dy;
            body.center[2] += this.dz;
            // we don't normalize here since the fragment has a limited lifespan anyway
            // hopefully before it deforms too badly
            MatrixMath.multiplyQuaternion(body.rotation, body.rotation, this.rotation);
            int c = ((this.r * this.time) / this.maxtime) << 16 |
                    ((this.g * this.time) / this.maxtime) << 8 |
                    ((this.b * this.time) / this.maxtime);
            ((Mesh)body.shape).color = c;
        }
    }

    protected void explode(Ship body)
    {
        // do nothing
    }

    public Controller copy()
    {
        return new FragmentController();
    }
}
