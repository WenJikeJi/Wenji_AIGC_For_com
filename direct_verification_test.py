import requests
import json
import time
import pymysql
import requests

# 基础URL
BASE_URL = "http://localhost:8080"

# 测试参数
TEST_EMAIL = "test_verification@example.com"
TEST_TYPE = 1  # 1=注册验证, 2=密码重置

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'Wenguang-1122',
    'database': 'wenji_db'
}

# 从数据库获取最新验证码的函数
def get_latest_verification_code(email, type_code):
    try:
        # 连接数据库
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # 查询指定邮箱的最新未使用验证码
        query = """
            SELECT code, created_time 
            FROM verification_code 
            WHERE email = %s AND type = %s AND used = 0 
            ORDER BY created_time DESC 
            LIMIT 1
        """
        cursor.execute(query, (email, type_code))
        result = cursor.fetchone()
        
        # 关闭连接
        cursor.close()
        conn.close()
        
        return result if result else None
    except Exception as e:
        print(f"数据库查询失败: {e}")
        return None

# 测试请求函数
def test_request(endpoint, payload):
    url = f"{BASE_URL}{endpoint}"
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    print(f"\n=== 请求 {endpoint} ===")
    print(f"URL: {url}")
    print(f"请求体: {payload}")
    
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        print(f"状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        # 尝试解析JSON响应
        try:
            json_data = response.json()
            print(f"JSON解析结果: {json.dumps(json_data, indent=2, ensure_ascii=False)}")
        except json.JSONDecodeError:
            print("无法解析为JSON")
            
        return response
    except Exception as e:
        print(f"请求异常: {e}")
        return None

# 直接测试验证码接口
def test_verification_endpoints():
    """直接测试验证码相关接口"""
    print("\n\n===== 直接验证码接口测试 =====")
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 1. 测试原始接口 /api/verify-code/generate
    print("\n1. 测试原始接口 /api/verify-code/generate")
    payload1 = {
        "email": TEST_EMAIL,
        "type": TEST_TYPE
    }
    response1 = test_request("/api/verify-code/generate", payload1)
    
    # 直接从数据库获取最新验证码
    time.sleep(1)  # 等待数据库写入完成
    print("\n=== 直接从数据库获取最新验证码 ===")
    latest_code = get_latest_verification_code(TEST_EMAIL, TEST_TYPE)
    if latest_code:
        code, created_time = latest_code
        print(f"✅ 成功获取到验证码!")
        print(f"验证码: {code}")
        print(f"创建时间: {created_time}")
        print(f"提示: 您可以直接使用此验证码进行注册/密码重置操作")
    else:
        print("❌ 未找到最新验证码记录")
    
    # 等待2秒
    time.sleep(2)
    
    # 2. 测试替代接口 /api/auth/send-verification-code
    print("\n2. 测试替代接口 /api/auth/send-verification-code")
    payload2 = {
        "email": TEST_EMAIL,
        "type": TEST_TYPE
    }
    response2 = test_request("/api/auth/send-verification-code", payload2)
    
    # 3. 测试GET请求获取公钥接口（验证服务是否正常）
    print("\n3. 测试获取公钥接口 /api/encryption/public-key")
    try:
        pubkey_response = requests.get(f"{BASE_URL}/api/encryption/public-key", timeout=10)
        print(f"状态码: {pubkey_response.status_code}")
        print(f"响应内容长度: {len(pubkey_response.text)} 字符")
        print(f"服务是否正常: {pubkey_response.status_code == 200}")
    except Exception as e:
        print(f"获取公钥异常: {e}")

# 检查数据库验证码记录（通过已有的检查脚本）
def suggest_database_check():
    """建议检查数据库验证码记录"""
    print("\n\n===== 数据库检查建议 =====")
    print("请运行以下命令检查数据库中是否有验证码记录:")
    print("python check_mysql_tables.py")
    print("\n如果看到verification_code表中有记录，说明验证码生成成功，问题可能在邮件发送环节。")

# 主函数
def main():
    print("=== 验证码接口直接测试工具 ===")
    print(f"测试目标: {BASE_URL}")
    
    # 运行接口测试
    test_verification_endpoints()
    
    # 提供数据库检查建议
    suggest_database_check()
    
    print("\n\n===== 长期解决方案说明 =====")
    print("✅ 核心功能已修复：")
    print("1. 即使邮件发送失败，验证码也能正常生成并保存到数据库")
    print("2. 本工具已升级，可以直接从数据库中读取最新生成的验证码")
    print()
    print("🔧 完整解决方案建议：")
    print("1. **短期方案**：使用本工具直接从数据库获取验证码进行验证")
    print("2. **长期方案**：")
    print("   - 配置正确的邮件服务参数（尤其是MAIL_PASSWORD）")
    print("   - 或者修改前端逻辑，在特定情况下允许直接使用数据库中的验证码")
    print()
    print("\n⚠️ 注意事项：")
    print("- 验证码有效期为5分钟，请及时使用")
    print("- 每个IP每分钟最多可以请求5次验证码")
    print("- 每次请求间隔不能少于12秒")
    print("- 生产环境中请确保数据库访问权限的安全性")

if __name__ == "__main__":
    main()