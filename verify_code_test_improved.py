#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Wenji平台验证码功能测试工具（改进版）
用于测试验证码生成、发送和验证功能是否正常工作
包含数据库查询功能作为备用方案
"""

import requests
import pymysql
import time
import json
from datetime import datetime


class VerificationCodeTester:
    def __init__(self):
        # 配置信息
        self.base_url = "http://localhost:8080"
        self.db_config = {
            "host": "localhost",
            "user": "root",
            "password": "Wenguang-1122",
            "database": "wenji_db",
            "charset": "utf8mb4"
        }
        self.email = "test@example.com"  # 可以修改为实际测试邮箱

    def print_color(self, text, color_code):
        """打印带颜色的文本"""
        print(f"\033[{color_code}m{text}\033[0m")

    def print_success(self, text):
        self.print_color(text, "32")  # 绿色

    def print_error(self, text):
        self.print_color(text, "31")  # 红色

    def print_warning(self, text):
        self.print_color(text, "33")  # 黄色

    def print_info(self, text):
        self.print_color(text, "34")  # 蓝色

    def test_generate_verification_code(self):
        """测试生成验证码接口"""
        self.print_info("\n测试 /api/verify-code/generate 接口...")
        url = f"{self.base_url}/api/verify-code/generate"
        payload = {
            "email": self.email,
            "type": 1  # 1: 注册验证, 2: 密码重置
        }
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=10)
            self.print_info(f"响应状态码: {response.status_code}")
            self.print_info(f"响应内容: {response.text}")
            
            if response.status_code == 200:
                self.print_success("✅ 验证码生成接口调用成功！")
                return True
            else:
                self.print_error(f"❌ 验证码生成接口调用失败: {response.text}")
                return False
        except requests.exceptions.ConnectionError:
            self.print_error("❌ 无法连接到服务器，请确认服务已启动并监听8080端口")
            return False
        except Exception as e:
            self.print_error(f"❌ 测试过程中发生错误: {str(e)}")
            return False

    def test_send_verification_code(self):
        """测试发送验证码接口"""
        self.print_info("\n测试 /api/auth/send-verification-code 接口...")
        url = f"{self.base_url}/api/auth/send-verification-code"
        payload = {
            "email": self.email,
            "type": 1  # 1: 注册验证, 2: 密码重置
        }
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=10)
            self.print_info(f"响应状态码: {response.status_code}")
            self.print_info(f"响应内容: {response.text}")
            
            if response.status_code == 200:
                self.print_success("✅ 发送验证码接口调用成功！")
                return True
            else:
                self.print_error(f"❌ 发送验证码接口调用失败: {response.text}")
                return False
        except requests.exceptions.ConnectionError:
            self.print_error("❌ 无法连接到服务器，请确认服务已启动并监听8080端口")
            return False
        except Exception as e:
            self.print_error(f"❌ 测试过程中发生错误: {str(e)}")
            return False

    def get_latest_verification_code_from_db(self):
        """直接从数据库查询最新的验证码"""
        self.print_info("\n从数据库查询最新验证码...")
        try:
            # 连接数据库
            conn = pymysql.connect(**self.db_config)
            cursor = conn.cursor()
            
            # 查询最新的验证码
            sql = """SELECT code, created_time, expired_time 
                     FROM verification_code 
                     WHERE email = %s 
                     ORDER BY created_time DESC 
                     LIMIT 1"""
            cursor.execute(sql, (self.email,))
            result = cursor.fetchone()
            
            # 关闭连接
            cursor.close()
            conn.close()
            
            if result:
                code, created_time, expired_time = result
                now = datetime.now()
                is_valid = now < expired_time
                
                self.print_info(f"数据库中找到最新验证码: {code}")
                self.print_info(f"创建时间: {created_time}")
                self.print_info(f"过期时间: {expired_time}")
                self.print_info(f"当前状态: {'有效' if is_valid else '已过期'}")
                
                if is_valid:
                    self.print_success(f"✅ 找到有效的验证码: {code}")
                    return code
                else:
                    self.print_warning(f"⚠️  找到验证码但已过期: {code}")
                    return None
            else:
                self.print_error("❌ 未在数据库中找到验证码记录")
                return None
        except Exception as e:
            self.print_error(f"❌ 数据库查询过程中发生错误: {str(e)}")
            return None

    def test_verify_code(self, code):
        """测试验证码验证接口"""
        if not code:
            self.print_warning("⚠️  没有有效的验证码可用于测试验证接口")
            return False
        
        self.print_info("\n测试 /api/verify-code/validate 接口...")
        url = f"{self.base_url}/api/verify-code/validate"
        payload = {
            "email": self.email,
            "code": code,
            "type": 1
        }
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=10)
            self.print_info(f"响应状态码: {response.status_code}")
            self.print_info(f"响应内容: {response.text}")
            
            if response.status_code == 200:
                result = response.json()
                if result.get("valid", False):
                    self.print_success("✅ 验证码验证成功！")
                else:
                    self.print_warning(f"⚠️  验证码验证结果: {result.get('message', '未知错误')}")
                return True
            else:
                self.print_error(f"❌ 验证码验证接口调用失败: {response.text}")
                return False
        except Exception as e:
            self.print_error(f"❌ 测试过程中发生错误: {str(e)}")
            return False

    def main(self):
        """主测试函数"""
        print("=" * 60)
        print("        Wenji平台验证码功能测试工具（改进版）")
        print("=" * 60)
        
        # 询问用户是否修改配置
        change_config = input(f"是否需要修改测试邮箱？当前邮箱: {self.email} (y/n): ")
        if change_config.lower() == 'y':
            self.email = input("请输入测试邮箱地址: ")
        
        print(f"\n使用邮箱进行测试: {self.email}")
        
        # 测试生成验证码接口
        self.test_generate_verification_code()
        
        # 等待一小段时间，确保数据库操作完成
        time.sleep(1)
        
        # 测试发送验证码接口
        self.test_send_verification_code()
        
        # 等待一小段时间，确保数据库操作完成
        time.sleep(1)
        
        # 从数据库获取最新验证码
        code = self.get_latest_verification_code_from_db()
        
        # 测试验证码验证接口
        self.test_verify_code(code)
        
        # 显示总结和建议
        print("\n" + "=" * 60)
        print("测试总结")
        print("=" * 60)
        
        if code:
            self.print_success(f"✅ 验证码功能基本正常: 系统能够生成验证码并保存到数据库")
            self.print_info(f"最新验证码: {code}")
        else:
            self.print_error("❌ 验证码功能存在问题，请检查服务和数据库配置")
        
        self.print_warning("\n注意事项:")
        self.print_warning("1. 如果接口返回'Authentication failed'错误，可能是邮件配置问题")
        self.print_warning("2. 已修复的配置: application.properties和启动脚本中的邮件用户名已统一为service@shamillaa.com")
        self.print_warning("3. EmailUtil.java已修改为从配置文件读取发件人地址，不再硬编码")
        self.print_warning("4. 如遇到邮件发送问题，请使用test_email_config.py脚本测试邮件配置")
        self.print_warning("5. 如需使用备用方案，可以直接从数据库获取验证码")
        self.print_warning("6. 优化的启动脚本: start_server_optimized.bat 包含详细日志记录功能")
        
        print("\n" + "=" * 60)
        print("测试完成")
        print("=" * 60)


if __name__ == "__main__":
    tester = VerificationCodeTester()
    tester.main()