package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.shape3d.*;
import au.com.zonski.math.FixedMath;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 08:35:20
 * <p>
 * Class describing a level
 */
public class Level
{
    private String name;
    private String nextLevelId;
    private Vector[] bodies;
    private Vector animations;
    private Game game;
    private Formation formation;
    private int maxActive;
    private Vector active;

    public int minx;
    public int maxx;
    public int height;

    public Level()
    {
        this.animations = new Vector();
    }

    public Formation getFormation()
    {
        return formation;
    }

    public void setFormation(Formation formation)
    {
        this.formation = formation;
    }

    /**
     * Obtains the maximum number of active baddies that there can be on this level
     * @return the maximum number of active baddies
     */
    public int getMaxActive()
    {
        return maxActive;
    }

    public void setMaxActive(int maxActive)
    {
        this.maxActive = maxActive;
        this.active = new Vector(maxActive);
    }

    public void activate(Body body)
    {
        if(!this.active.contains(body))
        {
            this.active.addElement(body);
        }
    }

    public void deactivate(Body body)
    {
        this.active.removeElement(body);
    }

    public boolean canActivate(Body body)
    {
        return this.active.size() < this.maxActive;
    }

    public int getNumSides()
    {
        return this.bodies.length;
    }

    public void setNumSides(int numSides)
    {
        this.bodies = new Vector[numSides];
        for(int i=numSides; i>0; )
        {
            i--;
            this.bodies[i] = new Vector();
        }
    }

    public int getWidth()
    {
        return this.maxx - minx;
    }

    public void setWidth(int width)
    {
        this.minx = -width/2;
        this.maxx = width/2;
    }

    public int getMinx()
    {
        return minx;
    }

    public void setMinx(int minx)
    {
        this.minx = minx;
    }

    public int getMaxx()
    {
        return maxx;
    }

    public void setMaxx(int maxx)
    {
        this.maxx = maxx;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNextLevelId()
    {
        return nextLevelId;
    }

    public void setNextLevelId(String nextLevelId)
    {
        this.nextLevelId = nextLevelId;
    }

    public void addBody(Body body, byte side)
    {
        if(body instanceof Ship)
        {
            ((Ship)body).setLevel(this);
            ((Ship)body).setSide(side);
        }
        this.bodies[side].addElement(body);
        this.formation.add(body);
        addAnimation(body.shape);
    }

    public void addBody(Body body, byte side,
                        int cx, int cy, int cz,
                        int rotationZ)
    {
        Body copy = body.copy();
        if(copy instanceof Ship)
        {
            ((Ship)copy).setLevel(this);
            ((Ship)copy).setSide(side);
        }
        copy.center[0] = cx;
        copy.center[1] = cy;
        copy.center[2] = cz;

        copy.rotation[0] = FixedMath.cos(rotationZ/2);
        copy.rotation[2] = FixedMath.sin(rotationZ/2);

        this.bodies[side].addElement(copy);
        this.formation.add(copy);
        addAnimation(copy.shape);
    }

    private void addAnimation(Shape shape)
    {
        if(shape instanceof AnimatedMesh)
        {
            AnimatedMesh animatedMesh = (AnimatedMesh)shape;
            if(!this.animations.contains(animatedMesh.animation))
            {
                this.animations.addElement(animatedMesh.animation);
            }
        }else if(shape instanceof Composite){
            Composite composite = (Composite)shape;
            for(int i=composite.shapes.size(); i>0; )
            {
                i--;
                Shape child = (Shape)composite.shapes.elementAt(i);
                addAnimation(child);
            }
        }
    }

    public Vector[] getBodies()
    {
        return this.bodies;
    }

    public int update(int multiplier)
    {
        int liveSides = 0;
        if(this.formation != null)
        {
            this.formation.update(multiplier);
        }
        for(int i = this.bodies.length; i>0; )
        {
            i--;
            Vector sideBodies = this.bodies[i];
            if(sideBodies.size() > 0)
            {
                if(i != Game.NEUTRAL_SIDE)
                {
                    liveSides++;
                }
                for(int j=sideBodies.size(); j>0; )
                {
                    j--;
                    Body body = (Body)sideBodies.elementAt(j);
                    body.update(multiplier);

                    // check for collisions
                    // the 0th side is never checked for collisions!!
                    if(i != 0)
                    {
                        // a side is never checked against itself for collisions
                        for(int k=this.bodies.length; k>(i+1); )
                        {
                            k--;
                            Vector collisionBodies = this.bodies[k];
                            for(int l=collisionBodies.size(); l>0; )
                            {
                                l--;
                                Body with = (Body)collisionBodies.elementAt(l);
                                int withRadius = with.shape.getMaxRadius();
                                int bodyRadius = body.shape.getMaxRadius();
                                int totalRadius = withRadius + bodyRadius;
                                // TODO : these collisions are pretty approximate, what's more
                                // all our collisions happen on a two dimensional axis (sort of)
                                if(Math.abs(with.center[0] - body.center[0]) < totalRadius &&
                                   Math.abs(with.center[1] - body.center[1]) < totalRadius &&
                                   Math.abs(with.center[2] - body.center[2]) < totalRadius)
                                {
                                    if(with instanceof Ship)
                                    {
                                        Ship withShip = (Ship)with;
                                        withShip.preCollisionHealth = withShip.health;
                                    }
                                    if(body instanceof Ship)
                                    {
                                        Ship bodyShip = (Ship)body;
                                        bodyShip.preCollisionHealth = bodyShip.health;
                                    }
                                    if(with.controller != null)
                                    {
                                        with.controller.collision(with, body);
                                    }
                                    if(body.controller != null)
                                    {
                                        body.controller.collision(body, with);
                                    }
                                }
                                if(with.isRemove())
                                {
                                    collisionBodies.removeElementAt(l);
                                    deactivate(with);
                                }
                            }
                        }
                    }
                    if(body.isRemove())
                    {
                        sideBodies.removeElementAt(j);
                        deactivate(body);
                    }
                }
            }
        }
        for(int i=this.animations.size(); i>0; )
        {
            i--;
            Animation animation = (Animation)this.animations.elementAt(i);
            animation.update(multiplier);
        }
        return liveSides;
    }

    public Level copy()
    {
        Level level = new Level();
        level.animations.ensureCapacity(this.animations.size());
        level.setMaxActive(this.maxActive);
        level.setFormation(this.formation.copy());
        level.setMinx(this.minx);
        level.setMaxx(this.maxx);
        level.setHeight(this.height);
        for(int i=0; i<this.animations.size(); i++)
        {
            Animation animation = (Animation)this.animations.elementAt(i);
            // TODO : copying the animation might be a good idea
            level.animations.addElement(animation);
        }
        level.setNumSides(this.bodies.length);
        for(int i=0; i<this.bodies.length; i++)
        {
            Vector sideBodies = this.bodies[i];
            for(int j=0; j<sideBodies.size(); j++)
            {
                Body body = (Body)sideBodies.elementAt(j);
                level.addBody(body.copy(), (byte)i);
            }
        }
        level.setName(this.name);
        level.setNextLevelId(this.nextLevelId);
        return level;
    }
}
