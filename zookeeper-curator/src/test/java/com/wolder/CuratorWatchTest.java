package com.wolder;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorWatchTest {

    private CuratorFramework client;

    /**
     * 连接测试
     */
    //@Test
    @Before //在进行测试之前先执行该方法
    public void test1() {

        /**
         * 设置重传时间 和次数
         */
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 2);
        /*=====================函数式构建===================================*/
        /**
         * @param1 url 集群的话通过 , 分割
         * @param2 会话超时时间
         * @param3 连接超时时间
         * @param4 重试策略
         */
        //client = CuratorFrameworkFactory.newClient("192.168.59.130:2181", 60000, 15000, policy);
        /*=====================builder式构建===================================*/
        /**
         * 多设置了一个命名空间,之后创建的所有节点以woldier为根节点
         */
        client = CuratorFrameworkFactory.builder().
                connectString("192.168.59.130:2181").
                sessionTimeoutMs(60000).
                connectionTimeoutMs(15000).
                retryPolicy(policy).
                namespace("woldier").build();


        //开启客户端
        client.start();
    }

    /**
     * 测试结束后关闭连接
     */
    @After
    public void closeClient() {
        client.close();
    }


    /**
     * 演示 NodeCache：给指定一个节点注册监听器
     */

    @Test
    public void testNodeCache() throws Exception {
        //1. 创建NodeCache对象
        final NodeCache nodeCache = new NodeCache(client,"/test1");
        //2. 注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了~");
                //获取修改节点后的数据
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });

        //3. 开启监听.如果设置为true，则开启监听是，加载缓冲数据
        nodeCache.start(true);


        while (true){

        }
    }




    /**
     * 演示 PathChildrenCache：监听某个节点的所有子节点们
     */

    @Test
    public void testPathChildrenCache() throws Exception {
        //1.创建监听对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/",true);

        //2. 绑定监听器
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("子节点变化了~");
                System.out.println(event);
                //监听子节点的数据变更，并且拿到变更后的数据
                //1.获取类型
                PathChildrenCacheEvent.Type type = event.getType();
                //2.判断类型是否是update
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("数据变了！！！");
                    byte[] data = event.getData().getData();
                    System.out.println(new String(data));

                }
            }
        });
        //3. 开启
        pathChildrenCache.start();

        while (true){

        }
    }


    /**
     * 演示 TreeCache：监听某个节点自己和所有子节点们
     */

    @Test
    public void testTreeCache() throws Exception {
        //1. 创建监听器
        TreeCache treeCache = new TreeCache(client,"/app2");

        //2. 注册监听
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println("节点变化了");
                System.out.println(event);
            }
        });

        //3. 开启
        treeCache.start();

        while (true){

        }
    }




}
