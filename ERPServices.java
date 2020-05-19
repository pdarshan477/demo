package com.orataro.services;

import android.util.Log;

import com.google.firebase.appindexing.internal.Thing;
import com.google.gson.Gson;
import com.orataro.Model.Property_vo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by admin on 12-05-2017.
 */

public class ERPServices  {

    ArrayList<Property_vo> list;
    String url ="";
    String methodName = "";
    public ERPServices(String url , ArrayList<Property_vo> list,String methodName
    ){
        this.url = url;
         this.list = list ;
        this.methodName = methodName;

    }

    public String ERPProjectCall(){
        String response = "";



        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpclient = new DefaultHttpClient();


            HttpPost httpPost = new HttpPost("http://beta.sunsofteduware.com/Services/ORATARO/apk_feeCollection.asmx?op=getstudentfeehistoryinfo");
//"http://dev.sentibeans.com/mrest/api/surveksan/questionnaire/" + questionnaireId + "/questions/answers/submit
            Log.v("json",new Gson().toJson(list));
            StringEntity se = new StringEntity(new Gson().toJson(list));

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("x-organizationId", "116");


            HttpResponse httpResponse = httpclient.execute(httpPost);


            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                response = convertStreamToString(inputStream);
            else
                response = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        Log.d("response", response);
        return response;


    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
