import java.util.List;
import java.util.Map;

/**
 * Redis持久层接口 Redis里实际的key为tableName+key， 添加table概念便于对数据的分类管理，
 * 不建议使用tableName来查询，模糊查询对资源消耗高
 *
 * @author FreeDroid
 * @created 2018 - 01 - 02 15:47
 */
public interface RedisDao {

	/**
	 * 从队列左边插入一个对象
	 * @param listKey 队列key
	 * @param listValue 队列值value
	 * @return 返回添加数量
	 */
	Long lPushObj(String listKey, Object listValue);

	/**
	 * 从队列左边取出一个对象
	 * @param listKey 队列key
	 * @return 返回value
	 */
	Object lPopObj(String listKey);

	/**
	 * 从队列右边插入一个对象
	 * @param listKey 队列key
	 * @param listValue 队列值value
	 * @return 返回添加数量
	 */
	Long rPushObj(String listKey, Object listValue);

	/**
	 * 从队列右边取出一个对象
	 * @param listKey 队列key
	 * @return 返回value
	 */
	Object rPopObj(String listKey);

	/**
	 * 从队列左边插入一条字符串数据
	 * @param listKey 队列key
	 * @param listValue 队列值value
	 * @return 返回添加数量
	 */
	Long lPushString(String listKey, String listValue);

	/**
	 * 从队列左边取出一条字符串数据
	 * @param listKey 队列key
	 * @return 返回value
	 */
	String lPopString(String listKey);

	/**
	 * 从队列右边插入一条字符串数据
	 * @param listKey 队列key
	 * @param listValue 队列值value
	 * @return 返回添加数量
	 */
	Long rPushString(String listKey, String listValue);

	/**
	 * 从队列右边取出一条字符串数据
	 * @param listKey 队列key
	 * @return 返回value
	 */
	String rPopString(String listKey);

	/**
	 * 在tableName表添加一个元素，key存在不更新value
	 * 
	 * @param key key
	 * @param obj value对象
	 * @param tableName 表名
	 * @param expirationTime 存储时间，单位s，到期将删除该条记录，传null不设时间
	 * @return 添加成功返回true，添加失败或已存在key返回false
	 */
	boolean add(String key, Object obj, String tableName, Long expirationTime);

	/**
	 * 在表tableName批量添加对象，使用pipeline方式，key存在不更新value
	 * 
	 * @param map 批量添加的集合
	 * @param tableName 表名
	 * @param expirationTime 存储时间，单位s，到期将删除该条记录，传null不设时间
	 * @return 添加成功返回true，添加失败或已存在key返回false
	 */
	boolean add(Map<String, Object> map, String tableName, Long expirationTime);

	/**
	 * 删除tableName表中key的数据
	 * 
	 * @param key key
	 * @param tableName 表名
	 */
	boolean delete(String key, String tableName);

	/**
	 * 批量删除tableName表的多个对象
	 * 
	 * @param key 批量删除对象的key的集合
	 * @param tableName 表名
	 */
	boolean delete(List<String> key, String tableName);

	/**
	 * 更新一个tableName表里的一个对象，key不存在，则新增数据
	 * 
	 * @param key key
	 * @param obj 对象
	 * @param tableName 表名
	 * @param expirationTime 存储时间，单位s，到期将删除该条记录，传null不设时间
	 * @return 返回更新结果
	 */
	boolean update(String key, Object obj, String tableName, Long expirationTime);

	/**
	 * 批量更新tableName表格里的数据，key不存在，则新增数据
	 * 
	 * @param objects 需要更新的数据集合
	 * @param tableName 表名
	 * @param expirationTime 存储时间，单位s，到期将删除该条记录，传null不设时间
	 * @return 返回更新结果
	 */
	boolean update(Map<String, Object> objects, String tableName, Long expirationTime);

	/**
	 * 从tableName根据key的值去除value
	 * 
	 * @param key key
	 * @param tableName 表名
	 * @return 返回key的value对象
	 */
	Object get(String key, String tableName);

	/**
	 * 查询tableName表中的数据，该方法提供给管理员查询和维护数据使用，
	 * 此方法调用Redis模糊查询，系统资源开销高，业务中不建议查询Redis中的表数据
	 * 
	 * @param tableName 表名
	 * @return 返回该表的所有数据
	 */
	Map<String, Object> getTableData(String tableName);

}
