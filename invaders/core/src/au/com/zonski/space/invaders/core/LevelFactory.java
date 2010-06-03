package au.com.zonski.space.invaders.core;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 16/05/2005
 * Time: 09:05:03
 */
public interface LevelFactory
{
    /**
     * obtains a level with the specified identifier
     * @param id the identifier of the level
     * @return the level with the specified id
     * @throws Exception if there is a problem obtaining the level
     */
    Level getLevel(String id)
            throws Exception;
}
