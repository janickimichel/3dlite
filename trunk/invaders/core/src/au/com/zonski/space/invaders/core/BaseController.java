package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.shape3d.Shape;
import au.com.zonski.shape3d.Composite;
import au.com.zonski.math.MatrixMath;
import au.com.zonski.math.FixedMath;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 9/05/2005
 * Time: 10:29:22
 */
public abstract class BaseController implements Controller
{
    public static final Random RANDOM = new Random();

    private static final int[][] ROTATIONS = new int[][]{
        {FixedMath.cos(FixedMath.DEFAULT_ONE), FixedMath.sin(FixedMath.DEFAULT_ONE), 0, 0, FixedMath.DEFAULT_PRECISION},
        {FixedMath.cos(FixedMath.DEFAULT_ONE), 0, FixedMath.sin(FixedMath.DEFAULT_ONE), 0, FixedMath.DEFAULT_PRECISION},
        {FixedMath.cos(FixedMath.DEFAULT_ONE), 0, 0, FixedMath.sin(FixedMath.DEFAULT_ONE), FixedMath.DEFAULT_PRECISION}
    };

    private static final int MAX_FRAGMENT_SPEED_DIVISOR = 5;
    private static final int MAX_FRAGMENT_LIFE_SPAN = 50;

    public void update(Body body, int elapsedTime)
    {
        Ship ship = (Ship)body;
        update(ship, elapsedTime, ship.speed);
    }

    protected void update(Ship ship, int elapsedTime, int speed)
    {
        int[][] points = new int[][]{{0, 0, -FixedMath.multiply(elapsedTime, speed)}};
        int[][] rotationMatrix = new int[3][3];
        int[][] result = new int[1][3];
        MatrixMath.getRotationMatrix(rotationMatrix, ship.rotation);
        MatrixMath.multiply(result, points, rotationMatrix);
        ship.center[0] += result[0][0];
        ship.center[1] += result[0][1];
        ship.center[2] += result[0][2];

        // if the projectile is outside the bounds of the arena, remove it
        Level level = ship.getLevel();
        int radius = ship.shape.getMaxRadius();
        if(ship.center[2] - radius > 0 || ship.center[2] + radius < -level.getHeight() ||
                ship.center[0] < level.getMinx()*2 || ship.center[0] > level.getMaxx()*2)
        {
            ship.setRemove(true);
        }
    }

    public void collision(Body controlled, Body with)
    {
        if(with instanceof Ship && controlled instanceof Ship)
        {
            Ship controlledShip = (Ship)controlled;
            Ship withShip = (Ship)with;
            controlledShip.health -= withShip.preCollisionHealth;
            if(controlledShip.health <= 0)
            {
                controlledShip.setRemove(true);
                explode(controlledShip);
            }
        }
    }

    protected void explode(Ship body)
    {
        explode(body.shape, body, RANDOM);
    }

    private void explode(Shape shape, Ship body, Random random)
    {
        if(shape instanceof Mesh)
        {
            explode((Mesh)shape, body, random);
        }else if(shape instanceof Composite){
            Composite c = (Composite)shape;
            for(int i=c.shapes.size(); i>0; )
            {
                i--;
                Shape child = (Shape)c.shapes.elementAt(i);
                explode(child, body, random);
            }
        }
    }

    private void explode(Mesh mesh, Ship body, Random random)
    {
        int[] center = body.center;
        for(int i=0; i<mesh.faces.length; i++)
        {
            // rotate
            int[][] points = mesh.baseMatrix;
            int[][] rotationMatrix = new int[3][3];
            MatrixMath.getRotationMatrix(rotationMatrix, body.rotation);
            int[][] result = body.rotatedMatrix;
            if(result == null)
            {
                result = new int[points.length][points[0].length];
            }
            points = MatrixMath.multiply(result, points, rotationMatrix);

            byte[] face = mesh.faces[i];
            int[][] newPoints = new int[face.length][];
            byte[][] newFaces = new byte[0][];

            int tx = 0;
            int ty = 0;
            int tz = 0;

            for(int j=0; j<face.length; j++)
            {
                int[] p = points[face[j]];
                tx += p[0];
                ty += p[1];
                tz += p[2];
            }
            tx /= face.length;
            ty /= face.length;
            tz /= face.length;

            for(int j=0; j<face.length; j++)
            {
                int[] p = points[face[j]];
                int[] newp = new int[p.length];
                newp[0] = p[0] - tx;
                newp[1] = p[1] - ty;
                newp[2] = p[2] - tz;
                newPoints[j] = newp;
//                newFaces[0][j] = (byte)j;
//                newFaces[1][j] = (byte)(face.length - j - 1);
            }

            Mesh newMesh = new Mesh(newPoints, newFaces);
            newMesh.maxradius = mesh.maxradius;
            newMesh.color = mesh.color;
            Body newBody = new Body(newMesh,
                    new int[]{center[0] + tx, center[1] + ty, center[2] + tz},
                    new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION}
            );
            int rotationIndex = Math.abs(random.nextInt()%ROTATIONS.length);
            int[] rotation = ROTATIONS[rotationIndex];
            newBody.controller = new FragmentController(
//                    tx/(Math.abs(random.nextInt()%MAX_FRAGMENT_SPEED_DIVISOR) + 1),
//                    ty/(Math.abs(random.nextInt()%MAX_FRAGMENT_SPEED_DIVISOR) + 1),
//                    tz/(Math.abs(random.nextInt()%MAX_FRAGMENT_SPEED_DIVISOR) + 1),
                    tx/MAX_FRAGMENT_SPEED_DIVISOR,
                    ty/MAX_FRAGMENT_SPEED_DIVISOR,
                    tz/MAX_FRAGMENT_SPEED_DIVISOR,
                    rotation,
                    Math.abs(random.nextInt() % MAX_FRAGMENT_LIFE_SPAN) + 1,
                    mesh.color
            );
            body.getLevel().addBody(newBody, Game.NEUTRAL_SIDE);
        }
    }
}
