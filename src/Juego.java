import java.util.*;

public class Juego {
    private Baraja mazo;
    private List<Carta> descarte;
    private List<Mano> jugadores;
    private int turnoActual;
    private int direccion; // 1 horario, -1 antihorario
    private Carta.Color colorActual;
    private int saltosPendientes;
    private boolean juegoTerminado;
    private Scanner scanner;

    class JugadaInvalidaException extends Exception {
        public JugadaInvalidaException(String mensaje) { super(mensaje); }
    }

    public Juego() {
        mazo = new Baraja();
        descarte = new ArrayList<>();
        jugadores = new ArrayList<>();
        jugadores.add(new Mano("Humano"));
        jugadores.add(new Mano("CPU"));
        scanner = new Scanner(System.in);
        direccion = 1;
        turnoActual = 0;
        saltosPendientes = 0;
        juegoTerminado = false;
    }

    public void iniciarJuego() {
        for (Mano m : jugadores) {
            for (int i = 0; i < 7; i++) m.agregarCarta(mazo.draw());
        }

        Carta primera = mazo.draw();
        if (primera == null) {
            System.out.println("Error al iniciar el mazo.");
            return;
        }
        descarte.add(primera);
        if (primera.getTipo() == Carta.Tipo.COMODIN) {
            colorActual = elegirColorAleatorio();
            System.out.println("Carta inicial comodín. Color elegido: " + colorActual);
        } else {
            colorActual = primera.getColor();
        }

        System.out.println("Juego de Cartas -UNO- v2.0\n");
        System.out.println("Este juego consiste en un enfrentamiento entre la CPU");
        System.out.println("y el jugador, de donde tienes que seleccionar una carta");
        System.out.println("adecuada en la mesa y asi consecutivamente, el jugador");
        System.out.println("que se quede sin cartas, es el ganador.\n");
        System.out.println("CONTIENE NUEVAS MEJORAS, PARA SABER MAS, VE A README.md\n");
        System.out.println("Carta inicial: " + getUltimaCarta() + " (color actual: " + colorActual + ")");

        while (!juegoTerminado) {
            Mano jugadorActual = jugadores.get(turnoActual);
            System.out.println("\n--- Turno de " + jugadorActual.getNombre() + " ---");
            System.out.println("Carta en mesa: " + getUltimaCarta() + " (color actual: " + colorActual + ")");
            if (jugadorActual.getNombre().equals("Humano")) {
                jugadorActual.mostrarCartas(); // Solo se muestra la mano del humano
            }

            boolean turnoCompletado = false;
            while (!turnoCompletado && !juegoTerminado) {
                if (jugadorActual.getNombre().equals("Humano")) {
                    try {
                        turnoCompletado = procesarTurnoHumano(jugadorActual);
                    } catch (JugadaInvalidaException e) {
                        System.out.println("\nError: " + e.getMessage());
                    }
                } else {
                    turnoCompletado = procesarTurnoCPU(jugadorActual);
                }
            }

            if (juegoTerminado) break;

            // Penalización por no decir UNO (solo humano)
            if (jugadorActual.getNombre().equals("Humano") && jugadorActual.getNumeroCartas() == 1 && !jugadorActual.haDichoUno()) {
                System.out.println("Penalizacion: robas una carta.");
                Carta penal = robarCarta();
                if (penal != null) {
                    jugadorActual.agregarCarta(penal);
                    System.out.println("Robaste: " + penal);
                }
            }
            jugadorActual.setDijoUno(false);

            if (jugadorActual.getNumeroCartas() == 0) {
                System.out.println("\n" + jugadorActual.getNombre() + " es el vencedor!");
                juegoTerminado = true;
                break;
            }

            int avance = direccion * (1 + saltosPendientes);
            turnoActual = (turnoActual + avance + jugadores.size()) % jugadores.size();
            saltosPendientes = 0;
        }
        scanner.close();
    }

    private Carta getUltimaCarta() { return descarte.get(descarte.size() - 1); }
    private Carta.Color elegirColorAleatorio() {
        Random rand = new Random();
        return Carta.Color.values()[rand.nextInt(Carta.Color.values().length)];
    }
    private Carta.Color elegirColorHumano() {
        while (true) {
            System.out.print("\nElige un color (ROJO, AMARILLO, VERDE, AZUL): ");
            try {
                return Carta.Color.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Color no valido.");
            }
        }
    }
    private Carta robarCarta() {
        if (mazo.isEmpty()) {
            if (descarte.size() <= 1) {
                System.out.println("No quedan cartas para robar.");
                return null;
            }
            Carta top = descarte.remove(descarte.size() - 1);
            List<Carta> resto = new ArrayList<>(descarte);
            descarte.clear();
            descarte.add(top);
            mazo.addCards(resto);
            System.out.println("Mazo rellenado con descarte.");
        }
        return mazo.draw();
    }

    private boolean procesarTurnoHumano(Mano jugador) throws JugadaInvalidaException {
        System.out.print("Elige carta (numero) o 'NO' para robar: ");
        String input = scanner.nextLine().trim();

        // Comando UNO al inicio del turno
        if (input.equalsIgnoreCase("UNO")) {
            if (jugador.getNumeroCartas() == 1) {
                jugador.setDijoUno(true);
                System.out.println("Has dicho UNO.");
            } else {
                System.out.println("\nNo puedes decir UNO si no tienes una sola carta.");
            }
            return false;
        }

        // Comando NO (robar)
        if (input.equalsIgnoreCase("NO")) {
            if (jugador.tieneCartaJugable(getUltimaCarta(), colorActual)) {
                System.out.println("Tienes cartas jugables, no puedes robar.");
                return false;
            } else {
                Carta robada = robarCarta();
                if (robada != null) {
                    jugador.agregarCarta(robada);
                    System.out.println("Robaste: " + robada);
                }
                return true;
            }
        }

        // Intentar jugar una carta por número
        try {
            int num = Integer.parseInt(input);
            if (num < 0) {
                System.out.println("\nComando no valido (negativo), coloca la opcion correcta.");
                return false;
            }
            int indice = num - 1;
            if (indice < 0 || indice >= jugador.getNumeroCartas())
                throw new JugadaInvalidaException("\nNumero fuera de rango. Intenta de nuevo.");

            Carta elegida = jugador.getCartas().get(indice);
            if (!elegida.esJugable(getUltimaCarta(), colorActual))
                throw new JugadaInvalidaException("\nEsa carta no es jugable. Debe coincidir en color o numero.");

            // Regla +4: no se puede jugar si se tiene carta del color actual
            if (elegida.getTipo() == Carta.Tipo.COMODIN && elegida.getComodin() == Carta.Comodin.ROBA4) {
                boolean tieneColor = false;
                for (Carta c : jugador.getCartas()) {
                    if (c.getTipo() != Carta.Tipo.COMODIN && c.getColor() == colorActual) {
                        tieneColor = true;
                        break;
                    }
                }
                if (tieneColor)
                    throw new JugadaInvalidaException("No puedes jugar +4 si tienes carta del color actual.");
            }

            Carta jugada = jugador.jugarCarta(indice);
            descarte.add(jugada);
            System.out.println("Jugaste: " + jugada);
            aplicarEfectoCarta(jugada, jugador);

            // Después de jugar, si queda una sola carta, dar oportunidad de decir UNO
            if (jugador.getNumeroCartas() == 1 && !jugador.haDichoUno()) {
                System.out.print("\nTe queda una carta. Escribe 'UNO' para declararlo (o presiona Enter para continuar): ");
                String respuesta = scanner.nextLine().trim();
                if (respuesta.equalsIgnoreCase("UNO")) {
                    jugador.setDijoUno(true);
                    System.out.println("Has dicho UNO.");
                } else {
                    System.out.println("No has dicho UNO.");
                }
            }

            return true;

        } catch (NumberFormatException e) {
            throw new JugadaInvalidaException("Comando no reconocido.");
        }
    }

    private boolean procesarTurnoCPU(Mano jugador) {
        Carta ultima = getUltimaCarta();
        Carta jugable = jugador.getPrimeraCartaJugable(ultima, colorActual);

        if (jugable != null) {
            // Regla +4 para CPU
            if (jugable.getTipo() == Carta.Tipo.COMODIN && jugable.getComodin() == Carta.Comodin.ROBA4) {
                boolean tieneColor = false;
                for (Carta c : jugador.getCartas()) {
                    if (c.getTipo() != Carta.Tipo.COMODIN && c.getColor() == colorActual) {
                        tieneColor = true;
                        break;
                    }
                }
                if (tieneColor) {
                    Carta alt = null;
                    for (Carta c : jugador.getCartas()) {
                        if (c != jugable && c.esJugable(ultima, colorActual)) {
                            alt = c;
                            break;
                        }
                    }
                    if (alt != null) jugable = alt;
                    else return robarTurnoCPU(jugador);
                }
            }

            int indice = jugador.getCartas().indexOf(jugable);
            Carta jugada = jugador.jugarCarta(indice);
            descarte.add(jugada);
            System.out.println("CPU juega: " + jugada);
            aplicarEfectoCarta(jugada, jugador);
            if (jugador.getNumeroCartas() == 1) {
                jugador.setDijoUno(true);
                System.out.println("CPU dice UNO.");
            }
            return true;
        } else {
            return robarTurnoCPU(jugador);
        }
    }

    private boolean robarTurnoCPU(Mano jugador) {
        System.out.println("CPU no tiene carta, roba.");
        Carta robada = robarCarta();
        if (robada != null) {
            jugador.agregarCarta(robada);
            System.out.println("CPU roba: " + robada);
            if (robada.esJugable(getUltimaCarta(), colorActual)) {
                int indice = jugador.getCartas().indexOf(robada);
                Carta jugada = jugador.jugarCarta(indice);
                descarte.add(jugada);
                System.out.println("Y la juega: " + jugada);
                aplicarEfectoCarta(jugada, jugador);
                if (jugador.getNumeroCartas() == 1) {
                    jugador.setDijoUno(true);
                    System.out.println("CPU dice UNO.");
                }
            } else {
                System.out.println("No puede jugarla.");
            }
        }
        return true;
    }

    private void aplicarEfectoCarta(Carta carta, Mano jugador) {
        // Actualizar color actual para cartas que no son comodín
        if (carta.getTipo() != Carta.Tipo.COMODIN) {
            colorActual = carta.getColor();
        }
        // Procesar efectos especiales
        switch (carta.getTipo()) {
            case ACCION:
                switch (carta.getAccion()) {
                    case REVERSA:
                        direccion *= -1;
                        System.out.println("Sentido invertido!");
                        break;
                    case SALTO:
                        saltosPendientes++;
                        System.out.println("Salta turno!");
                        break;
                    case ROBA2:
                        Mano sig = getSiguienteJugador();
                        for (int i = 0; i < 2; i++) {
                            Carta r = robarCarta();
                            if (r != null) sig.agregarCarta(r);
                        }
                        System.out.println(sig.getNombre() + " roba 2 y pierde turno.");
                        saltosPendientes++;
                        break;
                }
                break;
            case COMODIN:
                switch (carta.getComodin()) {
                    case CAMBIO_COLOR:
                        colorActual = (jugador.getNombre().equals("Humano")) ? elegirColorHumano() : elegirColorAleatorio();
                        System.out.println("Color elegido: " + colorActual);
                        break;
                    case ROBA4:
                        colorActual = (jugador.getNombre().equals("Humano")) ? elegirColorHumano() : elegirColorAleatorio();
                        System.out.println("Color elegido: " + colorActual);
                        Mano siguiente = getSiguienteJugador();
                        for (int i = 0; i < 4; i++) {
                            Carta r = robarCarta();
                            if (r != null) siguiente.agregarCarta(r);
                        }
                        System.out.println(siguiente.getNombre() + " roba 4 y pierde turno.");
                        saltosPendientes++;
                        break;
                }
                break;
            default:
                break;
        }
    }

    private Mano getSiguienteJugador() {
        int sig = (turnoActual + direccion + jugadores.size()) % jugadores.size();
        return jugadores.get(sig);
    }
}