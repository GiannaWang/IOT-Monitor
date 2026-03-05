package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.Alert;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AlertMapper {

    @Select("SELECT alerts.id, alerts.deviceid, alerts.locationid, alerts.alerttype, " +
            "alerts.severity, alerts.message, alerts.timestamp, alerts.handled, " +
            "locations.roomnumber " +
            "FROM alerts " +
            "JOIN locations ON alerts.locationid = locations.id " +
            "ORDER BY alerts.timestamp DESC " +
            "LIMIT 5")
    List<Alert> getLatest5Alerts();

    @Select("SELECT alerts.id, alerts.deviceid, alerts.locationid, alerts.alerttype, " +
            "alerts.severity, alerts.message, alerts.timestamp, alerts.handled, " +
            "locations.roomnumber " +
            "FROM alerts " +
            "JOIN locations ON alerts.locationid = locations.id " +
            "ORDER BY alerts.timestamp DESC")
    List<Alert> getAllAlerts();

    @Select("SELECT COUNT(*) FROM alerts WHERE DATE(timestamp) = CURDATE()")
    int getTodayAlertCount();

    // 标记告警为已处理
    @Update("UPDATE alerts SET handled = 1 WHERE id = #{alertId}")
    int markAsHandled(@Param("alertId") int alertId);

    // 插入新告警记录（由定时任务调用）
    @Insert("INSERT INTO alerts (deviceid, locationid, ruleid, alerttype, severity, message, timestamp, handled) " +
            "VALUES (#{deviceId}, #{locationId}, #{ruleId}, #{alertType}, #{severity}, #{message}, #{timestamp}, 0)")
    int insertAlert(Alert alert);

    // 检查1小时内是否已存在同设备同类型的未处理告警（防重复告警）
    @Select("SELECT COUNT(*) FROM alerts WHERE deviceid = #{deviceId} AND alerttype = #{alertType} " +
            "AND handled = 0 AND timestamp >= DATE_SUB(NOW(), INTERVAL 1 HOUR)")
    int countRecentUnhandledAlerts(@Param("deviceId") String deviceId, @Param("alertType") String alertType);

    // 根据设备编号删除告警记录
    @Delete("DELETE FROM alerts WHERE deviceid = #{deviceid}")
    int deleteAlertsByDeviceId(@Param("deviceid") String deviceid);
}
