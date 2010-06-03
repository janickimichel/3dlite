package au.com.zonski.space.client.application;

import au.com.zonski.space.domain.SolarSystem;
import au.com.zonski.space.domain.Body;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.math.FixedMath;
import au.com.zonski.math.MatrixMath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import net.jscience.math.kvm.MathFP;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 11:35:29
 */
public class SpaceCanvas extends Canvas
{
    private SolarSystem system;
    private ProjectionPlane projection;
    private Image buffered;
    private Graphics bufferedGraphics;

//    private int viewAngleX;
//    private int viewAngleY;
//    private int viewAngleZ;

    private int[] viewQuaternion = new int[]{FixedMath.MAX_ONE, 0, 0, 0, FixedMath.MAX_PRECISION};

    private int lastX;
    private int lastY;

    public SpaceCanvas()
    {
        this.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                super.componentResized(e);
                cancelImage();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter()
        {

            public void mouseDragged(MouseEvent e)
            {
                super.mouseDragged(e);
                if(lastX > Integer.MIN_VALUE)
                {
                    if(e.isAltDown())
                    {
                        setAngle(0, 0, FixedMath.toFixed(e.getX() - lastX));
                    }else{
                        setAngle(
                                MathFP.toFP(e.getY() - lastY),
                                MathFP.toFP(e.getX() - lastX),
                                0
                        );
                    }
                    repaint();
                    lastX = e.getX();
                    lastY = e.getY();
                }
            }
        });
        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                lastX = e.getX();
                lastY = e.getY();
            }

            public void mouseReleased(MouseEvent e)
            {
                super.mouseReleased(e);
                lastX = Integer.MIN_VALUE;
                lastY = Integer.MIN_VALUE;
            }
        });

        this.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                {
//                    viewAngleX += ProjectionPlane.sin(viewAngleZ, ProjectionPlane.PRECISION);
//                    viewAngleY -= ProjectionPlane.cos(viewAngleZ, ProjectionPlane.PRECISION);
                    rotateView(new int[]{FixedMath.cos(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0,
                                         FixedMath.sin(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION), 0,
                                         FixedMath.MAX_PRECISION});
                }else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
//                    viewAngleX -= ProjectionPlane.sin(viewAngleZ, ProjectionPlane.PRECISION);
//                    viewAngleY += ProjectionPlane.cos(viewAngleZ, ProjectionPlane.PRECISION);
                    rotateView(new int[]{FixedMath.cos(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0,
                                         FixedMath.sin(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION), 0,
                                         FixedMath.MAX_PRECISION});
                }else if(e.getKeyCode() == KeyEvent.VK_UP){
//                    viewAngleX -= ProjectionPlane.cos(viewAngleZ, ProjectionPlane.PRECISION);
//                    viewAngleY -= ProjectionPlane.sin(viewAngleZ, ProjectionPlane.PRECISION);
                    rotateView(new int[]{FixedMath.cos(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         FixedMath.sin(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0, 0, FixedMath.MAX_PRECISION});
                }else if(e.getKeyCode() == KeyEvent.VK_DOWN){
//                    viewAngleX += ProjectionPlane.cos(viewAngleZ, ProjectionPlane.PRECISION);
//                    viewAngleY += ProjectionPlane.sin(viewAngleZ, ProjectionPlane.PRECISION);
                    rotateView(new int[]{FixedMath.cos(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         FixedMath.sin(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0, 0, FixedMath.MAX_PRECISION});
                }else if(e.getKeyCode() == KeyEvent.VK_COMMA){
//                    viewAngleZ -= ProjectionPlane.ONE;
                    rotateView(new int[]{FixedMath.cos(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0, 0,
                                         FixedMath.sin(FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         FixedMath.MAX_PRECISION});
                }else if(e.getKeyCode() == KeyEvent.VK_PERIOD){
//                    viewAngleZ += ProjectionPlane.ONE;
                    rotateView(new int[]{FixedMath.cos(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         0, 0,
                                         FixedMath.sin(-FixedMath.MAX_ONE/2, FixedMath.MAX_PRECISION),
                                         FixedMath.MAX_PRECISION});
                }
            }
        });

    }

    public void rotateView(int[] quaternion)
    {
        MatrixMath.multiplyQuaternion(this.viewQuaternion,  this.viewQuaternion, quaternion);
        MatrixMath.normalizeQuaternion(this.viewQuaternion);
        repaint();
    }

    public void setAngle(int angleX, int angleY, int angleZ)
    {
        int[] rotation = new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION};
        if(angleX != 0)
        {
            MatrixMath.multiplyQuaternion(rotation, rotation,
                    new int[]{
                        FixedMath.cos(angleX/2, FixedMath.DEFAULT_PRECISION),
                        FixedMath.sin(angleX/2, FixedMath.DEFAULT_PRECISION), 0 , 0,
                        FixedMath.DEFAULT_PRECISION
                    });
        }
        if(angleY != 0)
        {
            MatrixMath.multiplyQuaternion(rotation, rotation,
                    new int[]{
                        FixedMath.cos(angleY/2, FixedMath.DEFAULT_PRECISION),
                        0, FixedMath.sin(angleY/2, FixedMath.DEFAULT_PRECISION), 0,
                        FixedMath.DEFAULT_PRECISION
                    });
        }
        if(angleZ != 0)
        {
            MatrixMath.multiplyQuaternion(rotation, rotation,
                    new int[]{
                        FixedMath.cos(angleZ/2, FixedMath.DEFAULT_PRECISION),
                        0, 0, FixedMath.sin(angleZ/2, FixedMath.DEFAULT_PRECISION),
                        FixedMath.DEFAULT_PRECISION
                    });
        }
        rotateObjects(rotation);
        int[] result = new int[5];
        MatrixMath.adjustPrecision(result, rotation, FixedMath.MAX_PRECISION);
    }

    public void rotateObjects(int[] rotation)
    {
        for(int i=0; i<this.system.bodies.size(); i++)
        {
            Body body = (Body)this.system.bodies.elementAt(i);
            MatrixMath.multiplyQuaternion(body.rotation, body.rotation, rotation);
            MatrixMath.normalizeQuaternion(body.rotation);
        }

        this.repaint();
    }

    private void cancelImage()
    {
        if(this.buffered != null)
        {
            this.buffered = null;
            this.bufferedGraphics.dispose();
            this.bufferedGraphics = null;
        }
        repaint();
    }

    private void draw(Graphics g)
    {
        int[] quaternion = new int[5];
        MatrixMath.adjustPrecision(quaternion, this.viewQuaternion, FixedMath.DEFAULT_PRECISION);
        this.projection = new ProjectionPlane(
                this.getWidth(), this.getHeight(),
                quaternion,
                0, 0, 0
        );
        Vector bodies = system.bodies;
        for(int i=0; i<bodies.size(); i++)
        {
            Body body = (Body)bodies.elementAt(i);
            this.projection.project(body);
        }
        this.projection.build();
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.gray);
        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        g.setColor(Color.white);
//        g.drawString(
//                Integer.toString(FixedMath.getInteger(this.angleX)%360)+"-"+
//                Integer.toString(FixedMath.getInteger(this.angleY)%360),
//                0, g.getFontMetrics().getHeight());
//        g.drawString(
//                Integer.toString(FixedMath.getInteger(this.viewAngleX))+"-"+
//                Integer.toString(FixedMath.getInteger(this.viewAngleY))+"-"+
//                Integer.toString(FixedMath.getInteger(this.viewAngleZ)),
//                0, g.getFontMetrics().getHeight()*2
//        );
        g.drawString(MatrixMath.toString(this.viewQuaternion), 0, g.getFontMetrics().getHeight());
        g.setColor(Color.white);
        this.projection.render(g);
    }

    public void update(Graphics g)
    {
        if(this.buffered == null)
        {
            this.buffered = this.createImage(this.getWidth(), this.getHeight());
            this.bufferedGraphics = this.buffered.getGraphics();
        }
        draw(this.bufferedGraphics);
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(this.buffered != null)
        {
            g.drawImage(this.buffered, 0, 0, this);
        }
    }


    public static final void main(String[] args)
    {
        final Frame frame = new Frame();
        frame.setLayout(new BorderLayout());
        SpaceCanvas canvas = new SpaceCanvas();

        canvas.system = new SolarSystem();

        Mesh shape1 = new Mesh(
                new int[][]
                {
                    {MathFP.toFP(-100), MathFP.toFP(0), MathFP.toFP(0)},
                    {MathFP.toFP(-200), MathFP.toFP(50), MathFP.toFP(-50)},
                    {MathFP.toFP(-200), MathFP.toFP(-50), MathFP.toFP(-50)},
                    {MathFP.toFP(-200), MathFP.toFP(0), MathFP.toFP(50)}
                },
                new byte[][]
                {
                    {2, 1, 0},  // ok
                    {1, 2, 3},  // ok
                    {0, 1, 3},
                    {3, 2, 0}
                }
        );
        Body body1 = new Body(shape1, new int[]{0, 0, MathFP.toFP(-1300)},
                new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION});
        canvas.system.bodies.addElement(body1);

        Mesh shape2 = new Mesh(
                new int[][]
                {
                    {FixedMath.toFixed(150), FixedMath.toFixed(-25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(150), FixedMath.toFixed(25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(200), FixedMath.toFixed(25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(200), FixedMath.toFixed(-25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(150), FixedMath.toFixed(-25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(150), FixedMath.toFixed(25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(200), FixedMath.toFixed(25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(200), FixedMath.toFixed(-25), FixedMath.toFixed(-25)},
                },
                new byte[][]
                {
                    {0, 3, 2, 1},
                    {5, 6, 7, 4},
                    {3, 7, 6, 2},
                    {2, 6, 5, 1},
                    {1, 5, 4, 0},
                    {0, 4, 7, 3}
                }
        );
        Body body2 = new Body(shape2, new int[]{0, 0, FixedMath.toFixed(-1300)},
                new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION});
        //canvas.system.bodies.addElement(body2);

        Mesh shape3 = new Mesh(
                new int[][]
                {
                    {FixedMath.toFixed(-25), FixedMath.toFixed(-25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(-25), FixedMath.toFixed(25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(25), FixedMath.toFixed(25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(25), FixedMath.toFixed(-25), FixedMath.toFixed(25)},
                    {FixedMath.toFixed(-25), FixedMath.toFixed(-25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(-25), FixedMath.toFixed(25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(25), FixedMath.toFixed(25), FixedMath.toFixed(-25)},
                    {FixedMath.toFixed(25), FixedMath.toFixed(-25), FixedMath.toFixed(-25)},
                },
                new byte[][]
                {
                    {0, 3, 2, 1},
                    {5, 6, 7, 4},
                    {3, 7, 6, 2},
                    {2, 6, 5, 1},
                    {1, 5, 4, 0},
                    {0, 4, 7, 3}
                }
        );
        Body body3 = new Body(shape3, new int[]{0, FixedMath.toFixed(200), FixedMath.toFixed(-1100)},
                new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION});
        canvas.system.bodies.addElement(body3);

        Mesh shape4 = new Mesh(
                new int[][]
                {
                    {0, FixedMath.toFixed(20), 0},
                    {0, FixedMath.toFixed(10), FixedMath.toFixed(100)},                      // 1
                    {FixedMath.toFixed(70), FixedMath.toFixed(10), FixedMath.toFixed(70)},   // 2
                    {FixedMath.toFixed(100), FixedMath.toFixed(10), 0},                      // 3
                    {FixedMath.toFixed(70), FixedMath.toFixed(10), FixedMath.toFixed(-70)},  // 4
                    {0, FixedMath.toFixed(10), FixedMath.toFixed(-100)},                     // 5
                    {FixedMath.toFixed(-70), FixedMath.toFixed(10), FixedMath.toFixed(-70)}, // 6
                    {FixedMath.toFixed(-100), FixedMath.toFixed(10), 0},                     // 7
                    {FixedMath.toFixed(-70), FixedMath.toFixed(10), FixedMath.toFixed(70)},  // 8
                    {0, FixedMath.toFixed(-20), 0},                                          // 9
                    {0, FixedMath.toFixed(-10), FixedMath.toFixed(100)},                      // 10
                    {FixedMath.toFixed(70), FixedMath.toFixed(-10), FixedMath.toFixed(70)},   // 11
                    {FixedMath.toFixed(100), FixedMath.toFixed(-10), 0},                      // 12
                    {FixedMath.toFixed(70), FixedMath.toFixed(-10), FixedMath.toFixed(-70)},  // 13
                    {0, FixedMath.toFixed(-10), FixedMath.toFixed(-100)},                     // 14
                    {FixedMath.toFixed(-70), FixedMath.toFixed(-10), FixedMath.toFixed(-70)}, // 15
                    {FixedMath.toFixed(-100), FixedMath.toFixed(-10), 0},                     // 16
                    {FixedMath.toFixed(-70), FixedMath.toFixed(-10), FixedMath.toFixed(70)},  // 17
                },
                new byte[][]
                {
                    // top half
                    {0, 1, 2},
                    {0, 2, 3},
                    {0, 3, 4},
                    {0, 4, 5},
                    {0, 5, 6},
                    {0, 6, 7},
                    {0, 7, 8},
                    {0, 8, 1},
                    // bottom half
                    {9, 11, 10},
                    {9, 12, 11},
                    {9, 13, 12},
                    {9, 14, 13},
                    {9, 15, 14},
                    {9, 16, 15},
                    {9, 17, 16},
                    {9, 10, 17},
                    // joining bits
                    {10, 11, 2, 1},
                    {11, 12, 3, 2},
                    {12, 13, 4, 3},
                    {13, 14, 5, 4},
                    {14, 15, 6, 5},
                    {15, 16, 7, 6},
                    {16, 17, 8, 7},
                    {17, 10, 1, 8}
                }
        );
        Body body4 = new Body(shape4, new int[]{0, 0, FixedMath.toFixed(-1400)},
                new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION});
        canvas.system.bodies.addElement(body4);

        Mesh shape5 = new Mesh(
                new int[][]
                {
                    {FixedMath.toFixed(0), FixedMath.toFixed(0), FixedMath.toFixed(0)},
                    {FixedMath.toFixed(40), FixedMath.toFixed(-40), FixedMath.toFixed(40)},
                    {FixedMath.toFixed(-40), FixedMath.toFixed(-40), FixedMath.toFixed(40)},
                    {FixedMath.toFixed(0), FixedMath.toFixed(-40), FixedMath.toFixed(-40)},
                },
                new byte[][]
                {
                    {0, 2, 1},
                    {0, 3, 2},
                    {0, 1, 3},
                    {1, 2, 3}
                }
        );
        Body body5 = new Body(shape5, new int[]{0, 0,  FixedMath.toFixed(-1300)}, new int[]{
            FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION
        });
        //canvas.system.bodies.addElement(body5);

        Mesh shape6 = new Mesh(
                new int[][]
                {
                    {FixedMath.toFixed(0), FixedMath.toFixed(0), FixedMath.toFixed(0)},
                    {FixedMath.toFixed(100), FixedMath.toFixed(100), FixedMath.toFixed(100)},
                    {FixedMath.toFixed(-100), FixedMath.toFixed(100), FixedMath.toFixed(100)},
                    {FixedMath.toFixed(0), FixedMath.toFixed(100), FixedMath.toFixed(-100)},
                },
                new byte[][]
                {
                    {1, 2, 0},
                    {2, 3, 0},
                    {3, 1, 0},
                    {3, 2, 1}
                }
        );
        Body body6 = new Body(shape6, new int[]{0, 0, FixedMath.toFixed(-1300)}, new int[]{
            FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION
        });
        //canvas.system.bodies.addElement(body6);

        try
        {
            canvas.setSize(480, 480);
            frame.add(canvas, BorderLayout.CENTER);
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    frame.pack();
                    frame.setVisible(true);
                }
            });
            synchronized(SpaceCanvas.class)
            {
                SpaceCanvas.class.wait();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
