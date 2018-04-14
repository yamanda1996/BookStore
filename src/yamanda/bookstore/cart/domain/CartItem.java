package yamanda.bookstore.cart.domain;

import java.math.BigDecimal;

import yamanda.bookstore.book.domain.Book;
/**
 * 购物车条目类
 * @author ymdhi
 *
 */
public class CartItem {
	private Book book;
	private int count;
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "CartItem [book=" + book + ", count=" + count + "]";
	}
	public CartItem(Book book, int count) {
		super();
		this.book = book;
		this.count = count;
	}
	public CartItem() {
		super();
	}
	//解决二进制运算误差问题
	public double getSubtotal(){  //小计，没有对应的成员变量
		BigDecimal bigPrice = new BigDecimal(book.getPrice()+"");  //强制转换成字符串 = = 
		BigDecimal bigCount = new BigDecimal(count + "");  //同理强制转换成字符串
		return bigPrice.multiply(bigCount).doubleValue();
	}
}
