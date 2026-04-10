## GameCardUNO v2.0 (Actualizado el día 17/03/2026)

Este juego consiste en un enfrentamiento entre la CPU y el jugador, de donde tienes que seleccionar una carta adecuada en la mesa y asi consecutivamente, el jugador que se quede sin cartas, es el ganador.

## Mejoras en la actualización 2.0

* Implementación de comandos con restricción:
- Al añadir un valor negativo (ejemplo: -4) la jugada no se altera y no pierde turno.
- Al NO tener cartas jugables el usuario P1 puede robar cartas de forma manual al poner "NO", si hay cartas jugables, no es válido y no pierde turno.
- El jugador tiene que decir UNO cuando este tenga una sola carta, sanción al no decirlo: Robar 1 carta.
- Se implementó las cartas especiales (ROBA 2, ROBA 4, SALTO, REVERSA, COMODIN (cambio de color)).

## Diagrama UML

![image alt](https://github.com/brmmx2005/GameCardUNO/blob/fd9fa42ac6ba1b5d1ef68658f58b266cc1e3d130/uml_diagram/uml_complete.png)
