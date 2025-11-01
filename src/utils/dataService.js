// 数据分析服务
import request from "./request";


const dataService = {
    // 1. 获取所有传感器数据
    async getAllSensorData() {
        try {
            const response = await request.get('/getAllSensorData');
            console.log('获取传感器数据成功:', response);
            return response.sensorData;
        } catch (error) {
            console.error('获取传感器数据失败:', error);
            return null;
        }
    },

    // 2. 根据传感器类型获取数据
    async getSensorDataByType(sensorType) {
        try {
            const response = await request.get('/getSensorDataByType', {
                params: { selectedDataType: sensorType } 
            });
            console.log(`获取${sensorType}数据成功:`, response);
            return response.sensorData;
        } catch (error) {
            console.error(`获取${sensorType}数据失败:`, error);
            return null;
        }
    },

    // 3. 获取所有设备数量
    async getDeviceCount() {
        try {
            const response = await request.get('/countAllDevices');
            console.log('获取设备数量成功:', response);
            return response.data;
        } catch (error) {
            console.error('获取设备数量失败:', error);
            return null;
        }
    },

    // 4. 获取在线设备数量
    async getOnlineDeviceCount() {
        try {
            const response = await request.get('/countOnlineDevices');
            console.log('获取在线设备数量成功:', response);
            return response.data;
        } catch (error) {
            console.error('获取在线设备数量失败:', error);
            return null;
        }
    }

}

export default dataService;