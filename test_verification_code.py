import requests
import json
import time

# 测试的基础URL
BASE_URL = "http://localhost:8080"

# 测试的邮箱和参数
TEST_EMAILS = [
    "ken@shamillaa.com",  # 用户提供的邮箱
    "test@example.com",   # 标准测试邮箱
    "test@gmail.com"       # 常见域名邮箱
]

# 测试函数
def test_verification_code(email, type_value, headers=None):
    """测试验证码生成接口"""
    url = f"{BASE_URL}/api/verify-code/generate"
    payload = {
        "email": email,
        "type": type_value
    }
    
    print(f"\n=== 测试请求 ===")
    print(f"URL: {url}")
    print(f"请求头: {headers or '默认'}")
    print(f"请求体: {payload}")
    
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        # 尝试解析JSON响应
        try:
            response_json = response.json()
            print(f"解析的JSON响应: {json.dumps(response_json, indent=2, ensure_ascii=False)}")
        except json.JSONDecodeError:
            print("无法解析响应为JSON格式")
            
    except requests.RequestException as e:
        print(f"请求异常: {e}")

# 测试不同请求头
def test_with_different_headers():
    """测试不同请求头的影响"""
    print("\n\n=== 测试不同请求头 ===")
    
    # 无请求头
    test_verification_code(TEST_EMAILS[0], 1)
    time.sleep(1)
    
    # 标准请求头
    standard_headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    test_verification_code(TEST_EMAILS[0], 1, standard_headers)
    time.sleep(1)
    
    # 模拟浏览器请求头
    browser_headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
    test_verification_code(TEST_EMAILS[0], 1, browser_headers)
    time.sleep(1)

# 测试不同的type值
def test_with_different_types():
    """测试不同的type值"""
    print("\n\n=== 测试不同的type值 ===")
    
    # 标准请求头
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    # 测试type=1（注册验证）
    test_verification_code(TEST_EMAILS[1], 1, headers)
    time.sleep(1)
    
    # 测试type=2（密码重置）
    test_verification_code(TEST_EMAILS[1], 2, headers)
    time.sleep(1)
    
    # 测试字符串类型的type（可能系统实际接受的是字符串）
    test_verification_code_string_type(TEST_EMAILS[1], "REGISTER", headers)
    time.sleep(1)
    test_verification_code_string_type(TEST_EMAILS[1], "RESET", headers)
    time.sleep(1)

# 测试字符串类型的type
def test_verification_code_string_type(email, type_value, headers=None):
    """测试字符串类型的type值"""
    url = f"{BASE_URL}/api/verify-code/generate"
    payload = {
        "email": email,
        "type": type_value
    }
    
    print(f"\n=== 测试字符串类型type ===")
    print(f"URL: {url}")
    print(f"请求头: {headers or '默认'}")
    print(f"请求体: {payload}")
    
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
    except requests.RequestException as e:
        print(f"请求异常: {e}")

# 测试不同的邮箱格式
def test_with_different_emails():
    """测试不同的邮箱格式"""
    print("\n\n=== 测试不同的邮箱格式 ===")
    
    # 标准请求头
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    # 测试不同的邮箱
    for email in TEST_EMAILS:
        test_verification_code(email, 1, headers)
        time.sleep(1)

# 测试AuthController中的另一个验证码接口
def test_alternative_verification_api():
    """测试AuthController中的另一个验证码接口"""
    print("\n\n=== 测试AuthController中的另一个验证码接口 ===")
    
    url = f"{BASE_URL}/api/auth/send-verification-code"
    payload = {
        "email": TEST_EMAILS[1],
        "type": 1
    }
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    print(f"URL: {url}")
    print(f"请求体: {payload}")
    
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
    except requests.RequestException as e:
        print(f"请求异常: {e}")

# 测试可能存在的额外参数需求
def test_with_additional_params():
    """测试可能存在的额外参数需求"""
    print("\n\n=== 测试可能存在的额外参数 ===")
    
    url = f"{BASE_URL}/api/verify-code/generate"
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    # 测试带场景参数
    payload_with_scene = {
        "email": TEST_EMAILS[1],
        "type": 1,
        "scene": "EMAIL"
    }
    print(f"测试场景参数:")
    print(f"请求体: {payload_with_scene}")
    try:
        response = requests.post(url, json=payload_with_scene, headers=headers, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
    except requests.RequestException as e:
        print(f"请求异常: {e}")
    
    time.sleep(1)

# 主函数
def main():
    print("=== 验证码生成接口测试工具 ===")
    print(f"测试目标: {BASE_URL}")
    print("\n请确保Java后端服务已启动")
    
    # 按照优先级进行测试
    print("\n\n===== 1. 首先测试AuthController中的替代验证码接口 =====")
    test_alternative_verification_api()
    
    print("\n\n===== 2. 测试不同的type值 =====")
    test_with_different_types()
    
    print("\n\n===== 3. 测试不同的邮箱格式 =====")
    test_with_different_emails()
    
    print("\n\n===== 4. 测试不同请求头 =====")
    test_with_different_headers()
    
    print("\n\n===== 5. 测试可能存在的额外参数 =====")
    test_with_additional_params()
    
    print("\n\n=== 测试完成 ===")
    print("请查看测试结果，找到成功的请求模式")
    print("常见问题排查:")
    print("1. 确保type参数为1或2（整数类型）")
    print("2. 检查IP是否在1小时内请求超过5次")
    print("3. 尝试使用AuthController中的/api/auth/send-verification-code接口")
    print("4. 确认邮箱格式是否被服务器接受")

if __name__ == "__main__":
    main()