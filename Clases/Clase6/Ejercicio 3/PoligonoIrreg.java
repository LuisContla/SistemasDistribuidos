import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class PoligonoIrreg {
    private ArrayList<Coordenada> vertices;
    private Random rand;

    public PoligonoIrreg() {
        vertices = new ArrayList<>();
        rand = new Random();
    }

    public void anadeVertice(Coordenada c) {
        vertices.add(c);
    }

    public void anadeVerticesAleatorios(int n) {
        for (int i = 0; i < n; i++) {
            double x = Math.round((rand.nextDouble() * 200 - 100) * 1000.0) / 1000.0;
            double y = Math.round((rand.nextDouble() * 200 - 100) * 1000.0) / 1000.0;
            anadeVertice(new Coordenada(x, y));
        }
    }

    // Ordenar vértices por magnitud
    public void ordenaVertices() {
        vertices.sort(Comparator.comparingDouble(Coordenada::getMagnitud));
    }

    public int getTotalVertices() {
        return vertices.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Coordenada v : vertices) {
            sb.append(v).append("\n");
        }
        sb.append("Vértices totales: ").append(getTotalVertices());
        return sb.toString();
    }
}
