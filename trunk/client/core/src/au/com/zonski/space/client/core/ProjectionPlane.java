package au.com.zonski.space.client.core;

import au.com.zonski.shape3d.Mesh;
import au.com.zonski.shape3d.Shape;
import au.com.zonski.shape3d.Composite;
import au.com.zonski.shape3d.AnimatedMesh;
import au.com.zonski.space.domain.Body;
import au.com.zonski.math.MatrixMath;
import au.com.zonski.math.FixedMath;

import java.util.Vector;

import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 15:21:09
 * <p>
 * TODO : ignore intersections outside the window
 * TODO : support spheres
 * TODO : use line bounds before obtaining intersections
 * TODO : support dots
 * TODO : viewers angle
 * ---
 * DONE : perspective
 * DONE : remove duplicate lines/intersection caculations
 * DONE : outline highlighting
 * DONE : remove unused code
 * DONE : clipping out faces outside the viewing trapezoid
  */
public class ProjectionPlane
{
    private Vector projected;
    private int width;
    private int height;

    private int[] rotation;
    private int[][] rotationMatrix;

    private int cx;
    private int cy;
    private int cz;

    public static final int DEFAULT_DEYE = 1000;

    private int deye = DEFAULT_DEYE;

    private int minRadius = 2;
    private int minRadiusf = MathFP.toFP(this.minRadius);


    public ProjectionPlane(int width, int height)
    {
        this.projected = new Vector();
        this.width = width;
        this.height = height;

    }

    public void reset(int[] rotation, int cx, int cy, int cz)
    {
        this.projected.removeAllElements();
        this.rotation = rotation;
        this.rotationMatrix = new int[3][3];
        MatrixMath.getRotationMatrix(this.rotationMatrix, rotation);

        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
    }

    public Vector getProjected()
    {
        return this.projected;
    }

    public void project(Body body)
    {
        Shape shape = body.shape;

        int dx = body.center[0] - cx;
        int dy = body.center[1] - cy;
        int dz = body.center[2] - cz;

        int[] dpoints = new int[]{dx, dy, dz};

        int[] rotateddpoints = MatrixMath.multiplyVector(dpoints, dpoints, this.rotationMatrix);

        int rdxf = rotateddpoints[0];
        int rdyf = rotateddpoints[1];
        int rdzf = rotateddpoints[2];

        int rdx = FixedMath.getInteger(rdxf);
        int rdy = FixedMath.getInteger(rdyf);
        int rdz = FixedMath.getInteger(rdzf);

        int radius = FixedMath.getInteger(shape.getMaxRadius());

        // TODO : rotate the body center around the viewers center by the view angle

        boolean mightBeVisible;
        // check that the body is actually visible at all (not sure about deye check)

        if((rdz - radius) < -this.deye && rdz < 0)
        {

            //double pow = Math.pow(2, Math.abs((double)FixedMath.getInteger(dz)/(double)this.shrinkage));
            int adjustedRadius = (radius * deye)/-rdz;
            // check that the mesh isn't so small that it won't be visible or should
            // be simplified to a point

            if(adjustedRadius >= this.minRadius)
            {
                int screenycenter = (rdy * deye) / -rdz;
                if((screenycenter - adjustedRadius < this.height/2) && (screenycenter + adjustedRadius > -this.height/2))
                {
                    int screenxcenter = (rdx * deye) / -rdz;
                    mightBeVisible = (
                            (screenxcenter - adjustedRadius < this.width/2) &&
                            (screenxcenter + adjustedRadius > -this.width/2)
                            );
//                    if(!mightBeVisible)
//                    {
//                        System.out.println("outside x bounds of screen");
//                    }
                }else{
                    mightBeVisible = false;
                    //System.out.println("outside y bounds of screen "+rdy+" "+screenycenter);
                }
            }else{
                // TODO : simplify to a point if it's reasonably close
                // too far away
                mightBeVisible = false;
                //System.out.println("too small "+body.center[2]+" "+rdz+" "+radius+" -> "+adjustedRadius);
            }
        }else{
            mightBeVisible = false;
            //System.out.println("behind pane "+rdz);
        }

        //mightBeVisible = (rdz - radius) <= 0;

        if(mightBeVisible)
        {
            if(shape instanceof Mesh)
            {
                Mesh mesh = (Mesh)shape;
                int[][] working = body.rotatedMatrix;
                if(working == null)
                {
                    int[][] points = mesh.baseMatrix;
                    working = new int[points.length][points[0].length];
                    body.rotatedMatrix = working;
                }
                addMesh(mesh, body, working, rdxf, rdyf, rdzf);
            }else if(shape instanceof Composite){
                Composite composite = (Composite)shape;
                addComposite(composite, body, rdxf, rdyf, rdzf);
            }else if(shape instanceof Sphere){
                // TODO : sphere
            }else{
                throw new IllegalArgumentException("unrecognised shape type : "+shape.getClass());
            }
        }
    }

    private void addComposite(Composite composite, Body body, int rdxf, int rdyf, int rdzf)
    {
        for(int i=composite.shapes.size(); i>0; )
        {
            i--;
            Shape shape = (Shape)composite.shapes.elementAt(i);
            if(shape instanceof Mesh)
            {
                Mesh mesh = (Mesh)shape;
                int[][] points = mesh.baseMatrix;
                int[][] working = new int[points.length][points[0].length];
                addMesh(mesh, body, working, rdxf, rdyf, rdzf);
            }else if(shape instanceof Composite){
                addComposite((Composite)shape, body, rdxf, rdyf, rdzf);
            }else{
                throw new IllegalArgumentException("unrecognised shape type : "+shape.getClass());
            }
        }
    }

    private void addMesh(Mesh mesh, Body body, int[][] working, int rdxf, int rdyf, int rdzf)
    {
        int[][] points = mesh.baseMatrix;
        //MatrixMath.translate(points, working, dx, dy, dz);
        // project onto the view plane

        //int[][] target = new int[mesh.baseMatrix.length][mesh.baseMatrix[0].length];
        int[][] target = working;
        int[] rotation = new int[5];
        int[][] rotationProjection = new int[3][3];
        if(mesh instanceof AnimatedMesh)
        {
            // we need to redo the rotation
            AnimatedMesh animatedMesh = (AnimatedMesh)mesh;
//            MatrixMath.multiplyQuaternion(rotation, body.rotation, animatedMesh.animation.getRotation());
//            MatrixMath.multiplyQuaternion(rotation, rotation, this.rotation);
            MatrixMath.multiplyQuaternion(rotation, animatedMesh.animation.getRotation(), body.rotation);
            MatrixMath.multiplyQuaternion(rotation, rotation, this.rotation);
            MatrixMath.getRotationMatrix(rotationProjection, rotation);
        }else{
            MatrixMath.multiplyQuaternion(rotation, body.rotation, this.rotation);
            MatrixMath.getRotationMatrix(rotationProjection, rotation);
        }

        MatrixMath.multiply(target, points, rotationProjection);
        //MatrixMath.rotate(target, points, new int[4][4], rx, ry, rz);
        points = target;

        for(int row = points.length; row > 0; )
        {
            row--;
            points[row][0] += rdxf;
            points[row][1] += rdyf;
            points[row][2] += rdzf;
        }

        // TODO : replace with single multiplied out matrix from above!!

        for(int row = points.length; row > 0; )
        {
            row--;
            //points[row][0] += rdx;
            //points[row][1] += rdy;
            //points[row][2] += rdz;
            for(int col=3; col>0; )
            {
                col--;
                //points[row][col] += points[points.length-1][col];
                // convert back to pixel values, after all, that's all the precision we'll need from now on
                //points[row][col] += points[points.length-1][col];
                points[row][col] = FixedMath.getInteger(points[row][col]);
            }

            // perspective
            // TODO : have a cut off point for when we even stop representing dots

            int z = points[row][2];
            if(z >= 0)
            {
                z = -1;
            }
            points[row][0] = (points[row][0] * deye) / -z;
            points[row][1] = (points[row][1] * deye) / -z;
        }


        ProjectionMesh projectionMesh = new ProjectionMesh(points);
        byte[][] faces = mesh.faces;
        projectionMesh.color = mesh.color;

        // work out the mesh's convex hull for intersections and rendering beauty
        // TODO : reuse working somehow
        byte[] stack = new byte[points.length];
        int stackLength = getConvexHull(points, new byte[points.length], stack);
        projectionMesh.hull = stack;
        projectionMesh.hullLength = stackLength;

        // add the convex hull to the lines
        // to reduce intersection detection and allow accurate
        // outlining
        for(int i=stackLength; i>0; )
        {
            i--;
            byte fromIndex = stack[i];
            byte nextIndex = stack[(i+1)%stackLength];
            int[] from = points[fromIndex];
            int[] next = points[nextIndex];
            ProjectionLine line = new ProjectionLine(from, next, fromIndex, nextIndex, true);
            projectionMesh.lines.addElement(line);
        }

        for(int i=faces.length; i>0; )
        {
            i--;
            byte[] faceIndices = faces[i];

            // beckface removal
            int minFaceIndex = faceIndices.length-1;
            int minIndex = faceIndices[minFaceIndex];
            int miny = points[minIndex][1];
            int minx = points[minIndex][0];
            for(int j=faceIndices.length-1; j>0; )
            {
                j--;
                int index = (int)faceIndices[j];
                if(points[index][1] < miny || (points[index][1] == miny && points[index][0] < minx))
                {
                    minFaceIndex = j;
                    miny = points[index][1];
                    minx = points[index][0];
                }
            }

            int preIndex = minFaceIndex-1;
            if(preIndex < 0)
            {
                preIndex = faceIndices.length-1;
            }
            preIndex = faceIndices[preIndex];
            int postIndex = faceIndices[(minFaceIndex+1)%faceIndices.length];

            int prex = points[preIndex][0];
            int prey = points[preIndex][1];
            int postx = points[postIndex][0];
            int posty = points[postIndex][1];

            int predx = prex - minx;
            int predy = prey - miny;
            int postdx = postx - minx;
            int postdy = posty - miny;


            boolean faceOK;
            if(predx == 0 && postdx == 0)
            {
                // it's flat, don't draw it
                //System.out.println("no dx");
                faceOK = false;
            }else if(predx == 0){
                // the incoming line comes from the right
                //System.out.println("no predx "+preIndex+"("+prex+","+prey+") "+minIndex+"("+minx+","+miny+") "+postIndex+"("+postx+","+posty+")");
                faceOK = postdx > 0;
            }else if(postdx == 0){
                // the outbound line goes to the right
                //System.out.println("no postdx "+preIndex+"("+prex+","+prey+") "+minIndex+"("+minx+","+miny+") "+postIndex+"("+postx+","+posty+")");

                faceOK = predx < 0;
            }else{
                // multiply it out a bit, we're losing precision, preferably this multiplier will be the width
                // of the screen since that will ensure that it's longer than almost any value of dx
                int prem = (predy*1024)/predx;
                int postm = (postdy*1024)/postdx;
                //System.out.println("not vertical "+preIndex+"("+prex+","+prey+") "+minIndex+"("+minx+","+miny+") "+postIndex+"("+postx+","+posty+")");
                if(prem == postm)
                {
                    // it's flat
                    faceOK = false;
                }
                else if(prem < 0 && postm < 0 || prem >= 0 && postm >= 0)
                {
                    faceOK = prem > postm;
                }else{
                    faceOK = prem < 0 && postm >= 0;
                }
                // or, more accurately (negatives dys and zeros are impossible in this context)
                //faceOK = predy * postdx > predx * postdy;
//                    double prem = (double)FixedMath.getInteger(predy)/(double)FixedMath.getInteger(predx);
//                    double postm = (double)FixedMath.getInteger(postdy)/(double)FixedMath.getInteger(postdx);
//                    double preAngle = Math.atan(prem);
//                    double postAngle = Math.atan(postm);
//                    System.out.println(preAngle);
//                    System.out.println(postAngle);
//                    faceOK = preAngle < postAngle;
            }

            if(faceOK)
            {
                for(int j=faceIndices.length; j>0; )
                {
                    j--;
                    byte index = faceIndices[j];
                    byte nextIndex = faceIndices[(j+1)%faceIndices.length];

                    // if a line with the same indices exists then we don't need to
                    // add it again
                    boolean found = false;
                    for(int k=projectionMesh.lines.size(); k>0; )
                    {
                        k--;
                        ProjectionLine line = (ProjectionLine)projectionMesh.lines.elementAt(k);
                        if(line.fromIndex == index && line.toIndex == nextIndex ||
                                line.toIndex == index && line.fromIndex == nextIndex)
                        {
                            found = true;
                            break;
                        }else if(line.perimeter){
                            // the line's as long as it can be, hence may contain other lines
                            if(line.fromIndex == index)
                            {
                                int[] common = points[index];
                                int[] linePoint = points[line.toIndex];
                                int[] testPoint = points[nextIndex];
                                if(this.isLeft(common, linePoint, testPoint) == 0)
                                {
                                    found = true;
                                    break;
                                }
                            }else if(line.toIndex == index){
                                int[] common = points[index];
                                int[] linePoint = points[line.fromIndex];
                                int[] testPoint = points[nextIndex];
                                if(this.isLeft(common, linePoint, testPoint) == 0)
                                {
                                    found = true;
                                    break;
                                }
                            }else if(line.fromIndex == nextIndex){
                                int[] common = points[nextIndex];
                                int[] linePoint = points[line.toIndex];
                                int[] testPoint = points[index];
                                if(this.isLeft(common, linePoint, testPoint) == 0)
                                {
                                    found = true;
                                    break;
                                }
                            }else if(line.toIndex == nextIndex){
                                int[] common = points[nextIndex];
                                int[] linePoint = points[line.fromIndex];
                                int[] testPoint = points[index];
                                if(this.isLeft(common, linePoint, testPoint) == 0)
                                {
                                    found = true;
                                    break;
                                }
                            }else{
                                // TODO : implement this...
                                // test to see if the line sits within the other although this
                                // is probably a very unusual case
                                found = false;
                            }
                        }
                    }
                    if(!found)
                    {
                        ProjectionLine line = new ProjectionLine(projectionMesh, index, nextIndex, false);
                        projectionMesh.lines.addElement(line);
                    }
                }
            }else{
                //System.out.println("culled "+i);
            }
        }

        this.projected.addElement(projectionMesh);

    }

    public void build()
    {
        // chop out all the extraneous shit
        for(int i=this.projected.size()-1; i>0; )
        {
            i--;
            ProjectionMesh imesh = (ProjectionMesh)this.projected.elementAt(i);
            for(int j=this.projected.size(); j>i+1; )
            {
                j--;
                ProjectionMesh jmesh = (ProjectionMesh)this.projected.elementAt(j);
                if(overlaps(imesh.bounds2d,  jmesh.bounds2d))
                {
                    ProjectionMesh backMesh;
                    ProjectionMesh frontMesh;
                    if(isInFront(imesh, jmesh))
                    {
                        frontMesh = imesh;
                        backMesh = jmesh;
                    }else{
                        frontMesh = jmesh;
                        backMesh = imesh;
                    }

                    Vector backLines = backMesh.lines;
                    byte[] frontHull = frontMesh.hull;
                    int frontHullLength = frontMesh.hullLength;

                    for(int k=backLines.size(); k>0; )
                    {
                        k--;
                        int numIntersections = 0;
                        //int[][] intersections = new int[frontLines.size()][3];
                        int[][] intersections = new int[5][3];
                        ProjectionLine backLine = (ProjectionLine)backLines.elementAt(k);
//                        System.out.println("back line "+backLine+" of "+this.projected.indexOf(backMesh)+
//                                " compared to polygon "+this.projected.indexOf(frontMesh));
                        boolean remove = false;
//                        for(int l=frontLines.size(); l>0; )
//                        {
//                            l--;
//                            Line frontLine = (Line)frontLines.elementAt(l);
                            //frontLine.reload();
                            //backLine.reload();
                            //if(overlaps(frontLine.bounds2d, backLine.bounds2d))
//                            {
//                                numIntersections = find2dIntersection(
//                                        intersections,
//                                        numIntersections,
//                                        backMesh,
//                                        backLine,
//                                        frontMesh,
//                                        frontLine);
//                            }
//                        }

//                        System.out.println("line : "+backLine.toString());
                        for(int l=frontHullLength; l>0; )
                        {
                            l--;
                            int frontFromIndex = frontHull[l];
                            int frontToIndex = frontHull[(l+1)%frontHullLength];
                            int[] frontFrom = frontMesh.points[frontFromIndex];
                            int[] frontTo = frontMesh.points[frontToIndex];
                            numIntersections = find2dIntersection(
                                    intersections,
                                    numIntersections,
                                    backMesh,
                                    backLine,
                                    frontFrom,
                                    frontTo
                            );
                            if(numIntersections >= intersections.length && l > 0)
                            {
                                numIntersections = trimIntersections(intersections, numIntersections);
                            }
                        }

                        if(numIntersections > 2)
                        {
                            numIntersections = trimIntersections(intersections, numIntersections);
                        }

//                        for(int m=0; m<numIntersections; m++)
//                        {
//                            System.out.println("intersects at ("+intersections[m][0]+","+intersections[m][1]+","+intersections[m][2]+")");
//                        }


                        if(numIntersections < 2)
                        {
                            // although the case of one intersection is technically impossible, due
                            // to rounding errors it can occur, if we only get one intersection then
                            // we ignore it, after all it can't be that far from the truth anyway.
                            // Of course, in the case of zero intersections we're sorted, the
                            // line is outside the polygon

                            // do nothing
                        }else{
                            // two intersections
                            int[] intersection0 = intersections[0];
                            int[] intersection1 = intersections[1];

                            boolean containsIntersection0 = contains(backLine.bounds2d, intersection0);
                            boolean containsIntersection1 = contains(backLine.bounds2d, intersection1);
//                            System.out.println("contains 0 : "+containsIntersection0);
//                            System.out.println("contains 1 : "+containsIntersection1);
                            if(!containsIntersection0 && !containsIntersection1)
                            {

                                int dx0f = intersection0[0] - backLine.from[0];
                                int dy0f = intersection0[1] - backLine.from[1];

                                int dx1f = intersection1[0] - backLine.from[0];
                                int dy1f = intersection1[1] - backLine.from[1];

                                int dx0t = intersection0[0] - backLine.to[0];
                                int dy0t = intersection0[1] - backLine.to[1];

                                int dx1t = intersection1[0] - backLine.to[0];
                                int dy1t = intersection1[1] - backLine.to[1];

                                int h0fsq = dx0f*dx0f + dy0f*dy0f;
                                int h1fsq = dx1f*dx1f + dy1f*dy1f;
                                int h0tsq = dx0t*dx0t + dy0t*dy0t;
                                int h1tsq = dx1t*dx1t + dy1t*dy1t;

                                if(h0fsq > h0tsq && h1fsq > h1tsq || h0fsq < h0tsq && h1fsq < h1tsq)
                                {
                                    // it's completely detached from the polygon
                                    // do nothing
                                }else{
                                    // the line is completely behind the polygon
                                    remove = true;
                                }

                            }else if(containsIntersection0 && containsIntersection1){
                                int[] minimumIntersection;
                                int[] maximumIntersection;
                                // find the minimum along the line
                                // find the maximum along the line

                                int dx0 = intersection0[0] - backLine.from[0];
                                int dy0 = intersection0[1] - backLine.from[1];

                                int dx1 = intersection1[0] - backLine.from[0];
                                int dy1 = intersection1[1] - backLine.from[1];

                                int h0sq = dx0*dx0 + dy0*dy0;
                                int h1sq = dx1*dx1 + dy1*dy1;

//                                System.out.println("h0sq : "+h0sq);
//                                System.out.println("h1sq : "+h1sq);

                                if(h0sq < h1sq)
                                {
                                    minimumIntersection = intersection0;
                                    maximumIntersection = intersection1;
                                }else{
                                    minimumIntersection = intersection1;
                                    maximumIntersection = intersection0;
                                }

                                // contains both
                                // split
                                int[] temp = backLine.to;
                                backLine.to = minimumIntersection;
                                if(maximumIntersection[0] != temp[0] || maximumIntersection[1] != temp[1])
                                {
                                    ProjectionLine line = new ProjectionLine(
                                            maximumIntersection, temp,
                                            backLine.fromIndex, backLine.toIndex,
                                            backLine.perimeter
                                    );
                                    backLines.addElement(line);
                                }
                                if(backLine.to[0] != backLine.from[0] || backLine.to[1] != backLine.from[1])
                                {
                                    backLine.reload();
                                }else{
                                    // if the line has zero length then remove it
                                    remove = true;
                                }
                            }else{
                                int[] intersection;
                                int[] externalIntersection;
                                if(containsIntersection0)
                                {
                                    intersection = intersection0;
                                    externalIntersection = intersection1;
                                }else{
                                    intersection = intersection1;
                                    externalIntersection = intersection0;
                                }
                                // work out whether the line is trimmed before or after the point
                                // of intersection
                                int dxf = externalIntersection[0] - backLine.from[0];
                                int dyf = externalIntersection[1] - backLine.from[1];

                                int dxt = externalIntersection[0] - backLine.to[0];
                                int dyt = externalIntersection[1] - backLine.to[1];

                                int hfsq = dxf*dxf + dyf*dyf;
                                int htsq = dxt*dxt + dyt*dyt;

                                if(hfsq > htsq)
                                {
                                    // trim the front
                                    backLine.to = intersection;
                                }else{
                                    backLine.from = intersection;
                                }
                                if(backLine.to[0] != backLine.from[0] || backLine.to[1] != backLine.from[1])
                                {
                                    backLine.reload();
                                }else{
                                    remove = true;
                                }
                            }
                        }
                        if(remove)
                        {
                            backLines.removeElementAt(k);
                        }
                    }
                }
            }
        }
    }

    public static final int find2dIntersection(int[][] intersections, int index,
                                               ProjectionMesh backFace, ProjectionLine backLine,
                                               int[] frontFrom, int[] frontTo)
    {
        // check that the lines are even in the same vicinity
        // "blur" the edges to account for our rounding errors
        int result;

        int x12 = MathFP.toFP(frontFrom[0]);
        int y12 = MathFP.toFP(frontFrom[1]);
//        int z12 = MathFP.toFP(frontFrom[2]);

        int x22 = MathFP.toFP(frontTo[0]);
        int y22 = MathFP.toFP(frontTo[1]);
//        int z22 = MathFP.toFP(frontTo[2]);

        int minfrontx = Math.min(x12, x22) - FixedMath.DEFAULT_ONE / 2;
        int minfronty = Math.min(y12, y22) - FixedMath.DEFAULT_ONE / 2;
        int maxfrontx = Math.max(x12, x22) + FixedMath.DEFAULT_ONE / 2;
        int maxfronty = Math.max(y12, y22) + FixedMath.DEFAULT_ONE / 2;

        byte backLineIndex = backLine.fromIndex;
        byte nextBackLineIndex = backLine.toIndex;

        int x11 = MathFP.toFP(backFace.points[backLineIndex][0]);
        int y11 = MathFP.toFP(backFace.points[backLineIndex][1]);
        int z11 = backFace.points[backLineIndex][2];

        int x21 = MathFP.toFP(backFace.points[nextBackLineIndex][0]);
        int y21 = MathFP.toFP(backFace.points[nextBackLineIndex][1]);
        int z21 = backFace.points[nextBackLineIndex][2];



        int dx1 = x21 - x11;
        int dy1 = y21 - y11;
        int dz1 = z21 - z11;

        int dx2 = x22 - x12;
        int dy2 = y22 - y12;
//        int dz2 = z22 - z12;


        if(dx1 == 0 && dx2 == 0)
        {
            // they're both vertical lines or points
            result = index;
        }else if(dx1 == 0){
            // l1 is a vertical line
            int m2 = MathFP.div(dy2,dx2);
            int c2 = y22 - MathFP.mul(m2,x22);
            int y = MathFP.mul(x21, m2) + c2;
            int x = x21;

            if(x >= minfrontx && x <= maxfrontx && dy1 != 0)
            {
                // work out the dz of the intersection point
                // cancels out : int z1 = z11 + FixedMath.divide(FixedMath.multiply(dz1, (y - y11)), dy1);
                int z1 = z11 + (dz1 * (y - y11)) / dy1;
//                int z2 = z12 + FixedMath.divide(FixedMath.multiply(dz2, (x - x12)), dx2);
                intersections[index][0] = FixedMath.getInteger(x);
                intersections[index][1] = FixedMath.getInteger(y);
//                intersections[index][2] = Math.min(z1, z2);
                intersections[index][2] = z1;
                result = index+1;
            }else{
                result = index;
            }
        }else if(dx2 == 0){
            // l2 is a vertical line

            int m1 = MathFP.div(dy1, dx1);
            int c1 = y21 - MathFP.mul(m1, x21);
            int y = MathFP.mul(x22, m1) + c1;

            if(y >= minfronty && y <= maxfronty && dy2 != 0)
            {
                // work out the dz of the intersection point
                int x = x22;
                //cancels out int z1 = z11 + MathFP.div(FixedMath.multiply(dz1, (x - x11)), dx1);
                int z1 = z11 + ((dz1 * (x - x11)) / dx1);
//                int z2 = z12 + FixedMath.divide(FixedMath.multiply(dz2, (y - y12)), dy2);
                intersections[index][0] = FixedMath.getInteger(x);
                intersections[index][1] = FixedMath.getInteger(y);
//                intersections[index][2] = Math.min(z1, z2);
                intersections[index][2] = z1;
                result = index + 1;
            }else{
                result = index;
            }
        }else{
            // check for gradient equality
            if(MathFP.mul(dy1, dx2) == MathFP.mul(dy2, dx1))
            {
                result = index;
//                System.out.println("parallel");
            }else{
                int m2 = MathFP.div(dy2, dx2);
                int c2 = y22 - MathFP.mul(m2, x22);
                int m1 = MathFP.div(dy1, dx1);
                int c1 = y21 - MathFP.mul(m1, x21);
                int x = MathFP.div((c2 - c1),(m1 - m2));
                int y = MathFP.mul(m1, x) + c1;
                // go for the larger, more accurate m

//                System.out.print("intersection ("+FixedMath.getInteger(x)+","+FixedMath.getInteger(y)+")");
                // check that it is contained
                if(x >= minfrontx && x <= maxfrontx && y >= minfronty && y <= maxfronty)
                {
                    // work out the dz of the intersection point
                    //int z1 = z11 + FixedMath.divide(FixedMath.multiply(dz1, (x - x11)), dx1);
                    // precision problems for multiplies and divides cancel out here
                    int z1 = z11 + (dz1 * (x - x11))/dx1;
                    //int z2 = z12 + FixedMath.divide(FixedMath.multiply(dz2, (x - x12)), dx2);
                    intersections[index][0] = FixedMath.getInteger(x);
                    intersections[index][1] = FixedMath.getInteger(y);
                    //intersections[index][2] = Math.min(z1, z2);
                    intersections[index][2] = z1;
                    result = index + 1;
//                    System.out.println(" - good");
                }else{
                    result = index;
//                    System.out.println(" - bad");
                }
            }
        }
        return result;
    }

    public static int trimIntersections(int[][] intersections, int numIntersections)
    {
//                            System.out.println("found "+numIntersections+" on "+backLine+
//                                    " of polygon "+this.projected.indexOf(backMesh)+
//                                    " with polygon "+this.projected.indexOf(frontMesh));
        // why do bad things happen to good people: rounding errors can
        // result in more than two results, in this case we aggregate back
        // to two
        while(numIntersections > 2)
        {
            int minhsq = Integer.MAX_VALUE;
            int minIndex = numIntersections;
            for(int m=numIntersections; m>1; )
            {
                m--;
                for(int n=m; n>0; )
                {
                    n--;
                    int[] mintersections = intersections[m];
                    int[] nintersections = intersections[n];
                    int dx = mintersections[0] - nintersections[0];
                    int dy = mintersections[1] - nintersections[1];
                    int hsq = dx*dx + dy*dy;
                    if(hsq < minhsq)
                    {
                        minhsq = hsq;
                        minIndex = m;
                    }
                }
            }
            // remove the min Index
            for(int m=minIndex; m<numIntersections-1; m++)
            {
                intersections[m] = intersections[m+1];
            }
            numIntersections--;
        }
//                            System.out.println("becomes...");
//                            for(int m=0; m<numIntersections; m++)
//                            {
//                                System.out.println("intersects at ("+intersections[m][0]+","+intersections[m][1]+","+intersections[m][2]+")");
//                            }

        return numIntersections;
    }

    /**
     * Obtains the indices of the convex hull for a collection of points
     * @return
     */
    private int getConvexHull(int[][] points, byte[] sorted, byte[] stack)
    {
        // TODO : use faster algorithm
        int southmostIndex = points.length-1;
        int[] southmost = points[southmostIndex];
        for(int i=southmostIndex; i>0; )
        {
            i--;
            int[] point = points[i];
            if(point[1] < southmost[1] ||
                    point[1] == southmost[1] && point[0] < southmost[0] ||
                    point[1] == southmost[1] && point[0] == southmost[0] && point[2] > southmost[0])
            {
                southmost = point;
                southmostIndex = i;
            }
        }

//        System.out.println("southmost ("+southmost[0]+","+southmost[1]+","+southmost[2]+")");

        // sort the points in order of appearance
        sorted[0] = (byte)southmostIndex;
        int sortedLength = 1;
        for(int i=points.length; i>0; )
        {
            i--;
            if(i != southmostIndex)
            {
                // insert and order the point
                int[] point = points[i];
                // binary search
                int bottom = 1;
                int top = sortedLength;
                boolean skipped = false;
                while(bottom < top)
                {
                    int mid = (top + bottom)/2;
                    byte midpointIndex = sorted[mid];
                    int[] midpoint = points[midpointIndex];
                    int left = isLeft(southmost, point, midpoint);
                    if(left < 0)
                    {
                        bottom = mid + 1;
                    }else if(left > 0){
                        top = mid;
                    }else{
                        // discard the closest point, if equal then discard the rear-most point
                            int dp = getDistanceSquared(southmost, point);
                            int dm = getDistanceSquared(southmost, midpoint);
//                            if(dp < dm)
//                            {
//                                bottom = mid + 1;
//                            }else{
//                                top = mid;
//                            }
                        if(dp > dm)
                        {
                            sorted[mid] = (byte)i;
                        }
//                            System.out.println("skipping ("+point[0]+","+point[1]+","+point[2]+")");
                        skipped = true;
                        break;
                    }
                }
                if(!skipped)
                {
                    // insert the point at top
                    for(int j=sortedLength; j>bottom; )
                    {
                        j--;
                        sorted[j+1] = sorted[j];
                    }
                    sorted[bottom] = (byte)i;
                    sortedLength++;
                }
            }
        }

//        System.out.println("sorted");
//        for(int i=0; i<sortedLength; i++)
//        {
//            int index = sorted[i];
//            int[] point = points[index];
//            System.out.println("("+point[0]+","+point[1]+","+point[2]+")");
//        }

        // make our stack
        // TODO : array copy for two values might be faster
        stack[0] = sorted[0];
        stack[1] = sorted[1];
        int stackLength = 2;
        int i = 2;
        while(i < sortedLength)
        {
            byte index = sorted[i];
            int[] point = points[index];
            int index1 = stack[stackLength-1];
            int index2 = stack[stackLength-2];
            int[] point1 = points[index1];
            int[] point2 = points[index2];
            int left = isLeft(point2, point1, point);
            if(left > 0)
            {
                stack[stackLength] = index;
                i++;
                stackLength++;
            }else{
                stackLength--;
            }
        }
        return stackLength;
    }


    private int isLeft(int[] p0, int[] p1, int[] p2)
    {
        return (p1[0] - p0[0]) * (p2[1] - p0[1]) - (p2[0] - p0[0]) * (p1[1] - p0[1]);
    }

    private int getDistanceSquared(int[] p0, int[] p1)
    {
        int x0 = p0[0];
        int y0 = p0[1];
        int x1 = p1[0];
        int y1 = p1[1];

        int dx = x1 - x0;
        int dy = y1 - y0;

        return dx*dx + dy*dy;
    }


    private class Sphere
    {
        int[] center;
        public Sphere()
        {
        }
    }


    public static final boolean contains(int[] r, int[] p)
    {
        int minx = r[0];
        int miny = r[1];
        int maxx = r[2];
        int maxy = r[3];

        int px = p[0];
        int py = p[1];

        return (minx <= px && maxx >= px && miny <= py && maxy >= py);
    }

    public static final boolean overlaps(int[] r1, int[] r2)
    {
        int minx1 = r1[0];
        int miny1 = r1[1];
        int maxx1 = r1[2];
        int maxy1 = r1[3];
        int minx2 = r2[0];
        int miny2 = r2[1];
        int maxx2 = r2[2];
        int maxy2 = r2[3];
        //System.out.println("[("+minx1+","+miny1+")->("+maxx1+","+maxy1+")]vs[("+minx2+","+miny2+")->("+maxx2+","+maxy2+")]");
        return ((minx1 >= minx2 && maxx2 >= minx1) || (minx2 >= minx1 && maxx1 >= minx2) ||
                (maxx1 >= minx2 && maxx2 >= maxx1) || (maxx2 >= minx1 && maxx1 >= maxx2)) &&
                //(maxx1 == minx1 && maxx2 >= maxx1 && minx2 <= maxx1) ||
                //(maxx2 == minx2 && maxx1 >= maxx2 && minx1 <= maxx2)) &&
               ((miny1 >= miny2 && maxy2 >= miny1) || (miny2 >= miny1 && maxy1 >= miny2) ||
                (maxy1 >= miny2 && maxy2 >= maxy1) || (maxy2 >= miny1 && maxy1 >= maxy2));
    }

    public static final boolean overlaps(
            int minx1, int miny1, int maxx1, int maxy1,
            int minx2, int miny2, int maxx2, int maxy2
            )
    {
        //System.out.println("[("+minx1+","+miny1+")->("+maxx1+","+maxy1+")]vs[("+minx2+","+miny2+")->("+maxx2+","+maxy2+")]");
        return ((minx1 >= minx2 && maxx2 >= minx1) || (minx2 >= minx1 && maxx1 >= minx2) ||
                (maxx1 >= minx2 && maxx2 >= maxx1) || (maxx2 >= minx1 && maxx1 >= maxx2)) &&
                //(maxx1 == minx1 && maxx2 >= maxx1 && minx2 <= maxx1) ||
                //(maxx2 == minx2 && maxx1 >= maxx2 && minx1 <= maxx2)) &&
               ((miny1 >= miny2 && maxy2 >= miny1) || (miny2 >= miny1 && maxy1 >= miny2) ||
                (maxy1 >= miny2 && maxy2 >= maxy1) || (maxy2 >= miny1 && maxy1 >= maxy2));
    }

    public static final boolean isInFront(ProjectionMesh m1, ProjectionMesh m2)
    {
//        int totalz1 = 0;
//        int totalz2 = 0;
//        for(int row=m1.points.length-1; row>0; )
//        {
//            row --;
//            totalz1 += m1.points[row][2];
//        }
//        for(int row = m2.points.length-1; row>0; )
//        {
//            row--;
//            totalz2 += m2.points[row][2];
//        }
        // another way of saying
        // totalz1/m1.points.length > totalz2/m2.points.length;
//        return totalz1*(m2.points.length-1) > totalz2 * (m1.points.length-1);
        // the zero-th point is always the center-most
        return m2.points[m2.points.length-1][2] < m1.points[m1.points.length-1][2];
    }
}
