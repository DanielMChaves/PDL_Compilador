package datos;

public class Matriz_ALexico {

	/**
	 * Dado un estado del automata te da la posicion que ocupa en la matriz
	 * @param estado
	 * @return
	 */
	public static int estadoPos(int estado){
		int pos = -1;

		switch(estado){

		case 0:
			pos = 0;
			break;
		case 7:
			pos = 1;
			break;
		case 12:
			pos = 2;
			break;
		case 14:
			pos = 3;
			break;
		case 16:
			pos = 4;
			break;
		case 17:
			pos = 5;
			break;
		case 18:
			pos = 6;
			break;
		case 20:
			pos = 7;
			break;
		case 22:
			pos = 8;
			break;
		case 24:
			pos = 9;
			break;
		}
		return pos;
	}

	/**
	 * Dado un simbolo te da la posicion que ocupa en la matriz
	 * @param simbolo
	 * @return
	 */
	public static int simboloPos(String simbolo){
		int pos = -1;

		switch(simbolo){

		case " ":
		case "	":
			pos = 0;
			break;
		case "(":
			pos = 1;
			break;
		case ")":
			pos = 2;
			break;
		case "{":
			pos = 3;
			break;
		case "}":
			pos = 4;
			break;
		case "-":
			pos = 5;
			break;
		case ">":
			pos = 6;
			break;
		case "&":
			pos = 7;
			break;
		case "=":
			pos = 8;
			break;
		case "\n":
			pos = 9;
			break;
		case "eof":
			pos = 10;
			break;
		case "/":
			pos = 13;
			break;
		case "*":
			pos = 14;
			break;
		case "\"":
			pos = 15;
			break;
		case "+":
			pos = 16;
			break;
		case ",":
			pos = 17;
			break;
		default:
			char c = simbolo.charAt(0);
			if('0' <= c && c <= '9'){
				pos = 11;
			}
			else if( ('a' <= c && c <= 'z' ) || ('A' <= c && c <= 'Z' ) 
					|| c == 'ñ' || c == 'Ñ'){
				pos = 12;
			}
			else{
				pos = -1;
			}
			break;
		}
		return pos;
	}

	/**
	 * matriz[estado][simbolo]
	 */
	public static TuplaMatriz_ALexico matrizLex[][] = {
			//Primera Fila - Estado 0
			{
				new TuplaMatriz_ALexico(0, "A"), new TuplaMatriz_ALexico(1, "B"), new TuplaMatriz_ALexico(2, "C"), 
				new TuplaMatriz_ALexico(3, "D"), new TuplaMatriz_ALexico(4, "E"), new TuplaMatriz_ALexico(5, "F"),
				new TuplaMatriz_ALexico(6, "G"), new TuplaMatriz_ALexico(7, "H"), new TuplaMatriz_ALexico(9, "J"),
				new TuplaMatriz_ALexico(10, "K"), new TuplaMatriz_ALexico(11, "L"), new TuplaMatriz_ALexico(12, "M"),
				new TuplaMatriz_ALexico(14, "O"), new TuplaMatriz_ALexico(16, "R"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(22, "AA"), new TuplaMatriz_ALexico(24, "AC")
			},
			//Segunda Fila - Estado 7
			{
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(8, "I"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR")
			},
			//Tercera Fila - Estado 12
			{
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"),
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"),
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"),
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(12, "N"),
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"),
				new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ"), new TuplaMatriz_ALexico(13, "Ñ")
			},
			//Cuarta Fila - Estado 14
			{
				new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), 
				new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), 
				new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), 
				new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(14, "P"),
				new TuplaMatriz_ALexico(14, "P"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), 
				new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q"), new TuplaMatriz_ALexico(15, "Q")
			},
			//Quinta Fila - Estado 16
			{
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(17, "S"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR")
			},
			// Sexta Fila - Estado 17
			{
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), 
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), 
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), 
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), 
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(18, "U"), 
				new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T"), new TuplaMatriz_ALexico(17, "T")
			},
			// Septima Fila - Esatdo 18
			{
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), 
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), 
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"),
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"),
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(19, "W"), new TuplaMatriz_ALexico(17, "V"), 
				new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V"), new TuplaMatriz_ALexico(17, "V") 
			},
			// Octava Fila - Estado 20
			{
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), 
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), 
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), 
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), 
				new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y"), 
				new TuplaMatriz_ALexico(21, "Z"), new TuplaMatriz_ALexico(20, "Y"), new TuplaMatriz_ALexico(20, "Y")
			},
			// Novena Fila - Estado 22
			{
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(0, "ERROR"),
				new TuplaMatriz_ALexico(0, "ERROR"), new TuplaMatriz_ALexico(23, "AB"), new TuplaMatriz_ALexico(0, "ERROR")
			}
	};

}
