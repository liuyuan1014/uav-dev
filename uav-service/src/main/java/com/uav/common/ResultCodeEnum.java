package com.uav.common;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    FEIGN_FAIL(207, "远程调用失败"),
    UPDATE_ERROR(208, "数据更新失败"),

    ARGUMENT_VALID_ERROR(210, "参数校验异常"),
    SIGN_ERROR(300, "签名错误"),
    SIGN_OVERDUE(301, "签名已过期"),
    VALIDATECODE_ERROR(218 , "验证码错误"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    ACCOUNT_ERROR(214, "账号不正确"),
    PASSWORD_ERROR(215, "密码不正确"),
    PHONE_CODE_ERROR(216, "手机验证码不正确"),
    LOGIN_MOBLE_ERROR( 217, "账号不正确"),
    ACCOUNT_STOP( 218, "账号已停用"),
    NODE_ERROR( 219, "该节点下有子节点，不可以删除"),

    // 无人机业务相关错误码
    GRAB_MISSION_FAIL( 220, "抢单失败"),
    MAP_FAIL( 221, "地图服务调用失败"),
    PROFITSHARING_FAIL( 222, "分账调用失败"),
    NO_START_SERVICE( 223, "未开启飞行服务，不能更新位置信息"),
    PILOT_START_LOCATION_DISTION_ERROR( 224, "距离任务起始点1公里以内才能确认"),
    PILOT_END_LOCATION_DISTION_ERROR( 225, "距离任务终点2公里以内才能确认"),
    IMAGE_AUDITION_FAIL( 226, "图片审核不通过"),
    AUTH_ERROR( 227, "认证通过后才可以开启飞行服务"),
    FACE_ERROR( 228, "当日未进行人脸识别"),
    
    // 无人机特有错误码
    UAV_NOT_AVAILABLE( 229, "无人机不可用"),
    WEATHER_NOT_SUITABLE( 230, "天气条件不适合飞行"),
    AIRSPACE_RESTRICTED( 231, "禁飞区域"),
    BATTERY_LOW( 232, "电量不足"),
    PILOT_LICENSE_EXPIRED( 233, "飞手执照已过期"),

    COUPON_EXPIRE( 250, "优惠券已过期"),
    COUPON_LESS( 251, "优惠券库存不足"),
    COUPON_USER_LIMIT( 252, "超出领取数量"),
    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}