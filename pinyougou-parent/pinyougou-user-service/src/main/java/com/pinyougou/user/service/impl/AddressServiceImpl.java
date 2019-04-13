package com.pinyougou.user.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;


/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;

	/**
	* @Date 2019/4/2 10:14
	* 根据用户查询地址
	* @Param [UserId]
	* @return java.util.List<com.pinyougou.pojo.TbAddress>
	**/
    @Override
    public List<TbAddress> findListByUserId(String UserId) {
        TbAddress where = new TbAddress();
        where.setUserId(UserId);
        return addressMapper.select(where);
    }

    /**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbAddress> result = new PageResult<TbAddress>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbAddress> list = addressMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbAddress address) {
		addressMapper.insertSelective(address);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
		addressMapper.updateByPrimaryKeySelective(address);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbAddress findOne(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long id) {


        //跟据查询条件删除数据
		TbAddress where =new TbAddress();
		where.setId(id);
		addressMapper.delete(where);
	}
	
	
	@Override
	public PageResult findPage(TbAddress address, int pageNum, int pageSize) {
		PageResult<TbAddress> result = new PageResult<TbAddress>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(address!=null){			
						//如果字段不为空
			if (address.getUserId()!=null && address.getUserId().length()>0) {
				criteria.andLike("userId", "%" + address.getUserId() + "%");
			}
			//如果字段不为空
			if (address.getProvinceId()!=null && address.getProvinceId().length()>0) {
				criteria.andLike("provinceId", "%" + address.getProvinceId() + "%");
			}
			//如果字段不为空
			if (address.getCityId()!=null && address.getCityId().length()>0) {
				criteria.andLike("cityId", "%" + address.getCityId() + "%");
			}
			//如果字段不为空
			if (address.getTownId()!=null && address.getTownId().length()>0) {
				criteria.andLike("townId", "%" + address.getTownId() + "%");
			}
			//如果字段不为空
			if (address.getMobile()!=null && address.getMobile().length()>0) {
				criteria.andLike("mobile", "%" + address.getMobile() + "%");
			}
			//如果字段不为空
			if (address.getAddress()!=null && address.getAddress().length()>0) {
				criteria.andLike("address", "%" + address.getAddress() + "%");
			}
			//如果字段不为空
			if (address.getContact()!=null && address.getContact().length()>0) {
				criteria.andLike("contact", "%" + address.getContact() + "%");
			}
			//如果字段不为空
			if (address.getIsDefault()!=null && address.getIsDefault().length()>0) {
				criteria.andLike("isDefault", "%" + address.getIsDefault() + "%");
			}
			//如果字段不为空
			if (address.getNotes()!=null && address.getNotes().length()>0) {
				criteria.andLike("notes", "%" + address.getNotes() + "%");
			}
			//如果字段不为空
			if (address.getAlias()!=null && address.getAlias().length()>0) {
				criteria.andLike("alias", "%" + address.getAlias() + "%");
			}
	
		}

        //查询数据
        List<TbAddress> list = addressMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	/**
	* @Date 2019/4/7 11:03
	* 设置默认地址
	* @Param [id]
	* @return void
	**/
    @Override
    public void install(Long id ,String userId) {

		List<TbAddress> addressList = addressMapper.select(null);
		for (TbAddress tbAddress : addressList) {
			if (tbAddress.getUserId().equals(userId)){
				tbAddress.setIsDefault("0");
				addressMapper.updateByPrimaryKey(tbAddress);
				if (tbAddress.getId().equals(id)){
					tbAddress.setIsDefault("1");
					addressMapper.updateByPrimaryKey(tbAddress);
				}
			}
		}
	}
}
