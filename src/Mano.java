import java.util.ArrayList;
import java.util.List;

public class Mano {
    private String nombre;
    private List<Carta> cartas;
    private boolean dijoUno;

    public Mano(String nombre) {
        this.nombre = nombre;
        this.cartas = new ArrayList<>();
        this.dijoUno = false;
    }

    public String getNombre() { return nombre; }
    public List<Carta> getCartas() { return cartas; }
    public int getNumeroCartas() { return cartas.size(); }
    public boolean haDichoUno() { return dijoUno; }
    public void setDijoUno(boolean dijo) { this.dijoUno = dijo; }

    public void agregarCarta(Carta carta) { cartas.add(carta); }
    public Carta jugarCarta(int indice) {
        return (indice >= 0 && indice < cartas.size()) ? cartas.remove(indice) : null;
    }

    public boolean tieneCartaJugable(Carta ultima, Carta.Color colorActual) {
        for (Carta c : cartas) {
            if (c.esJugable(ultima, colorActual)) return true;
        }
        return false;
    }

    public Carta getPrimeraCartaJugable(Carta ultima, Carta.Color colorActual) {
        for (Carta c : cartas) {
            if (c.esJugable(ultima, colorActual)) return c;
        }
        return null;
    }

    public void mostrarCartas() {
        System.out.println("Cartas de " + nombre + ":");
        for (int i = 0; i < cartas.size(); i++) {
            System.out.println((i + 1) + ": " + cartas.get(i));
        }
    }
}