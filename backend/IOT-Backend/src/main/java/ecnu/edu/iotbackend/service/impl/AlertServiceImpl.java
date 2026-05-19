package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 告警业务逻辑实现类
 */
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertMapper alertMapper;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Override
    public List<Alert> getAllAlerts() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return Collections.emptyList();
            }
            return alertMapper.getAllAlertsByLocationIds(accessibleRoomIds);
        }
        return alertMapper.getAllAlerts();
    }

    @Override
    public List<Alert> getLatest5Alerts() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return Collections.emptyList();
            }
            return alertMapper.getLatest5AlertsByLocationIds(accessibleRoomIds);
        }
        return alertMapper.getLatest5Alerts();
    }

    @Override
    public int getTodayAlertCount() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return 0;
            }
            return alertMapper.getTodayAlertCountByLocationIds(accessibleRoomIds);
        }
        return alertMapper.getTodayAlertCount();
    }

    @Override
    public boolean markAsHandled(int alertId) {
        return alertMapper.markAsHandled(alertId) > 0;
    }

    private List<Integer> getAccessibleRoomIds() {
        if (currentUserProvider.isAdmin()) {
            return null;
        }
        return currentUserProvider.getAccessibleRoomIds();
    }
}
