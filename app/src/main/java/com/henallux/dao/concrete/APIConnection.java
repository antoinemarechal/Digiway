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

class APIConnection
{
    static final String API_DATE_FORMAT = "yyyy-MM-dd";

    private static final String BASE_API_URL = "http://digiwayapi.azurewebsites.net/api/";

    private static APIConnection instance = null;

    static APIConnection getInstance()
    {
        if(instance == null)
            instance = new APIConnection();

        return instance;
    }

    private APIConnection() {}

    URLConnection connect(String urlAppendix) throws IOException
    {
        URL url = new URL(BASE_API_URL + urlAppendix);

        return url.openConnection();
    }

    String readFromConnection(URLConnection connection) throws IOException
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

    void writeToConnection(URLConnection connection, String message) throws IOException
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
