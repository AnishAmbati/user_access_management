package uam_pro.uam_pro;
import java.sql.Connection;
import java.sql.DriverManager;
public class DB {
	
	public static Connection connect() throws Exception {
		String driver="com.mysql.cj.jdbc.Driver",url="jdbc:mysql://localhost:3306/uam",userName="root",password="root";
		Class.forName(driver);
		Connection c=DriverManager.getConnection(url,userName,password);
		return c;
	}
}
