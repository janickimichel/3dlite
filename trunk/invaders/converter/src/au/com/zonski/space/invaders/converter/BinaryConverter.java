package au.com.zonski.space.invaders.converter;

import au.com.zonski.config.xml.FileXMLConfiguredObjectFactory;
import au.com.zonski.config.xml.XMLConfigurationHelper;
import au.com.zonski.config.xml.DefaultXMLConfigurerFactoryConfigurer;
import au.com.zonski.config.xml.XMLConfiguredObjectFactoryAdapter;
import au.com.zonski.space.invaders.core.*;
import au.com.zonski.space.invaders.application.ConfigurationLevelFactory;
import au.com.zonski.space.domain.Body;
import au.com.zonski.space.domain.Controller;
import au.com.zonski.shape3d.*;
import au.com.zonski.math.FixedMath;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 26/05/2005
 * Time: 15:18:57
 */
public class BinaryConverter
{
    private static final void write(File dir, Game game, Ship[] prototypes, Vector imageLocations)
        throws IOException, Exception
    {
        Hashtable levels = new Hashtable();

        String levelName = game.getStartLevelName();

        File gameFile = new File(dir, "game.abc");
        FileOutputStream gos = new FileOutputStream(gameFile);
        write(gos, game);
        gos.close();

        File imageFile = new File(dir, "img.lst");
        FileOutputStream ios = new FileOutputStream(imageFile);
        writeImageFile(ios, imageLocations);
        ios.close();

        for(int i=0; i<prototypes.length; i++)
        {

            Ship prototype = prototypes[i];
            if(prototype != null)
            {
                File file = new File(dir, prototype.getTypeId()+".shp");
                FileOutputStream fos = new FileOutputStream(file);
                writePrototype(fos, prototype);
                fos.close();
            }
        }

        while(!levels.containsKey(levelName))
        {
            Level level = game.getLevelFactory().getLevel(levelName);
            levels.put(levelName, level);
            File file = new File(dir, levelName+".lvl");
            FileOutputStream fos = new FileOutputStream(file);
            write(fos, level);
            fos.close();
            levelName = level.getNextLevelId();
        }
    }

    private static final void writeImageFile(OutputStream outs, Vector strings)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.write(strings.size());
        for(int i=strings.size(); i>0; )
        {
            i--;
            String s = (String)strings.elementAt(i);
            if(s != null && s.length() > 0)
            {
                dos.write(i);
                dos.write(s.length());
                dos.writeBytes(s);
            }
        }
    }

    private static final void write(OutputStream outs, Game game)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);

        String startLevelName = game.getStartLevelName();
        dos.write(startLevelName.length());
        dos.writeBytes(startLevelName);
        write(outs, game.getPlayerController());
        Body[] bodies = game.bodies;
        for(int i=bodies.length; i>0; )
        {
            i--;
            Body body = bodies[i];
            if(body == null)
            {
                outs.write(-1);
            }else{
                Ship ship = (Ship)body;
                outs.write((byte)i);
                writePrototype(outs, ship);
            }
        }
    }

    private static final void writePrototype(OutputStream outs, Ship ship)
        throws IOException
    {
        outs.write(ship.getHealth());
        outs.write(ship.getTypeId());
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(ship.getSpeed());
        dos.writeInt(ship.getPower());
        write(outs, ship.getShape());
        write(outs, ship.getWeapon());
        Controller controller = ship.getController();
        write(outs, controller);
    }

    private static final void write(OutputStream outs, Shape shape)
        throws IOException
    {
        if(shape instanceof AnimatedMesh)
        {
            outs.write(BinaryLevelFactory.SHAPE_ANIMATED_MESH);
            write(outs, (AnimatedMesh)shape);
        }else if(shape instanceof Mesh){
            outs.write(BinaryLevelFactory.SHAPE_MESH);
            write(outs, (Mesh)shape);
        }else if(shape instanceof Composite){
            outs.write(BinaryLevelFactory.SHAPE_COMPOSITE);
            write(outs, (Composite)shape);
        }else{
            throw new IOException("unknown type "+shape.getClass().getName());
        }
    }

    private static final void write(OutputStream outs, AnimatedMesh mesh)
        throws IOException
    {
        write(outs, (Mesh)mesh);
        write(outs, mesh.animation);
    }

    private static final void write(OutputStream outs, Animation animation)
        throws IOException
    {
        if(animation instanceof RotationAnimation)
        {
            outs.write(BinaryLevelFactory.ANIMATION_ROTATION);
            write(outs, (RotationAnimation)animation);
        }else if(animation instanceof FlapAnimation){
            outs.write(BinaryLevelFactory.ANIMATION_FLAP);
            write(outs, (FlapAnimation)animation);
        }else{
            throw new IOException("unexpected animation type : "+animation.getClass().getName());
        }
    }

    private static final void write(OutputStream outs, RotationAnimation animation)
        throws IOException
    {
        int[] multiplier = animation.multiplier;
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(multiplier[0]);
        dos.writeInt(multiplier[1]);
        dos.writeInt(multiplier[2]);
        dos.writeInt(multiplier[3]);
    }

    public static final void write(OutputStream outs, FlapAnimation animation)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(animation.getMinRotationAngle());
        dos.writeInt(animation.getMaxRotationAngle());
        dos.writeInt(animation.getRotationIncrement());
    }

    private static final void write(OutputStream outs, Mesh mesh)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(mesh.color);
        dos.writeInt(mesh.maxradius);
        int[][] points = mesh.baseMatrix;
        dos.writeByte(points.length);
        for(int i=0; i<points.length; i++)
        {
            int[] point = points[i];
            dos.writeInt(point[0]);
            dos.writeInt(point[1]);
            dos.writeInt(point[2]);
        }
        byte[][] faces = mesh.faces;
        dos.writeByte(faces.length);
        for(int i=0; i<faces.length; i++)
        {
            byte[] face = faces[i];
            dos.writeByte(face.length);
            dos.write(face);
        }
    }

    private static final void write(OutputStream outs, Composite composite)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.write(composite.shapes.size());
        dos.writeInt(composite.maxradius);
        for(int i=0; i<composite.shapes.size(); i++)
        {
            Shape shape = (Shape)composite.shapes.elementAt(i);
            write(outs, shape);
        }
    }

    private static final void write(OutputStream outs, Weapon weapon)
        throws IOException
    {
        if(weapon instanceof SpreadWeapon)
        {
            outs.write(BinaryLevelFactory.WEAPON_SPREAD);
            write(outs, (SpreadWeapon)weapon);
        }else if(weapon instanceof MultiWeapon){
            outs.write(BinaryLevelFactory.WEAPON_MULTI);
        }else if(weapon instanceof ScalingWeapon){
            outs.write(BinaryLevelFactory.WEAPON_SCALE);
        }else if(weapon == null){
            outs.write(BinaryLevelFactory.WEAPON_NONE);
        }else if(weapon instanceof Weapon){
            outs.write(BinaryLevelFactory.WEAPON_NO_SUBTYPE);
        }else{
            throw new IOException("unexpected weapon type : "+weapon.getClass());
        }
        if(weapon != null)
        {
            outs.write(weapon.getMaxUpgrades());
            outs.write(weapon.getCost());
            outs.write(weapon.getUpgradeTypeId());
            outs.write(((Ship)weapon.getProjectile()).getTypeId());
            DataOutputStream dos = new DataOutputStream(outs);
            dos.writeInt(weapon.getTimeBetweenShots());
        }
    }

    private static final void write(OutputStream outs, SpreadWeapon weapon)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(weapon.getSpreadAngle());
    }

    private static final void write(OutputStream outs, Controller controller)
        throws IOException
    {
        if(controller instanceof BomberController)
        {
            outs.write(BinaryLevelFactory.CONTROLLER_BOMBER);
        }else if(controller instanceof FighterController){
            outs.write(BinaryLevelFactory.CONTROLLER_FIGHTER);
        }else if(controller instanceof FragmentController){
            outs.write(BinaryLevelFactory.CONTROLLER_FRAGMENT);
        }else if(controller instanceof MissileController){
            outs.write(BinaryLevelFactory.CONTROLLER_MISSILE);
        }else if(controller instanceof PlayerController){
            outs.write(BinaryLevelFactory.CONTROLLER_PLAYER);
            write(outs, (PlayerController)controller);
        }else if(controller instanceof ShooterController){
            outs.write(BinaryLevelFactory.CONTROLLER_SHOOTER);
        }else if(controller instanceof TwitController){
            outs.write(BinaryLevelFactory.CONTROLLER_TWIT);
        }else if(controller instanceof ProjectileController){
            outs.write(BinaryLevelFactory.CONTROLLER_PROJECTILE);
        }else if(controller instanceof UpgradeController){
            outs.write(BinaryLevelFactory.CONTROLLER_UPGRADE);
        }else if(controller instanceof TurretController){
            outs.write(BinaryLevelFactory.CONTROLLER_TURRET);
        }else if(controller instanceof FormationController){
            outs.write(BinaryLevelFactory.CONTROLLER_FORMATION);
        }else if(controller == null){
            outs.write(BinaryLevelFactory.CONTROLLER_NONE);
        }else{
            throw new IOException("unexpected controller type :"+controller.getClass().getName());
        }
        if(controller instanceof FormationController)
        {
            FormationController formationController = (FormationController)controller;
            outs.write(formationController.getBreakFormationChance());
        }
    }

    private static final void write(OutputStream outs, PlayerController controller)
        throws IOException
    {
        outs.write(controller.lives.size());
        for(int i=0; i<controller.lives.size(); i++)
        {
            Ship ship = (Ship)controller.lives.elementAt(i);
            outs.write(ship.getTypeId());
        }
    }

    private static final void write(OutputStream outs, Level level)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.write(level.getNextLevelId().length());
        dos.writeBytes(level.getNextLevelId());
        dos.write(level.getName().length());
        dos.writeBytes(level.getName());
        dos.write(level.getMaxActive());
        dos.writeInt(level.getMaxx());
        dos.writeInt(level.getMinx());
        dos.writeInt(level.getHeight());
        write(outs, level.getFormation());
        Vector[] bodies = level.getBodies();
        outs.write(bodies.length);
        for(int side=0; side<bodies.length; side++)
        {
            outs.write(side);
            Vector sideBodies = bodies[side];
            outs.write(sideBodies.size());
            for(int i=0; i<sideBodies.size(); i++)
            {
                Ship ship = (Ship)sideBodies.elementAt(i);
                write(outs, ship);
            }
        }
    }

    private static final void write(OutputStream outs, Formation formation)
        throws IOException
    {
        if(formation instanceof CircularFormation)
        {
            outs.write(BinaryLevelFactory.FORMATION_CIRCULAR);
            write(outs, (CircularFormation)formation);
        }else if(formation instanceof SquareFormation){
            outs.write(BinaryLevelFactory.FORMATION_SQUARE);
            write(outs, (SquareFormation)formation);
        }else if(formation instanceof CompositeFormationProxy){
            outs.write(BinaryLevelFactory.FORMATION_COMPOSITE);
            write(outs, (CompositeFormationProxy)formation);
        }else{
            throw new IOException("unexpected formation type "+formation.getClass().getName());
        }
    }

    private static final void write(OutputStream outs, CircularFormation formation)
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(formation.getCenterX());
        dos.writeInt(formation.getCenterZ());
        dos.writeInt(formation.getRadius());
        dos.writeInt(formation.getSpeed());
    }

    private static final void write(OutputStream outs, SquareFormation formation)
            throws IOException
    {
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(formation.getCenterX());
        dos.writeInt(formation.getColumns());
        dos.writeInt(formation.getColumnSpacing());
        dos.writeInt(formation.getRowSpacing());
        dos.writeInt(formation.getTopZ());
        dos.writeInt(formation.getYRotation());
    }

    private static final void write(OutputStream outs, CompositeFormationProxy formation)
            throws IOException
    {
        Vector children = formation.children;
        outs.write(children.size());
        for(int i=children.size(); i>0; )
        {
            i--;
            CompositeFormationProxy.ChildFormation child;
            child = (CompositeFormationProxy.ChildFormation)children.elementAt(i);
            outs.write(child.numberAllowed);
            write(outs, child.formation);
        }
    }

    private static final void write(OutputStream outs, Ship ship)
        throws IOException
    {
        outs.write(ship.getTypeId());
        DataOutputStream dos = new DataOutputStream(outs);
        dos.writeInt(ship.center[0]);
        dos.writeInt(ship.center[1]);
        dos.writeInt(ship.center[2]);
        dos.writeInt(FixedMath.asin(ship.rotation[2]) * 2);
    }

    public static final void main(String[] args)
    {
        if(args.length != 2)
        {
            showUsage();
        }else{
            String fromDir = args[0];
            String toDir = args[1];
            File from = new File(fromDir);
            File to = new File(toDir);
            try
            {
                from.mkdirs();
                to.mkdirs();

                Game game;
                Vector prototypes;
                FileXMLConfiguredObjectFactory xmlConfiguredObjectFactory;
                xmlConfiguredObjectFactory = new FileXMLConfiguredObjectFactory(
                    from
                );
                // preload the index
                xmlConfiguredObjectFactory.configure(
                    XMLConfigurationHelper.DEFAULT_INDEX_VALUE, new DefaultXMLConfigurerFactoryConfigurer()
                );

                XMLConfiguredObjectFactoryAdapter configuredObjectFactory;
                configuredObjectFactory = new XMLConfiguredObjectFactoryAdapter(xmlConfiguredObjectFactory);

                game = (Game)configuredObjectFactory.getConfiguredObject("game");
                game.setLevelFactory(new ConfigurationLevelFactory(configuredObjectFactory));

                prototypes = (Vector)configuredObjectFactory.getConfiguredObject("_prototypes");
                Ship[] protos = new Ship[prototypes.size()];
                for(int i=prototypes.size(); i>0; )
                {
                    i--;
                    Ship prototype = (Ship)prototypes.elementAt(i);
                    protos[prototype.getTypeId()] = prototype;
                }

                Vector imageLocations = (Vector)configuredObjectFactory.getConfiguredObject("typeimages");

                write(to, game, protos, imageLocations);

            }catch(Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static final void showUsage()
    {
        System.err.println("usage : java "+BinaryConverter.class.getName()+" <from dir> <to dir>");
    }
}
