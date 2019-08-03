package club.lightingsummer.movie.order.dal.dao;

import club.lightingsummer.movie.order.api.po.Order;
import club.lightingsummer.movie.order.api.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(String uuid);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(String uuid);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectOrderByFieldId(@Param("fieldId") int fieldId);

    List<OrderVO> selectOrderByUserId(@Param("userId") int userId);

    OrderVO selectOrderByOrderId(@Param("orderId") String orderId);

    String getSoldSeatsByFieldId(@Param("fieldId") int fieldId);
}