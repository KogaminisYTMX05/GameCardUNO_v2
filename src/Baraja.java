import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Baraja {
    private Stack<Carta> cartas;

    public Baraja() {
        cartas = new Stack<>();
        for (Carta.Color color : Carta.Color.values()) {
            cartas.push(new Carta(color, 0));
            for (int num = 1; num <= 9; num++) {
                cartas.push(new Carta(color, num));
                cartas.push(new Carta(color, num));
            }
        }
        for (Carta.Color color : Carta.Color.values()) {
            for (Carta.Accion accion : Carta.Accion.values()) {
                cartas.push(new Carta(color, accion));
                cartas.push(new Carta(color, accion));
            }
        }
        for (int i = 0; i < 4; i++) {
            cartas.push(new Carta(Carta.Comodin.CAMBIO_COLOR));
            cartas.push(new Carta(Carta.Comodin.ROBA4));
        }
        shuffle();
    }

    public void shuffle() { Collections.shuffle(cartas); }
    public Carta draw() { return cartas.isEmpty() ? null : cartas.pop(); }
    public boolean isEmpty() { return cartas.isEmpty(); }
    public int size() { return cartas.size(); }
    public void addCards(List<Carta> cartasToAdd) {
        cartas.addAll(cartasToAdd);
        shuffle();
    }
}