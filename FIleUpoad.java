package com.orataro.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.util.Log;

import com.orataro.Model.Property_vo;
import com.orataro.util.UserSharedPrefrence;

public class FIleUpoad {
	
	public String connectToWebSvc(byte[] xmlInput,Context mContext,String filepath) 
	{
	    URL url = null;
	    URLConnection connection = null;
	    HttpURLConnection httpConn = null;
	    String responseString = null;
	    String outputString = null;
	    ByteArrayOutputStream bout = null;
	    OutputStream out = null;
	    InputStreamReader isr = null;
	    BufferedReader in = null;
	    String wsURL = ServiceResource.ADDPHOTO_URL;
	    try
	    {
	        url = new URL(wsURL);
	        connection = url.openConnection();
	        httpConn = (HttpURLConnection) connection;
	        bout = new ByteArrayOutputStream();
	      
	        byte[] b = xmlInput;
	        String SOAPAction =ServiceResource.UPLOAD_PHOTO_METHODNAME;
	        // Set the appropriate HTTP parameters.
	        httpConn.setRequestProperty("Content-Length", String
	                .valueOf(b.length));
	       
	        httpConn.addRequestProperty(ServiceResource.PHOTO_FILENAME, new File(
					filepath).getName().replace(" ", "-"));
			
	        httpConn.addRequestProperty(ServiceResource.PHOTO_CLIENTID,
					new UserSharedPrefrence(mContext).getLoginModel().getClientID());

	        httpConn.addRequestProperty(ServiceResource.PHOTO_INSTITUTEID,
					new UserSharedPrefrence(mContext).getLoginModel().getInstituteID());
			
	        httpConn.addRequestProperty(ServiceResource.PHOTO_FILETYPE,
						"IMAGE");
			
	        httpConn.addRequestProperty(ServiceResource.PHOTO_MEMBERID,
					new UserSharedPrefrence(mContext).getLoginModel().getMemberID());
			
//	        httpConn.setRequestProperty(ServiceResource.PHOTO_FILE, xmlInput);
	        httpConn.setRequestProperty("Content-Type",
	                "text/xml; charset=utf-8");
	        httpConn.setRequestProperty("SOAPAction", SOAPAction);
	        httpConn.setRequestMethod("POST");
	        httpConn.setDoOutput(true);
	        httpConn.setDoInput(true);
	        out = httpConn.getOutputStream();
	        out.write(b);
	        out.close();
	        // Read the response and write it to standard out.
	        isr = new InputStreamReader(httpConn.getInputStream());
	        in = new BufferedReader(isr);
	        while ((responseString = in.readLine()) != null) 
	        {
	            outputString = outputString + responseString;
	        }
	      Log.v("fgg[flg;fdg",outputString);
	    } 
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
		return outputString;
	}

}
