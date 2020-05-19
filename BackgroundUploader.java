package com.orataro.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.orataro.util.Global;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

class BackgroundUploader extends AsyncTask<Void, Integer, Void> implements DialogInterface.OnCancelListener {

	private ProgressDialog progressDialog;
	private String url;
	private File file;
	Context mContext;

	public BackgroundUploader(Context mContext,String url, File file) {
		this.url = url;
		this.file = file;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("Uploading...");
		progressDialog.setCancelable(false);
		progressDialog.setMax((int) file.length());
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... v) {
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connection = null;
		String fileName = file.getName();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			String boundary = "---------------------------boundary";
			String tail = "\r\n--" + boundary + "--\r\n";
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setDoOutput(true);

			String metadataPart = "--" + boundary + "\r\n" 
					+ "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
					+ "" + "\r\n";

			String fileHeader1 = "--" + boundary + "\r\n"
					+ "Content-Disposition: form-data; name=\"UploadFile\"; filename=\""
					+ fileName + "\"\r\n"
					+"; ClientID=\"" + Global.userdataModel.getClientID() + "\"\r\n"
					+"; MemberID=\"" + Global.userdataModel.getMemberID() + "\"\r\n"
					+"; InstituteID=\"" + Global.userdataModel.getInstituteID() + "\"\r\n"
					+"; FileType=\"" +"IMAGE" + "\"\r\n"
					+ "Content-Type: application/octet-stream\r\n"
					+ "Content-Transfer-Encoding: binary\r\n";

			long fileLength = file.length() + tail.length();
			String fileHeader2 = "Content-length: " + fileLength + "\r\n";
			String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
			String stringData = metadataPart + fileHeader;

			long requestLength = stringData.length() + fileLength;
			connection.setRequestProperty("Content-length", "" + requestLength);
			connection.setFixedLengthStreamingMode((int) requestLength);
			connection.connect();

			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(stringData);
			out.flush();

			int progress = 0;
			int bytesRead = 0;
			byte buf[] = new byte[1024];
			BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
			while ((bytesRead = bufInput.read(buf)) != -1) {
				// write output
				out.write(buf, 0, bytesRead);
				out.flush();
				progress += bytesRead;
				// update progress bar
				publishProgress(progress);
			}

			// Write closing boundary and close stream
			out.writeBytes(tail);
			out.flush();
			out.close();

			// Get server response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			StringBuilder builder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}

		} catch (Exception e) {
			// Exception
		} finally {
			if (connection != null) connection.disconnect();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		progressDialog.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(Void v) {
		progressDialog.dismiss();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cancel(true);
		dialog.dismiss();
	}
}
