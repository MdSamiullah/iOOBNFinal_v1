package triangulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/******************************************************************************
 *  Compilation:  javac Graph.java
 *  Execution:    java Graph < input.txt
 *  Dependencies: ST.java SET.java In.java StdOut.java
 *  Data files:   http://introcs.cs.princeton.edu/45graph/tinyGraph.txt
 *  
 *  Undirected graph data type implemented using a symbol table
 *  whose keys are vertices (String) and whose values are sets
 *  of neighbors (SET of Strings).
 *
 *  Remarks
 *  -------
 *   - Parallel edges are not allowed
 *   - Self-loop are allowed
 *   - Adjacency lists store many different copies of the same
 *     String. You can use less memory by interning the strings.
 *
 *  % java Graph < tinyGraph.txt
 *  A: B C G H 
 *  B: A C H 
 *  C: A B G 
 *  G: A C 
 *  H: A B 
 *
 *  A: B C G H 
 *  B: A C H 
 *  C: A B G 
 *  G: A C 
 *  H: A B 
 *
 ******************************************************************************/

/**
 *  The {@code Graph} class represents an undirected graph of vertices
 *  with string names.
 *  It supports the following operations: add an edge, add a vertex,
 *  get all of the vertices, iterate over all of the neighbors adjacent
 *  to a vertex, is there a vertex, is there an edge between two vertices.
 *  Self-loops are permitted; parallel edges are discarded.
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/45graph">Section 4.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Graph {

    // symbol table: key = string vertex, value = set of neighboring vertices
    public ST<String, SET<String>> st;
    public HashMap<String, Integer> po; 

    // number of edges
    private int E;

   /**
     * Initializes an empty graph with no vertices or edges.
     */
    public Graph() {
        st = new ST<String, SET<String>>();
        po = new HashMap<String, Integer>();
    }

   /**
     * Initializes a graph from the specified file, using the specified delimiter.
     *
     * @param filename the name of the file
     * @param delimiter the delimiter
 * @throws IOException 
     */
    public Graph(String filename, String delimiter) throws IOException {
        st = new ST<String, SET<String>>();
        po = new HashMap<String, Integer>();
        
        File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line = "";
        while ((line = bufferedReader.readLine()) != null) {
        		//System.out.println(line);
            String[] names = line.split(delimiter);
            addPerfectOrder(names[0], Integer.parseInt(names[1]));
            for (int i = 2; i < names.length; i++) {
                addEdge(names[0], names[i]);
                //System.out.println("Edge " + names[0] + " weight " + names[1] + "--" + names[i]);
            }
        }
    }
    
    public Graph(Graph G, HashMap<String, Integer> PO) throws IOException {
        st = G.st;
        po = PO;
    }

   /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int V() {
        return st.size();
    }

   /**
     * Returns the number of edges in this graph.
     *
     * @return the number of edges in this graph
     */
    public int E() {
        return E;
    }

    // throw an exception if v is not a vertex
    private void validateVertex(String v) {
        if (!hasVertex(v)) throw new IllegalArgumentException(v + " is not a vertex");
    }

   /**
     * Returns the degree of vertex v in this graph.
     *
     * @param  v the vertex
     * @return the degree of {@code v} in this graph
     * @throws IllegalArgumentException if {@code v} is not a vertex in this graph
     */
    public int degree(String v) {
        validateVertex(v);
        return st.get(v).size();
    }

   /**
     * Adds the edge v-w to this graph (if it is not already an edge).
     *
     * @param  v one vertex in the edge
     * @param  w the other vertex in the edge
     */
    public void addEdge(String v, String w) {
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        if (!hasEdge(v, w)) E++;
        st.get(v).add(w);
        st.get(w).add(v);
    }
    
    public void addPerfectOrder(String v, int num) {
    		po.put(v, num);
    }

   /**
     * Adds vertex v to this graph (if it is not already a vertex).
     *
     * @param  v the vertex
     */
    public void addVertex(String v) {
        if (!hasVertex(v)) st.put(v, new SET<String>());
    }


   /**
     * Returns the vertices in this graph.
     *
     * @return the set of vertices in this graph
     */
    public Iterable<String> vertices() {
        return st.keys();
    }

   /**
     * Returns the set of vertices adjacent to v in this graph.
     *
     * @param  v the vertex
     * @return the set of vertices adjacent to vertex {@code v} in this graph
     * @throws IllegalArgumentException if {@code v} is not a vertex in this graph
     */
    public Iterable<String> adjacentTo(String v) {
        validateVertex(v);
        return st.get(v);
    }

   /**
     * Returns true if v is a vertex in this graph.
     *
     * @param  v the vertex
     * @return {@code true} if {@code v} is a vertex in this graph,
     *         {@code false} otherwise
     */
    public boolean hasVertex(String v) {
        return st.contains(v);
    }

   /**
     * Returns true if v-w is an edge in this graph.
     *
     * @param  v one vertex in the edge
     * @param  w the other vertex in the edge
     * @return {@code true} if {@code v-w} is a vertex in this graph,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *         is not a vertex in this graph
     */
    public boolean hasEdge(String v, String w) {
        validateVertex(v);
        validateVertex(w);
        return st.get(v).contains(w);
    }

   /**
     * Returns a string representation of this graph.
     *
     * @return string representation of this graph
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String v : st) {
            s.append(v + "(" + po.get(v)+ ") : ");
            for (String w : st.get(v)) {
                s.append(w + "("+ po.get(w) + ") ");
            }
            s.append('\n');
        }
        return s.toString();
    }

   /**
     * Unit tests the {@code Graph} data type.
     */
    public static void main(String[] args) {

        // create graph
        Graph graph = new Graph();
        Scanner S = new Scanner(System.in);
        while (!S.hasNext()) {
            String v = S.next();
            String w = S.next();
            graph.addEdge(v, w);
        }

        // print out graph
        System.out.println(graph);

        // print out graph again by iterating over vertices and edges
        for (String v : graph.vertices()) {
        	System.out.print(v + ": ");
            for (String w : graph.adjacentTo(v)) {
            	System.out.print(w + " ");
            }
            System.out.println();
        }

    }

}
