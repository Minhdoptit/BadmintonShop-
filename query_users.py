import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="123456",
        database="badminton_shop"
    )
    cursor = conn.cursor()
    
    print("--- USERS IN DATABASE ---")
    cursor.execute("SELECT user_id, username, role, fullName FROM users")
    for row in cursor.fetchall():
        print(row)
        
    print("\n--- ORDERS COUNT ---")
    cursor.execute("SELECT COUNT(*) FROM orders")
    print("Total orders:", cursor.fetchone()[0])
    
    conn.close()
except Exception as e:
    print("Error:", e)
