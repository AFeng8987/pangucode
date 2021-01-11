package org.linlinjava.litemall.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ExpressTest {

    private final Log logger = LogFactory.getLog(ExpressTest.class);
    @Autowired
    private ExpressService expressService;

    @Test
    public void test() {
        ExpressInfo ei = null;
        String oo;
        try {
            ei = expressService.getExpressInfo("HTKY", "557020683421114");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info(ei);

    }

    @Test
    public void testExpNo() {
        String oo = null;
        try {
            oo = expressService.getOrderTracesByJson( "557020683421114");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info(oo);

    }
}
