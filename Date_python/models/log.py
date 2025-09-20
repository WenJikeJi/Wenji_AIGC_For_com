from sqlalchemy import Column, Integer, String, DateTime, Text, Index, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from ..main import Base

class UserOperationLog(Base):
    __tablename__ = "user_operation_log"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("user_account.id"), nullable=False)
    operation = Column(String(200), nullable=False)
    operation_time = Column(DateTime, nullable=False, default=datetime.now)
    operation_ip = Column(String(50))
    operation_address = Column(String(100))
    details = Column(Text)
    encryption_status = Column(Integer, nullable=False, default=0)
    
    # 与用户账户的关系
    user = relationship("UserAccount", back_populates="operation_logs")
    
    # 定义索引
    __table_args__ = (
        Index("idx_user_id", "user_id"),
        Index("idx_operation_time", "operation_time"),
    )
    
    def __repr__(self):
        return f"<UserOperationLog(user_id={self.user_id}, operation='{self.operation}', time='{self.operation_time}')>"