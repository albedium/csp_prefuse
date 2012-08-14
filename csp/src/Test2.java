

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

public class Test2 {
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
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\hp\\workspace\\csp_prefuse\\src\\polblogs.gml"));
		graph = new Graph();
		graph.addColumn("id",Integer.class);
		graph.addColumn("label",String.class);
		graph.addColumn("value",Integer.class);
		graph.addColumn("source",String.class);
		
		String s = br.readLine();
		StringTokenizer st;
		while(s!=null) {
			if(s.contains("node")) {
				Node n = graph.addNode();
				s = br.readLine().trim();
				st = new StringTokenizer(s," ");
				st.nextToken();
				n.set("id",Integer.parseInt(st.nextToken()));
				s = br.readLine().trim();
				n.set("label",s.substring(7,s.length()-1));
				st = new StringTokenizer(br.readLine().trim());
				st.nextToken();
				n.set("value",Integer.parseInt(st.nextToken()));
				s = br.readLine().trim();
				n.set("source",s.substring(8,s.length()-1));
				//System.out.println(n.get("id")+" "+n.get("label")+" "+n.get("value")+" "+n.get("source"));
			}
			else if(s.contains("edge")) {
				st = new StringTokenizer(br.readLine().trim()); st.nextToken();
				int n1 = Integer.parseInt(st.nextToken());
				st = new StringTokenizer(br.readLine().trim()); st.nextToken();
				int n2 = Integer.parseInt(st.nextToken());
				//System.out.println(n1+" "+n2);
				graph.addEdge(n1-1,n2-1);
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
	   /*
	   ShapeRenderer r = new ShapeRenderer();
	   vis.setRendererFactory(new DefaultRendererFactory(r));
	   */
	}
	
	public static void setUpActions()
	{        
	   // We must color the nodes of the graph.  
	   // Notice that we refer to the nodes using the text label for the graph,
	   // and then appending ".nodes".  The same will work for ".edges" when we
	   // only want to access those items.
	   // The ColorAction must know what to color, what aspect of those 
	   // items to color, and the color that should be used.
	   ColorAction fill = new ColorAction("graph.nodes", 
	                                       VisualItem.FILLCOLOR,
	                                       ColorLib.rgb(0, 200, 0));
	       
	   // Similarly to the node coloring, we use a ColorAction for the 
	   // edges
	   ColorAction edges = new ColorAction("graph.edges",
	                                       VisualItem.STROKECOLOR,
	                                       ColorLib.gray(200));
	        
	   // Create an action list containing all color assignments
	   // ActionLists are used for actions that will be executed
	   // at the same time.  
	   ActionList color = new ActionList();
	   color.add(fill);
	   color.add(edges);
	        
	   // The layout ActionList recalculates 
	   // the positions of the nodes.
	   ActionList layout = new ActionList(Activity.INFINITY);
	        
	   // We add the layout to the layout ActionList, and tell it
	   // to operate on the "graph".
	   layout.add(new RandomLayout("graph"));
	   layout.add(new ForceDirectedLayout("graph", true));
	        
	   // We add a RepaintAction so that every time the layout is 
	   // changed, the Visualization updates it's screen.
	   layout.add(new RepaintAction());
	        
	   // add the actions to the visualization
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