package com.uav.pilot.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 腾讯云COS存储服务
 */
@Slf4j
@Service
public class CosStorageService {

    @Autowired
    private COSClient cosClient;

    @Autowired
    private TencentCosConfig cosConfig;

    /**
     * 允许上传的文件类型
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf");

    /**
     * 最大文件大小：5MB
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param folder 文件夹（如：idcard, license）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 1. 文件校验
        validateFile(file);

        // 2. 生成文件名
        String fileName = generateFileName(file.getOriginalFilename(), folder);

        // 3. 上传到COS
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosConfig.getBucketName(),
                    fileName,
                    inputStream,
                    metadata
            );

            PutObjectResult result = cosClient.putObject(putObjectRequest);
            log.info("文件上传成功，ETag: {}, 文件名: {}", result.getETag(), fileName);

            // 4. 返回文件URL（使用签名URL，更安全）
            return getSignedUrl(fileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传身份证照片（专用方法）
     *
     * @param file 身份证照片
     * @param side 正面(front)或反面(back)
     * @return 文件访问URL
     */
    public String uploadIdCard(MultipartFile file, String side) throws IOException {
        String folder = "idcard/" + side;
        return uploadFile(file, folder);
    }

    /**
     * 上传飞手执照照片（专用方法）
     *
     * @param file 执照照片
     * @return 文件访问URL
     */
    public String uploadLicense(MultipartFile file) throws IOException {
        return uploadFile(file, "license");
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取文件key
            String fileKey = extractFileKey(fileUrl);
            cosClient.deleteObject(cosConfig.getBucketName(), fileKey);
            log.info("文件删除成功，文件名: {}", fileKey);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取签名URL（临时访问链接）
     *
     * @param fileKey 文件key
     * @return 签名URL
     */
    public String getSignedUrl(String fileKey) {
        try {
            // 设置过期时间
            Date expiration = new Date(System.currentTimeMillis() + cosConfig.getExpireTime() * 1000);
            
            // 生成签名URL
            URL url = cosClient.generatePresignedUrl(
                    cosConfig.getBucketName(),
                    fileKey,
                    expiration
            );
            
            return url.toString();
        } catch (Exception e) {
            log.error("生成签名URL失败", e);
            throw new RuntimeException("生成签名URL失败：" + e.getMessage());
        }
    }

    /**
     * 文件校验
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("只支持上传 jpg、jpeg、png、pdf 格式的文件");
        }
    }

    /**
     * 生成文件名
     * 格式：folder/yyyyMMdd/uuid.ext
     */
    private String generateFileName(String originalFilename, String folder) {
        String extension = getFileExtension(originalFilename);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%s/%s.%s", folder, date, uuid, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 从URL中提取文件key
     */
    private String extractFileKey(String fileUrl) {
        // 从签名URL中提取文件key
        // 例如：https://bucket.cos.region.myqcloud.com/folder/file.jpg?sign=xxx
        // 提取：folder/file.jpg
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            // 移除开头的 /
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            log.error("提取文件key失败", e);
            throw new RuntimeException("提取文件key失败：" + e.getMessage());
        }
    }
}