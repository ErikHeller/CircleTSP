package CircleTSP.gui;
// Based on: www1.cs.columbia.edu/~bert/courses/3137/hw3_files/GraphDraw.java

import CircleTSP.entities.Edge;
import CircleTSP.entities.Point;
import CircleTSP.entities.Tuple;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphDraw extends JFrame {

    private double scaling = 0;
    private double displacementX = 0;
    private double displacementY = 0;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minX = Double.POSITIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    private int width;
    private int height;

    private ArrayList<Point> points;
    private ArrayList<Edge> edges;

    private GraphPanel graph;

    public GraphDraw(int panelHeight, int panelWidth) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graph = new GraphPanel();
        graph.setPreferredSize(new Dimension(panelWidth,panelHeight));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.ipadx = 100;
        c.ipady = 100;

        this.add(graph, c);
        this.setSize(panelWidth+150,panelHeight+150);
        this.setVisible(true);

	    points = new ArrayList<>();
	    edges = new ArrayList<>();
	    this.width = panelWidth;
	    this.height = panelHeight;
    }

    public GraphDraw(String name, int panelHeight, int panelWidth) {
        this(panelHeight, panelWidth);
	    this.setTitle(name);
	    this.setVisible(true);
    }

    class GraphPanel extends JPanel {

        public GraphPanel() {
            super();
        }

        public void paint(Graphics g) {

            FontMetrics f = g.getFontMetrics();
            int nodeHeight = Math.max(20, f.getHeight());

            g.setColor(Color.black);
            for (Edge e : edges) {
                Point p1 = e.getFirst();
                Point p2 = e.getSecond();
                g.drawLine(getGUIcoords(p1).getFirst(), getGUIcoords(p1).getSecond(),
                        getGUIcoords(p2).getFirst(), getGUIcoords(p2).getSecond());
            }

            for (Point p : points) {
                int x = getGUIcoords(p).getFirst();
                int y = getGUIcoords(p).getSecond();
                int nodeWidth = Math.max(20, f.stringWidth(p.getId())+20/2);
                g.setColor(Color.white);
                g.fillOval(x-nodeWidth/2,
                        y-nodeHeight/2,
                        nodeWidth, nodeHeight);
                g.setColor(Color.black);
                g.drawOval(x-nodeWidth/2, y-nodeHeight/2,
                        nodeWidth, nodeHeight);

                g.drawString(p.getId(), x-f.stringWidth(p.getId())/2,
                        y+f.getHeight()/2);
            }
        }
    }

    public void addNode(Point p) {
        double x = p.getCoordinates()[0];
        double y = p.getCoordinates()[1];

        maxX = Math.max(x, maxX);
        maxY = Math.max(y, maxY);
        minX = Math.min(x, minX);
        minY = Math.min(y, minY);

        setScaling();

        points.add(p);
        this.repaint();
    }

    private void setScaling() {
        displacementX = Math.abs(minX);
        displacementY = Math.abs(minY);

        double scalingX = (width / Math.abs(maxX-minX))*0.99;
        double scalingY = (height / Math.abs(maxY-minY))*0.99;

        if (scalingX > scalingY) {
            if (width < (scalingX * (displacementX+maxX))
                    && height <= (scalingX * (displacementY+maxY)))
                scaling = scalingX;
            else
                scaling = scalingY;
        }
        else {
            if (width < (scalingY * (displacementX+maxX))
                    && height <= (scalingY * (displacementY+maxY)))
                scaling = scalingY;
            else
                scaling = scalingX;
        }
    }

    private Tuple<Integer, Integer> getGUIcoords(Point p) {
        int x = (int) Math.round((p.getCoordinates()[0]+displacementX)*scaling) + 50;
        int y = (int) Math.round((p.getCoordinates()[1]+displacementY)*scaling) + 50;
        return new Tuple<>(x, y);
    }

    public void addEdge(Point i, Point j) {
	    //add an Edge between nodes i and j
	    edges.add(new Edge(i,j));
	    this.repaint();
    }
    
    public void paint(Graphics g) { // draw the nodes and edges
	    graph.paint(graph.getGraphics());
    }
}