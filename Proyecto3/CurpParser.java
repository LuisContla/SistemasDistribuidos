
/*
 * Utilidades para interpretar campos a partir de una CURP "sintética" como la que
 * genera Curp.getCURP(). Maneja:
 *  - sexo (H/M) en la posición 11 (índice 10)
 *  - entidad (2 letras) en posiciones 12-13 (índices 11-12)
 *  - fecha YYMMDD en posiciones 5-10 (índices 4-9), infiere siglo.
 */
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public final class CurpParser {
    private static final DateTimeFormatter YYMMDD = DateTimeFormatter.ofPattern("yyMMdd", Locale.ROOT);
    private static final Map<String,String> ESTADOS = new HashMap<>();
    static {
        ESTADOS.put("AS","Aguascalientes"); ESTADOS.put("BC","Baja California");
        ESTADOS.put("BS","Baja California Sur"); ESTADOS.put("CC","Campeche");
        ESTADOS.put("CS","Chiapas"); ESTADOS.put("CH","Chihuahua");
        ESTADOS.put("CL","Coahuila"); ESTADOS.put("CM","Colima");
        ESTADOS.put("DF","Ciudad de México"); ESTADOS.put("DG","Durango");
        ESTADOS.put("GT","Guanajuato"); ESTADOS.put("GR","Guerrero");
        ESTADOS.put("HG","Hidalgo"); ESTADOS.put("JC","Jalisco");
        ESTADOS.put("MC","Estado de México"); ESTADOS.put("MN","Michoacán");
        ESTADOS.put("MS","Morelos"); ESTADOS.put("NT","Nayarit");
        ESTADOS.put("NL","Nuevo León"); ESTADOS.put("OC","Oaxaca");
        ESTADOS.put("PL","Puebla"); ESTADOS.put("QT","Querétaro");
        ESTADOS.put("QR","Quintana Roo"); ESTADOS.put("SP","San Luis Potosí");
        ESTADOS.put("SL","Sinaloa"); ESTADOS.put("SR","Sonora");
        ESTADOS.put("TC","Tabasco"); ESTADOS.put("TL","Tlaxcala");
        ESTADOS.put("TS","Tamaulipas"); ESTADOS.put("VZ","Veracruz");
        ESTADOS.put("YN","Yucatán"); ESTADOS.put("ZS","Zacatecas");
    }

    private CurpParser() {}

    public static char sexo(String curp) {
        if (curp == null || curp.length() < 11) return '?';
        return Character.toUpperCase(curp.charAt(10)); // H o M
    }

    public static String estado(String curp) {
        if (curp == null || curp.length() < 13) return "NA";
        String code = curp.substring(11, 13).toUpperCase(Locale.ROOT);
        return ESTADOS.getOrDefault(code, code);
    }

    public static int edad(String curp, LocalDate hoy) {
        LocalDate nacimiento = fechaNacimiento(curp);
        if (nacimiento == null) return -1;
        return Period.between(nacimiento, hoy).getYears();
    }

    public static LocalDate fechaNacimiento(String curp) {
        if (curp == null || curp.length() < 10) return null;
        String yymmdd = curp.substring(4, 10);
        try {
            // Inferimos el siglo: si la YY es > añoActual%100, asumimos 1900s, si no 2000s
            LocalDate base = LocalDate.parse(yymmdd, YYMMDD);
            int yy = Integer.parseInt(yymmdd.substring(0,2));
            int currentYY = LocalDate.now().getYear() % 100;
            int century = (yy > currentYY) ? 1900 : 2000;
            return base.withYear(century + yy);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
