
import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.event.*;


import java.util.*;
import javax.swing.*;
import prefuse.*;
import prefuse.action.*;
import prefuse.action.assignment.*;
import prefuse.action.layout.*;
import prefuse.action.layout.graph.*;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.*;
import prefuse.data.io.*;
import prefuse.render.*;
import prefuse.util.*;
import prefuse.visual.*;
import prefuse.visual.expression.*;

public class Test {
	private static Graph graph;
	private static Visualization vis;
	private static Display d;
	
	public static void main(String[] args) throws Exception {
		setUpData();
		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();
		JFrame frame = new JFrame("Amazon Books");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(d);
		frame.pack();
		frame.setVisible(true);
		vis.run("color");
		vis.run("layout");
		
	}
	
	public static void setUpData() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\hp\\workspace\\csp_prefuse\\src\\polbooks.gml"));
		graph = new Graph();
		graph.addColumn("id",Integer.class);
		graph.addColumn("label",String.class);
		graph.addColumn("value",Character.class);
		
		String s = br.readLine();
		StringTokenizer st;
		while(s!=null) {
			if(s.contains("node")) {
				Node n = graph.addNode();
				s = br.readLine();
				s = br.readLine().trim();
				st = new StringTokenizer(s," ");
				st.nextToken();
				n.set("id",Integer.parseInt(st.nextToken()));
				s = br.readLine().trim();
				n.set("label",s.substring(7,s.length()-1));
				st = new StringTokenizer(br.readLine().trim());
				st.nextToken();
				n.set("value",st.nextToken().charAt(1));
				//System.out.println(n.get("id")+" "+n.get("label")+" "+n.get("value"));
			}
			else if(s.contains("edge")) {
				s = br.readLine();
				st = new StringTokenizer(br.readLine().trim()); st.nextToken();
				int n1 = Integer.parseInt(st.nextToken());
				st = new StringTokenizer(br.readLine().trim()); st.nextToken();
				int n2 = Integer.parseInt(st.nextToken());
				//System.out.println(n1+" "+n2);
				graph.addEdge(n1,n2);
			}
			s = br.readLine();
		}
	}
	
	public static void setUpVisualization() {
		vis = new Visualization();
		vis.add("graph", graph);
	}
	
	public static void setUpRenderers() {
		 ShapeRenderer r = new ShapeRenderer();
	   DefaultRendererFactory drf = new DefaultRendererFactory(r);
	   LabelRenderer lr = new LabelRenderer("id");
	   lr.setRoundedCorner(8,8);
	   drf.add(new InGroupPredicate("nodedec"), lr);
	   vis.setRendererFactory(drf);
	   final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
	   DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); 
	   DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, 
	                               ColorLib.rgb(0, 0, 0)); 
	   DECORATOR_SCHEMA.setDefault(VisualItem.FONT, 
	                               FontLib.getFont("Tahoma",7));
	        
	   vis.addDecorators("nodedec", "graph.nodes", DECORATOR_SCHEMA);
	   /*
	   ShapeRenderer r = new ShapeRenderer();
	   vis.setRendererFactory(new DefaultRendererFactory(r));
	   */
	}
	
	public static void setUpActions() {
		int[] palette = {ColorLib.rgb(255, 0, 0), ColorLib.rgb(0,255, 255), ColorLib.rgb(0,255,0)};
		DataColorAction fill = new DataColorAction("graph.nodes", "value",
	                                           Constants.NOMINAL,
	                                           VisualItem.FILLCOLOR, 
	                                           palette);
	   ColorAction edges = new ColorAction("graph.edges",
	                                       VisualItem.STROKECOLOR,
	                                       ColorLib.gray(200));
	   ActionList color = new ActionList();
	   color.add(fill);
	   color.add(edges);
	   ActionList layout = new ActionList(Activity.INFINITY);
	   layout.add(new ForceDirectedLayout("graph", true));
	   layout.add(new FinalDecoratorLayout("nodedec"));
	   layout.add(new RepaintAction());
	   vis.putAction("color", color);
	   vis.putAction("layout", layout);
	}
	
	public static void setUpDisplay() {
		d = new Display(vis);
		d.setSize(500, 400);
		d.addControlListener(new FocusControl(1));
    d.addControlListener(new DragControl());
    d.addControlListener(new PanControl());
    d.addControlListener(new ZoomControl());
    d.addControlListener(new WheelZoomControl());
    d.addControlListener(new ZoomToFitControl());
    d.addControlListener(new FinalControlListener());
    d.addControlListener(new NeighborHighlightControl());
		
	}
}
class FinalControlListener extends ControlAdapter implements Control {
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item instanceof NodeItem)
		{			
			JPopupMenu jpub = new JPopupMenu();
			jpub.add("Id: " + (Integer)(item.get("id")));
			jpub.add("Label: " + (String)(item.get("label")));
			jpub.add("Value: "+(Character)(item.get("value")));
			jpub.show(e.getComponent(),(int) item.getX(),
                            (int) item.getY());
		}
	}
}
class FinalDecoratorLayout extends Layout
{
    public FinalDecoratorLayout(String group) {
        super(group);
    }

    public void run(double frac) {
        Iterator iter = m_vis.items(m_group);
        while ( iter.hasNext() ) {
            DecoratorItem decorator = (DecoratorItem)iter.next();
            VisualItem decoratedItem = decorator.getDecoratedItem();
            Rectangle2D bounds = decoratedItem.getBounds();
            
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();
            
            setX(decorator, null, x);
            setY(decorator, null, y);
        }
    }
}
//http://www.visualisingdata.com/index.php/2011/03/part-2-the-essential-collection-of-visualisation-resources/