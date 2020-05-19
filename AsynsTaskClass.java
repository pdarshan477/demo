package com.orataro.services;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.edusunsoft.orataro.R;
import com.orataro.Interface.ICancleAsynkTask;
import com.orataro.Interface.ResponseWebServices;
import com.orataro.Model.Property_vo;
import com.orataro.util.LoaderProgress;
import com.orataro.util.Utility;

public class AsynsTaskClass extends AsyncTask<String, Integer, String> implements ICancleAsynkTask {
	ArrayList<Property_vo> arraylist;
	Context mContext;
	String result="";
	boolean isShowLoader;
	ResponseWebServices  iResult;
	private ProgressDialog progressDialog;
	boolean isInternetAvailable;
	String methodName;
	int i = 0;
	boolean isInternet;
	boolean extraParam;
	long startTimeMillSceond,endTimeMillSceond;
	private LoaderProgress LoaderProgress;

	boolean isCalling = true;
	boolean isCancleDialog = true;
	boolean isPersentageDialog = false;
	private ProgressDialog progress; 
	String servermessage =" [{\"message\":\"Server is not responding, Please Try after some time.\",\"success\":0}]";

	String Canclemessage =" [{\"message\":\"You are cancle this task.\",\"success\":0}]";
	
	Handler handler;
	Runnable runnable;
	private int prog;
	
	public AsynsTaskClass(Context mContext,ArrayList<Property_vo> arraylist,boolean isShowLoader,ResponseWebServices  iResult) {
		this.mContext= mContext;
		this.arraylist = arraylist;
		this.iResult = iResult;
		this.isShowLoader = isShowLoader;


	}
	public AsynsTaskClass(Context mContext,ArrayList<Property_vo> arraylist,boolean isShowLoader,ResponseWebServices  iResult,boolean isPersentageDialog,boolean isPersentageDialog1) {
		this.mContext= mContext;
		this.arraylist = arraylist;
		this.iResult = iResult;
		this.isShowLoader = isShowLoader;
		this.isPersentageDialog = isPersentageDialog;


	}
	public AsynsTaskClass(Context mContext,ArrayList<Property_vo> arraylist,boolean isShowLoader,ResponseWebServices  iResult,boolean extraParam,int i) {
		this.mContext= mContext;
		this.arraylist = arraylist;
		this.iResult = iResult;
		this.isShowLoader = isShowLoader;
		this.i = i;
		this.extraParam = extraParam;

	}
	public AsynsTaskClass(Context mContext,ArrayList<Property_vo> arraylist,boolean isShowLoader,ResponseWebServices  iResult,boolean isCancleDialog) {
		this.mContext= mContext;
		this.arraylist = arraylist;
		this.iResult = iResult;
		this.isShowLoader = isShowLoader;
		this.isCancleDialog = isCancleDialog;


	}

	@Override
	protected void onPreExecute() {
		if(isShowLoader){
			if(isPersentageDialog){
				try{
					if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
					progress=new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
					}else{
						progress=new ProgressDialog(mContext);	
					}
					progress.setMessage("Upload Image");
					progress.setMax(100);
		            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progress.setProgress(0);
					// Get the Drawable custom_progressbar                     
                    Drawable customDrawable= mContext.getResources().getDrawable(R.drawable.custom_progressbar);

                    // set the drawable as progress drawavle

                    progress.setProgressDrawable(customDrawable);
					progress.show();
					updateDialog();
					handler.sendEmptyMessage(0);
				}catch (Exception e){

				}
				//				final int totalProgressTime = 100;
				//				final Thread t = new Thread() {
				//					@Override
				//					public void run() {
				//						int jumpTime = 0;
				//
				//						while(jumpTime < totalProgressTime) {
				//							try {
				//								sleep(10);
				//								jumpTime += 5;
				//
				//								publishProgress(jumpTime);
				//								progress.setProgress(jumpTime);
				//
				//
				//							} catch (InterruptedException e) {
				//								// TODO Auto-generated catch block
				//								e.printStackTrace();
				//							}
				//						}
				//					}
				//				};t.start();

			}else{
				try{
					LoaderProgress = new LoaderProgress(mContext,this);
					LoaderProgress.setMessage(mContext.getResources().getString(R.string.pleasewait));

					LoaderProgress.setCancelable(isCancleDialog);

					LoaderProgress.show();
				}catch (Exception e){

				}
			}
		}

		startTimeMillSceond =System.currentTimeMillis();
		super.onPreExecute();

	}


	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if(isPersentageDialog){
			this.progress.setProgress(progress[0]);
		}else{
			//				progressDialog.setMessage("Loding..."+progress[0]+"%");
		}
		
		

	}
	@Override
	protected String doInBackground(String... params) {
		if(Utility.isNetworkAvailable(mContext)){

//			int myProgress=0;
//			while(myProgress<100){
//				myProgress++;
//				publishProgress(myProgress);
//				Log.v("timeprocess"
//						+ "", ""+myProgress);
//				SystemClock.sleep(10);
//			}
			if(isPersentageDialog){
				if(isShowLoader){
					if (progress != null) {
						publishProgress(0);
					}
				}
			}
			isInternetAvailable = true;
			SoapParser primary = new SoapParser(arraylist,
					params[0],//MethodName
					params[1]);//URL
			methodName=params[0]; 

			result = primary.buildData(mContext).toString();
		}else{
			isInternetAvailable = false;
		}
		//		 while (true)
		//		   {
		//		      if (isCancelled())  
		//		         break;
		//		   } 
		return result;
	}

	@Override
	protected void onPostExecute(String _result) {
		try{
			endTimeMillSceond = System.currentTimeMillis();
			if(ServiceResource.isShowLog) {
				Log.v("Time ", (endTimeMillSceond - startTimeMillSceond) + "");
			}

			if(isCalling){
				if(result.equalsIgnoreCase("")){
					result = servermessage;
				}
				if(isPersentageDialog){
					if(isShowLoader){
						if(progress != null  ){
							handler.removeCallbacks(runnable);
							progress.dismiss();
						}
					}
				}else{
					if(isShowLoader){
						if(LoaderProgress != null  ){
							LoaderProgress.dismiss();
						}
					}
				}
				if(!isInternetAvailable){
					Utility.showAlertDialog(mContext, mContext.getResources().getString(R.string.PleaseCheckyourinternetconnection));
				}else{
					if(iResult != null){
						this.iResult.response(result,methodName);
					}
				}
			}else{
				result = Canclemessage;
				if(!isInternetAvailable){
					Utility.showAlertDialog(mContext, mContext.getResources().getString(R.string.PleaseCheckyourinternetconnection));
				}else{
					if(iResult != null){
						this.iResult.response(result,methodName);
					}
				}
			}
		}catch (Exception e){

		}
		super.onPostExecute(_result);


	}
	@Override
	public void onCancleTask() {
		// TODO Auto-generated method stub
		//		isCalling = false;
		if(!this.isCancelled()){
			this.cancel(true);

		}
		//		this.cancel(true);


	}


	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	
	public void updateDialog()
	{
		handler=new Handler()
		{
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				if (prog >= 100) 
				{
//				    _progressDialog.dismiss();
				} else 
				{
				    prog++;
				    Log.e("Process", ""+prog);
				   // progress.incrementProgressBy(1);
				    handler.sendEmptyMessageDelayed(0, 100);
				    
				    publishProgress(prog);
				    
				}
			}
		};
		prog=0;
		
		
		
//		handler.postDelayed(runnable=new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				if(prog<100)
//				{
//					progress.setProgress(prog);
//					prog++;
//					handler.postDelayed(runnable, 10);
//				}
//				
//			}
//		}, 10);
		
		
	}
	
}
