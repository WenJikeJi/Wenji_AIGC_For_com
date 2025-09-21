import requests
import json

# 定义请求URL和数据
url = 'http://localhost:8080/api/verify-code/generate'
headers = {'Content-Type': 'application/json'}
payload = {"email": "test_verification@example.com", "type": 1}

# 发送POST请求
print(f"发送请求到: {url}")
print(f"请求头: {headers}")
print(f"请求体: {payload}")

response = requests.post(url, headers=headers, json=payload)

# 打印响应信息
print(f"状态码: {response.status_code}")
print(f"响应内容: {response.text}")

# 尝试解析JSON响应
if response.headers.get('Content-Type') == 'application/json':
    try:
        json_response = response.json()
        print(f"JSON解析结果: {json.dumps(json_response, indent=2, ensure_ascii=False)}")
    except json.JSONDecodeError:
        print("JSON解析失败")