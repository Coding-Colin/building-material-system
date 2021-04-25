package com.contract.system.controller;
import com.contract.system.bean.*;
import com.contract.system.bean.entity.ContractDto;
import com.contract.system.bean.entity.MaterialsDto;
import com.contract.system.bean.entity.PersonDto;
import com.contract.system.bean.entity.UserDto;
import com.contract.system.bean.query.ContractQueryForm;
import com.contract.system.mapper.*;
import com.contract.system.util.DateUtil;
import com.contract.system.util.FileUtil;
import com.contract.system.util.JsonUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Strings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;



@Controller
public class AdminController {

    @Autowired
    public ContractMapper contractMapper;

    @Autowired
    public MaterialsMapper materialsMapper;

    @Autowired
    public PersonMapper personMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public LogsMapper logsMapper;

    public static String endDate = "";
    public static String signDate = "";


    /**
     * 用户管理页面
     * @return
     */
    @RequestMapping("/userHtml.do")
    public String userHtml(){
        return "admin/userList";
    }

    /**
     * 初始化用户页面
     * @return
     */
    @ResponseBody
    @RequestMapping("/aonloaduser")
    public String aonloaduser(){
        Map<String,Object> map = new HashMap<String, Object>();
        List<User> list = userMapper.getAllByPage(0);
        map.put("logs",list);
        map.put("num",userMapper.getAll().size());
        return JsonUtil.toJson(map);
    }


    /**
     * 查询用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectuserByname.do")
    public String selectuserByname(@RequestBody String array[]){
        String param = "";
        if (array.length != 0){
            param = array[0];
        }
        List<User> list =  userMapper.getByName(param) ;
        return JsonUtil.toJson(list);
    }


    /**
     * 查询用户分页
     * @return
     */
    @ResponseBody
    @RequestMapping("/aselectUser")
    public String aselectUser(@RequestBody String array[]){
        List<User> list = userMapper.getAllByPage(Integer.parseInt(array[0])*5);
        return JsonUtil.toJson(list);
    }

    /**
     * 添加用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/adduser.do")
    public String addUser(@RequestBody String array[]){
        List<User> user1 = userMapper.getByName(array[0]);//判断该用户名是否已经注册过
        if (user1.size()>0){
            return JsonUtil.toJson("用户已存在请重试");//如果已经注册过直接返回错误
        }
        else {
            User user = new User();
            user.setUsername(array[0]);
            user.setPassword(array[1]);
            user.setPos(Integer.parseInt(array[2]));
            user.setName(array[3]);
            user.setAge(Integer.parseInt(array[4]));
            user.setTel(array[5]);
            user.setIdcard(array[6]);
            user.setAddress(array[7]);
            user.setSex(Integer.parseInt(array[8]));
            userMapper.add(user);
            return JsonUtil.toJson("新增成功");
        }
    }

    /**
     * 查询用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectuser.do")
    public String getUser(@RequestBody String array[]){
        User user = userMapper.getById(Integer.parseInt(array[0]));
        return JsonUtil.toJson(user);
    }

    /**
     * 更新用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateuser.do")
    public String updateuser(@RequestBody String array[]){
        User user = userMapper.getById(Integer.parseInt(array[0]));
        user.setUsername(array[1]);
        user.setPassword(array[2]);
        user.setPos(Integer.parseInt(array[3]));
        user.setName(array[4]);
        user.setAge(Integer.parseInt(array[5]));
        user.setTel(array[6]);
        user.setIdcard(array[7]);
        user.setAddress(array[8]);
        user.setSex(Integer.parseInt(array[9]));
        userMapper.update(user);
        return JsonUtil.toJson("更新成功");
    }




    /**
     * 删除用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteuser.do")
    public String deleteUser(@RequestBody String array[]){
        userMapper.deleteById(Integer.parseInt(array[0]));
        return JsonUtil.toJson("删除成功");
    }


    /**
     * 客户管理
     * @return
     */
    @RequestMapping("/personHtml.do")
    public String personHtml(){
        return "admin/personList";
    }


    /**
     * 添加客户
     * @return
     */
    @ResponseBody
    @RequestMapping("/addperson.do")
    public String addperson(@RequestBody String array[]){
        Person person ;
        person = new Person();
        person.setName(array[0]);
        person.setAge(Integer.parseInt(array[1]));
        person.setTel(array[2]);
        person.setIdcard(array[3]);
        person.setAddress(array[4]);
        person.setSex(Integer.parseInt(array[5]));
        personMapper.add(person);
        return JsonUtil.toJson("新增成功");
    }


    /**
     * 查询客户初始化
     * @return
     */
    @ResponseBody
    @RequestMapping("/aonloadperson.do")
    public String aonloadperson(){
        Map<String,Object> map = new HashMap<String, Object>();
        List<Person> list = personMapper.getAll();
        List<Person> list1 = personMapper.getAllByPage(0);
        map.put("logs",list1);
        map.put("num",list.size());
        return JsonUtil.toJson(map);
    }

    /**
     * 查询用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectpersonByname.do")
    public String selectpersonByname(@RequestBody String array[]){
        String param = "";
        if (array.length != 0){
            param = array[0];
        }
        List<Person> list =  personMapper.getByName(param) ;
        return JsonUtil.toJson(list);
    }


    /**
     * 查询客户分页
     * @return
     */
    @ResponseBody
    @RequestMapping("/aselectperson.do")
    public String aselectperson(@RequestBody String array[]){
        List<Person> list1 = personMapper.getAllByPage(Integer.parseInt(array[0])*5);
        return JsonUtil.toJson(list1);
    }



    /**
     * 查询客户单个
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectperson.do")
    public String selectperson(@RequestBody String array[]){
        Person person = personMapper.getById(Integer.parseInt(array[0]));
        return JsonUtil.toJson(person);
    }

    /**
     * 更新客户
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateperson.do")
    public String updateperson(@RequestBody String array[]){
        Person person = personMapper.getById(Integer.parseInt(array[0]));
        person.setName(array[1]);
        person.setAge(Integer.parseInt(array[2]));
        person.setSex(Integer.parseInt(array[3]));
        person.setTel(array[4]);
        person.setIdcard(array[5]);
        person.setAddress(array[6]);
        personMapper.update(person);
        return JsonUtil.toJson("更新成功");
    }

    /**
     * 删除客户
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteperson.do")
    public String deleteperson(@RequestBody String array[]){
       personMapper.deleteById(Integer.parseInt(array[0]));
        return JsonUtil.toJson("删除成功");
    }


    /**
     * 合同管理
     * @return
     */
    @RequestMapping("/contractHtml.do")
    public String contractHtml(){
        return "admin/contractList";
    }


    /**
     * 合同首页初始化查询所有信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/admincontract.do")
    public String admincontract(){
        Map<String,Object> map = new HashMap<String, Object>();
        List<Contract> list = contractMapper.getAll(0);//查询第一页合同
        map.put("logs",list);
        int num = contractMapper.getAllNoPage().size();
        map.put("size",num);//查询数量用作分页的页数
        return JsonUtil.toJson(map);
    }
    /**
     * 条件查询合同
     * @return
     */
    @ResponseBody
    @RequestMapping("/getContract.do")
    public String getContract(@RequestBody ContractQueryForm form){
        Integer page = form.getPage();
        form.setPage(page < 1 ? 0 : (page-1) * 5);
        List<Contract> list  = contractMapper.getContract(form);
        return JsonUtil.toJson(list);
    }


    /**
     * 分页查询合同
     * @return
     */
    @ResponseBody
    @RequestMapping("/contractByPage.do")
    public String contract(@RequestBody String array[]){
        List<Contract> list = contractMapper.getAll(Integer.parseInt(array[0])*5);
        return JsonUtil.toJson(list);
    }

    /**
     * 更新合同状态
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatestatus.do")
    public String updatestatus(@RequestBody String array[]){
        Contract contract = contractMapper.getById(Integer.parseInt(array[0]));
        contract.setStatus(array[1]);
        contractMapper.update(contract);
        return JsonUtil.toJson("更新成功");
    }



    /**
     * 建材管理
     * @return
     */
    @RequestMapping("/materialsHtml.do")
    public String materialsHtml(){
        return "admin/materialsList";
    }


    /**
     * 建材管理初始化
     * @return
     */
    @ResponseBody
    @RequestMapping("/aonloadmaterials.do")
    public String aonloadmaterials(){
        Map map = new HashMap();
        List<Materials> list = materialsMapper.getAllByPage(0);
        map.put("logs",list);
        map.put("num",materialsMapper.getAll().size());
        return JsonUtil.toJson(map);
    }

    /**
     * 分页查询建材
     * @return
     */
    @ResponseBody
    @RequestMapping("/materialsByPage.do")
    public String materialsByPage(@RequestBody String array[]){
        List<Materials> list = materialsMapper.getAllByPage(Integer.parseInt(array[0])*5);
        return JsonUtil.toJson(list);
    }


    /**
     * 添加建材
     * @return
     */
    @ResponseBody
    @RequestMapping("/addmaterials.do")
    public String addmaterials(@RequestBody String array[]){
        Materials materials = materialsMapper.getByName(array[0]);
        if (materials!=null){
            return JsonUtil.toJson("建材已存在请重试");
        }
        materials = new Materials();
        materials.setName(array[0]);
        materials.setNum(Integer.parseInt(array[1]));
        materials.setPrice(Integer.parseInt(array[2]));
        materials.setValue(Integer.parseInt(array[1])*Integer.parseInt(array[2]));
        materialsMapper.add(materials);
        return JsonUtil.toJson("新增成功");
    }

    /**
     * 查询建材
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectmaterials.do")
    public String selectmaterials(@RequestBody String array[]){
        Materials materials = materialsMapper.getById(Integer.parseInt(array[0]));
        return JsonUtil.toJson(materials);
    }

    /**
     * 更新建材
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatematerials.do")
    public String updatematerials(@RequestBody String array[]){
        Materials materials = materialsMapper.getById(Integer.parseInt(array[0]));
        materials.setName(array[1]);
        materials.setNum(Integer.parseInt(array[2]));
        materials.setPrice(Integer.parseInt(array[3]));
        materials.setValue(Integer.parseInt(array[2])*Integer.parseInt(array[3]));
        materialsMapper.update(materials);
        return JsonUtil.toJson("更新成功");
    }

    /**
     * 删除建材
     * @return
     */
    @ResponseBody
    @RequestMapping("/deletematerials.do")
    public String deletematerials(@RequestBody String array[]){
        materialsMapper.deleteById(Integer.parseInt(array[0]));
        return JsonUtil.toJson("删除成功");
    }
    /**
     * 查询建材名字
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/selectmaterialsByname.do",method = RequestMethod.POST)
    public String selectmaterialsByname(@RequestBody MaterialsDto dto){
        if(dto.getPageNum()!=null&& dto.getPageSize()!=null){
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        }
        List<Materials>  materials = materialsMapper.getByName1(dto);
        PageInfo<Materials> pageInfo = new PageInfo<>(materials);
        return JsonUtil.toJson(pageInfo);
    }

    /**
     * 签单报表
     * @return
     */
    @RequestMapping("/reportHtml.do")
    public String reportHtml(){
        return "admin/report";
    }

    /**
     * 查询报表
     * @return
     */
    @ResponseBody
    @RequestMapping("/callreport.do")
    public String getDetails(){
        List<Contract> list = contractMapper.getAllNoPage();
        Map<String,Integer> map = new HashMap<String, Integer>();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
        for (int i=0;i<list.size();i++){
            if (null == list.get(i).signDate){
                continue;
            }
            String timeFormat = sdf.format(list.get(i).signDate);
            if (!map.containsKey(timeFormat)){
                map.put(timeFormat,list.get(i).accounts);
            }
            else {
                map.put(timeFormat,map.get(timeFormat)+(int)(list.get(i).accounts));
            }
        }
        Object dd[] = map.keySet().toArray();
        Integer pp[] = new Integer[map.size()];
        for(int i=0;i<map.size();i++){
            pp[i] = map.get(dd[i]);
        }
        for (int i=0;i<pp.length;i++){
            String heng = String.valueOf(dd[i]);
            int aa = 0;//总金额
            for (int j=0;j<list.size();j++){
                if (list.get(j).signDate == null){
                    continue;
                }
                String timeFormat = sdf.format(list.get(j).signDate);
                if (heng.equals(timeFormat)){
                    aa += list.get(j).accounts;//总金额
                }
            }
            pp[i] = aa;
        }
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("dd",dd);
        result.put("pp",pp);
        result.put("ff",0);
        result.put("ww",0);
        return JsonUtil.toJson(result);
    }

    //新增内容
    @RequestMapping("logsHtml.do")
    public String logsHtml(Model model){
        List<Logs> logs = logsMapper.getAll();
        model.addAttribute("logs",logs);
        return "admin/logsList";
    }

    @RequestMapping("statusHtml.do")
    public String statusHtml(){
        return "admin/statusReport";
    }

    /**
     * 状态报表
     * @return
     */
    @ResponseBody
    @RequestMapping("/callStatusReport.do")
    public String callStatusReport(){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String,Object> m1 = new HashMap<>();
        m1.put("name","合同总量");
        m1.put("value",contractMapper.queryCountByStatus(null));
        Map<String,Object> m2 = new HashMap<>();
        m2.put("name","审核成功");
        m2.put("value",contractMapper.queryCountByStatus(Arrays.asList("合同已生效","已付定金","已付尾款")));
        Map<String,Object> m3 = new HashMap<>();
        m3.put("name","审核失败");
        m3.put("value",contractMapper.queryCountByStatus(Arrays.asList("已返还材料")));
        result.add(m1);
        result.add(m2);
        result.add(m3);
        return JsonUtil.toJson(result);
    }

}

