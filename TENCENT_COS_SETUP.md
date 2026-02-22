# 腾讯云COS配置指南

## 1. 开通腾讯云COS服务

### 1.1 登录腾讯云控制台
访问：https://console.cloud.tencent.com/

### 1.2 开通对象存储COS
1. 搜索"对象存储COS"
2. 点击"立即开通"
3. 同意服务协议

### 1.3 创建存储桶
1. 进入COS控制台
2. 点击"存储桶列表" → "创建存储桶"
3. 配置信息：
   - **名称**：uav-pilot（需要全局唯一，系统会自动添加APPID后缀）
   - **所属地域**：广州（ap-guangzhou）
   - **访问权限**：私有读写
   - **存储桶标签**：可选
4. 点击"创建"

## 2. 获取访问密钥

### 2.1 创建子账号（推荐）
为了安全，建议创建子账号并授予最小权限：

1. 访问"访问管理CAM"：https://console.cloud.tencent.com/cam
2. 点击"用户" → "新建用户" → "自定义创建"
3. 选择"可访问资源并接收消息"
4. 填写用户信息：
   - 用户名：uav-cos-user
   - 访问方式：勾选"编程访问"
5. 设置权限：
   - 搜索"QcloudCOSDataFullControl"
   - 勾选该策略（COS数据读写权限）
6. 完成创建，**保存SecretId和SecretKey**

### 2.2 使用主账号（不推荐）
1. 访问"访问密钥"：https://console.cloud.tencent.com/cam/capi
2. 点击"新建密钥"
3. **保存SecretId和SecretKey**

## 3. 配置应用

### 3.1 修改 application.yml
```yaml
tencent:
  cos:
    secret-id: ${TENCENT_COS_SECRET_ID:your-secret-id}
    secret-key: ${TENCENT_COS_SECRET_KEY:your-secret-key}
    region: ap-guangzhou
    bucket-name: uav-pilot-1234567890  # 替换为实际的bucket名称（包含APPID后缀）
    base-url: https://uav-pilot-1234567890.cos.ap-guangzhou.myqcloud.com
    expire-time: 3600
```

### 3.2 设置环境变量（推荐）
为了安全，建议使用环境变量：

**Windows:**
```cmd
set TENCENT_COS_SECRET_ID=your-secret-id
set TENCENT_COS_SECRET_KEY=your-secret-key
```

**Linux/Mac:**
```bash
export TENCENT_COS_SECRET_ID=your-secret-id
export TENCENT_COS_SECRET_KEY=your-secret-key
```

**IDEA运行配置:**
1. Run → Edit Configurations
2. Environment variables: 
   ```
   TENCENT_COS_SECRET_ID=your-secret-id;TENCENT_COS_SECRET_KEY=your-secret-key
   ```

## 4. 配置防盗链（可选）

1. 进入存储桶 → 安全管理 → 防盗链
2. 添加白名单域名：
   ```
   https://yourdomain.com
   http://localhost:8081
   ```

## 5. 配置跨域CORS（可选）

1. 进入存储桶 → 安全管理 → 跨域访问CORS设置
2. 添加规则：
   ```json
   {
     "allowedOrigins": ["https://yourdomain.com", "http://localhost:8081"],
     "allowedMethods": ["GET", "POST", "PUT", "DELETE"],
     "allowedHeaders": ["*"],
     "exposeHeaders": ["ETag"],
     "maxAgeSeconds": 3600
   }
   ```

## 6. API接口说明

### 6.1 通用文件上传
```
POST /api/common/upload
Content-Type: multipart/form-data

参数：
- file: 文件（必填）
- folder: 文件夹名称（可选，默认temp）

返回：
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "https://...",
    "fileName": "example.jpg",
    "fileSize": "102400"
  }
}
```

### 6.2 上传身份证正面
```
POST /api/common/upload/idcard/front
Content-Type: multipart/form-data

参数：
- file: 身份证正面照片（必填）

返回：
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "https://...",
    "type": "idcard_front"
  }
}
```

### 6.3 上传身份证反面
```
POST /api/common/upload/idcard/back
Content-Type: multipart/form-data

参数：
- file: 身份证反面照片（必填）
```

### 6.4 上传飞手执照
```
POST /api/common/upload/license
Content-Type: multipart/form-data

参数：
- file: 执照照片（必填）
```

### 6.5 删除文件
```
DELETE /api/common/upload?fileUrl=https://...
```

## 7. 文件限制

- **支持格式**：jpg, jpeg, png, pdf
- **最大大小**：5MB
- **签名URL有效期**：1小时

## 8. 成本估算

### 存储费用
- 标准存储：0.118元/GB/月

### 流量费用
- 外网下行流量：0.5元/GB

### 请求费用
- 读请求：0.01元/万次

### 示例（1000个飞手）
- 存储：6GB × 0.118元 = 0.71元/月
- 流量：60GB × 0.5元 = 30元/月
- 请求：10万次 × 0.01元 = 0.1元/月
- **总计**：约31元/月

## 9. 安全建议

1. ✅ 使用子账号，不要使用主账号密钥
2. ✅ 使用环境变量存储密钥，不要硬编码
3. ✅ 设置存储桶为私有读写
4. ✅ 使用签名URL访问文件
5. ✅ 配置防盗链和CORS
6. ✅ 定期轮换密钥
7. ✅ 监控异常访问

## 10. 常见问题

### Q1: 上传失败，提示"NoSuchBucket"
A: 检查bucket-name是否正确，需要包含APPID后缀

### Q2: 上传成功但无法访问
A: 检查存储桶权限是否为私有读写，使用签名URL访问

### Q3: 签名URL过期
A: 默认1小时过期，可以调整expire-time配置

### Q4: 跨域问题
A: 配置CORS规则，添加允许的域名

## 11. 测试

使用Postman或curl测试上传接口：

```bash
curl -X POST http://localhost:8081/api/common/upload/idcard/front \
  -F "file=@/path/to/idcard.jpg"
```

## 12. 监控

1. 进入COS控制台 → 数据监控
2. 查看：
   - 存储量
   - 请求次数
   - 流量统计
   - 错误率

## 13. 备份

建议开启版本控制和跨地域复制：
1. 存储桶 → 容错与容灾 → 版本控制
2. 存储桶 → 容错与容灾 → 跨地域复制