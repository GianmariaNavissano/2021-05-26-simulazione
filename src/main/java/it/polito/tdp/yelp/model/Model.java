package it.polito.tdp.yelp.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> idMap;
	private List<Business> percorso;
	
	public Model() {
		this.dao = new YelpDao();
		this.idMap = this.dao.getAllBusiness();
	}
	
	public List<String> getCities(){
		List<String> citta = this.dao.getCities();
		Collections.sort(citta);
		return citta;
	}
	
	public void creaGrafo(String city, int year) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici
		Graphs.addAllVertices(grafo,  this.dao.getVertex(idMap, city, year));
		
		//archi
		for(Adiacenza a : this.dao.getEdges(idMap, city, year)) {
			Graphs.addEdgeWithVertices(grafo, a.getB1(), a.getB2(), a.getPeso());
		}
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Business getBestLocale() {
		Business best = null;
		double bestScore = Double.MIN_VALUE;
		
		for(Business b : this.grafo.vertexSet()) {
			double score = 0.0;
			for(Business vicino : Graphs.neighborListOf(grafo, b)) {
				if(grafo.getEdge(b, vicino)!=null) {
					
					//arco USCENTE
					score -= grafo.getEdgeWeight(grafo.getEdge(b, vicino));
					
				}else {
					if(grafo.getEdge(vicino, b)!=null) {
						
						//arco ENTRANTE
						score += grafo.getEdgeWeight(grafo.getEdge(vicino, b));
						
					}
				}
			}
			if(score>bestScore) {
				bestScore = score;
				best = b;
			}
		}
		return best;
	}
	
	public List<Business> getLocali(){
		List<Business> result = new LinkedList<>(this.grafo.vertexSet());
		Collections.sort(result);
		return result;
	}
	
	public List<Business> calcolaPercorso(Business partenza, double x){
		this.percorso = null;
		List<Business> parziale = new LinkedList<>();
		parziale.add(partenza);
		Business ultimo = this.getBestLocale();
		this.cerca(parziale, x, ultimo);
		return this.percorso;
	}
	
	public void cerca(List<Business> parziale, double x, Business ultimo) {
		
		Business precedente = parziale.get(parziale.size()-1);
		
		//CASO TERMINALE
		if(precedente.equals(ultimo)) {
			
			if(this.percorso==null) {
				//prima volta che entro nel caso teminale
				this.percorso = new LinkedList<>(parziale);
			}else {
				if(parziale.size()<this.percorso.size()) {
					//se è di lunghezza minore
					this.percorso = new LinkedList<>(parziale);
				}
					
			}
			
			return;
		}
		
		
		//GENERO SOTTOPROBLEMI
		for(Business b : Graphs.neighborListOf(grafo, precedente)) {
			
			//posso aggiungere b alla soluzione parziale SOLO SE l'arco è 
			//USCENTE da parziale e cioè va verso b
			if(grafo.getEdge(precedente, b)!=null) {
				
				//verifico che il peso dell'arco rispetti il requisito del problema
				if(grafo.getEdgeWeight(grafo.getEdge(precedente, b))>=x) {
					parziale.add(b);
					this.cerca(parziale, x, ultimo);
					
					//backtracking
					parziale.remove(b);
				}
			}
		}
	}
	
	
}
