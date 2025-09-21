import pymysql

# MySQL数据库连接信息
host = 'localhost'
user = 'root'
password = 'Wenguang-1122'
database = 'wenji_db'

# 连接到MySQL数据库
try:
    connection = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=database,
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )
    
    print(f"成功连接到数据库: {database}")
    
    # 检查数据库中的表
    with connection.cursor() as cursor:
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()
        
        if tables:
            print(f"数据库 {database} 中的表:")
            for table in tables:
                for key, value in table.items():
                    print(f"- {value}")
        else:
            print(f"数据库 {database} 中没有表")
            
finally:
    if connection and connection.open:
        connection.close()
        print("数据库连接已关闭")