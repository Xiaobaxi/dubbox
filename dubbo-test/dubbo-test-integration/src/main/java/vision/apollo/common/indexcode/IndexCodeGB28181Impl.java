package vision.apollo.common.indexcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jef.tools.StringUtils;
import jef.tools.string.RandomData;

public class IndexCodeGB28181Impl implements IndexCodeProvider {
	
	private DateFormat df = new SimpleDateFormat("yyMMddhh");
	/**
	 * 增加28181 20位编码的产生
	 * 
	 * @author caoyunfei 2012-6-8 上午09:11:49
	 * @param orgnizationCode 组织编码 8位，由6位国标行政区码加上2位单位编码组成 (1-8)
	 * @param type             设备种类   (提供11,12,13位编码)
	 * @param profession       接入行业 (提供9-10位)
	 * @param network          网络类型 (提供14位编码)
	 * @return
	 */
	public String generate(String orgnizationCode, DeviceType type,ProfessionType profession,NetworkType network,String deviceId) {
		if(StringUtils.isEmpty(orgnizationCode)){
			synchronized (df) {
				orgnizationCode=df.format(new Date());
			}
		}
		if(orgnizationCode.length()!=8){
			throw new IllegalArgumentException("GB28181的行政区和组织编码必须为8位！");
		}
		StringBuilder sb=new StringBuilder(20);
		sb.append(orgnizationCode);
		
		//
		if(profession==null){
			profession=ProfessionType.POLICE_SURFACE; //默认公安路面
		}
		sb.append(profession.getCode());
		
		//
		if(type==null){
			type=DeviceType.CAMERA;
		}
		sb.append(type.toString());  
		
		//
		if(network==null){
			network=NetworkType.PUBLIC_SECURITY;
		}
		sb.append(network.ordinal());  //14位
		
		//设备序号
		if(StringUtils.isEmpty(deviceId)){
			deviceId=String.valueOf(RandomData.randomInteger(1, 1000000));
		}else if(deviceId.length()>6){
			deviceId=deviceId.substring(deviceId.length()-6);
		}else if(deviceId.length()<6){
			deviceId=StringUtils.leftPad(deviceId, 6,'0');
		}
		sb.append(deviceId);  //20位
		return sb.toString();
	}
	public int codeLength() {
		return 20;
	}

}
