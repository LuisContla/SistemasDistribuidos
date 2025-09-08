public class Clase7 {
    public static void main(String[] args) {
        TrianguloEq t = new TrianguloEq(new Coordenada(0, 0), 4.0);
        Rectangulo r = new Rectangulo(new Coordenada(6, 1), 6.0, 3.0);

        System.out.println("=== Posicion inicial ===");
        System.out.println("Area Triangulo: " + t.area());
        Figura.imprimirVertices("Triangulo", t.getVertices());

        System.out.println("Area Rectangulo: " + r.area());
        Figura.imprimirVertices("Rectangulo", r.getVertices());

        double dx = 3.0, dy = 2.0;
        t.desplazar(dx, dy);
        r.desplazar(dx, dy);

        System.out.println("\n=== Despues de desplazar dx=" + dx + ", dy=" + dy + " ===");
        System.out.println("Area Triangulo: " + t.area());
        Figura.imprimirVertices("Triangulo", t.getVertices());

        System.out.println("Area Rectangulo: " + r.area());
        Figura.imprimirVertices("Rectangulo", r.getVertices());
    }
}
