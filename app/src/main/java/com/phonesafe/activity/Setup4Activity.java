package com.phonesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.phonesafe.R;
import com.phonesafe.receiver.AdminReceiver;

/**
 * 第4个设置向导页
 * 
 *
 * 
 */
public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtect;

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		cbProtect = (CheckBox) findViewById(R.id.cb_protect);

		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);// 设备管理组件

		boolean protect = mPref.getBoolean("protect", false);

		// 根据sp保存的状态,更新checkbox
		if (protect) {
			cbProtect.setText("防盗保护已经开启");
			cbProtect.setChecked(true);
		} else {
			cbProtect.setText("防盗保护没有开启");
			cbProtect.setChecked(false);
		}

		// 当checkbox发生变化时,回调此方法
		cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				if (isChecked) {
					cbProtect.setText("防盗保护已经开启");
					activeAdmin();
					mPref.edit().putBoolean("protect", true).commit();
				} else {
					cbProtect.setText("防盗保护没有开启");
					mDPM.removeActiveAdmin(mDeviceAdminSample);// 取消激活
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	// 激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
	public void activeAdmin() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"哈哈哈, 我们有了超级设备管理器, 好NB!");
		startActivity(intent);
	}

	@Override
	public void showNextPage() {
		mPref.edit().putBoolean("configed",true).commit();// 更新sp,表示已经展示过设置向导了,下次进来就不展示啦
		startActivity(new Intent(this, LostFindActivity.class));
		super.showNextPage();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		super.showPreviousPage();
	}

}
