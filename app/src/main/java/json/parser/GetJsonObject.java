package json.parser;

/**
 * Created by sohailpatel on 3/11/18.
 */

import java.sql.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import mysql.ProcedureCall;
import mysql.SQLConnection;

public class GetJsonObject {

    private static final String SQL = "sql", JSON = "json";

    public Document callHtmlPage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return doc;
        }
    }

    protected JSONObject getJsonObject(String url) {
        String jsonData = "";
        JSONParser parser = null;
        JSONObject jsonObject = null;
        Document doc = callHtmlPage(url);
        Elements scriptTags = doc.select("script");
        for (Element element : scriptTags) {
            if (element.toString().contains("var")) {
                int startIndex, endIndex;
                String rawJson = element.toString();
                rawJson = rawJson.substring(rawJson.indexOf("{") + 1);
                rawJson = rawJson.substring(rawJson.indexOf("{"));
                rawJson = rawJson.substring(0, rawJson.lastIndexOf("}") - 1);
                endIndex = rawJson.lastIndexOf("}") + 1;
                jsonData = rawJson.substring(0, endIndex);
                break;
            }
        }
        try {
            parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(jsonData);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }
        return  jsonObject;
    }

    public static void main(String args[]){
        GetJsonObject jp = new GetJsonObject();
        ParseJson parseJson = new ParseJson();
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection;
        String url = "http://ella.ils.indiana.edu/~szpatel/beacon_websites/html/attendance.html", type = null;
        JSONObject jsonObject = jp.getJsonObject(url);
        type = parseJson.getJSONString(jsonObject, "type");
        if(type.equalsIgnoreCase(JSON)) {
            jsonObject = parseJson.getJSONObject(jsonObject, "data");
            System.out.println(jsonObject);
        }
        else {
            String beaconName = parseJson.getJSONString(jsonObject, "data");
            connection = sqlConnection.getConnection();
            System.out.println("SQL Call");
        }
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.callStoredProcedure();
    }
}
