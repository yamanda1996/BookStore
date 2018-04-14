package yamanda.bookstore.book.service;

import java.util.List;

import yamanda.bookstore.book.dao.BookDao;
import yamanda.bookstore.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();
	//查询所有图书
	public List<Book> findAll(){
		return bookDao.findAll();
	}
	//按分类查询图书
	public List<Book> findByCategory(String cid){
		return bookDao.findByCategory(cid);
	}
	//加载某一本图书的内容
	public Book load(String bid){
		return bookDao.findByBid(bid);
	}
}
