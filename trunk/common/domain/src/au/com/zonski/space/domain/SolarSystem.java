package au.com.zonski.space.domain;

import au.com.zonski.shape3d.Mesh;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 11:14:01
 */
public class SolarSystem
{
    public int[] galacticCoordinates;
    public Vector bodies;

    public SolarSystem()
    {
        this.bodies = new Vector();
    }

    public void update(int elapsedTime)
    {
        for(int i = this.bodies.size(); i>0; )
        {
            i--;
            Body body = (Body)this.bodies.elementAt(i);
            if(body.controller != null)
            {
                body.update(elapsedTime);
            }
            // check for collisions
            for(int j=this.bodies.size(); j>i+1; )
            {
                j--;
                Body with = (Body)this.bodies.elementAt(j);
                Mesh withMesh = (Mesh)with.shape;
                Mesh bodyMesh = (Mesh)body.shape;
                int withRadius = withMesh.maxradius;
                int bodyRadius = bodyMesh.maxradius;
                int totalRadius = withRadius + bodyRadius;
                // TODO : these collisions are pretty approximate
                if(Math.abs(with.center[0] - body.center[0]) < totalRadius &&
                   Math.abs(with.center[1] - body.center[1]) < totalRadius &&
                   Math.abs(with.center[2] - body.center[2]) < totalRadius)
                {
//                    System.out.println(""+totalRadius+" vs ("+Math.abs(with.center[0] - body.center[0])+","+
//                            Math.abs(with.center[1] - body.center[1])+","+Math.abs(with.center[2] - body.center[2])+
//                            ")"
//                    );
                    if(with.controller != null)
                    {
                        with.controller.collision(with, body);
                    }
                    if(body.controller != null)
                    {
                        body.controller.collision(body, with);
                    }
                }
            }
            if(body.remove)
            {
                this.bodies.removeElementAt(i);
            }
        }
    }
}
