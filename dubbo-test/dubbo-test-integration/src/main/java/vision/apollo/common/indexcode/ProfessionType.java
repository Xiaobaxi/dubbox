package vision.apollo.common.indexcode;

/**
 * GB28181 和DB33_T 629.4-2011 规定的接入行业类型
 * @author jiyi
 *
 */
public enum ProfessionType {
	POLICE_SURFACE  ("00","社会治安路面接入 包括城市路面、商业街、公共区域、重点区域等"),
	POLICE_COMMUNITY("01","社会治安社区接入 包括社区、楼宇、网吧等"),
	POLICE_INSIDE   ("02","社会治安内部接入 包括公安办公楼、留置室等"),
	POLICE_OTHER    ("03","社会治安其他接入"),
	
	TRAFFIC_ROAD    ("04","交管路面接入 包括城市主要干道、国道、高速交通"),
	TRAFFIC_CROSS   ("05","交管卡口接入 包括交叉路口、“电子警察”、卡口、收费站等"),
	TRAFFIC_INSIDE  ("06","交管内部接入 包括交管办公楼等"),
	TRAFFIC_OTHER   ("07","交管其他接入"),

	CITY_MANAGEMENT ("08","城市管理接入"),
	HEALTHCARE      ("09","卫生环保接入"),
	CUSTOMS         ("10","商检海关接入"),
	EDUCATION       ("11","教育部门接入"),
	
	
	AGRICULTURE_FARMING("40","农林牧渔业接入"),
	MINING          ("41","采矿企业接入"),
	MANUFACTURE     ("42","制造企业接入"),
	METALLURGY      ("43","冶金企业接入"),
	ELECTRIC_POWER  ("44","电力企业接入"),
	GAS_INDUSTRY    ("45","燃气企业接入"),
	CONSTRUCTION    ("46","建筑企业接入"),
	LOGISTICS       ("47","物流企业接入"), 
	POSTAL          ("48","邮政企业接入"),
	IT              ("49","信息企业接入"),
	HOTEL_CATERING  ("50","住宿和餐饮业接入"),
	FINANCIAL_INDUSTRY("51","金融企业接入"),
	REALTY_INDUSTRY  ("52","房地产业接入"),
	SERVICE_INDUSTRY("53","商务服务业接入"),
	HYDRAULIC_INDUSTRY("54","水利企业接入"),
	ENTERTAINMENT   ("55","娱乐企业接入"),
	DENIZEN  		("80","居民自建"),
	OTHER     		("90","其他"),
	
	//以下为DB33扩展的接入类型
	PRISON         ("12","看守监管接入"),
	WIRELESS       ("13","无线移动接入"),
	TRANSPORT      ("14","交通运输接入"),
	PORT_MARINE    ("15","港航管理接入"),
	RAILWAY        ("16","铁路管理接入"),
	DISASTER_PREVENTION  ("17","防汛和灾害防治等接入"),
	ALERM_OPERATION("56","报警运营服务企业接入");
	
	private ProfessionType(String code,String comment){
		this.code=code;
		this.comment=comment;
	}
	private String code;
	private String comment;
	public String getCode() {
		return code;
	}
	public String getComment() {
		return comment;
	}
	@Override
	public String toString() {
		return String.valueOf(code);
	}
}
