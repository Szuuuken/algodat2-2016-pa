package ad2.ss16.pa;

import org.omg.CORBA.NO_IMPLEMENT;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;
	private int k;
	private ArrayList<HashSet<Edge>> problems = new ArrayList<HashSet<Edge>>();
	private Set<Integer> nodes;
	private HashMap<Integer,ArrayList<Edge>> paths;
	/**
	 * Der Konstruktor. Hier ist die richtige Stelle f&uuml;r die
	 * Initialisierung Ihrer Datenstrukturen.
	 * 
	 * @param numNodes
	 *            Die Anzahl der Knoten
	 * @param numEdges
	 *            Die Anzahl der Kanten
	 * @param edges
	 *            Die Menge der Kanten
	 * @param k
	 *            Die Anzahl der Knoten, die Ihr MST haben soll
	 */
	public KMST(Integer numNodes, Integer numEdges, HashSet<Edge> edges, int k) {
		this.k = k;
		problems.add(edges);
		setSolution(numEdges, edges);

		Graph graph = new Graph();
		int[] asd = new int[99];
		for (Edge edge:edges) {
			asd
		}
	}/*
		nodes = new HashSet<Integer>();
		paths = new HashMap<Integer, ArrayList<Edge>>();


		for (Edge edge:edges) {
			nodes.add(edge.node1);
			nodes.add(edge.node2);

			if(!paths.containsKey(edge.node1)){
				paths.put(edge.node1,new ArrayList<Edge>());

			}

			paths.get(edge.node1).add(edge);

			if(!paths.containsKey(edge.node2)){
				paths.put(edge.node2,new ArrayList<Edge>());

			}

			paths.get(edge.node2).add(edge);

		}

	}*/

	/**
	 * Diese Methode bekommt vom Framework maximal 30 Sekunden Zeit zur
	 * Verf&uuml;gung gestellt um einen g&uuml;ltigen k-MST zu finden.
	 * 
	 * <p>
	 * F&uuml;gen Sie hier Ihre Implementierung des Branch-and-Bound Algorithmus
	 * ein.
	 * </p>
	 */
	@Override
	public void run() {
		Main.printDebug("run");

		while(!problems.isEmpty()){
			HashSet<Edge> subProblem = problems.get(0);
			problems.remove(0);

			int localMin = 2;

			if(localMin < max){
				HashSet<Edge> mst = prim(subProblem);
				int localMax = mst;

				if(localMax < max)
					max = localMax;

				if(localMin < max){
					for()
				}
			}
		}
	}

	private void prim2(Graph graph){
		Collection<Node> nodes = graph.getNodes().values();
		int[] distance = new int[nodes.size()];

		for (Node node: ) {

		}
	}

	private void hui(Graph graph){

	}

	private void hui(Graph graph, Node node, Set<Integer> visited){
		Edge minEdge = null;

		for(Edge edge: node.getEdges()){
			int nextNode = node.getValue() == edge.node1?edge.node1:edge.node2;

			if(!visited.contains(nextNode) && (minEdge == null || edge.weight < minEdge.weight))
				minEdge = edge;
		}

		if(minEdge != null){
			visited.add(node.getValue());
			Node nextNode = minEdge.node1 == node.value ? graph.getNodes().get(minEdge.node2):graph.getNodes().get(minEdge.node1);
			hui(graph, nextNode, visited);
		}

		return;
	}

	private Graph prim(Graph graph){
		Queue<Node> visited = new LinkedList<Node>();
		HashSet<Integer> visitedNodes = new HashSet<Integer>();
		visited.add(graph.getHead());
		visitedNodes.add(graph.getHead().getValue());

		return prim(visited,visitedNodes);
	}

	private Graph prim(Queue<Node> visited,HashSet<Integer> visitedNodes){
		Graph mst = new Graph();
		Edge minEdge = null;

		for(Node node : visited){
			for(Edge edge: node.getEdges()){
				int nextNode = node.getValue() == edge.node1?edge.node1:edge.node2;

				if(!visitedNodes.contains(nextNode)) {
					if (minEdge == null)
						minEdge = edge;
					else if (edge.weight < minEdge.weight)
						minEdge = edge;
				}
			}
		}

		if(minEdge != null) {
			mst.addEdge(minEdge);
			visited.add(minEdge.node1);
			visited.add(minEdge.node2);
		}

		for(Node node: graph.getNodes().values()){


			for(Edge edge: node.getEdges()){
				if(minEdge != null) {
					minEdge = edge;
				}
				else if(edge.weight < minEdge.weight){
					minEdge = edge;
				}
			}

			visited.add(node.getValue());
		}

		return mst;
	}

	/*
	private HashSet<Edge> prim(HashSet<Edge> graph){
		HashSet<Edge> mst = new HashSet<Edge>();
		HashSet<Integer> visited = new HashSet<Integer>();

		for (int node:nodes) {
			ArrayList<Edge> edges = paths.get(node);

			Edge minEdge = null;
			for (Edge edge:edges) {
				int currentNode = -1;
				int nextNode = -1;
				if(edge.node1 == node){
					currentNode = edge.node1;
					nextNode = edge.node2;
				}

				if(minEdge == null) {
					if(!visited.contains(nextNode))
						minEdge = edge;
				}
				else{
					if(edge.weight < minEdge.weight && !visited.contains(nextNode))
						minEdge = edge;
				}
			}

			if(minEdge != null)
				mst.add(minEdge);

			visited.add(node);
		}


		return mst;
	}*/

	public class Node{
		private int value;
		private ArrayList<Edge> edges;

		public Node(int value){
			this.value = value;
			edges = new ArrayList<Edge>();
		}

		public int getValue(){return this.value;}
		public ArrayList<Edge> getEdges(){return this.edges;}
		public void addEdge(Edge edge){this.edges.add(edge);}
	}

	public class Graph{
		private HashMap<Integer,Node> nodes;
		private HashSet<Edge> edges;
		private Node head = null;

		public Graph(){
			this.nodes = new HashMap<Integer, Node>();
			this.edges = new HashSet<Edge>();
		}

		public void addEdge(Edge edge){
			addOrGetNode(edge.node1).addEdge(edge);
			addOrGetNode(edge.node2).addEdge(edge);
			this.edges.add(edge);
		}

		public HashMap<Integer,Node> getNodes(){return this.nodes;}
		public Node getHead(){return this.head;}

		private Node addOrGetNode(int value){
			if(nodes.containsKey(value))
				return nodes.get(value);

			Node node = new Node(value);
			nodes.put(value,node);

			if(head == null)
				head = node;

			return node;
		}
	}
}
