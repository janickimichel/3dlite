package au.com.zonski.math.config.xml;

import au.com.zonski.config.xml.XMLConfigurer;
import au.com.zonski.config.xml.XMLConfigurerFactory;
import au.com.zonski.config.xml.XMLConfigurationLocation;
import au.com.zonski.config.ConfiguredObjectFactory;
import au.com.zonski.config.ConfigurationException;
import au.com.zonski.math.FixedMath;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/05/2005
 * Time: 09:37:20
 */
public class XMLFixedConfigurer implements XMLConfigurer
{
    public static final String PRECISION = "precision";

    public Object create(Element element, XMLConfigurerFactory xmlConfigurerFactory, ConfiguredObjectFactory configuredObjectFactory, XMLConfigurationLocation xmlConfigurationLocation) throws ConfigurationException
    {
        int precision;
        String precisionString = element.getAttributeValue(PRECISION);
        try
        {
            if(precisionString != null)
            {
                precision = Integer.parseInt(precisionString);
            }else{
                precision = FixedMath.DEFAULT_PRECISION;
            }
        }catch(Exception ex){
            throw new ConfigurationException("unable to format "+precisionString, xmlConfigurationLocation, ex);
        }
        try
        {
            return new Integer(FixedMath.toFixed(Integer.parseInt(element.getText()), precision));
        }catch(Exception ex){
            throw new ConfigurationException("unable to format "+element.getText(), xmlConfigurationLocation, ex);
        }
    }

    public void configure(Object o, Element element, ConfiguredObjectFactory configuredObjectFactory, XMLConfigurationLocation xmlConfigurationLocation) throws ConfigurationException
    {
    }

    public void init(Element element, XMLConfigurationLocation xmlConfigurationLocation) throws ConfigurationException
    {
        // do nothing
    }
}
