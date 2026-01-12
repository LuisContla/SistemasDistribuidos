/*
 * Ejercicio Clase 22
 * Autor: Contla Mota Luis Andrés 
 * Grupo: 7CV3 
 */

import com.google.gson.*;
import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GsonExample {

    // Endpoint: 3 citas en una sola petición
    // Doc: https://api.breakingbadquotes.xyz/v1/quotes/{number}
    // (por ejemplo, /v1/quotes/3 devuelve 3 citas)
    private static final String BB_QUOTES_URL = "https://api.breakingbadquotes.xyz/v1/quotes/3";

    // Endpoint de Google Translate v2
    private static final String TRANSLATE_URL = "https://translate.googleapis.com/language/translate/v2";

    private static final HttpClient http = HttpClient.newHttpClient();

    public static void main(String[] args) {
        try {
            // API key: pásala como argumento o variable de entorno GCP_TRANSLATE_API_KEY
            String apiKey = args.length > 0 ? args[0] : System.getenv("GCP_TRANSLATE_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                System.err.println("Falta la API key de Google Translate. Pásala como argumento o " +
                                   "define la variable de entorno GCP_TRANSLATE_API_KEY.");
                System.exit(1);
            }

            // 1) Pedir 3 citas a la API de Breaking Bad (una sola petición)
            HttpRequest quotesReq = HttpRequest.newBuilder(URI.create(BB_QUOTES_URL)).GET().build();
            HttpResponse<String> quotesResp = http.send(quotesReq, HttpResponse.BodyHandlers.ofString());

            JsonArray quotesArray = JsonParser.parseString(quotesResp.body()).getAsJsonArray();

            // 2) Traducir cada cita por separado (3 peticiones a Google)
            for (int i = 0; i < quotesArray.size(); i++) {
                JsonObject obj = quotesArray.get(i).getAsJsonObject();
                String english = obj.get("quote").getAsString();
                String author  = obj.get("author").getAsString();

                String spanish = translateText(english, "es", apiKey);

                // 3) Imprimir: inglés, autor y traducción
                System.out.println("--------------------------------------------------");
                System.out.println("Cita (en):  " + english);
                System.out.println("Autor:      " + author);
                System.out.println("Traducción: " + spanish);
            }

        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Traduce un texto usando Google Translate v2 (POST application/x-www-form-urlencoded)
    private static String translateText(String text, String targetLang, String apiKey) throws Exception {
        // Usar URLEncoder para manejar espacios y caracteres especiales (recomendación de la práctica)
        String form = "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                      "&target=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8) +
                      "&format=text" +
                      "&key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder(URI.create(TRANSLATE_URL))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        JsonObject root = JsonParser.parseString(resp.body()).getAsJsonObject();
        JsonArray translations = root.getAsJsonObject("data").getAsJsonArray("translations");
        return translations.get(0).getAsJsonObject().get("translatedText").getAsString();
    }
}
