
package net.saganetwork.sagaproxy.operations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Request {
    
    public static ArrayList<String> removeCommas(String input) {
        String[] splitted = input.split(",");
        ArrayList<String> result = new ArrayList<>(Arrays.asList(splitted));
        return result;
    }
    
    public ArrayList<String> makeHttpRequest(String urlString) {
        ArrayList<String> responseList = new ArrayList<>();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\n");

            while (scanner.hasNext()) {
                responseList.add(scanner.next());
            }

            scanner.close();
            connection.disconnect();
        } catch (IOException e) {
        }

        return responseList;
    }

}
