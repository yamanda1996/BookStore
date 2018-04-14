package yamanda.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.jdbc.TxQueryRunner;
import yamanda.bookstore.user.domain.User;

public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	//按照用户名查询用户对象
	public User findByUsername(String username){
		
		try {
			String sql = "select * from tb_user where username=?";
			return qr.query(sql, new BeanHandler<User>(User.class),username);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//按邮件查询用户
	public User findByEmail(String email){
		
		try {
			String sql = "select * from tb_user where email=?";
			return qr.query(sql, new BeanHandler<User>(User.class),email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//添加用户
	public void addUser(User user){
		try {
			String sql = "insert into tb_user values(?,?,?,?,?,?)";
			Object[] params = {user.getUid(),user.getUsername(),user.getPassword(),user.getEmail(),user.getCode(),user.isState()};
			qr.update(sql,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//通过激活码查询用户
	public User findByCode(String code){
		
		try {
			String sql = "select * from tb_user where code=?";
			return qr.query(sql, new BeanHandler<User>(User.class),code);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	//设置用户状态，如未激活为false，如果激活了，为true
	public void updateState(String uid,boolean state){
		try {
			String sql = "update tb_user set state=? where uid=?";
			Object[] params = {state,uid};
			 qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
