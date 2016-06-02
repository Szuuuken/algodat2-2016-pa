package ad2.ss16.pa;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private int numNodes;
	private TreeMap<Integer,HashSet<Edge>> nodes;
	private HashSet<Edge> edges;


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
		this.numNodes = numNodes;
		this.edges = edges;
		this.nodes = new TreeMap<Integer, HashSet<Edge>>();
		this.setSolution(Integer.MAX_VALUE,edges);

		for(Edge edge: edges){
			if(nodes.containsKey(edge.node1))
				nodes.get(edge.node1).add(edge);
			else
				nodes.put(edge.node1,new HashSet<Edge>());

			if(nodes.containsKey(edge.node2))
				nodes.get(edge.node2).add(edge);
			else
				nodes.put(edge.node2,new HashSet<Edge>());
		}
	}
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
		Main.printDebug("run " + this.k);

		LinkedList<TreeMap<Integer, HashSet<Edge>>> problems = new LinkedList<TreeMap<Integer, HashSet<Edge>>>();
		problems.add(nodes);

		while(!problems.isEmpty()){
			TreeMap<Integer, HashSet<Edge>> problem = problems.getFirst();
			problems.remove(problem);


			MST mst = prim(problem);

			if(problem.size() == this.k) {
				//Main.printDebug("cont1");
				continue;
			}

			if(mst.size() <= this.k || mst.getWeight() < this.getSolution().getUpperBound()) {
				//Main.printDebug("cont2");
				continue;
			}

			for(int key: problem.keySet()){
				TreeMap<Integer, HashSet<Edge>> subProblem = new TreeMap<Integer, HashSet<Edge>>(problem);
				subProblem.remove(key);
				problems.add(subProblem);
			}
		}

	}

	private MST prim(TreeMap<Integer, HashSet<Edge>> graph){
		HashSet<Edge> mst = new HashSet<Edge>();
		TreeMap<Integer,HashSet<Edge>> visitedNodes = new TreeMap<Integer, HashSet<Edge>>();
		TreeMap<Integer,HashSet<Edge>> remainingNodes = new TreeMap<Integer, HashSet<Edge>>(graph);

		Map.Entry<Integer,HashSet<Edge>> firstEntry = remainingNodes.firstEntry();
		visitedNodes.put(firstEntry.getKey(),firstEntry.getValue());
		remainingNodes.remove(firstEntry.getKey());
		int weight = 0;

		while(!remainingNodes.isEmpty()){ //solange noch unbesuchte Knoten vorhanden sind
			Edge minEdge = null;
			boolean newNodeFound = false;
			int newNode = -1;

			for(Map.Entry<Integer,HashSet<Edge>> node: visitedNodes.entrySet()){ // suchen den Kante mit dem kleinsten Gewicht, der bereits besuchten Knoten
				if(node.getValue() != null) {
					for (Edge edge : node.getValue()) {
						int nextNode = getNextNode(node.getKey(), edge);

						if (!visitedNodes.containsKey(nextNode) && (graph.containsKey(nextNode)) && (minEdge == null || edge.weight < minEdge.weight)) { // falls wir einen neuen gefunden haben und es sich um die bis jetzt kleinst kante handelt nehmen wir die Kante
							minEdge = edge;
							newNode = nextNode;
							newNodeFound = true;
						}
					}
				}
			}

			if(minEdge != null && newNode >= 0){ // falls wir eine kleinste Kante zu einem unbekannten Knoten gefunden haben,

				mst.add(minEdge);//nehmen wir die Kante in unsere lösung auf
				visitedNodes.put(newNode,remainingNodes.get(newNode)); // markieren wir den Neuen Knoten als Besucht
				remainingNodes.remove(newNode); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
				weight+= minEdge.weight;
			}

			if(newNodeFound == false)
				remainingNodes.clear(); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
		}

		if(this.k <= mst.size()+1  && weight < this.getSolution().getUpperBound()) {
			this.setSolution(weight, mst);
			//Main.printDebug("newSolution" + mst);
		}


		return new MST(mst,weight);
	}

	private class MST {
		private HashSet<Edge> edges;
		private int weight;

		private MST(HashSet<Edge> edges, int weight) {
			this.edges = edges;
			this.weight = weight;
		}


		public HashSet<Edge> getEdges() {
			return edges;
		}

		public int getWeight() {
			return weight;
		}

		public int size(){
			return edges.size()+1;
		}
	}

	private int getNextNode(int node, Edge edge){
		if(node == edge.node1)
			return edge.node2;

		return edge.node1;
	}
}
