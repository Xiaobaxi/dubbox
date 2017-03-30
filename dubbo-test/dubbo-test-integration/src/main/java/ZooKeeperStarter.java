
import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.management.JMException;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.jmx.ManagedUtil;
import org.apache.zookeeper.server.DatadirCleanupManager;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class ZooKeeperStarter implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperStarter.class);

	private static final String ZOOKEEPER_CONFIG_FILE = "zoo.cfg";

	// 监听客户端连接的端口
	private int port = 2181;
	// 存储内存中数据库快照的位置
	private String dataDir = "./zoo_data";
	// 基本事件单元，以毫秒为单位.它用来控制心跳和超时，默认情况下最小的会话超时时间为两倍的 tickTime
	private int tickTime = 2000;
	// 指定限制连接到 ZooKeeper的客户端的数量，限制并发连接的数量，它通过 IP 来区分不同的客户端
	private int maxClientCnxns = 50;
	// 当在清理数据时需要保留的快照文件和事务日志文件的个数
	protected int snapRetainCount = 3;
	// 指定了在2个清理数据间相隔的时间，单位为小时
	protected int purgeInterval = 0;
	// 最小的会话超时时间 defaults to -1 if not set explicitly
	protected int minSessionTimeout = -1;
	// 最大会话超时时间 defaults to -1 if not set explicitly
	protected int maxSessionTimeout = -1;
	private String enable;

	public ZooKeeperStarter() {
	}

	public void afterPropertiesSet() throws Exception {
		try {
			ManagedUtil.registerLog4jMBeans();
		} catch (JMException e) {
			LOG.warn("Unable to register log4j JMX control", e);
		}
		start();
	}

	private void start() {
		final ServerConfig config = new ServerConfig();
		String basePath = this.getClass().getResource("/").getPath();
		String zookCfgPath = basePath + ZOOKEEPER_CONFIG_FILE;
		try {
			config.parse(zookCfgPath);
		} catch (ConfigException e) {
			LOG.warn("Zookeeper configration file parse error. Otherwise, parse from default args.", e);
			String[] args = new String[] { String.valueOf(port), dataDir, String.valueOf(tickTime), String.valueOf(maxClientCnxns) };
			config.parse(args);
		}
		// Set the snapRetainCount and purgeInterval
		setPurgeConfigByPath(zookCfgPath);
		// Start and schedule the the purge task
		DatadirCleanupManager purgeMgr = new DatadirCleanupManager(config.getDataDir(), config.getDataLogDir(), snapRetainCount, purgeInterval);
		purgeMgr.start();

		Thread t = new Thread() {
			@Override
			public void run() {
				this.setName("ZooKeeper-main");
				try {
					ZooKeeperServerMain main = new ZooKeeperServerMain();
					main.runFromConfig(config);
				} catch (Exception e) {
					LOG.error("Zookeeper terminated.", e);
				}
			}
		};
		t.setDaemon(true);
		t.start();
		LOG.info("ZooKeeper started at port:{}", port);
	}

	/**
	 * 解析zookeeper的配置文件，获取到对数据文件和日志文件的配置 FIXME
	 * 调用方法的时候已经解析过一遍配置文件，这次相当于重新解析一遍配置文件
	 * 
	 * @author fangzhibin 2014-9-9 上午10:56:24
	 * @param zookCfgPath
	 * @modify: {原因} by fangzhibin 2014-9-9 上午10:56:24
	 */
	private void setPurgeConfigByPath(String zookCfgPath) {
		File configFile = new File(zookCfgPath);
		try {
			if (configFile.exists()) {
				Properties cfg = new Properties();
				FileInputStream in = new FileInputStream(configFile);
				try {
					cfg.load(in);
					for (Entry<Object, Object> entry : cfg.entrySet()) {
						Object keyObj = entry.getKey();
						if (null != keyObj) {
							String key = keyObj.toString().trim();
							Object valueObj = entry.getValue();
							if (null != valueObj) {
								String value = valueObj.toString().trim();
								if (key.equals("autopurge.snapRetainCount")) {
									snapRetainCount = Integer.parseInt(value);
								} else if (key.equals("autopurge.purgeInterval")) {
									purgeInterval = Integer.parseInt(value);
								}
							}
						}
					}
				} finally {
					in.close();
					configFile.deleteOnExit();
				}
			}
		} catch (Exception e) {
			LOG.error("autopurge.snapRetainCount and autopurge.purgeInterval not set error", e);
		}
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public void setTicktime(int tickTime) {
		this.tickTime = tickTime;
	}

	public void setMaxcnxns(int maxClientCnxns) {
		this.maxClientCnxns = maxClientCnxns;
	}

	public void setMaxClientCnxns(int maxClientCnxns) {
		this.maxClientCnxns = maxClientCnxns;
	}

	public void setSnapRetainCount(int snapRetainCount) {
		this.snapRetainCount = snapRetainCount;
	}

	public void setPurgeInterval(int purgeInterval) {
		this.purgeInterval = purgeInterval;
	}

	public void setMinSessionTimeout(int minSessionTimeout) {
		this.minSessionTimeout = minSessionTimeout;
	}

	public void setMaxSessionTimeout(int maxSessionTimeout) {
		this.maxSessionTimeout = maxSessionTimeout;
	}

	public String getEnable() {
		if (StringUtils.isBlank(enable)) {
			enable = "false";
		}
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}
}
