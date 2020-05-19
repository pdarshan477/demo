package com.orataro.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Evaluator;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.edusunsoft.orataro.Circular_Fragment;
import com.edusunsoft.orataro.ClassWorkListActivity;
import com.edusunsoft.orataro.FacebookWall;
import com.edusunsoft.orataro.GcmBroadcastReceiver;
import com.edusunsoft.orataro.HomeWorkListFragment;
import com.edusunsoft.orataro.LoginActivity;
import com.edusunsoft.orataro.My_Poll;
import com.edusunsoft.orataro.Notes_fragment;
import com.edusunsoft.orataro.R;
import com.edusunsoft.orataro.TodoListActivity;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orataro.DatabasePojo.Circularparser;
import com.orataro.DatabasePojo.ClassworkParse;
import com.orataro.DatabasePojo.HomeworkParse;
import com.orataro.DatabasePojo.NoteParse;
import com.orataro.DatabasePojo.OnlyPhotoVideoParse;
import com.orataro.DatabasePojo.PollAnsMainParse;
import com.orataro.DatabasePojo.PollAnsSubParse;
import com.orataro.DatabasePojo.PollParse;
import com.orataro.DatabasePojo.ReminderParse;
import com.orataro.Model.CircularModel;
import com.orataro.Model.ClassWorkModel;
import com.orataro.Model.DynamicWallSetting;
import com.orataro.Model.HomeWorkModel;
import com.orataro.Model.LoginModel;
import com.orataro.Model.NotesModel;
import com.orataro.Model.PollModel;
import com.orataro.Model.PollOptionModel;
import com.orataro.Model.Property_vo;
import com.orataro.Model.TodoListModel;
import com.orataro.Model.WallPostModel;
import com.orataro.adaapter.CircularListAdapter;
import com.orataro.adaapter.ClassworkAdapter;
import com.orataro.adaapter.HomeWorkListAdapter;
import com.orataro.adaapter.NotesListAdapter;
import com.orataro.adaapter.PollListAdapter;
import com.orataro.adaapter.SaprateWallType;
import com.orataro.adaapter.TodoListAdapter;
import com.orataro.customeview.DeviceHardWareId;
import com.orataro.database.DataBaseHelper;
import com.orataro.parse.LoginParse;
import com.orataro.util.Global;
import com.orataro.util.UserSharedPrefrence;
import com.orataro.util.Utility;

public class SyncService extends Service  {

	Context mContext;
	UserSharedPrefrence userSharedPrefrence ;
	ArrayList<String> listnewName;

	public SyncService(Context context) {
		super();
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public SyncService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		startTimer();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("EXIT", "ondestroy!");
		Intent broadcastIntent = new Intent(
				"com.edusunsoft.orataro.RestartSensor");
		sendBroadcast(broadcastIntent);
		stoptimertask();
	}

	private Timer timer;
	private TimerTask timerTask;
	long oldTime = 0;

	public void startTimer() {
		// set a new Timer
		timer = new Timer();

		// initialize the TimerTask's job
		initializeTimerTask();

		// schedule the timer, to wake up every 1 hour
		// 1000 = 1 secound
		//
		// 3600000 = 1 hour = 1000 *(60*60)

		timer.schedule(timerTask, 1000, 3600000); //
	}

	/**
	 * it sets the timer to print the counter every x seconds
	 */
	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			private String result;

			public void run() {
				// Log.i("in timer", "in timer ++++  "+ (counter++));
				userSharedPrefrence = new UserSharedPrefrence(
						getApplicationContext());
				if (Utility.isNetworkAvailable(getApplicationContext())) {
					if (!userSharedPrefrence.getLOGIN_USERID().equals("NAN")) {
						Log.v("services", "service start");
						if(Utility.isNetworkAvailable(getApplicationContext())){


							// Main Wall
							LoginTask();
							mainWallCall(1);
						}

						if(Utility.isNetworkAvailable(getApplicationContext())){
							addPostoffline();
						}

						if(Utility.isNetworkAvailable(getApplicationContext())){
							// Profile Wall
							profileWall(1);

						}
						if(Utility.isNetworkAvailable(getApplicationContext())){

							result = DynamicMenuList();
							writeToFile(result, ServiceResource.GETUSERDYNAMICMENUDATA_METHODNAME);
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							Circular();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							Homework();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							classwork();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							SchooltimingpageDetail();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							getTimeTable();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							getTimeTableTeacher();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							notelist();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							holiday();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							calenderEvent();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							notificationList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							TodoList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							getPollList();

						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							getPollListStudent();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							Blog();

						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							cmsPages();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							FriendsList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							albumList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							photoList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							GroupList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							projectList();
						}

						if(Utility.isNetworkAvailable(getApplicationContext())){
							pagePrayer();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							pageInformation();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							GetUserRoleRightList();
						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							notificationCount();
						}
//						if(Utility.isNetworkAvailable(getApplicationContext())){
//							poastALL(1);
//						}
//						if(Utility.isNetworkAvailable(getApplicationContext())){
//							profilewall(1);
//						}
						if(Utility.isNetworkAvailable(getApplicationContext())){
							if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){

							}else{
								Happygram();
							}
						}
						if (Utility.isNetworkAvailable(getApplicationContext())){
							Video(1,"IMAGE");
							Video(1,"VIDEO");
							Video(1,"FILE");
						}



					}
				}

			}


		};

	}

	/**
	 * not needed
	 */
	public void stoptimertask() {
		// stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void writeToFile(String data, String FileName) {

		//		String 	storagePath=Environment.getExternalStorageDirectory()+"/Android/data/"+"com.edusunsoft.orataro"+"/";
		//		File file = new File(storagePath, FileName+".txt");
		//		FileOutputStream stream = null;
		//		try {
		//			stream = new FileOutputStream(file);
		//		} catch (FileNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		try {
		//		    try {
		//				stream.write(data.getBytes());
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		} finally {
		//		    try {
		//				stream.close();
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					getApplicationContext().openFileOutput(FileName,
							Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}


//	public void poastALL(int i){
//
//
//		int count =i;
//		WebserviceCall webcall = new WebserviceCall();
//		HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
//
//		HashMap<String, String> map1 = new HashMap<String, String>();
//		map1.put(ServiceResource.WALLID, userSharedPrefrence.getLoginModel().getWallID());
//		map1.put(ServiceResource.MEMBERID,
//				userSharedPrefrence.getLoginModel().getMemberID());
//
//
//
//		HashMap<String, String> map2 = new HashMap<String, String>();
//
//
//
//		//			PROFILEWALL
//
//		map2.put(ServiceResource.WALL_ROWNOSMALL, String.valueOf(count));
//		map.put(1, map2);
//
//		map.put(2, map1);
//		if(Utility.isNetworkAvailable(getApplicationContext())){
//			String 	result = webcall.getJSONFromSOAPWS(ServiceResource.WALL_METHODNAME,
//					map, ServiceResource.WALL_URL);
//
//
//			writeToFile(result,userSharedPrefrence.getLoginModel().getWallID()+count);
//
//
//			parsingdata(result, ServiceResource.Wall, count, userSharedPrefrence.getLoginModel().getWallID());
//
//		}
//
//	}



	public void Video(int count,String fileType){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();

		arrayList.add(new Property_vo(ServiceResource.WALLID,
				userSharedPrefrence.getLoginModel().getWallID()));

		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));

		arrayList.add(new Property_vo("Count",
				count+""));


		if(ServiceResource.FILE.equals(fileType)){
			arrayList.add(new Property_vo("PostFilterType",
					"FILE"));
		}else if(ServiceResource.VIDEO.equals(fileType) ||ServiceResource.MP3.equalsIgnoreCase(fileType)){
			arrayList.add(new Property_vo("PostFilterType",
					"VIDEO"));
		}else{
			arrayList.add(new Property_vo("PostFilterType",
					"IMAGE"));
		}
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETPOSTED_FILEIMGAEVIDEOS_METHODNAME,
				ServiceResource.POST_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		onlyVideoParse(result,ServiceResource.UPDATE,count,fileType);


//			new AsynsTaskClass(mContext, arrayList, false,this).execute(ServiceResource.GETPOSTED_FILEIMGAEVIDEOS_METHODNAME,
//					ServiceResource.POST_URL);




	}


//	public void profilewall(int i){
//		int count =i;
//		WebserviceCall webcall = new WebserviceCall();
//		HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
//
//		HashMap<String, String> map1 = new HashMap<String, String>();
//		map1.put(ServiceResource.WALLID,userSharedPrefrence.getLoginModel().getWallID());
//		map1.put(ServiceResource.MEMBERID,
//				userSharedPrefrence.getLoginModel().getMemberID());
//
//
//
//		HashMap<String, String> map2 = new HashMap<String, String>();
//
//		map2.put(ServiceResource.WALL_ROWNOSMALL, String.valueOf(count));
//		map.put(1, map2);
//
//		map.put(2, map1);
//
//
//		if(Utility.isNetworkAvailable(getApplicationContext())){
//			String 	result = webcall.getJSONFromSOAPWS(ServiceResource.GETMYWALLDATA_METHODNAME,
//					map, ServiceResource.WALL_URL);
//			writeToFile(result, userSharedPrefrence.getLoginModel().getWallID()+""+count);
//			parsingdata(result, ServiceResource.PROFILEWALL, count, userSharedPrefrence.getLoginModel().getWallID());
//		}
//
//
//	}


	public void LoginTask(){

		//Login Param
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.LOGIN_USERNAME, userSharedPrefrence.getLOGIN_MOBILENUMBER()));
		arrayList.add(new Property_vo(ServiceResource.LOGIN_PASSWORD, userSharedPrefrence.getLOGIN_PASSWORD()));
		String regId ="";
		GcmBroadcastReceiver mReceiver = null;
		GoogleCloudMessaging gcmObj = null;
		String authorizedEntity = ServiceResource.SENDERID;
		String scope = "GCM";
		int PLAY_SERVICES_RESOLUTION_REQUEST = 1001;
		try{
			GCMRegistrar.checkDevice(mContext);
			GCMRegistrar.checkManifest(mContext);
			InstanceID.getInstance(mContext).deleteToken(authorizedEntity,scope);
			regId = GCMRegistrar.getRegistrationId(mContext);


			mContext.registerReceiver(mReceiver, new IntentFilter(
					ServiceResource.DISPLAY_MESSAGE_ACTION));
			// generateNotification(this, "");
			if (regId.equals("")) {
				if (Utility.checkPlayServices((Activity)mContext, PLAY_SERVICES_RESOLUTION_REQUEST)) {
					GCMRegistrar.register(mContext,
							ServiceResource.SENDERID);
				}
			}


			if (gcmObj == null) {
				gcmObj = GoogleCloudMessaging
						.getInstance(mContext.getApplicationContext());
			}
			regId = gcmObj.register(ServiceResource.SENDERID);
		} catch (Exception e) {
			Log.e("Message", "" + e);
		}

		if(ServiceResource.STOREDEVICEID){
			arrayList.add(new Property_vo(ServiceResource.LOGIN_GCMID, regId));
			if(DeviceHardWareId.getDviceHardWarId(mContext).equalsIgnoreCase("")){
				UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
				arrayList.add(new Property_vo(ServiceResource.LOGIN_DIVREGISTID,uid.randomUUID()));
			}else{
				arrayList.add(new Property_vo(ServiceResource.LOGIN_DIVREGISTID, DeviceHardWareId.getDviceHardWarId(mContext)));
			}
		}else{
			arrayList.add(new Property_vo(ServiceResource.LOGIN_GCMID, ""));
			arrayList.add(new Property_vo(ServiceResource.LOGIN_DIVREGISTID,""));
		}

		//			new AsynsTaskClass(mContext, arrayList, true, new ResponseWebServices() {
		//				
		//				@Override
		//				public void response(String result,String methodName) {}
		//			}).execute(ServiceResource.LOGINWITHGCM_METHODNAME,ServiceResource.LOGIN_URL);
		//		

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.LOGINWITHGCM_METHODNAME,
				ServiceResource.LOGIN_URL);

		String result = primary.buildData(getApplicationContext()).toString();

		parseLogin(result);

		//		new AsynsTaskClass(mContext, arrayList, false, LoginActivity.this).execute(ServiceResource.LOGINWITHGCM_METHODNAME,ServiceResource.LOGIN_URL);




	}
	public void parseLogin(String result){

		JSONArray jsonObj;
		boolean isLogin= true,isMoreChild,isValidDevice=true;
		String messagefail="";
		try {
			Global.userdataModel = new LoginModel();
			if (result.contains("\"success\":0")) {
				isLogin = false;
				jsonObj= new JSONArray(result);
				for(int i = 0;i<jsonObj.length();i++){
					JSONObject innerObj = jsonObj.getJSONObject(i);
					messagefail = innerObj.getString(ServiceResource.MESSAGE) ;
				}
				if(messagefail.contains("Server")){

				}else{
					messagefail ="";
				}
			} else {

				writeToFile(result, ServiceResource.LOGINWITHGCM_METHODNAME);
				jsonObj = new JSONArray(result);
				isLogin = true;
				// JSONArray detailArrray= jsonObj.getJSONArray("");
				Global.userdataModel.setLoginJson(result);
				if (jsonObj.length() == 1) {
					for (int i = 0; i < jsonObj.length(); i++) {
						JSONObject innerObj = jsonObj.getJSONObject(i);
						Global.userdataModel = new LoginModel();

						Global.userdataModel= LoginParse.PaserLoginModelFromJSONObject(innerObj,result);
						Global.userdataModel.setLoginJson(result);
						userSharedPrefrence.setLoginModel(Global.userdataModel);
						isLogin = true;
						isMoreChild = false;
						if(!(ServiceResource.GURUKULINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID))>=0)){
							if(ServiceResource.SINGLE_DEVICE_FLAG){
								if(!(ServiceResource.STATICINSTITUTEID.indexOf(Global.userdataModel.getInstituteID())>=0)){
									//								if(!Global.userdataModel.getInstituteID().equalsIgnoreCase(ServiceResource.STATICINSTITUTEID)){
									if(innerObj
											.getString(ServiceResource.DEVICEIDENTITY).equalsIgnoreCase( DeviceHardWareId.getDviceHardWarId(mContext))){
										isValidDevice = true;


									}else{
										isValidDevice = false;
									}
								}else{
									isValidDevice = true;
								}
							}else{
								isValidDevice = true;
							}
						}else{
							isLogin = false;
						}


						if(isValidDevice){
							Global.userdataModel = new LoginModel();
							Global.userdataModel=LoginParse.PaserLoginModelFromJSONObject(innerObj,result);
							Global.userdataModel.setLoginJson(result);
							userSharedPrefrence.setLoginModel(Global.userdataModel);
							userSharedPrefrence.setISREMEMBAR(true);
						}


					}
				} else {
					int count = 0;
					int posJson = 0;
					for (int i = 0; i < jsonObj.length(); i++) {
						JSONObject innerObj = jsonObj.getJSONObject(i);
						if(!(ServiceResource.GURUKULINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID))>=0)){
							if(ServiceResource.SINGLE_DEVICE_FLAG){
								if(!(ServiceResource.STATICINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID))>=0)){
									//								if(!innerObj
									//										.getString(ServiceResource.LOGIN_INSTITUTEID).equalsIgnoreCase(ServiceResource.STATICINSTITUTEID)){
									if(innerObj
											.getString(ServiceResource.DEVICEIDENTITY).equalsIgnoreCase(DeviceHardWareId.getDviceHardWarId(mContext))){
										isValidDevice = true;
										count++;
										posJson = i;


									}
								}else{
									count++;
									isValidDevice = true;
									posJson = i;
								}
							}else{
								count++;
								isValidDevice = true;
								posJson = i;
							}
						}
					}
					if(count==1){

						JSONObject innerObj = jsonObj.getJSONObject(posJson);
						Global.userdataModel = new LoginModel();

						Global.userdataModel=LoginParse.PaserLoginModelFromJSONObject(innerObj,result);
						Global.userdataModel.setLoginJson(result);
						userSharedPrefrence.setLoginModel(Global.userdataModel);
						userSharedPrefrence.setISREMEMBAR(true);

						isLogin = true;
						isMoreChild = false;
						if(!(ServiceResource.GURUKULINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID))>=0)){
							if(ServiceResource.SINGLE_DEVICE_FLAG){
								if(!(ServiceResource.STATICINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID))>=0)){
									//								if(!innerObj
									//										.getString(ServiceResource.LOGIN_INSTITUTEID).equalsIgnoreCase(ServiceResource.STATICINSTITUTEID)){
									if(innerObj
											.getString(ServiceResource.DEVICEIDENTITY).equalsIgnoreCase( DeviceHardWareId.getDviceHardWarId(mContext))){
										isValidDevice = true;
									}else{
										isValidDevice = false;
									}
								}else{
									isValidDevice = true;
								}
							}else{
								isValidDevice = true;
							}
						}else{
							isLogin = false;
						}

						if(isValidDevice){
							Global.userdataModel = new LoginModel();
							Global.userdataModel= LoginParse.PaserLoginModelFromJSONObject(innerObj,result);
							Global.userdataModel.setLoginJson(result);
							userSharedPrefrence.setLoginModel(Global.userdataModel);
							userSharedPrefrence.setISREMEMBAR(true);
						}


					}else if(count >1){


						jsonObj = new JSONArray(result);
						Global.userdataList = new ArrayList<>();
						// JSONArray detailArrray= jsonObj.getJSONArray("");
						for (int i = 0; i < jsonObj.length(); i++) {
							JSONObject innerObj = jsonObj.getJSONObject(i);
							LoginModel model = new LoginModel();
							model = LoginParse.PaserLoginModelFromJSONObject(innerObj, result);
							if (!(ServiceResource.GURUKULINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID)) >= 0)) {
								if (ServiceResource.SINGLE_DEVICE_FLAG) {
									if (!(ServiceResource.STATICINSTITUTEID.indexOf(innerObj.getString(ServiceResource.LOGIN_INSTITUTEID)) >= 0)) {
										//						if(!innerObj
										//								.getString(ServiceResource.LOGIN_INSTITUTEID).equalsIgnoreCase(ServiceResource.STATICINSTITUTEID)){
										if (innerObj
												.getString(ServiceResource.DEVICEIDENTITY).equalsIgnoreCase(DeviceHardWareId.getDviceHardWarId(mContext))) {
											Global.userdataList.add(model);
										} else {

										}
									} else {
										Global.userdataList.add(model);

									}
								} else {
									Global.userdataList.add(model);
								}
							} else {

							}

							if(Global.userdataList.size() > 1){
								new UserSharedPrefrence(mContext).setISMULTIPLE(true);
							}
							boolean isUserAvailable = false ;
							for(int k = 0;k< Global.userdataList.size();k++) {
								if (userSharedPrefrence.getLOGIN_MEMBERID().equalsIgnoreCase(Global.userdataList.get(k).getMemberID())) {
									userSharedPrefrence.setLoginModel(Global.userdataList.get(k));
									userSharedPrefrence.setISREMEMBAR(true);
									isUserAvailable = true;
								}
							}
							if(!isUserAvailable){
								if(Global.userdataList != null && Global.userdataList.size()>0) {
									userSharedPrefrence.setLoginModel(Global.userdataList.get(0));
									userSharedPrefrence.setISREMEMBAR(true);
								}
							}
						}




						isMoreChild = true;
						isLogin = true;
					}else {
						isValidDevice = false;
						userSharedPrefrence.clearPrefrence();
					}

				}
			}





		} catch (Exception e) {
			// TODO Auto-generated catch block
			isValidDevice = false;
			userSharedPrefrence.clearPrefrence();
			// isMoreChild = true;
			e.printStackTrace();
		}





	}



	public void parsingdata(String result,String from ,int count,String walid){
		JSONObject mJsonObject;
		JSONArray wallListJsonArray;
		JSONArray mainJsonArray;
		boolean isMoreData;
		if (result != null
				&& !result.toString().equals(
				"[{\"message\":\"No Data Found\",\"success\":0}]")) {

			try {

				if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL) || from.equalsIgnoreCase(ServiceResource.Wall)){
					mainJsonArray = new JSONArray(result);
					if(count == 1){
						//						dynamicSettingtrueall();
					}
				}else{
					JSONObject jobj=new JSONObject(result);
					mainJsonArray = jobj.getJSONArray(ServiceResource.WALL_POSTDATA);
					JSONArray jsonArray = jobj.getJSONArray(ServiceResource.WALL_ROLEDATA);
					JSONArray jsonArrayAdminsetting = jobj.optJSONArray(ServiceResource.WALLROLEDATA);
					if(count ==1){
						Global.DynamicWallSetting = new DynamicWallSetting();
						for(int i=0;i<jsonArray.length();i++){
							JSONObject innerObj = jsonArray.getJSONObject(i);
							Global.DynamicWallSetting.setIsAllowPeopleToLikeThisWall(innerObj.getString(ServiceResource.ISALLOWPEOPLETOLIKETHISWALL));
							Global.DynamicWallSetting.setIsAllowPeoplePostComment(innerObj.getString(ServiceResource.ISALLOWPEOPLEPOSTCOMMENT));
							Global.DynamicWallSetting.setIsAllowPeopleToShareComment(innerObj.getString(ServiceResource.ISALLOWPEOPLETOSHARECOMMENT));
							Global.DynamicWallSetting.setIsAllowPeopleToLikeAndDislikeComment(innerObj.getString(ServiceResource.ISALLOWPEOPLETOLIKEANDDISLIKECOMMENT));
							Global.DynamicWallSetting.setIsAllowPeopleToPostStatus(innerObj.getString(ServiceResource.ISALLOWPEOPLETOPOSTSTATUS));
							Global.DynamicWallSetting.setIsAllowPeopleToCreatePoll(innerObj.getString(ServiceResource.ISALLOWPEOPLETOCREATEPOLL));
							Global.DynamicWallSetting.setIsAllowPeopleToParticipateInPoll(innerObj.getString(ServiceResource.ISALLOWPEOPLETOPARTICIPATEINPOLL));
							Global.DynamicWallSetting.setIsAllowPeopleToInviteOtherPeople(innerObj.getString(ServiceResource.ISALLOWPEOPLETOINVITEOTHERPEOPLE));
							Global.DynamicWallSetting.setIsAllowPeopleToTagOnPost(innerObj.getString(ServiceResource.ISALLOWPEOPLETOTAGONPOST));
							Global.DynamicWallSetting.setIsAllowPeopleToUploadAlbum(innerObj.getString(ServiceResource.ISALLOWPEOPLETOUPLOADALBUM));
							Global.DynamicWallSetting.setIsAllowPeopleToPutGeoLocationOnPost(innerObj.getString(ServiceResource.ISALLOWPEOPLETOPUTGEOLOCATIONONPOST));
							Global.DynamicWallSetting.setIsAllowPeopleToPostDocument(innerObj.getString(ServiceResource.ISALLOWPEOPLETOPOSTDOCUMENT));
							Global.DynamicWallSetting.setIsAllowPeopleToPostVideos(innerObj.getString(ServiceResource.ISALLOWPEOPLETOPOSTVIDEOS));
							Global.DynamicWallSetting.setWallName(innerObj.getString(ServiceResource.WALLNAME));
							Global.DynamicWallSetting.setWallImage(innerObj.getString(ServiceResource.WALLIMAGE));
							Global.DynamicWallSetting.setWallID(innerObj.getString(ServiceResource.WALLID));
							Global.DynamicWallSetting.setIsAutoApprovePost(innerObj.getString(ServiceResource.ISAUTOAPPROVEPOST));
							Global.DynamicWallSetting.setIsAutoApprovePostStatus(innerObj.getString(ServiceResource.ISAUTOAPPROVEPOSTSTATUS));
							Global.DynamicWallSetting.setIsAutoApproveAlbume(innerObj.getString(ServiceResource.ISAUTOAPPROVEALBUME));
							Global.DynamicWallSetting.setIsAutoApproveVideos(innerObj.getString(ServiceResource.ISAUTOAPPROVEVIDEOS));
							Global.DynamicWallSetting.setIsAutoApproveDocument(innerObj.getString(ServiceResource.ISAUTOAPPROVEDOCUMENT));
							Global.DynamicWallSetting.setIsAutoApprovePoll(innerObj.getString(ServiceResource.ISAUTOAPPROVEPOLL));
							Global.DynamicWallSetting.setIsAdmin(innerObj.getString(ServiceResource.ISADMIN));
							Log.v("isAdmin", innerObj.getString(ServiceResource.ISADMIN));
							if(Global.DynamicWallSetting.getIsAdmin()){
								//								dynamicSettingtrueall();


							}


						}
						if(Global.DynamicWallSetting.getIsAdmin()){
							for(int i=0;i<jsonArrayAdminsetting.length();i++){
								JSONObject innerObj = jsonArray.getJSONObject(i);
								Global.DynamicWallSetting.setIsAllowPeopleToPostVideos(innerObj.getString(ServiceResource.ISALLOWPOSTVIDEO));
								Global.DynamicWallSetting.setIsAllowPeopleToPostDocument(innerObj.getString(ServiceResource.ISALLOWPOSTFILE));
								Global.DynamicWallSetting.setIsAllowPeopleToUploadAlbum(innerObj.getString(ServiceResource.ISALLOWPOSTALBUM));
								Global.DynamicWallSetting.setIsAllowPeopleToPostStatus(innerObj.getString(ServiceResource.ISALLOWPOSTSTATUS));
								Global.DynamicWallSetting.setIsAllowPeoplePostComment(innerObj.getString(ServiceResource.ISALLOWPOSTPHOTO));


							}
						}
					}
				}


				// JSONArray detailArrray= jsonObj.getJSONArray("");

				if(count == 1){
					Global.wallPostModels = new ArrayList<WallPostModel>();
				}

				for (int i = 0; i < mainJsonArray.length(); i++) {
					JSONObject innerObj = mainJsonArray.getJSONObject(i);
					WallPostModel wallPostModel = new WallPostModel();

					ContentValues cv = new ContentValues();

					wallPostModel.setPostCommentID(innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTID, innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));

					wallPostModel
							.setAssociationType(innerObj
									.getString(ServiceResource.WALL_ASSOCIATIONTYPE));

					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONTYPE, innerObj
							.getString(ServiceResource.WALL_ASSOCIATIONTYPE));

					if(from.equalsIgnoreCase(ServiceResource.Wall) || from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));
					}else{
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

					}
					wallPostModel.setIsLike(innerObj
							.getString(ServiceResource.WALL_ISLIKE));

					cv.put(DataBaseHelper.DATABASE_ISLIKE,innerObj
							.optString(ServiceResource.WALL_ISLIKE));

					wallPostModel.setFullName(innerObj
							.getString(ServiceResource.WALL_FULLNAME));

					cv.put(DataBaseHelper.DATABASE_FULLNAME,innerObj
							.optString(ServiceResource.WALL_FULLNAME));
					wallPostModel
							.setProfilePicture(innerObj
									.getString(ServiceResource.WALL_PROFILEPICTURE));

					cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,innerObj
							.optString(ServiceResource.WALL_PROFILEPICTURE));



					wallPostModel.setTotalLikes(innerObj
							.getString(ServiceResource.WALL_TOTALLIKES));

					cv.put(DataBaseHelper.DATABASE_TOTALLIKES,innerObj
							.optString(ServiceResource.WALL_TOTALLIKES));

					wallPostModel.setTotalDislike(innerObj
							.getString(ServiceResource.WALL_TOTALDISLIKE));

					cv.put(DataBaseHelper.DATABASE_TOTALDISLIKE,innerObj
							.optString(ServiceResource.WALL_TOTALDISLIKE));

					wallPostModel.setRowNo(innerObj
							.getString(ServiceResource.WALL_ROWNO_POST));

					cv.put(DataBaseHelper.DATABASE_ROWNO,innerObj
							.optString(ServiceResource.WALL_ROWNO_POST));

					wallPostModel.setPostCommentID(innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));
					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTID,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTID));

					wallPostModel.setWallID(innerObj
							.getString(ServiceResource.WALL_WALLID));
					cv.put(DataBaseHelper.DATABASE_WALLID,innerObj
							.optString(ServiceResource.WALL_WALLID));

					wallPostModel
							.setPostCommentTypesTerm(innerObj
									.getString(ServiceResource.WALL_POSTCOMMENTTYPESTERM));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTTYPESTERM,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTTYPESTERM));

					wallPostModel
							.setPostCommentNote(innerObj
									.getString(ServiceResource.WALL_POSTCOMMENTNOTE));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTNOTE,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTNOTE));


					wallPostModel.setTotalLikes(innerObj
							.getString(ServiceResource.WALL_TOTALLIKES));

					cv.put(DataBaseHelper.DATABASE_TOTALLIKES,innerObj
							.optString(ServiceResource.WALL_TOTALLIKES));

					wallPostModel.setTotalComments(innerObj
							.getString(ServiceResource.WALL_TOTALCOMMENTS));

					cv.put(DataBaseHelper.DATABASE_TOTALCOMMENTS,innerObj
							.optString(ServiceResource.WALL_TOTALCOMMENTS));

					wallPostModel.setTotalDislike(innerObj
							.getString(ServiceResource.WALL_TOTALDISLIKE));

					cv.put(DataBaseHelper.DATABASE_TOTALDISLIKE,innerObj
							.optString(ServiceResource.WALL_TOTALDISLIKE));

					wallPostModel.setDateOfPost(innerObj
							.getString(ServiceResource.WALL_DATEOFPOST));

					cv.put(DataBaseHelper.DATABASE_DATEOFPOST,innerObj
							.optString(ServiceResource.WALL_DATEOFPOST));

					wallPostModel.setAssociationID(innerObj
							.getString(ServiceResource.WALL_ASSOCIATIONID));

					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONID,innerObj
							.optString(ServiceResource.WALL_ASSOCIATIONID));

					wallPostModel.setPostedOn(innerObj
							.getString(ServiceResource.WALL_POSTEDON));
					cv.put(DataBaseHelper.DATABASE_POSTEDON,innerObj
							.optString(ServiceResource.WALL_POSTEDON));

					wallPostModel
							.setAssociationType(innerObj
									.getString(ServiceResource.WALL_ASSOCIATIONTYPE));
					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONTYPE,innerObj
							.optString(ServiceResource.WALL_ASSOCIATIONTYPE));


					wallPostModel.setFullName(innerObj
							.getString(ServiceResource.WALL_FULLNAME));
					cv.put(DataBaseHelper.DATABASE_FULLNAME,innerObj
							.optString(ServiceResource.WALL_FULLNAME));


					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

					}else{
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
					}
					wallPostModel.setIsLike(innerObj
							.getString(ServiceResource.WALL_ISLIKE));

					cv.put(DataBaseHelper.DATABASE_ISLIKE,innerObj
							.optString(ServiceResource.WALL_ISLIKE));

					//						if(count == 1){
					if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL) || from.equalsIgnoreCase(ServiceResource.Wall)){
						if(innerObj.getString(ServiceResource.IS_USER_LIKE_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_LIKE).equalsIgnoreCase("1")){
								wallPostModel.setUserLike(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,1);

							}else{
								wallPostModel.setUserLike(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
							}
						}else{
							wallPostModel.setUserLike(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_DISLIKE_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_DISLIKE).equalsIgnoreCase("1")){
								wallPostModel.setUserDisLike(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,1);
							}else{
								wallPostModel.setUserDisLike(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);

							}
						}else{
							wallPostModel.setUserDisLike(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
								wallPostModel.setUserComment(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,1);

							}else{
								wallPostModel.setUserComment(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);

							}
						}else{
							wallPostModel.setUserComment(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_SHARE_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_SHARE).equalsIgnoreCase("1")){
								wallPostModel.setUserShare(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,1);
							}else{
								wallPostModel.setUserShare(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
							}
						}
						else{
							wallPostModel.setUserShare(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
						}
					}else{
						if(Global.DynamicWallSetting.getIsAllowPeopleToLikeThisWall()){
							if(innerObj.getString(ServiceResource.IS_USER_LIKE_WALL).equalsIgnoreCase("1")){
								if(innerObj.getString(ServiceResource.IS_USER_LIKE).equalsIgnoreCase("1")){
									wallPostModel.setUserLike(true);
									cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,1);
								}else{
									wallPostModel.setUserLike(false);
									cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
								}
							}
							else{
								wallPostModel.setUserLike(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
							}
						}
						else{
							wallPostModel.setUserLike(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
						}
						if(Global.DynamicWallSetting.getIsAllowPeopleToLikeThisWall()){
							if(innerObj.getString(ServiceResource.IS_USER_DISLIKE_WALL).equalsIgnoreCase("1")){
								if(innerObj.getString(ServiceResource.IS_USER_DISLIKE).equalsIgnoreCase("1")){
									wallPostModel.setUserDisLike(true);
									cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,1);
								}	else{
									wallPostModel.setUserDisLike(false);
									cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
								}
							}else{
								wallPostModel.setUserDisLike(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
							}
						}
						else{
							wallPostModel.setUserDisLike(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
						}
						if(Global.DynamicWallSetting.getIsAllowPeoplePostComment()){
							if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
								if(innerObj.getString(ServiceResource.IS_USER_COMMENT).equalsIgnoreCase("1")){
									wallPostModel.setUserComment(true);
									cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,1);
								}	else{
									wallPostModel.setUserComment(false);
									cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);

								}
							}else{
								wallPostModel.setUserComment(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
							}
						}
						else{
							wallPostModel.setUserComment(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);

						}
						//							if(Global.DynamicWallSetting.get){
						if(innerObj.getString(ServiceResource.IS_USER_SHARE_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_SHARE).equalsIgnoreCase("1")){
								wallPostModel.setUserShare(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,1);
							}
							//							}
							else{
								wallPostModel.setUserShare(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
							}
						}else{
							wallPostModel.setUserShare(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
						}
					}

					//						}

					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						wallPostModel
								.setSendToMemberID(innerObj
										.getString(ServiceResource.WALL_SENDTOMEMBERID));

						cv.put(DataBaseHelper.DATABASE_SENDTOMEMBERID,innerObj
								.getString(ServiceResource.WALL_SENDTOMEMBERID));

					}else{
						wallPostModel
								.setSendToMemberID(innerObj
										.getString(ServiceResource.MEMBERID));
						cv.put(DataBaseHelper.DATABASE_SENDTOMEMBERID,innerObj
								.getString(ServiceResource.MEMBERID));

					}
					wallPostModel
							.setProfilePicture(innerObj
									.getString(ServiceResource.WALL_PROFILEPICTURE));

					cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,innerObj
							.getString(ServiceResource.WALL_PROFILEPICTURE));

					wallPostModel.setPhoto(innerObj
							.getString(ServiceResource.WALL_PHOTO));
					cv.put(DataBaseHelper.DATABASE_PHOTO,innerObj
							.getString(ServiceResource.WALL_PHOTO));


					wallPostModel.setPostDate(innerObj
							.optString(ServiceResource.WALL_POSTDATE));
					cv.put(DataBaseHelper.DATABASE_POSTDATE,innerObj
							.optString(ServiceResource.WALL_POSTDATE));


					wallPostModel.setFileType(innerObj
							.getString(ServiceResource.WALL_FILETYPE));

					cv.put(DataBaseHelper.DATABASE_FILETYPE,innerObj
							.getString(ServiceResource.WALL_FILETYPE));


					wallPostModel.setFileMimeType(innerObj
							.getString(ServiceResource.WALL_FILEMIMETYPE));
					cv.put(DataBaseHelper.DATABASE_FILEMIMETYPE,innerObj
							.getString(ServiceResource.WALL_FILEMIMETYPE));



					if (wallPostModel.getPhoto() != null
							&& !wallPostModel.getPhoto().equals("")){
						wallPostModel.setPhoto(ServiceResource.BASE_IMG_URL
								+ "DataFiles/"
								+ wallPostModel.getPhoto().replaceAll(
								"DataFiles/", ""));

						cv.put(DataBaseHelper.DATABASE_PHOTO,ServiceResource.BASE_IMG_URL
								+ "DataFiles/"
								+ wallPostModel.getPhoto().replaceAll(
								"DataFiles/", ""));

					}
					if (wallPostModel.getProfilePicture() != null
							&& !wallPostModel.getProfilePicture()
							.equals("")) {

						if (wallPostModel.getProfilePicture().contains(
								"/img/"))
							wallPostModel
									.setProfilePicture(ServiceResource.BASE_IMG_URL
											+ wallPostModel
											.getProfilePicture()
											.replaceAll(
													"DataFiles/",
													""));

						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));

					}
					else if (wallPostModel.getProfilePicture().contains(
							"/AvtarImages/")){
						wallPostModel
								.setProfilePicture(ServiceResource.BASE_IMG_URL
										+ wallPostModel
										.getProfilePicture()
										.replaceAll(
												"DataFiles/",
												""));
						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));

					}
					else{
						wallPostModel
								.setProfilePicture(ServiceResource.BASE_IMG_URL
										+ "/DataFiles/"
										+ wallPostModel
										.getProfilePicture()
										.replaceAll(
												"DataFiles/",
												""));
						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ "/DataFiles/"
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));


					}

					cv.put(DataBaseHelper.DATABASE_WALLIDPARSE, wallPostModel.getWallID());
					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						cv.put(DataBaseHelper.DATABASE_METHODNAME,ServiceResource.Wall+wallPostModel.getWallID() );
					}else if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
						cv.put(DataBaseHelper.DATABASE_METHODNAME, ServiceResource.PROFILEWALL+wallPostModel.getWallID());
					}
					else {
						cv.put(DataBaseHelper.DATABASE_METHODNAME, ServiceResource.GETDYNAMICWALLDATA_METHODNAME+wallPostModel.getWallID());
					}
					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					//					Global.wallPostModels.add(wallPostModel);


					if(dbHelper.update(DataBaseHelper.TBL_ALLPOST, cv, DataBaseHelper.DATABASE_POSTCOMMENTID+"='"+wallPostModel.getPostCommentID()+"'")==0){
						dbHelper.insert(DataBaseHelper.TBL_ALLPOST, cv);
						Log.v("post", "insert");
					}else{
						Log.v("post", "update");
					}

				}

			} catch (JSONException e) {
				isMoreData = false;
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			isMoreData = true;

		} else {
			isMoreData = false;
		}

		if(isMoreData){
			count = count +10;
			if(from.equalsIgnoreCase(ServiceResource.Wall)){

				//poastALL(count);
			}else if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
				///	profileWall(count);
			}
		}
	}

	public void notificationCount(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.NOTIFICATIONCOUNT_METHODNAME,
				ServiceResource.NOTIFICATION_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.NOTIFICATIONCOUNT_METHODNAME);


		//				
		//		new AsynsTaskClass(mContext, arrayList, false, this).execute(ServiceResource.NOTIFICATIONCOUNT_METHODNAME,
		//				ServiceResource.NOTIFICATION_URL);

	}
	public String  DynamicMenuList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));

		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel()	.getInstituteID()));
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					null));
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					null));
		}else{
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					userSharedPrefrence.getLoginModel().getGradeId()));
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					userSharedPrefrence.getLoginModel().getDivisionId()));
		}
		arrayList.add(new Property_vo(
				ServiceResource.GETUSERDYNAMICMENUDATA_ROLENAME,
				userSharedPrefrence.getLoginModel().getMemberType()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETUSERDYNAMICMENUDATA_METHODNAME,
				ServiceResource.LOGIN_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GETUSERDYNAMICMENUDATA_METHODNAME);
		//		new AsynsTaskClass(SyncService.this, arrayList, true, this).
		//		execute(ServiceResource.GETUSERDYNAMICMENUDATA_METHODNAME,
		//				ServiceResource.LOGIN_URL);
		return result;
	}

	public void mainWallCall(int count){
		WebserviceCall webcall = new WebserviceCall();
		HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put(ServiceResource.WALLID, userSharedPrefrence
				.getLoginModel().getWallID());
		map1.put(ServiceResource.MEMBERID, userSharedPrefrence
				.getLoginModel().getMemberID());

		HashMap<String, String> map2 = new HashMap<String, String>();

		// PROFILEWALL
		// if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
		map2.put(ServiceResource.WALL_ROWNOSMALL,
				String.valueOf(count));
		map.put(1, map2);

		map.put(2, map1);
		String result = webcall.getJSONFromSOAPWS(
				ServiceResource.WALL_METHODNAME, map,
				ServiceResource.WALL_URL);

		writeToFile(result, userSharedPrefrence.getLoginModel()
				.getWallID());
		parsingdata1(result,ServiceResource.UPDATE,count,ServiceResource.Wall,userSharedPrefrence.getLoginModel()
				.getWallID());
	}

	public void profileWall(int count ){
		WebserviceCall webcall = new WebserviceCall();
		HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put(ServiceResource.WALLID, userSharedPrefrence
				.getLoginModel().getProfileWallId());
		map1.put(ServiceResource.MEMBERID, userSharedPrefrence
				.getLoginModel().getMemberID());
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put(ServiceResource.WALL_ROWNOSMALL,
				String.valueOf(count));
		map.put(1, map2);

		map.put(2, map1);
		String result = webcall.getJSONFromSOAPWS(
				ServiceResource.GETMYWALLDATA_METHODNAME, map,
				ServiceResource.WALL_URL);

		writeToFile(result, userSharedPrefrence.getLoginModel()
				.getProfileWallId());
		parsingdata1(result,ServiceResource.UPDATE,count,ServiceResource.PROFILEWALL, userSharedPrefrence
				.getLoginModel().getProfileWallId());
	}


	public void Circular(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,userSharedPrefrence
				.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.USER_ID, userSharedPrefrence
				.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence
						.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence
						.getLoginModel().getInstituteID()));
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					null));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					null));
		}else{
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					userSharedPrefrence
							.getLoginModel().getDivisionId()));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					userSharedPrefrence
							.getLoginModel().getGradeId()));

		}
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.CIRCULAR_METHODNAME,
				ServiceResource.CIRCULAR_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.CIRCULAR_METHODNAME);
		parseCircular(result,ServiceResource.UPDATE);
		//				String result = webcall.getJSONFromSOAPWS(
		//						ServiceResource.CIRCULAR_METHODNAME, map,
		//						ServiceResource.CIRCULAR_URL);
		//		new AsynsTaskClass(mContext, arrayList, true,this).execute(ServiceResource.CIRCULAR_METHODNAME,
		//				ServiceResource.CIRCULAR_URL);


	}

	public void Homework(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					null));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					null));
		}else{
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					userSharedPrefrence.getLoginModel().getDivisionId()));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					userSharedPrefrence.getLoginModel().getGradeId()));

		}
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));


		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.HOMEWORK_METHODNAME,
				ServiceResource.HOMEWORK_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.HOMEWORK_METHODNAME);
		parseHomework(result,ServiceResource.UPDATE);
		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.HOMEWORK_METHODNAME,
		//				ServiceResource.HOMEWORK_URL);

	}
	public void classwork(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID, userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.USER_ID,  userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID, userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID, userSharedPrefrence.getLoginModel().getInstituteID()));
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					null));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					null));
		}else{
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					userSharedPrefrence.getLoginModel().getDivisionId()));
			arrayList.add(new Property_vo(ServiceResource.GRADEID,
					userSharedPrefrence.getLoginModel().getGradeId()));

		}
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID,null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.CLASSWORK_METHODNAME,
				ServiceResource.CLASSWORK_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.CLASSWORK_METHODNAME);
		parseNote(result,ServiceResource.UPDATE);
		//		result =webcall.getJSONFromSOAPWS(ServiceResource.CLASSWORK_METHODNAME,map , ServiceResource.CLASSWORK_URL);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.CLASSWORK_METHODNAME, ServiceResource.CLASSWORK_URL);

	}

	public void SchooltimingpageDetail(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.CMSPAGE_ID,
				userSharedPrefrence.getLoginModel().getSchoolTiming()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
				ServiceResource.CMSPAGE_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, userSharedPrefrence.getLoginModel().getSchoolTiming());

		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.GETCMSPAGEDETAIL_METHODNAME, map,
		//				ServiceResource.CMSPAGE_URL);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
		//				ServiceResource.CMSPAGE_URL);
	}




	public void getTimeTableTeacher(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();

		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				/*"041d796d-de2f-4fcf-998f-7e4867b954b9"*/userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				/*"0146bb5b-9fd9-4fd7-b3bc-1c5eea9f634c"*/userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				/*"dfa3bb9b-51c6-4247-8285-61c5a5386181"*/userSharedPrefrence.getLoginModel().getMemberID()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.TIMETABLETEACHER_METHODNAME,
				ServiceResource.TIMETABLE_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.TIMETABLETEACHER_METHODNAME);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.TIMETABLETEACHER_METHODNAME,
		//				ServiceResource.TIMETABLE_URL);

	}

	public void getTimeTable(){

		int error = 0;
		try {
			ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
			arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
					userSharedPrefrence.getLoginModel().getClientID()));
			arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
					userSharedPrefrence.getLoginModel().getInstituteID()));
			if(userSharedPrefrence.getLoginModel().getUserType() != ServiceResource.USER_TEACHER_INT){
				arrayList.add(new Property_vo(ServiceResource.GRADEID,
						userSharedPrefrence.getLoginModel().getGradeId()));
				arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
						userSharedPrefrence.getLoginModel().getDivisionId()));
				Log.v("name", "");
			}else{
				Log.v("Teacher", "");
				arrayList.add(new Property_vo(ServiceResource.GRADEID,
						null));
				arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
						null));
			}

			SoapParser primary = new SoapParser(arrayList,
					ServiceResource.TIMETABLE_METHODNAME,
					ServiceResource.TIMETABLE_URL);

			String result = primary.buildData(getApplicationContext()).toString();
			writeToFile(result, ServiceResource.TIMETABLE_METHODNAME);
			//
			//			SoapParser primary = new SoapParser(arrayList,
			//					ServiceResource.TIMETABLE_METHODNAME,
			//					ServiceResource.TIMETABLE_URL);
			//			new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.TIMETABLE_METHODNAME,
			//					ServiceResource.TIMETABLE_URL);

			//			Log.e("Update Profile", result);
		} catch (Exception e) {
			// error = "Error Occures";
			error = 1;
		}

	}


	public void notelist(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();

		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.USER_ID, userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));
		arrayList.add(new Property_vo(ServiceResource.LOGIN_ROLENAME,
				userSharedPrefrence.getLoginModel().getMemberType()));
		arrayList.add(new Property_vo(ServiceResource.GRADEID, userSharedPrefrence.getLoginModel().getGradeId()));
		arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
				userSharedPrefrence.getLoginModel().getDivisionId()));
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.NOTES_METHODNAME,
				ServiceResource.NOTES_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.NOTES_METHODNAME);
		parseNote(result,ServiceResource.UPDATE);
		//		new AsynsTaskClass(getActivity(), arrayList, true, this)
		//		.execute(ServiceResource.NOTES_METHODNAME,
		//				ServiceResource.NOTES_URL);
	}

	public void holiday(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.LOGIN_CLIENTID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.LOGIN_INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.LOGIN_USERID,
				userSharedPrefrence.getLoginModel().getUserID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.HOLIDAY_METHODNAME,
				ServiceResource.HOLIDAY_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.HOLIDAY_METHODNAME);

		//	result = webcall.getJSONFromSOAPWS(
		//			ServiceResource.HOLIDAY_METHODNAME, map,
		//			ServiceResource.HOLIDAY_URL);
		//		new AsynsTaskClass(mContext, arrayList, true, this).
		//		execute(	ServiceResource.HOLIDAY_METHODNAME,
		//				ServiceResource.HOLIDAY_URL);
	}

	public void calenderEvent (){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		Calendar c = Calendar.getInstance();
		arrayList.add(new Property_vo(ServiceResource.CALENDERDATA_YEAR,c.get(Calendar.YEAR)));
		arrayList.add(new Property_vo(ServiceResource.CALENDERDATA_CLIENTID, userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.CALENDERDATA_INSTITUTEID,userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.CALENDERDATA_MEMBERID, userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CALENDERDATA_BEACHID,null));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.CALENDERDATA_METHODNAME,
				ServiceResource.CALENDERDATA_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.CALENDERDATA_METHODNAME);
		//		new AsynsTaskClass(getActivity(), arrayList, true, this).execute(ServiceResource.CALENDERDATA_METHODNAME,ServiceResource.CALENDERDATA_URL);
	}


	private void notificationList() {
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));//"d6a616bc-26ec-471e-ba16-1ee310f87dfa"/*userSharedPrefrence.getLoginModel().getMemberID()*/));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));//"041d796d-de2f-4fcf-998f-7e4867b954b9"/*userSharedPrefrence.getLoginModel().getClientID()*/	));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));//"0146bb5b-9fd9-4fd7-b3bc-1c5eea9f634c"/*userSharedPrefrence.getLoginModel().getInstituteID()*/));
		arrayList.add(new Property_vo(ServiceResource.NOTIFICATIONS_ROWCOUNT,
				1));
		arrayList.add(new Property_vo(ServiceResource.NOTIFICATIONS_ISVIEW,
				null));


		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.NOTIFICATIONGETLIST_METHODNAME,
				ServiceResource.NOTIFICATIONS_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.NOTIFICATIONGETLIST_METHODNAME);


	}

	private void TodoList() {
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();


		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));

		arrayList.add(new Property_vo(ServiceResource.USER_ID, userSharedPrefrence.getLoginModel().getUserID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.TODO_METHODNAME,
				ServiceResource.TODO_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.TODO_METHODNAME);
		parseTodoList(result,ServiceResource.UPDATE);
		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.TODO_METHODNAME, 
		//				ServiceResource.TODO_URL);
	}


	public void getPollList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.USER_ID,
				userSharedPrefrence.getLoginModel().getUserID()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETPOLLSLISTFORADDPAGE_METHODNAME,
				ServiceResource.POLL_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GETPOLLSLISTFORADDPAGE_METHODNAME);
		ParseAddpage(result,ServiceResource.UPDATE);
		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETPOLLSLISTFORADDPAGE_METHODNAME,
		//				ServiceResource.POLL_URL);
	}

	public void getPollListStudent(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID,
				userSharedPrefrence.getLoginModel().getBatchId()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETPOLLSLISTFORPARTICIPANTPAGE_METHODNAME,
				ServiceResource.POLL_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GETPOLLSLISTFORPARTICIPANTPAGE_METHODNAME);
		parseaddparticularpage(result,ServiceResource.UPDATE);
		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETPOLLSLISTFORPARTICIPANTPAGE_METHODNAME,
		//				ServiceResource.POLL_URL);
	}


	public void cmsPages(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));

		arrayList.add(new Property_vo(ServiceResource.USER_ID,userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID,
				null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETCMSPAGES_METHODNAME,
				ServiceResource.CMSPAGE_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GETCMSPAGES_METHODNAME);

		//		new AsynsTaskClass(mContext, arrayList, true, this)
		//		.execute(ServiceResource.GETCMSPAGES_METHODNAME,ServiceResource.CMSPAGE_URL);
	}
	public void Blog(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));

		arrayList.add(new Property_vo(ServiceResource.USER_ID,userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID,
				null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETBLOGLIST_METHODNAME,
				ServiceResource.BLOG_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GETBLOGLIST_METHODNAME);
		//		new AsynsTaskClass(mContext, arrayList, true, this)
		//		.execute(ServiceResource.GETBLOGLIST_METHODNAME,ServiceResource.BLOG_URL);
	}

	public void FriendsList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.FRIENDS_METHODNAME,
				ServiceResource.FRIENDS_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.FRIENDS_METHODNAME);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.FRIENDS_METHODNAME,
		//				ServiceResource.FRIENDS_URL);


	}
	public void albumList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();

		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.ALBUM_METHODNAME,
				ServiceResource.ALBUM_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.ALBUM_METHODNAME);

		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.ALBUM_METHODNAME, map,
		//				ServiceResource.ALBUM_URL);

		//		new AsynsTaskClass(getActivity(), arrayList, true, this).execute(ServiceResource.ALBUM_METHODNAME,
		//				ServiceResource.ALBUM_URL);
	}
	public void photoList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.PHOTO_POST_WALLID,
				userSharedPrefrence.getLoginModel().getWallID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.USER_ID,
				userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GET_PHOTO_METHODNAME,
				ServiceResource.PHOTO_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GET_PHOTO_METHODNAME);

		//		new AsynsTaskClass(getActivity(), arrayList, true, this).execute(ServiceResource.GET_PHOTO_METHODNAME,
		//				ServiceResource.PHOTO_URL);
	}
	public void GroupList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.USER_ID, userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GROUP_METHODNAME,
				ServiceResource.GROUP_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.GROUP_METHODNAME);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GROUP_METHODNAME,
		//				ServiceResource.GROUP_URL);

		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.GROUP_METHODNAME, map,
		//				ServiceResource.GROUP_URL);
	}



	public void projectList(){

		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.USER_ID, userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID,
				null));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.PROJECT_METHODNAME,
				ServiceResource.PROJECT_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, ServiceResource.PROJECT_METHODNAME);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.PROJECT_METHODNAME,
		//				ServiceResource.PROJECT_URL);


	}

	public void pagePrayer(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.CMSPAGE_ID,
				userSharedPrefrence.getLoginModel().getPrayer()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
				ServiceResource.CMSPAGE_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, userSharedPrefrence.getLoginModel().getPrayer());

		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.GETCMSPAGEDETAIL_METHODNAME, map,
		//				ServiceResource.CMSPAGE_URL);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
		//				ServiceResource.CMSPAGE_URL);
	}


	public void pageInformation(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.CMSPAGE_ID,
				userSharedPrefrence.getLoginModel().getInformation()));
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
				ServiceResource.CMSPAGE_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result, userSharedPrefrence.getLoginModel().getInformation());

		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.GETCMSPAGEDETAIL_METHODNAME, map,
		//				ServiceResource.CMSPAGE_URL);

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETCMSPAGEDETAIL_METHODNAME,
		//				ServiceResource.CMSPAGE_URL);
	}

	public void Happygram(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.MEMBERID,
				userSharedPrefrence.getLoginModel().getMemberID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
				userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
				userSharedPrefrence.getLoginModel().getInstituteID()));
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			//			arrayList.add(new Property_vo(ServiceResource.GRADEID,gradeId));
			//			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
			//					divisionId));
		}else{
			arrayList.add(new Property_vo(ServiceResource.GRADEID, userSharedPrefrence.getLoginModel().getGradeId()));
			arrayList.add(new Property_vo(ServiceResource.DIVISIONID,
					userSharedPrefrence.getLoginModel().getDivisionId()));

		}
		if(userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_TEACHER_INT){
			arrayList.add(new Property_vo(ServiceResource.ISSTUDNETS,
					false));
		}else{
			arrayList.add(new Property_vo(ServiceResource.ISSTUDNETS,
					true));
		}
		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETHAPPYGRAM_METHODNAME,
				ServiceResource.HAPPYGRAM_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result,	ServiceResource.GETHAPPYGRAM_METHODNAME);

		//		arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));

		//		new AsynsTaskClass(mContext, arrayList, true, this).execute(ServiceResource.GETHAPPYGRAM_METHODNAME,
		//				ServiceResource.HAPPYGRAM_URL);


		//		result = webcall.getJSONFromSOAPWS(
		//				ServiceResource.HAPPYGRAM_METHODNAME, map,
		//				ServiceResource.HAPPYGRAM_URL);
	}

	public void GetUserRoleRightList(){
		ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
		arrayList.add(new Property_vo(ServiceResource.USER_ID,userSharedPrefrence.getLoginModel().getUserID()));
		arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,userSharedPrefrence.getLoginModel().getClientID()));
		arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,userSharedPrefrence.getLoginModel().getInstituteID()));
		arrayList.add(new Property_vo(ServiceResource.LOGIN_ROLLID,userSharedPrefrence.getLoginModel().getRoleID()));

		SoapParser primary = new SoapParser(arrayList,
				ServiceResource.GETUSERROLERIGHTLIST_METHODNAME,
				ServiceResource.LOGIN_URL);

		String result = primary.buildData(getApplicationContext()).toString();
		writeToFile(result,	ServiceResource.GETUSERROLERIGHTLIST_METHODNAME);
		//		new AsynsTaskClass(mContext, arrayList, false, this).execute(ServiceResource.GETUSERROLERIGHTLIST_METHODNAME,ServiceResource.LOGIN_URL);

	}
	public void addPostoffline() {

		DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());

		try{





			Cursor cpost = dbHelper.DataFromTable("select * from "+DataBaseHelper.TBL_ADDPOST);

			if(cpost != null  && cpost.getCount() >0){
				cpost.moveToFirst();
				do {

					listnewName = new ArrayList<String>();

					String idPost = cpost.getString(cpost.getColumnIndex("ID"));

					Cursor c = dbHelper.DataFromTable("select * from "+DataBaseHelper.TBL_UPLOADPIC +" where "+DataBaseHelper.KEY_POSTID +" ='"+ cpost.getString(cpost.getColumnIndex(DataBaseHelper.KEY_POSTID))+"'" );

					if(c != null  && c.getCount() >0){
						c.moveToFirst();
						do {
							String id = c.getString(c.getColumnIndex("ID"));



							ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
							arrayList.add(new Property_vo(ServiceResource.PHOTO_FILENAME, new File(
									c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILENAME))).getName().replace(" ", "-")));
							arrayList.add(new Property_vo(ServiceResource.PHOTO_CLIENTID,
									c.getString(c.getColumnIndex(ServiceResource.PHOTO_CLIENTID))));

							arrayList.add(new Property_vo(ServiceResource.PHOTO_INSTITUTEID,
									c.getString(c.getColumnIndex(ServiceResource.PHOTO_INSTITUTEID))));
							//				if (args == 0) {
							//					arrayList.add(new Property_vo(ServiceResource.PHOTO_FILETYPE,
							//							"IMAGE"));
							//				} else if (args == 1) {
							//					arrayList.add(new Property_vo(ServiceResource.PHOTO_FILETYPE,
							//							"VIDEO"));
							//
							//				} else if (args == 2) {
							arrayList.add(new Property_vo(ServiceResource.PHOTO_FILETYPE,
									c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILETYPE))));

							//				}
							arrayList.add(new Property_vo(ServiceResource.PHOTO_MEMBERID,
									c.getString(c.getColumnIndex(ServiceResource.PHOTO_MEMBERID))));
							//				if (byteArray1 != null)
							byte[] byteArray = null;//convertImageToByteArra(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILENAME)));  
							if(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILETYPE)).equalsIgnoreCase("IMAGE")){
								byteArray = convertImageToByteArra(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILENAME)));
							}else if(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILETYPE)).equalsIgnoreCase("VIDEO")){
								byteArray = convertVideoToByteArra(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILENAME)));
							}else if(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILETYPE)).equalsIgnoreCase("FILE")){
								byteArray = convertVideoToByteArra(c.getString(c.getColumnIndex(ServiceResource.PHOTO_FILENAME)));
							}

							arrayList.add(new Property_vo(ServiceResource.PHOTO_FILE,
									byteArray));
							// SoapParser primary = new SoapParser(arrayList,
							// ServiceResource.UPLOAD_PHOTO_METHODNAME,
							// ServiceResource.ADDPHOTO_URL);
							// result = primary.buildData(mContext).toString();

							if(Utility.isNetworkAvailable(getApplicationContext())){



								SoapParser primary = new SoapParser(arrayList,
										ServiceResource.UPLOAD_PHOTO_METHODNAME,
										ServiceResource.ADDPHOTO_URL);

								String result = primary.buildData(getApplicationContext()).toString();




								//							new AsynsTaskClass(getApplicationContext(), arrayList, false, new ResponseWebServices() {
								//
								//								@Override
								//								public void response(String result, String methodName) {

								String message = " ";
								if (result != null && !result.isEmpty()) {
									//										String message = null;
									try {
										JSONArray obj = new JSONArray(result);
										for (int i = 0; i < obj.length(); i++) {
											JSONObject obj1 = obj.getJSONObject(i);
											message = obj1.getString(ServiceResource.MESSAGE);

										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}



								}
								String[] newName = message.split(" ");
								if (newName.length >= 1) {
									if(newName[1] != null ){
										listnewName.add(newName[1]);
										Log.v("img name", newName[1]);
									}
								}





								//								}
								//							}).execute(
								//									ServiceResource.UPLOAD_PHOTO_METHODNAME,
								//									ServiceResource.ADDPHOTO_URL);
								dbHelper.delete(DataBaseHelper.TBL_UPLOADPIC,Integer.valueOf( id));
							}
						} while (c.moveToNext());
					}



					ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();
					arrayList.add(new Property_vo(ServiceResource.INSTITUTEID,
							cpost.getString(cpost.getColumnIndex(ServiceResource.INSTITUTEID))));

					arrayList.add(new Property_vo(ServiceResource.CLIENT_ID,
							cpost.getString(cpost.getColumnIndex(ServiceResource.CLIENT_ID))));

					arrayList.add(new Property_vo(ServiceResource.WALLID,
							cpost.getString(cpost.getColumnIndex(ServiceResource.WALLID))));

					arrayList.add(new Property_vo(ServiceResource.MEMBERID,
							cpost.getString(cpost.getColumnIndex(ServiceResource.MEMBERID))));
					arrayList.add(new Property_vo(ServiceResource.USER_ID,
							cpost.getString(cpost.getColumnIndex(ServiceResource.USER_ID))));
					arrayList.add(new Property_vo(ServiceResource.BEATCH_ID, null));



					arrayList.add(new Property_vo(ServiceResource.POST_POSTSHARETYPE,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_POSTSHARETYPE))));

					arrayList.add(new Property_vo(ServiceResource.POST_POSTCOMMENTNOTE,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_POSTCOMMENTNOTE))));
					arrayList.add(new Property_vo(ServiceResource.POST_POSTBYTYPE,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_POSTBYTYPE))));

					String imgPath = "";
					if (listnewName != null && listnewName.size() > 0) {
						for (int i = 0; i < listnewName.size(); i++) {
							if (listnewName.size() - 1 == i) {
								imgPath = imgPath + listnewName.get(i);
							} else {
								imgPath = imgPath +listnewName.get(i)
										+ ",";
							}
						}
					}

					arrayList.add(new Property_vo(ServiceResource.POST_IMAGEPATH, imgPath));
					// arrayList.add(new Property_vo(ServiceResource.POST_FILEMINETYPE,
					// FileMineType));


					if(Global.DynamicWallSetting == null){
						Global.DynamicWallSetting = new DynamicWallSetting();
					}

					arrayList.add(new Property_vo(ServiceResource.POST_FILETYPE,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_FILETYPE))));
					arrayList.add(new Property_vo(ServiceResource.POST_FILEMINETYPE,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_FILEMINETYPE))));
					arrayList.add(new Property_vo(ServiceResource.POST_APPROVED,
							cpost.getString(cpost.getColumnIndex(ServiceResource.POST_APPROVED))));
					//				} else if (fileType == 1) {
					//					arrayList.add(new Property_vo(ServiceResource.POST_FILETYPE,
					//							"VIDEO"));
					//					arrayList.add(new Property_vo(ServiceResource.POST_FILEMINETYPE,
					//							"VIDEO"));
					//					arrayList.add(new Property_vo(ServiceResource.POST_APPROVED,
					//							Global.DynamicWallSetting.getIsAllowPeopleToPostVideos()));
					//				} else if (fileType == 2) {
					//					arrayList
					//					.add(new Property_vo(ServiceResource.POST_FILETYPE, "FILE"));
					//					arrayList.add(new Property_vo(ServiceResource.POST_FILEMINETYPE,
					//							FileMineType));
					//					arrayList.add(new Property_vo(ServiceResource.POST_APPROVED,
					//							Global.DynamicWallSetting.getIsAllowPeopleToPostVideos()));
					//				} else {
					//					arrayList.add(new Property_vo(ServiceResource.POST_FILEMINETYPE,
					//							FileMineType));
					//					arrayList.add(new Property_vo(ServiceResource.POST_APPROVED,
					//							Global.DynamicWallSetting.getIsAllowPeopleToPostStatus()));
					//				}

					// result = webcall.getJSONFromSOAPWS(
					// ServiceResource.POST_METHODNAME, map,
					// ServiceResource.POST_URL);
					//					new AsynsTaskClass(mContext, arrayList, false, null).execute(
					//							ServiceResource.POST_METHODNAME, ServiceResource.POST_URL);
					SoapParser primary = new SoapParser(arrayList,
							ServiceResource.POST_METHODNAME,
							ServiceResource.POST_URL);

					String result = primary.buildData(getApplicationContext()).toString();



					dbHelper.delete(DataBaseHelper.TBL_ADDPOST,Integer.valueOf( idPost));

				} while (cpost.moveToNext());
				ArrayList<Property_vo> arrayList = new ArrayList<Property_vo>();

				SoapParser primary = new SoapParser(arrayList,
						ServiceResource.SENDNOTIFICATION_METHODNAME,
						ServiceResource.NOTIFICATION_URL);
				String result = primary.buildData(getApplicationContext()).toString();


			}
			dbHelper.close();
		}catch(Exception e){

			dbHelper.close();
		}




	}
	public byte[] convertImageToByteArra(String filePath) {



		byte[] b = null;
		Bitmap thePic;
		try{
			thePic = Utility.getBitmap(filePath, getApplicationContext());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			if(filePath.contains(".png") || filePath.contains(".PNG")){
				thePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
			}else{
				thePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			}
			//		thePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);

			b = null;
			b = stream.toByteArray();
		}catch(Exception e){

		}
		return b;

	}

	public byte[] convertVideoToByteArra(String filePath) {

		File file = new File(filePath);

		byte[] b = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			b = new byte[(int) file.length()];
			fileInputStream.read(b);
			for (int i = 0; i < b.length; i++) {
				System.out.print((char) b[i]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		} catch (Exception e1) {
			System.out.println("Error Reading The File.");
			e1.printStackTrace();
		}

		return b;

	}


	public void parseCircular(String result,String update){

		JSONArray jsonObj;
		try {
			jsonObj = new JSONArray(result);
			// JSONArray detailArrray= jsonObj.getJSONArray("");
//			Type listType = new TypeToken<ArrayList<AllUsers>>(){}.getType();
//			List<Circularparser> allUserses =
//					new GsonBuilder().create().fromJson(jsonObj.toString(), listType);

			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				CircularModel model = new CircularModel();
				String dateParse = innerObj
						.getString(ServiceResource.CIRCULAR_DATEOFCIRCULAR);

				if (dateParse != null && !dateParse.equalsIgnoreCase("")
						&& !dateParse.equalsIgnoreCase("null")) {
					String date = Utility
							.getDate(
									Long.valueOf(innerObj
											.getString(
													ServiceResource.CIRCULAR_DATEOFCIRCULAR)
											.replace("/Date(", "")
											.replace(")/", "")),
									"EEEE/dd/MMM/yyyy/");
					String[] dateArray = date.split("/");
					if (dateArray != null && dateArray.length > 0) {
						Log.v("date", (dateArray[0] + "" + dateArray[1]
								+ "" + dateArray[2]+""+dateArray[3]));
						model.setCr_date(dateArray[1] + " "
								+ dateArray[0].substring(0, 3));
						model.setCr_month(dateArray[2]);
						model.setYear(dateArray[3]);
					}

					// model.setCr_detail(cr_detail);
					model.setCircularID(innerObj
							.getString(ServiceResource.CIRCULAR_CIRCULARID));
					model.setMilliSecond(innerObj
							.getString(
									ServiceResource.CIRCULAR_DATEOFCIRCULAR)
							.replace("/Date(", "").replace(")/", ""));
					model.setCr_detail_txt(innerObj
							.getString(ServiceResource.CIRCULAR_CIRCULARDETAIL));

					model.setCr_subject(innerObj
							.getString(ServiceResource.CIRCULAR_CIRCULARTITLE));
					model.setTeacher_name(innerObj
							.getString(ServiceResource.CIRCULAR_TEACHERNAME));
					model.setTeacher_img(innerObj
							.getString(ServiceResource.CIRCULAR_TEACHERPROFILEPIC));
					model.setSubjectID(innerObj
							.getString(ServiceResource.CIRCULAR_SUBJECTID));
					model.setDivisionID(innerObj
							.getString(ServiceResource.CIRCULAR_DIVISIONID));
					model.setGradeID(innerObj
							.getString(ServiceResource.CIRCULAR_GRADEID));
					model.setImgUrl(ServiceResource.BASE_IMG_URL+ServiceResource.DATAFILE+innerObj
							.getString(ServiceResource.PHOTOPARSE));
					model.setTypeTerm(innerObj
							.getString(ServiceResource.CIRCULAR_CIRCULARTUPETERM));


					String isApprove = innerObj
							.getString(ServiceResource.HOMEWORK_ISAPPROVED);
					if(isApprove.equalsIgnoreCase("true")){
						model.setAccept(true);
					}else{
						model.setAccept(false);
					}

					String isRead = innerObj
							.getString(ServiceResource.HOMEWORK_ISREAD);
					if (isRead.equalsIgnoreCase("true")) {
						model.setIsRead(true);
					} else {
						model.setIsRead(false);
					}


					if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

						Type listType = new TypeToken<Circularparser>() {
						}.getType();
						Circularparser parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
						ContentValues cv = new ContentValues();
						cv.put(DataBaseHelper.CIRCULAR_CIRCULARID, parse.getCircularID());
						cv.put(DataBaseHelper.CIRCULAR_TEACHERNAME, parse.getTeacherName());
						cv.put(DataBaseHelper.CIRCULAR_DATEOFCIRCULAR, parse.getDateOfCircular());
						cv.put(DataBaseHelper.CIRCULAR_GRADEID, parse.getGradeID());
						cv.put(DataBaseHelper.CIRCULAR_CIRCULARTITLE, parse.getCircularTitle());
						cv.put(DataBaseHelper.CIRCULAR_FILETYPE, parse.getFileType());
						cv.put(DataBaseHelper.CIRCULAR_CIRCULARDETAILS, parse.getCircularDetails());
						cv.put(DataBaseHelper.CIRCULAR_CIRCULARTYPETERM, parse.getCircularTypeTerm());
						cv.put(DataBaseHelper.CIRCULAR_TEACHERID, parse.getTeacherID());
						cv.put(DataBaseHelper.CIRCULAR_PROFILEPIC, parse.getProfilePic());
						cv.put(DataBaseHelper.CIRCULAR_DIVISIONID, parse.getDivisionID());
						cv.put(DataBaseHelper.CIRCULAR_SUBJECTID, parse.getSubjectID());
						cv.put(DataBaseHelper.CIRCULAR_ISREAD, parse.getIsRead());
						cv.put(DataBaseHelper.CIRCULAR_SEQNO, parse.getSeqNo());
						cv.put(DataBaseHelper.CIRCULAR_SUBJECTNAME, parse.getSubjectName());
						cv.put(DataBaseHelper.CIRCULAR_ISAPPROVED, parse.getIsApproved());
						cv.put(DataBaseHelper.CIRCULAR_PHOTO, parse.getPhoto());
						cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

						DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
						if (dbHelper.update(DataBaseHelper.TBL_CIRCULAR, cv, DataBaseHelper.CIRCULAR_CIRCULARID + "='" + parse.getCircularID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
							dbHelper.insert(DataBaseHelper.TBL_CIRCULAR, cv);
//						Log.v("circular", "insert");
						} else {
//						Log.v("circular", "update");
						}
					}
					// model.setTeacher_img(teacher_img);
					// model.setTeacher_name(teacher_name);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void parseClasswork(String result,String update){

		JSONArray jsonObj;
		try {
			jsonObj = new JSONArray(result);
			//JSONArray detailArrray= jsonObj.getJSONArray("");


			for(int i = 0;i<jsonObj.length();i++){
				try{
					ClassWorkModel model = new ClassWorkModel();
					JSONObject innerObj = jsonObj.getJSONObject(i);

					String date = Utility.getDate(
							Long.valueOf(innerObj.getString(ServiceResource.CLASSWORK_DATEOFCLASSWORK)
									.replace("/Date(", "").replace(")/", "")),
							"EEEE/dd/MMM/yyyy/");
					try{
						String dateView = Utility.getDate(
								Long.valueOf(innerObj.getString(ServiceResource.CLASSWORK_EXPECTINGDATEOFCOMPLETION)
										.replace("/Date(", "").replace(")/", "")),
								"dd MMM yyyy");
						model.setExpectingDateOfCompletion(dateView);
					}catch (Exception e){

					}

					String starttime = Utility.getDate(
							Long.valueOf(innerObj.getString(ServiceResource.CLASSWORK_STARTTIME)
									.replace("/Date(", "").replace(")/", "")),
							"hh:mm a");
					String endtime = Utility.getDate(
							Long.valueOf(innerObj.getString(ServiceResource.CLASSWORK_ENDTIME)
									.replace("/Date(", "").replace(")/", "")),
							"hh:mm a");
					try{
						String lastdate = Utility.getDate(
								Long.valueOf(innerObj.getString(ServiceResource.CLASSWORK_EXPECTINGDATEOFCOMPLETION)
										.replace("/Date(", "").replace(")/", "")),
								"EEEE/dd/MMM/yyyy/");
					}catch (Exception e){

					}
					//					Log.v("lastdate", lastdate);
					Log.v("time ", starttime+"dsad"+endtime);
					String[] dateArray =date.split("/");
					if(dateArray != null && dateArray.length>0){
						Log.v("date",(dateArray[0]+""+dateArray[1]+""+dateArray[2]));
						model.setDay(dateArray[1]+" "+dateArray[0].substring(0, 3));
						model.setMonth(dateArray[2]);
						model.setYear(dateArray[3]);
					}


					model.setDateInMillisecond(innerObj.getString(ServiceResource.CLASSWORK_DATEOFCLASSWORK)
							.replace("/Date(", "").replace(")/", ""));
					model.setClassWorkID(innerObj.getString(ServiceResource.CLASSWORK_CLASSWORKID));
					model.setClassWorkTitle(innerObj.getString(ServiceResource.CLASSWORK_TITLE));
					model.setDateOfClassWork(innerObj.getString(ServiceResource.CLASSWORK_DATEOFCLASSWORK));
					model.setClassWorkDetails(innerObj.getString(ServiceResource.CLASSWORK_DETAIL));
					model.setDivisionID(innerObj.getString(ServiceResource.CLASSWORK_DIVISIONID));
					model.setEndTime(endtime);

					model.setGradeID(innerObj.getString(ServiceResource.CLASSWORK_GRADEID));
					model.setPostID(innerObj.getString(ServiceResource.CLASSWORK_POSTID));
					model.setReferenceLink(innerObj.getString(ServiceResource.CLASSWORK_REFERENCELINK));
					model.setStartTime(starttime);
					model.setSubjectID(innerObj.getString(ServiceResource.CLASSWORK_SUBJECTID));
					model.setSubjectName(innerObj.getString(ServiceResource.CLASSWORK_SUBJECTNAME));
					model.setUserName(innerObj.getString(ServiceResource.CLASSWORK_USERNAME));
					model.setTeacherID(innerObj.getString(ServiceResource.CLASSWORK_TEACHERID));
					model.setTechearName(innerObj.getString(ServiceResource.CLASSWORK_TEACHERNAME));
					model.setTeacherImg(innerObj.getString(ServiceResource.CLASSWORK_TEACHERPROFILEPIC));
					model.setImgUrl(ServiceResource.BASE_IMG_URL+ServiceResource.DATAFILE+innerObj
							.getString(ServiceResource.PHOTOPARSE));
					model.setGradeName(innerObj
							.getString(ServiceResource.GRADENAME));
					model.setDivisionName(innerObj
							.getString(ServiceResource.DIVISIONNAME));
					String isRead =innerObj
							.getString(ServiceResource.HOMEWORK_ISREAD);
					String isFinish = innerObj
							.getString(ServiceResource.HOMEWORK_ISCHECKED);
					if( isFinish.equalsIgnoreCase("true")){
						model.setCheck(true);
					}else{
						model.setCheck(false);
					}
					if( isRead.equalsIgnoreCase("true")){
						model.setIsRead(true);
					}else{
						model.setIsRead(false);
					}

					//					if(from != null && !from.equalsIgnoreCase("") && from.equalsIgnoreCase(ServiceResource.CLASSWORK_FLAG_TEACHER)){
					//					if(gradeId.equalsIgnoreCase(model.getGradeID()) && divisionId.equalsIgnoreCase(model.getDivisionID()) && subjectId.equalsIgnoreCase(model.getSubjectID())){
					//						Global.classWorkModels.add(model);
					//					}
					//					}else {




					if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

						Type listType = new TypeToken<ClassworkParse>() {
						}.getType();
						ClassworkParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
						ContentValues cv = new ContentValues();
						cv.put(DataBaseHelper.CLASSWORK_CLASSWORKID, parse.getClassWorkID());
						cv.put(DataBaseHelper.CLASSWORK_DATEOFCLASSWORK, parse.getDateOfClassWork());
						cv.put(DataBaseHelper.CLASSWORK_POSTID, parse.getPostID());
						cv.put(DataBaseHelper.CLASSWORK_GRADEID, parse.getGradeID());
						cv.put(DataBaseHelper.CLASSWORK_DIVISIONID, parse.getDivisionID());
						cv.put(DataBaseHelper.CLASSWORK_SUBJECTID, parse.getSubjectID());
						cv.put(DataBaseHelper.CLASSWORK_FILETYPE, parse.getFileType());
						cv.put(DataBaseHelper.CLASSWORK_TEACHERID, parse.getTeacherID());
						cv.put(DataBaseHelper.CLASSWORK_TECHERNAME, parse.getTecherName());
						cv.put(DataBaseHelper.CLASSWORK_PROFILEPICTURE, parse.getProfilePicture());
						cv.put(DataBaseHelper.CLASSWORK_CLASSWORKTITLE, parse.getClassWorkTitle());
						cv.put(DataBaseHelper.CLASSWORK_CLASSWORKDETAILS, parse.getClassWorkDetails());
						cv.put(DataBaseHelper.CLASSWORK_REFERENCELINK, parse.getReferenceLink());
						cv.put(DataBaseHelper.CLASSWORK_EXPECTINGDATEOFCOMPLETION, parse.getExpectingDateOfCompletion());
						cv.put(DataBaseHelper.CLASSWORK_STARTTIME, parse.getStartTime());
						cv.put(DataBaseHelper.CLASSWORK_ENDTIME, parse.getEndTime());
						cv.put(DataBaseHelper.CLASSWORK_USERNAME, parse.getUserName());
						cv.put(DataBaseHelper.CLASSWORK_SUBJECTNAME, parse.getSubjectName());
						cv.put(DataBaseHelper.CLASSWORK_ISREAD, parse.getIsRead());
						cv.put(DataBaseHelper.CLASSWORK_ISCHECKED, parse.getIsChecked());
						cv.put(DataBaseHelper.CLASSWORK_PHOTO, parse.getPhoto());
						cv.put(DataBaseHelper.CLASSWORK_GRADENAME, parse.getGradeName());
						cv.put(DataBaseHelper.CLASSWORK_DIVISIONNAME, parse.getDivisionName());
						cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

						DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
						if (dbHelper.update(DataBaseHelper.TBL_CLASSWORK, cv, DataBaseHelper.CLASSWORK_CLASSWORKID + "='" + parse.getClassWorkID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
							dbHelper.insert(DataBaseHelper.TBL_CLASSWORK, cv);
//						Log.v("circular", "insert");
						} else {
//						Log.v("circular", "update");
						}
					}
				}catch(Exception  e){

				}
				//					}

			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void parseHomework(String result,String update){

		JSONArray jsonObj;
		try {


			jsonObj = new JSONArray(result);
			// JSONArray detailArrray= jsonObj.getJSONArray("");

			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				HomeWorkModel model = new HomeWorkModel();

				String date = Utility
						.getDate(
								Utility.dateToMilliSeconds(
										innerObj.getString(ServiceResource.HOMEWORK_DATEOFHOMEWORLK),
										"MM/dd/yyyy"), "EEEE/dd/MMM/yyyy/");
				Log.v("date And Time",  Utility
						.getDate(
								Utility.dateToMilliSeconds(
										innerObj.getString(ServiceResource.HOMEWORK_DATEOFHOMEWORLK),
										"MM/dd/yyyy"), "EEEE/dd/MMM/yyyy/ hh:mm:ss"));
				String[] dateArray = date.split("/");
				if (dateArray != null && dateArray.length > 0) {
					try{
						Log.v("date",
								(dateArray[0] + "" + dateArray[1] + "" + dateArray[2]));
						model.setDay(dateArray[1] + " "
								+ dateArray[0].substring(0, 3));
					}catch(Exception e){

					}
					model.setMonth(dateArray[2]);
					model.setYear(dateArray[3]);
					model.setDateOfFinish(dateArray[1] + " " + dateArray[2]
							+ " " + dateArray[3]);
				}

				model.setMilliSecond(Utility.dateToMilliSeconds(
						innerObj.getString(ServiceResource.HOMEWORK_DATEOFHOMEWORLK).replace("/Date(", "").replace(")/", ""),
						"MM/dd/yyyy")+"");
				Log.v("date", model.getMilliSecond());
				model.setDateOfFinish(innerObj
						.getString(ServiceResource.HOMEWORK_DATEOFFINISH));
				model.setAssignmentID(innerObj
						.getString(ServiceResource.HOMEWORK_ASSINMENTID));
				//					model.setClientID(innerObj
				//							.getString(ServiceResource.HOMEWORK_CLIENTID));
				//					model.setInstituteID(innerObj
				//							.getString(ServiceResource.HOMEWORK_INSTITUTEID));
				//					model.setMemberID(innerObj
				//							.getString(ServiceResource.HOMEWORK_MEMBERID));
				//					model.setGradeID(innerObj
				//							.getString(ServiceResource.HOMEWORK_GRADEID));
				//					model.setDivisionID(innerObj
				//							.getString(ServiceResource.HOMEWORK_DIVISIONID));
				model.setSubjectName(innerObj
						.getString(ServiceResource.HOMEWORK_SUBJECTNAME));
				model.setAssignmentID(innerObj
						.getString(ServiceResource.HOMEWORK_ASSINMENTID));
				//					model.setFacultyID(innerObj
				//							.getString(ServiceResource.HOMEWORK_FACULTYID));
				model.setDateOfHomeWork(innerObj
						.getString(ServiceResource.HOMEWORK_DATEOFHOMEWORLK));
				model.setTitle(innerObj
						.getString(ServiceResource.HOMEWORK_TITLE));
				model.setTecherName(innerObj
						.getString(ServiceResource.HOMEWORK_TEACHERNAME));
				model.setTeacherProfilePicture(innerObj
						.getString(ServiceResource.HOMEWORK_TEACHERPROFILEPIC));
				//					model.setSubjectID(innerObj
				//							.getString(ServiceResource.HOMEWORK_SUBJECTID));
				model.setHomeWorksDetails(innerObj
						.getString(ServiceResource.HOMEWORK_HOMEWORKDETAILS));
				//					model.setMessageReceived(Boolean.valueOf(innerObj
				//							.getString(ServiceResource.HOMEWORK_ISMESSAGERECEIVED)));
				model.setIsWorkFinished(Boolean.valueOf(innerObj
						.getString(ServiceResource.HOMEWORK_ISWORKFINISH)));
				model.setImgUrl(ServiceResource.BASE_IMG_URL+ServiceResource.DATAFILE+innerObj
						.getString(ServiceResource.PHOTOPARSE));
				model.setGradeName(innerObj
						.getString(ServiceResource.GRADENAME));
				model.setDivisionName(innerObj
						.getString(ServiceResource.DIVISIONNAME));
				String isRead = innerObj
						.getString(ServiceResource.HOMEWORK_ISREAD);


				String isFinish = innerObj
						.getString(ServiceResource.HOMEWORK_ISWORKFINISHED);
				if( isFinish.equalsIgnoreCase("true")){
					model.setFinish(true);
				}else{
					model.setFinish(false);
				}


				if (isRead.equalsIgnoreCase("true")) {
					model.setIsRead(true);
				} else {
					model.setIsRead(false);
				}

				//					if (from != null
				//							&& !from.equalsIgnoreCase("")
				//							&& from.equalsIgnoreCase(ServiceResource.HOMEWORK_FLAG_TEACHER)) {
				//						if (gradeId.equalsIgnoreCase(model.getGradeID())
				//								&& divisionId.equalsIgnoreCase(model
				//										.getDivisionID())
				//										&& subjectName.equalsIgnoreCase(model
				//												.getSubjectName())) {
				//							Global.homeworkModels.add(model);
				//						}
				//					} else {

				//					}

				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<HomeworkParse>() {
					}.getType();
					HomeworkParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.HOMEWORK_ASSIGNMENTID, parse.getAssignmentID());
					cv.put(DataBaseHelper.HOMEWORK_DATEOFHOMEWORK, parse.getDateOfHomeWork());
					cv.put(DataBaseHelper.HOMEWORK_TITLE, parse.getTitle());
					cv.put(DataBaseHelper.HOMEWORK_HOMEWORKSDETAILS, parse.getHomeWorksDetails());
					cv.put(DataBaseHelper.HOMEWORK_SUBJECTNAME, parse.getSubjectName());
					cv.put(DataBaseHelper.HOMEWORK_DATEOFFINISH, parse.getDateOfFinish());
					cv.put(DataBaseHelper.HOMEWORK_ISREAD, parse.getIsRead());
					cv.put(DataBaseHelper.HOMEWORK_ISAPPROVE, parse.getIsApprove());
					cv.put(DataBaseHelper.HOMEWORK_ISWORKFINISH, parse.getIsWorkFinish());
					cv.put(DataBaseHelper.HOMEWORK_SUBJECTID, parse.getSubjectId());
					cv.put(DataBaseHelper.HOMEWORK_TEACHERPROFILEPICTURE, parse.getTeacherProfilePicture());
					cv.put(DataBaseHelper.HOMEWORK_TEACHERNAME, parse.getTeacherName());
					cv.put(DataBaseHelper.HOMEWORK_PHOTO, parse.getPhoto());
					cv.put(DataBaseHelper.HOMEWORK_FILETYPE, parse.getFileType());
					cv.put(DataBaseHelper.HOMEWORK_GRADENAME, parse.getGradeName());
					cv.put(DataBaseHelper.HOMEWORK_DIVISIONNAME, parse.getDivisionName());
					cv.put(DataBaseHelper.DATABASE_USERID,userSharedPrefrence.getLoginModel().getUserID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_HOMEWORK, cv, DataBaseHelper.HOMEWORK_ASSIGNMENTID + "='" + parse.getAssignmentID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" +userSharedPrefrence.getLoginModel().getUserID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_HOMEWORK, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}


				// Global.homeworkModels.add(model);
				//
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void parsingdata1(String result,String update1,int count,String from,String wallId ){
		JSONObject mJsonObject;
		JSONArray wallListJsonArray;
		JSONArray mainJsonArray;
		boolean isMoreData ;
		if (result != null
				&& !result.toString().equals(
				"[{\"message\":\"No Data Found\",\"success\":0}]")) {

			try {

				if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL) || from.equalsIgnoreCase(ServiceResource.Wall)){
					mainJsonArray = new JSONArray(result);

				}else{
					JSONObject jobj=new JSONObject(result);
					mainJsonArray = jobj.getJSONArray(ServiceResource.WALL_POSTDATA);
					JSONArray jsonArray = jobj.getJSONArray(ServiceResource.WALL_ROLEDATA);
					JSONArray jsonArrayAdminsetting = jobj.optJSONArray(ServiceResource.WALLROLEDATA);

				}


				// JSONArray detailArrray= jsonObj.getJSONArray("");

				//				if(count == 1){
				//					Global.wallPostModels = new ArrayList<WallPostModel>();
				//				}


				for (int i = 0; i < mainJsonArray.length(); i++) {
					JSONObject innerObj = mainJsonArray.getJSONObject(i);
					WallPostModel wallPostModel = new WallPostModel();

					ContentValues cv = new ContentValues();

					wallPostModel.setPostCommentID(innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTID, innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));

					wallPostModel
							.setAssociationType(innerObj
									.getString(ServiceResource.WALL_ASSOCIATIONTYPE));

					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONTYPE, innerObj
							.getString(ServiceResource.WALL_ASSOCIATIONTYPE));

					if(from.equalsIgnoreCase(ServiceResource.Wall) || from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));
					}else{
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

					}
					wallPostModel.setIsLike(innerObj
							.getString(ServiceResource.WALL_ISLIKE));

					cv.put(DataBaseHelper.DATABASE_ISLIKE,innerObj
							.optString(ServiceResource.WALL_ISLIKE));

					wallPostModel.setFullName(innerObj
							.getString(ServiceResource.WALL_FULLNAME));

					cv.put(DataBaseHelper.DATABASE_FULLNAME,innerObj
							.optString(ServiceResource.WALL_FULLNAME));
					wallPostModel
							.setProfilePicture(innerObj
									.getString(ServiceResource.WALL_PROFILEPICTURE));

					cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,innerObj
							.optString(ServiceResource.WALL_PROFILEPICTURE));



					wallPostModel.setTotalLikes(innerObj
							.getString(ServiceResource.WALL_TOTALLIKES));

					cv.put(DataBaseHelper.DATABASE_TOTALLIKES,innerObj
							.optString(ServiceResource.WALL_TOTALLIKES));

					wallPostModel.setTotalDislike(innerObj
							.getString(ServiceResource.WALL_TOTALDISLIKE));

					cv.put(DataBaseHelper.DATABASE_TOTALDISLIKE,innerObj
							.optString(ServiceResource.WALL_TOTALDISLIKE));

					wallPostModel.setRowNo(innerObj
							.getString(ServiceResource.WALL_ROWNO_POST));

					cv.put(DataBaseHelper.DATABASE_ROWNO,innerObj
							.optString(ServiceResource.WALL_ROWNO_POST));

					wallPostModel.setPostCommentID(innerObj
							.getString(ServiceResource.WALL_POSTCOMMENTID));
					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTID,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTID));

					wallPostModel.setWallID(innerObj
							.getString(ServiceResource.WALL_WALLID));
					cv.put(DataBaseHelper.DATABASE_WALLID,innerObj
							.optString(ServiceResource.WALL_WALLID));

					wallPostModel
							.setPostCommentTypesTerm(innerObj
									.getString(ServiceResource.WALL_POSTCOMMENTTYPESTERM));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTTYPESTERM,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTTYPESTERM));

					wallPostModel
							.setPostCommentNote(innerObj
									.getString(ServiceResource.WALL_POSTCOMMENTNOTE));

					cv.put(DataBaseHelper.DATABASE_POSTCOMMENTNOTE,innerObj
							.optString(ServiceResource.WALL_POSTCOMMENTNOTE));


					wallPostModel.setTotalLikes(innerObj
							.getString(ServiceResource.WALL_TOTALLIKES));

					cv.put(DataBaseHelper.DATABASE_TOTALLIKES,innerObj
							.optString(ServiceResource.WALL_TOTALLIKES));

					wallPostModel.setTotalComments(innerObj
							.getString(ServiceResource.WALL_TOTALCOMMENTS));

					cv.put(DataBaseHelper.DATABASE_TOTALCOMMENTS,innerObj
							.optString(ServiceResource.WALL_TOTALCOMMENTS));

					wallPostModel.setTotalDislike(innerObj
							.getString(ServiceResource.WALL_TOTALDISLIKE));

					cv.put(DataBaseHelper.DATABASE_TOTALDISLIKE,innerObj
							.optString(ServiceResource.WALL_TOTALDISLIKE));

					wallPostModel.setDateOfPost(innerObj
							.getString(ServiceResource.WALL_DATEOFPOST));

					cv.put(DataBaseHelper.DATABASE_DATEOFPOST,innerObj
							.optString(ServiceResource.WALL_DATEOFPOST));

					wallPostModel.setAssociationID(innerObj
							.getString(ServiceResource.WALL_ASSOCIATIONID));

					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONID,innerObj
							.optString(ServiceResource.WALL_ASSOCIATIONID));

					wallPostModel.setPostedOn(innerObj
							.getString(ServiceResource.WALL_POSTEDON));
					cv.put(DataBaseHelper.DATABASE_POSTEDON,innerObj
							.optString(ServiceResource.WALL_POSTEDON));

					wallPostModel
							.setAssociationType(innerObj
									.getString(ServiceResource.WALL_ASSOCIATIONTYPE));
					cv.put(DataBaseHelper.DATABASE_ASSOCIATIONTYPE,innerObj
							.optString(ServiceResource.WALL_ASSOCIATIONTYPE));


					wallPostModel.setFullName(innerObj
							.getString(ServiceResource.WALL_FULLNAME));
					cv.put(DataBaseHelper.DATABASE_FULLNAME,innerObj
							.optString(ServiceResource.WALL_FULLNAME));


					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKESIN));

					}else{
						wallPostModel.setIsDisLike(innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
						cv.put(DataBaseHelper.DATABASE_ISDISLIKE,innerObj
								.optString(ServiceResource.WALL_ISDISLIKE));
					}
					wallPostModel.setIsLike(innerObj
							.getString(ServiceResource.WALL_ISLIKE));

					cv.put(DataBaseHelper.DATABASE_ISLIKE,innerObj
							.optString(ServiceResource.WALL_ISLIKE));

					//						if(count == 1){
					if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL) || from.equalsIgnoreCase(ServiceResource.Wall)){
						if(innerObj.getString(ServiceResource.IS_USER_LIKE_WALL).equalsIgnoreCase("1")){
							cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,1);
							if(innerObj.getString(ServiceResource.IS_USER_LIKE).equalsIgnoreCase("1")){
								wallPostModel.setUserLike(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,1);

							}else{
								wallPostModel.setUserLike(false);
								cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,0);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
							}
						}else{
							wallPostModel.setUserLike(false);
							cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,0);
							cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_DISLIKE_WALL).equalsIgnoreCase("1")){
							cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,1);
							if(innerObj.getString(ServiceResource.IS_USER_DISLIKE).equalsIgnoreCase("1")){
								wallPostModel.setUserDisLike(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,1);
							}else{
								wallPostModel.setUserDisLike(false);
								cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,0);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);

							}
						}else{
							wallPostModel.setUserDisLike(false);
							cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,0);
							cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
							cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,1);
							if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
								wallPostModel.setUserComment(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,1);

							}else{
								wallPostModel.setUserComment(false);
								cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,0);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);

							}
						}else{
							wallPostModel.setUserComment(false);
							cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,0);
							cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
						}
						if(innerObj.getString(ServiceResource.IS_USER_SHARE_WALL).equalsIgnoreCase("1")){

							cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,1);
							if(innerObj.getString(ServiceResource.IS_USER_SHARE).equalsIgnoreCase("1")){
								wallPostModel.setUserShare(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,1);
							}else{
								wallPostModel.setUserShare(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
								cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,0);
							}
						}
						else{
							wallPostModel.setUserShare(false);
							cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,0);
							cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
						}
					}else{
						if(Global.DynamicWallSetting.getIsAllowPeopleToLikeThisWall()){
							if(innerObj.getString(ServiceResource.IS_USER_LIKE_WALL).equalsIgnoreCase("1")){
								cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,1);
								if(innerObj.getString(ServiceResource.IS_USER_LIKE).equalsIgnoreCase("1")){
									wallPostModel.setUserLike(true);
									cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,1);
								}else{
									wallPostModel.setUserLike(false);
									cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,0);
									cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
								}
							}
							else{
								wallPostModel.setUserLike(false);
								cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,0);
								cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
							}
						}
						else{
							wallPostModel.setUserLike(false);
							cv.put(DataBaseHelper.DATABASE_IS_USER_LIKE_WALL,0);
							cv.put(DataBaseHelper.DATABASE_ISUSERLIKE,0);
						}
						if(Global.DynamicWallSetting.getIsAllowPeopleToLikeThisWall()){
							if(innerObj.getString(ServiceResource.IS_USER_DISLIKE_WALL).equalsIgnoreCase("1")){
								cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,1);
								if(innerObj.getString(ServiceResource.IS_USER_DISLIKE).equalsIgnoreCase("1")){
									wallPostModel.setUserDisLike(true);
									cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,1);
									cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,1);
								}	else{
									wallPostModel.setUserDisLike(false);
									cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,0);
									cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
								}
							}else{
								wallPostModel.setUserDisLike(false);
								cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,0);
								cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
							}
						}
						else{
							wallPostModel.setUserDisLike(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERDISLIKE,0);
							cv.put(DataBaseHelper.DATABASE_IS_USER_DISLIKE_WALL,0);
						}
						if(Global.DynamicWallSetting.getIsAllowPeoplePostComment()){
							if(innerObj.getString(ServiceResource.IS_USER_COMMENT_WALL).equalsIgnoreCase("1")){
								cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,1);
								if(innerObj.getString(ServiceResource.IS_USER_COMMENT).equalsIgnoreCase("1")){
									wallPostModel.setUserComment(true);
									cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,1);
									cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,1);
								}	else{
									wallPostModel.setUserComment(false);
									cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
									cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,0);

								}
							}else{
								wallPostModel.setUserComment(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
								cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,0);
							}
						}
						else{
							wallPostModel.setUserComment(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERCOMMENT,0);
							cv.put(DataBaseHelper.DATABASE_IS_USER_COMMENT,0);

						}
						//							if(Global.DynamicWallSetting.get){
						if(innerObj.getString(ServiceResource.IS_USER_SHARE_WALL).equalsIgnoreCase("1")){
							if(innerObj.getString(ServiceResource.IS_USER_SHARE).equalsIgnoreCase("1")){
								wallPostModel.setUserShare(true);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,1);
								cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,1);
							}
							//							}
							else{
								wallPostModel.setUserShare(false);
								cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
								cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,0);
							}
						}else{
							wallPostModel.setUserShare(false);
							cv.put(DataBaseHelper.DATABASE_ISUSERSHARE,0);
							cv.put(DataBaseHelper.DATABASE_IS_USER_SHARE,0);
						}
					}

					//						}

					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						wallPostModel
								.setSendToMemberID(innerObj
										.getString(ServiceResource.WALL_SENDTOMEMBERID));

						cv.put(DataBaseHelper.DATABASE_SENDTOMEMBERID,innerObj
								.getString(ServiceResource.WALL_SENDTOMEMBERID));

					}else{
						if(update1.equalsIgnoreCase(ServiceResource.UPDATE)) {
							wallPostModel
									.setSendToMemberID(innerObj
											.getString(ServiceResource.MEMBERID));
							cv.put(DataBaseHelper.DATABASE_SENDTOMEMBERID, innerObj
									.getString(ServiceResource.MEMBERID));
						}else{
							wallPostModel
									.setSendToMemberID(innerObj
											.getString(ServiceResource.WALL_SENDTOMEMBERID));

							cv.put(DataBaseHelper.DATABASE_SENDTOMEMBERID,innerObj
									.getString(ServiceResource.WALL_SENDTOMEMBERID));
						}

					}
					wallPostModel
							.setProfilePicture(innerObj
									.getString(ServiceResource.WALL_PROFILEPICTURE));

					cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,innerObj
							.getString(ServiceResource.WALL_PROFILEPICTURE));

					wallPostModel.setPhoto(innerObj
							.getString(ServiceResource.WALL_PHOTO));
					cv.put(DataBaseHelper.DATABASE_PHOTO,innerObj
							.getString(ServiceResource.WALL_PHOTO));


					wallPostModel.setPostDate(innerObj
							.optString(ServiceResource.WALL_POSTDATE));
					cv.put(DataBaseHelper.DATABASE_POSTDATE,innerObj
							.optString(ServiceResource.WALL_POSTDATE));


					/*wallPostModel.setPostDate(innerObj
							.optString(
									ServiceResource.WALL_TEMPDATE)
									.replace("/Date(", "")
									.replace(")/", ""));*/

					wallPostModel.setTempDate(innerObj
							.optString(
									ServiceResource.WALL_TEMPDATE)
							.replace("/Date(", "")
							.replace(")/", ""));
					cv.put(DataBaseHelper.DATABASE_TEMPDATE,innerObj
							.optString(
									ServiceResource.WALL_TEMPDATE)
							.replace("/Date(", "")
							.replace(")/", ""));


					wallPostModel.setFileType(innerObj
							.getString(ServiceResource.WALL_FILETYPE));

					cv.put(DataBaseHelper.DATABASE_FILETYPE,innerObj
							.getString(ServiceResource.WALL_FILETYPE));

					wallPostModel.setFileMimeType(innerObj
							.getString(ServiceResource.WALL_FILEMIMETYPE));
					cv.put(DataBaseHelper.DATABASE_FILEMIMETYPE,innerObj
							.getString(ServiceResource.WALL_FILEMIMETYPE));



					if (wallPostModel.getPhoto() != null
							&& !wallPostModel.getPhoto().equals("")){
						wallPostModel.setPhoto(ServiceResource.BASE_IMG_URL
								+ "DataFiles/"
								+ wallPostModel.getPhoto().replaceAll(
								"DataFiles/", ""));

						cv.put(DataBaseHelper.DATABASE_PHOTO,ServiceResource.BASE_IMG_URL
								+ "DataFiles/"
								+ wallPostModel.getPhoto().replaceAll(
								"DataFiles/", ""));

					}
					if (wallPostModel.getProfilePicture() != null
							&& !wallPostModel.getProfilePicture()
							.equals("")) {

						if (wallPostModel.getProfilePicture().contains(
								"/img/"))
							wallPostModel
									.setProfilePicture(ServiceResource.BASE_IMG_URL
											+ wallPostModel
											.getProfilePicture()
											.replaceAll(
													"DataFiles/",
													""));

						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));

					}
					else if (wallPostModel.getProfilePicture().contains(
							"/AvtarImages/")){
						wallPostModel
								.setProfilePicture(ServiceResource.BASE_IMG_URL
										+ wallPostModel
										.getProfilePicture()
										.replaceAll(
												"DataFiles/",
												""));
						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));

					}
					else{
						wallPostModel
								.setProfilePicture(ServiceResource.BASE_IMG_URL
										+ "/DataFiles/"
										+ wallPostModel
										.getProfilePicture()
										.replaceAll(
												"DataFiles/",
												""));
						cv.put(DataBaseHelper.DATABASE_PROFILEPICTURE,ServiceResource.BASE_IMG_URL
								+ "/DataFiles/"
								+ wallPostModel
								.getProfilePicture()
								.replaceAll(
										"DataFiles/",
										""));


					}

					cv.put(DataBaseHelper.DATABASE_WALLIDPARSE, wallId);
					cv.put(DataBaseHelper.DATABASE_USERID ,userSharedPrefrence.getLOGIN_USERID());
					if(from.equalsIgnoreCase(ServiceResource.Wall)){
						cv.put(DataBaseHelper.DATABASE_METHODNAME,ServiceResource.Wall+wallId );
					}else if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
						cv.put(DataBaseHelper.DATABASE_METHODNAME, ServiceResource.PROFILEWALL+wallId);
					}
					else {
						cv.put(DataBaseHelper.DATABASE_METHODNAME, ServiceResource.GETDYNAMICWALLDATA_METHODNAME+wallId);
					}
					//					Global.wallPostModels.add(wallPostModel);
					//					if(from.equalsIgnoreCase(ServiceResource.Wall)){
					//						Global.wallPostModels = dbHelper.getALLwallFromDB(wallId,ServiceResource.Wall);
					//					}else if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
					//						Global.wallPostModels = dbHelper.getALLwallFromDB(wallId,ServiceResource.PROFILEWALL);
					//					}
					//					else {
					//						Global.wallPostModels = dbHelper.getALLwallFromDB(wallId,ServiceResource.GETDYNAMICWALLDATA_METHODNAME);
					//					}


					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if(update1.equalsIgnoreCase(ServiceResource.UPDATE)) {
						if (from.equalsIgnoreCase(ServiceResource.Wall)) {

							if (dbHelper.update(DataBaseHelper.TBL_ALLPOST, cv, DataBaseHelper.DATABASE_POSTCOMMENTID + "='" + wallPostModel.getPostCommentID() + "' AND " + DataBaseHelper.DATABASE_METHODNAME + "='" + ServiceResource.Wall +wallId+ "' AND " + DataBaseHelper.DATABASE_USERID + "= '" + userSharedPrefrence.getLOGIN_USERID() + "' ") == 0) {
								dbHelper.insert(DataBaseHelper.TBL_ALLPOST, cv);
								//						Log.v("post", "insert");
							} else {
								//						Log.v("post", "update");
							}
						} else if (from.equalsIgnoreCase(ServiceResource.PROFILEWALL)) {

							if (dbHelper.update(DataBaseHelper.TBL_PROFILEWALL, cv, DataBaseHelper.DATABASE_POSTCOMMENTID + "='" + wallPostModel.getPostCommentID() + "' AND " + DataBaseHelper.DATABASE_METHODNAME + "='" +  ServiceResource.PROFILEWALL +wallId+ "' AND " + DataBaseHelper.DATABASE_USERID + "= '" + userSharedPrefrence.getLOGIN_USERID() + "' ") == 0) {
								dbHelper.insert(DataBaseHelper.TBL_PROFILEWALL, cv);
								//						Log.v("post", "insert");
							} else {
								//						Log.v("post", "update");
							}
						} else {
							if (dbHelper.update(DataBaseHelper.TBL_DAYNAMICWALL, cv, DataBaseHelper.DATABASE_POSTCOMMENTID + "='" + wallPostModel.getPostCommentID() + "' AND " + DataBaseHelper.DATABASE_METHODNAME + "='" + ServiceResource.GETDYNAMICWALLDATA_METHODNAME + wallId+"' AND " + DataBaseHelper.DATABASE_USERID + "= '" + userSharedPrefrence.getLOGIN_USERID() + "' ") == 0) {
								dbHelper.insert(DataBaseHelper.TBL_DAYNAMICWALL, cv);
								//						Log.v("post", "insert");
							} else {
								//						Log.v("post", "update");
							}
						}

					}else{

					}


				}

				isMoreData = true;
			} catch (JSONException e) {
				isMoreData = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
				isMoreData = false;
			}



		} else {
			isMoreData = false;
		}

		if (isMoreData) {
			count += 10;
			if(from.equalsIgnoreCase(ServiceResource.Wall)){
				mainWallCall(count);
			}else if(from.equalsIgnoreCase(ServiceResource.PROFILEWALL)){
				profileWall(count);
			}
		} else {
			count = 1;
		}
	}
	public void parseNote(String result,String update){

		JSONArray jsonObj;
		try {


			jsonObj = new JSONArray(result);
			// JSONArray detailArrray= jsonObj.getJSONArray("");

			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				NotesModel notesModel = new NotesModel();

				notesModel.setNotesID(innerObj
						.getString(ServiceResource.NOTES_NOTESID));

				notesModel.setGradeID(innerObj
						.getString(ServiceResource.NOTES_GRADEID));
				notesModel.setDivisionID(innerObj
						.getString(ServiceResource.NOTES_DIVISIONID));
				notesModel.setSubjectID(innerObj
						.getString(ServiceResource.NOTES_SUBJECTID));
				notesModel.setSubjectName(innerObj
						.getString(ServiceResource.NOTES_SUBJECTNAME));
				notesModel.setImgUrl(ServiceResource.BASE_IMG_URL+ServiceResource.DATAFILE+innerObj
						.getString(ServiceResource.PHOTOPARSE));
				if(notesModel.getSubjectName()!=null && !notesModel.getSubjectName().equals("") && !notesModel.getSubjectName().equals("null"))
				{

				}else
				{
					notesModel.setSubjectName("");
				}


				if (!innerObj.getString(
						ServiceResource.NOTES_ACTIONSTARTDATE)
						.equalsIgnoreCase("null")) {
					String tempdate = innerObj
							.getString(
									ServiceResource.NOTES_ACTIONSTARTDATE)
							.replace("/Date(", "").replace(")/", "");
					String date = Utility.getDate(Long.valueOf(tempdate),
							"EEEE/dd/MMM/yyyy");

					String[] dateArray = date.split("/");
					if (dateArray != null && dateArray.length > 0) {
						Log.v("date", (dateArray[0] + "" + dateArray[1]
								+ "" + dateArray[2]));
						notesModel.setDay(dateArray[1] + " "
								+ dateArray[0].substring(0, 3));
						notesModel.setMonth(dateArray[2]);
						notesModel.setYear(dateArray[3]);
					}

					notesModel.setMilliSecond(innerObj
							.getString(
									ServiceResource.NOTES_ACTIONSTARTDATE)
							.replace("/Date(", "").replace(")/", ""));

					notesModel
							.setDateOfNotes(innerObj
									.getString(ServiceResource.NOTES_ACTIONSTARTDATE));

					notesModel.setNoteTitle(innerObj
							.getString(ServiceResource.NOTES_NOTETITLE));
					notesModel.setNoteDetails(innerObj
							.getString(ServiceResource.NOTES_NOTEDETAILS));
					notesModel
							.setActionStartDate(Utility.getDate(
									Long.valueOf(innerObj
											.getString(
													ServiceResource.NOTES_ACTIONSTARTDATE)
											.replace("/Date(", "")
											.replace(")/", "")),
									"dd/MM/yyyy"));
					try{
						notesModel
								.setActionEndDate(Utility.getDate(
										Long.valueOf(innerObj
												.getString(
														ServiceResource.NOTES_ACTIONENDDATE)
												.replace("/Date(", "")
												.replace(")/", "")),
										"dd/MM/yyyy"));
					}catch (Exception e){

					}
					notesModel.setDressCode(innerObj
							.getString(ServiceResource.NOTES_DRESSCODE));
					notesModel.setIsRead(Boolean.valueOf(innerObj
							.getString(ServiceResource.NOTES_ISREAD)));
					notesModel.setIamReady(Boolean.valueOf(innerObj
							.getString(ServiceResource.NOTES_IAMREADY)));
					notesModel.setParentNote(innerObj
							.getString(ServiceResource.NOTES_PARENTNOTE));
					notesModel.setRating(innerObj
							.getString(ServiceResource.NOTES_RATING));
					notesModel.setUserName(innerObj
							.getString(ServiceResource.NOTES_USERNAME));
					notesModel
							.setProfilePicture(innerObj
									.optString(ServiceResource.NOTES_PROFILEPICTURE));

					String isCheck = innerObj
							.optString(ServiceResource.HOMEWORK_ISCHECKED);
					if(isCheck.equalsIgnoreCase("true")){
						notesModel.setCheck(true);
					}else{
						notesModel.setCheck(false);
					}

					if (userSharedPrefrence.getLoginModel().getUserType() == ServiceResource.USER_STUDENT_INT) {
						if (notesModel.getProfilePicture() != null
								&& !notesModel.getProfilePicture().equals(
								"null")) {

							if (notesModel.getProfilePicture().contains(
									"/img/"))
								notesModel
										.setProfilePicture(ServiceResource.BASE_IMG_URL
												+ notesModel
												.getProfilePicture()
												.replaceAll(
														"DataFiles/",
														""));
							else
								notesModel
										.setProfilePicture(ServiceResource.BASE_IMG_URL
												+ ""
												+ notesModel
												.getProfilePicture()
												.replaceAll(
														"DataFiles/",
														""));
						}
					}

				}

				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<NoteParse>() {
					}.getType();
					NoteParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.NOTES_NOTESID, parse.getNotesID());
					cv.put(DataBaseHelper.NOTES_GRADEID, parse.getGradeID());
					cv.put(DataBaseHelper.NOTES_DIVISIONID, parse.getDivisionID());
					cv.put(DataBaseHelper.NOTES_SUBJECTID, parse.getSubjectID());
					cv.put(DataBaseHelper.NOTES_SUBJECTNAME, parse.getSubjectName());
					cv.put(DataBaseHelper.NOTES_DATEOFNOTES, parse.getDateOfNotes());
					cv.put(DataBaseHelper.NOTES_NOTETITLE, parse.getNoteTitle());
					cv.put(DataBaseHelper.NOTES_NOTEDETAILS, parse.getNoteDetails());
					cv.put(DataBaseHelper.NOTES_ACTIONSTARTDATE, parse.getActionStartDate());
					cv.put(DataBaseHelper.NOTES_ACTIONENDDATE, parse.getActionEndDate());
					cv.put(DataBaseHelper.NOTES_DRESSCODE, parse.getDressCode());
					cv.put(DataBaseHelper.NOTES_ISREAD, parse.getIsRead());
					cv.put(DataBaseHelper.NOTES_IAMREADY, parse.getIamReady());
					cv.put(DataBaseHelper.NOTES_PARENTNOTE, parse.getParentNote());
					cv.put(DataBaseHelper.NOTES_RATING, parse.getRating());
					cv.put(DataBaseHelper.NOTES_USERNAME, parse.getUserName());
					cv.put(DataBaseHelper.NOTES_ISCHECKED, parse.getIsChecked());
					cv.put(DataBaseHelper.NOTES_PHOTO, parse.getPhoto());
					cv.put(DataBaseHelper.NOTES_FILETYPE, parse.getFileType());
					cv.put(DataBaseHelper.NOTES_TOTALREAD, parse.getTotalRead());
					cv.put(DataBaseHelper.NOTES_TOTALAPPROVE, parse.getTotalApprove());
					cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_NOTES, cv, DataBaseHelper.NOTES_NOTESID + "='" + parse.getNotesID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_NOTES, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}
				// Global.homeworkModels.add(model);
				//
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}



	public void parseTodoList(String result,String update){

		JSONArray jsonObj;
		try {


			jsonObj = new JSONArray(result);
			// JSONArray detailArrray= jsonObj.getJSONArray("");

			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				TodoListModel model = new TodoListModel();



				model.setMilliSecond(innerObj
						.getString(ServiceResource.CREATEON)
						.replace("/Date(", "").replace(")/", ""));
				String completedate="";
				if(!innerObj
						.getString(ServiceResource.COMPLETDATE).equalsIgnoreCase("") &&
						!innerObj
								.getString(ServiceResource.COMPLETDATE).equalsIgnoreCase("null")){
					completedate=Utility.getDate(Long.valueOf(innerObj
							.getString(ServiceResource.COMPLETDATE)
							.replace("/Date(", "").replace(")/", "")), "MM/dd/yyyy") ;
				}else{
					completedate= "05/23/2015";
				}
				String endDate="";
				if(!innerObj
						.getString(ServiceResource.ENDDATE).equalsIgnoreCase("") &&
						!innerObj
								.getString(ServiceResource.ENDDATE).equalsIgnoreCase("null")){
					endDate =Utility.getDate(Long.valueOf(innerObj
							.getString(ServiceResource.ENDDATE)
							.replace("/Date(", "").replace(")/", "")), "MM/dd/yyyy") ;
				}else{
					endDate= "05/23/2015";
				}
				String createOn="";

				if(!innerObj
						.getString(ServiceResource.CREATEON).equalsIgnoreCase("") &&
						!innerObj
								.getString(ServiceResource.CREATEON).equalsIgnoreCase("null")){
					createOn =Utility.getDate(Long.valueOf(innerObj
							.getString(ServiceResource.CREATEON)
							.replace("/Date(", "").replace(")/", "")), "MM/dd/yyyy") ;
				}else{
					createOn= "05/23/2015";
				}
				String date ="";
				if(!innerObj
						.getString(ServiceResource.CREATEON).equalsIgnoreCase("") &&
						!innerObj
								.getString(ServiceResource.CREATEON).equalsIgnoreCase("null")){
					date =Utility.getDate(Long.valueOf(innerObj
							.getString(ServiceResource.CREATEON)
							.replace("/Date(", "").replace(")/", "")), "EEEE/dd/MMM/yyyy") ;

				}else{
					date= "05/23/2015";
				}
				String[] dateArray = date.split("/");



				if (dateArray != null && dateArray.length > 0) {

					model.setDay(dateArray[1] + " "
							+ dateArray[0].substring(0, 3));
					model.setMonth(dateArray[2]);


				}



				model.setEndDate(endDate);
				model.setCompletDate(completedate);

				model.setTodosID(innerObj.getString(ServiceResource.TODOSID));

				model.setTitle(innerObj.getString(ServiceResource.TITLETODO));
				model.setDetails(innerObj.getString(ServiceResource.DETAILS));
				model.setUserName(innerObj.getString(ServiceResource.USERNAME));
				model.setTypeTerm(innerObj.getString(ServiceResource.TYPETERM));

				model.setStatus(innerObj.getString(ServiceResource.STATUS));
				model.setOnCreated(innerObj.getString(ServiceResource.ONCREATED));
				model.setYear(innerObj.getString(ServiceResource.YEAR));
				model.setCreateOn(createOn);


				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<ReminderParse>() {
					}.getType();
					ReminderParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.TOADO_TODOSID, parse.getTodosID());
					cv.put(DataBaseHelper.TOADO_ENDDATE, parse.getEndDate());
					cv.put(DataBaseHelper.TOADO_TITLE, parse.getTitle());
					cv.put(DataBaseHelper.TOADO_DETAILS, parse.getDetails());
					cv.put(DataBaseHelper.TOADO_USERNAME, parse.getUserName());
					cv.put(DataBaseHelper.TOADO_TYPETERM, parse.getTypeTerm());
					cv.put(DataBaseHelper.TOADO_COMPLETDATE, parse.getCompletDate());
					cv.put(DataBaseHelper.TOADO_STATUS, parse.getStatus());
					cv.put(DataBaseHelper.TOADO_ONCREATED, parse.getOnCreated());
					cv.put(DataBaseHelper.TOADO_YEAR, parse.getYear());
					cv.put(DataBaseHelper.TOADO_CREATEON, parse.getCreateOn());
					cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_TODOS, cv, DataBaseHelper.TOADO_TODOSID + "='" + parse.getTodosID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_TODOS, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void ParseAddpage(String result,String update){

		JSONArray jsonObj;

		try {
			jsonObj = new JSONArray(result);
			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				PollModel model = new PollModel();

				String date = Utility.getDate(
						Long.valueOf(innerObj
								.getString(ServiceResource.POLL_STARTDATE)
								.replace("/Date(", "").replace(")/", "")),
						"EEEE/dd/MMM/yyyy");
				String[] dateArray = date.split("/");
				if (dateArray != null && dateArray.length > 0) {
					Log.v("date",
							(dateArray[0] + "" + dateArray[1] + "" + dateArray[2]));
					model.setDate(dateArray[1] + " "
							+ dateArray[0].substring(0, 3));
					model.setMonth(dateArray[2]);
					model.setYear(dateArray[3]);
				}

				//				model.setVoteCount(innerObj
				//						.getString(ServiceResource.POLL_VOTECOUNT));
				model.setStartDate(innerObj
						.getString(ServiceResource.POLL_STARTDATE)
						.replace("/Date(", "").replace(")/", ""));
				model.setPollID(innerObj
						.getString(ServiceResource.POLL_POLLID));
				model.setEndDate(innerObj
						.getString(ServiceResource.POLL_ENDDATE)
						.replace("/Date(", "").replace(")/", ""));
				// model.setMonth(month);
				model.setTitle(innerObj
						.getString(ServiceResource.POLL_TITLE));
				model.setDetails(innerObj
						.getString(ServiceResource.POLL_DETAIL));
				model.setStartIn(innerObj
						.getString(ServiceResource.POLL_STARTIN));
				model.setEndIn(innerObj
						.getString(ServiceResource.POLL_ENDIN));
				model.setUserName(innerObj
						.getString(ServiceResource.POLL_USERNAME));

				model.setParticipateCOunt(innerObj
						.getString(ServiceResource.POLL_PARTICIPANT));



				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<PollParse>() {
					}.getType();
					PollParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.POLL_PollID, parse.getPollID());
					cv.put(DataBaseHelper.POLL_STARTDATE, parse.getStartDate());
					cv.put(DataBaseHelper.POLL_ENDDATE, parse.getEndDate());
					cv.put(DataBaseHelper.POLL_TITLE, parse.getTitle());
					cv.put(DataBaseHelper.POLL_DETAILS, parse.getDetails());
					cv.put(DataBaseHelper.POLL_STARTIN, parse.getStartIn());
					cv.put(DataBaseHelper.POLL_ENDIN, parse.getEndIn());
					cv.put(DataBaseHelper.POLL_USERNAME, parse.getUserName());
					cv.put(DataBaseHelper.POLL_PARTICIPANT, parse.getParticipant());

					cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_POLL, cv, DataBaseHelper.POLL_PollID + "='" + parse.getPollID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_POLL, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	public void parseaddparticularpage(String result,String update){

		JSONArray jsonObj;
		JSONObject jobj ;
		JSONArray OptionArray ;
		try {

			if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {
				jobj = new JSONObject(result);
				jsonObj = jobj.getJSONArray(ServiceResource.TABLE);
				OptionArray = jobj.getJSONArray(ServiceResource.TABLE1);
			}else{
				String[] saparatebyhase = result.split("#");
				if(saparatebyhase != null && saparatebyhase.length >2 ) {
					jsonObj = new JSONArray(saparatebyhase[0]);
					OptionArray = new JSONArray(saparatebyhase[1]);
				}else{
					jsonObj = new JSONArray() ;
					OptionArray =  new JSONArray();
				}
			}

			for (int i = 0; i < jsonObj.length(); i++) {
				JSONObject innerObj = jsonObj.getJSONObject(i);
				PollModel model = new PollModel();

				String date = Utility.getDate(
						Long.valueOf(innerObj
								.getString(ServiceResource.POLL_STARTDATE)
								.replace("/Date(", "").replace(")/", "")),
						"EEEE/dd/MMM/yyyy");
				String[] dateArray = date.split("/");
				if (dateArray != null && dateArray.length > 0) {
					Log.v("date",
							(dateArray[0] + "" + dateArray[1] + "" + dateArray[2]));
					model.setDate(dateArray[1] + " "
							+ dateArray[0].substring(0, 3));
					model.setMonth(dateArray[2]);
					model.setYear(dateArray[3]);
				}

				//				model.setVoteCount(innerObj
				//						.getString(ServiceResource.POLL_VOTECOUNT));
				model.setStartDate(innerObj
						.getString(ServiceResource.POLL_STARTDATE)
						.replace("/Date(", "").replace(")/", ""));
				model.setPollID(innerObj
						.getString(ServiceResource.POLL_POLLID));
				model.setEndDate(innerObj
						.getString(ServiceResource.POLL_ENDDATE)
						.replace("/Date(", "").replace(")/", ""));
				// model.setMonth(month);
				model.setTitle(innerObj
						.getString(ServiceResource.POLL_TITLE));
				model.setDetails(innerObj
						.getString(ServiceResource.POLL_DETAIL));

				model.setUserName(innerObj
						.getString(ServiceResource.FULLNAME));

				model.setIsMultichoice(innerObj
						.getString(ServiceResource.ISMULTICHOICE));

				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<PollAnsMainParse>() {
					}.getType();
					PollAnsMainParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.POLL_QUELIST_POLLID, parse.getPollID());
					cv.put(DataBaseHelper.POLL_QUELIST_TITLE, parse.getTitle());
					cv.put(DataBaseHelper.POLL_QUELIST_STARTDATE, parse.getStartDate());
					cv.put(DataBaseHelper.POLL_QUELIST_ENDDATE, parse.getEndDate());
					cv.put(DataBaseHelper.POLL_QUELIST_FULLNAME, parse.getFullName());
					cv.put(DataBaseHelper.POLL_QUELIST_ISMULTICHOICE, parse.getIsMultiChoice());
					cv.put(DataBaseHelper.POLL_QUELIST_SEQNO, parse.getSeqNo());
					cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_POLL_ANS, cv, DataBaseHelper.POLL_QUELIST_POLLID + "='" + parse.getPollID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_POLL_ANS, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}
			}


			for (int i = 0; i < OptionArray.length(); i++) {
				JSONObject innerObj = OptionArray.getJSONObject(i);


				PollOptionModel optionModel = new PollOptionModel();
				optionModel.setPollOptionID(innerObj
						.getString(ServiceResource.POLLOPTIONID));
				optionModel.setPollID(innerObj
						.getString(ServiceResource.POLL_POLLID));
				optionModel.setOption(innerObj
						.getString(ServiceResource.OPTION));

				if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

					Type listType = new TypeToken<PollAnsSubParse>() {
					}.getType();
					PollAnsSubParse parse = new GsonBuilder().create().fromJson(String.valueOf(innerObj), listType);
					ContentValues cv = new ContentValues();
					cv.put(DataBaseHelper.POLL_ANSLIST_POLLOPTIONID, parse.getPollOptionID());
					cv.put(DataBaseHelper.POLL_ANSLIST_POLLID, parse.getPollID());
					cv.put(DataBaseHelper.POLL_ANSLIST_OPTION, parse.getOption());


					cv.put(DataBaseHelper.DATABASE_USERID, userSharedPrefrence.getLOGIN_USERID());

					DataBaseHelper dbHelper = DataBaseHelper.newInstance(getApplicationContext());
					if (dbHelper.update(DataBaseHelper.TBL_POLL_OPTIONS, cv, DataBaseHelper.POLL_ANSLIST_POLLOPTIONID + "='" + parse.getPollOptionID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + userSharedPrefrence.getLOGIN_USERID() + "'") == 0) {
						dbHelper.insert(DataBaseHelper.TBL_POLL_OPTIONS, cv);
//						Log.v("circular", "insert");
					} else {
//						Log.v("circular", "update");
					}
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	public void  onlyVideoParse(String result,String update,int count,String fileType){


		boolean isMoreData = false;
		if (result != null
				&& !result.toString().equals(
				"[{\"message\":\"No Data Found\",\"success\":0}]")) {
			try {
				JSONArray hJsonArray = new JSONArray(result);
				for (int i = 0; i < hJsonArray.length(); i++) {
					JSONObject hJsonObject = hJsonArray.getJSONObject(i);
					WallPostModel model = new WallPostModel();
					model.setFileMimeType(hJsonObject
							.getString("FileMimeType"));
					model.setPostCommentID(hJsonObject
							.getString("PostCommentID"));
					model.setPhoto(ServiceResource.BASE_IMG_URL+"/DataFiles/"+hJsonObject
							.getString("Photo").replace("//DataFiles//", "/DataFiles/")
							.replace("//DataFiles/", "/DataFiles/"));
					model.setDateOfPost(Utility.getDate(Long.valueOf(hJsonObject
							.getString("DateOfPost").replace("/Date(", "").replace(")/", "")),"dd-MM-yyyy"));
					model.setFileType(hJsonObject
							.getString("FileType"));



					if(update.equalsIgnoreCase(ServiceResource.UPDATE)) {

						Type listType = new TypeToken<OnlyPhotoVideoParse>() {
						}.getType();
						OnlyPhotoVideoParse parse = new GsonBuilder().create().fromJson(String.valueOf(hJsonObject), listType);
						ContentValues cv = new ContentValues();
						cv.put(DataBaseHelper.ONLYPHOTOVIDEO_POSTCOMMENTID, parse.getPostCommentID());
						cv.put(DataBaseHelper.ONLYPHOTOVIDEO_DATEOFPOST, parse.getDateOfPost());
						cv.put(DataBaseHelper.ONLYPHOTOVIDEO_PHOTO, parse.getPhoto());
						cv.put(DataBaseHelper.ONLYPHOTOVIDEO_FILETYPE, parse.getFileType());
						cv.put(DataBaseHelper.ONLYPHOTOVIDEO_FILEMIMETYPE, parse.getFileMimeType());

						cv.put(DataBaseHelper.DATABASE_USERID,userSharedPrefrence.getLOGIN_USERID());

						DataBaseHelper dbHelper =DataBaseHelper.newInstance(mContext);
						if (dbHelper.update(DataBaseHelper.TBL_ONLYPHOTOVIDEO, cv, DataBaseHelper.ONLYPHOTOVIDEO_POSTCOMMENTID + "='" + parse.getPostCommentID() + "' AND " + DataBaseHelper.DATABASE_USERID + "='" + new UserSharedPrefrence(mContext).getLOGIN_USERID() + "'") == 0) {
							dbHelper.insert(DataBaseHelper.TBL_ONLYPHOTOVIDEO, cv);
//						Log.v("circular", "insert");
						} else {
//						Log.v("circular", "update");
						}
					}else {

					}
				}
				isMoreData = true;


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			isMoreData = false;
		}






		if(isMoreData){

			count = count +10;
			Video(count,fileType);
		}


	}
}
