/* Ejemplo correcto numero 5 -> Funciones y llamadas a funciones */

var int A = 20
var int B = 15
var chars C = "HOLA-C"
var bool D = true

function int restaDoble (int a, int b)
{
	return a - b
}

var int E = 10
var int F = 5
var chars G = "HOLA-G"
var bool H = false

function int restaTriple (int a, int b, int c)
{
	return a - (b - c)
}

restaDoble(A,B)
A = restaTriple(B,E,F)
