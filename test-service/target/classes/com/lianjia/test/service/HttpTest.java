package com.lianjia.test.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by helloworld on 2017/3/19.
 */
public class HttpTest {

    public static void main(String[] args) throws ClientProtocolException, IOException {
        String url = "http://bj.lianjia.com/";
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);
        System.out.println("Response Code: " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        String line = "";
        while((line = rd.readLine()) != null) {
            System.out.println(line);
        }



        }
}
