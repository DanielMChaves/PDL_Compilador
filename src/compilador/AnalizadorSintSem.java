package compilador;

import java.util.*;

import datos.*;

public class AnalizadorSintSem {

	// Contador linea del error
	private static int lineaError = 1;

	// Flags
	private static boolean fin = false;
	private static boolean error = false;
	private static int tablaActual = 0;

	// Estructuras de Datos
	private static ArrayList<Tipo> listaArgumentos = new ArrayList<Tipo>();
	private static ArrayList<Tipo> listaParametros = new ArrayList<Tipo>();
	private static LinkedList<Token> bufferTokens = new LinkedList<Token>();
	private static Stack<TuplaPila_ASintSem> pila = new Stack<TuplaPila_ASintSem>();
	private static Stack<TuplaPila_ASintSem> pilaAux = new Stack<TuplaPila_ASintSem>();

	public static void analizadorSintSem(String rutaParse, ArrayList<String> listaErrores,
			ArrayList<Token> listaTokens, ArrayList<String> listaParse, ArrayList<TablaDeSimbolos> tablaDeSimbolos) {

		// Inicializacion de la Pila
		pila.add(new TuplaPila_ASintSem("$", Tipo.nulo));
		pila.add(new TuplaPila_ASintSem("ProgramaPrincipal", Tipo.nulo));

		// Pasamos los Tokens al buffer de Tokens
		for (int i = 0; i < listaTokens.size() - 1; i++) {
			bufferTokens.addLast(listaTokens.get(i));
		}

		bufferTokens.addLast(new Token("Fin Buffer", "$"));

		TuplaPila_ASintSem cima;
		int posicion = 0;

		while (!fin && !error) {

			Token siguienteToken = bufferTokens.get(posicion); // Siguiente Token

			TuplaPila_ASintSem cimaPila = pila.lastElement(); // Cima Pila


			if (cimaPila.getElemento() == "$" && siguienteToken.getCadena() == "$") {
				fin = true;
			} else if (esTerminal(cimaPila.getElemento())) {

				if (siguienteToken.getId().equals("identificador") && cimaPila.getElemento().equals("id")) {
					pila.pop();
					pilaAux.push(new TuplaPila_ASintSem(siguienteToken.getCadena(), Tipo.nulo));
					posicion++;
				} else if (siguienteToken.getId().equals("cadena_caracteres") && cimaPila.getElemento().equals("cadena")) {
					pila.pop();
					pilaAux.push(new TuplaPila_ASintSem(siguienteToken.getCadena(), Tipo.nulo));
					posicion++;
				} else if (cimaPila.getElemento().equals(siguienteToken.getCadena())
						|| (siguienteToken.getId().equals("numero") && cimaPila.getElemento().equals("entero"))) {
					cima = pila.pop();
					pilaAux.push(cima);
					posicion++;
				} else {
					error = true;
					listaErrores.add("Error en la linea: " + lineaError);
					listaErrores.add("Error en el Analizador SinSem: Los elementos terminales de la cima del la pila: '" + cimaPila.getElemento() + "'");
					listaErrores.add("                               y el siguiente token en el buffer: '" + siguienteToken.getCadena() + "' son distintos");
					listaErrores.add("");
				}
			} else if (esNoTerminal(cimaPila.getElemento())) {

				String nt = cimaPila.getElemento();
				String t = siguienteToken.getCadena();

				int ntPos = Matriz_ASintSem.noTerminalPos(nt);
				int tPos = Matriz_ASintSem.terminalPos(t);

				// Comprobar si es identificador o es identificador
				if (tPos == 2 && !siguienteToken.getId().equals("identificador")) {
					tPos = 24;
				}

				if (Matriz_ASintSem.matrizSint[ntPos][tPos] != -1) {
					accion(ntPos, tPos, listaErrores, listaParse);
				} else {
					error = true;
					listaErrores.add("Error en la linea: " + lineaError);
					listaErrores.add("Error en el Analizador SinSem: No hay accion en la matriz para el elemento la cima del la pila: '" + cimaPila.getElemento() + "'");
					listaErrores.add("                               y el siguiente token en el buffer: '" + siguienteToken.getCadena() + "'");
					listaErrores.add("");
				}

			} else if (esAccion(cimaPila.getElemento())) {

				accionSemantica(cimaPila.getElemento(), listaErrores, tablaDeSimbolos);
				pila.pop();
			} else {
				error = true;
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: El elemento de la cima del la pila: '" + cimaPila.getElemento() + "' es desconocido");
				listaErrores.add("");
			}

		}

	}

	/**
	 * Dado un elemento no terminal y terminal te da la opcion a realizar
	 *
	 * @param nt
	 * @param n
	 */
	public static void accion(int nt, int t, ArrayList<String> listaErrores, ArrayList<String> listaParse) {

		TuplaPila_ASintSem cima;

		switch (Matriz_ASintSem.matrizSint[nt][t]) {

		case -1:
			listaErrores.add("Error en la linea: " + lineaError);
			listaErrores.add("Error en el Analizador SinSem: Fallo en la Matriz de Acciones");
			listaErrores.add("");
			break;
		case 0:
			// ProgramaPrincipal -> Programa	[ if Programa.Tipo == ok
			// 										then ProgramaPrincipal.Tipo = ok
			// 									else ProgramaPrincipal.Tipo = error("") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A0", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Programa", Tipo.nulo));
			listaParse.add("1");
			break;
		case 1:
			// Programa -> Decla_FuncionComp Salto Programa	[ if Decla_FuncionComp.Tipo == ok && Programa1.Tipo == ok
			// 													then Programa0.Tipo = ok
			// 												else Programa0.Tipo = error("") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A1", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Programa", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Decla_FuncionComp", Tipo.nulo));
			listaParse.add("2");
			break;
		case 2:
			// Programa -> Funcion Salto Programa	[ if Funcion.Tipo == ok && Programa1.Tipo == ok
			// 											then Programa0.Tipo = ok
			// 										else Programa0.Tipo = error("") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Programa", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Funcion", Tipo.nulo));
			listaParse.add("3");
			break;
		case 3:
			// Programa -> Salto Programa	[ if Programa1.Tipo == ok
			// 									then Programa0.Tipo = ok
			// 								else Programa0.Tipo = error("") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A3", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Programa", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			listaParse.add("4");
			break;
		case 4:
			// Programa -> lambda [ Programa.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A4", Tipo.nulo));
			listaParse.add("5");
			break;
		case 5:
			// Salto -> \\n
			cima = pila.pop();
			pilaAux.push(cima);
			lineaError++;
			pila.push(new TuplaPila_ASintSem("\\n", Tipo.nulo));
			listaParse.add("6");
			break;
		case 6:
			// Decla_FuncionComp -> var Tipo id Con_Sin	[ if Tipo.Tipo == Con_Sin.Tipo || Con_Sin.Tipo == ok
			// 												then insertarTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano)
			// 											else Decla_FuncionComp.Tipo = error("Tipo Erroneo en la Declaracion de una Variable") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A6", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Con_Sin", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Tipo", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("var", Tipo.nulo));
			listaParse.add("7");
			break;
		case 7:
			// Decla_FuncionComp -> if (Expresion) Sentencia 	[ if Expresion.Tipo == boolean
			// 														then	if Sentencia.Tipo != ok
			// 																	then Decla_FuncionComp.Tipo = Sentencia.Tipo
			// 																else Decla_FuncionComp.Tipo = ok
			// 													else Decla_FuncionComp.Tipo = error("Tipo Erroneo en Condicion de un if") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A7", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Sentencia", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("if", Tipo.nulo));
			listaParse.add("8");
			break;
		case 8:
			// Decla_FuncionComp -> do Salto { Salto Cuerpo Salto } while (Expresion)	[ if Expresion.Tipo == boolean
			// 																				then	if Cuerpo.Tipo != ok
			// 																							then Decla_FuncionComp.Tipo = Cuerpo.Tipo
			// 																						else Decla_FuncionComp.Tipo = ok
			// 																			else Decla_FuncionComp.Tipo = error("Tipo Erroneo en la Expresion While") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A8", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("while", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("}", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Cuerpo", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("{", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("do", Tipo.nulo));
			listaParse.add("9");
			break;
		case 9:
			// Decla_FuncionComp -> Sentencia [ Decla_FuncionComp.Tipo =  Sentencia.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A9", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Sentencia", Tipo.nulo));
			listaParse.add("10");
			break;
		case 10:
			// Con_Sin -> = Expresion [ Con_Sin.Tipo = Expresion.Tipo]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A10", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("=", Tipo.nulo));
			listaParse.add("11");
			break;
		case 11:
			// Con_Sin -> lambda [ Con_Sin.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A11", Tipo.nulo));
			listaParse.add("12");
			break;
		case 12:
			// Tipo -> int [ Tipo.Tipo = entero ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A12", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("int", Tipo.nulo));
			listaParse.add("13");
			break;
		case 13:
			// Tipo -> bool [ Tipo.Tipo = booleano ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A13", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("bool", Tipo.nulo));
			listaParse.add("14");
			break;
		case 14:
			// Tipo -> chars [ Tipo.Tipo = cadena ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A14", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("chars", Tipo.nulo));
			listaParse.add("15");
			break;
		case 15:
			// Cuerpo -> Decla_FuncionComp Salto Cuerpo	[ if (Decla_FuncionComp.Tipo == bool || Decla_FuncionComp.Tipo == chars
			// 													|| Decla_FuncionComp.Tipo == int ) && Cuerpo1.Tipo == vacio
			// 												then Cuerpo0.Tipo = Decla_FuncionComp.Tipo
			// 											else if Decla_FuncionComp.Tipo == ok && Cuerpo1.Tipo == vacio
			// 												then Cuerpo0.Tipo = ok
			// 											else if Decla_FuncionComp.Tipo == ok && Cuerpo1.Tipo == ok
			// 												then Cuerpo0.Tipo = ok
			// 											else if Decla_FuncionComp.Tipo == ok && Cuerpo1.Tipo == bool
			// 													|| Cuerpo1.Tipo == chars || Cuerpo1.Tipo == int )
			// 												then Cuerpo0.Tipo = Cuerpo1.Tipo
			// 											else Cuerpo0.Tipo = error("Error en el Cuerpo de una Funcion") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A15", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Cuerpo", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Decla_FuncionComp", Tipo.nulo));
			listaParse.add("16");
			break;
		case 16:
			// Cuerpo -> lambda [ Cuerpo.Tipo = vacio ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A16", Tipo.nulo));
			listaParse.add("17");
			break;
		case 17:
			// Sentencia -> return Return_llamada [ Sentencia.Tipo = Return_llamada.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A17", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Return_llamada", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("return", Tipo.nulo));
			listaParse.add("18");
			break;
		case 18:
			// Sentencia -> write (Expresion)	[ if Expresion.Tipo == entero || Expresion.Tipo == cadena || Expresion.Tipo == booleano
			// 										then Sentencia.Tipo = ok
			// 									else Sentencia.Tipo = error("Tipo Erroneo en Sentencia write") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A18", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("write", Tipo.nulo));
			listaParse.add("19");
			break;
		case 19:
			// Sentencia -> prompt(id)	[ if buscarTipoTS(id.entrada) == entero || buscarTipoTS(id.entrada) == cadena
			// 								then Sentencia.Tipo = ok
			// 							else Sentencia.Tipo = error("Tipo Erroneo en Sentencia prompt") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A19", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("prompt", Tipo.nulo));
			listaParse.add("20");
			break;
		case 20:
			// Sentencia -> id Sentencia_llamada	[ if buscarTipoTS(id.entrada) == Sentencia_llamada.Tipo
			// 											then Sentencia.Tipo = ok
			// 										else Sentencia.Tipo = error("Tipo Erroneo en Sentencia de Asignacion") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A20", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Sentencia_llamada", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			listaParse.add("21");
			break;
		case 21:
			// Return_llamada -> Expresion [ Return_llamada.Tipo = Expresion.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A21", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			listaParse.add("22");
			break;
		case 22:
			// Return_llamada -> lambda [ Return_llamada.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A22", Tipo.nulo));
			listaParse.add("23");
			break;
		case 23:
			// Sentencia_llamada -> (Parametros) [ Sentencia_llamada.Tipo = Parametros.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A23", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Parametros", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			listaParse.add("24");
			break;
		case 24:
			// Sentencia_llamada -> = Expresion [ Sentencia_llamada.Tipo = Expresion.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A24", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("=", Tipo.nulo));
			listaParse.add("25");
			break;
		case 25:
			// Funcion -> function TipoFuncion id [ insertarTipoValorDevueltoTS(id.entrada, TipoFuncion.Tipo) ] (Argumentos)
			//	[ insertarTipoParametrosTS(id.entrada, Argumentos.Tipo) ] Salto { Salto Cuerpo }
			//	[ if Cuerpo.Tipo == TipoFuncion.Tipo
			// 		then Funcion.Tipo = ok
			// 	else Funcion.Tipo = error("Error entre el Tipo del Cuerpo y el Tipo de la Funcion") ]
			tablaActual++;
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A25-2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("}", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Cuerpo", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("{", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Salto", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("A25-1", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Argumentos", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("A25-0", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("TipoFuncion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("function", Tipo.nulo));
			listaParse.add("26");
			break;
		case 26:
			// TipoFuncion -> Tipo [ TipoFuncion.Tipo = Tipo.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A26", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Tipo", Tipo.nulo));
			listaParse.add("27");
			break;
		case 27:
			// TipoFuncion -> lambda [ TipoFuncion.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A27", Tipo.nulo));
			listaParse.add("28");
			break;
		case 28:
			// Argumentos -> Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano) ] MasArgumentos [ Argumentos.Tipo = Tipo.Tipo x MasArgumentos.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A28-1", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("MasArgumentos", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("A28-0", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Tipo", Tipo.nulo));
			listaParse.add("29");
			break;
		case 29:
			// Argumentos -> lambda [ Argumentos.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A29", Tipo.nulo));
			listaParse.add("30");
			break;
		case 30:
			// MasArgumentos -> , Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano)] MasArgumentos1 [ MasArgumentos0.Tipo = Tipo.Tipo x MasArgumentos1.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A30-1", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("MasArgumentos", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("A30-0", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Tipo", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(",", Tipo.nulo));
			listaParse.add("31");
			break;
		case 31:
			// MasArgumentos -> lambda [ MasArgumentos.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A31", Tipo.nulo));
			listaParse.add("32");
			break;
		case 32:
			// Parametros -> Expresion MasParametros [Parametros.Tipo = Expresion.Tipo x MasParametros.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A32", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("MasParametros", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			listaParse.add("33");
			break;
		case 33:
			// Parametros -> lambda [ Parametros.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A33", Tipo.nulo));
			listaParse.add("34");
			break;
		case 34:
			// MasParametros -> , Expresion MasParametros1 [ MasParametros0.Tipo = Expresion.Tipo x Masparametros1.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A34", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("MasParametros", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(",", Tipo.nulo));
			listaParse.add("35");
			break;
		case 35:
			// MasParametros -> lambda [ MasPArametros.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A35", Tipo.nulo));
			listaParse.add("36");
			break;
		case 36:
			// Expresion -> Relacional Expresion_2	[ if Expresion_2.Tipo == ok
			//      									then Expresion.Tipo = Relacional.Tipo
			//     									else if Relacional.Tipo == booleano && Expresion_2.Tipo == booleano
			//      									then Expresion.Tipo = booleano
			//     									else Expresion.Tipo = error("Tipo Erroneo en Operador &&") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A36", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Relacional", Tipo.nulo));
			listaParse.add("37");
			break;
		case 37:
			// Expresion_2 -> && Relacional Expresion_2	[ if Relacional.Tipo == booleano && (Expresion_21.Tipo == booleano || Expresion_21.Tipo == ok)
			//      										then Expresion_20.Tipo = booleano
			//     										else Expresion_20.Tipo = error("Tipo Erroneo en Operador &&")]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A37", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Relacional", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("&&", Tipo.nulo));
			listaParse.add("38");
			break;
		case 38:
			// Expresion_2 -> lambda [ Expresion_2.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A38", Tipo.nulo));
			listaParse.add("39");
			break;
		case 39:
			// Relacional -> Unario Relacional_2	[ if Relacional_2.Tipo == ok
			//     										then Relacional.Tipo = Unario.Tipo
			//    									else if Unario.Tipo == entero && Relacional_2.Tipo == entero
			//     										then Relacional.Tipo = booleano
			//    									else Relacional.Tipo = error("Tipo Erroneo en Operador >") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A39", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Relacional_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Unario", Tipo.nulo));
			listaParse.add("40");
			break;
		case 40:
			// Relacional_2 -> > Unario Relacional_2	[ if Unario.Tipo == entero && (Relacional_21.Tipo == entero || Relacional_21.Tipo == ok)
			// 												then Relacional_20.Tipo = entero
			//    										else Relacional_20.Tipo = error("Tipo Erroneo en Operador >")]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A40", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Relacional_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Unario", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(">", Tipo.nulo));
			listaParse.add("41");
			break;
		case 41:
			// Relacional_2 -> lambda [ Relacional_2.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A41", Tipo.nulo));
			listaParse.add("42");
			break;
		case 42:
			// Unario -> Valor Unario_2	[ if Unario_2.Tipo == ok
			//   							then Unario.Tipo = Valor.Tipo
			//  						else if Valor.Tipo == entero && Unario_2.Tipo == entero
			//   							then Unario.Tipo = entero
			//  						else Unario.Tipo = error("Tipo Erroneo en Operador -") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A42", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Unario_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Valor", Tipo.nulo));
			listaParse.add("43");
			break;
		case 43:
			// Unario_2 -> - Valor Unario_2	[ if Valor.Tipo == entero && (Unario_21.Tipo == entero || Unario_21.Tipo == ok)
			//									then Unario_20.Tipo = entero
			//								else Unario_20.Tipo = error("Tipo Erroneo en Operador -")]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A43", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Unario_2", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Valor", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("-", Tipo.nulo));
			listaParse.add("44");
			break;
		case 44:
			// Unario_2 -> lambda [ Unario_2.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A44", Tipo.nulo));
			listaParse.add("45");
			break;
		case 45:
			// Valor -> (Expresion) [ Valor.Tipo = Expresion.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A45", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Expresion", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			listaParse.add("46");
			break;
		case 46:
			// Valor -> ++id	[ if buscaTipoTS(id.entrada) == entero
			//						then Valor.Tipo = entero
			//					else Valor.Tipo = error("Tipo Erroneo en Pre-Incrementador") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A46", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("++", Tipo.nulo));
			listaParse.add("47");
			break;
		case 47:
			// Valor -> entero [ Valor.Tipo = entero ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A47", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("entero", Tipo.nulo));
			listaParse.add("48");
			break;
		case 48:
			// Valor -> cadena [ Valor.Tipo = cadena ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A48", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("cadena", Tipo.nulo));
			listaParse.add("49");
			break;
		case 49:
			// Valor -> true [ Valor.Tipo = booleano ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A49", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("true", Tipo.nulo));
			listaParse.add("50");
			break;
		case 50:
			// Valor -> false [ Valor.Tipo = booleano ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A50", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("false", Tipo.nulo));
			listaParse.add("51");
			break;
		case 51:
			// Valor -> id Valor_llamada	[ if buscarTipoParametrosTS(id.entrada) == Valor_llamada.Tipo
			//     								then Valor.Tipo = buscarTipoValorDevueltoTS(id.entrada)
			//    							else Valor.Tipo = error("Tipo Erroneo en Tipos de Parametros") ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A51", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Valor_llamada", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("id", Tipo.nulo));
			listaParse.add("52");
			break;
		case 52:
			// Valor_llamada -> (Parametros) [ Valor_llamada.Tipo = Parametros.Tipo ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A52", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem(")", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("Parametros", Tipo.nulo));
			pila.push(new TuplaPila_ASintSem("(", Tipo.nulo));
			listaParse.add("53");
			break;
		case 53:
			// Valor_llamada -> lambda [ Valor_llamada.Tipo = ok ]
			cima = pila.pop();
			pilaAux.push(cima);
			pila.push(new TuplaPila_ASintSem("A53", Tipo.nulo));
			listaParse.add("54");
			break;
		}
	}

	/**
	 * Dado un id de accion semantica la hace
	 *
	 * @param accion
	 */
	public static void accionSemantica(String accion, ArrayList<String> listaErrores, ArrayList<TablaDeSimbolos> tablaDeSimbolos) {

		TuplaPila_ASintSem elemento;
		int tamano = 0;

		switch (accion) {

		case "A0":
			// ProgramaPrincipal -> Programa	[ if Programa.Tipo == ok
			//           							then ProgramaPrincipal.Tipo = ok
			//          						else ProgramaPrincipal.Tipo = error("Tipo Erroneo en Programa Principal") ]
			if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
				pilaAux.get(pilaAux.size() - 2).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 2).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Programa Principal");
				listaErrores.add("");
			}
			pilaAux.pop();
			break;
		case "A1":
			// Programa -> Decla_FuncionComp Salto Programa	[ if (Decla_FuncionComp.Tipo == ok || Decla_FuncionComp.Tipo == entero)
			// 													&& Programa1.Tipo == ok
			//              									then Programa0.Tipo = ok
			//             									else Programa0.Tipo = error("Tipo Erroneo en Programa") ]
			if (pilaAux.get(pilaAux.size() - 4).getTipo().equals(pilaAux.get(pilaAux.size() - 1).getTipo())) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Programa");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A2":
			// Programa -> Funcion Salto Programa	[ if Funcion.Tipo == ok && Programa1.Tipo == ok
			//            								then Programa0.Tipo = ok
			//           							else Programa0.Tipo = error("Tipo Erroneo en Programa") ]
			if ((pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.ok))
					&& (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Programa");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A3":
			// Programa -> Salto Programa	[ if Programa1.Tipo == ok
			// 									then Programa0.Tipo = ok
			// 								else Programa0.Tipo = error("") ]
			if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Programa");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A4":
			// Programa -> lambda [ Programa.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A5":
			// Salto -> \n
			break;
		case "A6":
			// Decla_FuncionComp -> var Tipo id Con_Sin	[ if Tipo.Tipo == Con_Sin.Tipo || Con_Sin.Tipo == ok
			//             									then 	insertarTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano)
			//														Decla_FuncionComp.Tipo = ok
			//            								else Decla_FuncionComp.Tipo = error("Tipo Erroneo en la Declaracion de una Variable") ]
			if (pilaAux.get(pilaAux.size() - 3).getTipo().equals(pilaAux.get(pilaAux.size() - 1).getTipo())
					|| pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {

				tamano = 0;

				switch (pilaAux.get(pilaAux.size() - 3).getTipo()) {
				case entero:
					tamano = 2;
					break;
				case booleano:
					tamano = 1;
					break;
				case cadena:
					tamano = 8;
					break;
				}

				boolean hecho = tablaDeSimbolos.get(tablaActual).insertarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento(),
						pilaAux.get(pilaAux.size() - 3).getTipo(), tamano);

				if (!hecho) {
					tablaDeSimbolos.get(0).insertarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento(),
							pilaAux.get(pilaAux.size() - 3).getTipo(), tamano);
				}

				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en la Declaracion de una Variable");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A7":
			// Decla_FuncionComp -> if (Expresion) Sentencia	[ if Expresion.Tipo == boolean
			//               										then 	if Sentencia.Tipo != ok
			//                  												then Decla_FuncionComp.Tipo = Sentencia.Tipo
			//                 												else Decla_FuncionComp.Tipo = ok
			//              									else Decla_FuncionComp.Tipo = error("Tipo Erroneo en Condicion de un if") ]
			if (pilaAux.get(pilaAux.size() - 3).getTipo().equals(Tipo.booleano)) {
				if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
					pilaAux.get(pilaAux.size() - 6).setTipo(Tipo.ok);
				} else {
					pilaAux.get(pilaAux.size() - 6).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
				}
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 6).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en la condicion de un if, deberia ser de");
				listaErrores.add("                               tipo 'booleano' y es de tipo '" + pilaAux.get(pilaAux.size() - 3).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A8":
			// Decla_FuncionComp -> do Salto { Salto Cuerpo Salto } while (Expresion)
			//                 				[ if Expresion.Tipo == boolean
			//      							then 	if Cuerpo.Tipo != ok
			//         										then Decla_FuncionComp.Tipo = Cuerpo.Tipo
			//        									else Decla_FuncionComp.Tipo = ok
			//      						else Decla_FuncionComp.Tipo = error("Tipo Erroneo en la Expresion While") ]
			if (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.booleano)) {
				if (pilaAux.get(pilaAux.size() - 8).getTipo().equals(Tipo.ok)) {
					pilaAux.get(pilaAux.size() - 15).setTipo(pilaAux.get(pilaAux.size() - 8).getTipo());
				} else {
					pilaAux.get(pilaAux.size() - 15).setTipo(Tipo.ok);
				}
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 15).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en la Expresion While");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A9":
			// Decla_FuncionComp -> Sentencia [ Decla_FuncionComp.Tipo =  Sentencia.Tipo ]
			pilaAux.get(pilaAux.size() - 2).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			break;
		case "A10":
			// Con_Sin -> = Expresion [ Con_Sin.Tipo = Expresion.Tipo]
			pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A11":
			// Con_Sin -> lambda [ Con_Sin.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A12":
			// Tipo -> int [ Tipo.tipo = entero ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.entero);
			break;
		case "A13":
			// Tipo -> bool [ Tipo.tipo = booleano ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.booleano);
			break;
		case "A14":
			// Tipo -> chars [ Tipo.Tipo = cadena ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.cadena);
			break;
		case "A15":
			// Cuerpo -> Decla_FuncionComp Salto Cuerpo [ if (Decla_FuncionComp.Tipo == bool
			//											|| Decla_FuncionComp.Tipo == chars
			//              							|| Decla_FuncionComp.Tipo == int ) && Cuerpo1.Tipo == vacio
			//             									then Cuerpo0.Tipo = Decla_FuncionComp.Tipo
			//            								else if Decla_FuncionComp.Tipo == ok && Cuerpo1.Tipo == vacio
			//             									then Cuerpo0.Tipo = ok
			//            								else if Decla_FuncionComp.Tipo == ok && Cuerpo1.Tipo == ok
			//             									then Cuerpo0.Tipo = ok
			//            								else if Decla_FuncionComp.Tipo == ok &&
			//											( Cuerpo1.Tipo == bool
			//              							|| Cuerpo1.Tipo == chars
			//											|| Cuerpo1.Tipo == int )
			//             									then Cuerpo0.Tipo = Cuerpo1.Tipo
			//            								else Cuerpo0.Tipo = error("Error en el Cuerpo de una Funcion") ]
			if (((pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.booleano))
					|| (pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.cadena))
					|| (pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.entero)))
					&& (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.vacio))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(pilaAux.get(pilaAux.size() - 4).getTipo());
			} else if ((pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.ok))
					&& (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.vacio))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else if ((pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.ok))
					&& (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else if ((pilaAux.get(pilaAux.size() - 4).getTipo().equals(Tipo.ok))
					&& ((pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))
							|| (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.cadena))
							|| (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.entero)))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Error en el tipo del cuerpo de una funcion");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A16":
			// Cuerpo -> lambda [ Cuerpo.Tipo = vacio ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.vacio);
			break;
		case "A17":
			// Sentencia -> return Return_llamada [ Sentencia.Tipo = Return_llamada.Tipo ]
			pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A18":
			// Sentencia -> write (Expresion)	[ if Expresion.Tipo == entero || Expresion.Tipo == cadena || Expresion.Tipo == booleano
			//           							then Sentencia.Tipo = ok
			//          						else Sentencia.Tipo = error("Tipo Erroneo en Sentencia write") ]
			if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.entero))
					|| (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.cadena))
					|| (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.booleano))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en la llamada write: el tipo de '" + pilaAux.get(pilaAux.size() - 2).getElemento() + "'");
				listaErrores.add("                               no es de tipo 'booleano', 'cadena' o 'entero'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A19":
			// Sentencia -> prompt(id)	[ if buscarTipoTS(id.entrada) == entero || buscarTipoTS(id.entrada) == cadena
			//         						then Sentencia.Tipo = ok
			//        					else Sentencia.Tipo = error("Tipo Erroneo en Sentencia prompt") ]
			if ((tablaDeSimbolos.get(tablaActual).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(Tipo.entero))
					|| (tablaDeSimbolos.get(tablaActual).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(Tipo.cadena))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else if ((tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(Tipo.entero))
					|| (tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(Tipo.cadena))) {
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 5).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en la llamada prompt: el tipo de '" + pilaAux.get(pilaAux.size() - 2).getElemento() + "'");
				listaErrores.add("                               no es de tipo 'cadena' o 'entero'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A20":
			// Sentencia -> id Sentencia_llamada	[ if Sentencia_llamada == ok 
			//											then	if argumentosIguales(id.entrada, listaParametros)
			//														then Sentencia.Tipo = ok
			//													else 
			//										else if buscarTipoTS(id.entrada) == Sentencia_llamada.Tipo
			// 											then Sentencia.Tipo = ok
			// 										else Sentencia.Tipo = error("Tipo Erroneo en Sentencia de Asignacion") ]
			if(pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)){

				if(tablaDeSimbolos.get(0).argumentosIguales(pilaAux.get(pilaAux.size() - 2).getElemento(), listaParametros, listaErrores, lineaError)){
					pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.ok);
					listaParametros.clear();
				}
				else{
					error = true;
					pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
					listaParametros.clear();
				}
			}
			else if (tablaDeSimbolos.get(tablaActual).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(pilaAux.get(pilaAux.size() - 1).getTipo())) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.ok);
			} else if (tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(pilaAux.get(pilaAux.size() - 1).getTipo())) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Sentencia de Asignacion");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A21":
			// Return_llamada -> Expresion [ Return_llamada.Tipo = Expresion.Tipo ]
			pilaAux.get(pilaAux.size() - 2).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			break;
		case "A22":
			// Return_llamada -> lambda [ Return_llamada.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A23":
			// Sentencia_llamada -> (Parametros) [ Sentencia_llamada.Tipo = Parametros.Tipo ]
			pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.ok);
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A24":
			// Sentencia_llamada -> = Expresion [ Sentencia_llamada.Tipo = Expresion.Tipo ]
			pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A25-0":
			// Funcion -> function TipoFuncion id [ insertarTipoValorDevueltoTS(id.entrada, TipoFuncion.Tipo) ]
			tablaDeSimbolos.get(0).insertarTipoValorDevueltoTS(pilaAux.get(pilaAux.size() - 1).getElemento(), pilaAux.get(pilaAux.size() - 2).getTipo());
			break;
		case "A25-1":
			// Funcion -> function TipoFuncion id [ insertarTipoValorDevueltoTS(id.entrada, TipoFuncion.Tipo) ] (Argumentos)
			// [ insertarTipoParametrosTS(id.entrada, Argumentos.Tipo) ]
			break;
		case "A25-2":
			// Funcion -> function TipoFuncion id [ insertarTipoValorDevueltoTS(id.entrada, TipoFuncion.Tipo) ] (Argumentos)
			// [ insertarTipoParametrosTS(id.entrada, Argumentos.Tipo) ] Salto { Salto Cuerpo }
			// 	[ if Cuerpo.Tipo == TipoFuncion.Tipo
			// 		then Funcion.Tipo = ok
			//  else Funcion.Tipo = error("Error entre el Tipo del Cuerpo y el Tipo de la Funcion") ]
			if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(pilaAux.get(pilaAux.size() - 12).getTipo()))) {
				pilaAux.get(pilaAux.size() - 14).setTipo(Tipo.ok);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 14).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Error en la funcion '" + pilaAux.get(pilaAux.size() - 11).getElemento() + "': el tipo "
						+ "devuelto es: '" + pilaAux.get(pilaAux.size() - 12).getTipo() + "'");
				listaErrores.add("                               y el tipo recibido por el cuerpo de la funcion es '" 
						+ pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A26":
			// TipoFuncion -> Tipo [ TipoFuncion.Tipo = Tipo.Tipo ]
			pilaAux.get(pilaAux.size() - 2).setTipo(pilaAux.get(pilaAux.size() - 1).getTipo());
			pilaAux.pop();
			break;
		case "A27":
			// TipoFuncion -> lambda [ TipoFuncion.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A28-0":
			// Argumentos -> Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano) ]
			tamano = 0;

			switch (pilaAux.get(pilaAux.size() - 2).getTipo()) {
			case entero:
				tamano = 2;
				break;
			case booleano:
				tamano = 1;
				break;
			case cadena:
				tamano = 8;
				break;
			}

			tablaDeSimbolos.get(tablaActual).insertarTipoTS(pilaAux.get(pilaAux.size() - 1).getElemento(), pilaAux.get(pilaAux.size() - 2).getTipo(), tamano);
			listaArgumentos.add(pilaAux.get(pilaAux.size() - 2).getTipo());
			break;
		case "A28-1":
			// Argumentos -> Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano) ] MasArgumentos
			//	[ Argumentos.Tipo = Tipo.Tipo x MasArgumentos.Tipo ]
			tablaDeSimbolos.get(0).insertarParametrosTS(pilaAux.get(pilaAux.size() - 6).getElemento(), listaArgumentos);
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			listaArgumentos.clear();
			break;
		case "A29":
			// Argumentos -> lambda [ Argumentos.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A30-0":
			// MasArgumentos -> , Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano)]
			tamano = 0;

			switch (pilaAux.get(pilaAux.size() - 2).getTipo()) {
			case entero:
				tamano = 2;
				break;
			case booleano:
				tamano = 1;
				break;
			case cadena:
				tamano = 8;
				break;
			}

			tablaDeSimbolos.get(tablaActual).insertarTipoTS(pilaAux.get(pilaAux.size() - 1).getElemento(), pilaAux.get(pilaAux.size() - 2).getTipo(), tamano);
			listaArgumentos.add(pilaAux.get(pilaAux.size() - 2).getTipo());
			break;
		case "A30-1":
			// MasArgumentos -> , Tipo id [ insertaTipoTS(id.entrada, Tipo.Tipo, Tipo.tamano)] MasArgumentos1
			// [ MasArgumentos0.Tipo = Tipo.Tipo x MasArgumentos1.Tipo ]
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A31":
			// MasArgumentos -> lambda [ MasArgumentos.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A32":
			// Parametros -> Expresion MasParametros [Parametros.Tipo = Expresion.Tipo x MasParametros.Tipo ]
			listaParametros.add(pilaAux.get(pilaAux.size() - 2).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A33":
			// Parametros -> lambda [ Parametros.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A34":
			// MasParametros -> , Expresion MasParametros1 [ MasParametros0.Tipo = Expresion.Tipo x Masparametros1.Tipo ]
			listaParametros.add(pilaAux.get(pilaAux.size() - 2).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A35":
			// MasParametros -> lambda [ MasPArametros.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A36":
			// Expresion -> Relacional Expresion_2	[ if Expresion_2.Tipo == ok
			//               							then Expresion.Tipo = Relacional.Tipo
			//              						else if Relacional.Tipo == booleano && Expresion_2.Tipo == booleano
			//               							then Expresion.Tipo = booleano
			//             	 						else Expresion.Tipo = error("Tipo Erroneo en Operador &&") ]
			if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 2).getTipo());
			} else if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.booleano))
					&& pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.booleano)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.booleano);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '&&', se esperaba un tipo 'booleano'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A37":
			// Expresion_2 -> && Relacional Expresion_2	[ if Relacional.Tipo == booleano
			//											&& (Expresion_21.Tipo == booleano || Expresion_21.Tipo == ok)
			//               								then Expresion_20.Tipo = booleano
			//               							else Expresion_20.Tipo = error("Tipo Erroneo en Operador &&")]
			if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.booleano))
					&& ((pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.booleano))
							|| pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))) {
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.booleano);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '&&', se esperaba un tipo 'booleano'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A38":
			// Expresion_2 -> lambda [ Expresion_2.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A39":
			// Relacional -> Unario Relacional_2	[ if Relacional_2.Tipo == ok
			//               							then Relacional.Tipo = Unario.Tipo
			//             							else if Unario.Tipo == entero && Relacional_2.Tipo == entero
			//              				 			then Relacional.Tipo = booleano
			//             							else Relacional.Tipo = error("Tipo Erroneo en Operador >") ]
			if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 2).getTipo());
			} else if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.entero)) && (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.entero))) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.booleano);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '>', se esperaba un tipo 'booleano'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A40":
			// Relacional_2 -> > Unario Relacional_2	[ if Unario.Tipo == entero && (Relacional_21.Tipo == entero || Relacional_21.Tipo == ok)
			// 												then Relacional_20.Tipo = entero
			//    										else Relacional_20.Tipo = error("Tipo Erroneo en Operador >")]
			if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(pilaAux.get(pilaAux.size() - 1).getTipo()))
					|| (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.entero)) && (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))) {
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.entero);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '>', se esperaba un tipo 'booleano'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A41":
			// Relacional_2 -> lambda [ Relacional_2.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A42":
			// Unario -> Valor Unario_2	[ if Unario_2.Tipo == ok
			//          					then Unario.Tipo = Valor.Tipo
			//        					else if Valor.Tipo == entero && Unario_2.Tipo == entero
			//          					then Unario.Tipo = entero
			//       					else Unario.Tipo = error("Tipo Erroneo en Operador -") ]
			if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(pilaAux.get(pilaAux.size() - 2).getTipo());
			} else if (pilaAux.get(pilaAux.size() - 2).getTipo().equals(pilaAux.get(pilaAux.size() - 1).getTipo())) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.entero);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '-', se esperaba un tipo 'entero'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A43":
			// Unario_2 -> - Valor Unario_2	[ if Valor.Tipo == entero && (Unario_21.Tipo == entero || Unario_21.Tipo == ok)
			//									then Unario_20.Tipo = entero
			//								else Unario_20.Tipo = error("Tipo Erroneo en Operador -")]
			if ((pilaAux.get(pilaAux.size() - 2).getTipo().equals(pilaAux.get(pilaAux.size() - 1)))
					|| (pilaAux.get(pilaAux.size() - 2).getTipo().equals(Tipo.entero) && pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok))) {
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.entero);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 4).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en el operador '-', se esperaba un tipo 'entero'");
				listaErrores.add("                               y el tipo actual es '" + pilaAux.get(pilaAux.size() - 2).getTipo() + "'");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A44":
			// Unario_2 -> lambda [ Unario_2.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		case "A45":
			// Valor -> (Expresion) [ Valor.Tipo = Expresion.Tipo ]
			pilaAux.get(pilaAux.size() - 4).setTipo(pilaAux.get(pilaAux.size() - 2).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A46":
			// Valor -> ++id 	[ if buscaTipoTS(id.entrada) == entero
			//      				then Valor.Tipo = entero
			//     				else Valor.Tipo = error("Tipo Erroneo en Pre-Incrementador") ]
			if (tablaDeSimbolos.get(tablaActual).buscarTipoTS(pilaAux.get(pilaAux.size() - 1).getElemento()).equals(Tipo.entero)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.entero);
			} else if (tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 1).getElemento()).equals(Tipo.entero)) {
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.entero);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo erroneo en la operacion Pre-Incrementador, el tipo que se esperaba");
				listaErrores.add("                               es 'entero' y el tipo de '" + pilaAux.get(pilaAux.size() - 1).getElemento() + "' no coincide");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A47":
			// Valor -> entero [ Valor.Tipo = entero ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.entero);
			break;
		case "A48":
			// Valor -> cadena [ Valor.Tipo = cadena ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.cadena);
			break;
		case "A49":
			// Valor -> true [ Valor.Tipo = booleano ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.booleano);
			break;
		case "A50":
			// Valor -> false [ Valor.Tipo = booleano ]
			pilaAux.pop();
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.booleano);
			break;
		case "A51": // Falta
			//Valor -> id Valor_llamada	[ if buscarTipoParametrosTS(id.entrada) == Valor_llamada.Tipo
			//             					then Valor.Tipo = buscarTipoValorDevueltoTS(id.entrada)
			//							else if Valor_llamada.Tipo == ok
			//								then Valor.Tipo = buscarTipoTS(id.entrada)
			//           				else Valor.Tipo = error("Tipo Erroneo en Tipos de Parametros") ]
			if (tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento()).equals(Tipo.function)) {
				if (tablaDeSimbolos.get(0).argumentosIguales(pilaAux.get(pilaAux.size() - 2).getElemento(), listaParametros, listaErrores, lineaError)) {
					pilaAux.get(pilaAux.size() - 3).setTipo(tablaDeSimbolos.get(0).buscarTipoValorDevueltoTS(pilaAux.get(pilaAux.size() - 2).getElemento()));
				}
			}
			else if (pilaAux.get(pilaAux.size() - 1).getTipo().equals(Tipo.ok)) {

				Tipo tipo = tablaDeSimbolos.get(tablaActual).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento());

				if (Tipo.nulo.equals(tipo)) {
					tipo = tablaDeSimbolos.get(0).buscarTipoTS(pilaAux.get(pilaAux.size() - 2).getElemento());
				}

				pilaAux.get(pilaAux.size() - 3).setTipo(tipo);
			} else {
				error = true;
				pilaAux.get(pilaAux.size() - 3).setTipo(Tipo.error);
				listaErrores.add("Error en la linea: " + lineaError);
				listaErrores.add("Error en el Analizador SinSem: Tipo Erroneo en Tipos de Parametros");
				listaErrores.add("");
			}
			pilaAux.pop();
			pilaAux.pop();
			listaParametros.clear();
			break;
		case "A52":
			// Valor_llamada -> (Parametros) [ Valor_llamada.Tipo = Parametros.Tipo ]
			pilaAux.get(pilaAux.size() - 4).setTipo(pilaAux.get(pilaAux.size() - 2).getTipo());
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;
		case "A53":
			// Valor_llamada -> lambda [ Valor_llamada.Tipo = ok ]
			pilaAux.get(pilaAux.size() - 1).setTipo(Tipo.ok);
			break;
		}

	}

	/**
	 * Dado un elemento comprueba si es un elemento NO terminal
	 *
	 * @param elemento
	 * @return
	 */
	public static boolean esNoTerminal(String elemento) {

		boolean es = false;
		String noTerminales[] = {"ProgramaPrincipal", "Programa", "Salto", "Decla_FuncionComp", "Con_Sin",
				"Tipo", "Cuerpo", "Sentencia", "Return_llamada", "Sentencia_llamada", "Funcion",
				"TipoFuncion", "Argumentos", "MasArgumentos", "Parametros", "MasParametros", "Expresion",
				"Expresion_2", "Relacional", "Relacional_2", "Unario", "Unario_2", "Valor", "Valor_llamada"};

		for (int i = 0; i < noTerminales.length && !es; i++) {
			if (elemento.equals(noTerminales[i])) es = true;
		}

		return es;
	}

	/**
	 * Dado un elemento comprueba si es un elemento terminal
	 *
	 * @param elemento
	 * @return
	 */
	public static boolean esTerminal(String elemento) {

		boolean es = false;
		String terminales[] = {"\\n", "var", "id", "if", "(", ")", "do", "{", "}", "while", "=", "int", "bool", "chars",
				"return", "write", "prompt", "function", "&&", ",", ">", "-", "++", "entero", "cadena", "true", "false"};

		for (int i = 0; i < terminales.length && !es; i++) {
			if (elemento.equals(terminales[i])) es = true;
		}

		return es;
	}

	/**
	 * Dado un elemento comprueba si es una accion
	 *
	 * @param elemento
	 * @return
	 */
	public static boolean esAccion(String elemento) {

		boolean es = false;
		String acciones[] = {"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13",
				"A14", "A15", "A16", "A17", "A18", "A19", "A20", "A21", "A22", "A23", "A24", "A25-0", "A25-1", "A25-2",
				"A26", "A27", "A28-0", "A28-1", "A29", "A30-0", "A30-1", "A31", "A32", "A33", "A34", "A35", "A36", "A37",
				"A38", "A39", "A40", "A41", "A42", "A43", "A44", "A45", "A46", "A47", "A48", "A49", "A50", "A51", "A52", "A53"};

		for (int i = 0; i < acciones.length && !es; i++) {
			if (elemento.equals(acciones[i])) es = true;
		}

		return es;
	}

	/**
	 * Imprime el contenido de la pila
	 */
	public static void imprimirPila() {

		for (int i = pila.size() - 1; i >= 0; i--) {
			System.out.println(pila.elementAt(i).getElemento() + ", " + pila.elementAt(i).getTipo());
		}
	}

	/**
	 * Imprime el contenido de la pilaAux
	 */
	public static void imprimirPilaAux() {

		for (int i = pilaAux.size() - 1; i >= 0; i--) {
			System.out.println(pilaAux.elementAt(i).getElemento() + ", " + pilaAux.elementAt(i).getTipo());
		}
	}

}
