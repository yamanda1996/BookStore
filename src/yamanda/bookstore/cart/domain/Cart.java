package yamanda.bookstore.cart.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
	private Map<String,CartItem> map = new LinkedHashMap<String, CartItem>();  //显示的时候需要按顺序显示，所以用linkedHashMap
	//添加条目到车中
	public void add(CartItem cartItem){
		/*map.put(cartItem.getBook().getBid(),cartItem);  //添加键值对，键是book对象的bid，值是条目
		 * 然而这样写是不行滴，因为这样写会因为map的键值必须唯一导致再次添加同一类型的商品时出现合并覆盖的效果
		 * 
*/		
		if(map.containsKey(cartItem.getBook().getBid())){  //判断原来的购物车中是否含有该条目
			CartItem oldCartItem = map.get(cartItem.getBook().getBid());  //找到原来购物车中的老条目
			
			oldCartItem.setCount(oldCartItem.getCount()+cartItem.getCount());  //将该条目中的商品数量和老条目商品数量进行加和
			
			map.put(cartItem.getBook().getBid(), oldCartItem);  //将加完和之后的条目储存在map中
			
		}else{
			map.put(cartItem.getBook().getBid(), cartItem);
			
		}
	}
	//清空购物车
	public void clear(){
		map.clear();;
	}
	//删除条目
	public void delete(String bid){
		map.remove(bid);  
	}
	//显示我的购物车
	public Collection<CartItem> getCartItems(){
		return map.values();  //返回map中的所有值
	}
	//购物车中商品合计
	public double getTotal(){
		//解决二进制计算的误差问题
		BigDecimal bigTotal = new BigDecimal(0 + "");
		for(CartItem temp : map.values()){  //遍历map中的所有值
			BigDecimal bigSubtotal = new BigDecimal(temp.getSubtotal() + "");
			
			bigTotal = bigTotal.add(bigSubtotal);
		}
		return bigTotal.doubleValue();
		
	}
	
	
	
}
