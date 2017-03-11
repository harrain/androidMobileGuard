package com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.phonesafe.R;

/**
 * 第一个设置向导页
 * 
 * @author Kevin
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		super.showNextPage();
	}

	@Override
	public void showPreviousPage() {
		super.showPreviousPage();
	}

}
