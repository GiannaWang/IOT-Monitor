// 告警服务
import request from './request.js'

// 告警服务对象
const alertService = {
    // 1. 获取所有告警
    async getAllAlerts() {
        try {
            const response = await request.get('/getAllAlerts');
            console.log('获取告警数据成功:', response);
            return response.data || [];
        } catch (error) {
            console.error('获取告警数据失败:', error);
            return [];
        }
    },

    // 4. 标记告警为已处理
    async markAsHandled(alertId) {
        try {
            const response = await request.post('/markAsHandled', null, {
                params: { alertId }
            });
            console.log('标记告警成功:', response);
            return response.data === true;
        } catch (error) {
            console.error('标记告警失败:', error);
            return false;
        }
    },

    // 2.获取最新5条告警
    async getLatest5Alerts() {
        try {
            const response = await request.get('/getLatest5Alerts');
            console.log('获取最新5条告警数据成功:', response);
            return response.data;
        } catch (error) {
            console.error('获取最新5条告警数据失败:', error);
            return null;
        }
    },

    // 3. 获取今日告警计数
    async getTodayAlertCount() {
        try {
            const response = await request.get('/getTodayAlertCount');
            console.log('获取今日告警计数成功:', response);
            return response.data;
        } catch (error) {
            console.error('获取今日告警计数失败:', error);
            return null;
        }
    }
}

export default alertService