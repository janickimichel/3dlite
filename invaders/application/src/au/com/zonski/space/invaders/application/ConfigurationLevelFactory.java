package au.com.zonski.space.invaders.application;

import au.com.zonski.space.invaders.core.LevelFactory;
import au.com.zonski.space.invaders.core.Level;
import au.com.zonski.config.ConfiguredObjectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 16/05/2005
 * Time: 09:07:00
 */
public class ConfigurationLevelFactory implements LevelFactory
{
    private ConfiguredObjectFactory objectFactory;

    public ConfigurationLevelFactory(ConfiguredObjectFactory objectFactory)
    {
        this.objectFactory = objectFactory;
    }

    public Level getLevel(String id)
            throws Exception
    {
        Level level = (Level)this.objectFactory.getConfiguredObject(id);
        if(level == null)
        {
            throw new Exception("no level "+id);
        }
        return level.copy();
    }
}
