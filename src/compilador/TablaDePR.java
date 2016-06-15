package compilador;

import java.util.ArrayList;
import datos.Token;

public class TablaDePR{

	private ArrayList<Token> tablaDeSimbolos;
	private String palabras_reservadas[] = {"bool", "chars", "do", "false", 
			"function", "if", "int", "prompt", "return", "true", "var", "while", "write"};

	// Creación de la Tabla de Simbolos
	public TablaDePR(){
		tablaDeSimbolos = new ArrayList<Token>();
		for(int i = 0; i < palabras_reservadas.length; i++){
			tablaDeSimbolos.add(new Token("palabra_reservada", palabras_reservadas[i]));
		}
	}
	
	/**
	 * Buscar un token en la Tabla de Simbolos
	 * @param cadena
	 * @return int
	 */
	public int buscarTS(String cadena){

		int posicion = -1;
		boolean encontrado = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !encontrado; i++){
			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				encontrado = true;
				posicion = i;
			}
		}

		return posicion;
	}

	/**
	 * Metodo que imprime la Tabla de Simbolos
	 * 
	 */

	public String imprimirTS(String nombre, int id){
		String resultado = "CONTENIDO DE LA TABLA " + nombre + " #" + id + ":\n\n";

		for(int i = 0; i < tablaDeSimbolos.size(); i++){
			resultado = resultado 
					+ "* LEXEMA: '" + tablaDeSimbolos.get(i).getCadena() + "'\n"
					+ "---------- ----------\n";
		}

		return resultado;
	}

	/**
	 * Metodo para eliminar la Tabla de Palabras Reservadas
	 * @param TS
	 */
	public void destruirTS(TablaDePR TS){
		TS = null;
	}

}

