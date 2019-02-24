package ANTLR_NPP;

import java.util.ArrayList;

public class mxCellStructure {
	
	Property properties;
	Geometry mxGeometry;
	iOOBNCell extraProperties;
	ArrayList<String> tuples;
	
	// className and instanceName are used for adding in node ids of an instance and 
	// source and target in an edge 
	// Asia2.class::B_2 = className.class::instanceName_2
	
	public String className;
	public String instanceName;
	
	public mxCellStructure(){
		this.properties = new Property();
		this.mxGeometry = new Geometry();
		this.extraProperties = new iOOBNCell();
		this.className = "";
		this.instanceName = "";
		tuples = new ArrayList<String>();
	}
	
	public mxCellStructure(Property properties, Geometry mxGeometry, iOOBNCell extraProp, ArrayList<String> tuples){
		this.properties = properties;
		this.mxGeometry = mxGeometry;
		this.extraProperties = extraProp;
		this.tuples = tuples;
	}
	
	public class iOOBNCell{
		ArrayList<String> states;// state names : true false, this is same in iOOBN and Basic node of the mxCell
		ArrayList<String> parents; // id of parents, this is same in iOOBN and Basic node of the mxCell
		/*.class representation:
			 <tuple value="C$True$^$False$^$" />
			<tuple value="L$True$False$True$False$" />
			<tuple value="^$^$^$^$^$" />
			<tuple value="True$0.5$0.5$0.5$0.5$" />
			<tuple value="False$0.5$0.5$0.5$0.5$" />
		 * 
		  Visual representation:
		  The above lines represent a table of the form
		 		R:
				 -----------------------------------------------------
				   C	|	True	|		^	|	False	|	^
				 -------|-----------|-----------|-----------|---------
				   L	|	True	|	False	|	True	|	False
				 -------|-----------|-----------|-----------|---------
				   ^    |    ^      |    ^      |    ^      |   ^   
				 -------|-----------|-----------|-----------|---------
				 True	|	0.5		|	0.5     |   0.5     |   0.5
				 -------|-----------|-----------|-----------|---------
				 False	|	0.5		|	0.5     |   0.5     |   0.5
				 -----------------------------------------------------
		 *
		Hugin oobn representation: 	 
			potential (R | C L)
		    {
			data = ((( 0.5 0.5 )	%  C=True  L=True
				 ( 0.5 0.5 ))	%  C=True  L=False
				(( 0.5 0.5 )	%  C=False  L=True
				 ( 0.5 0.5 )));	%  C=False  L=False
		    }
		    
		Internal iOOBN data representation:
		   data = (
		            (
		               ( 0.5 0.5 )	
				       ( 0.5 0.5 )
				    )	
				    (
				       ( 0.5 0.5 )	
				        ( 0.5 0.5 )
				    )
				 );	
				 
		This representation needs to be converted to .class for ioobn .class code
		
		 */
		
		ArrayList<String> tuples;// above is for iOOBN code, though in mxCell, it is a 2D array of objects
		// hence still need to decide Hugin OOBN to 2D object should be done or ArrayList of String should be done
		public iOOBNCell(){
			this.states = new ArrayList<String>();
			this.parents = new ArrayList<String>();
			this.tuples = new ArrayList<String>();
		}
		
		public iOOBNCell(ArrayList<String> states, ArrayList<String> parents, ArrayList<String> tuples){
			this.states = states;
			this.parents = parents;
			this.tuples = tuples;
		}
	}
	
	public class Geometry{
		String as;
		String relative;
		double height;
		double width;
		int xcoord;
		int ycoord;
		ArrayList<Point> mxPoints;
		
		public Geometry(){
			this.as = "";
			this.relative = "";
			this.height = 0.0;
			this.width = 0.0;
			this.xcoord = 0;
			this.ycoord = 0;
			this.mxPoints = new ArrayList<Point>();
		}
		
		public Geometry(String as, String relative, double height, double width, int xcoord, int ycoord, ArrayList<Point> mxPoints){
			this.as = as;
			this.relative = relative;
			this.height = height;
			this.width = width;
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.mxPoints = mxPoints;
		}
		
		public String toString(){
			String str = "";
			str += "\t\t\t<mxGeometry ";
			
			if(this.relative == ""){ // i.e. for nodes
				str += "as=\"" + this.as + "\" " + "height=\"" + this.height + "\" " + "width=\"" + this.width + "\" "+ "x=\"" + (double)this.xcoord + "\" "+ "y=\"" + (double)this.ycoord + "\"";
				if(this.mxPoints.size() == 0)
					str += "/>\n";
				else{
					str += ">\n";
					for(Point p : this.mxPoints)
						str += p.toString();
					str += "\t\t\t</mxGeometry>\n";
				}
			}
			else{// for edges
				str += "as=\"" + this.as + "\" " + "relative=\"" + this.relative + "\"";
				if(this.mxPoints.size() == 0)
					str += "/>\n";
				else{
					str += ">\n";
					for(Point p : this.mxPoints)
						str += p.toString();
					str += "\t\t\t</mxGeometry>\n";
				}
			}
			return str;
		}
	}
	
	public class Point{
		String as;
		int xcoord;
		int ycoord;
		
		public Point(){
			this.as = "";
			this.xcoord = 0;
			this.ycoord = 0;
		}
		public Point(String as, int xcoord, int ycoord){
			this.as = as;
			this.xcoord = xcoord;
			this.ycoord = ycoord;
		}
		
		public String toString(){
			String str = "";
			str += "\t\t\t\t<mxPoint ";
			str += "as=\"" + this.as + "\" x=\"" + (double)this.xcoord + "\" y= \"" + (double)this.ycoord + "\"";
			str += "/>\n";
			return str;
		}
	}
	
	public class Property{
		String id;
		String parentId;
		Style style;
		String value;// equal to label in iOOBN code
		String vertex;
		String edge;
		String source;
		String target;
		String name; // required for iOOBN code generation 
		String type; // required for iOOBN code generation
		String sub_type; // required for iOOBN code generation
		int kind;
		public Property(){
			this.id = "";
			this.parentId = "";
			this.style = new Style();
			this.value = "";
			this.vertex = "";
			this.edge = "";
			this.source = "";
			this.target = "";
			this.name = "";
			this.type = "";
			this.sub_type = "";
			this.kind = 2;// default kind is embedded
		}
		
		// kind = 0, 1, 2 : Input, output, embedded
		public Property(String id, String parentId,  Style style, String value, String vertex, String edge, String source, String target, String name, String type, String sub_type, int kind){
			this.id = id;
			this.parentId = parentId;
			this.style = style;
			this.value = value;
			this.vertex = vertex;
			this.edge = edge;
			this.source = source;
			this.target = target;
			this.name = name;
			this.type = type;
			this.sub_type = sub_type;
			this.kind = kind;
		}
				
		public String toString(){
			// id="Asia2.class::B_6" parent="4" style="ellipse" value="R" vertex="1"
			String str = "";
			if(!mxCellStructure.this.className.equalsIgnoreCase(""))
				str += " id=\"" + mxCellStructure.this.className + ".class::" + mxCellStructure.this.instanceName + "_" + this.id + "\" ";
			else str += " id=\"" + this.id + "\" ";
			
			if(this.parentId != "")
				str += "parent=\"" + this.parentId + "\" ";
			if(this.style.shape != "")
				str += this.style.toString();
			if(this.vertex.equalsIgnoreCase("1"))
			str += " value = \"" + this.value + "\" ";
			if(this.vertex != "")
				str += "vertex = \"" + this.vertex + "\" ";
			if(this.edge != "")
				str += "edge = \"" + this.edge + "\" ";
			if(this.source != ""){
				if(!mxCellStructure.this.className.equalsIgnoreCase(""))
					str += "source = \"" + mxCellStructure.this.className + ".class::" + mxCellStructure.this.instanceName + "_" + this.source + "\" ";
				else 
					str += "source = \"" + this.source + "\" ";
			}
			if(this.target != "")
			{
				if(!mxCellStructure.this.className.equalsIgnoreCase(""))
					str += "target = \"" + mxCellStructure.this.className + ".class::" + mxCellStructure.this.instanceName + "_" + this.target + "\" ";
				else str += "target = \"" + this.target + "\" ";
			}
			if(this.edge.equalsIgnoreCase("1"))
				str += " value = \"" + this.value + "\" ";
			return str;
		}
	}
	public class Style{
		String shape;
		String gesture;
		public Style(){
			this.shape = "";
			this.gesture = "";
		}
		
		public Style(String shape, String gesture){
			this.shape = shape;
			this.gesture = gesture;
		}
		
		public String toString(){
			String str = "";
			str = "style = \"" + this.shape;
			if (this.gesture != "")
				str += ";" + this.gesture; 
			str +=  "\"";
			return str; // this will cover both 1) style="ellipse;strokeWidth=5" and 2) style="ellipse"
		}
	}
	
	public String toString(){
		String str = "";
		if(this.mxGeometry != null && this.mxGeometry.as != "")
		{
			str += "\t\t<mxCell " + this.properties + ">\n";
			str += this.mxGeometry;
			str += "\t\t</mxCell>\n";
		}
		else{
			str += "\t\t<mxCell" + this.properties + "/>\n";
		}
		return str;
	}
	
	public static void main(String[] args){
		mxCellStructure mxCell = new mxCellStructure();
		mxCell.properties.id = "1";
		mxCell.properties.parentId = "0";
		System.out.println(mxCell);
	}
}
