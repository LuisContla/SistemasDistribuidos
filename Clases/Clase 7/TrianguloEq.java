public class TrianguloEq extends Figura {
    private double lado;

    public TrianguloEq(Coordenada centro, double lado) {
        super(centro);
        this.lado = lado;
        construirVertices();
    }

    private void construirVertices() {
        double h = Math.sqrt(3) / 2.0 * lado;
        double cx = centro.getX();
        double cy = centro.getY();

        Coordenada v1 = new Coordenada(cx, cy + 2*h/3.0);
        Coordenada v2 = new Coordenada(cx - lado/2.0, cy - h/3.0);
        Coordenada v3 = new Coordenada(cx + lado/2.0, cy - h/3.0);

        this.vertices = new Coordenada[] { v1, v2, v3 };
    }

    @Override
    public double area() {
        return (Math.sqrt(3) / 4.0) * lado * lado;
    }
}
