package ecnu.edu.iotbackend.exception;

import ecnu.edu.iotbackend.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        e.printStackTrace();
        // 返回更详细的错误信息用于调试
        String errorMsg = "系统异常，请稍后重试";
        if (e.getMessage() != null) {
            errorMsg += ": " + e.getMessage();
        }
        // 如果是RuntimeException，显示更详细的信息
        if (e instanceof RuntimeException && e.getCause() != null) {
            errorMsg += " (原因: " + e.getCause().getMessage() + ")";
        }
        return Result.fail(errorMsg);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return Result.fail("数据异常");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("参数异常", e);
        return Result.fail("参数错误: " + e.getMessage());
    }
}
