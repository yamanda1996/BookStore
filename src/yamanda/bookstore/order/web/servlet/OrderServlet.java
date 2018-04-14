package yamanda.bookstore.order.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import yamanda.bookstore.cart.domain.Cart;
import yamanda.bookstore.cart.domain.CartItem;
import yamanda.bookstore.order.domain.Order;
import yamanda.bookstore.order.domain.OrderItem;
import yamanda.bookstore.order.service.OrderException;
import yamanda.bookstore.order.service.OrderService;
import yamanda.bookstore.user.domain.User;
import yamanda.bookstore.user.service.UserException;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	//添加订单，
	//利用session中的cart生成order对象
	public String addOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 1.从session中得到cart
		 * 2.使用cart创建order对象
		 * 3.调用service中的方法完成添加订单
		 * 4.保存order到request域中，转发到/jsps/order/desc.jsp中
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		//把cart转换成order对象
		Order order = new Order();
		double total = cart.getTotal();
		order.setOid(CommonUtils.uuid());  //给oid设置为uuid
		order.setOrdertime(new Date());  //设置订单时间为当前时间
		order.setTotal(total);  //设置订单合计
		order.setState(1);  //设置订单状态为1，表示未付款
		order.setAddress("");
		order.setOwner((User)(request.getSession().getAttribute("session_user")));  //设置订单所有者,从session中得到
		/**
		 * 设置订单中的订单条目集合
		 * cartItemList-->orderItemList
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		Collection<CartItem> cartItems = cart.getCartItems();
		//循环遍历cartItems，使用每一个cartItem对象创建一个orderItem对象
		for (CartItem cartItem : cartItems) {
			OrderItem oi = new OrderItem();  //创建订单条目
			//为新创建的每一个orderItem赋值
			oi.setIid(CommonUtils.uuid());  //设置条目的id
			oi.setOrder(order);
			oi.setSubtotal(cartItem.getSubtotal());
			oi.setCount(cartItem.getCount());
			oi.setBook(cartItem.getBook());
			orderItemList.add(oi);  //将订单条目添加到条目列表中
			
		}
		
		order.setOrderItemList(orderItemList);  //把所有订单条目添加到订单中
		//清空购物车
		cart.clear();
		//调用orderService添加订单
		orderService.addOrder(order);
		/**
		 * 保存order到request域中
		 */
		request.setAttribute("order", order);
		
		
		return "f:/jsps/order/desc.jsp";
	}
	//显示我的订单
	
	public String myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		//得到用户对象
		User user = (User) request.getSession().getAttribute("session_user");
		String uid = user.getUid();  //得到用户id
		List<Order> orderList = orderService.myOrders(uid);  //通过用户编号uid得到订单列表
		request.setAttribute("orderList", orderList);  //将订单列表保存到request域中
		
		return "/jsps/order/list.jsp";
	}
	/**
	 * 通过oid加载订单
	 * 1.获取oid
	 * 2.调用service中的方法获得order对象
	 * 3.保存到request域中
	 */
	
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String oid = request.getParameter("oid");
		Order order = orderService.load(oid);
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String oid = request.getParameter("oid");  //获取oid
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "恭喜，交易成功！！！");
			return "f:/jsps/order/msg.jsp";
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/jsps/order/msg.jsp";
		}
	}
	//支付
	
	public String pay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 准备13个参数
		 */
		/////////易宝文件中参数，略
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("/merchantInfo.properties"));  //读取配置文件
		String oid = request.getParameter("oid");
		
		
		Order order = orderService.load(oid);
		request.setAttribute("order", order);
		
		
		request.setAttribute("msg", "放心，不会去银行的");
		return "f:/jsps/order/desc.jsp";
	}
}
