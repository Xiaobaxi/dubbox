package vision.apollo.common.indexcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jef.tools.StringUtils;
import jef.tools.string.RandomData;

public class IndexCodeDateTimeRandomImpl implements IndexCodeProvider{
	private DateFormat df = new SimpleDateFormat("yyMMddhhmmss");

	public String generate(String orgnizationCode, DeviceType type, ProfessionType profession, NetworkType network, String deviceId) {
		StringBuilder sb=new StringBuilder(20);
		synchronized (df) {
			sb.append(df.format(new Date()));//12位
		}
		if(StringUtils.isEmpty(deviceId)){
			sb.append(RandomData.randomInteger(1, 100000000));
		}else{
			deviceId=StringUtils.leftPad(deviceId, 8);
			sb.append(deviceId);
		}
		return sb.toString();
	}

	public int codeLength() {
		return 20;
	}
	

}
