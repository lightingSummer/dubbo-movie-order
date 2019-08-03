package club.lightingsummer.movie.order.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
@Data
public class Page<T> implements Serializable {
    private Integer nowPage;
    private Integer totalPage;
    private Integer size;
    private List<T> records;

    private Page() {
    }

    public Page(int nowPage) {
        this.nowPage = nowPage;
        this.totalPage = 0;
    }
}
