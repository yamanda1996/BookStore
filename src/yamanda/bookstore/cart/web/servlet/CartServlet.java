package yamanda.bookstore.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import yamanda.bookstore.book.dao.BookDao;
import yamanda.bookstore.book.domain.Book;
import yamanda.bookstore.book.service.BookService;
import yamanda.bookstore.cart.domain.Cart;
import yamanda.bookstore.cart.domain.CartItem;

public class CartServlet extends BaseServlet {
	
	//向购物车中添加条目
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 1.获取车
		 * 2.得到条目（得到商品和数量）
		 * 
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");  //从session中得到车
		String bid = request.getParameter("bid");  //得到商品编号
		int count = Integer.parseInt(request.getParameter("count"));  //得到商品的数量
		
		Book book = new BookService().load(bid);
		CartItem cartItem = new CartItem();  //每一次调用add方法必须重新新建一个条目对象
		cartItem.setBook(book);
		cartItem.setCount(count);
		
		//把条目添加到车中
		cart.add(cartItem);
	
		return "f:/jsps/cart/list.jsp";
	}
	//清空购物车
	public String clear(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 1.先得到车
		 * 2.清空购物车
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	//删除条目
	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 1.得到车
		 * 2.得到要删除的bid
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		String bid = request.getParameter("bid");
		cart.delete(bid);
		
		return "f:/jsps/cart/list.jsp";
	}
	
}
