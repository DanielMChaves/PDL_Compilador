package compilador;

import java.io.*;
import java.util.*;
import datos.*;

public class Compilador {

	public static void main(String[] args) {

		System.out.println("Empezamos el Compilador\n");
		
		String rutaCodigo = "/Users/dmelero/Google Drive/Universidad/Asignaturas/"
				+ "5º Semestre/WorkSpace_Java_5/PDL/src/ficheros/codigo.js";
		String rutaTokens = "/Users/dmelero/Google Drive/Universidad/Asignaturas/"
				+ "5º Semestre/WorkSpace_Java_5/PDL/src/ficheros/tokens.txt";
		String rutaTablaDeSimbolos = "/Users/dmelero/Google Drive/Universidad/Asignaturas/"
				+ "5º Semestre/WorkSpace_Java_5/PDL/src/ficheros/tablaDeSimbolos.txt";
		String rutaParse = "/Users/dmelero/Google Drive/Universidad/Asignaturas/"
				+ "5º Semestre/WorkSpace_Java_5/PDL/src/ficheros/parse.txt";
		String rutaErrores = "/Users/dmelero/Google Drive/Universidad/Asignaturas/"
				+ "5º Semestre/WorkSpace_Java_5/PDL/src/ficheros/errores.txt";

		FileWriter fwErrores = null;
		PrintWriter pwErrores = null;
		
		// Estructuras de Datos
		System.out.println("Creacion de la Lista de Tokens y Errores\n");

		// Lista de Tokens, Errores y Parse
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		ArrayList<String> listaErrores = new ArrayList<String>();

		ArrayList<String> listaParse = new ArrayList<String>();
		listaParse.add("Descendente");

		// Creacion de todas las tablas necesarias
		System.out.println("Creacion de Todas las Tablas Necesarias\n");

		TablaDePR TSReservada = new TablaDePR();

		ArrayList<TablaDeSimbolos> tablaDeSimbolos = new ArrayList<TablaDeSimbolos>();
		tablaDeSimbolos.add(new TablaDeSimbolos()); // tablaDeSimbolos.get(0)

		// Analizador Lexico
		System.out.println("Ejecucion del Analizador Lexico\n");

		AnalizadorLexico.analizadorLexico(rutaCodigo, rutaTokens, listaErrores, listaTokens, 
				tablaDeSimbolos, TSReservada);

		if(!listaErrores.isEmpty()){
			System.out.println("Se ha producido un Error durante la Compilacion\n");
			
			for(int i = 0; i < listaErrores.size(); i++){
				System.out.println(listaErrores.get(i));
			}
			
			// Copiando los Errores en el fichero correspondiente

			try {
				fwErrores = new FileWriter(rutaErrores);
				pwErrores = new PrintWriter(fwErrores);

				for(int i = 0; i < listaErrores.size(); i++){
					pwErrores.println(listaErrores.get(i));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (null != fwErrores)
						fwErrores.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			return;
		}

		// Analizador Sintactico y Semantico
		System.out.println("Ejecucion del Analizador Sintactico y Semantico\n");

		AnalizadorSintSem.analizadorSintSem(rutaParse, listaErrores, listaTokens, listaParse, tablaDeSimbolos);

		if(!listaErrores.isEmpty()){
			System.out.println("Se ha producido un Error durante la Compilacion\n");
			
			for(int i = 0; i < listaErrores.size(); i++){
				System.out.println(listaErrores.get(i));
			}
			
			// Copiando los Errores en el fichero correspondiente

			try {
				fwErrores = new FileWriter(rutaErrores);
				pwErrores = new PrintWriter(fwErrores);

				for(int i = 0; i < listaErrores.size(); i++){
					pwErrores.println(listaErrores.get(i));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (null != fwErrores)
						fwErrores.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			
			return;
		}

		// Copiando el Parse en el fichero correspondiente
		System.out.println("Copiando el Parse\n");

		FileWriter fwParse = null;
		PrintWriter pwParse = null;

		try {
			fwParse = new FileWriter(rutaParse);
			pwParse = new PrintWriter(fwParse);

			for(int i = 0; i < listaParse.size(); i++){
				pwParse.print(listaParse.get(i) + " ");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (null != fwParse)
					fwParse.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		// Copiando las tablas de Simbolos en el fichero correspondiente
		System.out.println("Copiando las Tablas de Simbolos\n");

		FileWriter fwTabla = null;
		PrintWriter pwTabla = null;

		try {
			fwTabla = new FileWriter(rutaTablaDeSimbolos);
			pwTabla = new PrintWriter(fwTabla);

			//String resultado = "CONTENIDO DE LA TABLA " + nombre + " #" + id + ":\n\n";
			//pwTabla.println(TSReservada.imprimirTS("DE PALABRAS RESERVADAS", 0));
			tablaDeSimbolos.get(0).colocarDesplazamiento();
			pwTabla.println(tablaDeSimbolos.get(0).imprimirTS("GLOBAL", 1));
			
			ArrayList<String> nombres = new ArrayList<String>();

			nombres = tablaDeSimbolos.get(0).obtenerNombreLocal(nombres);

			for(int i = 1; i < tablaDeSimbolos.size(); i++){
				tablaDeSimbolos.get(i).colocarDesplazamiento();
				pwTabla.println(tablaDeSimbolos.get(i).imprimirTS(nombres.get(i - 1), i + 1));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (null != fwTabla)
					fwTabla.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		// Destruimos todas las tablas Creadas
		System.out.println("Destrucción de Todas las Tablas Creadas\n");
		TSReservada.destruirTS(TSReservada);

		for(int i = 0; i < tablaDeSimbolos.size(); i++){
			tablaDeSimbolos.get(i).destruirTS(tablaDeSimbolos.get(i));
		}

		System.out.println("Fin de la Compilacion");

	}

}
