package yamanda.bookstore.category.service;

import java.util.List;

import yamanda.bookstore.category.dao.CategoryDao;
import yamanda.bookstore.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	//查询所有分类
	public List<Category> findAll(){
		return categoryDao.findAll();
	}
}
