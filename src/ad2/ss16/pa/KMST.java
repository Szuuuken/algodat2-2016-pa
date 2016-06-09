package ad2.ss16.pa;

import sun.reflect.generics.tree.Tree;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private int numNodes;
	private Problem mainProblem;
	private TreeMap<Integer,TreeSet<Integer>> al = new TreeMap<Integer, TreeSet<Integer>>();


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
		for(Edge edge: mainProblem.edges){
			TreeSet<Edge> newSelectedEdges = new TreeSet<Edge>();
			newSelectedEdges.add(edge);
			TreeSet<Integer> newSelectedNodes = new TreeSet<Integer>();
			newSelectedNodes.add(edge.node1);
			newSelectedNodes.add(edge.node2);

			branch(newSelectedEdges,newSelectedNodes,edge.weight);


			/*Problem subProblem = new Problem();
			subProblem.addNode(edge.node1,edge);
			subProblem.addNode(edge.node2,edge);
			bnb(subProblem);*/

			/*for(Edge connectedEdge: getNextEdges(edge)){
				subProblem.addNode(connectedEdge.node1,connectedEdge);
				subProblem.addNode(connectedEdge.node2,connectedEdge);

			}*/
		}
	}


	private void branch(TreeSet<Edge> selectedEdges,TreeSet<Integer> selectedNodes,int currentWeight){
		if(currentWeight < this.getSolution().getUpperBound()) {
			MST mst = prim2(selectedEdges);

			if (mst.size() == this.k) {
				this.setSolution(mst.weight,mst.edges);
			} else if(mst.size() < this.k){
				TreeSet<Edge> newSelectedEdges = new TreeSet<Edge>(selectedEdges);
				TreeSet<Integer> newSelectedNodes = new TreeSet<Integer>(selectedNodes);

				for (Edge remainingEdge : mainProblem.edges) {
					if (selectedNodes.contains(remainingEdge.node1) ^ selectedNodes.contains(remainingEdge.node2)) {
						newSelectedEdges.add(remainingEdge);
						newSelectedNodes.add(remainingEdge.node1);
						newSelectedNodes.add(remainingEdge.node2);

						branch(newSelectedEdges, newSelectedNodes, currentWeight + remainingEdge.weight);
					}
				}
			}
		}
	}

	private void bnb(Problem problem){
		int localLowerBound = calcLowerBound(problem);

		if(localLowerBound < this.getSolution().getUpperBound()) {
			TreeSet<Edge> edges = new TreeSet<Edge>(problem.edges);

			edges.addAll(getNextEdges(problem));

			MST mst = prim2(edges);

			int localUpperBound = mst.weight;
			if(localUpperBound == 0)
				return;

			if (localUpperBound < this.getSolution().getUpperBound() && mst.size() == this.k)
				this.setSolution(localUpperBound, mst.edges);

			if (localLowerBound < this.getSolution().getUpperBound() && problem.nodes.size() <= this.k) {
				for(Edge edge : getNextEdges(problem)){
					Problem subProblem = new Problem(problem);
					subProblem.addNode(edge.node1,edge);
					subProblem.addNode(edge.node2,edge);
					bnb(subProblem);

					Problem prob = new Problem(problem);
					prob.unselected.add(edge);
					bnb(prob);
				}
			}
		}
	}

	private TreeSet<Edge> getNextEdges(Edge first){
		TreeSet<Integer> connectedNodes = new TreeSet<Integer>();
		TreeSet<Edge> connectedEdges = new TreeSet<Edge>();
		connectedNodes.add(first.node1);
		connectedNodes.add(first.node2);

		for(Edge edge : mainProblem.edges){
			if(connectedNodes.contains(edge.node1) ^ connectedNodes.contains(edge.node2)){
				connectedEdges.add(edge);
				//connectedNodes.add(edge.node1);
				//connectedNodes.add(edge.node2);

			}
		}

		return connectedEdges;
	}

	private TreeSet<Edge> getNextEdges(Problem problem){
		TreeSet<Edge> remainingNodes = new TreeSet<Edge>(mainProblem.edges);
		TreeSet<Integer> connectedNodes = new TreeSet<Integer>(problem.nodes.keySet());
		TreeSet<Edge> connectedEdges = new TreeSet<Edge>();
		remainingNodes.removeAll(problem.edges);
		remainingNodes.removeAll(problem.unselected);

		for(Edge edge: remainingNodes){
			if(connectedNodes.contains(edge.node1) ^ connectedNodes.contains(edge.node2) && !problem.unselected.contains(edge)){
				connectedEdges.add(edge);
				connectedNodes.add(edge.node1);
				connectedNodes.add(edge.node2);
				if(connectedNodes.size() == this.k)
					break;
			}
		}

		return connectedEdges;
	}

	private int calcLowerBound(Problem problem){
		//if(problem.nodes.size() < this.k || problem.edges.size()+1 < this.k) return Integer.MAX_VALUE;

		//if(problem.edges.size() < this.k-1) return Integer.MAX_VALUE;

		int weight = 0;
		int count = 0;
		Iterator<Edge> it = mainProblem.edges.iterator();

		while(it.hasNext() && count < this.k-1) {
			Edge edge = it.next();
			if(!problem.unselected.contains(edge)){
				count++;
				weight+=edge.weight;
			}

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

	private MST prim2(TreeSet<Edge> edges){
		TreeSet<Edge> mstSetup = new TreeSet<Edge>();
		TreeSet<Integer> mstNodes = new TreeSet<Integer>();
		mstNodes.add(edges.first().node1);

		return prim2(edges,mstSetup,mstNodes,0);

	}

	private MST prim2(TreeSet<Edge> edges, TreeSet<Edge> mst, TreeSet<Integer> mstNodes,int weight){

		Edge minEdge = null;
		int minWeight = Integer.MAX_VALUE;

		for(Edge edge : edges){
			if((mstNodes.contains(edge.node1) ^ mstNodes.contains(edge.node2)) && edge.weight < minWeight){
				minEdge = edge;
				minWeight = edge.weight;
			}
		}

		if(minEdge != null) {
			weight+= minEdge.weight;
			mst.add(minEdge);

			TreeSet<Edge> newEdges = new TreeSet<Edge>(edges);
			newEdges.remove(minEdge);

			TreeSet<Integer> newMstNodes = new TreeSet<Integer>(mstNodes);
			newMstNodes.add(minEdge.node1);
			newMstNodes.add(minEdge.node2);

			return prim2(newEdges,mst, newMstNodes,weight);
		}

		return new MST(mst,weight);
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
				mst.add(minEdge);//nehmen wir die Kante in unsere loesung auf

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
				this.nodes.put(node,new TreeSet<Edge>());
			}

			this.nodes.get(node).add(edge);
			this.edges.add(edge);
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
	}
}