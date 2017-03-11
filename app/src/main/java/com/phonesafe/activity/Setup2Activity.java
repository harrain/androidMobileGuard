package com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.phonesafe.R;
import com.phonesafe.utils.ToastUtils;
import com.phonesafe.view.SettingItemView;

/**
 * 第2个设置向导页
 * 
 * @author Kevin
 * 
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView sivSim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		sivSim = (SettingItemView) findViewById(R.id.siv_sim);

		String sim = mPref.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			sivSim.setChecked(true);
		} else {
			sivSim.setChecked(false);
		}

		sivSim.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivSim.isChecked()) {
					sivSim.setChecked(false);
					mPref.edit().remove("sim").commit();// 删除已绑定的sim卡
				} else {
					sivSim.setChecked(true);
					// 保存sim卡信息
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();// 获取sim卡序列号
					System.out.println("sim卡序列号:" + simSerialNumber);

					mPref.edit().putString("sim", simSerialNumber).commit();// 将sim卡序列号保存在sp中
				}
			}
		});
	}

	long[] mHits = new long[2];
	@Override
	public void showNextPage() {
		// 如果sim卡没有绑定,就不允许进入下一个页面
		String sim = mPref.getString("sim", null);
		//		src 原数组
//		srcPos 原数组拷贝的开始位置
//		dst 目标数组
//		dstPos 目标数组的开始位置
//		length 拷贝元素的长度
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();
		if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
			startActivity(new Intent(this, Setup3Activity.class));
			super.showNextPage();
			return;
		}

		if (TextUtils.isEmpty(sim)) {
			ToastUtils.showToast(this, "必须绑定sim卡!");
			return;
		}

		startActivity(new Intent(this, Setup3Activity.class));

		super.showNextPage();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));

		super.showPreviousPage();
	}

}
