package me.aihe.blog.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:18
 * 使用场景：
 * 功能描述：
 */
@Data
public class Result<T> {
    private int code;

    private T data;

    private String msg;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功状态码
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 参数错误
     */
    private static final int PARAMETER_ERROR = 401;

    /**
     * 服务端错误
     */
    public static final Integer SERVER_ERROR = 500;

    public Result() {
    }

    public static Result error(String msg) {
        return new Result(SERVER_ERROR,msg);
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setData(data);
        return result;
    }

    public static Result paramError(String msg) {
        return new Result(PARAMETER_ERROR,msg);
    }
}
