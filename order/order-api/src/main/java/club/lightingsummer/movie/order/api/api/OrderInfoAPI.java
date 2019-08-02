package club.lightingsummer.movie.order.api.api;

import club.lightingsummer.movie.order.api.vo.OrderVO;
import club.lightingsummer.movie.order.api.vo.Page;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
public interface OrderInfoAPI {

    // 验证是否是合法的座位
    boolean isTrueSeats(String fieldId,String seats);

    // 已经销售的座位里，有没有这些座位
    boolean isNotSoldSeats(String fieldId,String seats);

    // 创建订单信息
    OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    // 使用当前登陆人获取已经购买的订单
    Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page);

    // 根据电影场次id 获取所有已经销售的座位编号
    String getSoldSeatsByFieldId(Integer fieldId);

}
