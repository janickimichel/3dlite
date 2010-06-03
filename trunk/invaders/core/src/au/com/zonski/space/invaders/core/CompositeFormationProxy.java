package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 16/05/2005
 * Time: 15:27:36
 * <p>
 * Proxy that allows many formations to work together
 */
public class CompositeFormationProxy implements Formation
{
    public Vector children;
    private int size;
    private boolean breakable;

    public CompositeFormationProxy()
    {
        this.children = new Vector(2);
    }

    public boolean isBreakable()
    {
        return this.breakable;
    }

    public int position(Body body, int[] position)
    {
        int rotation = 0;
        if(body.controller instanceof FormationController)
        {
            FormationController controller = (FormationController)body.controller;
            int count = 0;
            for(int i=this.children.size(); i>0; )
            {
                i--;
                ChildFormation child = (ChildFormation)this.children.elementAt(i);
                int newCount = count + child.numberAllowed;
                if(controller.formationIndex < newCount || i==0)
                {
                    // this is our man
                    // adjust the index so it appears OK for the child formation
                    int index = controller.formationIndex;
                    controller.formationIndex -= count;
                    rotation = child.formation.position(body, position);
                    controller.formationIndex = index;
                    break;
                }
                count = newCount;
            }
        }
        return rotation;
    }

    public void update(int multiplier)
    {
        boolean breakable = true;
        for(int i=this.children.size(); i>0; )
        {
            i--;
            ChildFormation child = (ChildFormation)this.children.elementAt(i);
            child.formation.update(multiplier);
            breakable = (breakable && child.formation.isBreakable());
        }
        this.breakable = breakable;
    }

    public void add(Body body)
    {
        if(body.controller instanceof FormationController)
        {
            FormationController controller = (FormationController)body.controller;
            int count = this.size;
            boolean added = false;
            for(int i=this.children.size(); i>0; )
            {
                i--;
                ChildFormation child = (ChildFormation)this.children.elementAt(i);
                count -= child.numberAllowed;
                if(count < 0)
                {
                    child.formation.add(body);
                    // TODO : use the index assigned by the child formation
                    controller.formationIndex = this.size;
                    this.size++;
                    added = true;
                    break;
                }
            }
            if(!added)
            {
                // find a spot that is already vacated by a dead enemy
                // TODO : there can never be more than 32 enemies in formation
                int indices = 0;
                Level level = ((Ship)body).getLevel();
                Vector[] bodies = level.getBodies();
                for(int side = bodies.length; side>0; )
                {
                    side --;
                    Vector sideBodies = bodies[side];
                    for(int i=sideBodies.size(); i>0; )
                    {
                        i--;
                        Body found = (Body)sideBodies.elementAt(i);
                        if(found.controller instanceof FormationController)
                        {
                            FormationController foundController;
                            foundController = (FormationController)found.controller;
                            indices |= 0x01 << foundController.formationIndex;
                        }
                    }
                }
                // look for an appropriate index
                for(int i=this.size; i>0; )
                {
                    i--;
                    if((indices & (0x01 << i)) == 0)
                    {
                        // TODO : assumes that the only thing that a controller will do is set the formation index
                        controller.formationIndex = i;
                        added = true;
                        break;
                    }
                }
                if(!added)
                {
                    // just add it to the last formation, ignoring size limits
                    ChildFormation child = (ChildFormation)this.children.elementAt(0);
                    child.formation.add(body);
                    controller.formationIndex = this.size;
                    this.size++;
                    added = true;
                }
            }
        }
    }

    public void addFormation(Formation formation, int numberAllowed)
    {
        ChildFormation child = new ChildFormation(formation, numberAllowed);
        this.children.insertElementAt(child, 0);
    }

    public Formation copy()
    {
        CompositeFormationProxy proxy = new CompositeFormationProxy();
        proxy.children.ensureCapacity(this.children.size());
        for(int i=this.children.size(); i>0; )
        {
            i--;
            ChildFormation child = (ChildFormation)this.children.elementAt(i);
            proxy.addFormation(child.formation.copy(), child.numberAllowed);
        }
        return proxy;
    }

    public class ChildFormation
    {
        public int numberAllowed;
        public Formation formation;

        public ChildFormation(Formation formation, int numberAllowed)
        {
            this.formation = formation;
            this.numberAllowed = numberAllowed;
        }
    }
}
