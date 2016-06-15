package datos;

import java.util.ArrayList;

public class Token {
 
	// Atributos de un Token
	private String id;
	private String cadena;
	private Tipo tipo;
	private int tamano;
	private int desplazamiento;
	private int numParametros;
	private ArrayList<Tipo> tipoParametros;
	private Tipo tipoValorDevuelto;
	
	// Constructor de un Token
	public Token(String id, String cadena){
		this.id = id;
		this.cadena = cadena;
		this.tipo = null;
		this.tamano = 0;
		this.desplazamiento = 0;
		this.numParametros = 0;
		this.tipoParametros = new ArrayList<Tipo>();
		this.tipoValorDevuelto = Tipo.vacio;
	}

	// Metodos getter y setter de un Token
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public int getDesplazamiento() {
		return desplazamiento;
	}

	public void setDesplazamiento(int desplazamiento) {
		this.desplazamiento = desplazamiento;
	}

	public int getNumParametros() {
		return numParametros;
	}

	public void setNumParametros(int numParametros) {
		this.numParametros = numParametros;
	}

	public ArrayList<Tipo> getTipoParametros() {
		return tipoParametros;
	}

	public void setTipoParametros(Tipo tipo) {
		this.tipoParametros.add(tipo);
	}

	public Tipo getTipoValorDevuelto() {
		return tipoValorDevuelto;
	}

	public void setTipoValorDevuelto(Tipo tipoValorDevuelto) {
		this.tipoValorDevuelto = tipoValorDevuelto;
	}

	public int getTamano() {
		return tamano;
	}

	public void setTamano(int tamano) {
		this.tamano = tamano;
	}
	
}
