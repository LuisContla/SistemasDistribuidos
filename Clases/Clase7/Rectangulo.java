public class Rectangulo extends Figura {
    private double base;
    private double altura;

    public Rectangulo(Coordenada centro, double base, double altura) {
        super(centro);
        this.base = base;
        this.altura = altura;
        construirVertices();
    }

    private void construirVertices() {
        double cx = centro.getX();
        double cy = centro.getY();
        double hb = base / 2.0;
        double ha = altura / 2.0;

        Coordenada v1 = new Coordenada(cx - hb, cy + ha);
        Coordenada v2 = new Coordenada(cx + hb, cy + ha);
        Coordenada v3 = new Coordenada(cx + hb, cy - ha);
        Coordenada v4 = new Coordenada(cx - hb, cy - ha);

        this.vertices = new Coordenada[] { v1, v2, v3, v4 };
    }

    @Override
    public double area() {
        return base * altura;
    }
}
