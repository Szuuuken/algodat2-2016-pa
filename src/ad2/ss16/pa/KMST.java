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
	private Problem mainProblem;
	//private TreeMap<Integer,TreeSet<Edge>> nodes;
	//private HashSet<Edge> edges;


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
		//this.edges = edges;
		//this.nodes = new TreeMap<Integer, TreeSet<Edge>>();
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
		LinkedList<Problem> problems = new LinkedList<Problem>();
		problems.add(mainProblem);
		//HashSet<String> testedSoultions = new HashSet<String>();

		//Problem firstProblem = new Problem();
		//mainProblem.ad


		/*Iterator<Edge> edgeIterator = this.mainProblem.edges.iterator();
		while(firstProblem.nodes.size() < this.k){
			Edge edge = edgeIterator.next();

			if(firstProblem.nodes.containsKey(edge.node1) ^ firstProblem.nodes.containsKey(edge.node2) || (!firstProblem.nodes.containsKey(edge.node1) && !firstProblem.nodes.containsKey(edge.node2)))
				firstProblem.addEdge(edge);
		}*/

		//problems.add(firstProblem);

		while(!problems.isEmpty()){
			Problem problem = problems.getFirst();
			problems.remove(problem);

			if(problem.alreadyDone){
				if(problem.lower < this.getSolution().getUpperBound()){
					int node = problem.getFreeNode();
					if(node >= 0) {
						Problem subProblem = new Problem(problem);
						subProblem.removeNode(node);
						problem.fixed.add(node);

						problems.add(subProblem);
						problems.add(problem);
					}

				}

			}
			else{
				int localLowerBound = calcLowerBound(problem);

				if(localLowerBound < this.getSolution().getUpperBound()) {
					MST mst = prim(problem);

					int localUpperBound = mst.getWeight();
					if (localUpperBound == 0) {
						continue;
					}

					if (localUpperBound < this.getSolution().getUpperBound() && mst.size() >= this.k /*&& problem.nodes.size() >= this.k*/) {
						this.setSolution(localUpperBound, mst.edges);
					}

					if (localLowerBound < this.getSolution().getUpperBound() && problem.nodes.size() > this.k) {
						Problem subproblem1 = new Problem(problem);

						int node = problem.getFreeNode();
						if (node != -1) {
							subproblem1.removeNode(node);
							problem.fixed.add(node);
							problem.alreadyDone = true;
							problem.upper = localUpperBound;
							problem.lower = localLowerBound;

							problems.add(subproblem1);
							problems.add(problem);


						}

		/*					for(Edge edge: problem.edges){
							if(problem.edges.last().node1 != edge.node1 && problem.edges.last().node1 != edge.node2)
								subproblem1.addEdge(edge);
							if(problem.edges.last().node1 != edge.node2 && problem.edges.last().node2 != edge.node2)
								subproblem2.addEdge(edge);
						}
		*/

		/*
						Iterator<Integer> it = problem.nodes.keySet().iterator();

						while(it.hasNext()){
							Problem subProblem = new Problem(problem);
							subProblem.removeNode(it.next());

							if(!testedSoultions.contains(subProblem.nodes.keySet().toString()) && calcLowerBound(subProblem) < this.getSolution().getUpperBound()) {
								problems.add(subProblem);
								testedSoultions.add(subProblem.nodes.keySet().toString());
							}
						}*/
					}
				}
			}
		}
	}

	public int calcLowerBound(Problem problem){
		//if(problem.nodes.size() < this.k || problem.edges.size()+1 < this.k) return Integer.MAX_VALUE;

		if(problem.edges.size() < this.k-1) return Integer.MAX_VALUE;

		int weight = 0;
		Iterator<Edge> it = problem.edges.iterator();

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
			boolean newNodeFound = false;
			int newNode = -1;
			int oldNode = -1;

			for(Map.Entry<Integer,TreeSet<Edge>> node: visitedNodes.entrySet()){ // suchen den Kante mit dem kleinsten Gewicht, der bereits besuchten Knoten
				int key = node.getKey();
				TreeSet<Edge> edgeTreeSet = node.getValue();
				if(edgeTreeSet != null) {
					for (Edge edge : edgeTreeSet) {
						int nextNode = getNextNode(key, edge);

						if (!visitedNodes.containsKey(nextNode) && (graph.nodes.containsKey(nextNode)) && (minEdge == null || edge.weight < minEdge.weight)) { // falls wir einen neuen gefunden haben und es sich um die bis jetzt kleinst kante handelt nehmen wir die Kante
							minEdge = edge;
							newNode = nextNode;
							oldNode = key;
							newNodeFound = true;
						}
					}
				}
			}

			if(minEdge != null && newNodeFound){ // falls wir eine kleinste Kante zu einem unbekannten Knoten gefunden haben,

				mst.add(minEdge);//nehmen wir die Kante in unsere lösung auf

				if(remainingNodes.containsKey(oldNode) && remainingNodes.get(oldNode).contains(minEdge))
					remainingNodes.get(oldNode).remove(minEdge);
/*
				if(remainingNodes.containsKey(newNode) && remainingNodes.get(newNode).contains(minEdge))
					remainingNodes.get(newNode).remove(minEdge);*/

				visitedNodes.put(newNode,remainingNodes.get(newNode)); // markieren wir den Neuen Knoten als Besucht
				remainingNodes.remove(newNode); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
				weight+= minEdge.weight;
			}

			else if(!newNodeFound)
				remainingNodes.clear(); // entfernen wir den neuen Knoten aus den noch zu suchenden Knoten
		}

		/*if(mst.size()+1 < graph.nodes.size() && graph.nodes.size() >= this.k){
			Problem  prob = new Problem(graph);

			for(int nodeValue : visitedNodes.keySet()){
				prob.removeNode(nodeValue);
			}

			if(prob.nodes.size() > 0) {
				return prim(prob);
			}
		}*/

		return new MST(mst,weight);
	}

	private class MST {
		private TreeSet<Edge> edges;
		private int weight;

		private MST(TreeSet<Edge> edges, int weight) {
			this.weight = weight;
			this.edges = edges;
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

	private class Problem{
		boolean alreadyDone = false;
		int lower = 0;
		int upper = 0;

		TreeSet<Integer> fixed = new TreeSet<Integer>();
		TreeMap<Integer, TreeSet<Edge>> nodes;
		TreeSet<Edge> edges;

		public Problem(TreeSet<Edge> edges){
			this.nodes = new TreeMap<Integer, TreeSet<Edge>>();
			this.edges = new TreeSet<Edge>();

			Iterator<Edge> edgeIterator = this.edges.iterator();
			while(edgeIterator.hasNext()){
				Edge edge = edgeIterator.next();

				addNode(edge.node1,edge);
				addNode(edge.node2,edge);
			}
		}

		public Problem(Problem problem){
			this.fixed = new TreeSet<Integer>(problem.fixed);
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
	}
}