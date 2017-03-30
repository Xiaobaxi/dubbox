package vision.apollo.common.indexcode;

/**
 * 国标 GB/T-28181的各种设备类型
 * @author jiyi
 *
 */
public enum DeviceType {
	DVR(111,"DVR","01"),
	VIDEO_SERVER(112,"视频服务器","23"),
	ENCODER(113,"编码器","00"),
	DECODER(114,"解码器","02"),
	VIDEO_MATRIX_SWITCHING(115,"视频切换矩阵","40"), 
	AUDIO_MATRIX_SWITCHING(116,"音频切换矩阵","40"),
	ALARM(117,"报警控制器","42"),
	NVR(118,"网络视频录像机（NVR）","01"),
	
	CAMERA(131,"摄像机","60"),
	CAMERA_IPC(132,"网络摄像机(IPC)","00"),
	
	SIGNAL_CONTROL_SERVER(200,"中心信令控制服务器","20"),
	WEB_SERVER(201,"Web应用服务器","22"),
	MEDIA_DISTRIBUTION_SERVER (202,"媒体分发服务器","21"),
	PROXY_SERVER(203,"代理服务器","21"),
	SECURITY_SERVER(204,"安全服务器","20"),
	ALERM_SERVER(205,"报警服务器","42"),
	DATABASE_SERVER(206,"数据库服务器","20"),
	GIS_SERVER(207,"GIS 服务器","20"),
	MANAGEMENT_SERVER(208,"管理服务器","20"),
	OTHER_SERVER(212,"其他服务器","24"),
	
	ACCESS_GATEWAY(209,"接入网关","20"),
	STORAGE_SERVER(210,"媒体存储服务器","23"),
	SIGNAL_ROUTE_GAGEWAY(211,"信令安全路由网关","20"),
	PLATFORM_USER(300,"中心用户","93"),
	PLATFORM_TRADE_USER(201,"中心行业用户","94"),
	TERMINAL_USER(400,"终端用户","93"),
	TERMINAL_TRADE_USER(401,"终端行业用户","94"),
	TERMINAL_USER_OTHER(444,"扩展的终端用户类型","95"),
	OTHERS(500,"扩展类型","96"),
	
	//以下是DB33在GB28181的基础上扩展的设备类型
	FIBER_TRANSCEIVER(139,"光纤收发机","03"),
	DIAITAL_CAMERA(131,"数字摄像机","04"),  
	FIBER_TRANSMITTER(139,"光端机","05"),
	DISK_ARRAY(210,"磁盘阵列","06"),
	
	REMOTE_DEVICE_BOX(212,"远程设备箱(间)","43"),
	UPS_FRONTEND(119,"前端用UPS电源","44"),
	UPS_CENTER(212,"中心用UPS电源","45"),
	
	ALERM_ACCESSOR(205,"报警接入器","46"),
	CHAR_PROCESSOR(113,"字符叠加器","47"),
	WIRELESS_TRANSPORT(500,"无线传输设备","48"),
	PICTURE_SPLLIER(113,"图像分割器","62");
	
	private DeviceType(int code,String comment,String bCode){
		this.code=code;
		this.comment=comment;
		this.db33Code=bCode;
	}
	private String db33Code;
	private int code;
	private String comment;
	/**
	 * 得到设备的编码（3位数字）
	 * @return
	 */
	public int getCode() {
		return code;
	}
	/**
	 * 得到设备的中文描述
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	@Override
	public String toString() {
		return String.valueOf(code);
	}
	/**
	 * 得到GB28181中的B类型编码（2位数字）
	 * @return
	 */
	public String getDB33Code() {
		return db33Code;
	}
}
