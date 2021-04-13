package com.contract.system.controller;
import com.contract.system.bean.Contract;
import com.contract.system.bean.Materials;
import com.contract.system.bean.User;
import com.contract.system.mapper.ContractMapper;
import com.contract.system.mapper.MaterialsMapper;
import com.contract.system.mapper.PersonMapper;
import com.contract.system.mapper.UserMapper;
import com.contract.system.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ClientController {

    @Autowired
    public ContractMapper contractMapper;

    @Autowired
    public MaterialsMapper materialsMapper;

    @Autowired
    public PersonMapper personMapper;

    @Autowired
    public UserMapper userMapper;

    public static String REAL_PATH = "src/main/webapp/resource/files/";
    public static String LOGINUSER = "";
    public static String AUTHER_NAME = "";
    public static final  String DOWNLOADPATH = "E:\\building-material-system\\";
    /**
     * 首页
     * @return
     */
    @RequestMapping("/cindexHtml.do")
    public String index(HttpSession session){
        List<User> users = userMapper.getByName(String.valueOf(session.getAttribute("loginUser")));
        LOGINUSER = String.valueOf(session.getAttribute("loginUser"));
        AUTHER_NAME = users.get(0).name;
        return "client/index";
    }


    /**
     * 个人信息首页
     * @return
     */
    @RequestMapping("/personalhtml.do")
    public String personalhtml(){
        return "client/personal";
    }

    /**
     * 查询当前登陆用户的信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/cselectperson.do")
    public String selectperson(){
        List<User> users = userMapper.getByName(LOGINUSER);
        return JsonUtil.toJson(users.get(0));
    }


    /**
     * 修改员工资料
     */
    @ResponseBody
    @RequestMapping("/cupdateperson.do")
    public String updateUser(@RequestBody String[] array){
        List<User> users = userMapper.getByName(LOGINUSER);
        if(users.size()>0){
            User user = users.get(0);
            user.setName(array[0]);
            user.setAge(Integer.parseInt(array[1]));
            user.setTel(array[2]);
            user.setIdcard(array[3]);
            user.setAddress(array[4]);
            user.setSex(Integer.parseInt(array[5]));
            user.setPassword(array[6]);
            userMapper.update(user);
            return JsonUtil.toJson("修改成功");
        }else {
            return JsonUtil.toJson("修改失败");
        }
    }


    /**
     * 合同首页
     * @return
     */
    @RequestMapping("/ccontractHtml.do")
    public String contractHtml(){
        return "client/contract";
    }


    /**
     * 合同首页初始化查询所有信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/contractOnload.do")
    public String contractOnload(){

        Map<String,Object> map = new HashMap<String, Object>();
        List<Contract> list = contractMapper.getByAuthor(AUTHER_NAME,"",0);//查询第一页合同
        map.put("logs",list);
        int num = contractMapper.getNums(AUTHER_NAME);
        map.put("size",num);//查询数量用作分页的页数
        List<Materials> materials = materialsMapper.getAll();
        map.put("materials",materials);
        map.put("infos", DateUtil.dateCompare(list));
        return JsonUtil.toJson(map);
    }

    /**
     * 分页查询合同
     * @return
     */
    @ResponseBody
    @RequestMapping("/contract.do")
    public String contract(@RequestBody String array[]){
        List<Contract> list = contractMapper.getByAuthor(AUTHER_NAME,array[1], Integer.parseInt(array[0])*5);
        return JsonUtil.toJson(list);
    }


    /**
     * 新增合同
     */
    @Transactional
    @ResponseBody
    @RequestMapping("/addcontract.do")
    public String addcontract(HttpServletRequest request){
        // 转型为MultipartHttpRequest：
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获得文件：
        List<MultipartFile> files = multipartRequest.getFiles("contractfile");
        String title = request.getParameter("title");
        String clientele = request.getParameter("clientele");
        String materials = request.getParameter("materials");
        String num = request.getParameter("num");
        String endDate = request.getParameter("endDate");
        if (files.isEmpty()) {
            return "false";
        }for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (file.isEmpty()) {
                return "false";
            } else {
                String path = this.getClass().getResource("").getPath().split("target/")[0] + REAL_PATH;
                File dest = new File(path + fileName);
                if (!dest.getParentFile().exists()) { // 判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    Contract contract = new Contract();
                    contract.setTitle(title);
                    contract.setClientele(clientele);
                    //解析材料和数量
                    String mtype[] = materials.split(",");
                    String mnum[] = num.split(",");
                    int accounts = 0;
                    for (int i=0;i<mtype.length;i++){
                        Materials materials1 = materialsMapper.getByName(mtype[i]);
                        //更新建材
                        int remain = materials1.num-Integer.parseInt(mnum[i]);
                        if (remain<0){
                            return JsonUtil.toJson(EncodingTool.encodeStr(materials1.name+"材料不足,请补充库存在重试"));
                        }
                        accounts += Integer.parseInt(mnum[i])*materials1.price;//计算总价值
                        materials1.setNum(remain);
                        materials1.setValue(materials1.price*remain);
                        materialsMapper.update(materials1);//更新库存,这里是事务原子操作，不用担心某一种材料库存不足造成的更新脏数据
                    }
                    contract.setContent(materials);
                    contract.setNum(num);
                    contract.setAccounts(accounts);
                    contract.setEndDate(StringToDateUtil.StringToDate(endDate));
                    contract.setUuid(UUIDUtil.makeUUID());
                    contract.setUrl(REAL_PATH + fileName);
                    contract.setAuthor(AUTHER_NAME);
                    contract.setMaterials(materials);
                    file.transferTo(dest);
                    contractMapper.add(contract);
                    return JsonUtil.toJson("success");
                } catch (Exception e) {
                    e.printStackTrace();
                    return JsonUtil.toJson("false");
                }
            }
        }
        return JsonUtil.toJson("false");
    }


    /**
     * 查询合同
     */
    @ResponseBody
    @RequestMapping("/selectcontract.do")
    public String getContract(@RequestBody String array[]){
        Contract contract = contractMapper.getById(Integer.parseInt(array[0]));
        String materials[] = contract.content.split(",");
        List<String> mlist = new ArrayList<String>();
        for (String m:materials){
            mlist.add(m);
        }
        String num[] = contract.num.split(",");
        List<String> nlist = new ArrayList<String>();
        for (String m:num){
            nlist.add(m);
        }
        Map map = new HashMap();
        map.put("materialsData",materialsMapper.getAll());
        map.put("contracts",contract);
        map.put("materials",mlist);
        map.put("nums",nlist);
        return JsonUtil.toJson(map);
    }

    /**
     * 查询合同
     */
//    @ResponseBody
//    @RequestMapping("/selectContractByUUID.do")
//    public String selectContractByUUID(@RequestBody String array[]){
//        if ("".equals(array[0])){
//            return JsonUtil.toJson(contractMapper.getByAuthor(AUTHER_NAME,0));
//        }
//        List<Contract> list = contractMapper.getByUUID(array[0]);
//        return JsonUtil.toJson(list);
//    }
    /**
     * 更新合同状态
     * @return
     */
    @ResponseBody

    @RequestMapping(value = "/updatestatus1.do",method = RequestMethod.POST)
    public String updatestatus1(@RequestBody String array[]){
        Contract contract = contractMapper.getById(Integer.parseInt(array[0]));
        contract.setStatus(array[1]);
        contractMapper.updateAudit(contract);
        return JsonUtil.toJson("更新成功");
    }


    /**
     * 修改合同
     */
    @Transactional
    @ResponseBody
    @RequestMapping("/updatecontract.do")
    public String updatecontract(HttpServletRequest request)throws Exception{
        // 转型为MultipartHttpRequest：
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获得文件：
        List<MultipartFile> files = multipartRequest.getFiles("contractfile");
        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String clientele = request.getParameter("clientele");
        String endDate = request.getParameter("endDate");
        String materials = request.getParameter("materials");
        String num = request.getParameter("num");
        Contract contract = contractMapper.getById(Integer.parseInt(id));
        contract.setTitle(title);
        contract.setClientele(clientele);
        if (!endDate.equals("")){
            contract.setEndDate(StringToDateUtil.StringToDate(endDate));
        }
        //解析材料和数量
        String mtype[] = materials.split(",");
        String mnum[] = num.split(",");
        int accounts = 0;
        StringBuffer content = new StringBuffer();
        StringBuffer contentnum = new StringBuffer();
        for (int i=0;i<mtype.length;i++){
            Materials materials1 = materialsMapper.getByName(mtype[i]);
            //更新建材
            String martype = contract.getContent();
            int remain;
            if (martype.contains(materials1.name)){
                remain = materials1.num+Integer.parseInt(contract.num.split(",")[i])-Integer.parseInt(mnum[i]);
            }
            else {
                remain = materials1.num-Integer.parseInt(mnum[i]);
            }
            if (remain<0){
                return JsonUtil.toJson(materials1.name+"材料不足,请补充库存在重试");
            }
            accounts += Integer.parseInt(mnum[i])*materials1.price;//计算总价值
            materials1.setNum(remain);
            materials1.setValue(materials1.price*remain);
            materialsMapper.update(materials1);//更新库存,这里是事务原子操作，不用担心某一种材料库存不足造成的更新脏数据
            content.append(materials1.name).append(",");
            contentnum.append(mnum[i]).append(",");

        }
        contract.setContent(content.toString().substring(0,content.toString().length()-1));
        contract.setNum(contentnum.toString().substring(0,contentnum.toString().length()-1));
        contract.setAccounts(accounts);
        if (files.size()>0){
            String path = this.getClass().getResource("").getPath().split("target/")[0] + REAL_PATH;
            String fileName = files.get(0).getOriginalFilename();
            File dest = new File(path + REAL_PATH + fileName);
            FileUtil.delete(path + REAL_PATH +contract.url);//如果合同不为空 则删除旧版合同
            contract.setUrl(REAL_PATH +fileName);
            files.get(0).transferTo(dest);
        }
        contractMapper.update(contract);
        return JsonUtil.toJson("success");
    }

    /**
     * 下载文件
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/cdownloadcontract/{id}.do")
    public void downloadFile(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Contract contract = contractMapper.getById(id);
        String path = this.getClass().getResource("").getPath().split("target/")[0];
        FileUtil.downfile(response,path+contract.getUrl());
    }


    /**
     * 删除合同
     */
    @ResponseBody
    @RequestMapping("/deletecontract.do")
    public String deletecontract(@RequestBody String array[]){
        contractMapper.deleteById(Integer.parseInt(array[0]));
        return JsonUtil.toJson("删除成功");
    }



    /**
     * 建材页面
     * @return
     */
    @RequestMapping("/materialshtml.do")
    public String materialshtml(Model model){
        List<Materials> list = materialsMapper.getAll();
        model.addAttribute("logs",list);
        return "client/materials";
    }



    /**
     * 签单报表页面
     * @return
     */
    @RequestMapping("/creportHtml.do")
    public String creportHtml(){
        return "client/report";
    }


    /**
     * 查询报表
     * @return
     */
    @ResponseBody
    @RequestMapping("/cgetreport.do")
    public String getDetails(){
        List<Contract> list = contractMapper.getAll1(AUTHER_NAME);
        List<Map<String,Integer>> map = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
        for (int i=0;i<list.size();i++){
            String timeFormat = sdf.format(list.get(i).signDate);
            int num = 0;
            for (int j=0;j<map.size();j++){
                if (map.get(j).containsKey(timeFormat)){
                    num = 1;
                    map.get(j).put(timeFormat,list.get(i).accounts);
                }
            }
            if (num==0){
                Map<String,Integer> mm = new HashMap<>();
                mm.put(timeFormat,list.get(i).accounts);
                map.add(mm);
            }

        }
        Object dd[] = new Object[map.size()];
        for (int i=0;i<map.size();i++){
            Set<String> list1 = map.get(i).keySet();
            dd[i] = new ArrayList<>(list1).get(0);
        }
        Integer pp[] = new Integer[map.size()];
        Integer ff[] = new Integer[map.size()];
        Integer ww[] = new Integer[map.size()];
        for(int i=0;i<map.size();i++){
            pp[i] = map.get(i).get(dd[i]);
        }
        for (int i=0;i<ff.length;i++){
            String heng = String.valueOf(dd[i]);
            int a1 = 0;//未付款金额
            int a2 = 0;//付款金额
            int a3 = 0;//总金额
            for (int j=0;j<list.size();j++){
                String timeFormat = sdf.format(list.get(j).signDate);
                if (heng.equals(timeFormat)){
                    a3 += list.get(j).accounts;//总金额
                }

            }
            pp[i] = a3;
            ff[i] = a2;
            ww[i] = a1;
        }
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("dd",dd);
        result.put("pp",pp);//总金额
        result.put("ff",ff);//已付款
        result.put("ww",ww);//未付款
        return JsonUtil.toJson(result);
    }

}
