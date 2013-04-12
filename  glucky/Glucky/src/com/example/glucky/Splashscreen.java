package com.example.glucky;

import java.io.IOException;

import com.glucky.utils.DBUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.Toast;



public class Splashscreen extends Activity {
	private CountDownTimer currentCountDownTimer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashglucky);
		try {
			DBUtils.createDatabaseIfNotExists(Splashscreen.this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(Splashscreen.this, e.toString(), Toast.LENGTH_SHORT).show();
		}
		currentCountDownTimer = new SplashCountDownTimer();
		currentCountDownTimer.start();		
	
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			cancelTimer();
		}
		return true;
	}
	private void cancelTimer() {
		currentCountDownTimer.cancel();
		currentCountDownTimer.onFinish();
	}

	private class SplashCountDownTimer extends CountDownTimer {
		private static final int SPLASH_TIME = 5000;
		private static final int COUNT_DOWN_INTERVAL = 100;
		
		public SplashCountDownTimer() {
			this(SPLASH_TIME, COUNT_DOWN_INTERVAL);
		}

		public SplashCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish();
//			startActivity(new Intent(new Intent(SplashScreenActivity.this, AdSplashScreenActivity.class)));
			startActivity(new Intent(new Intent(Splashscreen.this, MainActivity.class)));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			; // Do nothing
		}
	}
	
	@Override
	public void onBackPressed() {
		;
	}
}
