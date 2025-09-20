# Wenji Python Data Service

这是Wenji社交媒体管理系统的Python数据服务，主要负责数据库操作和统一的用户操作日志收集。

## 功能特性

- 提供统一的用户操作日志收集API
- 支持PostgreSQL数据库连接
- 使用FastAPI框架，高性能、易于扩展
- 包含完整的日志管理功能（记录、查询、清理、统计）

## 项目结构

```
Date_python/
├── main.py             # FastAPI主应用文件
├── .env                # 环境变量配置
├── requirements.txt    # Python依赖列表
├── start_server.bat    # Windows启动脚本
├── models/             # 数据库模型
│   ├── __init__.py
│   ├── user.py         # 用户账户模型
│   └── log.py          # 操作日志模型
├── services/           # 业务服务
│   ├── __init__.py
│   └── log_service.py  # 日志服务
├── routes/             # API路由
│   ├── __init__.py
│   └── log_router.py   # 日志API路由
└── utils/              # 工具类
    ├── __init__.py
    └── log_client.py   # 日志客户端工具
```

## 环境变量配置

在`.env`文件中配置以下环境变量：

```
# PostgreSQL数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=wenji_db
DB_USER=postgres
DB_PASSWORD=password

# 服务配置
SERVER_PORT=8000

# 加密配置
ENCRYPTION_KEY=WenJi@2024$Encryption

# JWT配置
JWT_SECRET_KEY=WenJi@2024$JWTSecret
JWT_ALGORITHM=HS256
JWT_ACCESS_TOKEN_EXPIRE_MINUTES=30

# 日志配置
LOG_FILE_PATH=app.log

# 日志服务配置
LOG_SERVICE_URL=http://localhost:8000/api/logs
LOG_SERVICE_TIMEOUT=5
```

## 安装依赖

```bash
pip install -r requirements.txt
```

## 启动服务

### Windows

直接双击`start_server.bat`文件，或在命令行中运行：

```bash
start_server.bat
```

### 命令行

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## API接口文档

服务启动后，访问以下地址查看自动生成的API文档：

- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 日志服务API

### 记录操作日志

```
POST /api/logs/record
```

**参数：**
- `user_id` (int): 用户ID
- `operation` (string): 操作描述
- `details` (string, 可选): 操作详情
- `encryption_status` (int, 可选): 加密状态（0=未加密，1=加密，默认0）

**返回：**
```json
{
  "status": "success",
  "message": "日志记录成功",
  "log_id": 1
}
```

### 获取用户日志

```
GET /api/logs/user/{user_id}
```

**参数：**
- `user_id` (int): 用户ID
- `operation_type` (string, 可选): 操作类型筛选
- `start_time` (datetime, 可选): 开始时间
- `end_time` (datetime, 可选): 结束时间
- `skip` (int, 可选): 跳过的记录数（默认0）
- `limit` (int, 可选): 返回的记录数（默认50，最大200）

**返回：**
```json
{
  "status": "success",
  "data": {
    "total": 100,
    "skip": 0,
    "limit": 50,
    "logs": [
      {
        "id": 1,
        "user_id": 1,
        "operation": "用户登录",
        "operation_time": "2024-05-01T10:00:00",
        "operation_ip": "127.0.0.1",
        "operation_address": "localhost",
        "details": "用户通过Web端登录",
        "encryption_status": 1
      }
    ]
  }
}
```

### 获取所有日志

```
GET /api/logs/all
```

**参数：**
- `user_id` (int, 可选): 用户ID筛选
- `operation_type` (string, 可选): 操作类型筛选
- `start_time` (datetime, 可选): 开始时间
- `end_time` (datetime, 可选): 结束时间
- `skip` (int, 可选): 跳过的记录数（默认0）
- `limit` (int, 可选): 返回的记录数（默认50，最大200）

### 清理过期日志

```
DELETE /api/logs/cleanup
```

**参数：**
- `days` (int, 可选): 清理多少天前的日志（默认90天）

### 获取日志统计

```
GET /api/logs/stats
```

**参数：**
- `user_id` (int, 可选): 用户ID筛选
- `days` (int, 可选): 统计多少天内的日志（默认30天）

## 日志客户端使用

Python客户端可以直接使用`LogClient`工具类：

```python
from utils.log_client import log_client

# 记录日志
log_client.record_operation(
    user_id=1,
    operation="用户登录",
    details="用户通过Web端登录",
    encryption_status=1
)

# 获取日志
logs = log_client.get_user_logs(user_id=1, limit=10)
print(logs)
```

## Java后端对接指南

Java后端需要移除原有的日志收集功能，统一通过HTTP请求调用此Python服务的日志API。

### 调用示例（Java）

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

public class PythonLogServiceClient {
    private static final String LOG_SERVICE_URL = "http://localhost:8000/api/logs";
    private final HttpClient client;
    private final Gson gson;
    
    public PythonLogServiceClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
    
    public boolean recordLog(int userId, String operation, String details, int encryptionStatus) {
        try {
            // 构建查询参数
            String params = String.format(
                "user_id=%d&operation=%s&details=%s&encryption_status=%d",
                userId,
                java.net.URLEncoder.encode(operation, "UTF-8"),
                java.net.URLEncoder.encode(details != null ? details : "", "UTF-8"),
                encryptionStatus
            );
            
            // 构建请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOG_SERVICE_URL + "/record?" + params))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            
            // 发送请求
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // 处理响应
            if (response.statusCode() == 200) {
                Map<String, Object> result = gson.fromJson(response.body(), Map.class);
                return "success".equals(result.get("status"));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

## 部署说明

1. 确保已安装Python 3.8+和PostgreSQL数据库
2. 配置.env文件中的数据库连接信息
3. 安装依赖：`pip install -r requirements.txt`
4. 启动服务：`uvicorn main:app --host 0.0.0.0 --port 8000`
5. 在生产环境中，建议使用Gunicorn等WSGI服务器部署

## 注意事项

- 请确保.env文件中的敏感信息（如数据库密码、加密密钥）得到妥善保管
- 生产环境中应关闭调试模式（--reload参数）
- 建议配置反向代理（如Nginx）以提高安全性和性能
- 定期备份数据库以防止数据丢失