public class PruebaPoligono {
    public static void main(String[] args) {
        PoligonoIrreg p = new PoligonoIrreg();

        p.anadeVerticesAleatorios(7);

        System.out.println("Los vértices del polígono son:");
        System.out.println(p);

        p.ordenaVertices();

        System.out.println("\nLos vértices del polígono ordenados por magnitud son:");
        System.out.println(p);
    }
}
