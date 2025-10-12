<template>
  <div class="body">
    <div class="main-box">
        <div class="left">
        </div>
        <div class="right">
            <h2>登录</h2>
            <form @submit.prevent="handleLogin">
                <div class="error-message" v-if="errorMessage">
                  <span>{{ errorMessage }}</span>
                  <button class="close-btn" @click="closeError">×</button>
                </div>
                <div class="input-box">
                    <input type="text" placeholder="用户名" v-model="username" />
                </div>
                <div class="input-box">
                    <input type="password" placeholder="密码" v-model="password" />
                </div>
                <div class="btn-box">
                    <button type="submit">登 录</button>
                </div>
            </form>
            <div class="register">
                <a href="#">忘记密码?</a>
                <a href="#">注册账号</a>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 导入路由钩子

// 获取路由实例
const router = useRouter();

// 定义表单数据
const username = ref('');
const password = ref('');
const errorMessage = ref('');


// 定义初始默认值（仅在本地存储为空时使用）
const DEFAULT_USERNAME = 'admin';
const DEFAULT_PASSWORD = 'admin123';

onMounted(() => {
  // 检查本地存储中是否已有值
  const stored_Username = localStorage.getItem('username');
  const stored_Password = localStorage.getItem('password');
  
  console.log('本地存储的用户名:', stored_Username);
  console.log('本地存储的密码:', stored_Password);


  // 如果本地存储为空，则设置初始值并保存
  if (!stored_Username) {
    localStorage.setItem('username', DEFAULT_USERNAME);
  }
  if (!stored_Password) {
    localStorage.setItem('password', DEFAULT_PASSWORD);
  }
});

// 处理登录逻辑
const handleLogin = () => {
  // 简单验证
  if (!username.value || !password.value) {
    errorMessage.value = '请输入用户名和密码';
    return;
  }
  
  // 这里可以添加实际的登录API调用
  // 为了演示，使用简单的验证
  if (username.value === localStorage.getItem('username') && password.value === localStorage.getItem('password')) {
    // 登录成功
    localStorage.setItem('username', username.value)
    localStorage.setItem('isLoggedIn', 'true')
    router.push('/admin')
    errorMessage.value = '';
    alert('登录成功！');
  } else {
    errorMessage.value = '用户名或密码错误';
  }
};

// 清空错误信息，提示框会因 v-if 隐藏
const closeError = () => {
  errorMessage.value = ''; 
};

</script>


<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.body{
  background: #f5f6fa;
  margin: 0;
  padding: 0;
}
.main-box {
  width: 50rem;
  height: 28rem;
  display: flex;
  border-radius: 10px;
  margin: 4rem auto;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.12);
}
.main-box .left {
  position: relative;
  width: 45%;
  height: 100%;
  background-image: url('../assets/login.png'); 
  background-size: cover;
  background-position: center;
}
.main-box .right {
  display: flex;
  width: 60%;
  flex-direction: column;
  align-items: center;
}
.main-box .right h2 {
  margin-top: 3rem;
  font-size: 1.8rem;
  color: #111111;
}
.main-box .right form {
  margin-top: 1.5rem;
  width: 70%;
}
.main-box .right form .input-box {
  margin: 1.5rem 0;
  width: 100%; 
  height: 2rem;
}
.main-box .right form .input-box input {
  width: 100%;
  height: 100%;
  padding: 0 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
}
.main-box .right form .btn-box {
  margin-top: 2rem;
  width: 100%;
  height: 2.5rem;
}
.main-box .right form .btn-box button {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 5px;
  background-color: #e3eafc;
  color: #409eff;
  font-size: 1rem;
  cursor: pointer;
}
.main-box .right .register {
  margin-top: 2.5rem;
  width: 70%;
  display: flex;
  justify-content: space-between;
}
.main-box .right .register a {
  font-size: 0.9rem;
  color: #409eff;
  text-decoration: none;
  border-bottom: #899cff solid 1px;
  cursor: pointer;
}
.error-message {
  height: 2.5rem;
  background-color: #ffd4d4aa;
  border-radius: 5px;
  border: #ff94949f solid 1px;
  color: #e64c4c;                  /* 错误文字颜色 */
  font-size: 0.9rem;
  display: flex;                  /* 开启 Flex 布局 */
  align-items: center;            /* 垂直居中 */
  justify-content: space-between; /* 文字左、×右分布 */
  padding: 0 1rem;                /* 左右内边距，避免内容贴边 */
}
.close-btn {
  background: transparent;        
  border: none;
  color: #f56c6c;               
  font-size: 1.5rem;
  cursor: pointer;
}
</style>
