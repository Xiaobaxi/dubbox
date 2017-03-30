package vision.apollo.common.indexcode;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class IndexCodeProviderTest {
	
	private ProfessionType profession;
	
	private NetworkType network;
	
	private DeviceType type;
	
	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public void setUpBeforeClass() throws Exception {
		profession = ProfessionType.POLICE_SURFACE;
		network = NetworkType.SPECIAL_1;
		type = IndexCodeFactory.getDeviceTypeByCode(DeviceType.CAMERA.name());
	}
	
	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void codeLength() {
		IndexCodeDateTimeRandomImpl ictr = new IndexCodeDateTimeRandomImpl();
		int result = ictr.codeLength();
		Assert.assertEquals(result, 20);
		IndexCodeDB33Impl icd = new IndexCodeDB33Impl();
		result = icd.codeLength();
		Assert.assertEquals(result, 18);
		IndexCodeGB28181Impl icb = new IndexCodeGB28181Impl();
		result = icb.codeLength();
		Assert.assertEquals(result, 20);
		IndexCodeGUIDImpl icg = new IndexCodeGUIDImpl();
		result = icg.codeLength();
		Assert.assertEquals(result, 32);
		icg.generate(null, null, null, null, null);
	}
	
	@Test
	public void generate() {
		IndexCodeDateTimeRandomImpl ictr = new IndexCodeDateTimeRandomImpl();
		String orgnizationCode = "";
		String deviceId = "";
		ictr.generate(orgnizationCode, type, profession, network, deviceId);
		deviceId = "11";
		ictr.generate(orgnizationCode, type, profession, network, deviceId);
		IndexCodeDB33Impl icd = new IndexCodeDB33Impl();
		orgnizationCode = "";
		deviceId = "";
		icd.generate(orgnizationCode, type, profession, network, deviceId);
		deviceId = "123";
		icd.generate(orgnizationCode, type, profession, network, deviceId);
		deviceId = "12345678";
		icd.generate(orgnizationCode, type, profession, network, deviceId);
		orgnizationCode = "14121212";
		deviceId = "";
		icd.generate(orgnizationCode, type, profession, network, deviceId);
		icd.generate(orgnizationCode, null, null, network, deviceId);
		IndexCodeGB28181Impl icb = new IndexCodeGB28181Impl();
		orgnizationCode = "";
		deviceId = "";
		icb.generate(orgnizationCode, type, profession, network, deviceId);
		deviceId = "123";
		icb.generate(orgnizationCode, type, profession, network, deviceId);
		deviceId = "12345678";
		icb.generate(orgnizationCode, type, profession, network, deviceId);
		orgnizationCode = "14121212";
		deviceId = "";
		icb.generate(orgnizationCode, type, profession, network, deviceId);
		icb.generate(orgnizationCode, null, null, null, deviceId);
		IndexCodeGUIDImpl icg = new IndexCodeGUIDImpl();
		icg.generate(null, null, null, null, null);
	}
}
