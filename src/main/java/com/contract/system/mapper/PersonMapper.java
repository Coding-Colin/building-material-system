package com.contract.system.mapper;

import com.contract.system.bean.Person;
import com.contract.system.bean.entity.PersonDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonMapper {

    /**
     * 查询全部
     * @return
     */
    public List<Person> getAll();

    /**
     * 查询全部分页
     * @return
     */
    public List<Person> getAllByPage(Integer page);

    /**
     * 根据name查询
     * @return
     */
    public List<Person> getByName(String name);


    /**
     * 新增
     * @param person
     */
    public void add(Person person);


    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Person getById(Integer id);



    /**
     * 更新
     */
    public void update(Person person);


    /**
     * 删除
     * @param id
     */
    public void deleteById(Integer id);

}
