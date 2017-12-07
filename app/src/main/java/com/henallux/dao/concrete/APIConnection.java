package com.henallux.dao.concrete;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class APIConnection // TODO gérer 403 = api fermée
{
    private static final String BASE_API_URL = "http://digiwayapi.azurewebsites.net/api/";

    private static APIConnection instance = null;

    public static APIConnection getInstance()
    {
        if(instance == null)
            instance = new APIConnection();

        return instance;
    }

    private APIConnection() {}

    public URLConnection connect(String urlAppendix) throws IOException
    {
        URL url = new URL(BASE_API_URL + urlAppendix);

        return url.openConnection();
    }

    public String readFromConnection(URLConnection connection) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine()) != null)
        {
            sb.append(line);
        }

        br.close();

        return sb.toString();
    }

    public void writeToConnection(URLConnection connection, String message) throws IOException
    {
        OutputStream out = new BufferedOutputStream(connection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

        writer.write(message);
        writer.flush();
        writer.close();

        out.close();

        connection.connect();
    }
}
