from fastapi import APIRouter, Request, Depends, Query, Body, HTTPException
from fastapi.security import APIKeyHeader
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from typing import Optional, List
from ..main import get_db
from ..services.log_service import LogService
from ..models.log import UserOperationLog
# from ..utils.cache import cache  # 暂时注释，模块不存在
# from ..utils.monitoring import record_metrics  # 暂时注释，模块不存在
import logging
import time

# 安全配置
API_KEY_HEADER = APIKeyHeader(name="X-API-KEY", auto_error=False)
RATE_LIMIT = 100  # 每分钟最大请求数

# 辅助函数
def validate_api_key(api_key: str) -> bool:
      """验证API Key有效性"""
      # 实际项目中应从数据库或配置中验证
      valid_keys = ["your_api_key_here", "wenji_api_key_2024", "default_api_key"]
      return api_key is not None and api_key in valid_keys

def check_rate_limit(ip: str) -> bool:
    """检查请求频率限制"""
    # 实际项目中应使用Redis等实现
    return True  # 示例实现

def record_audit_log(
    db: Session,
    user_id: str,
    operation: str,
    details: str,
    ip_address: Optional[str] = None
):
    """记录审计日志"""
    try:
        log_entry = UserOperationLog(
            user_id=user_id,
            operation=operation,
            details=details,
            operation_ip=ip_address
        )
        db.add(log_entry)
        db.commit()
    except Exception as e:
        logger.error(f"记录审计日志失败: {str(e)}")
        db.rollback()

# 创建路由实例
router = APIRouter(
    prefix="/api/logs",
    tags=["logs"],
    responses={
        404: {"description": "Not found"},
        429: {"description": "Too many requests"},
        403: {"description": "Forbidden"}
    },
    # dependencies=[Depends(record_metrics)]  # 暂时注释，模块不存在
)

logger = logging.getLogger(__name__)

# 主要的/record端点，与Java服务对应
@router.post("/record", 
    response_model=dict,
    responses={
        400: {"description": "Invalid input data"},
        500: {"description": "Internal server error"},
        429: {"description": "Too many requests"}
    },
    summary="Create a new operation log",
    description="Records user operations with details and encryption status",
    dependencies=[Depends(API_KEY_HEADER)]
)
def create_log_record(
    request: Request,
    db: Session = Depends(get_db),
    api_key: str = Depends(API_KEY_HEADER),
    user_id: int = Body(..., gt=0, description="User ID must be positive"),
    operation: str = Body(..., min_length=1, max_length=100, 
                         description="Operation description"),
    details: Optional[str] = Body(None, max_length=500, 
                                 description="Operation details"),
    encryption_status: int = Body(0, ge=0, le=1, 
                                description="0=unencrypted, 1=encrypted")
):
    """
    记录用户操作日志
    - **user_id**: 用户ID
    - **operation**: 操作描述
    - **details**: 操作详情（可选）
    - **encryption_status**: 加密状态（0=未加密，1=加密，默认0）
    """
    # 验证API Key
    if not api_key or not validate_api_key(api_key):
        raise HTTPException(status_code=403, detail="Invalid API key")
    
    # 检查请求频率
    client_ip = request.client.host
    if not check_rate_limit(client_ip):
        raise HTTPException(status_code=429, detail="Too many requests")
    
    try:
        log_entry = LogService.record_operation_log(
            db=db,
            user_id=user_id,
            operation=operation,
            ip_address=client_ip,
            details=details,
            encryption_status=encryption_status
        )
        
        return {
            "status": "success",
            "message": "日志记录成功",
            "log_id": log_entry.id,
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        logger.error(f"记录日志失败: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail={
                "status": "error",
                "message": "Internal server error"
            }
        )

# 保留原有的根路径端点以保持向后兼容性
@router.post("", 
    response_model=dict,
    responses={
        400: {"description": "Invalid input data"},
        500: {"description": "Internal server error"},
        429: {"description": "Too many requests"}
    },
    summary="Create a new operation log (Legacy)",
    description="Records user operations with details and encryption status (Legacy endpoint)",
    dependencies=[Depends(API_KEY_HEADER)]
)
def create_log(
    request: Request,
    db: Session = Depends(get_db),
    api_key: str = Depends(API_KEY_HEADER),
    user_id: int = Body(..., gt=0, description="User ID must be positive"),
    operation: str = Body(..., min_length=1, max_length=100, 
                         description="Operation description"),
    details: Optional[str] = Body(None, max_length=500, 
                                 description="Operation details"),
    encryption_status: int = Body(0, ge=0, le=1, 
                                description="0=unencrypted, 1=encrypted")
):
    # 直接调用主要的record端点函数以保持一致性
    return create_log_record(
        request=request,
        db=db,
        api_key=api_key,
        user_id=user_id,
        operation=operation,
        details=details,
        encryption_status=encryption_status
    )


@router.get("",
    response_model=dict,
    responses={
        400: {"description": "Invalid query parameters"},
        500: {"description": "Internal server error"}
    },
    summary="Query operation logs",
    description="Retrieve logs with filtering and pagination support"
)
def get_logs(
    request: Request,
    db: Session = Depends(get_db),
    user_id: Optional[int] = Query(None, gt=0, 
                                 description="Filter by user ID"),
    operation: Optional[str] = Query(None, min_length=1, max_length=100,
                                   description="Filter by operation type"),
    start_time: Optional[datetime] = Query(None,
                                        description="Start time filter"),
    end_time: Optional[datetime] = Query(None,
                                       description="End time filter"),
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=100, 
                     description="Items per page (max 100)")
):
    """
    获取指定用户的操作日志（分页）
    - **user_id**: 用户ID
    - **operation_type**: 操作类型筛选（可选）
    - **start_time**: 开始时间（可选）
    - **end_time**: 结束时间（可选）
    - **skip**: 跳过的记录数（默认0）
    - **limit**: 返回的记录数（默认50，最大200）
    """
    try:
        logs_data = LogService.get_user_logs(
            db=db,
            user_id=user_id,
            operation_type=operation,
            start_time=start_time,
            end_time=end_time,
            skip=(page-1)*size,
            limit=size
        )
        
        # 转换日志对象为字典格式
        logs_list = []
        for log in logs_data["logs"]:
            logs_list.append({
                "id": log.id,
                "user_id": log.user_id,
                "operation": log.operation,
                "operation_time": log.operation_time.isoformat(),
                "operation_ip": log.operation_ip,
                "operation_address": log.operation_address,
                "details": log.details,
                "encryption_status": log.encryption_status
            })
        
        return {
            "status": "success",
            "data": {
                "total": logs_data["total"],
                "page": page,
                "size": size,
                "logs": logs_list
            }
        }
    except Exception as e:
        logger.error(f"获取用户日志失败: {str(e)}")
        return {
            "status": "error",
            "message": str(e)
        }

@router.get("/all", response_model=dict)
def get_all_logs(
    user_id: Optional[int] = Query(None, description="用户ID筛选"),
    operation_type: Optional[str] = Query(None, description="操作类型筛选"),
    start_time: Optional[datetime] = Query(None, description="开始时间"),
    end_time: Optional[datetime] = Query(None, description="结束时间"),
    skip: int = Query(0, ge=0, description="跳过的记录数"),
    limit: int = Query(50, ge=1, le=200, description="返回的记录数"),
    db: Session = Depends(get_db)
):
    """
    获取所有用户的操作日志（分页）
    - **user_id**: 用户ID筛选（可选）
    - **operation_type**: 操作类型筛选（可选）
    - **start_time**: 开始时间（可选）
    - **end_time**: 结束时间（可选）
    - **skip**: 跳过的记录数（默认0）
    - **limit**: 返回的记录数（默认50，最大200）
    """
    try:
        logs_data = LogService.get_all_logs(
            db=db,
            user_id=user_id,
            operation_type=operation_type,
            start_time=start_time,
            end_time=end_time,
            skip=skip,
            limit=limit
        )
        
        # 转换日志对象为字典格式
        logs_list = []
        for log in logs_data["logs"]:
            logs_list.append({
                "id": log.id,
                "user_id": log.user_id,
                "operation": log.operation,
                "operation_time": log.operation_time.isoformat(),
                "operation_ip": log.operation_ip,
                "operation_address": log.operation_address,
                "details": log.details,
                "encryption_status": log.encryption_status
            })
        
        return {
            "status": "success",
            "data": {
                "total": logs_data["total"],
                "skip": logs_data["skip"],
                "limit": logs_data["limit"],
                "logs": logs_list
            }
        }
    except Exception as e:
        logger.error(f"获取所有日志失败: {str(e)}")
        return {
            "status": "error",
            "message": str(e)
        }

@router.delete("/cleanup", response_model=dict)
def cleanup_old_logs(
    days: int = Query(90, ge=1, description="清理多少天前的日志"),
    db: Session = Depends(get_db)
):
    """
    清理过期的操作日志
    - **days**: 清理多少天前的日志（默认90天）
    """
    try:
        count = LogService.cleanup_old_logs(db=db, days=days)
        
        return {
            "status": "success",
            "message": f"已清理 {count} 条 {days} 天前的日志"
        }
    except Exception as e:
        logger.error(f"清理日志失败: {str(e)}")
        return {
            "status": "error",
            "message": str(e)
        }

@router.get("/stats", response_model=dict)
def get_log_stats(
    user_id: Optional[int] = Query(None, description="用户ID筛选"),
    days: int = Query(30, ge=1, description="统计多少天内的日志"),
    db: Session = Depends(get_db)
):
    """
    获取日志统计信息
    - **user_id**: 用户ID筛选（可选）
    - **days**: 统计多少天内的日志（默认30天）
    """
    try:
        stats = LogService.get_log_stats(db=db, user_id=user_id, days=days)
        
        return {
            "status": "success",
            "data": {
                "stats": stats,
                "days": days,
                "user_id": user_id
            }
        }
    except Exception as e:
        logger.error(f"获取日志统计失败: {str(e)}")
        return {
            "status": "error",
            "message": str(e)
        }