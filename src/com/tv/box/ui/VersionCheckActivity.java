package com.tv.box.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.AppPreferences;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.model.SystemInfo;
import com.tv.box.ui.view.AppProgressDialog;
import com.tv.box.ui.view.MyDialogEdit;
import com.tv.box.ui.view.OnMyEditClickListener;
import com.tv.box.version.STBVersionManager;
import com.tv.box.version.VersionInfo;

public final class VersionCheckActivity extends BaseActivity {
	
	final static int  DATA_END_MARK					= 0x89ABCDEF ;
	final static int  HEADER_MARK 					= 0xFEFEFEFE ;
	
	final static int CMD_RET_SERVER_CONNECT_BUSY	= 0x20 ; //32
	final static int CMD_RET_SERVER_CONNECT_SAFE	= CMD_RET_SERVER_CONNECT_BUSY + 1 ; //33
	final static int CMD_RET_TRANSFER_OK			= CMD_RET_SERVER_CONNECT_SAFE + 1 ; //34
	final static int CMD_RET_TRANSFER_CRC32_ERR		= CMD_RET_TRANSFER_OK + 1 ; //35
	final static int CMD_RET_UPGRADE_CRC_CHECK_FAIL	= CMD_RET_TRANSFER_CRC32_ERR + 1 ;
	final static int CMD_RET_UPGRADE_PROCESS		= CMD_RET_UPGRADE_CRC_CHECK_FAIL + 1 ;
	final static int CMD_RET_UPDATE_FAIL			= CMD_RET_UPGRADE_PROCESS + 1 ;
	final static int CMD_RET_UPDATE_SUCCESS			= CMD_RET_UPDATE_FAIL + 1 ;
	
	final static int STB_SECTION_ALL 				= 0x10 ;
	final static int STB_SECTION_USER				= STB_SECTION_ALL + 1 ;	
	final static int STB_SECTION_DB					= STB_SECTION_USER + 1 ;	
	
	static final int MSG_UPDATE_ERROR			= 1000 ;
	static final int MSG_UPDATE_STATUS			= MSG_UPDATE_ERROR + 1 ;
	static final int MSG_UPDATE_SUCCESS			= MSG_UPDATE_STATUS + 1 ;
	static final int MSG_UPDATE_FAILED			= MSG_UPDATE_SUCCESS + 1 ;
	static final int MSG_UPDATE_END				= MSG_UPDATE_FAILED + 1 ;
	
	final int CMD_SIZE		= 20 ;
	final int BUFFER_SIZE 	= 1024 ;

	AppProgressDialog updateDialog ;
	String filePath ;
	TextView bntNotUpdate ;
	ProgressDialog mProgressDialog ;
	
	int flagNot ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version_layout);

		setTopBarTitle(getString(R.string.title_version));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);
		
		bntNotUpdate	= (TextView)findViewById(R.id.bnt_version_not) ;
		bntNotUpdate.setOnClickListener(this);
		
		final AppPreferences pf = AppPreferences.getPreferences() ;
		flagNot			= pf.getInt(AppPreferences.KEY_NOT_UPDATE,AppConfig.SWITCH_OFF);
		
		changeNotUpdate(false) ;
		
		filePath = STBVersionManager.getStbBinPath(this);
		
		mApi.getSystemApi().querySystemInfo(mHandler);
	}
	
	private void changeNotUpdate(boolean change){
		
		if(change) flagNot	= (++flagNot) % 2 ;
		
		if(flagNot == AppConfig.SWITCH_ON){
			bntNotUpdate.setBackgroundResource(R.drawable.ic_lock_sel) ;
		} else {
			bntNotUpdate.setBackgroundResource(R.drawable.ic_lock_nor) ;
		}
		
		AppPreferences.getPreferences().putInt(AppPreferences.KEY_NOT_UPDATE, flagNot);
	}

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				
				AlertDialog.Builder builder = null ;
				
				switch(msg.what){
				case MSG_VERIFY_HOST_PASSWORD_SUCCESS :
					initUpdateStb();
					break ;
				case MSG_VERIFY_HOST_PASSWORD_FAILED :
					showText(getString(R.string.error_host_pwd)) ;
					break ;
				case MSG_UPDATE_STATUS :
					
					// showText(msg.obj.toString());
					
					/*if(updateDialog != null){
						updateDialog.dismiss() ;
						updateDialog = null ;
					}*/
					break ;
				case MSG_UPDATE_SUCCESS :
					
					showText(msg.obj.toString());
					
					if(updateDialog != null){
						updateDialog.dismiss() ;
						updateDialog = null ;
					}
					
					break ;
				case MSG_UPDATE_FAILED :
					
					showText(msg.obj.toString());
					
					if(updateDialog != null){
						updateDialog.dismiss() ;
						updateDialog = null ;
					}
					break ;
				case MSG_UPDATE_ERROR :
					
					String error = msg.obj != null ? msg.obj.toString() : getString(R.string.label_update_stb_error) ;
					
					showText(error);
					
					if(updateDialog != null){
						updateDialog.dismiss() ;
						updateDialog = null ;
					}
					break ;
				case MSG_UPDATE_END :
					if(updateDialog != null){
						updateDialog.dismiss() ;
						updateDialog = null ;
					}
					break ;
				case STBVersionManager.MSG_NOT_NEW_VERSION :
					showText(getString(R.string.label_no_new_version));
					break ;
				case STBVersionManager.MSG_HAS_NEW_VERSION:
					
					File file = new File(filePath);
					if(file == null || !file.exists()){
						return ;
					}
					
					builder = new Builder(VersionCheckActivity.this);
					builder.setTitle(R.string.label_hint);
					builder.setMessage(getString(R.string.label_has_new_version));
					builder.setPositiveButton(R.string.bnt_label_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							STBVersionManager.downloadVersion(VersionCheckActivity.this, mHandler);
						}
					});
					builder.create().show();
					break ;
				case STBVersionManager.MSG_DOWNLOAD_PROGRESS:
					
					if(mProgressDialog == null){
						mProgressDialog = new ProgressDialog(VersionCheckActivity.this);
						mProgressDialog.setCancelable(false);
						mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mProgressDialog.setMessage(getString(R.string.label_download_doing));
					}
					
					if (!mProgressDialog.isShowing())
						mProgressDialog.show();

					mProgressDialog.setMax(msg.arg1 / 1024);
					mProgressDialog.setProgress(msg.arg2 / 1024);
					
					break ;
				case STBVersionManager.MSG_DOWNLOAD_FINISH:
				case STBVersionManager.MSG_DOWNLOAD_ERROR:
					
					if(mProgressDialog != null){
						mProgressDialog.dismiss() ;
						mProgressDialog	= null ;
					}
					
					break ;
				case MSG_QUERY_SYSTEM_INFO_SUCCESS :
					
					SystemInfo info  = (SystemInfo) msg.obj;
					VersionInfo local = STBVersionManager.parserVersion(STBVersionManager.getConfigPath(VersionCheckActivity.this));
					
					if(local == null){
						break ;
					}
					
					String serverVersion 	= info.getApplication();
					String localVersion 	= local.getStbSoftwareVer();
					
					String[] ss = STBVersionManager.parserVersionString(serverVersion);
					String[] ll = STBVersionManager.parserVersionString(localVersion);
					
					if(ss.length == 3 && ll.length == 3){
						
						int vl1 = Integer.valueOf(ss[0]) ;
						int vl2 = Integer.valueOf(ss[1]) ;
						int vl3 = Integer.valueOf(ss[2]) ;
						
						int vs1 = Integer.valueOf(ll[0]) ;
						int vs2 = Integer.valueOf(ll[1]) ;
						int vs3 = Integer.valueOf(ll[2]) ;
						
						if((vl1 < vs1) 
								|| (vl1 == vs1 && vl2 < vs2)
								|| (vl1 == vs1 && vl2 == vs2 && vl3 < vs3)){
							showUpdateDialog();
						} else {
							showText(getString(R.string.label_no_new_version)) ;
						}
					}
					
					break ;
				}
			}
		};
	}
	
	private void showUpdateDialog(){
		
		AlertDialog.Builder builder = new Builder(VersionCheckActivity.this);
		builder.setTitle(R.string.title_version);
		builder.setMessage(R.string.label_power_off);
		builder.setPositiveButton(R.string.bnt_label_yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showCheckPassword();
			}
		});
		
		builder.create().show();
	}
	
	private void showCheckPassword(){
		
		MyDialogEdit dialog = new MyDialogEdit(this, getString(R.string.hint_input_wifi_password), "", 4,
				
				new OnMyEditClickListener() {
					@Override
					public void onMyEditClick(String content) {
						mApi.getSystemApi().verifyHostPassword(content, mHandler);
					}
		});
		
		dialog.setMaxLength(4);
		dialog.show() ;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		case R.id.bnt_version_download :
			
			STBVersionManager.downloadVersion(this, mHandler);
			
			break ;
		case R.id.bnt_version_update :
			showCheckPassword();
			break ;
		case R.id.bnt_version_not :
			changeNotUpdate(true) ;
			break ;
		}
	}
		
	public synchronized void initUpdateStb(){
		
		// 对话框
		updateDialog = AppProgressDialog.showDialog(this, getString(R.string.label_update_stb_doing),false);
		
		// 开始升级
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				byte[] buffer = new byte[BUFFER_SIZE];
				int read = -1 ;
				int file_length = 0;
				int checksum = 0;
				
				try{
					
					File file = new File(filePath);
					
					if(file == null || !file.exists()){
						mHandler.obtainMessage(MSG_UPDATE_ERROR,getString(R.string.error_cv_file)).sendToTarget();
						return ;
					}
					
					Socket socket = new Socket(AppConfig.IP,8000) ;
					OutputStream out = socket.getOutputStream() ;
					InputStream input = new FileInputStream(filePath);
					
					// 处理响应
					int command = readResponse(socket.getInputStream());
					
					// 传输数据
					if(command == CMD_RET_SERVER_CONNECT_SAFE){
						
						// 分析内容
						while((read = input.read(buffer, 0, BUFFER_SIZE)) > 0){
							checksum 	+= getChecksum(buffer, read);
							file_length += read;
						}
						
						System.out.println(String.format("zhouwei : check sum 0x%x\n", checksum));
						
						input.close() ;
						input	= null ;
						
						// 填充header
						int offset = 0 ;
						byte[] header = new byte[CMD_SIZE];
						
						offset += AppConfig.intToByteArray(header, offset, HEADER_MARK);
						offset += AppConfig.intToByteArray(header, offset, STB_SECTION_ALL);
						offset += AppConfig.intToByteArray(header, offset, checksum);
						offset += AppConfig.intToByteArray(header, offset, file_length);
						
						sendData(out,header,0,CMD_SIZE);
						
						// 发送文件
						input = new FileInputStream(filePath);
						while((read = input.read(buffer, 0, BUFFER_SIZE)) > 0){
							sendData(out,buffer,0,read);
						}
						// 结束发送
						input.close() ;
						input = null ;
						
						// 处理响应
						while((command = readResponse(socket.getInputStream())) > 0) {
							
							System.out.println(String.format("zhouwei : response command = %d", command));
							
							if(command == CMD_RET_TRANSFER_OK){
								mHandler.obtainMessage(MSG_UPDATE_STATUS,getString(R.string.label_cv_upload_stb_success)).sendToTarget();
							} else if(command == CMD_RET_UPDATE_SUCCESS){
								mHandler.obtainMessage(MSG_UPDATE_SUCCESS,getString(R.string.label_update_stb_succeed)).sendToTarget();
								break ;
							} else if(command == CMD_RET_UPDATE_FAIL){
								mHandler.obtainMessage(MSG_UPDATE_FAILED,getString(R.string.label_update_stb_error)).sendToTarget();
							} else if(command == CMD_RET_UPGRADE_PROCESS){
								mHandler.obtainMessage(MSG_UPDATE_STATUS,getString(R.string.label_update_stb_doing)).sendToTarget();
							} else {
								mHandler.obtainMessage(MSG_UPDATE_ERROR).sendToTarget();
							}
						}
						
						//
						mHandler.obtainMessage(MSG_UPDATE_END).sendToTarget();
						
					} else {
						mHandler.obtainMessage(MSG_UPDATE_ERROR).sendToTarget();
					}
					
					socket.close() ;
					socket	= null ;
					
				} catch(Exception e){
					mHandler.obtainMessage(MSG_UPDATE_ERROR).sendToTarget();
				}
			}
		}).start() ;
	}
	
	private int readResponse(InputStream input) throws Exception {
		
		int read = -1 ;
		byte[] buffer = new byte[CMD_SIZE];
		
		while((read = input.read(buffer, 0, CMD_SIZE)) > 0){
			
			int mark 	= AppConfig.byteArraytoInt(buffer, 0);
			int cmd		= AppConfig.byteArraytoInt(buffer, 4);
			
			System.out.println("zhouwei : read=" + read + "  mark=" + mark + "  cmd=" + cmd);
			return cmd;
		}
		
		return -1 ;
	}
	
	private void sendData(OutputStream out, byte[] buffer,int offset,int size) throws Exception{
		out.write(buffer, offset, size);
		out.flush() ;
	}
	
	private int getChecksum(byte[] buffer, int len){
		
		int index;
		int checksum = 0;
		for (index = 0; index < len; index++) {
			checksum += (buffer[index] & 0x0ff);
		}
		
		return checksum;
	}
}
