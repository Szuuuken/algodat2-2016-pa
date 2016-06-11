package ad2.ss16.pa;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private TreeSet<Edge> edgesSorted;
	private Edge[] edgeArray;
	private TreeSet<Edge> selectedEdges = new TreeSet<Edge>();
	private HashSet<Integer> selectedNodes = new HashSet<Integer>();


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
		this.edgesSorted = new TreeSet<Edge>(edges);
		this.edgeArray = this.edgesSorted.toArray(new Edge[numEdges]);

		this.setSolution(Integer.MAX_VALUE,edges);
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
		MST mst = prim2(this.edgesSorted);
		this.setSolution(mst.weight,mst.edges);

		for(Edge edge: this.edgeArray){
			this.selectedEdges.add(edge);
			selectedNodes.add(edge.node1);
			selectedNodes.add(edge.node2);
			//TreeSet<Edge> unselectedEdges = new TreeSet<Edge>();

			//branch(edge.weight,unselectedEdges, edge.node1 + "-" + edge.node2 +" ");
			branch(edge.weight);
			this.selectedEdges.remove(edge);
		}
	}

	//private void branch(int currentWeight, TreeSet<Edge> unselectedEdges, String code){
	private void branch(int currentWeight){
		int localLowerBound = calcLowerBound(currentWeight);

		//System.out.println(code + " \t " + localLowerBound + " \t " + currentWeight);

		if(localLowerBound >= this.getSolution().getUpperBound()){
			return;
		}

		if(localLowerBound < this.getSolution().getUpperBound()) {

			if (currentWeight < this.getSolution().getUpperBound()) {
				if (selectedEdges.size() == this.k-1) {
					this.setSolution(currentWeight, new HashSet<Edge>(selectedEdges));
					//unselectedEdges.add(selectedEdges.last());

				} else if (selectedNodes.size() < this.k) {
					for (Edge remainingEdge : this.edgeArray) {

						if ((this.selectedNodes.contains(remainingEdge.node1) != this.selectedNodes.contains(remainingEdge.node2))) {

							//super eigenartig: dieses abbruch if hier alleine ist viel schneller als, eine Ebene weiter oben als zusaezlicher parameter
							if(currentWeight + remainingEdge.weight >= this.getSolution().getUpperBound())
								return;

							int node;

							if(this.selectedNodes.contains(remainingEdge.node1))
								node = remainingEdge.node2;
							else
								node = remainingEdge.node1;

							this.selectedNodes.add(node);
							this.selectedEdges.add(remainingEdge);

							//branch(currentWeight + remainingEdge.weight, unselectedEdges, code + remainingEdge.node1 + "-" + remainingEdge.node2 + " ");
							branch(currentWeight + remainingEdge.weight);

							this.selectedEdges.remove(remainingEdge);
							this.selectedNodes.remove(node);
						}
					}
				}
			}
		}
	}

	//private int calcLowerBound(int currentWeight, TreeSet<Edge> unselectedEdges){
	private int calcLowerBound(int currentWeight){
		int weight = 0;
		int count = 0;

		for(Edge edge : this.edgeArray){
			if(selectedEdges.size() + count >= this.k-1)
				return weight+currentWeight;

			if((!this.selectedNodes.contains(edge.node1) || !this.selectedNodes.contains(edge.node2))){
				weight+=edge.weight;
				count++;
			}
		}

		return weight+currentWeight;
	}

	private MST prim2(TreeSet<Edge> edges){
		HashSet<Edge> mstSetup = new HashSet<Edge>();
		TreeSet<Integer> mstNodes = new TreeSet<Integer>();
		mstNodes.add(edges.first().node1);

		return prim2(edges,mstSetup,mstNodes,0);
	}

	private MST prim2(TreeSet<Edge> edges, HashSet<Edge> mst, TreeSet<Integer> mstNodes,int weight){
		Edge minEdge = null;
		int minWeight = Integer.MAX_VALUE;

		for(Edge edge : edges){
			if((mstNodes.contains(edge.node1) != mstNodes.contains(edge.node2)) && edge.weight < minWeight){
				minEdge = edge;
				minWeight = edge.weight;
			}
		}

		if(minEdge != null) {
			weight+= minEdge.weight;
			mst.add(minEdge);

			/*TreeSet<Edge> newEdges = new TreeSet<Edge>(edges);
			newEdges.remove(minEdge);*/
			edges.remove(minEdge);

			//TreeSet<Integer> newMstNodes = new TreeSet<Integer>(mstNodes);
			mstNodes.add(minEdge.node1);
			mstNodes.add(minEdge.node2);


			return prim2(edges,mst, mstNodes,weight);
		}

		return new MST(mst,weight);
	}

	private class MST {
		private HashSet<Edge> edges;
		private int weight;

		private MST(HashSet<Edge> edges, int weight) {
			this.weight = weight;
			this.edges = edges;
		}
	}
}