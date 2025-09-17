<template>
  <div class="body">
    <div class="main-box">
        <div class="left">
        </div>
        <div class="right">
            <h2>登录</h2>
            <form action="">
                <div class="input-box">
                    <input type="text" placeholder="用户名" />
                </div>
                <div class="input-box">
                    <input type="password" placeholder="密码" />
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
// 目前登录功能未实现
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
  margin-top: 2.5rem;
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
</style>
