package com.tv.box.ui;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLSurface;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;
import org.zw.android.framework.db.core.DateUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.box.AppConfig;
import com.tv.box.AppDebug;
import com.tv.box.AppHanlder;
import com.tv.box.AppPreferences;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.model.Channel;
import com.tv.box.network.SocketClient;
import com.tv.box.network.api.BaseApi;
import com.tv.box.ui.GestureObserver.GestureCallback;
import com.tv.box.ui.view.AppProgressDialog;
import com.tv.box.ui.view.MyDialogEdit;
import com.tv.box.ui.view.OnMyEditClickListener;
import com.tv.box.ui.view.ProgramListView;
import com.tv.box.ui.view.ProgramListView.OnChannelListChange;
import com.tv.box.upnp.DeviceItem;
import com.tv.box.upnp.DeviceListRegistryListener;
import com.tv.box.upnp.WireUpnpService;
import com.tv.box.version.STBVersionManager;

public final class HomeActivity extends BaseActivity implements OnClickListener {

	static final String TAG = "HomeActivity";
	static final String AUDIO_ACTION = "android.media.VOLUME_CHANGED_ACTION";
	static final int TIMER_LENGTH	= 90 * 60 * 1000 ;
	static final int MSG_REPLAY		= 99 ;

	RelativeLayout leftLayout, bottomLayout, advertiseLayout;
	FrameLayout surfaceLayout;
	FrameLayout surfaceContent;

	TextView titile;
	ProgramListView listView;

	SDLActivity mSoftPlayer;
	ProgressDialog mdialog;
	Channel		mChannel ;
	Channel		mTempChannel ;
	SeekBar audioBar;

	AudioManager mAudioManager;
	int voiceValue ;
	GestureObserver mObserver;
	DisplayMetrics dm = new DisplayMetrics();

	static final int AV_TIME_BASE = 1000000;
	int buffersize = 120;
	int maxAnalyzeDuration = 2000000;

	public final int MSG_LOAD_FINISHED = 10;
	public final int MSG_LOAD_UNFINISHED = 11;
	public final int MSG_OPEN_ERROR = 12;
	public final int MSG_OPEN_OK = 13;
	public final int MSG_SEEK_UPDATE = 30;

	public static int full_screen_width = 0;
	public static int full_screen_height = 0;
	private static int current_aspect_ratio_type = 0; 

	private AndroidUpnpService upnpService;
	private DeviceListRegistryListener registryListener;
	private TextView bntVoice ;
	
	private SocketClient	mClient ;
	private boolean			mCheck ;
	private AppProgressDialog mDialog ;
	
	private BroadcastReceiver voiceReceiver;
	private BroadcastReceiver upnpReceiver;
	private BroadcastReceiver phoneReceiver;
	private BroadcastReceiver channelReceiver ;
	private BroadcastReceiver screenReceiver ;
	
	private static boolean screenOff ;
	private ProgressDialog mProgressDialog ;
	
	private String[] 	streamIds;
	private String[] 	languages;
	private String 		selectedLanguage;
	private String 		selectedStreamId;
	private long 		mStartTime ;
	private Timer		mTimer ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int[] params = getScreenparameters();
		full_screen_width = params[0];
		full_screen_height = params[1];

		leftLayout = (RelativeLayout) findViewById(R.id.left_program_layout);
		bottomLayout = (RelativeLayout) findViewById(R.id.bottom_tool_layout);
		surfaceLayout = (FrameLayout) findViewById(R.id.surface_view_layout);
		surfaceContent = (FrameLayout) findViewById(R.id.surface_view_content);
		advertiseLayout = (RelativeLayout) findViewById(R.id.bottom_advertise_layout);
		listView = (ProgramListView) findViewById(R.id.list_view);
		audioBar = (SeekBar) findViewById(R.id.audio_bar);
		bntVoice	= (TextView)findViewById(R.id.bnt_voice_icon);

		surfaceLayout.setOnClickListener(this);
		
		// 时间检测
//		initCheck();
		
		// 声音设置
		initAudio();

		// 节目列表
		initListView();

		// init upnp
		initGesture();

		// upnp service
		initUpnpService();
		
		// init client socket
		initSocket();
		
		// init phone receiver
		initPhoneReceiver();
		
		// init search Channel
		initSearchChannel() ;
		
		// init screen on
		initScreenOnAndOff();
		
		// down stb software
		if(AppPreferences.getPreferences().getInt(AppPreferences.KEY_NOT_UPDATE,AppConfig.SWITCH_ON) == AppConfig.SWITCH_OFF){
			STBVersionManager.downSTBSoft(this, mHandler);
		}
		
		executePlay(null);
	}
	
	private void initScreenOnAndOff(){
		
		screenReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				
				if (action.equals(Intent.ACTION_SCREEN_OFF)) {
					screenOff	= true ;
					stop() ;
				} else if(action.equals(Intent.ACTION_SCREEN_ON)){
					screenOff	= false ;
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);
	}
	
	private void initCheck(){
		
		Date d1 = toDate(System.currentTimeMillis()) ;
		Date d2 = DateUtils.toDate(AppConfig.VERSION_DATE);
		
		long dx = DateUtils.compare(d1, d2) ;
		
		if(dx >= 0){
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.label_hint)
					.setMessage(R.string.label_soft_overdue)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setNeutralButton(R.string.bnt_label_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
						}
					}).create();
			
			dialog.setCancelable(false);
			dialog.show() ;
			mCheck	= true ;
		} else {
			mCheck	= false ;
		}
	}
	
	public static Date toDate(long milliseconds) {
		
		try{
			return new Date(milliseconds) ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		return null;
	}
	
	private void initSocket(){
		
		mClient	= SocketClient.create(this) ;
		mClient.init(mHandler) ;
	}

	/** 初始化列表 */
	private void initAudio() {

		audioBar.setMax(getMaxVolume());
		audioBar.setProgress(getVolume());
		audioBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					setVolume(progress);
				}
			}
		});

		voiceReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				if (action.equals(AUDIO_ACTION)) {
					audioBar.setProgress(getVolume());
					updateVoiceIcon() ;
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(AUDIO_ACTION);
		registerReceiver(voiceReceiver, filter);
	}

	/** 初始化列表 */
	private void initListView() {

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Channel program = listView.getItem(position);

				// 是否正在播放
				if (program.isSelected()) {
					return;
				}

				// 重置
				listView.reset();

				playChannel(program);
			}
		});

		listView.setChangeListener(new OnChannelListChange() {

			@Override
			public void onChange(List<Channel> list) {

				// 播放历史
				AppPreferences pf = AppPreferences.getPreferences();

				int channelId = pf.getInt(AppPreferences.KEY_CHANNLE_ID);
				
				if(channelId == -1 && !list.isEmpty()){
					channelId = list.get(0).getChannelId() ;
				}
				
				Channel selected = null;

				for (Channel channel : list) {

					if (channelId == channel.getChannelId()) {
						selected = channel;
						channel.setSelected(true);
					} else {
						channel.setSelected(false);
					}
				}
				
				if(selected == null && !list.isEmpty()){
					selected	= list.get(0);	
				}

				playChannel(selected);

				listView.showCurrentItem();
			}
		});
	}

	/** 初始化手势识别 */
	private void initGesture() {

		mObserver = new GestureObserver(this);
		mObserver.setCallback(new GestureCallback() {

			@Override
			public void callback(int event, int distance, MotionEvent e) {

				int value = getVolume();
				int max = getMaxVolume();
				float x = e.getX();
				float y = e.getY();

				Rect rootRect = new Rect();
				surfaceContent.getHitRect(rootRect);

				// 不在矩形中
				if (!rootRect.contains((int) x, (int) y)) {
					return;
				}

				switch (event) {
				case GestureObserver.EVENT_SINGLE_TAP:
					if (mSoftPlayer != null) {
						switch (current_aspect_ratio_type) {
						case 0:
							// 如果当前为小屏幕非满屏，则切换至全屏，且分辨率为4:3
							current_aspect_ratio_type = 2;
							break;
						case 1:
							// 如果当前为小屏幕满屏，则切换至全屏，且为满屏
							current_aspect_ratio_type = 3;
							break;
						case 2:
							// 如果当前为全屏非满屏，则切换至小屏幕，且为原始比例
							current_aspect_ratio_type = 0;
							break;
						case 3:
							// 如果当前为全屏满屏，则切换至小屏幕，且为满屏
							current_aspect_ratio_type = 1;
							break;
						}

						onFullScreen();
					}
					break;
				case GestureObserver.EVENT_DOUBLE_TAP:

					if (mSoftPlayer != null) {
						switch (current_aspect_ratio_type) {
						case 0:
							// 如果当前是小屏原始比例，切换至小屏满屏
							current_aspect_ratio_type = 1;
							break;
						case 1:
							// 如果当前是小屏满屏，切换至小屏原始比例
							current_aspect_ratio_type = 0;
							break;
						case 2:
							// 如果当前是全屏分辨率为4:3，则切换至全屏满屏
							current_aspect_ratio_type = 3;
							break;
						case 3:
							// 如果当前为全屏满屏，则切换至全屏分辨率4:3
							current_aspect_ratio_type = 2;
							break;
						}

						onFullScreen();
					}

					break;
				case GestureObserver.EVENT_DIRECTION_UP:
					playLast();
					break;
				case GestureObserver.EVENT_DIRECTION_DOWN:
					playNext();
					break;
				case GestureObserver.EVENT_DIRECTION_LEFT:
					if (value > 0) {
						VolumeDown();
					}
					break;
				case GestureObserver.EVENT_DIRECTION_RIGHT:
					if (value < max) {
						VolumeUp();
					}
					break;
				}
			}
		});
	}

	/** init upnp service */
	private void initUpnpService() {

		registryListener = new DeviceListRegistryListener(this);

		// 绑定服务
		getApplicationContext().bindService(
				new Intent(this, WireUpnpService.class), serviceConnection,
				Context.BIND_AUTO_CREATE);

		upnpReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(AppConfig.ACTION_SEARCH_BOX_IP) && !screenOff) {

					AppPreferences pf 	= AppPreferences.getPreferences();
					AppConfig.IP 		= pf.getString(AppPreferences.KEY_ANDROID_BOX_IP);

					if (listView.isEmpty()) {
						listView.onRelaod();
					}
				}
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConfig.ACTION_SEARCH_BOX_IP);
		registerReceiver(upnpReceiver, filter);
		
		// 搜索设备
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				searchNetwork();
			}
		}, 2000);
	}
	
	private void initPhoneReceiver(){
		
		phoneReceiver	= new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				
				int state = telephony.getCallState();
				
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					
					if(mChannel != null){
						playChannel(mChannel);
					}
					
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stop() ;
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				}
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.setPriority(Integer.MAX_VALUE);  
		registerReceiver(phoneReceiver, filter);
	}
	
	private void initSearchChannel(){
		
		channelReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				
				final String action = intent.getAction() ;
				
				if(action.equals(AppConfig.ACTION_BIND_CLIENT_SOCKET)){
					
					if(mClient != null){
						mClient.release() ;
						mClient	= null ;
					}
					
					initSocket() ;
				} else if(action.equals(AppConfig.ACTION_LOCK_CHANNEL)){
					
					listView.notifyUI() ;
					
					if(mChannel != null && mChannel.getLock() == AppConfig.LOCK_STATE){
						stop() ;
						mChannel = null ;
					}
				} else if(action.equals(AppConfig.ACTION_REFRESH_CHANNEL_LIST)){
					stop() ;
					listView.onRelaod() ;
				} else if(action.equals(AppConfig.ACTION_SEARCH_CHANNEL)
						|| action.equals(AppConfig.ACTION_STOP_ALL)){
					stop() ;
					mChannel = null ;
				}
			}
			
		} ;
		
		IntentFilter filter = new IntentFilter() ;
		filter.addAction(AppConfig.ACTION_BIND_CLIENT_SOCKET) ;
		filter.addAction(AppConfig.ACTION_LOCK_CHANNEL) ;
		filter.addAction(AppConfig.ACTION_REFRESH_CHANNEL_LIST) ;
		filter.addAction(AppConfig.ACTION_SEARCH_CHANNEL) ;
		filter.addAction(AppConfig.ACTION_STOP_ALL) ;
		registerReceiver(channelReceiver, filter) ;
	}

	protected void searchNetwork() {

		if (upnpService == null)
			return;

		upnpService.getRegistry().removeAllRemoteDevices();
		upnpService.getControlPoint().search();
	}

	private void playNext() {

		Log.d(TAG, "zhouwei : 播放下一个视频");

		Channel channel = listView.getNextChannel();

		if (channel != null) {

			listView.reset();

			playChannel(channel);
		}
	}

	private void playLast() {

		Log.d(TAG, "zhouwei : 播放上一个视频");

		Channel channel = listView.getLastChannel();

		if (channel != null) {

			listView.reset();

			playChannel(channel);
		}
	}

	private void playChannel(final Channel channel) {

		if (channel == null || mCheck) {
			return;
		}
		
		if(channel.getLock() == AppConfig.LOCK_STATE){
			
			mTempChannel	= channel ;
			
			MyDialogEdit dialog = new MyDialogEdit(this,
					getString(R.string.hint_input_channel_password), "", 4,
					
					new OnMyEditClickListener() {

						@Override
						public void onMyEditClick(String content) {
							mApi.getSystemApi().verifyChannelPassword(content, mHandler);
						}
					});
			
			dialog.setMaxLength(4);
			dialog.show() ;
		} else {
			executePlay(channel);
		}
	}
	
	private synchronized void executePlay(Channel channel){
		
//		if(mTimer != null){
//			mTimer.cancel() ;
//			mTimer	= null ;
//		}
//		
//		if(AppConfig.IP.trim().equals("")){
//			Toast.makeText(getApplicationContext(), getString(R.string.error_network),Toast.LENGTH_SHORT).show();
//			return ;
//		}
//		
//		AppPreferences pf = AppPreferences.getPreferences();
//
//		// 设置选中
//		channel.setSelected(true);

//		String url = BaseApi.buildChannelUrl(channel);
		String url = "/sdcard/problem_player.ts";

		if (url == null) {
			return;
		}

		AppDebug.d("HomeActivity", "zhouwei : play: " + url);
		
		PlayerTask task = new PlayerTask();
		task.execute(url);

//		pf.putInt(AppPreferences.KEY_CHANNLE_ID, channel.getChannelId());
//
//		// 更新
//		listView.notifyUI();
//		listView.showCurrentItem();
//		mChannel = channel ;
//		mTempChannel = null ;
//		
//		mStartTime	= System.currentTimeMillis() ;
//		mTimer	= new Timer() ;
//		mTimer.scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				
//				if(System.currentTimeMillis() - mStartTime >= TIMER_LENGTH){
//					releasePlayer() ;
//					mHandler.obtainMessage(MSG_REPLAY).sendToTarget();
//				}
//			}
//		}, 0, 3000 );
	}
	
	private void updateVoiceIcon(){
		
		if(getVolume() > 0){
			bntVoice.setBackgroundResource(R.drawable.ic_voice_nor);
		} else {
			bntVoice.setBackgroundResource(R.drawable.ic_voice_silence);
		}
	}

	public void VolumeUp() {
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
		
		updateVoiceIcon();
	}

	public void VolumeDown() {
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
		
		updateVoiceIcon() ;
	}

	public void setVolume(int value) {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
		
		updateVoiceIcon() ;
	}

	public int getMaxVolume() {
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public int getVolume() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	private void onFullScreen() {

		switch (current_aspect_ratio_type) {
		case 0:
		case 1:
			// 第1和第2种：小屏幕
			advertiseLayout.setVisibility(View.VISIBLE);
			leftLayout.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
			quitFullScreen();
			break;
		case 2:
		case 3:
			// 第3、4种：大屏幕
			setFullScreen();
			advertiseLayout.setVisibility(View.GONE);
			leftLayout.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {

		Intent intent = new Intent();
		MyDialogEdit dialog = null;

		switch (v.getId()) {
		case R.id.surface_view_layout:
			// onFullScreen();
			break;
		case R.id.setting_layout:
			intent.setClass(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.audio_setting:
			if (mSoftPlayer.isPlaying()) {
				mSoftPlayer.PlayerPause();
			} else {
				mSoftPlayer.PlayerPause();
			}
			
			if (mSoftPlayer != null && mSoftPlayer.isPlaying()) {
				
				String data = SDLActivity.getyingguiData();
				
				// 查看音轨
				String tmp = URLDecoder.decode(data);
				
				// tmp的结构为"start:language0@streamId0,language1@streamId1,language2@streamId2,..@..,"
				if(tmp == null || tmp.equals("none")){
					Toast.makeText(getApplicationContext(), "当前没有音轨信息",Toast.LENGTH_SHORT).show();
					break;
				}
				
				if(tmp.length() < 10){
					Toast.makeText(getApplicationContext(), "当前没有音轨信息",Toast.LENGTH_SHORT).show();
					return ;
				}
				
				String info = tmp.substring(10, tmp.length() - 1);
				String[] infoarray;
				String[] infoitem;
				
				if (info.contains(";")) {
					infoarray = info.split(";", -1);
					streamIds = new String[infoarray.length-1];
					languages = new String[infoarray.length-1];
					for (int i = 0,j=0; i < infoarray.length; i++) {
						// 数组第一个元素是当前播放的语言和streamID，后面的是所有的语言种类和streamID
						infoitem = infoarray[i].split("@", -1);
						if (i == 0) {
							selectedLanguage = infoitem[0];
							selectedStreamId = infoitem[1];
						} else {
							languages[j] = infoitem[0];
							streamIds[j] = infoitem[1];
							j++;
						}					
					}
					
					new AlertDialog.Builder(this).setTitle("选择音轨")
					    .setItems(languages, new DialogInterface.OnClickListener() {
					     public void onClick(DialogInterface dialog, int item) {
					    	 
					         if (streamIds[item].equals(selectedStreamId)) {
					        	 Toast.makeText(getApplicationContext(), "当前已经是" + languages[item],Toast.LENGTH_SHORT).show();
						     } else {
						    	 mSoftPlayer.PlayerChangeAudio(Integer.parseInt(streamIds[item]));
							 }
					     }
					}).show();// 显示对话框
				}
			}

			break;
		case R.id.bnt_input_address:

			dialog = new MyDialogEdit(this, "设置地址", AppConfig.IP, 1,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {

							if (content != null) {
								AppConfig.IP = content;
							}
						}
					});

			dialog.show();

			break;
		case R.id.bnt_input_analyze_duration:

			dialog = new MyDialogEdit(this, "设置分析时间", "", 4,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {
							try {
								maxAnalyzeDuration = Integer.valueOf(content);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			dialog.show();
			break;
		case R.id.bnt_input_max_buffer:

			dialog = new MyDialogEdit(this, "设置缓冲buffer", "", 4,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {

							try {
								buffersize = Integer.valueOf(content);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			dialog.show();

			break;
		case R.id.bnt_reload:
			stop() ;
			listView.onRelaod();
			break;
		case R.id.bnt_voice_icon_layout :
			if(getVolume() > 0){
				
				voiceValue = getVolume() ;
				
				setVolume(0);
			} else {
				setVolume(voiceValue > 0 ? voiceValue : (getMaxVolume() >> 1));
			}
			break ;
		}
	}

	private void setFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (mSoftPlayer != null && mSoftPlayer.isPlaying()) {
			SDLActivity.PlayerSetAspectRatio(current_aspect_ratio_type);
		}
	}

	private void quitFullScreen() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (mSoftPlayer != null && mSoftPlayer.isPlaying()) {
			SDLActivity.PlayerSetAspectRatio(current_aspect_ratio_type);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(mChannel != null){
			playChannel(mChannel);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		stop();
	}
	
	/** 释放资源 */
	private void stop() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				releasePlayer();
			}
		}).start() ;
	}
	
	private synchronized void releasePlayer(){
		
		if (mSoftPlayer != null) {
			mSoftPlayer.exit();
			mSoftPlayer.onDestroy();
			mSoftPlayer	= null ;
		}

		// 保证完全释放资源
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		stop();

		if (voiceReceiver != null) {
			unregisterReceiver(voiceReceiver);
			voiceReceiver	= null ;
		}

		if (upnpService != null) {
			upnpService.getRegistry().removeListener(registryListener);
			upnpService	= null ;
		}

		if (upnpReceiver != null) {
			unregisterReceiver(upnpReceiver);
			upnpReceiver	= null ;
		}
		
		if(phoneReceiver != null){
			unregisterReceiver(phoneReceiver);
			phoneReceiver	= null ;
		}
		
		if(channelReceiver != null){
			unregisterReceiver(channelReceiver);
			channelReceiver	= null ;
		}
		
		if(mClient != null){
			mClient.release() ;
		}
		
		if(screenReceiver != null){
			unregisterReceiver(screenReceiver);
			screenReceiver	= null ;
		}
		
		mChannel	= null ;
		
		getApplicationContext().unbindService(serviceConnection);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		mObserver.dispatchTouchEvent(ev);

		return super.dispatchTouchEvent(ev);
	}

	private class PlayerTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			
			releasePlayer();
			
			return url;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {

				result = result.replaceAll("\\s", "");

				mSoftPlayer = new SDLActivity(getApplication(), handler,
						result, buffersize, maxAnalyzeDuration,
						full_screen_width, full_screen_height);
				SDLSurface surface = mSoftPlayer.getSDLSurface();
				SDLActivity.PlayerSetAspectRatio(current_aspect_ratio_type);

				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				layoutParams.gravity = Gravity.CENTER;
				surface.setLayoutParams(layoutParams);

				surfaceLayout.removeAllViews();
				surfaceLayout.addView(surface);
			}
		}

		@Override
		protected void onPreExecute() {
			showConnectDialog();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}
	
	private synchronized void showConnectDialog(){
		
		if(mdialog != null){
			mdialog.dismiss() ;
			mdialog	= null ;
		}
		
		// 开启一个进度条
		mdialog = new ProgressDialog(HomeActivity.this);
		mdialog.setTitle(R.string.label_hint);
		mdialog.setMessage(getString(R.string.label_buffering));
		mdialog.setCancelable(true);
		mdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				
			}
		});
		mdialog.show();
	}

	final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (!Thread.currentThread().isInterrupted()) {

				System.out.println("receive msg : " + msg.what);

				if (mdialog != null) {
					mdialog.dismiss();
				}

				switch (msg.what) {
				case MSG_OPEN_OK:
					break;
				case MSG_OPEN_ERROR:
					// Toast.makeText(getApplicationContext(), getString(R.string.error_network),Toast.LENGTH_SHORT).show();
					break;
				case MSG_LOAD_FINISHED:
					break;
				case MSG_LOAD_UNFINISHED:
					break;
				case MSG_SEEK_UPDATE:
					break;
				}
			}
		}
	};

	/**
	 * 获取全屏幕的高度和宽度
	 * 
	 * @return
	 * */
	private int[] getScreenparameters() {
		int paras[] = new int[2];

		paras[0] = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		paras[1] = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）

		Log.d(TAG, "paras[0]=" + paras[0] + "paras[1] = " + paras[1]);

		return paras;
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {

			upnpService = (AndroidUpnpService) service;

			for (Device device : upnpService.getRegistry().getDevices()) {
				registryListener.deviceAdded(new DeviceItem(device));
			}

			// Getting ready for future device advertisements
			upnpService.getRegistry().addListener(registryListener);

			// Refresh device list
			upnpService.getControlPoint().search();
		}

		public void onServiceDisconnected(ComponentName className) {
			upnpService = null;
		}
	};

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_TASK_START:
					if(mDialog == null){
						mDialog	= AppProgressDialog.showDialog(mContext, getString(R.string.label_connecting));
					}
					break;
				case MSG_TASK_END:
					if(mDialog != null){
						mDialog.dismiss() ;
						mDialog	= null;
					}
					break;
				case MSG_REGIST_EVENT :
					showText(msg.obj.toString());
					break ;
				case MSG_REGIST_TIP :
					showText(msg.obj.toString());
					break ;
				case MSG_VERIFY_HOST_PASSWORD_SUCCESS :
					if(mTempChannel != null){
						executePlay(mTempChannel);
					}
					break ;
				case MSG_VERIFY_HOST_PASSWORD_FAILED :
					showText(getString(R.string.error_channel));
					break ;
				case STBVersionManager.MSG_NOT_NEW_VERSION :
					// showText(getString(R.string.label_no_new_version));
					break ;
				case STBVersionManager.MSG_HAS_NEW_VERSION:
					AlertDialog.Builder builder = new Builder(HomeActivity.this);
					builder.setTitle(R.string.label_hint);
					builder.setMessage(getString(R.string.label_has_new_version));
					builder.setPositiveButton(R.string.bnt_label_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							STBVersionManager.downloadVersion(HomeActivity.this, mHandler);
						}
					});
					builder.create().show();
					break ;
				case STBVersionManager.MSG_DOWNLOAD_PROGRESS:
					
					if(mProgressDialog == null){
						mProgressDialog = new ProgressDialog(HomeActivity.this);
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
					// 重新播放
				case MSG_REPLAY :
					executePlay(mChannel);
					break ;
				}
			}
			
		};
	}
}
