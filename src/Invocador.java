import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Currency;


public class Invocador {

    private String accId;
    private String id;                                                //Dsp modificar para agregar otras regiones, por ahora LAS
    private String ligaSoloDuo;
    private String ligaFlex;
    private String divisionSoloDuo;
    private String divisionFlex;
    private String nombre;
    private Long nivel;

    public Invocador(String nombre, String apiKey) throws IOException, ParseException {
        JSONObject datosInvocador = this.obtenerDatosBasicos(nombre, apiKey);

        this.id = (String) datosInvocador.get("id");
        this.accId = (String) datosInvocador.get("accountId");
        this.nombre = (String) datosInvocador.get("name");
        this.nivel = (Long) datosInvocador.get("summonerLevel");

        JSONArray datosLigas = this.obtenerLigas(this.id,apiKey);
        System.out.println(datosInvocador);
        JSONObject datosSoloDuo = (JSONObject) datosLigas.get(0);
        //JSONObject datosFlex = (JSONObject) datosLigas.get(1);      //Si es unranked get(1) no existe, hay que validar con el size del array de JSONs

        this.ligaSoloDuo = (String) datosSoloDuo.get("tier");
        this.divisionSoloDuo = (String) datosSoloDuo.get("rank");
        /*this.ligaFlex = (String) datosFlex.get("tier");
        this.divisionFlex = (String) datosFlex.get("rank");*/

        JSONObject historial = this.obtenerHistorial(this.accId,apiKey);
        JSONArray partidas = (JSONArray) historial.get("matches");
        /*for (Object partida : partidas){
            System.out.println(partida);
        }*/

        JSONObject ultimaPartida = (JSONObject) partidas.get(0);
        Long matchId =  (Long)ultimaPartida.get("gameId");
        JSONObject datosUltimaPartida = this.obtenerDatosPartida(matchId,apiKey);
        System.out.println(datosUltimaPartida);

        //this.printearDatos();
    }

    private Object pedirARito(String CURL) throws IOException, ParseException {
        URL url = new URL(CURL);
        URLConnection uc = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        JSONParser parser = new JSONParser();
        String line = reader.readLine();
        return parser.parse(line);
    }

    private JSONObject obtenerDatosBasicos(String nombreInvocador, String apiKey) throws IOException, ParseException {
        String CURL = "https://la2.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + nombreInvocador + "?api_key=" + apiKey;
        JSONObject datosInvocador = (JSONObject) pedirARito(CURL);
        return datosInvocador;
    }


    private JSONArray obtenerLigas(String summId,String apiKey) throws IOException, ParseException {
        String CURL = "https://la2.api.riotgames.com/lol/league/v4/positions/by-summoner/" + summId + "?api_key=" + apiKey;
        JSONArray datosLiga = (JSONArray) pedirARito(CURL);
        return datosLiga;
    }

    private void printearDatos(){
        System.out.println("Nombre: " + this.nombre + "\nNivel: " + this.nivel + "\nSolo/Duo: "
                + this.ligaSoloDuo + " " + this.divisionSoloDuo +"\nFlex: " + this.ligaFlex + " " +this.divisionFlex);
    }

    private void printearBasico(){
        System.out.println("Nombre: " + this.nombre + "\nNivel: " + this.nivel);
    }

    private JSONObject obtenerHistorial(String accId,String apiKey) throws IOException, ParseException {
        String CURL = "https://la2.api.riotgames.com/lol/match/v4/matchlists/by-account/" + accId + "?api_key=" + apiKey;
        JSONObject datosHistorial = (JSONObject) pedirARito(CURL);
        return datosHistorial;
    }

    private JSONObject obtenerDatosPartida(Long matchId,String apiKey) throws IOException, ParseException {
        String CURL = "https://la2.api.riotgames.com/lol/match/v4/matches/" + matchId.toString() + "?api_key=" + apiKey;
        JSONObject datosPartida = (JSONObject) pedirARito(CURL);
        return datosPartida;
    }



}