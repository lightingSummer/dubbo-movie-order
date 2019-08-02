package club.lightingsummer.movie.order.biz.util;

import java.util.UUID;

/**
 * @author     ：lightingSummer
 * @date       ：2019/8/2 0002
 * @description：
 */
public class UUIDUtil {

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
