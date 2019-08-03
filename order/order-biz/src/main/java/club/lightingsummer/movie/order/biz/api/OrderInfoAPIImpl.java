package club.lightingsummer.movie.order.biz.api;

import club.lightingsummer.movie.cinema.api.api.CinemaInfoAPI;
import club.lightingsummer.movie.cinema.api.vo.OrderQueryVO;
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
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Transactional
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        try {
            // 调用影院模块接口 查询场次电影信息
            OrderQueryVO orderQueryVO = cinemaInfoAPI.getOrderNeedFilmInfoByField(fieldId);
            if (orderQueryVO == null) {
                logger.error("调用cinema模块异常");
                return null;
            }
            // 求订单总金额
            double filmPrice = orderQueryVO.getPrice();
            int solds = soldSeats.split(",").length;
            double totalPrice = getTotalPrice(solds, filmPrice);
            // 填充order信息
            Order order = new Order();
            order.setUuid(UUIDUtil.getUUID());
            order.setCinemaId(orderQueryVO.getCinemaId());
            order.setFieldId(orderQueryVO.getFieldId());
            order.setFilmId(orderQueryVO.getFilmId());
            order.setFilmPrice(filmPrice);
            order.setOrderPrice(totalPrice);
            order.setSeatsIds(soldSeats);
            order.setSeatsName(seatsName);
            order.setOrderUser(userId);
            // 封装VO返回
            int insert = orderMapper.insertSelective(order);
            if (insert > 0) {
                OrderVO orderVO = orderMapper.selectOrderByOrderId(order.getUuid());
                if (orderVO != null) {
                    return orderVO;
                } else {
                    throw new Exception();
                }
            } else {
                logger.error("订单插入失败");
                throw new Exception();
            }
        } catch (Exception e) {
            logger.error("生成订单信息失败 " + "场次信息:" + fieldId + "座位:" + soldSeats + "userId:" + userId + e.getMessage());
            // 事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return null;
    }

    /**
     * @author: lightingSummer
     * @date: 2019/8/3 0003
     * @description: 根据用户id查询用户订单信息
     */
    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        try {
            PageHelper.startPage(page.getCurrent(), page.getSize(), "order_time");
            List<OrderVO> list = orderMapper.selectOrderByUserId(userId);
            // 计算页数
            page.setTotalPage((int) Math.ceil(list.size() / (1.0 * page.getSize())));
            page.setRecords(list);
            return page;
        } catch (Exception e) {
            logger.error("查询用户订单信息出错" + e.getMessage());
            return null;
        }
    }

    /**
     * @author: lightingSummer
     * @date: 2019/8/3 0003
     * @description: 查询场次已售信息
     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        try {
            if (fieldId == null) {
                logger.error("查询已售座位错误，未传入任何场次编号");
                return "";
            } else {
                return orderMapper.getSoldSeatsByFieldId(fieldId);
            }
        } catch (Exception e) {
            logger.error("查询场次已售信息失败" + e.getMessage());
            return "";
        }
    }

    private static double getTotalPrice(int solds, double filmPrice) {
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);
        BigDecimal result = soldsDeci.multiply(filmPriceDeci);
        // 四舍五入，取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
