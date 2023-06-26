# Práctica 5: Programación dinámica

Realitzar una aplicació amb interfície gràfica d'usuari i seguint el patró de disseny MVC que permeti l'anàlisi i la comparació d'un conjunt d'idiomes. Es tracta d'esbrinar com s'assemblen els idiomes entre ells. El programa ha de funcionar amb almenys 10 idiomes (si no voleu complicar-vos preferentment europeus).
Es tracta d'implementar un programa, basat en l'algorisme dinàmic de Levenhstein, que sigui capaç d'esbrinar a quina distància es troben  dos idiomes. Per comparar dos idiomes, per exemple espanyol i català, el que farem és comparar cada paraula del diccionari espanyol amb totes les del català, anotant per a cada paraula la puntuació de la que queda més aprop. La suma final de totes les puntuacions mínimes de cada paraula la ponderarem dividint pel nombre de paraules del diccionari espanyol, anomenant a la distància resultant “esp-cat”. Després fem el mateix amb cada paraula del diccionari català amb el diccionari espanyol, anomenant a la mida resultant "cat-esp". La distància final entre els dos idiomes serà igual a: Dis-esp-cat = sqrt((esp-cat)^2 + (cat-esp)^2).

Realitzar una aplicació amb interfície gràfica d'usuari i seguint el patró de disseny MVC que permeti l'anàlisi i la comparació d'un conjunt d'idiomes. Es tracta d'esbrinar com s'assemblen els idiomes entre ells. El programa ha de funcionar amb almenys 10 idiomes (si no voleu complicar-vos preferentment europeus).

D'aquesta manera obtindrem una matriu de distàncies entre els N idiomes comparats. Tenir en compte que la distància de Levenhstein ha d'anar ponderada a la longitud de cada paraula sobre la que es calcula.

L'aplicació, que no podrà tenir pre-calculat el resultat, ens ha de poder proporcionar resposta a les preguntes següents (OBLIGATORI):

- Seleccionat un idioma, a quina distància està un altre idioma?
- Seleccionat un idioma, a quina distància estan tots els altres?

Possibles afegits voluntaris:

- Representar el graf de distàncies. (OBLIGATORI en la recuperació de juliol)
- Proporcionar les respostes recolzant els resultats amb una gràfica.
- Obtenir l'arbre filo-lèxic dels idiomes.
- Donat un paràgraf de text, esbrinar de quin idioma es tracta.

No es permet que l'aplicació doni cap informació per consola, tot ha de poder ser visible a la GUI. Al vídeo s'haurà d'incloure una compilació amb l'execució de la llibreria "mesurament", junt amb les execucions del exemples que calgui. S'ha de lliurar una memòria semblant als exercicis anteriors del curs.

Al final de les diapositives de la distància d'edició es donen unes pautes per facilitar l'obtenció dels diccionaris d'open-office, tal com vam veure a classe (com més complets siguin aquests millors seran els resultats). Es poden emprar altres diccionaris. El fitxer annex és un exemple en miniatura que mostra el flux de càlcul de la pràctica.

## Estrucutra de carpetas

Esta práctica se ha estructurado de la siguiente manera:

- `assets`: Conjunto de imagenes y configuración adicional.
- `docs`: Documentación del proyecto. Generalmente guardrá el código fuente (LaTeX) y su respectivo PDF compilado.
- `lib`: Dependencias externas.
- `src`: Código fuente.
- `bin`: Archivos de salida compilados.
