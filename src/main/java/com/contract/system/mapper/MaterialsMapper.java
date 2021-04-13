package com.contract.system.mapper;

import com.contract.system.bean.Materials;

import com.contract.system.bean.entity.MaterialsDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MaterialsMapper {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Materials getById(Integer id);

    /**
     * 根据name查询
     * @param name
     * @return
     */
    public Materials getByName(String name);
    /**
     * 根据name查询
     * @param dto
     * @return
     */
    public  List<Materials> getByName1(@Param("dto")MaterialsDto dto);

    /**
     * 更新
     * @param materials
     */
    public void update(Materials materials);

    /**
     * 查询全部
     */
    public List<Materials> getAll();

    /**
     * 查询全部分页
     */
    public List<Materials> getAllByPage(Integer page);


    /**
     * 新增
     * @param materials
     */
    public void add(Materials materials);

    /**
     * 删除
     * @param id
     *
     */
    public void deleteById(Integer id);

    List<Materials> queryByKeyWord(String keyWord);
}
