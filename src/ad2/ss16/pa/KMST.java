package ad2.ss16.pa;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private int numNodes;
	private int globalLowerBound = Integer.MIN_VALUE;
	private TreeMap<Integer,TreeSet<Edge>> nodes;
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
		this.nodes = new TreeMap<Integer, TreeSet<Edge>>();
		this.setSolution(Integer.MAX_VALUE,edges);

		for(Edge edge: edges){
			if(nodes.containsKey(edge.node1))
				nodes.get(edge.node1).add(edge);
			else
				nodes.put(edge.node1,new TreeSet<Edge>());

			if(nodes.containsKey(edge.node2))
				nodes.get(edge.node2).add(edge);
			else
				nodes.put(edge.node2,new TreeSet<Edge>());
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

		LinkedList<TreeMap<Integer, TreeSet<Edge>>> problems = new LinkedList<TreeMap<Integer, TreeSet<Edge>>>();
		problems.add(nodes);

		while(!problems.isEmpty()){
			TreeMap<Integer, TreeSet<Edge>> problem = problems.getFirst();
			problems.remove(problem);

			MST mst = prim(problem);

			if(mst.getWeight() > this.globalLowerBound){
				int localLowerBound = calcLowerBound(mst.getEdges());

				if(localLowerBound > this.globalLowerBound){
					this.globalLowerBound =
				}
			}





			if(localLowerBound > this.globalLowerBound)
				globalLowerBound = localLowerBound;


			if(problem.size() == this.k) {
				//Main.printDebug("cont1");
				continue;
			}

			if(mst.size() <= this.k || mst.getWeight() < this.getSolution().getUpperBound()) {
				//Main.printDebug("cont2");
				continue;
			}

			for(int key: problem.keySet()){
				TreeMap<Integer, TreeSet<Edge>> subProblem = new TreeMap<Integer, TreeSet<Edge>>(problem);
				subProblem.remove(key);
				problems.add(subProblem);
			}
		}

	}

	public int calcLowerBound(TreeSet<Edge> edges){
		int weight = 0;
		Iterator<Edge> it = edges.iterator();

		for(int i = 0; i < 4; i++){
			weight+= it.next().weight;
		}

		return weight;

	}

	private MST prim(TreeMap<Integer, TreeSet<Edge>> graph){
		TreeSet<Edge> mst = new TreeSet<Edge>();
		TreeMap<Integer,TreeSet<Edge>> visitedNodes = new TreeMap<Integer, TreeSet<Edge>>();
		TreeMap<Integer,TreeSet<Edge>> remainingNodes = new TreeMap<Integer, TreeSet<Edge>>(graph);

		Map.Entry<Integer,TreeSet<Edge>> firstEntry = remainingNodes.firstEntry();
		visitedNodes.put(firstEntry.getKey(),firstEntry.getValue());
		remainingNodes.remove(firstEntry.getKey());
		int weight = 0;

		while(!remainingNodes.isEmpty()){ //solange noch unbesuchte Knoten vorhanden sind
			Edge minEdge = null;
			boolean newNodeFound = false;
			int newNode = -1;

			for(Map.Entry<Integer,TreeSet<Edge>> node: visitedNodes.entrySet()){ // suchen den Kante mit dem kleinsten Gewicht, der bereits besuchten Knoten
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
		private TreeSet<Edge> edges;
		private int weight;

		private MST(TreeSet<Edge> edges, int weight) {
			this.edges = edges;
			this.weight = weight;
		}


		public TreeSet<Edge> getEdges() {
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
