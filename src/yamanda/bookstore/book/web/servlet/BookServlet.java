package yamanda.bookstore.book.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import yamanda.bookstore.book.domain.Book;
import yamanda.bookstore.book.service.BookService;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	//查询所有图书
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");//处理响应编码
		request.setCharacterEncoding("UTF-8");
		List<Book> bookList = bookService.findAll();
		request.setAttribute("bookList", bookList);
		
		return "f:/jsps/book/list.jsp";
	}
	//按分类查询图书
	
	public String findByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");//处理响应编码
		request.setCharacterEncoding("UTF-8");
		String cid = request.getParameter("cid");  //从请求中得到cid参数值
		List<Book> bookList = bookService.findByCategory(cid);
		request.setAttribute("bookList", bookList);
		return "f:/jsps/book/list.jsp";
		
	}
	//加载某一本图书的内容
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");//处理响应编码
		request.setCharacterEncoding("UTF-8");
		String bid = request.getParameter("bid");  //得到图书编号
		Book book = bookService.load(bid);
		request.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
		
	}

}
