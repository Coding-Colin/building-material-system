package contract;

import java.util.List;

import com.contract.system.bean.Contract;
import com.contract.system.bean.User;
import com.contract.system.mapper.ContractMapper;
import com.contract.system.mapper.UserMapper;
import com.contract.system.util.StringToDateUtil;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Tester {


    @Test
    public void testUSerMapper() {
        System.out.println(test());
    }


    public int test(){
        try {
            int i = 0;
            throw new Exception();
//            return 1;
        }
        catch (Exception e){
            System.out.println("error");
            return 2;
        }
        finally {
            System.out.println("final");
            return 3;
        }
    }

}



