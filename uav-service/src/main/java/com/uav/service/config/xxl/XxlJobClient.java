package com.uav.service.config.xxl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * XXL-JOB任务管理客户端
 * 用于调用XXL-JOB Admin的API接口进行任务管理
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@Component
public class XxlJobClient {

    @Autowired
    private XxlJobClientConfig config;

    /**
     * 登录后的Cookie，用于后续API调用
     */
    private String cookie;

    /**
     * 初始化时自动登录
     */
    @PostConstruct
    public void init() {
        try {
            login();
            log.info("XXL-JOB客户端初始化成功");
        } catch (Exception e) {
            log.error("XXL-JOB客户端初始化失败", e);
        }
    }

    /**
     * 登录XXL-JOB调度中心
     * 获取Cookie用于后续API调用
     */
    public void login() {
        String url = config.getBaseUrl() + "/login";
        
        Map<String, Object> params = new HashMap<>();
        params.put("userName", config.getUsername());
        params.put("password", config.getPassword());

        try {
            HttpResponse response = HttpRequest.post(url)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                this.cookie = response.getCookieValue("XXL_JOB_LOGIN_IDENTITY");
                log.info("XXL-JOB登录成功");
            } else {
                log.error("XXL-JOB登录失败: {}", response.body());
            }
        } catch (Exception e) {
            log.error("XXL-JOB登录异常", e);
        }
    }

    /**
     * 添加任务
     * 
     * @param jobGroup 执行器ID
     * @param jobDesc 任务描述
     * @param scheduleConf Cron表达式
     * @param executorHandler JobHandler名称
     * @param executorParam 任务参数（JSON格式）
     * @return 任务ID，失败返回null
     */
    public Integer addJob(Integer jobGroup, String jobDesc, String scheduleConf, 
                         String executorHandler, String executorParam) {
        String url = config.getBaseUrl() + "/jobinfo/add";
        
        Map<String, Object> params = new HashMap<>();
        params.put("jobGroup", jobGroup);
        params.put("jobDesc", jobDesc);
        params.put("scheduleType", "CRON");
        params.put("scheduleConf", scheduleConf);
        params.put("glueType", "BEAN");
        params.put("executorHandler", executorHandler);
        params.put("executorParam", executorParam);
        params.put("executorRouteStrategy", "FIRST");
        params.put("misfireStrategy", "DO_NOTHING");
        params.put("executorBlockStrategy", "SERIAL_EXECUTION");
        params.put("executorTimeout", 0);
        params.put("executorFailRetryCount", 0);
        params.put("glueRemark", "GLUE代码初始化");
        params.put("author", "system");

        try {
            HttpResponse response = HttpRequest.post(url)
                    .cookie("XXL_JOB_LOGIN_IDENTITY=" + cookie)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                if (result.getInt("code") == 200) {
                    Integer jobId = result.getInt("content");
                    log.info("添加任务成功, jobId: {}", jobId);
                    return jobId;
                } else {
                    log.error("添加任务失败: {}", result.getStr("msg"));
                }
            }
        } catch (Exception e) {
            log.error("添加任务异常", e);
        }
        return null;
    }

    /**
     * 启动任务
     * 
     * @param jobId 任务ID
     * @return 是否成功
     */
    public boolean startJob(Integer jobId) {
        String url = config.getBaseUrl() + "/jobinfo/start";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", jobId);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .cookie("XXL_JOB_LOGIN_IDENTITY=" + cookie)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                if (result.getInt("code") == 200) {
                    log.info("启动任务成功, jobId: {}", jobId);
                    return true;
                } else {
                    log.error("启动任务失败: {}", result.getStr("msg"));
                }
            }
        } catch (Exception e) {
            log.error("启动任务异常", e);
        }
        return false;
    }

    /**
     * 停止任务
     * 
     * @param jobId 任务ID
     * @return 是否成功
     */
    public boolean stopJob(Integer jobId) {
        String url = config.getBaseUrl() + "/jobinfo/stop";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", jobId);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .cookie("XXL_JOB_LOGIN_IDENTITY=" + cookie)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                if (result.getInt("code") == 200) {
                    log.info("停止任务成功, jobId: {}", jobId);
                    return true;
                } else {
                    log.error("停止任务失败: {}", result.getStr("msg"));
                }
            }
        } catch (Exception e) {
            log.error("停止任务异常", e);
        }
        return false;
    }

    /**
     * 删除任务
     * 
     * @param jobId 任务ID
     * @return 是否成功
     */
    public boolean removeJob(Integer jobId) {
        String url = config.getBaseUrl() + "/jobinfo/remove";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", jobId);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .cookie("XXL_JOB_LOGIN_IDENTITY=" + cookie)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                if (result.getInt("code") == 200) {
                    log.info("删除任务成功, jobId: {}", jobId);
                    return true;
                } else {
                    log.error("删除任务失败: {}", result.getStr("msg"));
                }
            }
        } catch (Exception e) {
            log.error("删除任务异常", e);
        }
        return false;
    }

    /**
     * 触发任务执行一次
     * 
     * @param jobId 任务ID
     * @param executorParam 执行参数
     * @return 是否成功
     */
    public boolean triggerJob(Integer jobId, String executorParam) {
        String url = config.getBaseUrl() + "/jobinfo/trigger";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", jobId);
        params.put("executorParam", executorParam);
        params.put("addressList", "");

        try {
            HttpResponse response = HttpRequest.post(url)
                    .cookie("XXL_JOB_LOGIN_IDENTITY=" + cookie)
                    .form(params)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                if (result.getInt("code") == 200) {
                    log.info("触发任务成功, jobId: {}", jobId);
                    return true;
                } else {
                    log.error("触发任务失败: {}", result.getStr("msg"));
                }
            }
        } catch (Exception e) {
            log.error("触发任务异常", e);
        }
        return false;
    }
}