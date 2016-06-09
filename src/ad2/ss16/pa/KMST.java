package ad2.ss16.pa;

import sun.reflect.generics.tree.Tree;

import java.util.*;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int k;
	private TreeSet<Edge> edges;
	TreeSet<Edge> newSelectedEdges = new TreeSet<Edge>();
	TreeSet<Integer> newSelectedNodes = new TreeSet<Integer>();



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
		this.setSolution(Integer.MAX_VALUE,edges);
		this.edges = new TreeSet<Edge>(edges);
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
			TreeSet<Edge> newSelectedEdges = new TreeSet<Edge>();
			newSelectedEdges.add(edge);
			TreeSet<Integer> newSelectedNodes = new TreeSet<Integer>();
			newSelectedNodes.add(edge.node1);
			newSelectedNodes.add(edge.node2);

			branch(newSelectedEdges,newSelectedNodes,edge.weight);
		}
	}

	private void branch(TreeSet<Edge> selectedEdges,TreeSet<Integer> selectedNodes,int currentWeight){
		if(currentWeight < this.getSolution().getUpperBound()) {
			MST mst = prim2(selectedEdges);

			if (mst.weight < this.getSolution().getUpperBound()) {

				if (mst.size() == this.k) {
					this.setSolution(mst.weight, mst.edges);

				} else if (mst.size() < this.k) {

					for (Edge remainingEdge : this.edges) {
						/*if (selectedNodes.contains(remainingEdge.node1) ^ selectedNodes.contains(remainingEdge.node2)) {

							selectedEdges.add(remainingEdge);
							selectedNodes.add(remainingEdge.node1);
							selectedNodes.add(remainingEdge.node2);

							branch(selectedEdges, selectedNodes, currentWeight + remainingEdge.weight);
							selectedEdges.remove(remainingEdge);
							selectedNodes.remove(remainingEdge.node1);
							selectedNodes.remove(remainingEdge.node2);
						}*/
						if(selectedNodes.contains(remainingEdge.node1) && !selectedNodes.contains(remainingEdge.node2)){
							selectedEdges.add(remainingEdge);
							selectedNodes.add(remainingEdge.node2);

							branch(selectedEdges, selectedNodes, currentWeight + remainingEdge.weight);
							selectedEdges.remove(remainingEdge);
							selectedNodes.remove(remainingEdge.node2);

						} else if(!selectedNodes.contains(remainingEdge.node1) && selectedNodes.contains(remainingEdge.node2)){
							selectedEdges.add(remainingEdge);
							selectedNodes.add(remainingEdge.node1);

							branch(selectedEdges, selectedNodes, currentWeight + remainingEdge.weight);
							selectedEdges.remove(remainingEdge);
							selectedNodes.remove(remainingEdge.node1);
						}

					}
				}
			}
		}
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
}