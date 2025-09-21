import requests
import time

# 基础URL
BASE_URL = "http://localhost:8080"

# 测试邮箱
TEST_EMAIL = "test_verification@example.com"

print("=== 验证修改环境变量并重启服务后的验证码接口 ===")
print("等待30秒让服务完全启动...")
time.sleep(30)

# 测试 /api/verify-code/generate 接口
def test_verify_code_generate():
    print("\n\n=== 测试 /api/verify-code/generate 接口 ===")
    
    url = f"{BASE_URL}/api/verify-code/generate"
    payload = {
        "email": TEST_EMAIL,
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

# 测试 /api/auth/send-verification-code 接口
def test_auth_send_verification():
    print("\n\n=== 测试 /api/auth/send-verification-code 接口 ===")
    
    url = f"{BASE_URL}/api/auth/send-verification-code"
    payload = {
        "email": TEST_EMAIL,
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

# 测试 /api/encryption/public-key 接口（作为参考对比）
def test_encryption_public_key():
    print("\n\n=== 测试 /api/encryption/public-key 接口（参考）===")
    
    url = f"{BASE_URL}/api/encryption/public-key"
    headers = {
        "Accept": "application/json"
    }
    
    print(f"URL: {url}")
    
    try:
        response = requests.get(url, headers=headers, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
    except requests.RequestException as e:
        print(f"请求异常: {e}")

# 运行所有测试
test_encryption_public_key()  # 先测试参考接口
test_verify_code_generate()
test_auth_send_verification()

print("\n\n=== 测试完成 ===")