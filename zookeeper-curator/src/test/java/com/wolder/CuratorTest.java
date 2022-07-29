package com.wolder;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorTest {
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
     * 创建节点
     */
    @Test
    public void test2() throws Exception {

        /**
         * 若创建节点没有给定数据 则默认保存本机ip
         */
        //String s = client.create().forPath("/test1");

        /**
         * 给定了数据的话 则不会保存本机地址
         */
        //String s = client.create().forPath("/test2","test_data".getBytes());

        /**
         * 创建临时节点(如果本程序结束 cli中查看不到)
         */
        /*
        String s = client.create().withMode(CreateMode.EPHEMERAL).forPath("/test3");
        Thread.sleep(10000);
        */

        /**
         * 创建多级节点
         */
        client.create().creatingParentsIfNeeded().forPath("/test5");

    }

    /**
     * 查询节点
     * 1. 查询数据 对应get
     * 2. 查询节点 对应ls
     * 3. 查询状态 对应ls -s
     */
    @Test
    public void test3() throws Exception {
        /**
         * 查询数据
         */
        /*
        byte[] bytes = client.getData().forPath("/test1");
        System.out.println(new String(bytes));
        */

        /**
         * 查询节点
         */

//        List<String> list = client.getChildren().forPath("/");
//        System.out.println(list);

        /**
         * 查询状态
         */
        Stat stat = new Stat();
        System.out.println(stat);
        client.getData().storingStatIn(stat).forPath("/");
        System.out.println(stat);
    }

    /**
     * 修改 数据
     * 根据版本修改
     */
    @Test
    public void test4() throws Exception {
        /**
         * 简单修改
         */
        //client.setData().forPath("/test2","set_data".getBytes());

        /**
         * 根据版本修改 (乐观锁)
         */
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/test2");
        int version = stat.getVersion();
        /*单机模拟版本不一致*/
        //client.setData().withVersion(version-1).forPath("/test2","set_data_withVersion".getBytes());
        client.setData().withVersion(version).forPath("/test2","set_data_withVersion".getBytes());

    }

    /**
     * 删除节点 delete deleteAll(递归删除)
     */
    @Test
    public void test5() throws Exception {
        /**
         * 删除
         */
        //client.delete().forPath("/test1");

        /**
         * 删除带有子节点的节点
         */
        //client.delete().deletingChildrenIfNeeded().forPath("/test5");


        /**
         * 必须删除成功的删除
         */




    }

}
