/**   
* @Package vision.apollo.common.indexcode
* TODO(用一句话描述该文件做什么)
* @author  lingshuang kong  2014-4-18 上午10:07:44    
*/

package vision.apollo.common.indexcode;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @ClassName: IndexCodeUtilTest
 * 
 * @author lingshuang kong 2014-4-18 上午10:07:44
 * 
 */

public class IndexCodeUtilTest {

	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for {@link vision.apollo.common.indexcode.IndexCodeUtil#genDateTimeRandomIndexCode()}.
	 */
	@Test
	public void testGenDateTimeRandomIndexCode() {
		Assert.assertNotSame(IndexCodeUtil.genDateTimeRandomIndexCode(),
				IndexCodeUtil.genDateTimeRandomIndexCode());
	}

}
