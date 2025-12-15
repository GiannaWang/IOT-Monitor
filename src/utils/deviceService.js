// 设备管理服务
import request from "./request";

const deviceService = {
    // 获取所有已启用的设备
    async getAllDevices() {
        try {
            const response = await request.get('/device/enabled');
            console.log('获取已启用设备列表响应:', response);
            if (response.code === 200) {
                return response.data || [];
            } else {
                throw response;
            }
        } catch (error) {
            console.error('获取设备列表失败:', error);
            throw error;
        }
    },

    // 获取可添加的 HA 设备（未启用的设备）
    async getAvailableDevices() {
        try {
            const response = await request.get('/device/available');
            console.log('获取可添加设备列表响应:', response);
            if (response.code === 200) {
                return response.data || [];
            } else {
                throw response;
            }
        } catch (error) {
            console.error('获取可添加设备列表失败:', error);
            throw error;
        }
    },

    // 启用设备（添加设备）
    async enableDevice(deviceData) {
        try {
            const response = await request.post('/device/enable', deviceData);
            console.log('启用设备响应:', response);
            return response;
        } catch (error) {
            console.error('启用设备失败:', error);
            throw error;
        }
    },

    // 禁用设备（删除设备）
    async disableDevice(id) {
        try {
            const response = await request.delete(`/device/${id}`);
            console.log('禁用设备成功:', response);
            return response;
        } catch (error) {
            console.error('禁用设备失败:', error);
            throw error;
        }
    },

    // 获取设备详情
    async getDeviceById(id) {
        try {
            const response = await request.get(`/device/${id}`);
            console.log('获取设备详情成功:', response);
            return response.data;
        } catch (error) {
            console.error('获取设备详情失败:', error);
            throw error;
        }
    }
}

export default deviceService;

