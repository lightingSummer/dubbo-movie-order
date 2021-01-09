package club.lightingsummer.movie.order;

import club.lightingsummer.movie.order.api.OrderInfoAPI;
import club.lightingsummer.movie.order.util.FTPUtil;
import club.lightingsummer.movie.order.vo.OrderVO;
import club.lightingsummer.movie.order.vo.Page;
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
    @Autowired
    OrderInfoAPI orderInfoAPI;

    @Test
    public void contextLoads() {
        System.out.println(ftpUtil.getFileStrByAddress("1.txt"));
    }

    @Test
    public void contextLoads1() {
        System.out.println(orderInfoAPI.isTrueSeats("1","33"));
        System.out.println(orderInfoAPI.isNotSoldSeats("1","1.3"));
        System.out.println(orderInfoAPI.saveOrderInfo(1,"5,6,7,8","第一排5座，第一排6座，第一排7座，第一排8座",1));
        System.out.println(orderInfoAPI.getSoldSeatsByFieldId(1));
        Page<OrderVO> page = new Page<>(1,10);
        System.out.println(orderInfoAPI.getOrderByUserId(1,page));
    }

}
