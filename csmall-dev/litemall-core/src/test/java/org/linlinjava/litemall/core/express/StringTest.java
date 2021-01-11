package org.linlinjava.litemall.core.express;

import org.junit.Test;
import org.linlinjava.litemall.core.util.DesensitizationUtil;

public class StringTest {


    /**
     * 测试地址脱敏，从最后一个杠开始
     */
    @Test
    public void addRessSub(){
        String address="湖南省-株洲市-茶陵县-虎踞镇-双元村乔上组1002号";
        String subAddress= DesensitizationUtil.address(address,address.length()-address.lastIndexOf("-"));
        System.out.println(subAddress);
    }
}
