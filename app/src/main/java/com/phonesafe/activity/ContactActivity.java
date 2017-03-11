package com.phonesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.phonesafe.R;

public class ContactActivity extends Activity {

	private ListView lvList;
	private ProgressBar pb;
	private ArrayList<HashMap<String, String>> readContact = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);


		new Thread(new Runnable() {
			@Override
			public void run() {
				permission();
				if (readContact.size() > 0) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pb = (ProgressBar) findViewById(R.id.pb);
							pb.setVisibility(View.GONE);
							lvList = (ListView) findViewById(R.id.lv_list);

							// System.out.println(readContact);
							lvList.setAdapter(new SimpleAdapter(ContactActivity.this, readContact,
									R.layout.contact_list_item, new String[]{"name", "phone"},
									new int[]{R.id.tv_name, R.id.tv_phone}));

							lvList.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
														int position, long id) {
									String phone = readContact.get(position).get("phone");// 读取当前item的电话号码
									Intent intent = new Intent();
									intent.putExtra("phone", phone);
									setResult(Activity.RESULT_OK, intent);// 将数据放在intent中返回给上一个页面

									finish();
								}
							});
							lvList.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		}).start();

	}

	private void permission(){
		Log.e("start","permission");
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			Log.e("startcompat","permission");

			ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS }, 1);

		} else {
			readContacts();
		}
	}

	private void readContacts() {
		Cursor cursor = null;
		try {
			// 查询联系人数据
			cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			Log.e("cursor","permission");

			if (cursor != null) {
				while (cursor.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					// 获取联系人姓名
					String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					// 获取联系人手机号
					String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					map.put("name", displayName);
					Log.e("name",displayName);
					map.put("phone",number);
					readContact.add(map);
					map = null;
				}
				//adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					readContacts();
					Log.e("permissionresult","on");
				} else {
					Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
		}
	}

	private ArrayList<HashMap<String, String>> readContact() {//d废弃不用
		// 首先,从raw_contacts中读取联系人的id("contact_id")
		// 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
		// 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// 从raw_contacts中读取联系人的id("contact_id")
		Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactsCursor != null) {
			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				// System.out.println(contactId);

				// 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);

				Log.e("cursor",dataCursor.toString());
				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						// System.out.println(contactId + ";" + data1 + ";"
						// + mimetype);
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
					}

					list.add(map);
					dataCursor.close();
				}
			}

			rawContactsCursor.close();
		}

		return list;
	}

}
