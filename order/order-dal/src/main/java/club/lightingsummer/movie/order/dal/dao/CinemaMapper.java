package club.lightingsummer.movie.order.dal.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
@Mapper
@Repository
public interface CinemaMapper {

    @Select("select " +
            "seat_address " +
            "from tb_hall_dict h,tb_field f " +
            "where h.UUID = f.hall_id and f.UUID = #{field}")
    String selectSeatAddressByFieldId(@Param("field") int field);
}
