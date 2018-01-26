import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

/**
 * Redis数据库持久层接口实现类
 *
 * @author FreeDroid
 */
@Repository("redisDao")
public class RedisDaoImpl implements RedisDao {

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Long lPushObj(String listKey, Object listValue) {
		return (Long) redisTemplate.execute((RedisCallback) redisConnection -> {
            RedisSerializer redisSerializer = redisTemplate.getStringSerializer();
            byte[] key = redisSerializer.serialize(listKey);
            byte[] value = null;
            if (listValue instanceof String) {
                value = redisSerializer.serialize(listValue);
            } else {
                value = SerializeUtil.serialize(listValue);
            }
            return redisConnection.lPush(key, value);
        });
	}

	@Override
	public Object lPopObj(String listKey) {
		return resultLPop(listKey, Object.class);
	}

	private Object resultLPop(String listKey, Class clzz) {
		return redisTemplate.execute((RedisCallback) redisConnection -> {
            RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
            byte[] key = redisSerializer.serialize(listKey);
            if (Objects.equals(String.class, clzz)) {
                return redisSerializer.deserialize(redisConnection.lPop(key));
            } else {
                return SerializeUtil.deserialize(redisConnection.lPop(key));
            }
        });
	}

	@Override
	public Long rPushObj(String listKey, Object listValue) {
		return (Long) redisTemplate.execute((RedisCallback) redisConnection -> {
            RedisSerializer redisSerializer = redisTemplate.getStringSerializer();
            byte[] key = redisSerializer.serialize(listKey);
            byte[] value = null;
            if (listValue instanceof String) {
                value = redisSerializer.serialize(listValue);
            } else {
                value = SerializeUtil.serialize(listValue);
            }
            return redisConnection.rPush(key, value);
        });
	}

	@Override
	public Object rPopObj(String listKey) {
		return resultRPop(listKey, Object.class);
	}

	private Object resultRPop(String listKey, Class clzz) {
		return redisTemplate.execute((RedisCallback) redisConnection -> {
            RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
            byte[] key = redisSerializer.serialize(listKey);
            if (Objects.equals(String.class, clzz)) {
                return redisSerializer.deserialize(redisConnection.rPop(key));
            } else {
                return SerializeUtil.deserialize(redisConnection.rPop(key));
            }
        });
	}

	@Override
	public Long lPushString(String listKey, String listValue) {
		return lPushObj(listKey, listValue);
	}

	@Override
	public String lPopString(String listKey) {
		return (String) resultLPop(listKey, String.class);
	}

	@Override
	public Long rPushString(String listKey, String listValue) {
		return rPushObj(listKey, listValue);
	}

	@Override
	public String rPopString(String listKey) {
		return (String) resultRPop(listKey, String.class);
	}

	@Override
	public boolean add(final String key, final Object obj, final String tableName, final Long expirationTime) {
		return (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] keys = serializer.serialize(getKey(tableName, key));
            byte[] value = SerializeUtil.serialize(obj);
            Boolean result = connection.setNX(keys, value);
            if (expirationTime != null) {
                connection.expire(keys, expirationTime);
            }
            return result;
        });
	}

	@Override
	public boolean add(final Map<String, Object> map, final String tableName, final Long expirationTime) {
		return (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (map == null || map.isEmpty()) {
                return false;
            }
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    byte[] key = serializer.serialize(getKey(tableName, entry.getKey()));
                    byte[] value = SerializeUtil.serialize(entry.getValue());
                    connection.setNX(key, value);
                    if (expirationTime != null) {
                        connection.expire(key, expirationTime);
                    }
                }
            }
            return true;
        }, false, true);
	}

	@Override
	public boolean delete(String key, String tableName) {
		List<String> keys = new ArrayList<>();
		keys.add(key);
		return delete(keys, tableName);
	}

	@Override
	public boolean delete(final List<String> keys, final String tableName) {
		return (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (keys == null || keys.isEmpty()) {
                return false;
            }
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            for (String key : keys) {
                byte[] k = serializer.serialize(getKey(tableName, key));
                connection.del(k);
            }
            return true;
        }, false, true);
	}

	@Override
	public boolean update(String key, Object obj, String tableName, Long expirationTime) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, obj);
		return update(map, tableName, expirationTime);
	}

	@Override
	public boolean update(final Map<String, Object> objects, final String tableName, final Long expirationTime) {
		return (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (objects == null || objects.isEmpty()) {
                return null;
            }
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                if (entry.getKey() != null) {
                    byte[] key = serializer.serialize(getKey(tableName, entry.getKey()));
                    byte[] value = SerializeUtil.serialize(entry.getValue());
                    if (expirationTime != null) {
                        connection.set(key, value, Expiration.seconds(expirationTime),
                                RedisStringCommands.SetOption.UPSERT);
                    } else {
                        connection.set(key, value);
                    }
                }
            }
            return true;
        }, false, true);
	}

	@Override
	public Object get(final String key, final String tableName) {
		return redisTemplate.execute((RedisCallback<Object>) connection -> {
            RedisSerializer serializer = redisTemplate.getStringSerializer();
            String k = getKey(tableName, key);
            byte[] key1 = serializer.serialize(k);
            byte[] value = connection.get(key1);
            if (value == null) {
                return null;
            }
            return SerializeUtil.deserialize(value);
        });
	}

	@Override
	public Map<String, Object> getTableData(final String tableName) {
		return (Map<String, Object>) redisTemplate.execute((RedisCallback) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] tableKey = serializer.serialize(getPattern(tableName));
            Set<byte[]> set = connection.keys(tableKey);
            if (set == null || set.isEmpty()) {
                return null;
            }
            Map<String, Object> map = new HashMap<>();
            for (byte[] bytes : set) {
                String key = serializer.deserialize(bytes);
                key = key.substring(getPattern(tableName).length() - 1, key.length());
                Object obj = get(key, tableName);
                map.put(key, obj);
            }
            return map;
        });
	}

	/**
	 * 拼接key前缀，模糊查询使用
	 * 
	 * @param tableName 表名
	 * @return 返回拼接之后的字符串
	 */
	private String getPattern(String tableName) {
		return "tableName=>" + tableName + ":*";
	}

	/**
	 * 返回tableName+key作为Redis数据库中的key
	 * 
	 * @param tableName 表名
	 * @param key key
	 * @return "tableName=>"+tableName+":"+key
	 */
	private String getKey(String tableName, String key) {
		return "tableName=>" + tableName + ":" + key;
	}

}
