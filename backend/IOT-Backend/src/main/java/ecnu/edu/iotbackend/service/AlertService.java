package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.Alert;
import java.util.List;

/**
 * 告警业务逻辑接口
 */
public interface AlertService {

    /**
     * 获取所有告警记录
     * @return 告警列表
     */
    List<Alert> getAllAlerts();

    /**
     * 获取最新5条告警记录
     * @return 告警列表
     */
    List<Alert> getLatest5Alerts();

    /**
     * 获取今日告警数量
     * @return 今日告警总数
     */
    int getTodayAlertCount();

    /**
     * 标记告警为已处理
     * @param alertId 告警ID
     * @return 是否成功
     */
    boolean markAsHandled(int alertId);
}
