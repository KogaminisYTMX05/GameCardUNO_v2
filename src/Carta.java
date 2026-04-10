public class Carta {
    public enum Tipo { NUMERO, ACCION, COMODIN }
    public enum Color { ROJO, AMARILLO, VERDE, AZUL }
    public enum Accion { REVERSA, SALTO, ROBA2 }
    public enum Comodin { CAMBIO_COLOR, ROBA4 }

    private Tipo tipo;
    private Color color;
    private Integer numero;
    private Accion accion;
    private Comodin comodin;

    public Carta(Color color, int numero) {
        this.tipo = Tipo.NUMERO;
        this.color = color;
        this.numero = numero;
    }

    public Carta(Color color, Accion accion) {
        this.tipo = Tipo.ACCION;
        this.color = color;
        this.accion = accion;
    }

    public Carta(Comodin comodin) {
        this.tipo = Tipo.COMODIN;
        this.comodin = comodin;
        this.color = null;
    }

    public Tipo getTipo() { return tipo; }
    public Color getColor() { return color; }
    public Integer getNumero() { return numero; }
    public Accion getAccion() { return accion; }
    public Comodin getComodin() { return comodin; }

    public boolean esJugable(Carta ultima, Color colorActual) {
        if (tipo == Tipo.COMODIN) return true;
        Color colorComparar = (ultima.getTipo() == Tipo.COMODIN) ? colorActual : ultima.getColor();
        if (this.color == colorComparar) return true;
        if (ultima.getTipo() == Tipo.NUMERO && this.tipo == Tipo.NUMERO) {
            return this.numero.equals(ultima.getNumero());
        } else if (ultima.getTipo() == Tipo.ACCION && this.tipo == Tipo.ACCION) {
            return this.accion == ultima.getAccion();
        }
        return false;
    }

    @Override
    public String toString() {
        switch (tipo) {
            case NUMERO: return color + " " + numero;
            case ACCION: return color + " " + accion;
            case COMODIN: return comodin.toString();
            default: return "?";
        }
    }
}