package club.lightingsummer.movie.order.po;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private String uuid;

    private Integer cinemaId;

    private Integer fieldId;

    private Integer filmId;

    private String seatsIds;

    private String seatsName;

    private Double filmPrice;

    private Double orderPrice;

    private Date orderTime;

    private Integer orderUser;
    //0-待支付,1-已支付,2-已关闭
    private Integer orderStatus;
}