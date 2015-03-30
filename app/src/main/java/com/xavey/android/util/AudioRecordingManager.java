package com.xavey.android.util;

import java.io.File;

import android.app.Activity;
import android.media.MediaRecorder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.xavey.android.ApplicationValues;
import com.xavey.android.R;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Audio;

public class AudioRecordingManager {
	Activity activity_;
	
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };
	XaveyDBHelper dbHelper = new XaveyDBHelper(activity_);
	
	Button btnStart;
	Button btnStop;
	
	public AudioRecordingManager(Activity activity){
		activity_ = activity;
	}
	
	public LinearLayout getRecordingLayout(){
		btnStart = new Button(activity_);
		btnStop = new Button(activity_);
		btnStart.setText("record");
		btnStop.setText("stop");
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		btnStart.setLayoutParams(params);
		btnStop.setLayoutParams(params);
		final ToastManager xToast = new ToastManager(activity_);

		final LinearLayout recordingLayout = new LinearLayout(activity_);
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				xToast.xaveyToast(null, "Start Recording....");
				enableButtons(true);
				boolean isRecording = true;
				btnStart.setEnabled(!isRecording);
				btnStop.setEnabled(isRecording);
				startRecording();
				String audio_path = getFilename();	
				recordingLayout.setTag(R.id.audio_path, audio_path);
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				xToast.xaveyToast(null, "Recording is stopped..");
				boolean isRecording = false;
				btnStart.setEnabled(!isRecording);
				btnStop.setEnabled(isRecording);
				stopRecording();
			}
		});
		
		LayoutParams recordingLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		recordingLayoutParams.setMargins(30, 10, 10, 10);
		recordingLayout.setLayoutParams(recordingLayoutParams);
		recordingLayout.setOrientation(LinearLayout.HORIZONTAL);
		recordingLayout.addView(btnStart);
		recordingLayout.addView(btnStop);
		return recordingLayout;
	}

	private void enableButton(Button button, boolean isEnable){
		button.setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording){
		
	}
	
	private String getFilename() {
		File file = new File(ApplicationValues.XAVEY_DIRECTORY, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + fileName + file_exts[currentFormat]);
	}

	public String getFilename(String fileName_) {
		File file = new File(ApplicationValues.XAVEY_DIRECTORY, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + fileName_ + file_exts[currentFormat]);
	}
	
	String fileName="xxx";
	
	public void setFileName(String file_name){
		fileName = file_name;
	}
	
	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			ApplicationValues.IS_RECORDING_NOW = true;
			recorder.prepare();
			recorder.start();
		}catch (Exception e){
			e.printStackTrace();
			ApplicationValues.IS_RECORDING_NOW = false;
		}
	}

	public void stopRecording() {
		ApplicationValues.IS_RECORDING_NOW = false;
		//boolean isAudioAlreadyExist = dbHelper.isAudioAlreadyExistInDB(getAudioInfo().getAudio_path());
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
			
//			if(!isAudioAlreadyExist){
//				dbHelper.addNewAudio(getAudioInfo());
//			}
//			else{
//				dbHelper.updateAudioByPath(getAudioInfo());
//			}
		}
	}

	public void triggerStopClick(){
		btnStop.performClick();
	}
	public Audio getAudioInfo() {
		audioInfo.setAudio_path(getFilename());
		return audioInfo;
	}

	public void setAudioInfo(Audio audioInfo) {
		this.audioInfo = audioInfo;
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(activity_,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(activity_,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};
	
	private Audio audioInfo;
	
}
