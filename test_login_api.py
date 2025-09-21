import json
import requests
import base64

# 测试登录接口
def test_login():
    # 登录URL
    login_url = "http://localhost:8080/api/auth/login"
    
    # 准备登录数据（使用明文密码的Base64编码，因为我们没有RSA加密环境）
    plain_password = "admin123"  # 我们创建的测试用户密码
    # 直接Base64编码代替RSA加密，因为我们的数据初始化使用的是普通密码加密
    base64_password = base64.b64encode(plain_password.encode('utf-8')).decode('utf-8')
    
    login_data = {
        "account": "admin",
        "encryptedPassword": base64_password,
        "role": 0
    }
    
    print(f"使用密码: {plain_password}")
    print(f"Base64编码后: {base64_password}")
    
    # 设置请求头
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    
    try:
        # 发送POST请求
        print("正在发送登录请求...")
        response = requests.post(login_url, headers=headers, data=json.dumps(login_data))
        
        # 打印响应状态码
        print(f"响应状态码: {response.status_code}")
        
        # 打印响应内容
        print("响应内容:")
        try:
            response_json = response.json()
            print(json.dumps(response_json, ensure_ascii=False, indent=2))
        except json.JSONDecodeError:
            print(response.text)
        
        # 打印完整响应头用于诊断
        print("\n完整响应头:")
        for header, value in response.headers.items():
            print(f"{header}: {value}")
            
        # 分析响应
        if response.status_code == 200:
            print("登录成功!")
            if "token" in response_json:
                print(f"获取到JWT令牌: {response_json['token']}")
        else:
            print("登录失败!")
            # 检查是否是跨域错误
            if "Access-Control-Allow-Origin" not in response.headers:
                print("警告: 响应头中缺少Access-Control-Allow-Origin")
            else:
                print(f"Access-Control-Allow-Origin: {response.headers['Access-Control-Allow-Origin']}")
                
    except Exception as e:
        print(f"请求出错: {str(e)}")

# 测试获取公钥接口（用于加密密码）
def test_get_public_key():
    public_key_url = "http://localhost:8080/api/encryption/public-key"
    
    try:
        response = requests.get(public_key_url)
        print(f"获取公钥响应状态码: {response.status_code}")
        if response.status_code == 200:
            public_key_data = response.json()
            print("公钥数据:")
            print(json.dumps(public_key_data, ensure_ascii=False, indent=2))
            return public_key_data
        else:
            print("获取公钥失败")
            return None
    except Exception as e:
        print(f"获取公钥出错: {str(e)}")
        return None

if __name__ == "__main__":
    print("===== 测试登录接口 =====")
    # 先测试获取公钥
    print("\n1. 测试获取公钥接口")
    public_key_data = test_get_public_key()
    
    print("\n2. 测试登录接口")
    test_login()
    
    print("\n===== 测试完成 =====")
    print("\n注意：")
    print("1. 登录请求中的encryptedPassword使用的是Base64编码的明文密码")
    print("2. 如果登录失败显示'账号或密码错误'，请检查账号和加密密码是否正确")
    print("3. 如果不再显示跨域错误(400)，说明CORS配置已修复")
    print("4. 当前测试使用的admin账号密码为admin123")