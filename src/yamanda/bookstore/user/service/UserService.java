package yamanda.bookstore.user.service;

import yamanda.bookstore.user.dao.UserDao;
import yamanda.bookstore.user.domain.User;

public class UserService {
	private UserDao userDao = new UserDao();
	//注册用户
	public void regist(User form) throws UserException{
		//校验用户名
		User user = userDao.findByUsername(form.getUsername());
		if(user != null){
			throw new UserException("该用户名已被注册");  //抛出异常
		}
		//校验邮箱
		user = userDao.findByEmail(form.getEmail());
		if(user != null){
			throw new UserException("该邮箱已被注册");  //抛出异常
		}
		userDao.addUser(form);
	}
	//激活用户
	public void active(String code) throws UserException{
		User user = userDao.findByCode(code);  //使用code查询数据库，得到user
		if(user == null){  //如果用户不存在，激活码错误
			throw new UserException("激活码错误");
		}
		if(user.isState()){
			throw new UserException("您已经激活过了，不要重复激活");
		}else{
			userDao.updateState(user.getUid(), true);  //用户找到了，并且状态为false，执行激活，并将其状态变成true
		}
		
	}
	//用户登陆
	public User login(User form) throws UserException{
		User user = userDao.findByUsername(form.getUsername());
		if(user == null){
			throw new UserException("用户名不存在");
		}
		if(!user.getPassword().equals(form.getPassword())){
			throw new UserException("密码错误");
		}
		if(!user.isState()){
			throw new UserException("您还未激活");
		}
		return user;
	}
}
