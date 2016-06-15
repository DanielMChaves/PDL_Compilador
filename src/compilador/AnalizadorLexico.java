package compilador;

import java.io.*;
import java.util.ArrayList;

import datos.*;

public class AnalizadorLexico extends Matriz_ALexico {

	private static int llavesLeidas = 0;
	private static int tablaLocal = 0;
	private static int lineaError = 1;
	private static boolean declarandoFuncion = false;
	private static boolean declarandoParametros = false;

	public static void analizadorLexico(String rutaCodigo, String rutaTokens, 
			ArrayList<String> listaErrores, ArrayList<Token> listaTokens, 
			ArrayList<TablaDeSimbolos> tablaDeSimbolos, TablaDePR tablasReservada){

		FileReader frCodigo = null;

		FileWriter fwTokens = null;
		PrintWriter pwTokens = null;

		int caracterLeido = 0;

		try{
			frCodigo = new FileReader(rutaCodigo);

			fwTokens = new FileWriter(rutaTokens);
			pwTokens = new PrintWriter(fwTokens);

			caracterLeido = frCodigo.read();

			while(caracterLeido != -1) {

				int estadoAFD = 0;
				String accionAFD;

				int nuevoEstado = 0;
				int nuevaAccion = 0;

				int posicion = -1;
				String valor = "";
				String lexema = "";
				boolean tokenGenerado = false;
				boolean tokenError = false;

				while(!tokenGenerado && !tokenError){

					nuevoEstado = estadoPos(estadoAFD);
					nuevaAccion = simboloPos(String.valueOf((char) caracterLeido));

					accionAFD = matrizLex[nuevoEstado][nuevaAccion].getAccion();
					estadoAFD = matrizLex[nuevoEstado][nuevaAccion].getEstado();

					switch(accionAFD){

					case "ERROR":
						tokenError = true;
						listaErrores.add("Error en la linea: " + lineaError);
						listaErrores.add("Error en el Analizador Lexico: No se ha generado un Token correctamente");
						listaErrores.add("                               Caracter leido no valido");
						listaErrores.add("");
						break;
					case "A":
						caracterLeido = frCodigo.read(); 
						break;
					case "B":
						generaToken("parentesis_llave", "(", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						if(declarandoFuncion)
							declarandoParametros = true;
						break;
					case "C":
						generaToken("parentesis_llave", ")", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						if(declarandoFuncion)
							declarandoParametros = false;
						break;
					case "D": 
						generaToken("parentesis_llave", "{", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						llavesLeidas++;
						break;
					case "E":
						generaToken("parentesis_llave", "}", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						llavesLeidas--;
						if(llavesLeidas == 0){
							declarandoFuncion = false;
							tablaDeSimbolos.get(tablaLocal).getPrimero().setTipo(Tipo.function);
							tablaDeSimbolos.get(0).insertarTokenTS(tablaDeSimbolos.get(tablaLocal).getPrimero());
							tablaDeSimbolos.get(tablaLocal).borrarPrimero();
						}
						break;
					case "F":  
						generaToken("op_aritmetico", "-", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "G":  
						generaToken("op_relacional", ">", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "H":
						caracterLeido = frCodigo.read();
						break;
					case "I":  
						generaToken("op_logico", "&&", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "J":  
						generaToken("op_asignacion", "=", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "K":  
						generaToken("eol", "\\n", pwTokens, listaTokens);
						lineaError++;
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "L":  
						generaToken("eof", "-", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "M":
						valor = String.valueOf((char) caracterLeido);
						caracterLeido = frCodigo.read();
						break;
					case "N":  
						valor = concatenar(valor, String.valueOf((char) caracterLeido));
						caracterLeido = frCodigo.read();
						break;
					case "Ñ":
						if(Integer.parseInt(valor) >= 32767){
							tokenError = true;
							listaErrores.add("Error en la linea: " + lineaError);
							listaErrores.add("Error en el Analizador Lexico: No se ha generado un Token correctamente");
							listaErrores.add("                               Constante numerica mayor de 32.767");
							listaErrores.add("");
							estadoAFD = 0;
						}
						else{
							generaToken("numero", valor, pwTokens, listaTokens);
							tokenGenerado = true;
							estadoAFD = 0;
							valor = "";
						}
						
						break;
					case "O":  
						lexema = String.valueOf((char) caracterLeido);
						caracterLeido = frCodigo.read();
						break;
					case "P":  
						lexema = concatenar(lexema, String.valueOf((char) caracterLeido));
						caracterLeido = frCodigo.read();
						break;
					case "Q":

						posicion = tablasReservada.buscarTS(lexema);

						// Palabra Reservada
						if(posicion >= 0){

							if(lexema.equals("function")){
								tablaDeSimbolos.add(new TablaDeSimbolos());
								tablaLocal++;
								declarandoFuncion = true;
							}
							generaToken("palabra_reservada", lexema, pwTokens, listaTokens);
						}

						// NO Palabra Reservada
						else{

							// Buscamos en Tabla Local si estamos declarando
							if(declarandoFuncion)
								posicion = tablaDeSimbolos.get(tablaLocal).buscarTS(lexema);

							// Buscamos en Tabla Global si NO estamos declarando
							else
								posicion = tablaDeSimbolos.get(0).buscarTS(lexema);

							// Si esta en alguna Tabla
							if(posicion >= 0)
								generaToken("identificador", lexema, pwTokens, listaTokens);
							else{

								if(declarandoFuncion){
									tablaDeSimbolos.get(tablaLocal).insertarTS("identificador", lexema);
								}
								else
									tablaDeSimbolos.get(0).insertarTS("identificador", lexema);

								generaToken("identificador", lexema, pwTokens, listaTokens);
							}
						}
						tokenGenerado = true;
						estadoAFD = 0;
						break;
					case "R":  
						caracterLeido = frCodigo.read();
						break;
					case "S":  
						caracterLeido = frCodigo.read();
						break;
					case "T":
						caracterLeido = frCodigo.read();
						break;
					case "U":
						caracterLeido = frCodigo.read();
						break;
					case "V":
						caracterLeido = frCodigo.read();
						break;
					case "W":
						caracterLeido = frCodigo.read();
						estadoAFD = 0;
						break;
					case "X":
						lexema = String.valueOf((char) caracterLeido);
						caracterLeido = frCodigo.read();
						break;
					case "Y":
						lexema = concatenar(lexema, String.valueOf((char) caracterLeido));
						caracterLeido = frCodigo.read();
						break;
					case "Z":
						lexema = concatenar(lexema, String.valueOf((char) caracterLeido));
						generaToken("cadena_caracteres",lexema, pwTokens, listaTokens);
						estadoAFD = 0;
						tokenGenerado = true;
						caracterLeido = frCodigo.read();
						break;
					case "AA":
						caracterLeido = frCodigo.read();
						break;
					case "AB":  
						generaToken("op_aritmetico", "++", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					case "AC":
						generaToken("coma", ",", pwTokens, listaTokens);
						tokenGenerado = true;
						estadoAFD = 0;
						caracterLeido = frCodigo.read();
						break;
					default:
						tokenError = true;
					}

				}
			}

			if(caracterLeido == -1){
				generaToken("eof","-", pwTokens, listaTokens);
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fwTokens)
					fwTokens.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	/**
	 * Funcion que genera un token
	 * @param id
	 * @param cadena
	 * @param pwTokens
	 * @return
	 */
	public static Token generaToken(String id, String cadena, PrintWriter pwTokens, ArrayList<Token> listaTokens){

		Token tokenCreado = new Token(id, cadena);
		listaTokens.add(tokenCreado);
		pwTokens.println("< " + id + ", " + cadena + " >");
		return tokenCreado;	
	}

	/**
	 * Funcion que concatena Strings
	 * @param leyendo
	 * @param lexema
	 * @return
	 */
	public static String concatenar(String leyendo, String lexema){
		return leyendo + lexema;
	}

}
