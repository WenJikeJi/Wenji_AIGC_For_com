import requests
import json
import time
from datetime import datetime

# 基础URL
BASE_URL = "http://localhost:8080"

# 测试邮箱
TEST_EMAIL = "test_verification@example.com"

# 简单测试函数
def simple_test(endpoint, payload, headers=None):
    """简单的HTTP请求测试函数"""
    url = f"{BASE_URL}{endpoint}"
    print(f"\n=== 测试 {endpoint} ===")
    print(f"URL: {url}")
    print(f"请求体: {payload}")
    
    try:
        start_time = time.time()
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        end_time = time.time()
        
        print(f"响应时间: {end_time - start_time:.2f}秒")
        print(f"响应状态码: {response.status_code}")
        print(f"响应头: {response.headers}")
        print(f"响应内容: {response.text}")
        
        # 尝试解析JSON响应
        try:
            response_json = response.json()
            print(f"解析的JSON响应: {json.dumps(response_json, indent=2, ensure_ascii=False)}")
        except json.JSONDecodeError:
            print("无法解析响应为JSON格式")
            
    except requests.RequestException as e:
        print(f"请求异常: {e}")
        return None
    
    return response

# 检查Java服务是否正在运行
def check_java_service():
    """检查Java服务是否正在运行"""
    try:
        response = requests.get(f"{BASE_URL}/swagger-ui/", timeout=5)
        print(f"\n=== Java服务状态检查 ===")
        print(f"状态码: {response.status_code}")
        print(f"服务正在运行: {response.status_code == 200}")
        return response.status_code == 200
    except requests.ConnectionError:
        print("Java服务未运行或无法连接")
        return False

# 测试验证码生成接口
def test_verification_code():
    """测试验证码生成接口的不同场景"""
    # 标准请求头
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    }
    
    print(f"\n\n===== 验证码生成接口测试 =====")
    print(f"当前时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 1. 测试原始接口 (可能存在IP防刷限制)
    print("\n1. 测试原始接口 /api/verify-code/generate (注册验证)")
    payload1 = {
        "email": TEST_EMAIL,
        "type": 1
    }
    simple_test("/api/verify-code/generate", payload1, headers)
    
    # 等待几秒，避免请求太频繁
    time.sleep(2)
    
    # 2. 测试AuthController中的替代验证码接口
    print("\n2. 测试替代接口 /api/auth/send-verification-code (注册验证)")
    payload2 = {
        "email": TEST_EMAIL,
        "type": 1
    }
    simple_test("/api/auth/send-verification-code", payload2, headers)
    
    # 等待几秒
    time.sleep(2)
    
    # 3. 测试密码重置类型
    print("\n3. 测试密码重置类型")
    payload3 = {
        "email": TEST_EMAIL,
        "type": 2
    }
    simple_test("/api/auth/send-verification-code", payload3, headers)
    
    # 4. 测试可能的字符串类型type值
    print("\n4. 测试字符串类型的type值")
    payload4 = {
        "email": TEST_EMAIL,
        "type": "REGISTER"
    }
    simple_test("/api/auth/send-verification-code", payload4, headers)
    
    # 5. 测试不同的邮箱格式
    print("\n5. 测试不同的邮箱格式")
    alternative_emails = [
        "test@gmail.com",
        "test@163.com",
        "test@qq.com"
    ]
    
    for email in alternative_emails:
        payload = {
            "email": email,
            "type": 1
        }
        print(f"\n测试邮箱: {email}")
        simple_test("/api/auth/send-verification-code", payload, headers)
        time.sleep(2)

# 主函数
def main():
    print("=== 验证码接口问题排查工具 ===")
    print(f"测试目标: {BASE_URL}")
    
    # 检查Java服务状态
    if not check_java_service():
        print("\n错误: Java后端服务未运行，请先启动服务再进行测试。")
        print("启动命令示例: java -Dfile.encoding=UTF-8 -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890")
        return
    
    # 运行测试
    test_verification_code()
    
    print("\n\n=== 测试完成 ===")
    print("\n问题排查建议:")
    print("1. 如果所有请求都返回400，可能是IP防刷限制，尝试等待10分钟后再试，或使用不同网络环境")
    print("2. 查看Java服务日志，特别关注SQL异常和邮件发送相关的错误信息")
    print("3. 检查MySQL数据库verification_code表，确认是否有新记录插入")
    print("4. 尝试使用/api/auth/send-verification-code替代/api/verify-code/generate接口")
    print("5. 确认请求体中email和type字段的格式是否正确(type应为1或2的整数)")

if __name__ == "__main__":
    main()