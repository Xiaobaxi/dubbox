package com.alibaba.dubbo.remoting.zookeeper.zkclient;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkNoNodeException;
import com.github.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.support.AbstractZookeeperClient;

public class ZkclientZookeeperClient extends AbstractZookeeperClient<IZkChildListener> {

	private final ZkClient client;

	private volatile KeeperState state = KeeperState.SyncConnected;

	public ZkclientZookeeperClient(URL url) {
		super(url);
		client = new ZkClient(
                url.getBackupAddress(),
                url.getParameter(Constants.SESSION_TIMEOUT_KEY, Constants.DEFAULT_SESSION_TIMEOUT),
                url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_REGISTRY_CONNECT_TIMEOUT));
		client.subscribeStateChanges(new IZkStateListener() {
			public void handleStateChanged(KeeperState state) throws Exception {
				ZkclientZookeeperClient.this.state = state;
				if (state == KeeperState.Disconnected) {
					stateChanged(StateListener.DISCONNECTED);
				} else if (state == KeeperState.SyncConnected) {
					stateChanged(StateListener.CONNECTED);
				}
			}
			public void handleNewSession() throws Exception {
				stateChanged(StateListener.RECONNECTED);
			}
		});
	}

	public void createPersistent(String path) {
		try {
			client.createPersistent(path, true);
		} catch (ZkNodeExistsException e) {
		}
	}

	public void createEphemeral(String path) {
		try {
			client.createEphemeral(path);
		} catch (ZkNodeExistsException e) {
		}
	}

	public void delete(String path) {
		try {
			client.delete(path);
		} catch (ZkNoNodeException e) {
		}
	}

	public List<String> getChildren(String path) {
		try {
			return client.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
	}

	public boolean isConnected() {
		return state == KeeperState.SyncConnected;
	}

	public void doClose() {
		client.close();
	}

	public IZkChildListener createTargetChildListener(String path, final ChildListener listener) {
		return new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				listener.childChanged(parentPath, currentChilds);
			}
		};
	}

	public List<String> addTargetChildListener(String path, final IZkChildListener listener) {
		return client.subscribeChildChanges(path, listener);
	}

	public void removeTargetChildListener(String path, IZkChildListener listener) {
		client.unsubscribeChildChanges(path,  listener);
	}

	@Override
	public void createOrUpdate(String path, byte[] data, boolean ephemeral) {
		if(!client.exists(path)) {
			if (ephemeral) {
				client.createEphemeral(path);
			} else {
				client.createPersistent(path, true);
			}
		}
		client.writeData(path, data);
	}

	@Override
	public String readData(String path) {
		String data = null;
		if(client.exists(path)) {
			try {
				data = new String(client.readData(path, true), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("to read the data for path error", e);
			}
		}
		return data;
	}

	@Override
	public boolean checkExist(String path) {
		if(null != path) {
			return client.exists(path);
		}
		return false;
	}

}
