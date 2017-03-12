package com.phonesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.phonesafe.R;
import com.phonesafe.service.AddressService;
import com.phonesafe.service.CallSafeService;
import com.phonesafe.service.RocketService;
import com.phonesafe.service.WatchDogService;
import com.phonesafe.utils.MD5Utils;
import com.phonesafe.utils.ServiceStatusUtils;
import com.phonesafe.view.SettingClickView;
import com.phonesafe.view.SettingItemView;

/**
 * 设置中心
 * 
 *
 * 
 */
public class SettingActivity extends Activity implements OnClickListener {

	private SettingItemView sivUpdate;// 设置升级
	private SettingItemView sivAddress;// 设置升级
	private SettingClickView scvAddressStyle;// 修改风格
	private SettingClickView scvAddressLocation;// 修改归属地位置
	private SettingItemView siv_callsafe;// 黑名单
	private SettingItemView sv_watch_dog;

	private Intent watchDogIntent;
	private SharedPreferences mPref;
	private String dialogTitle = new String("远程锁屏密码设置");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initRemoteLockScreen();
		initRocketSetting();
		initBlackView();
		initWatchDog();
		setSoftWareLockPass();
	}

	/**
	 * 初始化自动更新开关
	 */
	private void initUpdateView() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// sivUpdate.setTitle("自动更新设置");

		boolean autoUpdate = mPref.getBoolean("auto_update", true);

		if (autoUpdate) {
			// sivUpdate.setDesc("自动更新已开启");
			sivUpdate.setChecked(true);
		} else {
			// sivUpdate.setDesc("自动更新已关闭");
			sivUpdate.setChecked(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (sivUpdate.isChecked()) {
					// 设置不勾选
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("自动更新已关闭");
					// 更新sp
					mPref.edit().putBoolean("auto_update", false).commit();
				} else {
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("自动更新已开启");
					// 更新sp
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	/**
	 * 初始化归属地开关
	 */
	private void initAddressView() {
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);

		// 根据归属地服务是否运行来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.phonesafe.service.AddressService");

		if (serviceRunning) {
			sivAddress.setChecked(true);
		} else {
			sivAddress.setChecked(false);
		}

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));// 停止归属地服务
				} else {
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));// 开启归属地服务
				}
			}
		});
	}

	final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

	/**
	 * 修改提示框显示风格
	 */
	private void initAddressStyle() {
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);

		scvAddressStyle.setTitle("归属地提示框风格");

		int style = mPref.getInt("address_style", 0);// 读取保存的style
		scvAddressStyle.setDesc(items[style]);

		scvAddressStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSingleChooseDailog();
			}
		});
	}

	/**
	 * 弹出选择风格的单选框
	 */
	protected void showSingleChooseDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示框风格");

		int style = mPref.getInt("address_style", 0);// 读取保存的style

		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPref.edit().putInt("address_style", which).commit();// 保存选择的风格
						dialog.dismiss();// 让dialog消失

						scvAddressStyle.setDesc(items[which]);// 更新组合控件的描述信息
					}
				});

		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 修改归属地显示位置
	 */
	private void initAddressLocation() {
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("归属地提示框显示位置");
		scvAddressLocation.setDesc("设置归属地提示框的显示位置");

		scvAddressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,
						DragViewActivity.class));
			}
		});
	}

	public void initRocketSetting(){
		final SettingItemView sivRocket = (SettingItemView) findViewById(R.id.siv_startRocket);

		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,"com.mobilesafe.service.RocketService");
		if (serviceRunning){
			sivRocket.setChecked(true);
		}else {
			sivRocket.setChecked(false);
		}
		sivRocket.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sivRocket.isChecked()){
					sivRocket.setChecked(false);

					stopService(new Intent(SettingActivity.this,RocketService.class));
				}else {
					sivRocket.setChecked(true);
					startService(new Intent(SettingActivity.this,RocketService.class));
				}
			}
		});


	}

	/**
	 * 初始化黑名单
	 */
	private void initBlackView() {
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		// 根据归属地服务是否运行来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.itheima52.mobilesafe.service.CallSafeService");

		if (serviceRunning) {
			siv_callsafe.setChecked(true);
		} else {
			siv_callsafe.setChecked(false);
		}

		siv_callsafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsafe.isChecked()) {
					siv_callsafe.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							CallSafeService.class));// 停止归属地服务
				} else {
					siv_callsafe.setChecked(true);
					startService(new Intent(SettingActivity.this,
							CallSafeService.class));// 开启归属地服务
				}
			}
		});
	}

	public void initWatchDog(){
		// 看萌狗
		sv_watch_dog = (SettingItemView) findViewById(R.id.sv_watch_dog);
		boolean isRun = ServiceStatusUtils.isServiceRunning(this,"com.phonesafe.service.WatchDogService");
		if (isRun){
			sv_watch_dog.setChecked(true);
		}else {
			sv_watch_dog.setChecked(false);
		}
		watchDogIntent = new Intent(this, WatchDogService.class);
		sv_watch_dog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sv_watch_dog.isChecked()) {
					sv_watch_dog.setChecked(false);
					// 停止拦截服务
					stopService(watchDogIntent);
				} else {
					sv_watch_dog.setChecked(true);
					// 开启拦截服务
					startService(watchDogIntent);
				}
			}
		});
	}

	public void setSoftWareLockPass(){
		SettingClickView swlp = (SettingClickView) findViewById(R.id.slv_software_lockpass);
		swlp.setTitle("软件锁密码设置");
		swlp.setDesc("点击设置软件锁密码");

		swlp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startsoftlockdialog();
				dialogTitle = "rrrrrr";
				Log.e("dialogTitle",dialogTitle);
			}
		});

	}

	private void startsoftlockdialog(){
		showPasswordSetDailog(new String("软件锁密码设置"),new AlertDialogEngineInterface() {
			@Override
			public void saveSp(String value) {
				if (value == null){
					return;
				}
				mPref.edit().putString("softwareLockPass",MD5Utils.encode(value)).commit();
			}

		});
	}

	public void initRemoteLockScreen(){
		SettingClickView rls = (SettingClickView) findViewById(R.id.siv_remoteLockScreen);
		rls.setTitle("远程锁屏密码设置");
		rls.setDesc("点击进行设置密码");

		rls.setOnClickListener(this);

	}

	private void lala(String s){
		Log.e("---",s);
	}

	public void startremotelockdialog(){
		lala("远程锁屏密码设置");

		showPasswordSetDailog("远程锁屏密码设置",new AlertDialogEngineInterface() {
			@Override
			public void saveSp(String value) {
				if (value == null){
					return;
				}
				mPref.edit()
						.putString("lockScreenPass",
								MD5Utils.encode(value)).commit();
			}

		});
	}

	public void showPasswordSetDailog(String title, final AlertDialogEngineInterface dialogInterface) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(this, R.layout.dailog_set_password, null);
		//dialog.setView(view);// 将自定义的布局文件设置给dialog
		dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
		TextView tvTitle = (TextView) view.findViewById(R.id.dialog_tv_title);
		//CharSequence charSequence = title.subSequence(0,title.length());
		Log.e("xxx",title);
		dialogTitle = "sjkljjka";
		Log.e("dialogTitle",dialogTitle);
		tvTitle.setText(title);
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);
		final EditText etPasswordConfirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				String passwordConfirm = etPasswordConfirm.getText().toString();
				// password!=null && !password.equals("")
				if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
					if (password.equals(passwordConfirm)) {
						dialogInterface.saveSp(password);

						dialog.dismiss();

					} else {
						Toast.makeText(SettingActivity.this, "两次密码不一致!",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SettingActivity.this, "输入框内容不能为空!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();// 隐藏dialog
			}
		});

		dialog.show();

		try {

			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width= 700;
			//params.height=700;
			params.alpha = 1f;
			dialog.getWindow().setAttributes(params);

		}catch (NullPointerException e){
			Log.wtf("alertDialogwindow",e);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.siv_remoteLockScreen:
				startremotelockdialog();
				break;
		}
	}

	private interface AlertDialogEngineInterface{

		void saveSp(String value);

	}
}
