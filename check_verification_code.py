import pymysql
import time
import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'Wenguang-1122',  # 从check_mysql_tables.py获取的正确密码
    'database': 'wenji_db',
    'charset': 'utf8mb4'
}

# 查询verification_code表记录
def check_verification_code_table():
    """检查verification_code表中的记录"""
    print("=== 检查verification_code表记录 ===")
    print(f"查询时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    
    try:
        # 连接数据库
        connection = pymysql.connect(**db_config)
        cursor = connection.cursor()
        
        print("\n1. 表结构查询:")
        # 查询表结构
        cursor.execute("DESCRIBE verification_code")
        structure = cursor.fetchall()
        print("字段名\t类型\t空\t默认值\t键")
        print("-" * 60)
        for row in structure:
            print(f"{row[0]}\t{row[1]}\t{row[2]}\t{row[4]}\t{row[3]}")
        
        print("\n2. 最新记录查询:")
        # 查询最新的10条记录
        cursor.execute("SELECT id, email, type, code, created_time, expired_time, request_ip, used FROM verification_code ORDER BY created_time DESC LIMIT 10")
        records = cursor.fetchall()
        
        if not records:
            print("verification_code表中没有记录")
        else:
            print(f"找到 {len(records)} 条记录:")
            print("ID\t邮箱\t\t\t类型\t验证码\t创建时间\t\t\t\t\t\t请求IP\t是否使用")
            print("-" * 120)
            for row in records:
                created_time = row[4].strftime('%Y-%m-%d %H:%M:%S') if row[4] else "None"
                email = row[1] if row[1] else "None"
                print(f"{row[0]}\t{email}\t{row[2]}\t{row[3]}\t{created_time}\t{row[6]}\t{row[7]}")
        
        print("\n3. 按邮箱查询:")
        # 按测试邮箱查询
        test_email = "test_verification@example.com"
        cursor.execute("SELECT id, email, type, code, created_time, request_ip FROM verification_code WHERE email = %s", (test_email,))
        email_records = cursor.fetchall()
        
        if not email_records:
            print(f"没有找到邮箱为 {test_email} 的记录")
        else:
            print(f"找到邮箱 {test_email} 的记录数: {len(email_records)}")
            for row in email_records:
                created_time = row[4].strftime('%Y-%m-%d %H:%M:%S') if row[4] else "None"
                print(f"  ID: {row[0]}, 类型: {row[2]}, 验证码: {row[3]}, 创建时间: {created_time}, IP: {row[5]}")
        
        # 统计各类型数量
        print("\n4. 记录统计:")
        cursor.execute("SELECT type, COUNT(*) as count FROM verification_code GROUP BY type")
        type_stats = cursor.fetchall()
        print("类型\t数量")
        print("-" * 20)
        for row in type_stats:
            type_name = "注册验证" if row[0] == 1 else "密码重置" if row[0] == 2 else f"未知({row[0]})"
            print(f"{type_name}\t{row[1]}")
        
        # 检查时间窗口内的记录（防刷检查）
        print("\n5. 防刷检查分析:")
        # 查询最近10分钟内的记录
        cursor.execute("SELECT COUNT(*) as count FROM verification_code WHERE created_time >= DATE_SUB(NOW(), INTERVAL 10 MINUTE)")
        recent_count = cursor.fetchone()[0]
        print(f"最近10分钟内生成的验证码数量: {recent_count}")
        
        # 查询同一个IP最近的请求
        cursor.execute("SELECT request_ip, COUNT(*) as count FROM verification_code WHERE created_time >= DATE_SUB(NOW(), INTERVAL 10 MINUTE) GROUP BY request_ip ORDER BY count DESC LIMIT 5")
        ip_stats = cursor.fetchall()
        if ip_stats:
            print("最近10分钟内请求最频繁的IP:")
            for row in ip_stats:
                print(f"  IP: {row[0]}, 请求次数: {row[1]}")
        
    except Exception as e:
        print(f"数据库查询错误: {e}")
    finally:
        # 关闭连接
        if 'connection' in locals() and connection.open:
            cursor.close()
            connection.close()
            print("\n数据库连接已关闭")

# 主函数
def main():
    check_verification_code_table()
    
    print("\n\n===== 验证码生成分析 ====""")
    print("根据查询结果，您可以判断:")
    print("1. 如果表中有记录，说明验证码已成功生成")
    print("2. 如果记录中的email和type与您请求的一致，说明参数没有问题")
    print("3. 如果最近10分钟内有大量记录，可能触发了IP防刷限制")
    print("\n如果验证码已生成但接口返回400，很可能是邮件发送环节出现问题。")

if __name__ == "__main__":
    main()