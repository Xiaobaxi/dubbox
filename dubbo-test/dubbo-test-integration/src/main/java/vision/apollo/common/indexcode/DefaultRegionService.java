package vision.apollo.common.indexcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import jef.tools.IOUtils;

/**
 * 中国行政区服务的默认实现
 * 
 * @author jiyi
 * 
 */
public class DefaultRegionService implements RegionService {
	private static final Data data = new Data();

	private static class Data {
		List<Region> allRegions;
		Map<String, Region> codeIndex = new TreeMap<String, Region>();
		Map<Character, List<Region>> pinyinIndex = new HashMap<Character, List<Region>>();
		Region root;

		public Data() {
			URL resource = this.getClass().getResource("GB_2260-2007.csv");
			Assert.notNull(resource, "没有找到 GB/T 2260-2007全国行政区域代码表");
			BufferedReader reader = IOUtils.getReader(resource, "GB18030");
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("#"))
						continue;
					String[] params = StringUtils.split(line, ',');
					if (params.length < 3) {
						continue;
					}
					Region r = new Region(params[0].trim(), params[1].trim(), params[3]);
					codeIndex.put(r.getCode(), r);
					List<Region> rs = pinyinIndex.get(r.getCaptialPinyin().charAt(0));
					if (rs == null) {
						rs = new ArrayList<Region>();
						pinyinIndex.put(r.getCaptialPinyin().charAt(0), rs);
					}
					rs.add(r);
					if (r.getLevel() == Region.LEVEL_COUNTRY) {
						root = r;
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
			if (root != null) {
				processTree(root);
			}
			allRegions = Arrays.asList(codeIndex.values().toArray(new Region[codeIndex.size()]));
		}

		private void processTree(Region parent) {
			String mask = parent.getCode().substring(0, parent.getLevel() * 2);
			List<Region> children = findRegions(parent.getLevel() + 1, mask);
			if (children.isEmpty() && parent.getLevel() == Region.LEVEL_PROVINCE) { // 直辖市是从省级直接到区县级的
				children = findRegions(parent.getLevel() + 2, mask);
			}
			parent.setChildren(children);
			for (Region child : children) {
				child.setParent(parent);
				processTree(child);
			}
		}

		@SuppressWarnings("unchecked")
		private List<Region> findRegions(int level, String string) {
			List<Region> result = new ArrayList<Region>();
			for (Region r : codeIndex.values()) {
				if (r.getLevel() == level) {
					if (r.getCode().startsWith(string)) {
						result.add(r);
					}
				}
			}
			return result.isEmpty() ? Collections.EMPTY_LIST : result;
		}

	}

	public Region getRegionById(String id) {
		return data.codeIndex.get(id);
	}

	public List<Region> getAll() {
		return data.allRegions;
	}

	public List<Region> getRegionStartsWith(String id) {
		List<Region> result = new ArrayList<Region>();
		for (Region r : data.codeIndex.values()) {
			if (r.getCode().startsWith(id)) {
				result.add(r);
			}
		}
		return result;
	}

	public List<Region> findRegion(String name) {
		return findRegion(name, 0);
	}

	public List<Region> findRegion(String name, int level) {
		List<Region> result = new ArrayList<Region>();
		
		char firstChar=0;
		if (StringUtils.isNotEmpty(name)) {
			firstChar= name.charAt(0);
		}
		if (CharUtils.isAsciiAlpha(firstChar)) {// 按拼音查找
			name = name.toLowerCase();
			List<Region> rgs = data.pinyinIndex.get(name.charAt(0));
			if (rgs != null) {
				for (Region r : rgs) {
					if (level > 0 && level != r.getLevel())
						continue;
					if (r.getCaptialPinyin().startsWith(name) || r.getPinyin().startsWith(name)) {
						result.add(r);
					}
				}
			}
		} else if (CharUtils.isAsciiNumeric(firstChar)) {// 按编码查找
			for (Region r : data.codeIndex.values()) {
				if (level > 0 && level != r.getLevel())
					continue;
				if (r.getCode().startsWith(name)) {
					result.add(r);
				}
			}
		} else { // 按名称查找
			for (Region r : data.codeIndex.values()) {
				if (level > 0 && level != r.getLevel())
					continue;
				if (r.getName().indexOf(name) > -1) {
					result.add(r);
				}
			}
		}
		// 返回结果按按行政区划和拼音顺序排序，行政级别高的排前面，编码较小的排前面，这样可以让知名度较高的城市排到前面
		Collections.sort(result, new Comparator<Region>() {
			public int compare(Region o1, Region o2) {
				if (o1.getLevel() > o2.getLevel())
					return 1;
				if (o1.getLevel() < o2.getLevel())
					return -1;
				return o1.getCode().compareTo(o2.getCode());
			}

		});
		return result;
	}

	public List<Region> findRegion(String name, String parentCode, boolean cascade) {
		Region current = getRegionById(parentCode);
		if (current == null)
			return Collections.emptyList();
		List<Region> result = new ArrayList<Region>();
		doFind(name, current, cascade, result);
		return result;
	}

	private void doFind(String name, Region current, boolean cascade, List<Region> result) {
		for (Region r : current.getChildren()) {
			if(StringUtils.isNotEmpty(name)){
				char firstChar = name.charAt(0);
				if (CharUtils.isAsciiAlpha(firstChar)) {// 按拼音查找
					name = name.toLowerCase();
					if (r.getCaptialPinyin().startsWith(name) || r.getPinyin().startsWith(name)) {
						result.add(r);
					}
				} else if (CharUtils.isAsciiNumeric(firstChar)) {// 按编码查找
					if (r.getCode().startsWith(name)) {
						result.add(r);
					}
				} else {
					if (r.getName().indexOf(name) > -1) {
						result.add(r);
					}
				}				
			}else{
				result.add(r);
			}
			if (cascade) {
				doFind(name, r, cascade, result);
			}
		}
	}

	public Region getRoot() {
		return data.root;
	}

}
