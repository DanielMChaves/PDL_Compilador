package compilador;

import java.util.ArrayList;

import datos.Tipo;
import datos.Token;

public class TablaDeSimbolos{

	private ArrayList<Token> tablaDeSimbolos;

	// Creación de la Tabla de Simbolos
	public TablaDeSimbolos(){
		tablaDeSimbolos = new ArrayList<Token>();
	}

	/**
	 * Insertar un token en la Tabla de Simbolos
	 * @param id
	 * @param cadena
	 */
	public void insertarTS(String id, String cadena){
		tablaDeSimbolos.add(new Token(id, cadena));
	}

	/**
	 * Insertar un token en la Tabla de Simbolos
	 * @param id
	 * @param cadena
	 */
	public void insertarTokenTS(Token token){
		tablaDeSimbolos.add(token);
	}

	/**
	 * Inserta el Tipo en el Token
	 * @param cadena
	 * @param tipo
	 * @param tamano
	 * @return
	 */
	public boolean insertarTipoTS(String cadena, Tipo tipo, int tamano){

		boolean hecho = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !hecho; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				tablaDeSimbolos.get(i).setTipo(tipo);
				tablaDeSimbolos.get(i).setDesplazamiento(tamano);
				hecho = true;
			}
		}

		return hecho;

	}

	/**
	 * Inserta el Tipo Devuelto en el Token
	 * @param elemento
	 * @param tipo
	 */
	public boolean insertarTipoValorDevueltoTS(String cadena, Tipo tipo) {

		boolean hecho = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !hecho; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				tablaDeSimbolos.get(i).setTipoValorDevuelto(tipo);
				hecho = true;
			}
		}

		return hecho;

	}

	/**
	 * Inserta los Parametros en el Token
	 * @param elemento
	 * @param listaArgumentos
	 */
	public boolean insertarParametrosTS(String cadena, ArrayList<Tipo> listaArgumentos) {

		boolean hecho = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !hecho; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){


				for(int j = 0; j < listaArgumentos.size(); j++){
					tablaDeSimbolos.get(i).setTipoParametros(listaArgumentos.get(j));
				}

				hecho = true;
			}
		}

		return hecho;

	}

	/**
	 * Metodo para acceder al primer elemento
	 */
	public Token getPrimero(){
		return tablaDeSimbolos.get(0);
	}

	/**
	 * Metodo para borrar al primer elemento
	 */
	public void borrarPrimero(){
		tablaDeSimbolos.remove(0);
	}

	/**
	 * Metodo para acceder al ultimo elemento
	 */
	public Token getUltimo(){
		return tablaDeSimbolos.get(tablaDeSimbolos.size() - 1);
	}

	/**
	 * Metodo para borrar el ultimo elemento
	 */
	public void borrarUltimo(){
		tablaDeSimbolos.remove(tablaDeSimbolos.size() - 1);
	}

	/**
	 * Borrar un token en la Tabla de Simbolos
	 * @param cadena
	 * @return int
	 */
	public boolean borrarTokenTS(String cadena){

		boolean borrado = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !borrado; i++){
			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				tablaDeSimbolos.remove(i);
				borrado = true;
			}
		}

		return borrado;
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
	 * Buscar un token en la Tabla de Simbolos
	 * @param cadena
	 * @return int
	 */
	public Token buscarTokenTS(int posicion){

		return tablaDeSimbolos.get(posicion);
	}

	/**
	 * Buscar el Tipo de un Token en la Tabla de Simbolos
	 * @param cadena
	 * @return
	 */
	public Tipo buscarTipoTS(String cadena){

		Tipo tipo = Tipo.nulo;
		boolean hecho = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !hecho; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				tipo = tablaDeSimbolos.get(i).getTipo();
				hecho = true;
			}
		}

		return tipo;

	}

	/**
	 * Buscar el Tipo Devuelto de una funcion en la Tabla de Simbolos
	 * @param cadena
	 * @return
	 */
	public Tipo buscarTipoValorDevueltoTS(String cadena){

		Tipo tipo = Tipo.nulo;
		boolean hecho = false;

		for(int i = 0; i < tablaDeSimbolos.size() && !hecho; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){
				tipo = tablaDeSimbolos.get(i).getTipoValorDevuelto();
				hecho = true;
			}
		}

		if(!hecho){
			tipo = Tipo.error;
		}

		return tipo;

	}

	/**
	 * Compara si dos listas de Argumentos/Parametros son iguales
	 * @param lineaError 
	 * @param elemento
	 * @param listaParametros2
	 * @return
	 */
	public boolean argumentosIguales(String cadena, ArrayList<Tipo> listaParametros, ArrayList<String> listaErrores, int lineaError) {

		boolean igualTabla = false;
		boolean igualLista = true;

		for(int i = 0; i < tablaDeSimbolos.size() && !igualTabla; i++){

			if(tablaDeSimbolos.get(i).getCadena().equals(cadena)){

				igualTabla = true;
				igualLista = true;

				if(tablaDeSimbolos.get(i).getTipoParametros().size() == listaParametros.size()){

					for(int j = 0; j < tablaDeSimbolos.get(i).getTipoParametros().size() && igualLista; j++){

						if(!tablaDeSimbolos.get(i).getTipoParametros().get(j).equals(listaParametros.get(j))){

							igualLista = false;
							listaErrores.add("Error en la linea: " + lineaError);
							listaErrores.add("Error en el Analizador SinSem: El argumento " + (j + 1) + " de la función '" + tablaDeSimbolos.get(i).getCadena() 
									+ "' deberia ser de tipo '" + tablaDeSimbolos.get(i).getTipoParametros().get(j) + "'");
							listaErrores.add("                               pero es de tipo '" + listaParametros.get(j) + "'");
							listaErrores.add("");

						}
					}
				}
				else{
					igualLista = false;
					listaErrores.add("Error en la linea: " + lineaError);
					listaErrores.add("Error en el Analizador SinSem: La función '" + tablaDeSimbolos.get(i).getCadena() + 
							"' ha sido llamada con '" + listaParametros.size() + "' argumento(s)");
					listaErrores.add("                               pero esperaba recibir '" + tablaDeSimbolos.get(i).getTipoParametros().size() + "'");
					listaErrores.add("");
				}

			}
			else{
				igualLista = false;
			}
		}

		return igualLista;

	}

	/**
	 * Metodo que coloca bien el Desplazamiento dentro de la Tabla de Simbolos
	 */
	public void colocarDesplazamiento(){

		int tamano = 0;
		int desplazamiento = 0;

		for(int i = 0; i < tablaDeSimbolos.size(); i++){

			if(tablaDeSimbolos.get(i).getTipo().equals(Tipo.function)){
				tablaDeSimbolos.get(i).setDesplazamiento(0);
			}
			else{
				tamano = tablaDeSimbolos.get(i).getDesplazamiento();
				tablaDeSimbolos.get(i).setTamano(tamano);
				tablaDeSimbolos.get(i).setDesplazamiento(desplazamiento);
				desplazamiento = desplazamiento + tamano;
			}
		}

	}
	
	/**
	 * Obtiene el nombre de las tablas locales
	 * @param nombre
	 * @return 
	 */
	public ArrayList<String> obtenerNombreLocal(ArrayList<String> nombre){
		
		for(int i = 0; i < tablaDeSimbolos.size(); i++){

			if(tablaDeSimbolos.get(i).getTipo().equals(Tipo.function)){
				nombre.add(tablaDeSimbolos.get(i).getCadena());
			}
		}
		
		return nombre;
		
	}

	/**
	 * Tamaño de la tabla de simbolos
	 */
	public int tamanoTS(){
		return tablaDeSimbolos.size();
	}

	/**
	 * Metodo que imprime la Tabla de Simbolos
	 * 
	 */

	public String imprimirTS(String nombre, int id){
		String resultado = "CONTENIDO DE LA TABLA '" + nombre + "' #" + id + ":\n\n";

		for(int i = 0; i < tablaDeSimbolos.size(); i++){
			resultado = resultado 
					+ "* LEXEMA: '" + tablaDeSimbolos.get(i).getCadena() + "'\n"
					+ "  ATRIBUTOS:\n"
					+ "  + Tipo: '" + tablaDeSimbolos.get(i).getTipo() + "'\n";
					
					if(!Tipo.function.equals(tablaDeSimbolos.get(i).getTipo())){
						resultado = resultado 
								+ "  + Tamaño: '" + tablaDeSimbolos.get(i).getTamano() + "'\n"
								+ "  + Desplazamiento: '" + tablaDeSimbolos.get(i).getDesplazamiento() + "'\n";
					}

			if(Tipo.function.equals(tablaDeSimbolos.get(i).getTipo())){
				resultado = resultado
						+ "  + Numero de Parametros: '" + tablaDeSimbolos.get(i).getTipoParametros().size() + "'\n";

				if(!tablaDeSimbolos.get(i).getTipoParametros().equals(null)){

					for(int j = 0; j < tablaDeSimbolos.get(i).getTipoParametros().size(); j++){
						resultado = resultado
								+ "  + Tipo de Parametro[" + j + "]: '" 
								+ tablaDeSimbolos.get(i).getTipoParametros().get(j) + "'\n";
					}
				}

				resultado = resultado
						+ "  + Tipo de Valor Devuelto: '" + tablaDeSimbolos.get(i).getTipoValorDevuelto() + "'\n";
			}

			resultado = resultado
					+ "---------- ---------- ---------- ----------\n";
		}

		return resultado;
	}

	/**
	 * Metodo para eliminar la Tabla de Simbolos
	 * @param TS
	 */
	public void destruirTS(TablaDeSimbolos TS){
		TS = null;
	}

}
