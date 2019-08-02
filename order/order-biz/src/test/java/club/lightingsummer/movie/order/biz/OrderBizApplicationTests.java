package club.lightingsummer.movie.order.biz;

import club.lightingsummer.movie.order.biz.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderBizApplicationTests {

    @Autowired
    FTPUtil ftpUtil;

    @Test
    public void contextLoads() {
        System.out.println(ftpUtil.getFileStrByAddress("1.txt"));
    }

}
