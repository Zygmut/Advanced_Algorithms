# Práctica 7: Primalidad y encriptación

Es tracta de desenvolupar un treball com els anteriors amb una memòria en condicions semblants a les pràctiques anteriors. Un vídeo semblant als de les pràctiques anteriors explicant aquest treball. Y el codi del projecte. La pràctica ha de presentar un esquema seguint el MVC com la resta de treballs del curs.

En aquest cas es tracta de desenvolupar una pràctica que analitzi el cost asimptòtic de factoritzar grans nombres emprant les tècniques i gràfiques vists en aquest curs. La pràctica ha de presentar una IGU. el que s'ha de presentar com a obligatori és el següent:

- Aplicació amb GUI capaç de dir si un nombre de N xifres és primer o no.
- L'aplicació també ha de ser capaç en la mesura de lo possible de factoritzar un nombre fort ("número duro"). En cas de no tenir temps de factoritzar-lo ha de preveure més o manco el temps que tardaria en fer-ho.
- La GUI ha de presentar una opció per donar la mesura a petició de l'usuari del valor de la llibreria subministrada pel professor "mesurament".
- L'aplicació ha de ser capaç de mostrar una gràfica aproximada de l'evolució del cost asimptòtic de la factorització.

La valoració d'aquests apartats obligatoris serà sobre 6 punts. Lliurar-ho tot no implica tenir un 6, dependrà de la qualitat del material presentat pels estudiants.

Apartats voluntaris (4 punts restants):

- (Obligatori a la recuperació): Generació de claus públiques i privades RSA de fins a 600 xifres.
- Encriptació per RSA: Poder encriptar i desencriptar fitxers amb la tècnica vista a classe.
- Dotar de parel·lelisme part dels càlculs. Tècniques pròpies per amagar més l'encriptació.
- Tècniques de compactació dels fitxers encriptats.
- Altres idees que puguin implementar els estudiants.

## Estrucutra de carpetas

Esta práctica se ha estructurado de la siguiente manera:

- `assets`: Conjunto de imagenes y configuración adicional.
- `docs`: Documentación del proyecto. Generalmente guardrá el código fuente (LaTeX) y su respectivo PDF compilado.
- `lib`: Dependencias externas.
- `src`: Código fuente.
- `tools`: Herramientas adicionales como scripts o estudios.
- `bin`: Archivos de salida compilados.
