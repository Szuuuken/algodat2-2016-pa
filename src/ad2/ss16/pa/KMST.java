package ad2.ss16.pa;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private int numNodes;
	private Problem mainProblem;


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
		this.setSolution(Integer.MAX_VALUE,edges);
		this.mainProblem = new Problem();

		for(Edge edge: edges){
			this.mainProblem.addNode(edge.node1,edge);
			this.mainProblem.addNode(edge.node2,edge);
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
		Edge firstEdge = mainProblem.edges.first();
		mainProblem.fixed.add(firstEdge);
		bnb(mainProblem,firstEdge);

		Problem subProblem = new Problem(mainProblem);
		subProblem.remove(firstEdge);
		subProblem.unselected.add(firstEdge);
		bnb(subProblem,firstEdge);
	}
/*
	private Edge getNextEdge(Edge lastEdge, TreeMap<Integer,TreeSet<Edge>> nodes, TreeSet<Edge> selected, TreeSet<Edge> unselected){
		TreeSet<Edge> relevantEdges = new TreeSet<Edge>();

		for(Edge edge: nodes.get(lastEdge.node1)){
			if(!unselected.contains(edge) && !selected.contains(edge) && lastEdge != edge)
				relevantEdges.add(edge);
		}

		if(relevantEdges.isEmpty())
			return null;

		return relevantEdges.first();
	}

	private void bnb(TreeSet<Edge> edges, TreeMap<Integer,TreeSet<Edge>> nodes, TreeSet<Edge> selected, TreeSet<Edge> unselected, Edge lastEdge){
		int localLowerBound = calcLowerBound(edges);

		if(localLowerBound < this.getSolution().getUpperBound()){
			Problem mstProb = new Problem();
			mstProb.edges = edges;
			mstProb.nodes = nodes;
			MST mst = prim(mstProb);

			int localUpperBound = mst.weight;

			if(localUpperBound < this.getSolution().getUpperBound()){
				this.setSolution(localUpperBound,edges);
			}

			if(localLowerBound < this.getSolution().getUpperBound()) {
				Edge nextEdge = getNextEdge(lastEdge, nodes, selected, unselected);

				if(nextEdge != null){
					TreeSet<Edge>

				}
			}
		}
	}
	*/

	private void bnb(Problem problem, Edge lastEdge){
		int localLowerBound = calcLowerBound(problem);

		if(localLowerBound < this.getSolution().getUpperBound()) {
			MST mst = prim(problem);

			int localUpperBound = mst.weight;

			if (localUpperBound < this.getSolution().getUpperBound())
				this.setSolution(localUpperBound, problem.edges);

			if (localLowerBound < this.getSolution().getUpperBound()) {
				Edge nextEdge = getNextEdge(problem, lastEdge);
				if(nextEdge != null) {
					Problem problem2 = new Problem(problem);
					problem2.fixed.add(nextEdge);
					bnb(problem2,nextEdge);

					Problem problem1 = new Problem(problem);
					problem1.unselected.add(nextEdge);
					problem1.remove(nextEdge);
					bnb(problem1,nextEdge);
				}
			}
		}
	}

	private Edge getNextEdge(Problem problem, Edge lastEdge){
		TreeSet<Edge> possilbeEdges = new TreeSet<Edge>();

		if(problem.nodes.containsKey(lastEdge.node1)) {
			for (Edge edge : problem.nodes.get(lastEdge.node1)) {
				if(!problem.fixed.contains(edge) && !problem.unselected.contains(edge) && edge != lastEdge)
					possilbeEdges.add(edge);
			}
		}

		if(problem.nodes.containsKey(lastEdge.node2)) {
			for (Edge edge : problem.nodes.get(lastEdge.node2)) {
				if(!problem.fixed.contains(edge) && edge != lastEdge  && !problem.unselected.contains(edge))
					possilbeEdges.add(edge);
			}
		}

		if(possilbeEdges.isEmpty())
			return null;

		return possilbeEdges.last();
	}

	private int calcLowerBound(Problem problem){
		//if(problem.nodes.size() < this.k || problem.edges.size()+1 < this.k) return Integer.MAX_VALUE;

		if(problem.edges.size() < this.k-1) return Integer.MAX_VALUE;

		int weight = 0;
		Iterator<Edge> it = problem.edges.iterator();

		for(int i = 0; i < this.k-1; i++) {
			weight += it.next().weight;
		}

		return weight;
	}

	private int calcLowerBound(TreeSet<Edge> edges){
		if(edges.size() < this.k-1) return Integer.MAX_VALUE;

		int weight = 0;
		Iterator<Edge> it = edges.iterator();

		for(int i = 0; i < this.k-1; i++) {
			weight += it.next().weight;
		}

		return weight;
	}

	private MST prim(Problem graph){
		TreeSet<Edge> mst = new TreeSet<Edge>();
		TreeMap<Integer,TreeSet<Edge>> visitedNodes = new TreeMap<Integer, TreeSet<Edge>>();
		TreeMap<Integer,TreeSet<Edge>> remainingNodes = new TreeMap<Integer, TreeSet<Edge>>(graph.nodes);

		Map.Entry<Integer,TreeSet<Edge>> firstEntry = remainingNodes.firstEntry();
		visitedNodes.put(firstEntry.getKey(),remainingNodes.remove(firstEntry.getKey()));

		int weight = 0;

		while(!remainingNodes.isEmpty()){ //solange noch unbesuchte Knoten vorhanden sind
			Edge minEdge = null;
			//boolean newNodeFound = false;
			int newNode = -1;
			//int oldNode = -1;

			for(Map.Entry<Integer,TreeSet<Edge>> node: visitedNodes.entrySet()){ // suchen den Kante mit dem kleinsten Gewicht, der bereits besuchten Knoten
				int key = node.getKey();
				TreeSet<Edge> edgeTreeSet = node.getValue();
				if(edgeTreeSet != null) {
					for (Edge edge : edgeTreeSet) {
						int nextNode = getNextNode(key, edge);

						if (!visitedNodes.containsKey(nextNode) && (graph.nodes.containsKey(nextNode)) && (minEdge == null || edge.weight < minEdge.weight)) { // falls wir einen neuen gefunden haben und es sich um die bis jetzt kleinst kante handelt nehmen wir die Kante
							minEdge = edge;
							newNode = nextNode;
							//oldNode = key;
							//newNodeFound = true;
						}
					}
				}
			}

			if(minEdge != null){ // falls wir eine kleinste Kante zu einem unbekannten Knoten gefunden haben,
				mst.add(minEdge);//nehmen wir die Kante in unsere lösung auf

/*				if(remainingNodes.containsKey(oldNode) && remainingNodes.get(oldNode).contains(minEdge))
					remainingNodes.get(oldNode).remove(minEdge);

				if(remainingNodes.containsKey(newNode) && remainingNodes.get(newNode).contains(minEdge))
					remainingNodes.get(newNode).remove(minEdge);*/

				visitedNodes.put(newNode,remainingNodes.get(newNode)); // markieren wir den Neuen Knoten als Besucht
				remainingNodes.remove(newNode); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
				weight+= minEdge.weight;
			}

			else
				remainingNodes.clear(); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
		}

		return new MST(mst,weight);
	}

	private class MST {
		private TreeSet<Edge> edges;
		private int weight;

		private MST(TreeSet<Edge> edges, int weight) {
			this.weight = weight;
			this.edges = edges;
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

	private class Problem{
		boolean alreadyDone = false;
		int lower = 0;
		int upper = 0;

		TreeSet<Edge> unselected = new TreeSet<Edge>();
		TreeSet<Edge> fixed = new TreeSet<Edge>();
		TreeMap<Integer, TreeSet<Edge>> nodes;
		TreeSet<Edge> edges;

		public Problem(Problem problem){
			this.unselected = new TreeSet<Edge>(problem.unselected);
			this.fixed = new TreeSet<Edge>(problem.fixed);
			this.nodes = new TreeMap<Integer, TreeSet<Edge>>(problem.nodes);
			this.edges = new TreeSet<Edge>(problem.edges);
		}

		public Problem(){
			this.nodes = new TreeMap<Integer, TreeSet<Edge>>();
			this.edges = new TreeSet<Edge>();
		}

		public void addNode(int node, Edge edge){
			if(!this.nodes.containsKey(node)) {
				TreeSet<Edge> nodeEdges = new TreeSet<Edge>();
				nodeEdges.add(edge);
				this.nodes.put(node, nodeEdges);
			}else{
				this.nodes.get(node).add(edge);
			}

			this.edges.add(edge);
		}

		public void addEdge(Edge edge){
			addNode(edge.node1,edge);
			addNode(edge.node2,edge);
		}

		public int getFreeNode(){
			for(int maybeNode : this.nodes.keySet()){
				if(!fixed.contains(maybeNode))
					return maybeNode;
			}

			return -1;
		}

		public void remove(Edge edge){
			edges.remove(edge);

			nodes.get(edge.node1).remove(edge);

			if(nodes.get(edge.node1).isEmpty())
				nodes.remove(edge.node1);

			nodes.get(edge.node2).remove(edge);

			if(nodes.get(edge.node2).isEmpty())
				nodes.remove(edge.node2);
		}

		public void removeNode(int node) {

			TreeSet<Edge> nodeEdges = nodes.get(node);
			nodes.remove(node);
			edges.removeAll(nodeEdges);

			/*for(int nodeKey: nodes.keySet()){
				TreeSet<Edge> nEdges = nodes.get(nodeKey);
				TreeSet<Edge> tmpEdges = new TreeSet<Edge>(nEdges);
				Iterator<Edge> edgeIterator = tmpEdges.iterator();

				while(edgeIterator.hasNext()){
					Edge edge = edgeIterator.next();

					if(edge.node1 == node || edge.node2 == node)
						nEdges.remove(edge);
				}
			}*/
		}

		public Edge getFreeEdge(){
			for(Edge edge: this.edges){
				if(!this.fixed.contains(edge))
					return edge;
			}

			return null;
		}
	}
}