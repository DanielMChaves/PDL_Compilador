package datos;

public class Matriz_ASintSem {

	/**
	 * Dado un elemento no terminal te da la posicion que ocupa en la matriz
	 * @param nt
	 * @return posicion en la matriz
	 */
	public static int noTerminalPos(String nt) {

		int pos = -1;

		switch (nt) {
		case "ProgramaPrincipal":
			pos = 0;
			break;
		case "Programa":
			pos = 1;
			break;
		case "Salto":
			pos = 2;
			break;
		case "Decla_FuncionComp":
			pos = 3;
			break;
		case "Con_Sin":
			pos = 4;
			break;
		case "Tipo":
			pos = 5;
			break;
		case "Cuerpo":
			pos = 6;
			break;
		case "Sentencia":
			pos = 7;
			break;
		case "Return_llamada":
			pos = 8;
			break;
		case "Sentencia_llamada":
			pos = 9;
			break;
		case "Funcion":
			pos = 10;
			break;
		case "TipoFuncion":
			pos = 11;
			break;
		case "Argumentos":
			pos = 12;
			break;
		case "MasArgumentos":
			pos = 13;
			break;
		case "Parametros":
			pos = 14;
			break;
		case "MasParametros":
			pos = 15;
			break;
		case "Expresion":
			pos = 16;
			break;
		case "Expresion_2":
			pos = 17;
			break;
		case "Relacional":
			pos = 18;
			break;
		case "Relacional_2":
			pos = 19;
			break;
		case "Unario":
			pos = 20;
			break;
		case "Unario_2":
			pos = 21;
			break;
		case "Valor":
			pos = 22;
			break;
		case "Valor_llamada":
			pos = 23;
			break;
		}

		return pos;
	}

	/**
	 * Dado un elemento  terminal te da la posicion que ocupa en la matriz
	 * @param t
	 * @return
	 */
	public static int terminalPos(String t){

		int pos;

		for (char c: t.toCharArray()) {
			if (!Character.isDigit(c)) {
				pos = -1;
			}
			else{
				return 23;
			}
		}

		switch(t){
		case "\\n":
			pos = 0;
			break;
		case "var":
			pos = 1;
			break;
		case "id":
			pos = 2;
			break;
		case "if":
			pos = 3;
			break;
		case "(":
			pos = 4;
			break;
		case ")":
			pos = 5;
			break;
		case "do":
			pos = 6;
			break;
		case "{":
			pos = 7;
			break;
		case "}":
			pos = 8;
			break;
		case "while":
			pos = 9;
			break;
		case "=":
			pos = 10;
			break;
		case "int":
			pos = 11;
			break;
		case "bool":
			pos = 12;
			break;
		case "chars":
			pos = 13;
			break;
		case "return":
			pos = 14;
			break;
		case "write":
			pos = 15;
			break;
		case "prompt":
			pos = 16;
			break;
		case "function":
			pos = 17;
			break;
		case ",":
			pos = 18;
			break;
		case "&&":
			pos = 19;
			break;
		case ">":
			pos = 20;
			break;
		case "-":
			pos = 21;
			break;
		case "++":
			pos = 22;
			break;
		case "entero":
			pos = 23;
			break;
		case "cadena":
			pos = 24;
			break;
		case "true":
			pos = 25;
			break;
		case "false":
			pos = 26;
			break;
		case "$":
			pos = 27;
			break;
		default:
			pos = 2;
			break;
		}

		return pos;
	}

	/**
	 * matriz[noTerminal][terminal]
	 */
	public static int matrizSint[][] = {
			{0, 0, 0, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 1, 1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4},
			{5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, 6, 9, 7, -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, 9, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{11, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 13, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{16, 15, 15, 15, -1, -1, 15, -1, 16, -1, -1, -1, -1, -1, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, 18, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{22, -1, 21, -1, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 21, 21, 21, 21, 21, -1},
			{-1, -1, -1, -1, 23, -1, -1, -1, -1, -1, 24, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 27, -1, -1, -1, -1, -1, -1, -1, -1, 26, 26, 26, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, 28, 28, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 32, -1, 32, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, 32, 32, 32, 32, -1},
			{-1, -1, -1, -1, -1, 35, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 36, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 36, 36, 36, 36, -1},
			{38, -1, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 38, 37, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 39, -1, 39, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 39, 39, 39, 39, 39, -1},
			{41, -1, -1, -1, -1, 41, -1, -1, -1, -1, 41, -1, -1, -1, -1, -1, -1, -1, 41, 41, 40, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, 42, -1, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 42, 42, 42, 42, 42, -1},
			{44, -1, -1, -1, -1, 44, -1, -1, -1, -1, 44, -1, -1, -1, -1, -1, -1, -1, 44, 44, 44, 43, -1, -1, -1, -1, -1, -1},
			{-1, -1, 51, -1, 45, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, 47, 48, 49, 50, -1},
			{53, -1, -1, -1, 52, 53, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, -1, -1, 53, 53, 53, 53, -1, -1, -1, -1, -1, -1}
	};

}
