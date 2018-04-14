package yamanda.bookstore.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;
import yamanda.bookstore.order.dao.OrderDao;
import yamanda.bookstore.order.domain.Order;
import yamanda.bookstore.user.service.UserException;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	/**
	 * 1.添加订单
	 * 2.处理事务
	 * @param order
	 */
	public void addOrder(Order order){
		try {
			//开启事务
			JdbcUtils.beginTransaction();
			orderDao.addOrder(order);  //添加订单
			orderDao.addOrderItemList(order.getOrderItemList());  //添加订单中的条目
			
			
			//提交事务
			JdbcUtils.commitTransaction();
		} catch (Exception e) {
			//回滚事务
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				
			}
			throw new RuntimeException(e);
		}
	}
	//我的订单
	public List<Order> myOrders(String uid){
		return orderDao.findByUid(uid);
	}
	//通过oid加载订单
	public Order load(String oid){
		return orderDao.findByOid(oid);
	}
	/**
	 * 确认收货
	 * @throws UserException 
	 * @throws OrderException 
	 */
	public void confirm(String oid) throws  OrderException{
		int state = orderDao.getStateByOid(oid);
		if(state != 3){
			throw new OrderException("您还不具备确认付款的能力");
		}
		orderDao.updateState(oid, 4);  //没问题，将状态设置为4，交易成功
	}
}
