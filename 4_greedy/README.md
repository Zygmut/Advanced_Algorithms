# Práctica 4: Algoritmos "Greedy"

Efectuar una aplicació seguint el patró de disseny MVC per resoldre la problemàtica del camí mínim. L'aplicació haurà de donar resposta a la qüestió de trobar el camí mínim entre dos pobles d'Eivissa i Formentera donats, passant per un tercer. Per defecte hi haurà les localitats representades al mapa d'aquest enunciat. L'algoritme de càlcul desenvolupat haurà de ser obligatòriament un de tipus Dikstra (recursiu o no), que resolgui aquest problema per les illes d'Eivissa i Formentera (es suposa que el ferri és una carretera més, ruta vermella). Al dibuix es mostra un exemple d'indicar la partida (Sant Rafel de sa Creu), el destí (És Caló), passant per San José.

El programa ha de presentar una GUI a on l'usuari pugui clicar sobre el punt d'origen, el destí i el punt intermedi. Associat al codi del programa s'ha de lliurar una memòria i un vídeo amb les mateixes condicions que les pràctiques anteriors. Però en aquest cas puntuaran més a la memòria els aspectes propis de l'exercici i la qualitat de les reflexions de l'alumne. El mateix passarà amb el vídeo, ja que es valoraran molt la qualitat i quantitat dels exemple d'execució. En els tres primers exercicis, per donar temps a l'alumne a familiaritzar-se amb el tipus de document, s'ha corregit de forma molt esquemàtica. A partir d'ara, es valorarà sobretot la forma de presentar i discutir la implementació concreta de l'exercici.

La part obligatòria puntuarà un màxim de 7 sobre 10.

Feines addicionals per l'entrega a la convocatòria durant el curs:

1. Que el programa pugui introduir diferents mapes i arxius de localitzacions emprant tècniques d'arxius XML/SAX.
2. Que l'algoritme de càlcul empri tècniques d'ordenar les arestes o col·locar-les a un monticle. Efectuar una comparativa de cost dels diferents monticles vists (aquesta feina es pot fer apart).
3. Que es puguin definir rutes alternatives. Segon més curt, etc.
4. Altres.

## Estrucutra de carpetas

Esta práctica se ha estructurado de la siguiente manera:

- `assets`: Conjunto de imagenes y configuración adicional.
- `docs`: Documentación del proyecto. Generalmente guardrá el código fuente (LaTeX) y su respectivo PDF compilado.
- `lib`: Dependencias externas.
- `src`: Código fuente.
- `tools`: Herramientas adicionales como scripts o estudios.
- `bin`: Archivos de salida compilados.
