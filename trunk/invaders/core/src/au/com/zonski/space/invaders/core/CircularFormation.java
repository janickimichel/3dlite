package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/05/2005
 * Time: 09:10:12
 */
public class CircularFormation implements Formation
{
    private int size;
    private int yRotation;

    private int radius;
    private int speed;

    private int centerX;
    private int centerZ;

    private int deltaAngle;

    public CircularFormation()
    {

    }

    public boolean isBreakable()
    {
        return true;
    }

    public int getCenterX()
    {
        return centerX;
    }

    public void setCenterX(int centerX)
    {
        this.centerX = centerX;
    }

    public int getCenterZ()
    {
        return centerZ;
    }

    public void setCenterZ(int centerZ)
    {
        this.centerZ = centerZ;
    }

    public int getRadius()
    {
        return radius;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public int position(Body body, int[] center)
    {
        FormationController formationController = (FormationController)body.controller;
        int index = formationController.formationIndex;
        int angle = this.yRotation + this.deltaAngle * index;
        int dx = FixedMath.multiply(this.radius, FixedMath.cos(angle));
        int dz = FixedMath.multiply(this.radius, FixedMath.sin(angle));
        angle +=  FixedMath.ANGLE_90;
        center[0] = this.centerX + dx;
        center[2] = this.centerZ + dz;
        // rotate the body to face away from the center
        return angle;
    }

    public void update(int multiplier)
    {
        int rotation = (this.yRotation + FixedMath.multiply(this.speed, multiplier)) % FixedMath.ANGLE_360;
        if(rotation > FixedMath.ANGLE_180)
        {
            rotation -= FixedMath.ANGLE_360;
        }
        this.yRotation = rotation;
    }

    public void add(Body body)
    {
        if(body.controller instanceof FormationController)
        {
            FormationController formationController = (FormationController)body.controller;
            formationController.formationIndex = this.size;
            this.size++;
            this.deltaAngle = FixedMath.toFixed(360) / this.size;
        }
    }

    public Formation copy()
    {
        CircularFormation formation = new CircularFormation();
        formation.setSpeed(this.speed);
        formation.setRadius(this.radius);
        formation.centerX = this.centerX;
        formation.centerZ = this.centerZ;
        return formation;
    }
}
