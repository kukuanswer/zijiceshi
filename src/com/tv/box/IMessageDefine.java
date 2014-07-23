package com.tv.box;

public interface IMessageDefine {
	
	static final int MSG_TASK_START								= 0x00f0001 ;
	static final int MSG_TASK_END								= MSG_TASK_START + 1 ;
	
	// 注册事件回调
	static final int MSG_REGIST_EVENT							= MSG_TASK_END + 1 ;
	static final int MSG_REGIST_TIP								= MSG_REGIST_EVENT + 1 ;
	
	// 查询节目
	static final int MSG_QUERY_PROGRAM_LIST_SUCCESS				= MSG_REGIST_TIP + 1 ;
	static final int MSG_QUERY_PROGRAM_LIST_FAILED				= MSG_QUERY_PROGRAM_LIST_SUCCESS + 1 ;
	static final int MSG_QUERY_PROGRAM_EMPTY					= MSG_QUERY_PROGRAM_LIST_FAILED + 1 ;
	
	// 验证主机密码
	static final int MSG_QUERY_WIFI_PASSWORD_SUCCESS			= MSG_QUERY_PROGRAM_EMPTY + 1 ;
	static final int MSG_QUERY_WIFI_PASSWORD_FAILED				= MSG_QUERY_WIFI_PASSWORD_SUCCESS + 1 ;
	
	// 验证主机密码
	static final int MSG_VERIFY_HOST_PASSWORD_SUCCESS			= MSG_QUERY_WIFI_PASSWORD_FAILED + 1 ;
	static final int MSG_VERIFY_HOST_PASSWORD_FAILED			= MSG_VERIFY_HOST_PASSWORD_SUCCESS + 1 ;
	static final int MSG_SERVER_RESPONSE_ERROR					= MSG_VERIFY_HOST_PASSWORD_FAILED + 1 ;
	
	// 修改主机秘密
	static final int MSG_MODIFY_HOST_PASSWORD_SUCCESS			= MSG_SERVER_RESPONSE_ERROR + 1 ;
	static final int MSG_MODIFY_HOST_PASSWORD_FAILED			= MSG_MODIFY_HOST_PASSWORD_SUCCESS + 1 ;
	
	// 系统信息
	static final int MSG_QUERY_SYSTEM_INFO_SUCCESS 				= MSG_MODIFY_HOST_PASSWORD_FAILED + 1;
	static final int MSG_QUERY_SYSTEM_INFO_FAILED 				= MSG_QUERY_SYSTEM_INFO_SUCCESS + 1;
	
	// 父母锁信息
	static final int MSG_QUERY_PARENT_LOCK_SUCCESS 				= MSG_QUERY_SYSTEM_INFO_FAILED + 1;
	static final int MSG_QUERY_PARENT_LOCK_FAILED 				= MSG_QUERY_PARENT_LOCK_SUCCESS + 1;
	
	// 父母锁信息
	static final int MSG_LOCK_CHANNEL_SUCCESS 					= MSG_QUERY_PARENT_LOCK_FAILED + 1;
	static final int MSG_LOCK_CHANNEL_FAILED 					= MSG_LOCK_CHANNEL_SUCCESS + 1;
	
	// CA信息
	static final int MSG_QUERY_CA_SUCCESS 						= MSG_LOCK_CHANNEL_FAILED + 1;
	static final int MSG_QUERY_CA_FAILED 						= MSG_QUERY_CA_SUCCESS + 1;
	
	// CA信息
	static final int MSG_QUERY_SIGNAL_SUCCESS 					= MSG_QUERY_CA_FAILED + 1;
	static final int MSG_QUERY_SIGNAL_FAILED 					= MSG_QUERY_SIGNAL_SUCCESS + 1;
	
	// 出厂设置
	static final int MSG_RESET_FACTORY_SUCCESS 					= MSG_QUERY_SIGNAL_FAILED + 1;
	static final int MSG_RESET_FACTORY_FAILED 					= MSG_RESET_FACTORY_SUCCESS + 1;
	
	// 添加卫星
	static final int MSG_SEARCH_CHANNEL_SUCCESS 				= MSG_RESET_FACTORY_FAILED + 1;
	static final int MSG_SEARCH_CHANNEL_FAILED 					= MSG_SEARCH_CHANNEL_SUCCESS + 1;
	
	// wifi
	static final int MSG_MODIFY_WIFI_PWD_SUCCESS				= MSG_SEARCH_CHANNEL_FAILED + 1;
	static final int MSG_MODIFY_WIFI_PWD_FAILED 				= MSG_MODIFY_WIFI_PWD_SUCCESS + 1;
}
