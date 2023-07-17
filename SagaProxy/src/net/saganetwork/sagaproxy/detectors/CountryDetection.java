package net.saganetwork.sagaproxy.detectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.saganetwork.sagaproxy.operations.Request;

public class CountryDetection extends Request{

    public String getIPInfo(String address) {
        
        if (!address.equals("127.0.0.1")) { // Fixing java.lang.NullPointerException (It's maybe unnecessary beacuse im already fixed it in Main.java)

            String url = "http://ip-api.com/line/" + address + "?fields=status,countryCode,proxy,hosting";
            ArrayList<String> response = makeHttpRequest(url);  
            if (response.get(0).contains("success")) {
                return response.get(1) + "," + response.get(2) + "," + response.get(3);    
            }  
        }
 
        /*
        for (String line : response) {
            System.out.println(line);
        }
        */

        return "";
    }
    
    public ArrayList<String> getResultList(String input) {
        if (!input.isEmpty()) {
            ArrayList<String> resultList = removeCommas(input);
            return resultList;
        }
        return null;
    }    
}   
    
    
    
    
    
    

