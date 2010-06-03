package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/05/2005
 * Time: 08:39:11
 */
public class FormationController extends BaseController implements Controller
{
    private static final int MAX_ATTEMPTS = 4;
    private static final int MAX_SEEK_ROTATION = FixedMath.toFixed(10);

    protected boolean inFormation;
    protected boolean seekingFormation;

    int formationIndex;

    protected int breakFormationChance;

    public FormationController()
    {
        this.inFormation = false;
        this.seekingFormation = true;
    }

    public int getBreakFormationChance()
    {
        return breakFormationChance;
    }

    public void setBreakFormationChance(int breakFormationChance)
    {
        this.breakFormationChance = breakFormationChance;
    }

    public void update(Body body, int elapsedTime)
    {
        if(this.inFormation)
        {
            Formation formation = ((Ship)body).getLevel().getFormation();
            position(formation, body, body.center, body.rotation);
            Level level = ((Ship)body).getLevel();
            if(formation.isBreakable() && level.canActivate(body))
            {
                if(Math.abs(RANDOM.nextInt()%100) < this.breakFormationChance)
                {
                    preBreakFormation(body);
                    this.inFormation = false;
                    level.activate(body);
                }
            }
//            if(((Ship)body).weapon != null)
//            {
//                ((Ship)body).weapon.update(elapsedTime);
//                ((Ship)body).weapon.shoot(((Ship)body), ((Ship)body).getGame());
//            }
        }else if(this.seekingFormation){
            Ship ship = (Ship)body;
            Level level = ship.getLevel();
            Formation formation = level.getFormation();
            int[] target = new int[3];
            int targetAngle = formation.position(body, target);
            seek(body, elapsedTime, target);
            if(body.center[0] == target[0] && body.center[2] == target[2])
            {
//                level.deactivate(body);
//                this.seekingFormation = false;
//                this.inFormation = true;
                int facingAngle = face(body, targetAngle, MAX_SEEK_ROTATION);
                if(Math.abs(targetAngle - facingAngle) < MAX_SEEK_ROTATION)
                {
                    level.deactivate(body);
                    this.seekingFormation = false;
                    this.inFormation = true;
                }
            }
        }else{
            doUpdate(body, elapsedTime);
        }
    }

    protected void position(Formation formation, Body body, int[] center, int[] rotation)
    {
        int angle = formation.position(body, body.center);
        rotation[0] = FixedMath.cos(angle/2);
        rotation[1] = 0;
        rotation[2] = FixedMath.sin(angle/2);
        rotation[3] = 0;
    }

    protected void seek(Body body, int multiplier, int[] target)
    {
        Ship ship = (Ship)body;
        // ignore the rotation
        // seek out the specified point
        int[] center = body.center;
        int dx = target[0] - center[0];
        int dz = target[2] - center[2];
        int speed = FixedMath.multiply(ship.getSpeed(), multiplier);

//            System.out.println("dx : "+FixedMath.getInteger(dx));
//            System.out.println("dz : "+FixedMath.getInteger(dz));
//            System.out.println("speed : "+FixedMath.getInteger(speed));
        // note : comparison of squares gives overflows (I think)
        if(Math.abs(dx) <= speed && Math.abs(dz) <= speed)
        {
            System.arraycopy(target, 0, center, 0, target.length);
            // TODO : interpolate between current rotation and desired rotation
        }else{
            // TODO : radius instead of boxed movement
            if(dx > speed)
            {
                dx = speed;
            }else if(dx < -speed){
                dx = -speed;
            }
            if(dz > speed)
            {
                dz = speed;
            }else if(dz < -speed){
                dz = -speed;
            }
            center[0] += dx;
            center[2] += dz;
        }
    }

    protected void doUpdate(Body body, int multiplier)
    {
        this.seekingFormation = true;
    }

    protected void preBreakFormation(Body body)
    {

    }

    public Controller copy()
    {
        FormationController formationController;
        formationController = new FormationController();
        copyInto(formationController);
        return formationController;
    }

    protected void copyInto(FormationController formationController)
    {
        formationController.breakFormationChance = this.breakFormationChance;
        formationController.formationIndex = this.formationIndex;
        formationController.inFormation = this.inFormation;
        formationController.breakFormationChance = this.breakFormationChance;
    }

    protected void explode(Ship body)
    {
        // add in a bonus
        Weapon weapon = body.getWeapon();
        if(weapon != null)
        {
            Body bonus = body.getLevel().getGame().getBody(weapon.getUpgradeTypeId());
            if(bonus != null)
            {
                Body copy = bonus.copy();
                System.arraycopy(body.center,  0, copy.center, 0, body.center.length);
                copy.rotation[0] = FixedMath.cos(FixedMath.toFixed(180/2));
                copy.rotation[2] = FixedMath.sin(FixedMath.toFixed(180/2));
                body.getLevel().addBody(copy, body.getSide());
            }
        }
        super.explode(body);
    }

    protected int face(Body body, int targetAngle, int maxTurnAngle)
    {
        // find out the angle of rotation around the y axis
        // TODO : this has to be slow!!
        int facingAngle = FixedMath.asin(body.rotation[2])*2;
//        if(FixedMath.getInteger(maxTurnAngle) == 0)
//        {
//            throw new RuntimeException("0 max turn angle "+maxTurnAngle);
//        }
//        System.out.println("max : "+FixedMath.getInteger(maxTurnAngle));
//        System.out.println("target : "+FixedMath.getInteger(targetAngle));
//        System.out.println("facing : "+FixedMath.getInteger(facingAngle));

        int diffAngle = targetAngle - facingAngle;
        while(diffAngle < -FixedMath.ANGLE_180)
        {
            diffAngle += FixedMath.ANGLE_360;
        }
        while(diffAngle >= FixedMath.ANGLE_180)
        {
            diffAngle -= FixedMath.ANGLE_360;
        }

//        System.out.println("diff "+FixedMath.getInteger(diffAngle));

        int angle;
        if(diffAngle > 0)
        {
            // turn left
            angle = facingAngle + Math.min(maxTurnAngle, diffAngle);
            while(angle > FixedMath.ANGLE_180)
            {
                angle -= FixedMath.ANGLE_360;
            }
            body.rotation[0] = FixedMath.cos(angle/2);
            body.rotation[1] = 0;
            body.rotation[2] = FixedMath.sin(angle/2);
            body.rotation[3] = 0;
        }else if(diffAngle == 0){
            // do nothing
            angle = targetAngle;
        }else{
            // turn right
            angle = facingAngle + Math.max(-maxTurnAngle, diffAngle);
            while(angle <= -FixedMath.ANGLE_180)
            {
                angle += FixedMath.ANGLE_360;
            }
            body.rotation[0] = FixedMath.cos(angle/2);
            body.rotation[1] = 0;
            body.rotation[2] = FixedMath.sin(angle/2);
            body.rotation[3] = 0;
        }
//        System.out.println("turned to : "+FixedMath.getInteger(angle));
        return angle;
    }

    protected Body getRandomEnemy(Body body)
    {
        Ship ship = (Ship)body;
        Level level = ship.getLevel();
        Body target = null;
        for(int i=0; i<MAX_ATTEMPTS; i++)
        {
            int side = Math.abs(RANDOM.nextInt() % level.getNumSides());
            if(side != ship.getSide() && side != Game.NEUTRAL_SIDE)
            {
                Vector bodies = level.getBodies()[side];
                if(bodies.size() > 0)
                {
                    int index = Math.abs(RANDOM.nextInt() % bodies.size());
                    target = (Body)bodies.elementAt(index);
                    break;
                }
            }
        }
        return target;
    }
}
