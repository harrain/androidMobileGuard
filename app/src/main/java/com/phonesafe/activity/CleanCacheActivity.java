package com.phonesafe.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.phonesafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *
 * 描 述 ：
 *
 *      缓存清理
 * 修订历史 ：
 *
 * ============================================================
 **/
public class CleanCacheActivity extends Activity {

	private ListView listview;
	PackageManager packageManager;
	private List<CacheInfo> cacheList;
	private CacheAdapter cacheAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUI();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			cacheAdapter = new CacheAdapter();
			listview.setAdapter(cacheAdapter);
		}
	};

	private void initUI() {
		setContentView(R.layout.activity_clean_cache);

		listview = (ListView)findViewById(R.id.list_view_cleancache);

		cacheList = new ArrayList<>();
		packageManager = getPackageManager();
		/**
		 * 接收2个参数
		 * 第一个参数接收一个包名
		 * 第二个参数接收aidl的对象
		 */
//		  * @hide
//		     */
//		    public abstract void getPackageSizeInfo(String packageName,
//		            IPackageStatsObserver observer);
//		packageManager.getPackageSizeInfo();

		new Thread(new Runnable() {
			@Override
			public void run() {
				getAllCachePackages();

				handler.sendEmptyMessage(0);
			}
		}).start();

	}

	private void getAllCachePackages() {
		//安装到手机上面所有的应用程序
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

		for (PackageInfo packageInfo : installedPackages) {
            getCacheSize(packageInfo);
        }
	}

	/**
	 * 全部清除
	 * @param view
     */
	public void cleanAll(View view){
		try {
			Method[] methods = PackageManager.class.getMethods();
			for (Method method:methods) {
				if (method.getName().equals("freeStorageAndNotify")){
					Log.e("PackageManager","freeStorageAndNotify");
					method.invoke(packageManager,Integer.MAX_VALUE,new MyIPackageDataObserver());
				}
			}
			cacheList.clear();
			getAllCachePackages();
			cacheAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getCacheSize(PackageInfo packageInfo) {
		try {
			//Class<?> clazz = getClassLoader().loadClass("PackageManager");
			//通过反射获取到当前的方法
			Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);//IPackageStatsObserver.class
			//Log.e("loadClass",packageInfo.applicationInfo.loadLabel(packageManager).toString());
			/**
			 * 第一个参数表示当前的方法是由哪个对象调用的，
			 * 接下来的参数就是要反射得到的getPackageInfo方法参数 1:包名 ,不是应用名 ; 2 :stateAIDL
			 */
			method.invoke(packageManager,packageInfo.packageName,new MyIPackageStatsObserver(packageInfo));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub{

		private PackageInfo packageInfo;

		public MyIPackageStatsObserver(PackageInfo packageInfo){
			//Log.e("stateobserver","new");
			this.packageInfo = packageInfo;
		}
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			//得到当前应用的缓存大小
			long cacheSize = pStats.cacheSize;
			Log.e("getstate","...."+cacheSize);//03-10 23:43:45.594 13100-13112/com.phonesafe E/getstate: ....0 拿不到缓存，看来只能通过文件读取了
			if (cacheSize > 0){
				String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
				Log.e("当前应用的名字",name+"---"+"缓存："+cacheSize+"");
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.apkName = name;
				cacheInfo.cacheSize = cacheSize;
				cacheInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);
				cacheList.add(cacheInfo);//几个内部类可以通过new对象，然后add进集合里进行保存
			}

		}
	}
	//找不到新粘贴进来的aidl文件，就clean下工程 ; aidl 即使有错也不需要修改
	private  class MyIPackageDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

		}
	}


	private class CacheAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return cacheList.size();
		}

		@Override
		public Object getItem(int i) {
			return cacheList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (view == null){
				view = LayoutInflater.from(CleanCacheActivity.this).inflate(R.layout.item_clean_cache,null);
				holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.appName = (TextView) view.findViewById(R.id.tv_name);
				holder.cacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
				view.setTag(holder);
			}else {
				holder = (ViewHolder) view.getTag();
			}
			holder.icon.setImageDrawable(cacheList.get(i).icon);
			holder.cacheSize.setText("缓存大小："+ Formatter.formatFileSize(CleanCacheActivity.this,cacheList.get(i).cacheSize));
			holder.appName.setText(cacheList.get(i).apkName);
			return view;
		}
	}

	static class ViewHolder{
		ImageView icon;
		TextView appName;
		TextView cacheSize;
	}

	static class CacheInfo{
		long cacheSize;
		String apkName;
		Drawable icon;
	}
}
