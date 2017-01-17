package com.alibaba.dubbo.remoting.zookeeper;

import java.util.List;

import com.alibaba.dubbo.common.URL;

public interface ZookeeperClient {

	void create(String path, boolean ephemeral);

	void delete(String path);

	List<String> getChildren(String path);

	List<String> addChildListener(String path, ChildListener listener);

	void removeChildListener(String path, ChildListener listener);

	void addStateListener(StateListener listener);
	
	void removeStateListener(StateListener listener);

	boolean isConnected();

	void close();

	URL getUrl();


	/**
	 * 创建zookeeper对应的节点，并给节点赋值，没有则创建，有则覆盖值
	 * @author fangzhibin 2015年2月5日 上午9:39:17
	 * @param path
	 * @param data
	 * @param ephemeral
	 * @modify: {原因} by fangzhibin 2015年2月5日 上午9:39:17
	 */
	void createOrUpdate(String path, byte[] data, boolean ephemeral);

	/**
	 * 根据节点获取数据
	 * @author fangzhibin 2015年2月5日 下午7:52:17
	 * @param path
	 * @return
	 * @modify: {原因} by fangzhibin 2015年2月5日 下午7:52:17
	 */
	String readData(String path);

	/**
	 * 判断路径是否存在
	 * @author fangzhibin 2015年2月12日 下午4:42:44
	 * @param path
	 * @return
	 * @modify: {原因} by fangzhibin 2015年2月12日 下午4:42:44
	 */
	boolean checkExist(String path);

}
