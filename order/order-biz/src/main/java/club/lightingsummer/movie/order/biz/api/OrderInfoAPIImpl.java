package club.lightingsummer.movie.order.biz.api;

import club.lightingsummer.movie.cinema.api.api.CinemaInfoAPI;
import club.lightingsummer.movie.order.api.api.OrderInfoAPI;
import club.lightingsummer.movie.order.api.po.Order;
import club.lightingsummer.movie.order.api.vo.OrderVO;
import club.lightingsummer.movie.order.api.vo.Page;
import club.lightingsummer.movie.order.biz.util.FTPUtil;
import club.lightingsummer.movie.order.biz.util.UUIDUtil;
import club.lightingsummer.movie.order.dal.dao.CinemaMapper;
import club.lightingsummer.movie.order.dal.dao.OrderMapper;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
@Component
@Service(interfaceClass = OrderInfoAPI.class, loadbalance = "roundrobin")
public class OrderInfoAPIImpl implements OrderInfoAPI {
    private static final Logger logger = LoggerFactory.getLogger(OrderInfoAPIImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private FTPUtil ftpUtil;
    @Reference(interfaceClass = CinemaInfoAPI.class, cache = "lru", connections = 10, check = false)
    private CinemaInfoAPI cinemaInfoAPI;

    /**
     * @author: lightingSummer
     * @date: 2019/8/2 0002
     * @description: 验证是否是合法的座位
     */
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        try {
            String seatPath = cinemaMapper.selectSeatAddressByFieldId(Integer.valueOf(fieldId));
            // 获取座位分布图
            String fileFromSeatPath = ftpUtil.getFileStrByAddress(seatPath);
            JSONObject jsonObject = JSONObject.parseObject(fileFromSeatPath);
            // 获取seat id座位集合
            String ids = jsonObject.get("ids").toString();
            String[] idArray = ids.split(",");
            String[] seatArray = seats.split(",");
            // 判断seat是否都在json获取的id里面
            Set<String> set = new HashSet<>();
            Collections.addAll(set, idArray);
            for (String seat : seatArray) {
                if (!set.contains(seat)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("验证座位合法性失败" + e.getMessage());
            return false;
        }
    }

    /**
     * @author: lightingSummer
     * @date: 2019/8/2 0002
     * @description: 判断选的座位是否都没卖
     */
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        try {
            // 查找当场电影所有已经卖出的座位
            List<Order> orders = orderMapper.selectOrderByFieldId(Integer.valueOf(fieldId));
            Set<String> hasSoldSeats = new HashSet<>();
            for (Order order : orders) {
                String[] soldSeatIds = order.getSeatsIds().split(",");
                Collections.addAll(hasSoldSeats, soldSeatIds);
            }
            String[] seatArray = seats.split(",");
            // 遍历查找选的座位是否在卖出的列表里
            for (String seat : seatArray) {
                if (hasSoldSeats.contains(seat)) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            logger.error("查询已售座位失败" + e.getMessage());
            return false;
        }
    }

    /**
     * @author: lightingSummer
     * @date: 2019/8/2 0002
     * @description: 生成订单信息
     */
    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        Order order = new Order();
        order.setUuid(UUIDUtil.getUUID());



        OrderVO orderVO = new OrderVO();
        return null;
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        return null;
    }

    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        return null;
    }
}
