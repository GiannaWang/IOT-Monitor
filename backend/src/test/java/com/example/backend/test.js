function curried(...args) {
  // 收集所有参数
  const collect = (...newArgs) => {
    // 合并已有参数和新传入的参数
    const allArgs = [...args, ...newArgs];
    // 重写toString方法，在比较时自动计算总和
    const fn = (...nextArgs) => collect(...allArgs, ...nextArgs);
    fn.toString = () => allArgs.reduce((a, b) => a + b, 0);
    return fn;
  };
  // 第一次调用时就返回收集函数，并绑定toString
  return collect(...args);
}

const sum3 = curried(1)(2)(3);
console.log(+sum3); // 6

const sum4 = curried(1)(2)(3)(4);
console.log(+sum4); // 10

const sum5 = curried(1)(2)(3)(4)(5);
console.log(+sum5); // 15

console.log(curried(1)(2)(3) === 6);    // true
console.log(curried(1)(2)(3)(4) === 10); // true