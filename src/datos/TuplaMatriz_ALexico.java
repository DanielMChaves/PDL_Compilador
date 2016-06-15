package datos;

public class TuplaMatriz_ALexico {

	private int estado;
	private String accion;
	
	public TuplaMatriz_ALexico(int estado, String accion){
		this.estado = estado;
		this.accion = accion;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String acción) {
		this.accion = acción;
	}
}
