public abstract class Figura implements Desplazable {
    protected Coordenada centro;
    protected Coordenada[] vertices;

    public Figura(Coordenada centro) {
        this.centro = centro;
    }

    public Coordenada getCentro() { return centro; }
    public Coordenada[] getVertices() { return vertices; }

    public abstract double area();

    @Override
    public void desplazar(double dx, double dy) {
        if (centro != null) centro.desplazar(dx, dy);
        if (vertices != null) {
            for (Coordenada v : vertices) {
                v.desplazar(dx, dy);
            }
        }
    }

    protected static void imprimirVertices(String nombre, Coordenada[] vs) {
        System.out.println("Vertices de " + nombre + ":");
        for (int i = 0; i < vs.length; i++) {
            System.out.println("  v" + (i+1) + " = " + vs[i]);
        }
    }
}
