package org.linlinjava.litemall.core.util.bcrypt;

import org.junit.Test;
import org.linlinjava.litemall.core.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegexUtilTest {

    @Test
    public void telTest(){
        String tel="0577-8456547";
        if (!RegexUtil.isMobileExact(tel)&&!RegexUtil.isTelNew(tel)){
            System.out.println("校验结果：非电话号码也不是手机号码");
        }else
            System.out.println("校验结果：符合校验");

    }


    public static void main(String[] args) {
        // 测试源
        List<String> sourceList = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            sourceList.add("第" + i + "姓名");
        }
        sourceList.stream().collect(Collectors.toList());

    }

    private static void doSome() {
   /*     try {
            Thread.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
