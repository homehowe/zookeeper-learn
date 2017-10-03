package bhz.zookeeper.base;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.Create2Callback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * Zookeeper base学习笔记
 * @since 2015-6-13
 */
public class ZookeeperBase {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = "192.168.1.111:2181,192.168.1.112:2181,192.168.1.113:2181";
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 
	/** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
	static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception{
		
		ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				//获取事件的状态
				KeeperState keeperState = event.getState();
				EventType eventType = event.getType();
				//如果是建立连接
				if(KeeperState.SyncConnected == keeperState){
					if(EventType.None == eventType){
						//如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
						connectedSemaphore.countDown();
						System.out.println("zk 建立连接");
					}
				}
			}
		});

		//进行阻塞
		connectedSemaphore.await();
		
		//创建父节点
		//System.out.println("获取zk实力对象:"+ zk);
		zk.create("/testRoot/child",
				"i am a child".getBytes(),
				Ids.OPEN_ACL_UNSAFE, 
				CreateMode.PERSISTENT, 
				new AsyncCallback.StringCallback(){
					//zk服务器的相应码 0
					public void processResult(int rc, String parentPath, Object ctx, String name) {
						System.out.println("回调线程：" + Thread.currentThread().getName());
						System.out.println("rc: " + rc);
						System.out.println("parentPath: " + parentPath);
						System.out.println("ctx:" + ctx);
						System.out.println("name: " + name);
					}},
				"param");
		
		System.out.println("程序继续执行。。。");
		System.out.println("主线程：" + Thread.currentThread().getName());
		TimeUnit.SECONDS.sleep(5);
		
		
		//创建子节点
//		zk.create("/testRoot/children", "children data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//获取节点洗信息
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));
		
//		for(String path : zk.getChildren("/testRoot", false)){
//			//path: [children2, children]
//			 System.out.println("数据：" + zk.getData("/testRoot/" + path, false, null));
//		}
		//修改节点的值
//		zk.setData("/testRoot", "modify data root".getBytes(), -1);
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));		
		
		//判断节点是否存在
//		System.out.println("stat:" + zk.exists("/testRoot/children2", false));
		//删除节点
//		zk.delete("/testRoot/children2", -1);
		
//		System.out.println("stat:" + zk.exists("/testRoot/children", false));
		
		zk.close();
		
		
		
	}
	
}
