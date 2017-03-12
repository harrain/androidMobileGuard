package com.phonesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.phonesafe.R;
import com.phonesafe.utils.ToastUtils;

import java.util.Arrays;

/**
 * 第3个设置向导页
 * 
 * @author Kevin
 * 
 */
public class Setup3Activity extends BaseSetupActivity implements OnPermissionCallback{

	private EditText etPhone;
	private PermissionHelper permissionHelper;
	private boolean isSingle;
	private final static String[] MULTI_PERMISSIONS = new String[]{

			Manifest.permission.SEND_SMS,
			Manifest.permission.RECEIVE_SMS,
			Manifest.permission.READ_SMS,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		permissionHelper = PermissionHelper.getInstance(this);

		etPhone = (EditText) findViewById(R.id.et_phone);
		String phone = mPref.getString("safe_phone", "");
		etPhone.setText(phone);
	}



	@Override
	public void showNextPage() {
		String phone = etPhone.getText().toString().trim();// 注意过滤空格

		if (TextUtils.isEmpty(phone)) {
			// Toast.makeText(this, "安全号码不能为空!", Toast.LENGTH_SHORT).show();
			ToastUtils.showToast(this, "安全号码不能为空!");
			return;
		}

		mPref.edit().putString("safe_phone", phone).commit();// 保存安全号码

		permissionHelper
				.setForceAccepting(false) // default is false. its here so you know that it exists.
				.request(MULTI_PERMISSIONS);

		startActivity(new Intent(this, Setup4Activity.class));
		super.showNextPage();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		super.showPreviousPage();
	}

	/**
	 * 选择联系人
	 *
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent = new Intent(this, ContactActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// System.out.println("resultCode:" + resultCode);
		// System.out.println("requestCode:" + requestCode);
		permissionHelper.onActivityForResult(requestCode);
		if (resultCode == Activity.RESULT_OK) {
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");// 替换-和空格

			etPhone.setText(phone);// 把电话号码设置给输入框
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override public void onPermissionGranted(@NonNull String[] permissionName) {
		Log.i("onPermissionGranted", "Permission(s) " + Arrays.toString(permissionName) + " Granted");
	}

	@Override public void onPermissionDeclined(@NonNull String[] permissionName) {
		Log.i("onPermissionDeclined", "Permission(s) " + Arrays.toString(permissionName) + " Declined");
	}

	@Override public void onPermissionPreGranted(@NonNull String permissionsName) {
		Log.i("onPermissionPreGranted", "Permission( " + permissionsName + " ) preGranted");
	}

	@Override public void onPermissionNeedExplanation(@NonNull String permissionName) {
		Log.i("NeedExplanation", "Permission( " + permissionName + " ) needs Explanation");

	}

	@Override public void onPermissionReallyDeclined(@NonNull String permissionName) {
		Log.i("ReallyDeclined", "Permission " + permissionName + " can only be granted from settingsScreen");
		/** you can call  {@link PermissionHelper#openSettingsScreen(Context)} to open the settings screen */
	}

	@Override public void onNoPermissionNeeded() {
		Log.i("onNoPermissionNeeded", "Permission(s) not needed");
	}
}
