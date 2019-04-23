package com.shotacon.movie.utils.newapi;

import java.util.ArrayList;
import java.util.List;

import com.shotacon.movie.api.moviefan.entity.ShowSeatsEntity;

public class SeatUtil {

    /**
     * <li>seatID String 座位ID
     * <li>rowNum String 行号
     * <li>colNum String 列号
     * <li>xCoord int 横坐标
     * <li>yCoord int 纵坐标
     * <li>type int 座位类型（1：普通 2, 情侣左座; 3, 情侣右座）
     * <li>loveCode String 情侣座编码。当座位类型是情侣座时才有该编码，情侣座编码相同的两个座位互相关联。
     * <li>status String 可售状态（0：不可售 1：可售，2不是座位）
     * 
     * @param json
     */
    public static List<String> printSeat(List<ShowSeatsEntity> seatList) {

        int maxXCoord = 0;
        int maxYCoord = 0;
        List<String> list = new ArrayList<>();
        for (ShowSeatsEntity seat : seatList) {
            if (maxXCoord < seat.getXCoord()) {
                maxXCoord = seat.getXCoord();
            }
            if (maxYCoord < seat.getYCoord()) {
                maxYCoord = seat.getYCoord();
            }
        }
        int i = 0;
        int j = 0;
        String s = "";
        for (ShowSeatsEntity seat : seatList) {
            i++;
//            String ss = seat.getColNum() + "," + seat.getRowNum();
            String b = "  🚫  "; // 不可售
            String a = "      "; // 非座位
            String c = "  🐶  "; // 可售
            String l = "  🐕  "; // 情侣左
            String r = "  🐩  "; // 情侣右

            if (i == maxYCoord) {
                list.add(s);
                i = 0;
                j++;
                s = "第" + (j) + "排";
            }
            if (seat.getStatus().equals("1")) {
                if (seat.getType() == 2) {
                    s += l;
                } else if (seat.getType() == 3) {
                    s += r;
                } else {
                    s += c;
                }
            } else if (seat.getStatus().equals("0")) {
                s += b;
            } else {
                s += a;
            }
        }
        for (String string : list) {
            System.out.println(string);
        }

        return list;

    }
}
