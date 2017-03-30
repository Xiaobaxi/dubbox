package vision.apollo.common.lock;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.AfterClass;
import org.junit.Test;

public class ZkTest {
	final static  Queue<CuratorFramework> allClients=new ConcurrentLinkedQueue<CuratorFramework>();
	private Lock createLock(String zoo,String path){
//		try {
//			return new SimpleLock(zoo, path);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		CuratorFramework client = builder.connectString("127.0.0.1:2181")
				.sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000)
				.canBeReadOnly(false)
				.retryPolicy(new ExponentialBackoffRetry(1000, 20))
				.defaultData(null)
				.build();
		client.start();
		allClients.add(client);
		try {
//			return new CuratorLockImpl(client, "/"+path);
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void test1() {
		Runnable task1 = new Runnable() {
			public void run() {
				Lock lock = null;
				try {
					lock = createLock("127.0.0.1:2181", "test1");
					lock.lock();
					Thread.sleep(3000);
					System.out.println("===Thread " + Thread.currentThread().getId() + " running");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (lock != null)
						lock.unlock();
				}
			}
		};
		new Thread(task1).start();
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ConcurrentTask[] tasks = new ConcurrentTask[10];
		for (int i = 0; i < tasks.length; i++) {
			ConcurrentTask task3 = new ConcurrentTask() {
				public void run() {
					Lock lock = null;
					try {
						lock = createLock("127.0.0.1:2181", "test2");
						lock.lock();
						System.out.println("Thread " + Thread.currentThread().getId() + " running");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			};
			tasks[i] = task3;
		}
		new ConcurrentTest(tasks);
	}

	@AfterClass
	public static void close(){
		CuratorFramework client=allClients.poll();
		while(client!=null){
			client.close();
			client=allClients.poll();
		}
	}
	
	@Test
	public void testSimple() {
		Lock lock = null;
		try {
			lock = createLock("127.0.0.1:2181", "test");
			lock.lock();
			// do something...
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lock != null)
				lock.unlock();
		}
	}
	
	@Test
	public void zkWatchTest() throws IOException, KeeperException, InterruptedException{
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 3000, new Watcher() {
			public void process(WatchedEvent event) {
				System.out.println(event);
			}
		});
		System.out.println("开始判断。。。");
		
		String node="/xxx";
		node=zk.create("/xxx", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.exists(node, true);
		System.out.println("开始监听。。。");
		zk.setData(node, "aa".getBytes(), -1);
		System.out.println("继续修改。。。");
		zk.setData(node, "bb".getBytes(), -1);
		zk.setData(node, "bb".getBytes(), -1);
		zk.exists(node, false);
		System.out.println("停止监听。。。");
		zk.setData(node, "cc".getBytes(), -1);
		
		zk.setData(node, "dd".getBytes(), -1);
		zk.close();
	}
}