# 文姬项目后端数据库配置和启动指南

本指南将帮助您配置和启动文姬项目的所有后端服务，并确保它们能正确连接到数据库。

## 已完成的工作

1. ✅ 创建了 `wenji_db` 数据库
2. ✅ 初始化了所有必要的数据库表
3. ✅ 配置了 Python 后端服务的数据库连接
4. ✅ 创建了 Java 后端服务的启动脚本
5. ✅ 提供了数据库连接测试工具

## 数据库配置状态

所有后端服务现在都配置为连接到以下数据库：

- **主机**: localhost
- **端口**: 3306
- **数据库名**: wenji_db
- **用户名**: root
- **密码**: Wenguang-1122

## 测试 Python 后端服务

Python 后端服务位于 `Date_python` 目录，用于数据处理服务。

### 测试数据库连接

```bash
cd c:/Word_Wenji/dev/Date_python
python test_db_connection.py
```

这个脚本会验证 Python 后端是否能正确连接到数据库，并显示数据库中的表信息。

### 启动 Python 后端服务

```bash
cd c:/Word_Wenji/dev/Date_python
start_server.bat
```

服务将在 http://localhost:8000 启动。

## 启动 Java 后端服务

Java 后端服务位于 `Server` 目录，是主要的业务逻辑服务。

```bash
cd c:/Word_Wenji/dev/Server
start_server.bat
```

服务将在 http://localhost:8080 启动。启动脚本中已设置了必要的数据库连接环境变量。

## 启动 Node.js 数据服务器

Node.js 数据服务器位于 `Data_server` 目录，用于数据库初始化和管理。

```bash
cd c:/Word_Wenji/dev/Data_server
node index.js  # 如果有主入口文件的话
```

## 前端服务

前端服务位于 `web` 目录，使用 Vue.js 开发。

```bash
cd c:/Word_Wenji/dev/web
npm install
npm run dev
```

## 常见问题解决

### 数据库连接失败

如果遇到数据库连接失败，请检查以下事项：

1. **MySQL 服务是否运行**：
   - 打开服务管理器，确认 MySQL 服务正在运行
   - 或使用命令 `services.msc` 查看服务状态

2. **数据库凭证是否正确**：
   - 检查各后端服务的 `.env` 文件或启动脚本中的数据库用户名和密码
   - 确保使用的是正确的凭证（当前配置为 root 用户，密码 Wenguang-1122）

3. **数据库权限**：
   - 确认 root 用户有足够的权限访问 `wenji_db` 数据库

4. **防火墙设置**：
   - 确保防火墙没有阻止数据库连接

### 端口冲突

如果启动服务时遇到端口被占用的情况：

- 对于 Python 服务：修改 `Date_python/.env` 文件中的 `SERVER_PORT` 配置
- 对于 Java 服务：修改 `Server/start_server.bat` 文件中的 `SERVER_PORT` 环境变量
- 对于前端服务：修改 `web/vite.config.js` 中的端口配置

## 验证后端服务是否正常工作

1. Python 服务：访问 http://localhost:8000/docs 查看 API 文档
2. Java 服务：访问 http://localhost:8080/actuator/health 检查健康状态
3. 前端服务：访问 http://localhost:5173 查看前端界面

## 后续步骤

1. 运行各后端服务的测试用例确保功能正常
2. 配置前端服务连接到后端 API
3. 进行完整的系统测试和集成测试