/*
 * PROYECTO 3 — Simulación de votaciones
 * Autor: Contla Mota Luis Andrés 
 * Grupo: 7CV3 
 */
// ----------------------------------------------------------------------------------
// Este archivo forma parte de la simulación de votaciones en tiempo real.
// Traté de mantener el código simple y bien comentado para facilitar su lectura.
// ----------------------------------------------------------------------------------

import java.time.*;
import java.util.*;

public class CURPUtils {
    private static final String[] ESTADOS = {
        "AS","BC","BS","CC","CL","CM","CS","CH","DF","DG",
        "GT","GR","HG","JC","MC","MN","MS","NT","NL","OC",
        "PL","QT","QR","SP","SL","SR","TC","TL","TS","VZ",
        "YN","ZS","NE"
    };

    private static final Map<String,String> ESTADO_NOMBRE = new HashMap<>();
    static {
        String[] nombres = {
            "Aguascalientes","Baja California","Baja California Sur","Campeche","Coahuila",
            "Colima","Chiapas","Chihuahua","Ciudad de México","Durango","Guanajuato",
            "Guerrero","Hidalgo","Jalisco","México","Michoacán","Morelos","Nayarit",
            "Nuevo León","Oaxaca","Puebla","Querétaro","Quintana Roo","San Luis Potosí",
            "Sinaloa","Sonora","Tabasco","Tlaxcala","Tamaulipas","Veracruz","Yucatán",
            "Zacatecas","Extranjero"
        };
        for (int i = 0; i < ESTADOS.length; i++) {
            ESTADO_NOMBRE.put(ESTADOS[i], nombres[i]);
        }
    }

    static char randomChar(String alphabet) {
        return alphabet.charAt((int)(Math.random() * alphabet.length()));
    }

    // Generación basada en getCURP del profesor (4L+6D+S+EE+3L+2D),
    // pero ahora los 6 dígitos (YYMMDD) representan SIEMPRE una FECHA VÁLIDA
    // y garantizamos mayoría de edad (>=18).
    // Genera una CURP con 18 caracteres siguiendo el patrón del profe (no valida nombres reales)
    public static String generarCURP() {
        String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String DIGITOS = "0123456789";
        String SEXO = "HM";

        StringBuilder sb = new StringBuilder(18);
        for (int i = 0; i < 4; i++) sb.append(randomChar(LETRAS)); // 4 letras

        // Fecha válida entre 1940-01-01 y hoy-18 años
        LocalDate max = LocalDate.now().minusYears(18);
        LocalDate min = LocalDate.of(1940, 1, 1);
        long days = max.toEpochDay() - min.toEpochDay();
        long offset = (long)(Math.random() * (days + 1));
        LocalDate birth = LocalDate.ofEpochDay(min.toEpochDay() + offset);
        sb.append(String.format("%02d%02d%02d", birth.getYear() % 100, birth.getMonthValue(), birth.getDayOfMonth())); // YYMMDD

        sb.append(SEXO.charAt((int)(Math.random() * SEXO.length()))); // sexo
        sb.append(ESTADOS[(int)(Math.random() * 32)]); // estado (sin NE)
        for (int i = 0; i < 3; i++) sb.append(randomChar(LETRAS)); // 3 letras
        for (int i = 0; i < 2; i++) sb.append(randomChar(DIGITOS)); // 2 dígitos
        return sb.toString();
    }

    public static String sexoDe(String curp) {
        return curp.charAt(10) == 'H' ? "Hombre" : "Mujer";
    }

    public static String estadoClave(String curp) {
        return curp.substring(11, 13);
    }

    public static String estadoNombre(String curp) {
        return ESTADO_NOMBRE.getOrDefault(estadoClave(curp), "Desconocido");
    }

    // Calcula la edad a partir de YYMMDD. Ajuste de siglo básico para >=2000
    public static int edadDe(String curp) {
        try {
            String yy = curp.substring(4,6);
            String mm = curp.substring(6,8);
            String dd = curp.substring(8,10);
            int y = Integer.parseInt(yy);
            int year = y <= java.time.LocalDate.now().getYear()%100 ? 2000 + y : 1900 + y;
            int month = Integer.parseInt(mm);
            int day = Integer.parseInt(dd);
            java.time.LocalDate birth = java.time.LocalDate.of(year, month, day);
            return java.time.Period.between(birth, java.time.LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }
}
