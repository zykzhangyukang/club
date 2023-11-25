package com.coderman.club.service.redis.impl;

import com.coderman.club.service.redis.RedisService;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangyukang
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate redisTemplate;

    @Getter
    private static String host;

    @Getter
    private static int port;


    @PostConstruct
    public void getHostAndPort() {

        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
        assert jedisConnectionFactory != null;

        setHost(jedisConnectionFactory.getHostName());
        setPort(jedisConnectionFactory.getPort());

    }

    private void setPort(int port) {
        RedisServiceImpl.port = port;
    }

    private void setHost(String hostName) {
        RedisServiceImpl.host = hostName;
    }

    @SuppressWarnings("unchecked")
    public byte[] serializeKey(String key) {
        return redisTemplate.getKeySerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeValue(Object obj) {
        return redisTemplate.getValueSerializer().serialize(obj);
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeHashValue(Object obj) {
        return this.redisTemplate.getHashValueSerializer().serialize(obj);
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeHashKey(Object obj) {
        return this.redisTemplate.getHashKeySerializer().serialize(obj);
    }


    private Object deserializeValue(byte[] bytes) {
        return this.redisTemplate.getValueSerializer().deserialize(bytes);
    }

    private Object deserializeKey(byte[] bytes) {
        return this.redisTemplate.getKeySerializer().deserialize(bytes);
    }

    private Object deserializeHashValue(byte[] bytes) {
        return this.redisTemplate.getHashValueSerializer().deserialize(bytes);
    }


    @Override
    public boolean exists(String key, int db) {

        Object obj = redisTemplate.execute((RedisCallback) redisConnection -> {
            redisConnection.select(db);
            return redisConnection.exists(serializeKey(key));
        });

        if (obj != null) {
            return (boolean) obj;
        }

        return false;
    }


    @Override
    public void expire(String key, int seconds, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                redisConnection.expire(serializeKey(key), seconds);
                return null;
            }
        });

    }

    @Override
    public long incrBy(String key, long value, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                return redisConnection.incrBy(serializeKey(key), value);
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;
    }

    @Override
    public long incr(String key, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Long aLong = redisConnection.incr(serializeKey(key));
                return aLong;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;
    }

    @Override
    public long hIncrBy(String key, String field, long value, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Long aLong = redisConnection.hIncrBy(serializeKey(key), serializeHashKey(field), value);
                return aLong;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;

    }

    @Override
    public long hSize(String key, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Long aLong = redisConnection.hLen(serializeKey(key));
                return aLong;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;

    }

    @Override
    public <T> long lSize(String key, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Long aLong = redisConnection.lLen(serializeKey(key));
                return aLong;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;

    }

    @Override
    public long dbSize(int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Long aLong = redisConnection.dbSize();
                return aLong;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;
    }

    @Override
    public long del(String key, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                return redisConnection.del(serializeKey(key));
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;
    }

    @Override
    public long delKeys(List<String> keys, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                Long delNums = 0L;

                for (String key : keys) {
                    delNums += redisConnection.del(serializeKey(key));
                }


                return delNums;
            }
        });

        if (obj != null) {
            return (Long) obj;
        }

        return 0;

    }

    @Override
    public void delHash(String hashKey, String field, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                redisConnection.hDel(serializeKey(hashKey), serializeHashKey(field));
                return null;
            }
        });

    }

    @Override
    public void delHash(String key, List<String> filterField, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (String field : filterField) {
                    if (StringUtils.isNotBlank(field)) {
                        redisConnection.hDel(serializeKey(key), serializeHashKey(field));
                    }
                }

                return null;
            }
        });

    }

    @Override
    public String getString(String key, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                return deserializeValue(redisConnection.get(serializeKey(key)));
            }
        });

        if (obj != null) {
            return obj.toString();
        }

        return null;

    }


    @Override
    public String getStringBySameStrategy(String key, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                return deserializeKey(redisConnection.get(serializeKey(key)));
            }
        });

        if (obj != null) {
            return obj.toString();
        }

        return null;

    }

    @Override
    public <T> void setString(String key, String string, int db) {

        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Boolean set = redisConnection.set(serializeKey(key), serializeValue(string));
                return null;
            }
        });
    }


    @Override
    public <T> void setStringBySameStrategy(String key, String string, int db) {


        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Boolean set = redisConnection.set(serializeKey(key), serializeKey(string));
                return null;
            }
        });
    }

    @Override
    public <T> void setString(String key, String string, int seconds, int db) {
        Object execute = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                Boolean set = connection.setEx(serializeKey(key), seconds, serializeValue(string));
                return set;
            }
        });
    }

    @Override
    public String getAndSetString(String key, String string, int seconds, int db) {

        Object execute = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                Object obj = deserializeValue(connection.getSet(serializeKey(key), serializeValue(string)));
                connection.expire(serializeKey(key), seconds);
                return obj;
            }
        });

        if (execute != null) {
            return execute.toString();
        }

        return null;

    }

    @Override
    public <T> void setObject(String key, T obj, int db) {


        Object execute = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                connection.set(serializeKey(key), serializeValue(obj));
                return null;
            }
        });

    }

    @Override
    public <T> void setObject(String key, T obj, int seconds, int db) {
        Object execute = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                connection.setEx(serializeKey(key), seconds, serializeValue(obj));
                return null;
            }
        });
    }

    @Override
    public <T> T getObject(String key, Class<T> clas, int db) {

        Object execute = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                return deserializeValue(connection.get(serializeKey(key)));
            }
        });

        if (execute != null) {
            return (T) execute;
        }

        return null;

    }

    @Override
    public <T> long setList(String key, T obj, int db) {

        List result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                if (obj instanceof List) {

                    List objList = (List) obj;
                    for (Object objVal : objList) {
                        redisConnection.sAdd(serializeKey(key), serializeValue(objVal));
                    }
                } else {

                    redisConnection.sAdd(serializeKey(key), serializeValue(obj));
                }

                return null;
            }
        });

        if (CollectionUtils.isNotEmpty(result)) {

            long resultValu = 0;
            for (Object objVal : result) {
                resultValu += Long.parseLong(objVal.toString());
            }

            return resultValu;
        }


        return 0;
    }

    @Override
    public <T> void setSet(String key, Set<T> set, int db) {

        redisTemplate.delete(key);
        List result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (T objVal : set) {
                    redisConnection.sAdd(serializeKey(key), serializeValue(objVal));
                }

                return null;
            }
        });

    }

    @Override
    public <T> void addToSet(String key, T obj, int db) {

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                redisConnection.sAdd(serializeKey(key), serializeValue(obj));
                return null;
            }
        });
    }

    @Override
    public <T> boolean isSetMember(String key, T obj, int db) {

        Object object = redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.select(db);
                return redisConnection.sIsMember(serializeKey(key), serializeValue(obj));
            }
        });

        if (object != null) {
            return (boolean) object;
        }

        return false;
    }

    @Override
    public <T> void removeFromSet(String key, T obj, int db) {

        Object object = redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.select(db);
                redisConnection.sRem(serializeKey(key), serializeValue(obj));
                return null;
            }
        });

    }

    @Override
    public <T> void setListData(String key, List<T> list, int db) {

        List result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (T objVal : list) {
                    redisConnection.rPush(serializeKey(key), serializeValue(objVal));
                }

                return null;
            }
        });

    }

    @Override
    public <T> void delRListData(String key, int size, int db) {
        List result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (int i = 0; i < size; i++) {

                    redisConnection.rPop(serializeKey(key));
                }

                return null;
            }
        });
    }

    @Override
    public <T> long setListAppend(String key, T obj, int db) {

        List result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                if (obj instanceof List) {

                    List objList = (List) obj;
                    for (Object objVal : objList) {
                        redisConnection.lPush(serializeKey(key), serializeValue(objVal));
                    }
                } else {

                    redisConnection.lPush(serializeKey(key), serializeValue(obj));
                }

                return null;
            }
        });

        if (CollectionUtils.isNotEmpty(result)) {

            long resultValu = 0;
            for (Object objVal : result) {
                resultValu += Long.parseLong(objVal.toString());
            }

            return resultValu;
        }

        return 0;
    }

    @Override
    public <T> void setList(Map<String, T> map, int db) {

        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Set<Map.Entry<String, T>> set = map.entrySet();

                for (Map.Entry<String, T> entry : set) {

                    redisConnection.set(serializeKey(entry.getKey()), serializeValue(entry.getValue()));
                }

                return null;
            }
        });

    }

    @Override
    public <T> List<T> getList(String key, Class<T> clas, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                List<T> resultList = new ArrayList<>();

                ScanOptions scanOptions = ScanOptions.scanOptions().count(2000).build();
                Cursor<byte[]> cursor = redisConnection.sScan(serializeKey(key), scanOptions);

                while (cursor.hasNext()) {

                    resultList.add((T) deserializeValue(cursor.next()));
                }


                return resultList;
            }
        });

        if (obj != null && obj instanceof List) {

            return (List<T>) obj;
        }


        return null;
    }

    @Override
    public <T> Set<T> getSet(String key, Class<T> clas, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                Set<T> resultSet = new HashSet<>();

                ScanOptions scanOptions = ScanOptions.scanOptions().count(2000).build();
                Cursor<byte[]> cursor = redisConnection.sScan(serializeKey(key), scanOptions);

                while (cursor.hasNext()) {

                    resultSet.add((T) deserializeValue(cursor.next()));
                }


                return resultSet;
            }
        });

        if (obj != null && obj instanceof Set) {

            return (Set<T>) obj;
        }


        return null;

    }

    @Override
    public <T> List<T> getListData(String key, Class<T> clas, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                List<T> resultList = new ArrayList<>();

                List<byte[]> tempList = redisConnection.lRange(serializeKey(key), 0, -1);
                for (byte[] tempByte : tempList) {


                    resultList.add((T) deserializeValue(tempByte));
                }

                return resultList;
            }
        });

        if (obj != null && obj instanceof List) {

            return (List<T>) obj;
        }

        return null;
    }

    @Override
    public <T> List<T> getList(List<String> filterKey, Class<T> cls, int db) {


        List<T> result = redisTemplate.executePipelined(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                for (String key : filterKey) {


                    if (StringUtils.isBlank(key)) {
                        continue;
                    }

                    redisConnection.get(serializeKey(key));
                }

                return null;
            }
        });

        return (List<T>) result;
    }

    @Override
    public <T> Map<String, T> getMapOfList(List<String> filterKeys, Class<T> clas, int db) {


        Map<String, T> map = new HashMap<>();
        List<Object> result = redisTemplate.executePipelined(new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);
                for (String key : filterKeys) {


                    if (StringUtils.isBlank(key)) {
                        continue;
                    }

                    redisConnection.get(serializeKey(key));
                }

                return null;
            }
        });

        List<T> listTmp = (List<T>) result;
        int i = 0;
        for (String key : filterKeys) {

            if (StringUtils.isBlank(key)) {
                continue;
            }

            map.put(key, listTmp.get(i));
            i++;
        }

        return map;
    }

    @Override
    public <T> void setHash(String key, String filed, T obj, int db) {
        this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.select(db);
                return connection.hSet(serializeKey(key), serializeHashKey(filed), serializeHashValue(obj));
            }
        });

    }


    @Override
    public <T> T getHash(String key, String filed, Class<T> clas, int db) {

        Object obj = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                byte[] k = serializeKey(key);
                byte[] v = serializeHashKey(filed);

                byte[] bytes = redisConnection.hGet(k, v);

                return deserializeHashValue(bytes);
            }
        });

        if (obj != null) {

            return (T) obj;
        }


        return null;
    }

    @Override
    public <T> void setHash(String key, Map<String, T> map, int db) {

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.select(db);
                Set<Map.Entry<String, T>> set = map.entrySet();


                for (Map.Entry<String, T> entry : set) {

                    redisConnection.hSet(serializeKey(key), serializeHashKey(entry.getKey()),
                            serializeHashValue(entry.getValue()));
                }

                return null;
            }
        });

    }


    @Override
    public <T> List<T> getHash(String key, List<String> filterField, Class<T> clas, int db) {

        Object result = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (String field : filterField) {

                    if (StringUtils.isBlank(field)) {
                        continue;
                    }

                    redisConnection.hGet(serializeKey(key), serializeHashKey(field));
                }

                return null;
            }
        });


        return (List<T>) result;
    }

    @Override
    public <T> Map<String, T> getMapOfHash(String key, List<String> filterField, Class<T> clas, int db) {


        Map<String, T> map = new HashMap<>();
        List<T> result = redisTemplate.executePipelined(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(RedisConnection redisConnection) throws DataAccessException {

                redisConnection.select(db);

                for (String field : filterField) {

                    if (StringUtils.isBlank(field)) {
                        continue;
                    }

                    redisConnection.hGet(serializeKey(key), serializeHashKey(field));
                }

                return null;
            }
        });

        int i = 0;
        for (String field : filterField) {

            if (StringUtils.isBlank(field)) {
                continue;
            }

            map.put(field, result.get(i));
            i++;
        }

        return map;
    }


    @Override
    public <T> List<T> getHashAll(String key, Class<T> clas, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                List<T> list = new ArrayList<>();


                ScanOptions scanOptions = ScanOptions.scanOptions().count(2000).build();
                Cursor<Map.Entry<byte[], byte[]>> cursor = connection.hScan(serializeKey(key), scanOptions);

                while (cursor.hasNext()) {


                    Map.Entry<byte[], byte[]> entry = cursor.next();
                    byte[] key = serializeHashKey(entry.getKey());

                    if (key != null) {

                        list.add((T) deserializeHashValue(entry.getValue()));
                    }
                }
                return list;
            }
        });


        if (obj != null) {

            return (List) obj;
        }


        return null;
    }

    @Override
    public <T> Map<String, T> getMapOfHashAll(String key, Class<T> clas, int db) {


        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                Map<String, T> map = new HashMap<>();


                ScanOptions scanOptions = ScanOptions.scanOptions().count(2000).build();
                Cursor<Map.Entry<byte[], byte[]>> cursor = connection.hScan(serializeKey(key), scanOptions);

                while (cursor.hasNext()) {


                    Map.Entry<byte[], byte[]> entry = cursor.next();
                    byte[] key = serializeHashKey(entry.getKey());

                    if (key != null) {

                        map.put(key.toString(), (T) deserializeHashValue(entry.getValue()));
                    }
                }
                return map;
            }
        });


        if (obj != null) {

            return (Map<String, T>) obj;
        }

        return null;
    }

    @Override
    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    @Override
    public List<Long> executeLuaScript(String luaScript, List<String> keys, Object... args) {

        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setResultType(List.class);
        redisScript.setScriptText(luaScript);
        Object obj = redisTemplate.execute(redisScript, redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(), keys, args);

        if (obj != null) {

            return (List<Long>) obj;
        }

        return null;
    }

    @Override
    public Set<String> keys(String key, int db) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.select(db);
                Set<String> resultSet = new HashSet<>();

                Set<byte[]> keys = connection.keys(serializeKey(key));
                if (CollectionUtils.isNotEmpty(keys)) {

                    Iterator<byte[]> iterator = keys.iterator();
                    while (iterator.hasNext()) {

                        resultSet.add((String) deserializeKey(iterator.next()));
                    }
                }

                return resultSet;
            }
        });


        return (Set<String>) obj;
    }

    @Override
    public void sendMessage(String topic, Object msgObj) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Long publish = redisConnection.publish(serializeKey(topic), serializeValue(msgObj));
                return null;
            }
        }, true);
    }
}