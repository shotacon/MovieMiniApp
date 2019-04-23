package movie_v3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.api.moviefan.entity.ShowSeatsEntity;
import com.shotacon.movie.utils.newapi.SeatUtil;

public class Test {

    public static void main(String[] args) throws Exception {

        String path = "/Users/shotacon/Downloads/";
        String fileName = "response_1548216775491.json";
//        String fileName = "response_1548058000351.json";
        Path filePath = Paths.get(path + fileName);
        byte[] readAllBytes = Files.readAllBytes(filePath);
        String json = new String(readAllBytes);
        List<ShowSeatsEntity> javaList = JSONObject.parseObject(json).getJSONArray("data").toJavaList(ShowSeatsEntity.class);
        SeatUtil.printSeat(javaList);
    }

}
