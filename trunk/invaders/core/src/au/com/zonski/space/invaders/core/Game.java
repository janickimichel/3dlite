package au.com.zonski.space.invaders.core;

import au.com.zonski.math.FixedMath;
import au.com.zonski.space.domain.Body;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 3/05/2005
 * Time: 14:40:03
 */
public class Game
{
    public static final int DEFAULT_DELAY               = 40;

    public static final byte NEUTRAL_SIDE               = 0;

    public static final int LEVEL_DELAY                 = 5000 / DEFAULT_DELAY;

    public PlayerController playerController;
    public PlayerController currentPlayerController;
    public LevelFactory levelFactory;
    public Body[] bodies;

    private int countDown = -1;

    public int updateDelay = DEFAULT_DELAY;


    private long time = System.currentTimeMillis();

    private String startLevelName;
    private Level currentLevel;

    public Game()
    {
        this.bodies = new Body[5];
    }

    public PlayerController getPlayerController()
    {
        return playerController;
    }

    public void setPlayerController(PlayerController playerController)
    {
        this.playerController = playerController;
    }

    public Ship getCurrentPlayer()
    {
        return this.currentPlayerController.getCurrentLife();
    }

    public LevelFactory getLevelFactory()
    {
        return levelFactory;
    }

    public void setLevelFactory(LevelFactory levelFactory)
    {
        this.levelFactory = levelFactory;
    }

    public Level getStartLevel()
        throws Exception
    {
        return this.levelFactory.getLevel(this.startLevelName);
    }

    public String getStartLevelName()
    {
        return this.startLevelName;
    }

    public void setStartLevelName(String startLevelName)
    {
        this.startLevelName = startLevelName;
    }

    public void start()
    {
        this.currentPlayerController = (PlayerController)this.playerController.copy();
        try
        {
            Level currentLevel = this.getStartLevel();
            this.setCurrentLevel(currentLevel);
            this.currentPlayerController.costLife(currentLevel, (byte)1);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Body getBody(byte typeId)
    {
        Body b;
        if(typeId >= 0)
        {
            b =  this.bodies[typeId];
        }else{
            b = null;
        }
        //System.out.println("getting body : "+typeId+" = "+b);
        return b;
    }

    public void setBody(byte typeId, Body body)
    {
        //System.out.println("setting body : "+typeId+" = "+body);
        this.bodies[typeId] = body;
    }

    public Level getCurrentLevel()
    {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel)
    {
        if(this.currentLevel != null)
        {
            // add in any existing objects
            Vector[] bodies = this.currentLevel.getBodies();
            for(int side = bodies.length; side>0; )
            {
                side--;
                Vector sideBodies = bodies[side];
                for(int i=sideBodies.size(); i>0; )
                {
                    i--;
                    Body body = (Body)sideBodies.elementAt(i);
                    currentLevel.addBody(body, (byte)side);
                }
            }
        }
        this.currentLevel = currentLevel;
        this.currentLevel.setGame(this);
        //this.currentLevel.addBody(this.currentPlayer, (byte)1);
    }

    public void iterate()
    {
        int elapsed;
        int diff = (int)(System.currentTimeMillis() - this.time);
        if(diff >= this.updateDelay)
        {
            elapsed = FixedMath.divide(diff, this.updateDelay);
        }else{
            elapsed = FixedMath.DEFAULT_ONE;
            try
            {
                Thread.sleep(this.updateDelay - diff);
            }catch(Exception ex){
                // should never happen
            }
        }
        this.time = System.currentTimeMillis();
        int liveSides;
        try
        {
            liveSides = this.currentLevel.update(elapsed);
        }catch(Exception ex){
            ex.printStackTrace();
            // assume that it's still OK, we'll find out next time
            liveSides = 2;
        }
        if(liveSides <= 1)
        {
            // we have a winner, find out who it is
            if(this.getCurrentPlayer().isRemove())
            {
                // the player's dead, he's lost
                // TODO : listen for a mouse click
            }else{
                if(countDown == 0)
                {
                    // the player's not dead, he's won!!
                    String id = this.currentLevel.getNextLevelId();
                    try
                    {
                        Level level = this.levelFactory.getLevel(id);
                        this.setCurrentLevel(level);
                    }catch(Exception ex){
                        System.err.println("unable to load level : "+id);
                        ex.printStackTrace();
                    }
                    countDown = -1;
                }else if(countDown < 0){
                    countDown = LEVEL_DELAY;
                }else{
                    countDown --;
                }
            }
        }
    }
}
