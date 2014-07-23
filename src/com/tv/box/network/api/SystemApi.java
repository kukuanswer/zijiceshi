package com.tv.box.network.api;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.model.CAInfo;
import com.tv.box.model.Channel;
import com.tv.box.model.PageInfo;
import com.tv.box.model.ParentLock;
import com.tv.box.model.SignalInfo;
import com.tv.box.model.SystemInfo;
import com.tv.box.network.BaseTask;
import com.tv.box.network.ResultObject;
import com.tv.box.network.SyncTaskExecutor;

/**
 * 1. 系统基本信息 2. 恢复出产设置
 * 
 * @author mac
 * 
 */
public class SystemApi extends BaseApi {

	static final String TAG = "SystemApi";

	/** 频道列表 */
	private List<Channel> mChannellist;

	protected SystemApi() {

	}

	/** 查询频道列表 */
	public void queryChannelList(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				Log.d(TAG, "zhouwei : 开始搜索节目列表");

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");
				PageInfo<Channel> info = null;

				try {
					JSONObject command = new JSONObject();
					command.put("command", "channel_list");
					command.put("commandId", 4);

					JSONObject filter = new JSONObject();
					command.put("filter", filter);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject page = new JSONObject(result.getContent());
						int count = page.getInt("count");

						info = new PageInfo<Channel>();

						if (count > 0) {

							info.setPageCount(count);
							JSONArray array = page.getJSONArray("channel");

							for (int index = 0, size = array.length(); index < size; index++) {

								JSONObject jo = array.getJSONObject(index);

								Channel pro = new Channel();
								pro.setLock(jo.getInt("lock"));
								pro.setAudioMode(jo.getInt("audioMode"));
								pro.setVideoPid(jo.getInt("videoPid"));
								pro.setDefinition(jo.getInt("definition"));
								pro.setSatId(jo.getInt("satId"));
								pro.setAudioPid(jo.getInt("audioPid"));
								pro.setAudioEcmPid(jo.getInt("audioEcmPid"));
								pro.setChannelId(jo.getInt("channelId"));
								pro.setTpId(jo.getInt("tpId"));
								pro.setName(jo.getString("name"));
								pro.setAudioVolume(jo.getInt("audioVolume"));
								pro.setGroupNum(jo.getInt("groupNum"));
								pro.setServiceId(jo.getInt("serviceId"));
								pro.setSubtitlePid(jo.getInt("subtitlePid"));
								pro.setTeletextPid(jo.getInt("teletextPid"));
								pro.setScrambled(jo.getInt("scrambled"));
								pro.setPmtPid(jo.getInt("pmtPid"));
								pro.setPcrPid(jo.getInt("pcrPid"));
								pro.setOriginalId(jo.getInt("originalId"));
								pro.setVideoType(jo.getInt("videoType"));
								pro.setAudioType(jo.getInt("audioType"));
								pro.setServiceType(jo.getInt("serviceType"));
								pro.setTsId(jo.getInt("tsId"));
								pro.setVideoEcmPid(jo.getInt("videoEcmPid"));
								pro.setSubtitleType(jo.getInt("subtitleType"));
								pro.setSkip(jo.getInt("skip"));
								pro.setBouquetId(jo.getInt("bouquetId"));
								pro.setCasId(jo.getInt("casId"));
								pro.setLcn(jo.getInt("lcn"));

								if (pro.getLock() == AppConfig.LOCK_STATE) {
									pro.setLocked(true);
								} else {
									pro.setLocked(false);
								}

								info.addObject(pro);
							}
						}

						// 频道列表
						mChannellist = info.getList();
						
						if(mChannellist == null || mChannellist.isEmpty()){
							sendMessage(MSG_QUERY_PROGRAM_EMPTY);
						} else {
							sendMessage(MSG_QUERY_PROGRAM_LIST_SUCCESS, info);
						}
					} else {
						sendMessage(MSG_QUERY_PROGRAM_LIST_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_QUERY_PROGRAM_LIST_FAILED);
				}
			}
		});
	}

	public final List<Channel> getChannelsList() {
		return mChannellist;
	}

	public void queryChannelTrack(final Channel program, AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("channel_get_track");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "channel_get_track");
					command.put("commandId", program.getChannelId());

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {
						System.out.println();
					}

				} catch (Exception e) {

				}
			}
		});
	}

	public void querySystemInfo(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_version_get");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());
						SystemInfo info = new SystemInfo();
						info.setApplication(json.getString("Application"));
						info.setLoader(json.getString("Loader"));
						info.setReleaseDate(json.getInt("REALEASE_DATE"));
						info.setHWVersion(json.getString("HW_Version"));
						info.setDefault_db(json.getString("Default DB"));
						info.setLib(json.getString("Lib"));

						sendMessage(MSG_QUERY_SYSTEM_INFO_SUCCESS, info);
					} else {
						sendMessage(MSG_QUERY_SYSTEM_INFO_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_QUERY_SYSTEM_INFO_FAILED);
				}
			}

		});
	}

	/** 恢复出厂设置 */
	public void resetFactory(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {
					
					// sleep
					Thread.sleep(500);

					JSONObject command = new JSONObject();
					command.put("command", "bs_factory_default");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,command.toString(), result);

					if (noerror) {
						
						JSONObject json = new JSONObject(result.getContent());
						int code = json.getInt("result");
						
						sendMessage(MSG_RESET_FACTORY_SUCCESS) ;
						
						if(code == AppConfig.Result_Code_Success){
							// sendMessage(MSG_RESET_FACTORY_SUCCESS) ;
						} else {
							//sendMessage(MSG_RESET_FACTORY_FAILED) ;
						}
					} else {
						//sendMessage(MSG_RESET_FACTORY_FAILED) ;
						sendMessage(MSG_RESET_FACTORY_SUCCESS) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_RESET_FACTORY_FAILED);
				}
			}

		});
	}

	/** 查询父母锁 */
	public void queryParentLockInfo(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_get_lock_control");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());
						ParentLock lock = new ParentLock();
						lock.setChannel_lock(json.getInt("channel_lock"));
						lock.setMenu_lock(json.getInt("menu_lock"));
						lock.setPasswd_channel(json.getString("passwd_channel"));
						lock.setUniveral_passwd(json
								.getString("univeral_passwd"));

						sendMessage(MSG_QUERY_PARENT_LOCK_SUCCESS, lock);
					} else {
						sendMessage(MSG_QUERY_PARENT_LOCK_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_QUERY_PARENT_LOCK_FAILED);
				}
			}

		});
	}

	/** 锁定频道 */
	public void lockChannel(final Channel channel, final int state,
			AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "channel_lock");
					command.put("state", state);

					JSONObject filter = new JSONObject();
					command.put("filter", filter);

					JSONArray array = new JSONArray();
					array.put(channel.getChannelId());
					filter.put("channelId", array);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {
						sendMessage(MSG_LOCK_CHANNEL_SUCCESS);
					} else {
						sendMessage(MSG_LOCK_CHANNEL_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_LOCK_CHANNEL_FAILED);
				}
			}

		});
	}
	
	public void notifyChannelLock(AppHanlder handler){
		
		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_notify_frush_channel");

					boolean noerror = mHttpExecutor.doPost(url,command.toString(), result);

					if (noerror) {
						//sendMessage(MSG_LOCK_CHANNEL_SUCCESS);
					} else {
						//sendMessage(MSG_LOCK_CHANNEL_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					//sendMessage(MSG_LOCK_CHANNEL_FAILED);
				}
			}

		});
	}

	/** CA 信息 */
	public void queryCA(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_get_ca_infor");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());
						CAInfo bean = new CAInfo();
						bean.setAuthenticateDate(json
								.getString("Authenticate Date"));
						bean.setValidDate(json.getString("Valid Date"));

						sendMessage(MSG_QUERY_CA_SUCCESS, bean);
					} else {
						sendMessage(MSG_QUERY_CA_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_QUERY_CA_FAILED);
				}
			}
		});
	}

	/** Signal 信息 */
	public void querySignal(AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "frontend_get_info");
					command.put("commandId", 2001);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());
						SignalInfo bean = new SignalInfo();
						bean.setBitErrorRate(json.getInt("bitErrorRate"));
						bean.setStrength(json.getInt("strength"));
						bean.setNoiseRatio(json.getInt("noiseRatio"));
						bean.setLockStatus(json.getString("lockStatus"));

						sendMessage(MSG_QUERY_SIGNAL_SUCCESS, bean);
					} else {
						sendMessage(MSG_QUERY_SIGNAL_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_QUERY_SIGNAL_FAILED);
				}
			}
		});
	}

	/** 验证主机密码 */
	public void verifyHostPassword(final String password, AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_get_lock_control");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());

						String pwd 	= json.getString("passwd");
						String spwd = json.getString("univeral_passwd");//passwd_channel
						
						if (pwd.equals(password) || spwd.equals(password)) {
							sendMessage(MSG_VERIFY_HOST_PASSWORD_SUCCESS);
						} else {
							sendMessage(MSG_VERIFY_HOST_PASSWORD_FAILED);
						}
					} else {
						sendMessage(MSG_SERVER_RESPONSE_ERROR);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_SERVER_RESPONSE_ERROR);
				}
			}
		});
	}
	
	/** 验证主机密码 */
	public void verifyChannelPassword(final String password, AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_get_lock_control");
					command.put("commandId", 111);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());

						String pwd 	= json.getString("passwd");
						String spwd = json.getString("univeral_passwd");
						String cpwd = json.getString("passwd_channel");
						
						if (pwd.equals(password) 
								|| spwd.equals(password) 
								|| cpwd.equals(password)) {
							sendMessage(MSG_VERIFY_HOST_PASSWORD_SUCCESS);
						} else {
							sendMessage(MSG_VERIFY_HOST_PASSWORD_FAILED);
						}
					} else {
						sendMessage(MSG_VERIFY_HOST_PASSWORD_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_VERIFY_HOST_PASSWORD_FAILED);
				}
			}
		});
	}
	
	/** 验证主机密码 */
	public void saveHostPassword(final String oldPwd, final String newPwd,final String channelPwd,
			AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");

				try {

					JSONObject command = new JSONObject();
					command.put("command", "bs_set_lock_control");
					command.put("commandId", 111);
					command.put("passwd_channel", channelPwd);
					command.put("channel_lock", 1);
					command.put("menu_lock", 1);
					command.put("passwd_old", oldPwd);
					command.put("passwd", newPwd);

					boolean noerror = mHttpExecutor.doPost(url,
							command.toString(), result);

					if (noerror) {

						JSONObject json = new JSONObject(result.getContent());

						int resultCode = json.getInt("result");

						if (resultCode == AppConfig.Result_Code_Success) {
							sendMessage(MSG_MODIFY_HOST_PASSWORD_SUCCESS);
						} else {
							sendMessage(MSG_MODIFY_HOST_PASSWORD_FAILED);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_MODIFY_HOST_PASSWORD_FAILED);
				}
			}
		});
	}

	public void searchProgram(final int frequency, final int symbolRate,
			final int polar, final AppHanlder handler) {

		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {

				ResultObject result = mResultObject.clone();
				String url = buildPost("command");
				int code = -1;

				try {
					
					JSONObject command = new JSONObject();
					command.put("command", "bs_one_key_search");
					command.put("commandId", 3005);
					command.put("freq", frequency);
					command.put("symb", symbolRate);
					command.put("polar", polar);
					
					boolean norerror = mHttpExecutor.doPost(url,command.toString(), result);
					
					if (norerror) {

						JSONObject json = new JSONObject(result.getContent());
						code = json.getInt("result");

						if (code == 0) {
							sendMessage(MSG_SEARCH_CHANNEL_SUCCESS);
						} else {
							sendMessage(MSG_SEARCH_CHANNEL_FAILED);
						}
					} else {
						sendMessage(MSG_SEARCH_CHANNEL_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(MSG_SEARCH_CHANNEL_FAILED);
				}
			}
		});
	}
}
