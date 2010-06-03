package au.com.zonski.space.invaders.core;

import au.com.zonski.shape3d.*;
import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

import java.io.InputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 26/05/2005
 * Time: 16:40:15
 */
public class BinaryLevelFactory implements LevelFactory
{
    // shapes
    public static final byte SHAPE_MESH         = 1;
    public static final byte SHAPE_COMPOSITE    = 2;
    public static final byte SHAPE_ANIMATED_MESH = 3;

    // weapons
    public static final byte WEAPON_NONE        = 0;
    public static final byte WEAPON_SPREAD      = 1;
    public static final byte WEAPON_MULTI       = 2;
    public static final byte WEAPON_SCALE       = 3;
    public static final byte WEAPON_NO_SUBTYPE  = 4;

    // controller
    public static final byte CONTROLLER_NONE    = 0;
    public static final byte CONTROLLER_BOMBER  = 1;
    public static final byte CONTROLLER_FIGHTER = 2;
    public static final byte CONTROLLER_FORMATION = 3;
    public static final byte CONTROLLER_FRAGMENT = 4;
    public static final byte CONTROLLER_MISSILE  = 5;
    public static final byte CONTROLLER_PLAYER   = 6;
    public static final byte CONTROLLER_SHOOTER  = 7;
    public static final byte CONTROLLER_TWIT     = 8;
    public static final byte CONTROLLER_PROJECTILE = 9;
    public static final byte CONTROLLER_UPGRADE  = 10;
    public static final byte CONTROLLER_TURRET   = 11;

    // formation
    public static final byte FORMATION_CIRCULAR = 1;
    public static final byte FORMATION_SQUARE   = 2;
    public static final byte FORMATION_COMPOSITE= 3;

    // animation
    public static final byte ANIMATION_ROTATION = 1;
    public static final byte ANIMATION_FLAP     = 2;

    private String resourcePath;

    private Hashtable prototypes;
    private Game game;
    private String[] imageLocations;

    public BinaryLevelFactory(String resourcePath)
    {
        this.resourcePath = resourcePath;
        this.prototypes = new Hashtable(20);
    }

    public int getNumImages()
        throws IOException
    {
        return getImageLocations().length;
    }

    public String getImageLocation(int id)
        throws IOException
    {
        return getImageLocations()[id];
    }

    public String[] getImageLocations()
        throws IOException
    {
        if(this.imageLocations == null)
        {
            String imageResourceString = this.resourcePath + "img.lst";
            DataInputStream ins = new DataInputStream(getClass().getResourceAsStream(imageResourceString));
            this.imageLocations = readImageLocations(ins);
            ins.close();
        }
        return this.imageLocations;
    }

    public String[] readImageLocations(DataInputStream dis)
        throws IOException
    {
        int length = dis.read();
        String[] imageLocations = new String[length];
        for(int i=0; i<length; i++)
        {
            int pos = dis.read();
            if(pos >= 0)
            {
                String imageLocation = readString(dis);
                imageLocations[pos] = imageLocation;
            }else{
                break;
            }
        }
        return imageLocations;
    }

    public Game getGame()
        throws IOException
    {
        String gameResourceString = this.resourcePath + "game.abc";
        DataInputStream ins = new DataInputStream(getClass().getResourceAsStream(gameResourceString));
        Game game = readGame(ins);
        ins.close();
        return game;
    }

    public Game readGame(DataInputStream dis)
        throws IOException
    {
        if(this.game == null)
        {
            Game game = new Game();
            game.setStartLevelName(readString(dis));
            game.setPlayerController(readPlayerController(dis));
            game.setLevelFactory(this);
            Body[] bodies = game.bodies;
            for(int i=bodies.length; i>0; )
            {
                i--;
                byte typeId = dis.readByte();
                if(typeId >= 0)
                {
                    bodies[typeId] = readPrototype(dis);
                }
            }
            this.game = game;
        }
        return this.game;
    }

    public Level getLevel(String id)
            throws IOException
    {
        String levelResourceString = this.resourcePath + id + ".lvl";
        DataInputStream ins = new DataInputStream(getClass().getResourceAsStream(levelResourceString));
        Level level = readLevel(ins);
        level.setGame(this.game);
        ins.close();
        return level;
    }

    public Ship getPrototype(byte typeId)
        throws IOException
    {
        Byte type = new Byte(typeId);
        Ship prototype = (Ship)this.prototypes.get(type);
        if(prototype == null)
        {
            String prototypeResourceString = this.resourcePath + typeId + ".shp";
            DataInputStream dis = new DataInputStream(getClass().getResourceAsStream(prototypeResourceString));
            prototype = readPrototype(dis);
            dis.close();
            this.prototypes.put(type, prototype);
        }
        return prototype;
    }

    private Ship readPrototype(DataInputStream dis)
        throws IOException
    {
        Ship ship = new Ship();
        ship.setHealth((byte)dis.read());
        ship.setTypeId((byte)dis.read());
        ship.setSpeed(dis.readInt());
        ship.setPower(dis.readInt());
        ship.setShape(readShape(dis));
        ship.setWeapon(readWeapon(dis));
        ship.setController(readController(dis));
        return ship;
    }

    private Shape readShape(DataInputStream dis)
        throws IOException
    {
        int type = dis.read();
        Shape shape;
        switch(type)
        {
            case SHAPE_COMPOSITE:
                shape = readCompositeShape(dis);
                break;
            case SHAPE_MESH:
                shape = readMesh(dis);
                break;
            case SHAPE_ANIMATED_MESH:
                shape = readAnimatedMesh(dis);
                break;
            default:
                throw new IOException("unexpected shape type : "+type);
        }
        return shape;
    }

    private Composite readCompositeShape(DataInputStream dis)
        throws IOException
    {
        int size = dis.read();
        int maxRadius = dis.readInt();
        Composite composite = new Composite(size);
        composite.maxradius = maxRadius;
        for(int i=0; i<size; i++)
        {
            composite.shapes.addElement(readShape(dis));
        }
        return composite;
    }

    private Mesh readMesh(DataInputStream dis)
        throws IOException
    {
        Mesh mesh = new Mesh(null, null);
        readMesh(dis, mesh);
        return mesh;
    }

    private Mesh readMesh(DataInputStream dis, Mesh mesh)
        throws IOException
    {
        mesh.color = dis.readInt();
        mesh.maxradius = dis.readInt();
        int numPoints = dis.read();
        int[][] points = new int[numPoints][3];
        for(int i=0; i<numPoints; i++)
        {
            points[i][0] = dis.readInt();
            points[i][1] = dis.readInt();
            points[i][2] = dis.readInt();
        }
        int numFaces = dis.read();
        byte[][] faces = new byte[numFaces][];
        for(int i=0; i<numFaces; i++)
        {
            int numVertices = dis.read();
            byte[] face = new byte[numVertices];
            dis.readFully(face);
            faces[i] = face;
        }
        mesh.baseMatrix = points;
        mesh.faces = faces;
        return mesh;
    }

    private Mesh readAnimatedMesh(DataInputStream dis)
        throws IOException
    {
        AnimatedMesh mesh = new AnimatedMesh(null, null);
        readMesh(dis, mesh);
        mesh.animation = readAnimation(dis);
        return mesh;
    }

    private Animation readAnimation(DataInputStream dis)
        throws IOException
    {
        int type = dis.read();
        Animation animation;
        switch(type)
        {
            case ANIMATION_ROTATION:
                animation = readRotationAnimation(dis);
                break;
            case ANIMATION_FLAP:
                animation = readFlapAnimation(dis);
                break;
            default:
                throw new IOException("unexpected animation type : "+type);
        }
        return animation;
    }

    private RotationAnimation readRotationAnimation(DataInputStream dis)
        throws IOException
    {
        RotationAnimation rotation = new RotationAnimation();
        rotation.multiplier[0] = dis.readInt();
        rotation.multiplier[1] = dis.readInt();
        rotation.multiplier[2] = dis.readInt();
        rotation.multiplier[3] = dis.readInt();
        return rotation;
    }

    private FlapAnimation readFlapAnimation(DataInputStream dis)
        throws IOException
    {
        FlapAnimation animation = new FlapAnimation();
        animation.setMinRotationAngle(dis.readInt());
        animation.setMaxRotationAngle(dis.readInt());
        animation.setRotationIncrement(dis.readInt());
        return animation;
    }

    private Weapon readWeapon(DataInputStream dis)
        throws IOException
    {
        int type = dis.read();
        Weapon weapon;
        switch(type)
        {
            case WEAPON_SPREAD:
                weapon = readSpreadWeapon(dis);
                break;
            case WEAPON_MULTI:
                weapon = new MultiWeapon();
                break;
            case WEAPON_SCALE:
                weapon = new ScalingWeapon();
                break;
            case WEAPON_NONE:
                weapon = null;
                break;
            case WEAPON_NO_SUBTYPE:
                weapon = new Weapon();
                break;
            default:
                throw new IOException("unknown weapon type : "+type);
        }
        if(weapon != null)
        {
            weapon.setMaxUpgrades((byte)dis.read());
            weapon.setCost((byte)dis.read());
            weapon.setUpgradeTypeId((byte)dis.read());
            byte projectileTypeId = (byte)dis.read();
            weapon.setProjectile(getPrototype(projectileTypeId));
            weapon.setTimeBetweenShots(dis.readInt());
        }
        return weapon;
    }

    private SpreadWeapon readSpreadWeapon(DataInputStream dis)
        throws IOException
    {
        SpreadWeapon weapon = new SpreadWeapon();
        weapon.setSpreadAngle(dis.readInt());
        return weapon;
    }

    private Controller readController(DataInputStream dis)
        throws IOException
    {
        int type = dis.read();
        Controller controller;
        switch(type)
        {
            case CONTROLLER_NONE:
                controller = null;
                break;
            case CONTROLLER_BOMBER:
                controller = new BomberController();
                break;
            case CONTROLLER_FIGHTER:
                controller = new FighterController();
                break;
            case CONTROLLER_FORMATION:
                controller = new FormationController();
                break;
            case CONTROLLER_FRAGMENT:
                controller = new FragmentController();
                break;
            case CONTROLLER_MISSILE:
                controller = new MissileController();
                break;
            case CONTROLLER_PLAYER:
                controller = readPlayerController(dis);
                break;
            case CONTROLLER_SHOOTER:
                controller = new ShooterController();
                break;
            case CONTROLLER_TWIT:
                controller = new TwitController();
                break;
            case CONTROLLER_PROJECTILE:
                controller = new ProjectileController();
                break;
            case CONTROLLER_UPGRADE:
                controller = new UpgradeController();
                break;
            case CONTROLLER_TURRET:
                controller = new TurretController();
                break;
            default:
                throw new IOException("unexpected controller type : "+type);
        }
        if(controller instanceof FormationController)
        {
            FormationController formationController;
            formationController = (FormationController)controller;
            formationController.setBreakFormationChance(dis.read());
        }
        return controller;
    }

    private PlayerController readPlayerController(DataInputStream dis)
        throws IOException
    {
        PlayerController controller = new PlayerController();
        int numLives = dis.read();
        controller.lives.ensureCapacity(numLives);
        for(int i=0; i<numLives; i++)
        {
            byte typeId = (byte)dis.read();
            controller.addLife(getPrototype(typeId));
        }
        return controller;
    }

    private Level readLevel(DataInputStream dis)
        throws IOException
    {
        Level level = new Level();
        level.setNextLevelId(readString(dis));
        level.setName(readString(dis));
        level.setMaxActive(dis.read());
        level.setMaxx(dis.readInt());
        level.setMinx(dis.readInt());
        level.setHeight(dis.readInt());
        level.setFormation(readFormation(dis));
        int sides = dis.read();
        level.setNumSides(sides);
        for(int i=0; i<sides; i++)
        {
            int side = dis.read();
            int size = dis.read();
            for(int j=0; j<size; j++)
            {
                Ship ship = readShip(dis);
                level.addBody(ship, (byte)side);
            }
        }
        return level;
    }

    private Ship readShip(DataInputStream dis)
        throws IOException
    {
        byte typeId = (byte)dis.read();
        Ship ship = (Ship)getPrototype(typeId).copy();
        ship.center[0] = dis.readInt();
        ship.center[1] = dis.readInt();
        ship.center[2] = dis.readInt();
        int angle = dis.readInt();
        ship.rotation[0] = FixedMath.cos(angle/2);
        ship.rotation[2] = FixedMath.sin(angle/2);
        return ship;
    }

    private Formation readFormation(DataInputStream dis)
        throws IOException
    {
        int type = dis.read();
        Formation formation;
        switch(type)
        {
            case FORMATION_COMPOSITE:
                formation = readCompositeFormation(dis);
                break;
            case FORMATION_CIRCULAR:
                formation = readCircularFormation(dis);
                break;
            case FORMATION_SQUARE:
                formation = readSquareFormation(dis);
                break;
            default:
                throw new IOException("unexpected formation type : "+type);
        }
        return formation;
    }

    private CompositeFormationProxy readCompositeFormation(DataInputStream dis)
        throws IOException
    {
        CompositeFormationProxy formation = new CompositeFormationProxy();
        int size = dis.read();
        formation.children.ensureCapacity(size);
        for(int i=0; i<size; i++)
        {
            int numberAllowed = dis.read();
            Formation child = readFormation(dis);
            formation.addFormation(child, numberAllowed);
        }
        return formation;
    }

    private CircularFormation readCircularFormation(DataInputStream dis)
        throws IOException
    {
        CircularFormation circularFormation = new CircularFormation();
        circularFormation.setCenterX(dis.readInt());
        circularFormation.setCenterZ(dis.readInt());
        circularFormation.setRadius(dis.readInt());
        circularFormation.setSpeed(dis.readInt());
        return circularFormation;
    }

    private SquareFormation readSquareFormation(DataInputStream dis)
        throws IOException
    {
        SquareFormation formation = new SquareFormation();
        formation.setCenterX(dis.readInt());
        formation.setColumns(dis.readInt());
        formation.setColumnSpacing(dis.readInt());
        formation.setRowSpacing(dis.readInt());
        formation.setTopZ(dis.readInt());
        formation.setYRotation(dis.readInt());
        return formation;
    }

    private static final String readString(DataInputStream ins)
        throws IOException
    {
        int length = ins.read();
        byte[] data = new byte[length];
        ins.readFully(data);
        return new String(data);
    }
}
