package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.Alert;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AlertMapper {

    @Select("SELECT alerts.timestamp, locations.roomnumber, alerts.alerttype " +
            "FROM alerts " +
            "JOIN locations ON alerts.locationid = locations.id " +
            "ORDER BY alerts.timestamp DESC " +  // 明确指定timestamp所属表（避免字段冲突）
            "LIMIT 5;")
    public List<Alert> getLatest5Alerts();

    @Select("SELECT COUNT(*) FROM alerts WHERE DATE(timestamp) = CURDATE()")
    public int getTodayAlertCount();
}
