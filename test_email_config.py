#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Wenji平台邮件配置验证脚本
用于测试SMTP服务器连接和认证配置是否正确
"""

import smtplib
import ssl
import sys
import getpass
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart


def print_color(text, color_code):
    """打印带颜色的文本"""
    print(f"\033[{color_code}m{text}\033[0m")


def print_success(text):
    print_color(text, "32")  # 绿色


def print_error(text):
    print_color(text, "31")  # 红色


def print_warning(text):
    print_color(text, "33")  # 黄色


def print_info(text):
    print_color(text, "34")  # 蓝色


def test_smtp_connection(smtp_server, port, username, password):
    """测试SMTP服务器连接和认证"""
    try:
        # 创建SSL上下文
        context = ssl.create_default_context()
        
        print_info(f"\n正在连接到SMTP服务器: {smtp_server}:{port}")
        # 连接到服务器
        server = smtplib.SMTP_SSL(smtp_server, port, context=context)
        server.set_debuglevel(0)  # 设为1可查看详细调试信息
        
        print_info("连接成功，正在进行认证...")
        # 尝试登录
        server.login(username, password)
        
        print_success(f"✅ 认证成功！用户名 '{username}' 已通过验证")
        
        # 询问是否发送测试邮件
        send_test = input("\n是否发送测试邮件？(y/n): ")
        if send_test.lower() == 'y':
            recipient = input("请输入接收测试邮件的邮箱地址: ")
            send_test_email(server, username, recipient)
        
        # 退出服务器连接
        server.quit()
        return True
        
    except smtplib.SMTPAuthenticationError:
        print_error("❌ 认证失败！请检查用户名和密码是否正确")
        print_warning("提示: 某些邮件服务（如飞书）可能需要使用应用专用密码而非登录密码")
        return False
    except smtplib.SMTPConnectError:
        print_error(f"❌ 无法连接到SMTP服务器: {smtp_server}:{port}")
        print_warning("提示: 请检查服务器地址和端口是否正确，以及网络连接是否畅通")
        return False
    except Exception as e:
        print_error(f"❌ 测试过程中发生错误: {str(e)}")
        return False


def send_test_email(server, sender, recipient):
    """发送测试邮件"""
    try:
        message = MIMEMultipart("alternative")
        message["Subject"] = "Wenji平台邮件配置测试"
        message["From"] = sender
        message["To"] = recipient
        
        text = """\
        这是Wenji平台邮件配置测试邮件
        恭喜！您的邮件服务器配置正确，可以正常发送邮件。
        如果您收到此邮件，说明Wenji平台的邮件功能已配置成功。
        """
        
        html = """\
        <html>
          <body>
            <p>这是Wenji平台邮件配置测试邮件</p>
            <p>恭喜！您的邮件服务器配置正确，可以正常发送邮件。</p>
            <p>如果您收到此邮件，说明Wenji平台的邮件功能已配置成功。</p>
          </body>
        </html>
        """
        
        part1 = MIMEText(text, "plain")
        part2 = MIMEText(html, "html")
        
        message.attach(part1)
        message.attach(part2)
        
        print_info(f"正在发送测试邮件到: {recipient}")
        server.sendmail(sender, recipient, message.as_string())
        print_success("✅ 测试邮件发送成功！请查收邮箱")
        
    except Exception as e:
        print_error(f"❌ 发送测试邮件失败: {str(e)}")


def main():
    """主函数"""
    print("=" * 60)
    print("        Wenji平台邮件配置验证工具")
    print("=" * 60)
    
    # 默认配置（与Wenji平台配置一致）
    default_smtp_server = "smtp.feishu.cn"
    default_port = 465
    default_username = "service@shamillaa.com"
    
    # 获取用户输入
    smtp_server = input(f"SMTP服务器地址 [{default_smtp_server}]: ") or default_smtp_server
    port = input(f"SMTP服务器端口 [{default_port}]: ") or default_port
    username = input(f"邮件用户名 [{default_username}]: ") or default_username
    password = getpass.getpass("邮件密码: ")
    
    # 转换端口为整数
    try:
        port = int(port)
    except ValueError:
        print_error("❌ 无效的端口号！请输入数字")
        sys.exit(1)
    
    # 执行测试
    test_smtp_connection(smtp_server, port, username, password)
    
    print("\n" + "=" * 60)
    print("测试完成")
    print("=" * 60)


if __name__ == "__main__":
    main()