package com.phonesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.phonesafe.R;
import com.phonesafe.fragment.LockFragment;
import com.phonesafe.fragment.UnLockFragment;

public class AppLockActivity extends FragmentActivity implements
		OnClickListener {

	private FrameLayout fl_content;
	private TextView tv_unlock;
	private TextView tv_lock;
	private FragmentManager fragmentManager;
	private UnLockFragment unLockFragment;
	private LockFragment lockFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_app_lock);

		fl_content = (FrameLayout) findViewById(R.id.fl_content);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);

		tv_lock = (TextView) findViewById(R.id.tv_lock);

		tv_unlock.setOnClickListener(this);
		tv_lock.setOnClickListener(this);
		//获取到fragment的管理者

		fragmentManager = getSupportFragmentManager();
		//开启事务
		FragmentTransaction mTransaction = fragmentManager.beginTransaction();

		unLockFragment = new UnLockFragment();

		lockFragment = new LockFragment();
		/**
		 * 替换界面
		 * 1 需要替换的界面的id
		 * 2具体指某一个fragment的对象
		 */
		mTransaction.replace(R.id.fl_content, unLockFragment).commit();



	}

	@Override
	public void onClick(View v) {

		FragmentTransaction ft = fragmentManager.beginTransaction();
		switch (v.getId()) {
			case R.id.tv_unlock:
				//没有加锁
				tv_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
				tv_lock.setBackgroundResource(R.mipmap.tab_right_default);
				ft.replace(R.id.fl_content, unLockFragment);
				System.out.println("切换到unlockFragment");

				break;

			case R.id.tv_lock:
				//没有加锁
				tv_unlock.setBackgroundResource(R.mipmap.tab_left_default);
				tv_lock.setBackgroundResource(R.mipmap.tab_right_pressed);
				ft.replace(R.id.fl_content, lockFragment);
				System.out.println("切换到lockFragment");

				break;
		}
		ft.commit();
	}
}
