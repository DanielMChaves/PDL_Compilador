package datos;

public class TuplaPila_ASintSem {

	private String elemento;
	private Tipo tipo;
	
	public TuplaPila_ASintSem(String elemento, Tipo tipo){
		this.elemento = elemento;
		this.tipo = tipo;
	}

	public String getElemento() {
		return elemento;
	}

	public void setElemento(String elemento) {
		this.elemento = elemento;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
}
