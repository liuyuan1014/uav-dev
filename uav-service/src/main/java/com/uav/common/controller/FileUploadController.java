package com.uav.common.controller;

import com.uav.common.Result;
import com.uav.common.service.CosStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/common/upload")
public class FileUploadController {

    @Autowired
    private CosStorageService cosStorageService;

    /**
     * 通用文件上传
     *
     * @param file 文件
     * @param folder 文件夹（可选，默认为temp）
     * @return 文件URL
     */
    @PostMapping
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "temp") String folder) {
        try {
            String fileUrl = cosStorageService.uploadFile(file, folder);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", String.valueOf(file.getSize()));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传身份证正面照片
     *
     * @param file 身份证正面照片
     * @return 文件URL
     */
    @PostMapping("/idcard/front")
    public Result<Map<String, String>> uploadIdCardFront(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = cosStorageService.uploadIdCard(file, "front");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("type", "idcard_front");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("身份证正面照片上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传身份证反面照片
     *
     * @param file 身份证反面照片
     * @return 文件URL
     */
    @PostMapping("/idcard/back")
    public Result<Map<String, String>> uploadIdCardBack(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = cosStorageService.uploadIdCard(file, "back");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("type", "idcard_back");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("身份证反面照片上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传飞手执照照片
     *
     * @param file 执照照片
     * @return 文件URL
     */
    @PostMapping("/license")
    public Result<Map<String, String>> uploadLicense(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = cosStorageService.uploadLicense(file);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("type", "license");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("执照照片上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @DeleteMapping
    public Result<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            cosStorageService.deleteFile(fileUrl);
            return Result.success("文件删除成功");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error(e.getMessage());
        }
    }
}