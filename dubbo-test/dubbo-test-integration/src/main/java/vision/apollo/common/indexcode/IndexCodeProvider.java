package vision.apollo.common.indexcode;

public interface IndexCodeProvider {
	/**
	 * 生成符合对应规范的设备编码
	 * @param orgnizationCode 组织编码(8位)由行政区编码（6位)+组织编码(2位）组成。
	 * @param type     设备类型{@link DeviceType}}
	 * @return 生成后的设备编码
	 */
	String generate(String orgnizationCode,DeviceType type,ProfessionType profession,NetworkType network,String deviceId);

	/**
	 * 返回当前编码生成器生成的编码长度
	 * @return
	 */
	int codeLength();
}
