from sqlalchemy import Column, Integer, String, DateTime, Boolean, ForeignKey, Index
from sqlalchemy.orm import relationship
from datetime import datetime
from ..main import Base

class UserAccount(Base):
    __tablename__ = "user_account"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(100), unique=True, index=True, nullable=False)
    account = Column(String(50), unique=True, index=True, nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
    role = Column(Integer, nullable=False, default=0, comment='0 = 主账号，1 = 子账号')
    parent_id = Column(Integer, ForeignKey("user_account.id"), comment='子账号关联主账号ID')
    email_verified = Column(Integer, nullable=False, default=0, comment='0 = 未验证，1 = 已验证')
    created_time = Column(DateTime, default=datetime.now)
    last_login_time = Column(DateTime)
    last_login_ip = Column(String(50))
    last_login_address = Column(String(100))
    status = Column(Integer, nullable=False, default=1, comment='1 = 正常，0 = 禁用')
    rsa_public_key = Column(Text)
    created_by = Column(Integer, ForeignKey("user_account.id"))
    
    # 自引用关系
    creator = relationship("UserAccount", remote_side=[id])
    
    # 操作日志关系
    operation_logs = relationship("UserOperationLog", back_populates="user")
    
    def __repr__(self):
        return f"<UserAccount(username='{self.username}', email='{self.email}', role='{self.role}')>"