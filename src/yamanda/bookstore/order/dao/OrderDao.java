package yamanda.bookstore.order.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import yamanda.bookstore.book.dao.BookDao;
import yamanda.bookstore.book.domain.Book;
import yamanda.bookstore.order.domain.Order;
import yamanda.bookstore.order.domain.OrderItem;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	//添加订单
	public void addOrder(Order order){
		try {
			String sql = "insert into orders values(?,?,?,?,?,?)";
			//处理util中的date转换成sql的TimeStamp
			//Timestamp timeStamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = {order.getOid(),new java.sql.Timestamp(order.getOrdertime().getTime()),     //这个日期类型应该是java.sql.TimeStamp类型
					order.getTotal(),order.getState(),order.getOwner().getUid(),
					order.getAddress()};
			qr.update(sql,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//循环遍历订单中的条目，添加订单中的所有条目
	//使用批处理来实现
	public void addOrderItemList(List<OrderItem> orderItemList){
		/**
		 * queryRunner中的batch（String sql,Object[][] params）;  参数是一个二维数组
		 * 二维数组就是多个一维数组
		 * 每一个一维数组都与sql在一起执行一次，多个一维数组就执行多次
		 * 
		 */
		try {
			
				String sql = "insert into orderitem values(?,?,?,?,?)";
				/**
				 * 把orderItemList转换成二维数组
				 * 		把一个orderItem对象转换成一个一维数组
				 * 
				 * 
				 */
				Object[][] params = new Object[orderItemList.size()][];    //第一个方括号里面写一维数组的个数
				//循环遍历orderItemList,为二维数组中的一维数组赋值
				for (int i = 0;i<orderItemList.size();i++) {
					OrderItem orderItem = orderItemList.get(i);
					params[i] = new Object[]{orderItem.getIid(),orderItem.getCount(),  //给第i个一维数组赋值
							orderItem.getSubtotal(),orderItem.getOrder().getOid(),
							orderItem.getBook().getBid()};
				}
				
				qr.batch(sql,params);  //执行批处理
			
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//通过uid查询所有的订单
	public List<Order> findByUid(String uid) {
		/**
		 * 1.通过uid查询出当前用户所有的List<Order>
		 * 2.循环遍历每一个order，为起加载所有orderItem
		 */
		try {
			String sql = "select * from orders where uid=?";
		//List<Order> orderListReturn = new ArrayList<Order>();  //新建一个集合用来存储添加条目之后的order对象
			//得到当前用户的所有订单
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			/**
			 * 循环遍历每一个order，为起加载所有orderItem
			 */
			for (Order order : orderList) {
				String oid = order.getOid();  //得到每一个订单的oid
				//根据订单的oid查询这个订单中所有的订单条目
				/*String sqlForOrderItem = "select * from orderitem where oid=?";
				List<OrderItem> orderItemList = qr.query(sqlForOrderItem, new BeanListHandler<OrderItem>(OrderItem.class),oid);*/
				/*for (OrderItem orderItem : orderItemList) {
					
				}*/
				List<OrderItem> orderItemList = findItemsByOid(oid);  //利用每一个订单的oid来查询出orderItemList
				order.setOrderItemList(orderItemList);  //给每一个order对象赋值，值为通过oid查询出来的结果
				//orderListReturn.add(order);
			}
			
			
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	//通过oid来获得order对象
	public Order findByOid(String oid){
		try {
			String sql = "select * from orders where oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
			List<OrderItem> orderItemList = findItemsByOid(oid);
			order.setOrderItemList(orderItemList);
			
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//通过oid来查询orderitem中的所有该订单下的条目
	public List<OrderItem> findItemsByOid(String oid){
		try {
			//利用多表查询查询数据，这样查询的数据之中就有book和orderItem中的信息了
			String sql = "select * from orderitem i,book b where i.bid=b.bid and oid=?";  
			//一行结果集对应的不是一个javabean，所以不能再用beanListHandler,而使用mapListHandler
			List<Map<String, Object>> orderItemMapList = qr.query(sql, new MapListHandler(),oid);
			List<OrderItem> orderItemList = new ArrayList<OrderItem>();
			//每个map对应一行结果集
			//循环遍历每一个map，最终获得一个OrderItem
			/*for (Map<String, Object> map : orderItemMapList) {
				OrderItem orderItem = new OrderItem();
				orderItem.setIid((String)map.get("iid"));  //设置iid
				orderItem.setCount((int)map.get("count"));  //设置数量
				//orderItem.setSubtotal(new BigDecimal((String)map.get("subtotal")).doubleValue());  
				orderItem.setBook(new BookDao().findByBid((String)map.get("bid")));  //通过bid得到book对象，再导入到orderItem之中  
				orderItemList.add(orderItem);  //将新建的orderItem导入到orderItemList中
				
			}*/
			orderItemList = toOrderItemList(orderItemMapList);
			return orderItemList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 把mapList转换成一个ordetItemList
	 */
	private List<OrderItem> toOrderItemList(List<Map<String,Object>> mapList){
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (Map<String,Object> map : mapList) {
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
	/**
	 * 把一个map转换成一个orderItem
	 */
	private OrderItem toOrderItem(Map<String,Object> map){
		//将一个map转换成两个对象
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);  //建立两个对象之间的联系
		return orderItem;
		
	}
	/**
	 * 通过oid查询订单的状态
	 */
	public int getStateByOid(String oid){
		try {
			String sql = "select * from orders where oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
			//String sql = "select state from orders where oid=?";
			//Number num = (Number)qr.query(sql,new ScalarHandler(),oid);
			//return num.intValue();
			
			
			return order.getState();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过oid修改订单的状态
	 */
	public void updateState(String oid,int state){
		try {
			String sql = "update orders set state=? where oid=?";  //更改数据库中的内容
			Object[] params = {state,oid};
			qr.update(sql,params);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
