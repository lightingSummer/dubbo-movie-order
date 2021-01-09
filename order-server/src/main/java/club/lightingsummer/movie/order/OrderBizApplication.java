package club.lightingsummer.movie.order;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "club.lightingsummer.movie.order")
@MapperScan("club.lightingsummer.movie.order.dal.dao")
@EnableDubboConfiguration
public class OrderBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBizApplication.class, args);
    }

}
