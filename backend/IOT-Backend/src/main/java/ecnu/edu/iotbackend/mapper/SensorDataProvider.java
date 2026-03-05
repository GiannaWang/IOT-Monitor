package ecnu.edu.iotbackend.mapper;

import java.util.Map;

/**
 * SensorData 动态 SQL 构建器
 * 支持按 dataType / locationId / period / timeSlot 组合筛选
 */
public class SensorDataProvider {

    public String getSensorDataWithFilters(Map<String, Object> params) {
        Integer locationId = params.get("locationId") != null
                ? Integer.parseInt(params.get("locationId").toString()) : null;
        String period    = (String) params.get("period");
        String timeSlot  = (String) params.get("timeSlot");

        StringBuilder sql = new StringBuilder("SELECT sd.* FROM sensor_datas sd ");

        if (locationId != null) {
            sql.append("JOIN sensor_devices dev ON sd.deviceid = dev.id ");
        }

        sql.append("WHERE sd.datatype = #{dataType} ");

        if (locationId != null) {
            sql.append("AND dev.locationid = #{locationId} ");
        }

        if (period != null && !period.isEmpty()) {
            switch (period) {
                case "today":
                    sql.append("AND DATE(sd.timestamp) = CURDATE() ");
                    break;
                case "yesterday":
                    sql.append("AND DATE(sd.timestamp) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) ");
                    break;
                case "7days":
                    sql.append("AND sd.timestamp >= DATE_SUB(NOW(), INTERVAL 7 DAY) ");
                    break;
                case "30days":
                    sql.append("AND sd.timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY) ");
                    break;
                default:
                    break;
            }
        }

        if (timeSlot != null && !timeSlot.isEmpty()) {
            switch (timeSlot) {
                case "morning":
                    sql.append("AND HOUR(sd.timestamp) BETWEEN 6 AND 11 ");
                    break;
                case "afternoon":
                    sql.append("AND HOUR(sd.timestamp) BETWEEN 12 AND 17 ");
                    break;
                case "evening":
                    sql.append("AND HOUR(sd.timestamp) BETWEEN 18 AND 23 ");
                    break;
                case "teaching":
                    sql.append("AND TIME(sd.timestamp) BETWEEN '08:00:00' AND '20:30:00' ");
                    break;
                default:
                    break;
            }
        }

        sql.append("ORDER BY sd.timestamp DESC");
        return sql.toString();
    }
}
