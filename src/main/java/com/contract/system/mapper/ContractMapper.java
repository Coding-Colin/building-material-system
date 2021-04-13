package com.contract.system.mapper;

import com.contract.system.bean.Contract;
import com.contract.system.bean.entity.ContractDto;
import com.contract.system.bean.query.ContractQueryForm;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ContractMapper {

    /**
     * 查询全部
     * @return
     */
    public List<Contract> getAll(@Param("page")Integer page);

    /**
     * 合同条件查询
     * @return
     */
    public List<Contract> getContract(ContractQueryForm form);

    /**
     * 根据登录用户查询（员工端）
     * @return
     */
    public List<Contract> getByAuthor(@Param("author") String author, @Param("queryword")String queryword, @Param("page") Integer page);

    /**
     * 根据登录用户查询
     * @return
     */
    public List<Contract> getAll1(String author);
    /**
     * 根据登录用户查询合同数量
     * @return
     */
    public int getNums(String author);


    /**
     * 查询所有的数量
     * @return
     */
    public List<Contract>  getAllNoPage();


    /**
     * 新增
     * @param contract
     */
    public void add(Contract contract);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Contract getById(Integer id);

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    public List<Contract> getByUUID(String uuid);


    /**
     * 更新
     */
    public void update(Contract contract);
    /**
     * 更新状态
     */
    public void  updateAudit(Contract contract);

    /**
     * 删除
     * @param id
     */
    public void deleteById(Integer id);


}
