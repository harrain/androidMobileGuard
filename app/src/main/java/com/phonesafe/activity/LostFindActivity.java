package com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonesafe.R;

/**
 * 手机防盗页面
 * 
 *
 * 
 */
public class LostFindActivity extends Activity {

	private SharedPreferences mPrefs;
	private TextView tvSafePhone;
	private ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

	}

	@Override
	protected void onStart() {
		super.onStart();
		boolean configed = mPrefs.getBoolean("configed", false);// 判断是否进入过设置向导
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			// 根据sp更新安全号码
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPrefs.getString("safe_phone", "");
			tvSafePhone.setText(phone);
			// 根据sp更新保护锁
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPrefs.getBoolean("protect", false);
			if (protect) {
				ivProtect.setImageResource(R.mipmap.lock);
			} else {
				ivProtect.setImageResource(R.mipmap.unlock);
			}

		} else {
			// 跳转设置向导页
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}
	}

	/**
	 * 重新进入设置向导
	 * 
	 * @param view
	 */
	public void reEnter(View view) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		/*Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//从此启动的HomeActivity当按返回键是它还会返回到HomeActivity。normal情况下返回就退出应用了
		startActivity(intent);*/
		finish();
	}
}
