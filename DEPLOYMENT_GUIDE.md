# 文姬AIGC项目部署指南

## 📋 目录
- [系统要求](#系统要求)
- [环境准备](#环境准备)
- [快速部署](#快速部署)
- [手动部署](#手动部署)
- [配置说明](#配置说明)
- [服务启动](#服务启动)
- [验证部署](#验证部署)
- [常见问题](#常见问题)
- [维护指南](#维护指南)

## 🔧 系统要求

### 硬件要求
- **CPU**: 2核心以上
- **内存**: 4GB以上（推荐8GB）
- **存储**: 10GB以上可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **操作系统**: Windows 10/11, Linux, macOS
- **Java**: JDK 17或更高版本
- **Node.js**: 16.x或更高版本
- **MySQL**: 8.0或更高版本
- **Maven**: 3.6或更高版本
- **Git**: 最新版本

## 🚀 环境准备

### 1. 安装Java JDK
```bash
# 验证Java安装
java -version
javac -version
```

### 2. 安装Node.js
```bash
# 验证Node.js安装
node --version
npm --version
```

### 3. 安装MySQL
- 下载并安装MySQL 8.0+
- 创建数据库用户和数据库
- 记录数据库连接信息

### 4. 安装Maven
```bash
# 验证Maven安装
mvn --version
```

## ⚡ 快速部署

### 使用自动化部署脚本

1. **克隆项目**
```bash
git clone <repository-url>
cd Wenji_AIGC_For_com
```

2. **配置环境变量**
```bash
# 复制环境配置模板
copy .env.production.example .env.production
# 编辑配置文件，填入实际的配置信息
```

3. **运行部署脚本**
```bash
# Windows
deploy.bat

# Linux/macOS
chmod +x deploy.sh
./deploy.sh
```

## 🔨 手动部署

### 1. 数据库初始化

```bash
# 进入数据库服务目录
cd Data_server

# 安装依赖
npm install

# 配置数据库连接
# 编辑 init_databases.js 中的数据库配置

# 初始化数据库
node init_databases.js
```

### 2. 后端服务部署

```bash
# 进入后端服务目录
cd Server

# 编译项目
mvn clean compile

# 打包项目
mvn package -DskipTests

# 启动服务
java -jar target/Server-1.0.0.jar
# 或使用Maven启动
mvn spring-boot:run
```

### 3. 前端服务部署

```bash
# 进入前端目录
cd web

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产环境构建
npm run build
```

### 4. Python数据服务部署

```bash
# 进入Python服务目录
cd Date_python

# 安装Python依赖
pip install -r requirements.txt

# 启动服务
python app.py
```

## ⚙️ 配置说明

### 环境变量配置 (.env.production)

```env
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=wenji_db
DB_USER=your_username
DB_PASSWORD=your_password

# JWT配置
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400

# 邮件服务配置
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password

# 服务器配置
SERVER_PORT=8080
FRONTEND_PORT=5173
PYTHON_SERVICE_PORT=5000

# 社交媒体API配置
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret

# 飞书配置
FEISHU_APP_ID=your_feishu_app_id
FEISHU_APP_SECRET=your_feishu_app_secret
```

### 数据库配置

1. **创建数据库**
```sql
CREATE DATABASE wenji_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'wenji_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON wenji_db.* TO 'wenji_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **配置连接参数**
- 确保MySQL服务正在运行
- 验证用户权限和密码
- 检查防火墙设置

## 🎯 服务启动

### 启动顺序
1. **MySQL数据库服务**
2. **数据库初始化** (首次部署)
3. **Java后端服务** (端口: 8080)
4. **Python数据服务** (端口: 5000)
5. **前端服务** (端口: 5173)

### 启动命令

```bash
# 1. 启动数据库初始化 (首次)
cd Data_server && node init_databases.js

# 2. 启动Java后端
cd Server && mvn spring-boot:run

# 3. 启动Python服务
cd Date_python && python app.py

# 4. 启动前端服务
cd web && npm run dev
```

## ✅ 验证部署

### 1. 服务健康检查

```bash
# 检查Java后端
curl http://localhost:8080/health

# 检查Python服务
curl http://localhost:5000/health

# 检查前端服务
curl http://localhost:5173
```

### 2. 功能测试

1. **访问前端页面**: http://localhost:5173
2. **测试用户登录功能**
3. **验证社交媒体管理功能**
4. **检查数据统计功能**

### 3. 日志检查

```bash
# 查看Java后端日志
tail -f Server/logs/application.log

# 查看Python服务日志
tail -f Date_python/logs/app.log

# 查看前端构建日志
npm run build
```

## 🔧 常见问题

### 数据库连接问题
```
问题: 无法连接到MySQL数据库
解决: 
1. 检查MySQL服务是否启动
2. 验证数据库配置信息
3. 检查防火墙设置
4. 确认用户权限
```

### 端口冲突问题
```
问题: 端口被占用
解决:
1. 使用 netstat -an | findstr :8080 检查端口占用
2. 修改配置文件中的端口号
3. 或停止占用端口的进程
```

### 依赖安装问题
```
问题: npm install 失败
解决:
1. 清除npm缓存: npm cache clean --force
2. 删除node_modules文件夹重新安装
3. 使用国内镜像: npm config set registry https://registry.npmmirror.com
```

### Java编译问题
```
问题: Maven编译失败
解决:
1. 检查Java版本是否符合要求
2. 清理Maven缓存: mvn clean
3. 检查网络连接和Maven仓库配置
```

## 🛠️ 维护指南

### 日常维护

1. **日志监控**
   - 定期检查应用日志
   - 监控错误和异常信息
   - 清理过期日志文件

2. **数据库维护**
   - 定期备份数据库
   - 监控数据库性能
   - 清理过期数据

3. **安全更新**
   - 定期更新依赖包
   - 检查安全漏洞
   - 更新系统补丁

### 性能优化

1. **数据库优化**
   - 添加适当的索引
   - 优化查询语句
   - 定期分析表结构

2. **应用优化**
   - 监控内存使用
   - 优化代码性能
   - 配置缓存策略

### 备份策略

1. **数据备份**
```bash
# 数据库备份
mysqldump -u username -p wenji_db > backup_$(date +%Y%m%d).sql

# 应用文件备份
tar -czf app_backup_$(date +%Y%m%d).tar.gz /path/to/app
```

2. **配置备份**
   - 备份环境配置文件
   - 备份数据库配置
   - 备份SSL证书

## 📞 技术支持

如果在部署过程中遇到问题，请：

1. 查看相关日志文件
2. 检查配置文件设置
3. 参考项目文档
4. 联系技术支持团队

---

**注意**: 在生产环境部署时，请确保：
- 使用HTTPS协议
- 配置适当的防火墙规则
- 设置强密码和安全密钥
- 定期进行安全审计和更新