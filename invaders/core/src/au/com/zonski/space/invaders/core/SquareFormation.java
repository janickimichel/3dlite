package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/05/2005
 * Time: 10:10:08
 */
public class SquareFormation implements Formation
{
    private int columns;
    private int columnSpacing;
    private int rowSpacing;
    private int centerX;
    private int topZ;
    private int yRotation;

    private int size;

    public SquareFormation()
    {
    }

    public int getCenterX()
    {
        return centerX;
    }

    public void setCenterX(int centerX)
    {
        this.centerX = centerX;
    }

    public int getTopZ()
    {
        return topZ;
    }

    public void setTopZ(int topZ)
    {
        this.topZ = topZ;
    }

    public int getYRotation()
    {
        return this.yRotation;
    }

    public void setYRotation(int yRotation)
    {
        this.yRotation = yRotation;
    }

    public int getColumns()
    {
        return columns;
    }

    public void setColumns(int columns)
    {
        this.columns = columns;
    }

    public int getColumnSpacing()
    {
        return columnSpacing;
    }

    public void setColumnSpacing(int columnSpacing)
    {
        this.columnSpacing = columnSpacing;
    }

    public int getRowSpacing()
    {
        return rowSpacing;
    }

    public void setRowSpacing(int rowSpacing)
    {
        this.rowSpacing = rowSpacing;
    }

    public boolean isBreakable()
    {
        return true;
    }

    public int position(Body body, int[] center)
    {
        FormationController formationController;
        formationController = (FormationController)body.controller;
        int row = formationController.formationIndex / this.columns;
        int column = formationController.formationIndex % this.columns;
        int x = this.centerX - ((this.columns-1) * this.columnSpacing)/2 + (column * this.columnSpacing);
        int z = this.topZ + row * this.rowSpacing;
        center[0] = x;
        center[2] = z;
        return this.yRotation;
    }

    public void update(int multiplier)
    {
        // TODO : move the center around a bit
    }

    public void add(Body body)
    {
        if(body.controller instanceof FormationController)
        {
            FormationController formationController;
            formationController = (FormationController)body.controller;
            formationController.formationIndex = this.size;
            this.size++;
        }
    }

    public Formation copy()
    {
        SquareFormation square = new SquareFormation();
        square.setCenterX(this.centerX);
        square.setTopZ(this.topZ);
        square.setColumns(this.columns);
        square.setColumnSpacing(this.columnSpacing);
        square.setRowSpacing(this.rowSpacing);
        square.setYRotation(this.yRotation);
        return square;
    }
}
