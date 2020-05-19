package com.orataro.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;

import android.util.Log;

import com.orataro.util.Constants;

public class WebserviceCall {

	/**
	 * Variable Decleration................
	 * 
	 * 
	 */
	String namespace = "http://tempuri.org/";
	// private String url = "http://www.webservicex.net/ConvertWeight.asmx";

	String SOAP_ACTION;
	SoapObject request = null, objMessages = null;
	SoapSerializationEnvelope envelope;
	HttpTransportSE androidHttpTransport;
	private String result;

	long startTimeMillsceond,endTimeMilliSceond;
	/**
	 * Set Envelope
	 */
	protected void SetEnvelope(String url) {

		try {

			// Creating SOAP envelope
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

			// You can comment that line if your web service is not .NET one.
			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);
			androidHttpTransport = new HttpTransportSE(url);
			androidHttpTransport.debug = true;

		} catch (Exception e) {
			System.out.println("Soap Exception---->>>" + e.toString());
		}
	}

	// MethodName variable is define for which webservice function will call
	public String getJSONFromSOAPWS(String MethodName,
			HashMap<Integer, HashMap<String, String>> map, String url) {

		try {
			Log.v("url", url);
			Log.v("methodName", MethodName);
			startTimeMillsceond = System.currentTimeMillis();
			SOAP_ACTION = namespace + MethodName;

			// Adding values to request object
			request = new SoapObject(namespace, MethodName);

			Set<Integer> keys = map.keySet();
			for (Iterator<Integer> i = keys.iterator(); i.hasNext();) {
				Integer key = (Integer) i.next();
				HashMap<String, String> value = (HashMap<String, String>) map
						.get(key);

				Set<String> keysmap = value.keySet();
				for (Iterator<String> j = keysmap.iterator(); j.hasNext();) {
					String keyMapValue = (String) j.next();
					String valueMap = (String) value.get(keyMapValue);
					if (Constants.DATATYPE_INT == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Integer.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_STRING == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(String.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_DOUBLE == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Double.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_BYTE == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Byte.class);
						request.addProperty(pi);
					}

				}

				// System.out.println(key + " = " + value);
				// PropertyInfo pi =new PropertyInfo();
				// pi.setName(key);
				// pi.setValue(value);
				// pi.setType(String.class);
				// request.addProperty(pi);
			}

			/*
			 * for(int i= 0;i<propertyList.size();i++){ //Adding Double value to
			 * request object PropertyInfo weightProp =new PropertyInfo();
			 * weightProp.setName("Weight"); weightProp.setValue(weight);
			 * weightProp.setType(double.class);
			 * request.addProperty(weightProp); }
			 */

			// Adding String value to request object
			// request.addProperty("FromUnit", "" + fromUnit);
			// request.addProperty("ToUnit", "" + toUnit);

			SetEnvelope(url);

			try {


//				new SoapParser().allowAllSSL();


				String[] str=url.split("/");

//				HttpsTransportSE androidHttpTransport = new HttpsTransportSE(str[2],443, "/Services/"+ str[str.length-1].toString() +   "?op=" + MethodName.toString(), 30000);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(url, 30000);
				// SOAP calling webservice
				androidHttpTransport.call(SOAP_ACTION, envelope);


				// Got Webservice response
				result = envelope.getResponse().toString();
				Log.e(MethodName, result);
				endTimeMilliSceond = System.currentTimeMillis();
				Log.v("Time ", (endTimeMilliSceond-startTimeMillsceond)+"");

			} catch (Exception e) {
				// TODO: handle exception
				return e.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.toString();

		}
		return result;
	}

	public String getJSONFromSOAPWSWithBoolean(String MethodName,
			HashMap<Integer, HashMap<String, String>> map, String url,
			String keybool, boolean bool) {

		try {
			Log.v("url", url);
			Log.v("methodName", MethodName);
			SOAP_ACTION = namespace + MethodName;

			// Adding values to request object
			request = new SoapObject(namespace, MethodName);

			Set<Integer> keys = map.keySet();
			for (Iterator<Integer> i = keys.iterator(); i.hasNext();) {
				Integer key = (Integer) i.next();
				HashMap<String, String> value = (HashMap<String, String>) map
						.get(key);

				Set<String> keysmap = value.keySet();
				for (Iterator<String> j = keysmap.iterator(); j.hasNext();) {
					String keyMapValue = (String) j.next();
					String valueMap = (String) value.get(keyMapValue);
					if (Constants.DATATYPE_INT == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Integer.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_STRING == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(String.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_DOUBLE == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Double.class);
						request.addProperty(pi);
					}
				}

				// System.out.println(key + " = " + value);
				// PropertyInfo pi =new PropertyInfo();
				// pi.setName(key);
				// pi.setValue(value);
				// pi.setType(String.class);
				// request.addProperty(pi);
			}

			PropertyInfo pi = new PropertyInfo();
			pi.setName(keybool);
			pi.setValue(bool);
			pi.setType(Boolean.class);
			request.addProperty(pi);

			Log.v("param bool", keybool+"="+bool);

			/*
			 * for(int i= 0;i<propertyList.size();i++){ //Adding Double value to
			 * request object PropertyInfo weightProp =new PropertyInfo();
			 * weightProp.setName("Weight"); weightProp.setValue(weight);
			 * weightProp.setType(double.class);
			 * request.addProperty(weightProp); }
			 */

			// Adding String value to request object
			// request.addProperty("FromUnit", "" + fromUnit);
			// request.addProperty("ToUnit", "" + toUnit);

			SetEnvelope(url);

			try {
//				new SoapParser().allowAllSSL();


				String[] str=url.split("/");

//				HttpsTransportSE androidHttpTransport = new HttpsTransportSE(str[2],443, "/Services/"+ str[str.length-1].toString() +   "?op=" + MethodName.toString(), 30000);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(url, 30000);

				// SOAP calling webservice
				androidHttpTransport.call(SOAP_ACTION, envelope);

				Log.v("url", url);
				Log.v("methodName", MethodName);
				SOAP_ACTION = namespace + MethodName;


				// Got Webservice response
				String result = envelope.getResponse().toString();
				Log.v(MethodName, result);
				return result;

			} catch (Exception e) {
				// TODO: handle exception
				return e.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return e.toString();
		}

	}

	// MethodName variable is define for which webservice function will call
	public String getJSONFromSOAPWSFromImage(String MethodName,
			HashMap<Integer, HashMap<String, Object>> map, String url) {

		try {
			Log.v("url", url);
			Log.v("methodName", MethodName);
			SOAP_ACTION = namespace + MethodName;

			// Adding values to request object
			request = new SoapObject(namespace, MethodName);

			Set<Integer> keys = map.keySet();
			for (Iterator<Integer> i = keys.iterator(); i.hasNext();) {
				Integer key = (Integer) i.next();
				HashMap<String, Object> value = (HashMap<String, Object>) map
						.get(key);

				Set<String> keysmap = value.keySet();
				for (Iterator<String> j = keysmap.iterator(); j.hasNext();) {
					String keyMapValue = (String) j.next();
					Object valueMap = (Object) value.get(keyMapValue);
					if (Constants.DATATYPE_INT == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Integer.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_STRING == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(String.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_DOUBLE == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Double.class);
						request.addProperty(pi);
					} else if (Constants.DATATYPE_BYTE == key) {
						System.out.println(keyMapValue + " = " + valueMap);
						PropertyInfo pi = new PropertyInfo();
						pi.setName(keyMapValue);
						pi.setValue(valueMap);
						pi.setType(Byte.class);
						request.addProperty(pi);
					}

				}

				// System.out.println(key + " = " + value);
				// PropertyInfo pi =new PropertyInfo();
				// pi.setName(key);
				// pi.setValue(value);
				// pi.setType(String.class);
				// request.addProperty(pi);
			}

			/*
			 * for(int i= 0;i<propertyList.size();i++){ //Adding Double value to
			 * request object PropertyInfo weightProp =new PropertyInfo();
			 * weightProp.setName("Weight"); weightProp.setValue(weight);
			 * weightProp.setType(double.class);
			 * request.addProperty(weightProp); }
			 */

			// Adding String value to request object
			// request.addProperty("FromUnit", "" + fromUnit);
			// request.addProperty("ToUnit", "" + toUnit);

			SetEnvelope(url);

			try {


//				new SoapParser().allowAllSSL();

				


				String[] str=url.split("/");

//				HttpsTransportSE androidHttpTransport = new HttpsTransportSE(str[2],443, "/Services/"+ str[str.length-1].toString() +   "?op=" + MethodName.toString(), 30000);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(url, 30000);


				//				HttpsTransportSE androidHttpTransport = new HttpsTransportSE("orataro.com",443, "/Services/"+ c_url.split("/")[c_url.split("/").length-1].toString() +   "?op=" + c_method.toString(), 1000);



				// SOAP calling webservice
				androidHttpTransport.call(SOAP_ACTION, envelope);

				// Got Webservice response
				result = envelope.getResponse().toString();
				Log.e(MethodName, result);

			} catch (Exception e) {
				// TODO: handle exception
				return e.toString();
			}
		} catch (Exception e) {

			e.printStackTrace();
			return e.toString();

		}
		return result;
	}

}