/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;
	private boolean grafoCreato = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	this.txtResult.clear();
    	if(!this.grafoCreato) {
    		this.txtResult.appendText("Creare il grafo\n");
    		return;
    	}
    	if(this.txtX.getText().equals("")) {
    		this.txtResult.appendText("Selezionare una soglia X\n");
    		return;
    	}
    	double x = 0.0;
    	try {
    		x = Double.parseDouble(this.txtX.getText());
    	}catch (NumberFormatException e) {
    		this.txtResult.appendText("Il valore di soglia X deve essere un decimale positivo compreso tra 0 e 1");
    		return;
    	}
    	if(x<=0.0 || x>1.0) {
    		this.txtResult.appendText("Il valore di soglia X deve essere un decimale positivo compreso tra 0 e 1");
    		return;
    	}
    	Business partenza = this.cmbLocale.getValue();
    	if(partenza==null) {
    		this.txtResult.appendText("Selezionare un locale di partenza\n");
    		return;
    	}
    	List<Business> percorso = this.model.calcolaPercorso(partenza, x);
    	if(percorso==null) {
    		this.txtResult.appendText("Nessun percorso trovato\n");
    		return;
    	}
    	this.txtResult.appendText("Individuato il seguente percorso:\n");
    	for(Business b : percorso) {
    		this.txtResult.appendText(b+"\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	String city = this.cmbCitta.getValue();
    	Integer year = this.cmbAnno.getValue();
    	if(city==null) {
    		this.txtResult.appendText("Selezionare una città\n");
    		return;
    	}
    	if(year==null) {
    		this.txtResult.appendText("Selezionare un anno\n");
    		return;
    	}
    	this.model.creaGrafo(city, year);
    	this.grafoCreato = true;
    	this.txtResult.appendText("Grafo creato.\n# vertici: "+this.model.getNumVertici()+"\n# archi: "+this.model.getNumArchi()+"\n");
    	this.cmbLocale.getItems().clear();
    	this.cmbLocale.getItems().addAll(this.model.getLocali());

    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	this.txtResult.clear();
    	if(!this.grafoCreato) {
    		this.txtResult.appendText("Creare il grafo\n");
    		return;
    	}
    	this.txtResult.appendText("Il miglior locale in cui passare la serata è: "+this.model.getBestLocale()+"\n");
    	

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbCitta.getItems().addAll(this.model.getCities());
    	this.cmbAnno.getItems().add(2005);
    	this.cmbAnno.getItems().add(2006);
    	this.cmbAnno.getItems().add(2007);
    	this.cmbAnno.getItems().add(2008);
    	this.cmbAnno.getItems().add(2009);
    	this.cmbAnno.getItems().add(2010);
    	this.cmbAnno.getItems().add(2011);
    	this.cmbAnno.getItems().add(2012);
    	this.cmbAnno.getItems().add(2013);
    }
}
