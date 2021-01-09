package club.lightingsummer.movie.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
@Data
public class OrderVO implements Serializable {
    // 订单id
    private String orderId;
    // 电影name
    private String filmName;
    // 时间
    private String fieldTime;
    private String cinemaName;
    // 座位信息
    private String seatsName;
    private String orderPrice;
    // 时间戳
    private String orderTimestamp;
    // 订单状态
    private String orderStatus;
}
