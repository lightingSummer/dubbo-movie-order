# dubbo-movie-order
film-order模块
订单模块<br>

## 用到的技术及实现主要功能<br>
* 使用dubbo提供cinema信息各种查询服务，使用zookeeper做注册中心，用于服务注册及调用<br>
* 使用springboot作为后端主要框架，aop配置日志<br>
* 使用mysql5.7作为数据库存储，mybatis做查询，mybatis-generator生成xml映射，pagehelper做分页<br>
* 搭建FTP服务器，存储座位对应的json文件
* 使用spring事务保证下单

## 待优化
* 前三个接口gateway调用的时候可以考虑异步同时调用，需要引入分布式事务
* 引入水平分表来分散单表压力

## api列表<br>
```java
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
```

## 数据表ddl<br>
```sql
CREATE TABLE `tb_order` (
  `UUID` varchar(100) NOT NULL COMMENT '主键编号',
  `cinema_id` int(11) DEFAULT NULL COMMENT '影院编号',
  `field_id` int(11) DEFAULT NULL COMMENT '放映场次编号',
  `film_id` int(11) DEFAULT NULL COMMENT '电影编号',
  `seats_ids` varchar(50) DEFAULT NULL COMMENT '已售座位编号',
  `seats_name` varchar(200) DEFAULT NULL COMMENT '已售座位名称',
  `film_price` double DEFAULT NULL COMMENT '影片售价',
  `order_price` double DEFAULT NULL COMMENT '订单总金额',
  `order_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `order_user` int(11) DEFAULT NULL COMMENT '下单人',
  `order_status` int(11) DEFAULT '0' COMMENT '0-待支付,1-已支付,2-已关闭',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单信息表';
```
