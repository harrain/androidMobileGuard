package com.phonesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.phonesafe.R;

/**
 * 烟雾背景
 *
 *
 */
public class BackgroundActivity extends Activity {

	private KillReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bg);

		ImageView ivTop = (ImageView) findViewById(R.id.iv_top);
		ImageView ivBottom = (ImageView) findViewById(R.id.iv_bottom);

		// 渐变动画
		AlphaAnimation anim = new AlphaAnimation(0, 1);
		anim.setDuration(800);
		anim.setFillAfter(true);// 动画结束后保持状态

		// 运行动画
		ivTop.startAnimation(anim);
		ivBottom.startAnimation(anim);

		receiver=new KillReceiver();
		IntentFilter filter=new IntentFilter("com.mobile.kill");
		registerReceiver(receiver, filter);

		/*new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
			}
		}, 1000);// 延时1秒后结束activity*/
	}


	class KillReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
}
