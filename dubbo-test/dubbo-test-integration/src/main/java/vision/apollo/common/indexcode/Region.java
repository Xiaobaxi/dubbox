package vision.apollo.common.indexcode;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import jef.tools.StringUtils;
import jef.tools.chinese.PinyinUtil;

/**
 * 描述中国的一个行政地区
 * 
 * @author jiyi
 * 
 */
public class Region {
	/**
	 * 行政编码(6位)，如北京为110000
	 */
	private String code;
	/**
	 * 行政区名称
	 */
	private String name;
	/**
	 * 经度
	 */
	private double longitude;
	/**
	 * 纬度
	 */
	private double latitude;
	/**
	 * 级别，中国=0 省(如浙江)=1 市（如杭州)=2 县区(如富阳)=3
	 * 
	 * @see #LEVEL_COUNTRY
	 * @see #LEVEL_PROVINCE
	 * @see #LEVEL_CITY
	 * @see #LEVEL_COUNTY
	 */
	private int level;
	/**
	 * 该行政区下的所有行政区。
	 */
	private List<Region> children;
	/**
	 * 该行政区的上级行政区。
	 */
	private Region parent;
	/**
	 * 该地区的拼音首字母
	 * 
	 * @return
	 */
	private String captialPinyin;

	/**
	 * 该地区的完整拼音
	 */
	private String pinyin;

	public Region(){
	}
	
	Region(String code, String name, String longlat) {
		this.code = code;
		this.name = name;
		if (StringUtils.isNotEmpty(longlat)) {
			if (longlat.charAt(0) == '"') {
				longlat = longlat.substring(1, longlat.length() - 1);
			}
			int index = longlat.indexOf(',');
			if (index > -1) {
				this.longitude = StringUtils.toDouble(longlat.substring(0, index), 0d);
				this.latitude = StringUtils.toDouble(longlat.substring(index + 1), 0d);
			}
		}
		if (code.endsWith("000000")) {
			level = 0;
		} else if (code.endsWith("0000")) {
			level = 1;
		} else if (code.endsWith("00")) {
			level = 2;
		} else {
			level = 3;
		}
		this.captialPinyin=PinyinUtil.getPinYinHeadChar(name);
		this.pinyin=PinyinUtil.getPinyin(name);

	}

	/**
	 * 返回 GB/T 2260-2007规范的行政区编码
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * 返回 GB/T 2260-2007规范的行政区名称
	 * @return
	 */
	public String getName() {
		return name;
	}


	/**
	 * 返回纬度
	 */
	public double getLongitude() {
		return longitude;
	}


	/**
	 * 返回 纬度
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}


	/**
	 * 返回行政级别
	 * 
	 * @return
	 * @see #LEVEL_COUNTRY
	 * @see #LEVEL_PROVINCE
	 * @see #LEVEL_CITY
	 * @see #LEVEL_COUNTY
	 */
	public int getLevel() {
		return level;
	}

	void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 得到该行政区的所有下属行政区
	 * @return
	 */
	public List<Region> getChildren() {
		return children;
	}

	void setChildren(List<Region> children) {
		this.children = children;
	}

	/**
	 * 得到该行政区的上级行政区
	 * @return
	 */
	public Region getParent() {
		return parent;
	}

	public void setParent(Region parent) {
		this.parent = parent;
	}

	/**
	 * 返回拼音首字母，全小写
	 * @return
	 */
	public String getCaptialPinyin() {
		return captialPinyin;
	}

	void setCaptialPinyin(String captialPinyin) {
		this.captialPinyin = captialPinyin;
	}

	/**
	 * 返回完整拼音，无空格，全小写
	 * 
	 * @return
	 */
	public String getPinyin() {
		return pinyin;
	}

	void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	/**
	 * 行政级别 国家
	 */
	public static final int LEVEL_COUNTRY = 0;
	/**
	 * 行政级别 省
	 */
	public static final int LEVEL_PROVINCE = 1;
	/**
	 * 行政级别 地级市
	 */
	public static final int LEVEL_CITY = 2;
	/**
	 * 行政级别 县、区
	 */
	public static final int LEVEL_COUNTY = 3;

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Region){
			Region rhs=(Region)obj;
			return ObjectUtils.equals(this.code, rhs.code);
		}
		return false;
	}

	@Override
	public String toString() {
		return code+":"+name;
	}
}
