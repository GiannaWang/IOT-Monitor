package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警业务逻辑实现类
 */
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertMapper alertMapper;

    @Override
    public List<Alert> getLatest5Alerts() {
        return alertMapper.getLatest5Alerts();
    }

    @Override
    public int getTodayAlertCount() {
        return alertMapper.getTodayAlertCount();
    }

    @Override
    public boolean markAsHandled(int alertId) {
        // TODO: 实现标记告警为已处理的逻辑
        // 需要在AlertMapper中添加相应的方法
        return false;
    }
}
