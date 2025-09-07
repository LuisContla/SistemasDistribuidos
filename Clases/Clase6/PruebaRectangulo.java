public class PruebaRectangulo {
    public static void main (String[] args) {

        // Usando el constructor con 4 doubles
        System.out.println("RESULTADOS");
        Rectangulo rect1 = new Rectangulo(2,3,5,1);
        double ancho, alto;

        System.out.println("Rectángulo creado con coordenadas (double):");
        System.out.println(rect1);
        alto = rect1.superiorIzquierda().ordenada() - rect1.inferiorDerecha().ordenada();
        ancho = rect1.inferiorDerecha().abcisa() - rect1.superiorIzquierda().abcisa();
        System.out.println("El área del rectángulo es = " + ancho*alto);

        // Usando el constructor con objetos Coordenada
        Rectangulo rect2 = new Rectangulo(new Coordenada(2,3), new Coordenada(5,1));

        System.out.println("\nRectángulo creado con objetos Coordenada:");
        System.out.println(rect2);
        alto = rect2.superiorIzquierda().ordenada() - rect2.inferiorDerecha().ordenada();
        ancho = rect2.inferiorDerecha().abcisa() - rect2.superiorIzquierda().abcisa();
        System.out.println("El área del rectángulo es = " + ancho*alto);
    }
}