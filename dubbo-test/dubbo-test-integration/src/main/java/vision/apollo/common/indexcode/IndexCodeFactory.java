package vision.apollo.common.indexcode;

import java.util.HashMap;
import java.util.Map;

public class IndexCodeFactory {
	public static final Map<String, DeviceType> deviceTypeMap = new HashMap<String, DeviceType>();
	private IndexCodeFactory() {
	};

	static {
		deviceTypeMap.put(DeviceType.DVR.name(), DeviceType.DVR);
		deviceTypeMap.put(DeviceType.VIDEO_SERVER.name(), DeviceType.VIDEO_SERVER);
		deviceTypeMap.put(DeviceType.ENCODER.name(), DeviceType.ENCODER);
		deviceTypeMap.put(DeviceType.DECODER.name(), DeviceType.DECODER);
		deviceTypeMap.put(DeviceType.VIDEO_MATRIX_SWITCHING.name(),
				DeviceType.VIDEO_MATRIX_SWITCHING);
		deviceTypeMap.put(DeviceType.AUDIO_MATRIX_SWITCHING.name(),
				DeviceType.AUDIO_MATRIX_SWITCHING);
		deviceTypeMap.put(DeviceType.ALARM.name(), DeviceType.ALARM);
		deviceTypeMap.put(DeviceType.NVR.name(), DeviceType.NVR);
		deviceTypeMap.put(DeviceType.CAMERA.name(), DeviceType.CAMERA);
		deviceTypeMap.put(DeviceType.CAMERA_IPC.name(), DeviceType.CAMERA_IPC);
		deviceTypeMap.put(DeviceType.SIGNAL_CONTROL_SERVER.name(),
				DeviceType.SIGNAL_CONTROL_SERVER);
		deviceTypeMap.put(DeviceType.WEB_SERVER.name(), DeviceType.WEB_SERVER);
		deviceTypeMap.put(DeviceType.MEDIA_DISTRIBUTION_SERVER.name(),
				DeviceType.MEDIA_DISTRIBUTION_SERVER);
		deviceTypeMap.put(DeviceType.PROXY_SERVER.name(), DeviceType.PROXY_SERVER);
		deviceTypeMap.put(DeviceType.SECURITY_SERVER.name(), DeviceType.SECURITY_SERVER);
		deviceTypeMap.put(DeviceType.ALERM_SERVER.name(), DeviceType.ALERM_SERVER);
		deviceTypeMap.put(DeviceType.DATABASE_SERVER.name(), DeviceType.DATABASE_SERVER);

		deviceTypeMap.put(DeviceType.GIS_SERVER.name(), DeviceType.GIS_SERVER);
		deviceTypeMap.put(DeviceType.MANAGEMENT_SERVER.name(), DeviceType.MANAGEMENT_SERVER);
		deviceTypeMap.put(DeviceType.OTHER_SERVER.name(), DeviceType.OTHER_SERVER);
		deviceTypeMap.put(DeviceType.ACCESS_GATEWAY.name(), DeviceType.ACCESS_GATEWAY);
		deviceTypeMap.put(DeviceType.STORAGE_SERVER.name(), DeviceType.STORAGE_SERVER);
		deviceTypeMap.put(DeviceType.SIGNAL_ROUTE_GAGEWAY.name(),
				DeviceType.SIGNAL_ROUTE_GAGEWAY);
		deviceTypeMap.put(DeviceType.PLATFORM_USER.name(), DeviceType.PLATFORM_USER);
		deviceTypeMap.put(DeviceType.PLATFORM_TRADE_USER.name(), DeviceType.PLATFORM_TRADE_USER);
		deviceTypeMap.put(DeviceType.TERMINAL_USER.name(), DeviceType.TERMINAL_USER);
		deviceTypeMap.put(DeviceType.TERMINAL_TRADE_USER.name(), DeviceType.TERMINAL_TRADE_USER);
		deviceTypeMap.put(DeviceType.TERMINAL_USER_OTHER.name(), DeviceType.TERMINAL_USER_OTHER);
		deviceTypeMap.put(DeviceType.OTHERS.name(), DeviceType.OTHERS);
		deviceTypeMap.put(DeviceType.FIBER_TRANSCEIVER.name(), DeviceType.FIBER_TRANSCEIVER);
		deviceTypeMap.put(DeviceType.DIAITAL_CAMERA.name(), DeviceType.DIAITAL_CAMERA);
		deviceTypeMap.put(DeviceType.FIBER_TRANSMITTER.name(), DeviceType.FIBER_TRANSMITTER);
		deviceTypeMap.put(DeviceType.DISK_ARRAY.name(), DeviceType.DISK_ARRAY);
		deviceTypeMap.put(DeviceType.REMOTE_DEVICE_BOX.name(), DeviceType.REMOTE_DEVICE_BOX);
		deviceTypeMap.put(DeviceType.UPS_FRONTEND.name(), DeviceType.UPS_FRONTEND);
		deviceTypeMap.put(DeviceType.UPS_CENTER.name(), DeviceType.UPS_CENTER);
		deviceTypeMap.put(DeviceType.ALERM_ACCESSOR.name(), DeviceType.ALERM_ACCESSOR);
		deviceTypeMap.put(DeviceType.CHAR_PROCESSOR.name(), DeviceType.CHAR_PROCESSOR);
		deviceTypeMap.put(DeviceType.WIRELESS_TRANSPORT.name(), DeviceType.WIRELESS_TRANSPORT);
		deviceTypeMap.put(DeviceType.PICTURE_SPLLIER.name(), DeviceType.PICTURE_SPLLIER);
	}
	public static IndexCodeProvider getIndexCodeProvider(String className){
		ClassLoader cl=Thread.currentThread().getContextClassLoader();
		if(cl==null)cl=IndexCodeFactory.class.getClassLoader();
		try{
			Class<?> clz=cl.loadClass(className);
			return (IndexCodeProvider) clz.newInstance();
		}catch(ClassNotFoundException e){
			return null;
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static DeviceType getDeviceTypeByCode(String code) {
		return deviceTypeMap.get(code);
	}

}
