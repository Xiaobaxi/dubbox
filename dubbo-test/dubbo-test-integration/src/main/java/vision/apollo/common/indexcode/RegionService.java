package vision.apollo.common.indexcode;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 中国行政区服务
 * @author jiyi
 *
 */
@WebService
@Path("/regions")
public interface RegionService {
	
	/**
	 * 传入六位长度的ID，返回该地区
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(value = {MediaType.APPLICATION_JSON})
	Region getRegionById(@PathParam("id") String id);
	
	
	/**
	 *  返回系统已知的区域，不区分层级
	 */
	@GET
	@Path("/all")
	@Produces(value = {MediaType.APPLICATION_JSON})
	List<Region> getAll();

	/**
	 * 传入ID的头几位，返回该地区
	 * @param id
	 * @return
	 */
	@GET
	@Path("/starts_with/{id}")
	@Produces(value = {MediaType.APPLICATION_JSON})
	List<Region> getRegionStartsWith(@PathParam("id") String id);
	
//	/**
//	 * 传入区域名称的头几位，查找符合文字的区域
//	 * 也可以传入区域的拼音字母，查找符合拼音的区域，比如输入HZ，返回
//	 * @param name
//	 * @return
//	 */
//	@POST
//	@Path("/find/")
//	@Produces(value = {MediaType.APPLICATION_JSON})
//	List<Region> findRegion(@HeaderParam("name") String name);
//	
//	/**
//	 * 传入区域名称的头几位，查找符合文字的区域
//	 * @param name
//	 * @param level 先定查找的行政区级别 1省 2市 3县区
//	 * 
//	 * @return
//	 */
//	@POST
//	@Path("/find2/")
//	@Produces(value = {MediaType.APPLICATION_JSON})
//	@Consumes(MediaType.APPLICATION_JSON)
//	List<Region> findRegion(String name,int level);
//	
//	/**
//	 * 根据上级单位的编码，在下级行政区中查找名称符合的行政区
//	 * @param name 查找关键字
//	 * @param parentCode 上级节点编码(不含)
//	 * @param cascade true=递归查找，即直接从省找到县， false=只查找下级行政区，不查找下级的下级
//	 * @return
//	 */
//	@POST
//	@Path("/find3/")
//	@Produces(value = {MediaType.APPLICATION_JSON})
//	@Consumes(MediaType.APPLICATION_JSON)
//	List<Region> findRegion(String name,String parentCode,boolean cascade);
//	
//	/**
//	 * 特殊节点中国作为所有区域的根节点。可以得到整棵区域树。
//	 * @return
//	 */
//	@GET
//	@Path("/root")
//	@Produces(value = {MediaType.APPLICATION_JSON})
//	Region getRoot();
}
