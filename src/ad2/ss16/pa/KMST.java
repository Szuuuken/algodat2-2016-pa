package ad2.ss16.pa;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private TreeSet<Edge> edges;
	private TreeSet<Edge> selectedEdges = new TreeSet<Edge>();
	TreeSet<Integer> selectedNodes = new TreeSet<Integer>();


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


		this.edges = new TreeSet<Edge>(edges);
		MST mst = prim2(this.edges);
		this.setSolution(mst.weight,mst.edges);
		//this.edgeArray = this.edges.toArray(new Edge[this.edges.size()]);
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
		for(Edge edge: this.edges){
			this.selectedEdges.add(edge);
			selectedNodes.add(edge.node1);
			selectedNodes.add(edge.node2);
			TreeSet<Edge> unselectedEdges = new TreeSet<Edge>();

			branch(edge.weight,unselectedEdges, edge.node1 + "-" + edge.node2 +" ");
			this.selectedEdges.remove(edge);
			//unselectedEdges.add(edge);
		}
	}

	private void branch(int currentWeight, TreeSet<Edge> unselectedEdges, String code){
		int localLowerBound = calcLowerBound(currentWeight,unselectedEdges);
		//if (true) System.out.println(code + " \t " + localLowerBound + " \t " + currentWeight);

		if(localLowerBound >= this.getSolution().getUpperBound()){
			return;
		}

		if(localLowerBound < this.getSolution().getUpperBound()) {

			if (currentWeight < this.getSolution().getUpperBound()) {
				if (selectedEdges.size() == this.k-1) {
					this.setSolution(currentWeight, new HashSet<Edge>(selectedEdges));

				} else if (selectedNodes.size() < this.k) {
					for (Edge remainingEdge : this.edges) {

						if ((this.selectedNodes.contains(remainingEdge.node1) != this.selectedNodes.contains(remainingEdge.node2)) ) {

							if(currentWeight + remainingEdge.weight >= this.getSolution().getUpperBound())
								return;

							int node;

							if(this.selectedNodes.contains(remainingEdge.node1))
								node = remainingEdge.node2;
							else
								node = remainingEdge.node1;

							this.selectedNodes.add(node);

							this.selectedEdges.add(remainingEdge);

							branch(currentWeight + remainingEdge.weight,unselectedEdges, code + remainingEdge.node1 + "-" + remainingEdge.node2 + " ");

							unselectedEdges.add(remainingEdge);
							this.selectedEdges.remove(remainingEdge);
							this.selectedNodes.remove(node);
						}
					}
				}
			}
		}
	}

	private int calcLowerBound(int currentWeight, TreeSet<Edge> unselectedEdges){
		int weight = 0;
		int count = 0;

		Iterator<Edge> it = this.edges.iterator();
		while(it.hasNext() && (selectedEdges.size()+count < this.k-1)){
			Edge edge = it.next();

			if(this.selectedNodes.contains(edge.node1) != this.selectedNodes.contains(edge.node2) && !unselectedEdges.contains(edge)){
				weight+=edge.weight;
				count++;
			}else if(!this.selectedNodes.contains(edge.node1) && !this.selectedNodes.contains(edge.node2)  && !unselectedEdges.contains(edge)){
				weight+= edge.weight;
				count++;
			}

		}

		return weight+currentWeight;
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
			if((mstNodes.contains(edge.node1) != mstNodes.contains(edge.node2)) && edge.weight < minWeight){
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

	private class MST {
		private TreeSet<Edge> edges;
		private int weight;

		private MST(TreeSet<Edge> edges, int weight) {
			this.weight = weight;
			this.edges = edges;
		}
	}
}