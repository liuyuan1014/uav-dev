# 飞手管理API测试文档

## 前置条件

1. 确保 `uav-service` 服务已启动（默认端口：8080）
2. 确保数据库已执行 `add_pilot_certification_fields.sql` 脚本
3. 确保腾讯云COS配置正确（参考 `TENCENT_COS_SETUP.md`）

## API接口列表

### 1. 上传身份证照片

**接口**: `POST /api/common/upload/idcard/front`

**说明**: 上传身份证正面照片

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/common/upload/idcard/front \
  -F "file=@/path/to/idcard_front.jpg"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "pilot/idcard/20260222/abc123.jpg"
}
```

---

### 2. 上传身份证反面照片

**接口**: `POST /api/common/upload/idcard/back`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/common/upload/idcard/back \
  -F "file=@/path/to/idcard_back.jpg"
```

---

### 3. 上传驾驶证照片

**接口**: `POST /api/common/upload/license`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/common/upload/license \
  -F "file=@/path/to/license.jpg"
```

---

### 4. 提交飞手认证信息

**接口**: `POST /api/pilot/info/auth/update`

**说明**: 飞手提交认证信息，状态会自动变为"审核中"

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/pilot/info/auth/update \
  -H "Content-Type: application/json" \
  -d '{
    "pilotId": 1,
    "name": "张三",
    "gender": "1",
    "birthday": "1990-01-01",
    "idcardNo": "110101199001011234",
    "idcardAddress": "北京市朝阳区",
    "idcardExpire": "2030-12-31",
    "idcardFrontUrl": "pilot/idcard/20260222/front_abc123.jpg",
    "idcardBackUrl": "pilot/idcard/20260222/back_abc123.jpg",
    "idcardHandUrl": "pilot/idcard/20260222/hand_abc123.jpg",
    "driverLicenseNo": "110101202600001",
    "driverLicenseClass": "C1",
    "driverLicenseIssueDate": "2020-01-01",
    "driverLicenseExpire": "2026-01-01",
    "driverLicenseFrontUrl": "pilot/license/20260222/front_xyz789.jpg",
    "driverLicenseBackUrl": "pilot/license/20260222/back_xyz789.jpg",
    "driverLicenseHandUrl": "pilot/license/20260222/hand_xyz789.jpg",
    "contactPhone": "13800138000",
    "contactAddress": "北京市朝阳区某某街道"
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 5. 获取飞手认证信息

**接口**: `GET /api/pilot/info/auth/{pilotId}`

**说明**: 查看飞手的认证信息和照片（带签名URL）

**请求示例**:
```bash
curl -X GET http://localhost:8080/api/pilot/info/auth/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "张三",
    "gender": "1",
    "birthday": "1990-01-01",
    "idcardNo": "110101199001011234",
    "idcardAddress": "北京市朝阳区",
    "idcardExpire": "2030-12-31",
    "idcardFrontUrl": "pilot/idcard/20260222/front_abc123.jpg",
    "idcardFrontShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/idcard/20260222/front_abc123.jpg?sign=xxx",
    "idcardBackUrl": "pilot/idcard/20260222/back_abc123.jpg",
    "idcardBackShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/idcard/20260222/back_abc123.jpg?sign=xxx",
    "idcardHandUrl": "pilot/idcard/20260222/hand_abc123.jpg",
    "idcardHandShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/idcard/20260222/hand_abc123.jpg?sign=xxx",
    "driverLicenseNo": "110101202600001",
    "driverLicenseClass": "C1",
    "driverLicenseIssueDate": "2020-01-01",
    "driverLicenseExpire": "2026-01-01",
    "driverLicenseFrontUrl": "pilot/license/20260222/front_xyz789.jpg",
    "driverLicenseFrontShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/license/20260222/front_xyz789.jpg?sign=xxx",
    "driverLicenseBackUrl": "pilot/license/20260222/back_xyz789.jpg",
    "driverLicenseBackShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/license/20260222/back_xyz789.jpg?sign=xxx",
    "driverLicenseHandUrl": "pilot/license/20260222/hand_xyz789.jpg",
    "driverLicenseHandShowUrl": "https://xxx.cos.ap-beijing.myqcloud.com/pilot/license/20260222/hand_xyz789.jpg?sign=xxx",
    "authStatus": 1,
    "contactPhone": "13800138000",
    "contactAddress": "北京市朝阳区某某街道"
  }
}
```

**认证状态说明**:
- `0`: 未认证
- `1`: 审核中
- `2`: 已认证
- `3`: 认证失败

---

### 6. 审核飞手认证（管理员）

**接口**: `POST /api/pilot/info/auth/audit`

**说明**: 管理员审核飞手认证，通过或拒绝

**审核通过示例**:
```bash
curl -X POST "http://localhost:8080/api/pilot/info/auth/audit?pilotId=1&status=2"
```

**审核拒绝示例**:
```bash
curl -X POST "http://localhost:8080/api/pilot/info/auth/audit?pilotId=1&status=3&rejectReason=身份证照片不清晰"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 7. 获取飞手基本信息

**接口**: `GET /api/pilot/info/{pilotId}`

**说明**: 获取飞手的基本信息，包括驾龄、完成任务数、平均评分

**请求示例**:
```bash
curl -X GET http://localhost:8080/api/pilot/info/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "张三",
    "gender": "1",
    "contactPhone": "13800138000",
    "driverLicenseClass": "C1",
    "driverLicenseAge": 6,
    "authStatus": 2,
    "avgRating": 4.8,
    "completedMissions": 25
  }
}
```

---

## 完整测试流程

### 步骤1: 准备测试数据

首先在数据库中插入一个测试飞手：

```sql
INSERT INTO uav_pilot (name, phone, auth_status, create_time, update_time) 
VALUES ('测试飞手', '13800138000', 0, NOW(), NOW());
```

记录返回的飞手ID（假设为1）。

### 步骤2: 上传照片

```bash
# 上传身份证正面
curl -X POST http://localhost:8080/api/common/upload/idcard/front \
  -F "file=@idcard_front.jpg"
# 返回: "pilot/idcard/20260222/xxx.jpg"

# 上传身份证反面
curl -X POST http://localhost:8080/api/common/upload/idcard/back \
  -F "file=@idcard_back.jpg"

# 上传手持身份证
curl -X POST http://localhost:8080/api/common/upload/idcard/front \
  -F "file=@idcard_hand.jpg"

# 上传驾驶证正面
curl -X POST http://localhost:8080/api/common/upload/license \
  -F "file=@license_front.jpg"

# 上传驾驶证反面
curl -X POST http://localhost:8080/api/common/upload/license \
  -F "file=@license_back.jpg"

# 上传手持驾驶证
curl -X POST http://localhost:8080/api/common/upload/license \
  -F "file=@license_hand.jpg"
```

### 步骤3: 提交认证信息

使用步骤2返回的URL，提交认证信息：

```bash
curl -X POST http://localhost:8080/api/pilot/info/auth/update \
  -H "Content-Type: application/json" \
  -d '{
    "pilotId": 1,
    "name": "张三",
    "gender": "1",
    "birthday": "1990-01-01",
    "idcardNo": "110101199001011234",
    "idcardAddress": "北京市朝阳区",
    "idcardExpire": "2030-12-31",
    "idcardFrontUrl": "pilot/idcard/20260222/xxx_front.jpg",
    "idcardBackUrl": "pilot/idcard/20260222/xxx_back.jpg",
    "idcardHandUrl": "pilot/idcard/20260222/xxx_hand.jpg",
    "driverLicenseNo": "110101202600001",
    "driverLicenseClass": "C1",
    "driverLicenseIssueDate": "2020-01-01",
    "driverLicenseExpire": "2026-01-01",
    "driverLicenseFrontUrl": "pilot/license/20260222/yyy_front.jpg",
    "driverLicenseBackUrl": "pilot/license/20260222/yyy_back.jpg",
    "driverLicenseHandUrl": "pilot/license/20260222/yyy_hand.jpg",
    "contactPhone": "13800138000",
    "contactAddress": "北京市朝阳区某某街道"
  }'
```

### 步骤4: 查看认证信息

```bash
curl -X GET http://localhost:8080/api/pilot/info/auth/1
```

此时 `authStatus` 应该是 `1`（审核中）。

### 步骤5: 管理员审核

**审核通过**:
```bash
curl -X POST "http://localhost:8080/api/pilot/info/auth/audit?pilotId=1&status=2"
```

**或审核拒绝**:
```bash
curl -X POST "http://localhost:8080/api/pilot/info/auth/audit?pilotId=1&status=3&rejectReason=照片不清晰，请重新上传"
```

### 步骤6: 查看飞手基本信息

```bash
curl -X GET http://localhost:8080/api/pilot/info/1
```

---

## 数据库验证

### 查看飞手认证状态

```sql
SELECT id, name, auth_status, idcard_no, driver_license_no, create_time, update_time
FROM uav_pilot
WHERE id = 1;
```

### 查看认证审核记录

```sql
SELECT * FROM pilot_certification_audit
WHERE pilot_id = 1
ORDER BY audit_time DESC;
```

---

## 常见问题

### 1. 文件上传失败

**错误**: "文件类型不支持"
- **原因**: 只支持 jpg、jpeg、png、pdf 格式
- **解决**: 检查文件格式

**错误**: "文件大小超过限制"
- **原因**: 文件大小超过5MB
- **解决**: 压缩图片或使用更小的文件

### 2. 认证信息提交失败

**错误**: "当前状态不允许修改认证信息"
- **原因**: 飞手已经在审核中或已认证
- **解决**: 只有未认证(0)或认证失败(3)状态才能提交

### 3. 审核失败

**错误**: "飞手当前状态不是审核中，无法审核"
- **原因**: 只有审核中(1)的飞手才能审核
- **解决**: 确保飞手已提交认证信息

**错误**: "拒绝认证必须提供拒绝原因"
- **原因**: status=3时必须提供rejectReason
- **解决**: 添加rejectReason参数

---

## Postman导入

可以将以下JSON导入Postman进行测试：

```json
{
  "info": {
    "name": "UAV飞手管理API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "上传身份证正面",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "formdata",
          "formdata": [
            {
              "key": "file",
              "type": "file",
              "src": []
            }
          ]
        },
        "url": {
          "raw": "http://localhost:8080/api/common/upload/idcard/front",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "common", "upload", "idcard", "front"]
        }
      }
    },
    {
      "name": "提交飞手认证",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"pilotId\": 1,\n  \"name\": \"张三\",\n  \"gender\": \"1\",\n  \"birthday\": \"1990-01-01\",\n  \"idcardNo\": \"110101199001011234\",\n  \"idcardAddress\": \"北京市朝阳区\",\n  \"idcardExpire\": \"2030-12-31\",\n  \"idcardFrontUrl\": \"pilot/idcard/20260222/front.jpg\",\n  \"idcardBackUrl\": \"pilot/idcard/20260222/back.jpg\",\n  \"idcardHandUrl\": \"pilot/idcard/20260222/hand.jpg\",\n  \"driverLicenseNo\": \"110101202600001\",\n  \"driverLicenseClass\": \"C1\",\n  \"driverLicenseIssueDate\": \"2020-01-01\",\n  \"driverLicenseExpire\": \"2026-01-01\",\n  \"driverLicenseFrontUrl\": \"pilot/license/20260222/front.jpg\",\n  \"driverLicenseBackUrl\": \"pilot/license/20260222/back.jpg\",\n  \"driverLicenseHandUrl\": \"pilot/license/20260222/hand.jpg\",\n  \"contactPhone\": \"13800138000\",\n  \"contactAddress\": \"北京市朝阳区\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/pilot/info/auth/update",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "pilot", "info", "auth", "update"]
        }
      }
    },
    {
      "name": "获取飞手认证信息",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/pilot/info/auth/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "pilot", "info", "auth", "1"]
        }
      }
    },
    {
      "name": "审核飞手认证-通过",
      "request": {
        "method": "POST",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/pilot/info/auth/audit?pilotId=1&status=2",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "pilot", "info", "auth", "audit"],
          "query": [
            {
              "key": "pilotId",
              "value": "1"
            },
            {
              "key": "status",
              "value": "2"
            }
          ]
        }
      }
    },
    {
      "name": "获取飞手基本信息",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/pilot/info/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "pilot", "info", "1"]
        }
      }
    }
  ]
}
```

---

## 下一步

完成测试后，可以继续开发：
1. 飞手列表查询（分页、筛选）
2. 评分系统
3. 飞手统计报表