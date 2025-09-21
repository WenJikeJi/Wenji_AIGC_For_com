from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import create_engine, text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from dotenv import load_dotenv
import os
import logging
from datetime import datetime
import pymysql

# 加载环境变量
load_dotenv()

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    filename=os.getenv('LOG_FILE_PATH', 'app.log')
)
logger = logging.getLogger(__name__)

# 数据库配置
DATABASE_URL = f"mysql+pymysql://{os.getenv('DB_USERNAME')}:{os.getenv('DB_PASSWORD')}@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}?charset=utf8mb4"

# 创建数据库引擎
engine = create_engine(DATABASE_URL)

# 创建会话工厂
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 创建基类
Base = declarative_base()

# 依赖项：获取数据库会话
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# 初始化FastAPI应用
app = FastAPI(title="Wenji Python Data Service", version="1.0")

# 配置CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 根路由
@app.get("/")
def read_root():
    return {"status": "success", "message": "Wenji Python Data Service is running"}

# 健康检查路由
@app.get("/health")
def health_check():
    try:
        # 测试数据库连接
        db = next(get_db())
        result = db.execute(text("SELECT 1"))
        # 尝试列出所有数据库，确认连接正常
        databases = db.execute(text("SHOW DATABASES"))
        db_list = [row[0] for row in databases]
        return {
            "status": "healthy", 
            "database": "connected", 
            "available_databases": db_list,
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        logger.error(f"Health check failed: {str(e)}")
        return {
            "status": "unhealthy", 
            "database": "disconnected", 
            "error": str(e),
            "connection_string": f"mysql+pymysql://{os.getenv('DB_USERNAME')}:****@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/"
        }

# 导入路由模块
# from .routes import log_router
from .routes import log_router

# 注册路由
app.include_router(log_router, prefix="/logs", tags=["Logs"])

# 添加初始化数据库的函数
def init_db():
    try:
        # 创建所有表
        Base.metadata.create_all(bind=engine)
        logger.info("Database tables created successfully")
    except Exception as e:
        logger.error(f"Failed to create database tables: {str(e)}")

if __name__ == "__main__":
    # 初始化数据库
    session = next(get_db())
    try:
        # 检查是否连接到正确的数据库
        current_db = session.execute(text("SELECT DATABASE()")).scalar()
        logger.info(f"Connected to database: {current_db}")
        
        # 尝试创建表（如果需要）
        init_db()
    finally:
        session.close()
        
    # 启动服务器
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.getenv("SERVER_PORT", 8000)))