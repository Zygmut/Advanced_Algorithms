# Práctica 2: Backtracking

Backtracking. Exercici: Una variant del recorregut d'una peça d'escacs per un tauler de NxN.

Es tracta de realitzar un programa que tingui definides 6 peces (o més; cavall, reina i torre més tres o més peces inventades pel programador), i que sigui capaç de realitzar un recorregut de casella única per un tauler d'escacs de nxn caselles. Un recorregut de casella única del tauler és aquell a on la peça visita totes les caselles, passant una sola vegada per cada una d'elles. Si no es possible, el programa ha de'informar del cas. Aquí però complicarem un poc el problema, de forma que el recorregut podrà ser compartit per una, dues, tres o quatre peces. Que començaran cada una a un cantó del tauler, començant pel canto superior esquerra i amb ordre horari. Per exemple si hi ha tres peces, la primera al cató superior esquerra, la segona al superior dretà i la tercera a l'inferior dretà.

Opcional a lliurament ordinari: Marcar el punt d'inici de cada peça. Generalitzar-lo a n peces. Realitzar estudis de comportament de l'algorisme quan variem els paràmetres. I més ...

Obligatori a la recuperació: Marcar el punt d'inici de cada peça. Generalitzar-lo a n peces..

El programa elaborat ha de presentar una estructura de disseny MVC.

A més cal que tingui les peces dissenyades en una jerarquia objecte
(Peça exten --> {cavall, torre, …})
El backtracking maneja peça
D'aquesta forma es fa molt simple afegir noves peces.

El programa ha de presentar una IGU gràfica.

Cal lliurar també una memòria amb les mateixes normes que las del primer exercici del curs.

## Estrucutra de carpetas

Esta práctica se ha estructurado de la siguiente manera:

- `assets`: Conjunto de imagenes y configuración adicional.
- `docs`: Documentación del proyecto. Generalmente guardrá el código fuente (LaTeX) y su respectivo PDF compilado.
- `lib`: Dependencias externas.
- `src`: Código fuente.
- `bin`: Archivos de salida compilados.
