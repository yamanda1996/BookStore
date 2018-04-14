package yamanda.bookstore.user.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;
import yamanda.bookstore.cart.domain.Cart;
import yamanda.bookstore.user.domain.User;
import yamanda.bookstore.user.service.UserException;
import yamanda.bookstore.user.service.UserService;

public class UserServlet extends BaseServlet {  //可以在一个servlet中写多个方法
	private UserService userService = new UserService();
	//用户退出
	
		public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");//处理响应编码
			request.setCharacterEncoding("UTF-8");
			request.getSession().invalidate();  //销毁session
			return "r:/index.jsp";
		}
		//用户登陆
		public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");//处理响应编码
			request.setCharacterEncoding("UTF-8");
			User form = CommonUtils.toBean(request.getParameterMap(), User.class);
			//输入校验，以后再加
			
			try {
				User user = userService.login(form);
				request.getSession().setAttribute("session_user", user);  //将在数据库中查询出来的用户信息保存在session中以便于别的页面上使用
				/**
				 * 给用户添加一辆购物车，即向session中保存一个cart的对象
				 * 
				 */
				Cart cart = new Cart();
				request.getSession().setAttribute("cart", cart);
				return "r:/index.jsp";  //重定向
				//response.sendRedirect(request.getContextPath());  //重定向到index.jsp中
			} catch (UserException e) {
				request.setAttribute("msg", e.getMessage());
				request.setAttribute("form", form);
				return "f:/jsps/user/login.jsp";
			}
			
			
		}
		//激活功能
		public String active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");//处理响应编码
			request.setCharacterEncoding("UTF-8");
//			response.getWriter().print("<h1>您已经激活了！！！</h1>");
//			System.out.println("您已经激活了");
			
			try {
				String code = request.getParameter("code");  //获取激活码
				userService.active(code);
				request.setAttribute("msg", "<h1>激活成功,请登陆</h1>");
				return "f:/jsps/msg.jsp";
				
			} catch (UserException e) {
				request.setAttribute("msg", e.getMessage());  //将异常信息保存在request中
				System.out.println(e.getMessage());
				return "f:/jsps/msg.jsp";
			}
			
			
		}
	
	/**
	 * 注册功能	
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");//处理响应编码
			request.setCharacterEncoding("UTF-8");
			
			/**
			 * 1.封装表单数据到form对象中
			 * 2.补全信息，uid和code
			 * 3.输入校验
			 *     保存错误信息、form到request中，转发到regist.jsp中
			 * 4.调用service方法完成注册
			 * 		保存错误信息、form到request中，转发到regist.jsp中
			 * 5.发邮件
			 * 6.保存成功信息转发到msg.jsp中
			 */
			//一键封装表单数据
			User form = CommonUtils.toBean(request.getParameterMap(), User.class);
			//补全信息
			form.setUid(CommonUtils.uuid());
			form.setCode(CommonUtils.uuid()+CommonUtils.uuid());  //64位激活码
			/**
			 * 输入校验
			 * 1.创建map封装错误信息，key为表单字段名称，值为错误信息
			 * 2.
			 */
			Map<String,String> errors = new HashMap<String, String>();
			//对username、password、Email进行校验
			String username = form.getUsername();
			if(username == null || username.trim().isEmpty()){
				errors.put("username", "用户名不能为空");
			}else if(username.length()<3 || username.length()>20){
				errors.put("username", "用户名长度过长或过短");
			}
			String password = form.getPassword();
			if(password == null || password.trim().isEmpty()){
				errors.put("password", "密码不能为空");
			}else if(password.length()<3 || password.length()>20){
				errors.put("password", "密码长度过长或过短");
			}
			String email = form.getEmail();
			if(email == null || email.trim().isEmpty()){
				errors.put("email", "邮箱不能为空");
			}else if(!email.matches("\\w+@\\w+\\.\\w+")){  //利用正则表达式来判断邮箱格式是否正确
				errors.put("email", "邮箱格式错误");
			}
			
			/**
			 * 判断是否存在错误信息
			 */
			if(errors.size() > 0){
				request.setAttribute("errors", errors);  //保存错误信息
				request.setAttribute("form", form);  //保存表单数据
				return "f:/jsps/user/regist.jsp";
			}
			
			
			//调用service的regist方法
			try {
				
				userService.regist(form);
				
				
			} catch (UserException e) {
				/**
				 * 出了异常，说明用户名重名或者邮箱重复了
				 1.保存异常信息到request中
				 2.保存表单数据到request中
				 3.转发到regist页面中
				 */
				request.setAttribute("msg", e.getMessage());
				request.setAttribute("form", form);
				return "f:/jsps/user/regist.jsp";
			}
			
			
			/**
			 * 发邮件
			 *1.准备配置文件 
			 * 
			 */
			//获取配置文件内容
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("email.properties"));  //加载配置文件
			String host = props.getProperty("host");  //获取服务器主机
			String uname = props.getProperty("uname");  //获取用户名
			String pwd = props.getProperty("pwd");  //获取密码
			String from = props.getProperty("from");  //获取发件人
			String to = form.getEmail();
			String subject = props.getProperty("subject");  //获取主题
			String content = props.getProperty("content");  //获取邮件内容
			content = MessageFormat.format(content, form.getCode());  //将content中的占位符{0}替换为真实的激活码
			
			Session session = MailUtils.createSession(host, uname, pwd);  //得到session
			Mail mail = new Mail(from, to, subject, content);  //创建邮件对象
			try {
				MailUtils.send(session, mail);  //发邮件
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}  
			  
			
			
			
			//userService执行成功，没有抛出异常
			//保存成功信息，转发到msg.jsp中
			request.setAttribute("msg", "恭喜，注册成功,请到邮箱激活");
			return "f:/jsps/msg.jsp";
			
			
			
		}
	
	

}
