package com.orataro.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SensorRestarterBroadcastReceiver  extends BroadcastReceiver  {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		context.startService(new Intent(context, SyncService.class));
	}

}
