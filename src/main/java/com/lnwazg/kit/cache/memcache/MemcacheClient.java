package com.lnwazg.kit.cache.memcache;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.lnwazg.kit.cache.CacheClient;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * memcache的客户端<br>
 * 采用xmemcached的实现（基于Java NIO实现）<br>
 * 1.Xmemcached支持所有的memcached协议<br>
 * 2.Memcached的分布只能通过客户端来实现，XMemcached实现了此功能，并且提供了一致性哈希(consistent hash)算法的实现。<br>
 * 3.XMemcached允许通过设置节点的权重来调节memcached的负载，设置的权重越高，该memcached节点存储的数据将越多，所承受的负载越大。<br>
 * 4.XMemcached允许通过JMX或者代码编程实现节点的动态添加或者移除，方便用户扩展和替换节点等。<br>
 * 5.XMemcached通过JMX暴露的一些接口，支持client本身的监控和调整，允许动态设置调优参数、查看统计数据、动态增删节点等。<br>
 * @author nan.li
 * @version 2018年9月26日
 */
public class MemcacheClient implements CacheClient
{
    MemcachedClientBuilder builder;
    
    public MemcacheClient(String address, int port)
    {
        builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(String.format("%s:%s", address, port)));
    }
    
    public MemcachedClient getClient()
    {
        try
        {
            return builder.build();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void put(String key, String value)
    {
        //0表示永久存储（默认是一个月）
        put(key, value, 0);
    }
    
    @Override
    public void put(String key, String value, int expireSeconds)
    {
        MemcachedClient memcachedClient = getClient();
        try
        {
            //参数value就是实际存储的数据，可以是任意的java可序列化类型
            //此处我们只存String
            memcachedClient.set(key, expireSeconds, value);
            memcachedClient.shutdown();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (MemcachedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public String get(String key)
    {
        MemcachedClient memcachedClient = getClient();
        try
        {
            String value = memcachedClient.get(key);
            memcachedClient.shutdown();
            return value;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (MemcachedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
