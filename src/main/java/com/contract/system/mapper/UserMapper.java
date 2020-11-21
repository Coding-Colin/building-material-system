package com.contract.system.mapper;


import com.contract.system.bean.User;
import com.contract.system.bean.entity.UserDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {


    /**
     * 查询全部
     * @return
     */
    public List<User> getAll();

    /**
     * 查询全部分页
     * @return
     */
    public List<User> getAllByPage(Integer page);


    /**
     * 新增
     * @param user
     */
    public void add(User user);


    /**
     * 根据id查询
     * @param id
     * @return
     */
    public User getById(Integer id);

    /**
     * 根据name查询
     * @param name
     * @return
     */
    public List<User> getByName(String name);


    /**
     * 更新
     */
    public void update(User user);


    /**
     * 删除
     * @param id
     */
    public void deleteById(Integer id);


}
