package vision.apollo.common.indexcode;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;


/**
 * 生成32位长度的GUID
 * @author jiyi
 *
 */
public class IndexCodeGUIDImpl implements IndexCodeProvider{
	public String generate(String orgnizationCode, DeviceType type, ProfessionType profession, NetworkType network, String deviceId) {
		UUID uuid = UUID.randomUUID();
		return StringUtils.remove(uuid.toString(), '-');
	}

	public int codeLength() {
		return 32;
	}
}
