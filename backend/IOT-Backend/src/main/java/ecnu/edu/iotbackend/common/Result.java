package ecnu.edu.iotbackend.common;

public class Result<T> {
    private Integer code;  // 200=成功，500=失败
    private String msg;
    private T data;

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功响应（不带数据）
    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        return result;
    }

    // 失败响应
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    // getter + setter（必须有，否则序列化失败）
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}