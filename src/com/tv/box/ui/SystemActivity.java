package com.tv.box.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.model.SystemInfo;

public final class SystemActivity extends BaseActivity {

	TextView machineVersion, boostVersion, loaderVersion, macId,
			hardwareVersion, clientVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_layout);

		setTopBarTitle(getString(R.string.title_system_info));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);

		machineVersion = (TextView) findViewById(R.id.machine_version);
		boostVersion = (TextView) findViewById(R.id.boost_version);
		loaderVersion = (TextView) findViewById(R.id.loader_version);
		macId = (TextView) findViewById(R.id.mac_id);
		hardwareVersion = (TextView) findViewById(R.id.hardware_version);
		clientVersion = (TextView) findViewById(R.id.ip_address);
		
		clientVersion.setText(getString(R.string.label_client_version) + versionName());

		mApi.getSystemApi().querySystemInfo(mHandler);
	}

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this) {

			@Override
			public void disposeMessage(Message msg) {
				switch (msg.what) {
				case MSG_QUERY_SYSTEM_INFO_SUCCESS:

					SystemInfo info = (SystemInfo) msg.obj;

					machineVersion.setText(getString(R.string.label_machine_soft_version) + info.getLoader());
					boostVersion.setText(getString(R.string.label_boost_version) + info.getApplication());
					loaderVersion.setText(getString(R.string.label_release_date) + info.getReleaseDate());
					macId.setText(getString(R.string.label_database_version) + info.getDefault_db());
					hardwareVersion.setText(getString(R.string.label_lib_version) + info.getLib());
					break;
				case MSG_QUERY_SYSTEM_INFO_FAILED :
					showText(getString(R.string.label_error_version_info));
					break ;
				}
			}

		};
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		}
	}
	
	public String versionName() {
		try {
			PackageManager packageManager = getPackageManager();
			return packageManager.getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "1.0.0";
		}
	}
}
