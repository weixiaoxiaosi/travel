package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbContent> result = new PageResult<TbContent>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbContent> list = contentMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbContent> info = new PageInfo<TbContent>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insertSelective(content);
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的广告类型ID
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//先删除当前修改广告类型的缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.updateByPrimaryKeySelective(content);
		//如果用户修改了广告类型
		if (categoryId.longValue()!=content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(categoryId);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

		//必需在删除之前，查询出来，清除缓存
		List<TbContent> select = contentMapper.selectByExample(example);
		for (TbContent content : select) {
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		//跟据查询条件删除数据
        contentMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageResult<TbContent> result = new PageResult<TbContent>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						//如果字段不为空
			if (content.getTitle()!=null && content.getTitle().length()>0) {
				criteria.andLike("title", "%" + content.getTitle() + "%");
			}
			//如果字段不为空
			if (content.getUrl()!=null && content.getUrl().length()>0) {
				criteria.andLike("url", "%" + content.getUrl() + "%");
			}
			//如果字段不为空
			if (content.getPic()!=null && content.getPic().length()>0) {
				criteria.andLike("pic", "%" + content.getPic() + "%");
			}
			//如果字段不为空
			if (content.getStatus()!=null && content.getStatus().length()>0) {
				criteria.andLike("status", "%" + content.getStatus() + "%");
			}
	
		}

        //查询数据
        List<TbContent> list = contentMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbContent> info = new PageInfo<TbContent>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	/**
	* @Date 21:20 2019/3/20
	* 跟据内容类目ID查询内容信息列表
	* @Param [categoryId]
	* @return java.util.List<com.pinyougou.pojo.TbContent>
	**/
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//先从缓存中查询广告列表
		List<TbContent> content = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if (content==null){
			//设置查询条件
			Example example = new Example(TbContent.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("categoryId",categoryId );

			//查询正常状态的数据
			criteria.andEqualTo("status","1" );
			//设置排序,多个字段可逗号分隔
			example.setOrderByClause("sortOrder asc");
			content = contentMapper.selectByExample(example);

			//把数据放入缓存
			redisTemplate.boundHashOps("content").put(categoryId, content);
			return content;
		}else {
			//方便测试
			System.out.println("从缓存中读取数据");
		}
		return content;
	}

}
