from sqlalchemy.orm import Session
from sqlalchemy import desc
from datetime import datetime, timedelta
# from ..models.log import UserOperationLog
from Date_python.models.log import UserOperationLog
import logging

logger = logging.getLogger(__name__)

class LogService:
    @staticmethod
    def record_operation_log(
        db: Session,
        user_id: int,
        operation: str,
        ip_address: str = None,
        address: str = None,
        details: str = None,
        encryption_status: int = 0
    ):
        """记录用户操作日志"""
        try:
            log_entry = UserOperationLog(
                user_id=user_id,
                operation=operation,
                operation_ip=ip_address,
                operation_address=address,
                details=details,
                encryption_status=encryption_status
            )
            db.add(log_entry)
            db.commit()
            db.refresh(log_entry)
            logger.info(f"用户 {user_id} 操作日志已记录: {operation}")
            return log_entry
        except Exception as e:
            db.rollback()
            logger.error(f"记录用户操作日志失败: {str(e)}")
            raise
    
    @staticmethod
    def get_user_logs(
        db: Session,
        user_id: int,
        operation_type: str = None,
        start_time: datetime = None,
        end_time: datetime = None,
        skip: int = 0,
        limit: int = 50
    ):
        """获取用户操作日志，支持分页和筛选"""
        try:
            query = db.query(UserOperationLog).filter(UserOperationLog.user_id == user_id)
            
            if operation_type:
                query = query.filter(UserOperationLog.operation.contains(operation_type))
            
            if start_time:
                query = query.filter(UserOperationLog.operation_time >= start_time)
            
            if end_time:
                query = query.filter(UserOperationLog.operation_time <= end_time)
            
            total = query.count()
            logs = query.order_by(desc(UserOperationLog.operation_time)).offset(skip).limit(limit).all()
            
            return {
                "total": total,
                "logs": logs,
                "skip": skip,
                "limit": limit
            }
        except Exception as e:
            logger.error(f"获取用户操作日志失败: {str(e)}")
            raise
    
    @staticmethod
    def get_all_logs(
        db: Session,
        user_id: int = None,
        operation_type: str = None,
        start_time: datetime = None,
        end_time: datetime = None,
        skip: int = 0,
        limit: int = 50
    ):
        """获取所有用户操作日志，支持分页和筛选"""
        try:
            query = db.query(UserOperationLog)
            
            if user_id:
                query = query.filter(UserOperationLog.user_id == user_id)
            
            if operation_type:
                query = query.filter(UserOperationLog.operation.contains(operation_type))
            
            if start_time:
                query = query.filter(UserOperationLog.operation_time >= start_time)
            
            if end_time:
                query = query.filter(UserOperationLog.operation_time <= end_time)
            
            total = query.count()
            logs = query.order_by(desc(UserOperationLog.operation_time)).offset(skip).limit(limit).all()
            
            return {
                "total": total,
                "logs": logs,
                "skip": skip,
                "limit": limit
            }
        except Exception as e:
            logger.error(f"获取所有用户操作日志失败: {str(e)}")
            raise
    
    @staticmethod
    def cleanup_old_logs(db: Session, days: int = 90):
        """清理指定天数前的过期日志"""
        try:
            cutoff_date = datetime.now() - timedelta(days=days)
            
            # 先查询要删除的日志数量
            count = db.query(UserOperationLog).filter(UserOperationLog.operation_time < cutoff_date).count()
            
            # 执行删除操作
            db.query(UserOperationLog).filter(UserOperationLog.operation_time < cutoff_date).delete(synchronize_session=False)
            db.commit()
            
            logger.info(f"已清理 {count} 条 {days} 天前的过期操作日志")
            return count
        except Exception as e:
            db.rollback()
            logger.error(f"清理过期操作日志失败: {str(e)}")
            raise
    
    @staticmethod
    def get_log_stats(db: Session, user_id: int = None, days: int = 30):
        """获取日志统计信息"""
        try:
            from sqlalchemy import func
            
            start_date = datetime.now() - timedelta(days=days)
            query = db.query(
                UserOperationLog.operation,
                func.count(UserOperationLog.id).label('count')
            )
            
            if user_id:
                query = query.filter(UserOperationLog.user_id == user_id)
            
            query = query.filter(UserOperationLog.operation_time >= start_date)
            
            stats = query.group_by(UserOperationLog.operation).order_by(desc('count')).all()
            
            return [{
                "operation": stat.operation,
                "count": stat.count
            } for stat in stats]
        except Exception as e:
            logger.error(f"获取日志统计信息失败: {str(e)}")
            raise