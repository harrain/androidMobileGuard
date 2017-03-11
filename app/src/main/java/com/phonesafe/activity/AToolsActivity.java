package com.phonesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.phonesafe.R;
import com.phonesafe.utils.SmsUtils;
import com.phonesafe.utils.UIUtils;

import static android.R.attr.max;
import static android.R.attr.process;

/**
 * 高级工具
 * 
 *
 * 
 */
public class AToolsActivity extends Activity {

	private static final int PROCESS = 0;
	private static final int MAX = 1;
	private static final int DISMISS = 2;

	private ProgressDialog pd;
	@ViewInject(R.id.progressBar1)
	private ProgressBar progressBar1;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case PROCESS:
					int process ;
					process = msg.arg1;
					pd.setProgress(process);
					progressBar1.setProgress(process);
					break;
				case MAX:
					int count;
					count = msg.arg2;
					pd.setMax(count);
					progressBar1.setMax(count);
					break;
				case DISMISS:
					pd.dismiss();
					break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);

	}

	/**
	 * 归属地查询
	 * 
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		startActivity(new Intent(this, AddressActivity.class));
	}

	/**
	 * 程序锁
	 * @param view
	 */
	public void appLock(View view){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}

	/**
	 * 备份短信
	 * @param view
	 */
	public void backUpsms(View view){
		//初始化一个进度条的对话框
		pd = new ProgressDialog(AToolsActivity.this);
		pd.setTitle("提示");
		pd.setMessage("稍安勿躁。正在备份。你等着吧。。");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				boolean result = SmsUtils.backUp(AToolsActivity.this,new SmsUtils.BackUpCallBackSms() {

					@Override
					public void onBackUpSms(int process) {
						Message msg = Message.obtain();
						msg.what = PROCESS;
						msg.arg1 = process;
						handler.sendMessage(msg);
					}

					@Override
					public void befor(int count) {
						Message msg = Message.obtain();
						msg.what = MAX;
						msg.arg2 = count;
						handler.sendMessage(msg);

					}
				});
				if(result){
					//安全弹吐司的方法
					UIUtils.showToast(AToolsActivity.this, "备份成功");
				}else{
					UIUtils.showToast(AToolsActivity.this, "备份失败");
				}
				Message msg = Message.obtain();
				msg.what = DISMISS;
				handler.sendMessage(msg);

			};
		}.start();


	}

}
