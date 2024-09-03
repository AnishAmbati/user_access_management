package uam_pro.uam_pro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
 
public class user {
	public static String firstname;
	public static String lastname;
	public String email;
	public String pwd;
	public String cpwd;
	public String usertype;
	private String pswd;
	user(){
		
	}
	user(String pwd,String cpwd){
		this.pwd=pwd;
		this.cpwd=cpwd;
	}
	user(String firstname,String lastname,String email,String pwd,String cpwd){
		this.firstname=firstname;
		this.lastname=lastname;
		this.email=email;
		this.pwd=pwd;
		this.cpwd=cpwd;
	}
	
	user(String firstname,String lastname,String email){
		this.firstname=firstname;
		this.lastname=lastname;
		this.email=email;
		this.pwd=firstname+lastname;
	}
	String stored_username="";
	public void store_username(String username) {
		stored_username=username;
	}
	public boolean password_match() {
		if(pwd.equals(cpwd))return false;
		return true;
	}
	
	public static String check_username() throws Exception {
		Connection c=DB.connect();
		String username=firstname+"."+lastname;
		String s="select count(*) from user where username like ?";
		PreparedStatement pst=c.prepareStatement(s);
		pst.setString(1, username+"%");
		ResultSet rs=pst.executeQuery();
		int count=0;
		if(rs.next()) {
			count=rs.getInt(1);
		}
		if(count!=0) {
			username=username+count;
		}
        return username;
	}
	
	
	public String user_type() throws Exception{
		Connection c=DB.connect();
		String query="select count(*) from user";
		PreparedStatement ps=c.prepareStatement(query);
		ResultSet rs=ps.executeQuery();
		if(rs.next()) {
			if(rs.getInt(1)==0)
				return "Admin";
			else {
				return "User";
			}
		}
		else {
			return "";
		}
	}
	
	
	public static String encrypt(String password) {
        String input = "ABCDEFGHI\n" +
                       "JKLMNOPQR\n" +
                       "STUVWXYZa\n" +
                       "bcdefghij\n" +
                       "klmnopqrs\n" +
                       "tuvwxyz01\n" +
                       "23456789`\n" +
                       "~!@#$%^&*\n" +
                       "()-_=+[{]\n" +
                       "}|;:',<.>\n" +
                       "/?";
        StringBuilder s = new StringBuilder();
        String[] lines = input.split("\n");
        for(int i=0;i<password.length();i++) {
        	for (int lineNumber = 0; lineNumber < lines.length; lineNumber++) {
                String line = lines[lineNumber];
                for (int charNumber = 0; charNumber < line.length(); charNumber++) {
                    if(password.charAt(i)==line.charAt(charNumber)) {
                    	s=s.append(lineNumber+1);
                    	s=s.append(charNumber+1);
                    }
                }
            }
        }
        return s.toString();
    }
	public String createUser() throws Exception{
		Connection c=DB.connect();
		String username=check_username();
		String usertype=user_type();
		LocalDate date=LocalDate.now();
    	String query="insert into user (firstname,lastname,email,pwd,username,doj,usertype,managername,priority)values(?,?,?,?,?,?,?,?,?)";
    	PreparedStatement ps=c.prepareStatement(query);
    	ps.setString(1, firstname);
    	ps.setString(2, lastname);
    	ps.setString(3, email);
    	ps.setString(4, encrypt(pwd));
    	ps.setString(5, username);
    	ps.setString(6, date.toString());
    	ps.setString(7, usertype);
    	ps.setString(8, null);
    	if(usertype.equals("Admin")) {
    		ps.setInt(9, 1);
    	}
    	else {
    		ps.setInt(9, 0);
    	}
    	ps.executeUpdate();
    	return username;
	}
	 
	
	public boolean authenticate(String username,String password) throws Exception {
		//stored_username=username;
		String query = "SELECT * FROM user WHERE pwd = ?";
        Connection con=DB.connect();
        user obj=new user();
        PreparedStatement pst = con.prepareStatement(query);
        String ans=obj.encrypt(password);
        pst.setString(1, ans);
        ResultSet rs = pst.executeQuery();
        String z="";
        boolean flag=false;
        while (rs.next()) {
        	z=rs.getString("username");
        	if(z.equals(username)) {
        		flag=true;
        		z=username;
        		break;
        	}
        }
        return flag;
	}
	
	public boolean userExists(String username) throws Exception {
		Connection c=DB.connect();
		String query="select username from user where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, username);
		ResultSet rs=pst.executeQuery();
		if(rs.next()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String emailExists(String username) throws Exception {
		Connection c=DB.connect();
		String query="select email from user where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, username);
		ResultSet rs=pst.executeQuery();
		String s="";
		if(rs.next()) {
			s=rs.getString("email");
		}
		return s;
	}
	
	public String resetpassword(String username,String password) throws Exception {
		String encryptedPassword=user.encrypt(password);
		Connection c=DB.connect();
		String query="update user set pwd=? where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, encryptedPassword);
		pst.setString(2, username);
		pst.executeUpdate();
		return "<p style=color:blue>Password is successfully changed Click on Back to login</p>";
	}
	
	public String checkforuseradd(String username) throws Exception {
		String query="select * from user where username=?";
		Connection c=DB.connect();
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, username);
		ResultSet rs=pst.executeQuery();
		String fname="";
		String lname="";
		while(rs.next()) {
			fname=rs.getString("firstname");
			lname=rs.getString("lastname");
		}
		String z=fname;
		z+=lname;
		return z;
	}
	
	public String changepasswordaddeduser() throws Exception {
		String s="<form action='changepasswordaddeduser1' method='post'>";
		s+="<input type='password' name='newpassword' placeholder='Enter password to change' required minlength='8'  maxlength='20' pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}' required>";
		s+="<small>Password must be between 8 and 20 characters, include at least one digit, one lowercase letter, and one uppercase letter.</small>";
        s+="<br>";
        s+="<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
        s+="<button type='submit' >Submit</button>";
        s+="</form>";
        return s;	
	}
	
	public String changepasswordaddesuser1(String name,String p) throws Exception {
		Connection c=DB.connect();
		String query="update user set pwd=? where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, encrypt(p));
		pst.setString(2, name);
		pst.executeUpdate();
		return "<h1>Password successfully updated.</h1>";
		
	}

	
	
	
	//Admin function starts	
	public String listofusers() throws Exception {
		Connection c=DB.connect();
		String query="select * from user";
		PreparedStatement pst=c.prepareStatement(query);
		ResultSet rs=pst.executeQuery();
		String show="<table border='1'><tr><th>Username</th><th>Date of Joined</th><th>UserType</th></tr>";
		while(rs.next()) {
			show+="<tr>";
    		show+="<td>"+rs.getString(5)+"</td>";
    		show+="<td>"+rs.getString(6)+"</td>";
    		show+="<td>"+rs.getString(7)+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
	}
	 
	
	public String listofresources() throws Exception {
		Connection c=DB.connect();
		String query1="select * from resource";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs=pst1.executeQuery();
		String show="<table border='1'><tr><th>ResourceName</th></tr>";
		while(rs.next()) {
			show+="<tr>";
    		show+="<td>"+rs.getString(1)+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
		
	}
	
	public String listofmanagers() throws Exception {
		Connection c=DB.connect();
		String query1="select * from user where usertype=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, "Manager");
		ResultSet rs=pst1.executeQuery();
		
		if(rs.equals(null)) {
			return "No resources";
		}
		String show="<table border='1'><tr><th>ManagerName</th></tr>";
		while(rs.next()) {
			show+="<tr>";
    		show+="<td>"+rs.getString(5)+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
		
	}
	
//	public String listofrequests() throws Exception {
//		Connection c=DB.connect();
//		String query1="select * from request";
//		PreparedStatement pst1=c.prepareStatement(query1);
//		ResultSet rs=pst1.executeQuery();
//		if(rs.equals(null)) {
//			return "No resources";
//		}
//		String show="<table border='1'><tr><th>RequestID</th><th>RequestedFrom</th><th>RequesteeType</th><th>DateOfRequesting</th><th>RequestName</th><th>Approval</th></tr>";
//		while(rs.next()) {
//			show+="<tr>";
//    		show+="<td>"+rs.getString(1)+"</td>";
//    		show+="<td>"+rs.getString(2)+"</td>";
//    		show+="<td>"+rs.getString(6)+"</td>";
//    		show+="<td>"+rs.getString(3)+"</td>";
//    		show+="<td>"+rs.getString(5)+"</td>";
//    		show+="<td>"+rs.getString(4)+"</td>";
//    		show+="</tr>";
//		}
//		show+="</table>";
//		return show;
//		
//	}
	
	
	
	
	
	public String listofrequests() throws Exception {
	    Connection c = DB.connect();
	    
	    // Query to get requests based on the username
	    String query = "select * from request";
	    PreparedStatement pst = c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    
	    // StringBuilder to build the HTML table for requests and action buttons
	    StringBuilder html = new StringBuilder("<table class='requests-table'>");
	    
	    // Table headers
	    html.append("<thead>");
	    html.append("<table border='1'>");
	    html.append("<tr>");
	    html.append("<th>Request ID</th>");
	    html.append("<th>Requested From</th>");
	    html.append("<th>Requestee Type</th>");
	    html.append("<th>Date Of Requesting</th>");
	    html.append("<th>Request Name</th>");
	    html.append("<th>Approval</th>");
	    html.append("<th>Actions</th>");
	    html.append("</tr>");
	    html.append("</thead>");
	    
	    // Table body
	    html.append("<tbody>");
	    
	    while (rs.next()) {
	        int requestId = rs.getInt("requestid");
	        String requestedFrom = rs.getString("requestedfrom");
	        String requesteeType = rs.getString("requesteetype");
	        String dateOfRequesting = rs.getString("dor");
	        String requestName = rs.getString("requestname");
	        String approval = rs.getString("approvalstatus");
	        
	        html.append("<tr>");
	        html.append("<td>").append(requestId).append("</td>");
	        html.append("<td>").append(requestedFrom).append("</td>");
	        html.append("<td>").append(requesteeType).append("</td>");
	        html.append("<td>").append(dateOfRequesting).append("</td>");
	        html.append("<td>").append(requestName).append("</td>");
	        html.append("<td>").append(approval).append("</td>");
	        html.append("<td>");
	        
	        
	        html.append("<form action='accept' method='post' style='display:inline;'>");
	        html.append("<input type='hidden' name='requestId' value='").append(requestId).append("' />");
	        html.append("<input type='hidden' name='requestName' value='").append(requestName).append("' />");
	        html.append("<input type='hidden' name='requestedFrom' value='").append(requestedFrom).append("' />");
	        html.append("<button type='submit'>Accept</button>");
	        html.append("</form>");
	        
	        html.append("<form action='reject' method='post' style='display:inline;'>");
	        html.append("<input type='hidden' name='requestId' value='").append(requestId).append("' />");
	        //html.append("<input type='hidden' name='requestedFrom' value='").append(requestedFrom).append("' />");
	        html.append("<button type='submit'>Reject</button>");
	        html.append("</form>");
	        
	        html.append("</td>");
	        html.append("</tr>");
	    }
	    
	    html.append("</tbody>");
	    html.append("</table>");
	    
	    return html.toString();
	}
	
	public String removeusers(String name) throws Exception {
		String dropDown="<form action='removeusers1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select An User To Delete:</label>"+
				"<select id='dropdown' name='options'>";
		Connection c=DB.connect();
		String query1="select username from user where priority!=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setInt(1,1);
		ResultSet rs=pst1.executeQuery();
		boolean flag=false;
		while(rs.next()) {
			String value=rs.getString(1);
        	dropDown+="<option value='"+value+"'>"+value+"</option>";
        	flag=true;
		}
		if(!flag) {
			dropDown += "<option value='' disabled selected>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
		}
		else {
			dropDown += "</select>" +
                    "<button type='submit'>Submit</button>";
		}
        dropDown+="</form>";
		return dropDown;

	}
	
	public String removeusers1(String options) throws Exception {
		Connection c=DB.connect();
		String query1="delete from user where username=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, options);
		pst1.executeUpdate();
		String query2="delete from request where requestedfrom=?";
		PreparedStatement pst2=c.prepareStatement(query2);
		pst2.setString(1, options);
		pst2.executeUpdate();
		String query3="delete from userresource where user=?";
		PreparedStatement pst3=c.prepareStatement(query3);
		pst3.setString(1, options);
		pst3.executeUpdate();
		return "<h1>User and his all belongings are deleted</h1>";
	}
	
	public boolean addresources(String resourcename) throws Exception {
		Connection c=DB.connect();
		String query1="insert into resource(resourcename,no_of_users) values(?,?)";
		String query2="select resourcename from resource where lower(resourcename)=?";
		PreparedStatement pst2=c.prepareStatement(query2);
		pst2.setString(1, resourcename);
		ResultSet rs=pst2.executeQuery();
		if(rs.next()) {
			return false;
		}
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, resourcename);
		pst1.setInt(2, 0);
		pst1.executeUpdate();
		return true;
	}
	
	
	
	public String changepasswordadmin() throws Exception {
		String s="<form action='changepassword1admin' method='post'>";
		s+="<input type='password' name='newpassword' placeholder='Enter password to change' required minlength='8'  maxlength='20' pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}' required>";
		s+="<small>Password must be between 8 and 20 characters, include at least one digit, one lowercase letter, and one uppercase letter.</small>";
        s+="<br>";
        s+="<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
        s+="<button type='submit' >Submit</button>";
        s+="</form>";
        return s;	
	}
	
	public String removeresources() throws Exception {
		String dropDown="<form action='removeresources1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select resourcename to delete:</label>"+
				"<select id='dropdown' name='options'>";
		Connection c=DB.connect();
		String query1="select * from resource";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs=pst1.executeQuery();
		boolean flag=false;
		while(rs.next()) {
			String value=rs.getString(1);
        	dropDown+="<option value='"+value+"'>"+value+"</option>";
        	flag=true;
		}
		if(!flag) {
			dropDown += "<option value='' disabled selected>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
		}
		else {
			dropDown += "</select>" +
                    "<button type='submit'>Submit</button>";
		}
		
		
        dropDown+="</form>";
		return dropDown;
	}
	
	public String removeresources1(String resourcename) throws Exception {
		Connection c=DB.connect();
		String query1="delete from resource where resourcename=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, resourcename);
		int x=pst1.executeUpdate();
		String query2="delete from request where requestname=?";
		PreparedStatement pst2=c.prepareStatement(query2);
		pst2.setString(1, resourcename);
		pst2.executeUpdate();
		String s;
		if(x>0) {
			s="<p style=color:blue>Resource is successfully deleted</p>";
		}
		else {
			s="<p style=color:red>There is no resource that you have entered</p>";
		}
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/removeresources'></head>" +
        "<body>" +
        s +
        "</body>" +
        "</html>";
		
	}
	
	public String removeresourcefromauser() throws Exception {
		Connection c = DB.connect();
	    String query = "select * from user where priority!=1"; 
	    PreparedStatement pst = c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='removeresourcefromauser1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select A user:</label>"+
				"<select id='dropdown' name='options1'>";
	    boolean hasUsers=false;
	    while (rs.next()) {
	    	hasUsers=true;
	        String value = rs.getString("username");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!hasUsers) {
	        dropDown+="<option value=''>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
		}
		else {
			dropDown += "</select>" +
                    "<button type='submit'>Select a User</button>";
		}
        dropDown+="</form>";
	    
	    return dropDown.toString();
	}
	
	public String removeresourcefromauser1(String options1) throws Exception {
		Connection c = DB.connect();
	    String query = "select resourcename from userresource where user=?";
	    PreparedStatement pst = c.prepareStatement(query);
	    pst.setString(1, options1);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='removeresourcefromauser2' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select A Resource To Delete:</label>"+
				"<select id='dropdown' name='options2'>";
	    boolean hasResource=false;
	    while (rs.next()) {
	    	hasResource=true;
	        String value = rs.getString("resourcename");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!hasResource) {
	        dropDown+="<option value=''>No Resource Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
		}
		else {
			dropDown += "</select>" +
                    "<button type='submit'>Delete Resource</button>";
		}
        dropDown+="</form>";
        return dropDown;
	}
	
	public String removeresourcefromauser2(String options2) throws Exception {
		Connection c = DB.connect();
	    String query = "delete from userresource where resourcename=?";
	    PreparedStatement pst = c.prepareStatement(query);
	    pst.setString(1, options2);
	    int n=pst.executeUpdate();
	    String z;
	    if(n>0) {
	    	z="<p style=color:blue>Resource is deleted successfully</p>";
	    }
	    else {
	    	z="<p style=color:red>Resource cannot be deleted</p>";
	    }
	    return "<html>" +
        "<head><meta http-equiv='refresh' content='1;url=http://localhost:8888/uam_pro/webapi/myresource/removeresourcefromauser'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	    
	}
	
	public String checkusersforaresource() throws Exception {
		Connection c = DB.connect();
	    String query = "select resourcename from resource";
	    PreparedStatement pst = c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='checkusersforaresource1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select A Resource To Check For Users:</label>"+
				"<select id='dropdown' name='options'>";
	    boolean hasResource=false;
	    while (rs.next()) {
	    	hasResource=true;
	        String value = rs.getString("resourcename");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!hasResource) {
	        dropDown+="<option value=''>No Resource Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Check Users</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    }
	}
	
	public String checkusersforaresource1(String options) throws Exception {
		Connection c=DB.connect();
		String query1="select * from userresource where resourcename=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, options);
		ResultSet rs=pst1.executeQuery();
		boolean flag=false;
		String show="<table border='1'><tr><th>Username</th></tr>";
		while(rs.next()) {
			flag=true;
			show+="<tr>";
    		show+="<td>"+rs.getString(1)+"</td>";
    		show+="</tr>";
		}
		if(!flag) {
			return "<h1>No Users Available</h1>";
		}
		show+="</table>";
		return show;
	}
	
	public String checkresourcesofanuser() throws Exception {
		Connection c = DB.connect();
	    String query = "select * from user where priority!=1";
	    PreparedStatement pst=c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='checkresourcesofanuser1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select A User To Check His Resources:</label>"+
				"<select id='dropdown' name='options'>";
	    boolean flag=false;
	    while (rs.next()) {
	    	flag=true;
	        String value = rs.getString("username");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!flag) {
	        dropDown+="<option value=''>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Check Resources</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    }
	}
	
	public String checkresourcesofanuser1(String options) throws Exception {
		Connection c=DB.connect();
		String query1="select resourcename from userresource where user=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1, options);
		ResultSet rs=pst1.executeQuery();
		String show="<table border='1'><tr><th>ResourceNames</th></tr>";
		while(rs.next()) {
			show+="<tr>";
    		show+="<td>"+rs.getString("resourcename")+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
	}
	
	
	public String assignresourcestoanuser() throws Exception {
		Connection c = DB.connect();
	    String query = "select * from user where priority!=1";
	    PreparedStatement pst=c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='assignresourcestoanuser1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select An User To Assign Resource:</label>"+
				"<select id='dropdown' name='options1'>";
	    boolean flag=false;
	    while (rs.next()) {
	    	flag=true;
	        String value = rs.getString("username");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!flag) {
	        dropDown+="<option value=''>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Check For Unavailable Resources</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    }
	}
	
	public String assignresourcestoanuser1(String options1) throws Exception {
		Connection c = DB.connect();
	    String query1 = "select resourcename from userresource where user=?";
	    PreparedStatement pst1 = c.prepareStatement(query1);
	    pst1.setString(1, options1);
	    ResultSet rs1= pst1.executeQuery();
	    Set<String> hs = new HashSet<>();
	    while (rs1.next()) {
	        hs.add(rs1.getString(1));
	    }
	    String query2 = "select requestname from request where requestedfrom=? and approvalstatus='pending'";
	    PreparedStatement pst2 = c.prepareStatement(query2);
	    pst2.setString(1, options1);
	    ResultSet rs2= pst2.executeQuery();
	    Set<String> hs2 = new HashSet<>();
	    while (rs2.next()) {
	        hs2.add(rs2.getString(1));
	    }
	    String query3 = "select resourcename from resource";
	    PreparedStatement pst3 = c.prepareStatement(query3);
	    ResultSet rs3 = pst3.executeQuery();
	    StringBuilder dropDown = new StringBuilder("<form action='assignresourcestoanuser2' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select resource name to request:</label>");
	    dropDown.append("<select id='dropdown' name='options2'>");
	    boolean flag = false;
	    while (rs3.next()) {
	        String resourceName = rs3.getString(1);
	        if (!hs.contains(resourceName) && !hs2.contains(resourceName)) {
	            dropDown.append("<option value='").append(resourceName).append("'>").append(resourceName).append("</option>");
	            flag = true;
	        }
	    }
	    if (!flag) {
	        dropDown.append("<option value=''>No Resources Available</option>");
	        dropDown.append("<button type='submit' disabled>Submit</button>");
	        dropDown.append("</form>");
	        return dropDown.toString();
	    }
	    else {
	    	dropDown.append("</select>");
            dropDown.append("<input type='hidden' name='options1' value='").append(options1).append("'>");
    	    dropDown.append("<button type='submit'>Assign</button>");
	    	dropDown.append("</form>");
	        return dropDown.toString();
	    }
	}
	
	public String assignresourcestoanuser2(String options1,String options2) throws Exception {
		Connection c=DB.connect();
		String query="insert into userresource values(?,?)";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, options1);
		pst.setString(2, options2);
		pst.executeUpdate();
		String z="<h1>Resource is Assigned Successfully</h1>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='1;url=http://localhost:8888/uam_pro/webapi/myresource/assignresourcestoansuser'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	
	public String makeasadminormanager() throws Exception {
		Connection c = DB.connect();
	    String query = "select * from user where priority!=1";
	    PreparedStatement pst=c.prepareStatement(query);
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='makeasadminormanager1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select An User To Assign Resource:</label>"+
				"<select id='dropdown' name='options1'>";
	    boolean flag=false;
	    while (rs.next()) {
	    	flag=true;
	        String value = rs.getString("username");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
	    if (!flag) {
	        dropDown+="<option value=''>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Check For UserType</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    }
	}
	
	public String makeasadminormanager1(String options1) throws Exception {
		Connection c=DB.connect();
		String query1="select usertype from user where username=?";
		PreparedStatement pst1=c.prepareStatement(query1);
		pst1.setString(1,options1);
		ResultSet rs=pst1.executeQuery();
		String typeofuser="";
		if(rs.next()) {
			typeofuser=rs.getString("usertype");
		}
		StringBuilder dropDown = new StringBuilder("<form action='makeasadminormanager2' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select New User Type:</label>");
	    dropDown.append("<select id='dropdown' name='options2'>");
	    if (typeofuser != null) {
	        switch (typeofuser) {
	            case "User":
	                dropDown.append("<option value='Admin'>Admin</option>");
	                dropDown.append("<option value='Manager'>Manager</option>");
	                break;
	            case "Manager":
	                dropDown.append("<option value='User'>User</option>");
	                dropDown.append("<option value='Admin'>Admin</option>");
	                break;
	            case "Admin":
	                dropDown.append("<option value='User'>User</option>");
	                dropDown.append("<option value='Manager'>Manager</option>");
	                break;
	            default:
	                dropDown.append("<option value=''>No valid user type found</option>");
	                break;
	        }
	    } else {
	        dropDown.append("<option value=''>No usertype found</option>");
	    }
	    dropDown.append("</select>");
	    dropDown.append("<input type='hidden' name='options1' value='").append(options1).append("'>");	    
	    dropDown.append("<button type='submit'>Change User Type</button>");
	    dropDown.append("</form>");
	    return dropDown.toString();
	}
	
	public String makeasadminormanager2(String options1,String options2) throws Exception {
		Connection c=DB.connect();
		String query1="update user set usertype=? where username=?";
		PreparedStatement pst=c.prepareStatement(query1);
		pst.setString(1, options2);
		pst.setString(2,options1);
		pst.executeUpdate();
		String query4="select username from user where priority=1";
		PreparedStatement pst4=c.prepareStatement(query4);
		ResultSet rs=pst4.executeQuery();
		String rootadmin="";
		while(rs.next()) {
			rootadmin=rs.getString("username");
		}
		if(options2.equals("Manager") || options2.equals("Admin")) {
			String query5="update user set managername=? where username=?";
			PreparedStatement pst5=c.prepareStatement(query5);
			pst5.setString(1, rootadmin);
			pst5.setString(2, options1);
			pst5.executeUpdate();
		}
		if(options2.equals("User")) {
			String query6="update user set managername=null where username=?";
			PreparedStatement pst6=c.prepareStatement(query6);
			pst6.setString(1, options1);
			pst6.executeUpdate();
		}
		String z="<h1>Successfully updated usertype</h1>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='1;url=http://localhost:8888/uam_pro/webapi/myresource/makeasadminormanager'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	//Admin functions end
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Manager functions start
	
	public String showteam(String name) throws Exception {
		Connection c=DB.connect();
		String query="select * from user where managername=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, name);
		ResultSet rs=pst.executeQuery();
		String show="<table border='1'><tr><th>UserName</th><th>UserType</th><th>DateOfJoined</th></tr>";
		while(rs.next()) {
			show+="<tr>";
    		show+="<td>"+rs.getString("username")+"</td>";
    		show+="<td>"+rs.getString("usertype")+"</td>";
    		show+="<td>"+rs.getString("doj")+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
	}
	
	public String getateammember() throws Exception {
		Connection c=DB.connect();
		String query="select username from user where usertype=? and managername is null";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1,"User");
		ResultSet rs=pst.executeQuery();
		String dropDown="<form action='getateammember1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select A User To Add Into Your Team</label>"+
				"<select id='dropdown' name='options'>";
	    boolean flag=false;
	    while (rs.next()) {
	    	flag=true;
	        String value = rs.getString("username");
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	    }
        if (!flag) {
	        dropDown+="<option value=''>No Users Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Add To Team</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    }
	}
	
	public String getateammember1(String options,String name) throws Exception {
		Connection c=DB.connect();
		String query="update user set managername=? where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1,name);
		pst.setString(2,options);
		pst.executeUpdate();
		String z="<p style=color:blue>Successfully User Added to Your Team</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/getateammember'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
		
	}
	
	public String requestforadmin(String name) throws Exception {
		Connection c = DB.connect();
	    String query = "SELECT * FROM request WHERE requestedfrom=? AND requestname=?";
	    PreparedStatement pst = c.prepareStatement(query);
	    pst.setString(1, name);
	    pst.setString(2, "Admin");
	    ResultSet rs = pst.executeQuery();
	    String dropDown="<form action='requestforadmin1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select For Requesting:</label>"+
				"<select id='dropdown' name='options'>";
	    boolean flag=false;
	    while (!rs.next()) {
	    	flag=true;
	        String value = "Admin";
	        dropDown+="<option value='"+value+"'>"+value+"</option>";
	        break;
	    }
        if (!flag) {
        	dropDown+="<option value=''>No Options Available</option>";
	        dropDown += "<button type='submit' disabled>Submit</button>";
	        dropDown+="</form>";
	        return dropDown;
	    }
	    else {
	    	dropDown += "</select>" +
                    "<button type='submit'>Request</button>";
	    	dropDown+="</form>";
	        return dropDown;
	    	
	    	
	    }
	}
	
	public String requestforresourcesmanager(String name) throws Exception {
	    Connection c = DB.connect();
	    String query1 = "select resourcename from userresource where user=?";
	    PreparedStatement pst1 = c.prepareStatement(query1);
	    pst1.setString(1, name);
	    ResultSet rs1= pst1.executeQuery();
	    Set<String> hs = new HashSet<>();
	    while (rs1.next()) {
	        hs.add(rs1.getString(1));
	    }
	    String query2 = "select requestname from request where requestedfrom=? and approvalstatus='pending'";
	    PreparedStatement pst2 = c.prepareStatement(query2);
	    pst2.setString(1, name);
	    ResultSet rs2= pst2.executeQuery();
	    Set<String> hs2 = new HashSet<>();
	    while (rs2.next()) {
	        hs2.add(rs2.getString(1));
	    }
	    String query3 = "select resourcename from resource";
	    PreparedStatement pst3 = c.prepareStatement(query3);
	    ResultSet rs3 = pst3.executeQuery();
	    StringBuilder dropDown = new StringBuilder("<form action='requestforresources1manager' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select resource name to request:</label>");
	    dropDown.append("<select id='dropdown' name='options'>");
	    boolean flag = false;
	    while (rs3.next()) {
	        String resourceName = rs3.getString(1);
	        if (!hs.contains(resourceName) && !hs2.contains(resourceName)) {
	            dropDown.append("<option value='").append(resourceName).append("'>").append(resourceName).append("</option>");
	            flag = true;
	        }
	    }
	    if (!flag) {
	        dropDown.append("<option value=''>No Resources Available</option>");
	        dropDown.append("<button type='submit' disabled>Submit</button>");
	        dropDown.append("</form>");
	        return dropDown.toString();
	    }
	    else {
	    	dropDown.append("</select>");
            dropDown.append("<button type='submit'>Request</button>");
	    	dropDown.append("</form>");
	        return dropDown.toString();
	    }
	}
	
	
	public String requestforresource1manager(String options,String s) throws Exception {
		Connection c=DB.connect();
		String query1="SELECT COUNT(*) FROM request";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs1=pst1.executeQuery();
		int count=0;
		String query2="insert into request(requestid,requestedfrom,dor,approvalstatus,requestname,requesteetype) values (?,?,?,?,?,?)";
		PreparedStatement pst2=c.prepareStatement(query2);
		if(rs1.next()) {
			String query3="SELECT max(requestid) FROM request";
			PreparedStatement pst3=c.prepareStatement(query3);
			ResultSet rs3=pst3.executeQuery();
			if(rs3.next()) {
				count=rs3.getInt(1);
			}
		}
		pst2.setInt(1,count+1);
		pst2.setString(2, s);
		LocalDate date=LocalDate.now();
		pst2.setString(3, date.toString());
		pst2.setString(4, "pending");
		pst2.setString(5, options);
		pst2.setString(6, "Manager");
		pst2.executeUpdate();
		String z="<p style=color:blue>Resource is requested</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/requestforresourcesmanager'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	public String removeownresourcesmanager(String uname) throws Exception {
		Connection c=DB.connect();
		String query="select resourcename from userresource where user=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, uname);
		ResultSet rs=pst.executeQuery();
		String dropDown="<form action='removeresource1manager' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select resourcename to delete:</label>"+
				"<select id='dropdown' name='options'>";
		boolean flag=false;
		while(rs.next()) {
			String value=rs.getString(1);
        	dropDown+="<option value='"+value+"'>"+value+"</option>";
        	flag=true;
		}
		if (!flag) {
	        dropDown+=("<option value=''>No Options Available</option>");
	        dropDown+=("<button type='submit' disabled>Submit</button>");
	        dropDown+=("</form>");
	        return dropDown;
	    }
	    else {
	    	dropDown+=("</select>");
            dropDown+=("<button type='submit'>Request</button>");
	    	dropDown+=("</form>");
	        return dropDown;
	    }
	}
	
	public String requestformanager(String options,String s) throws Exception {
		Connection c=DB.connect();
		String query1="SELECT COUNT(*) FROM request";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs1=pst1.executeQuery();
		int count=0;
		String query2="insert into request(requestid,requestedfrom,dor,approvalstatus,requestname,requesteetype) values (?,?,?,?,?,?)";
		PreparedStatement pst2=c.prepareStatement(query2);
		if(rs1.next()) {
			count=Integer.parseInt(rs1.getString(1));
		}
		pst2.setInt(1,count+1);
		pst2.setString(2, s);
		LocalDate date=LocalDate.now();
		pst2.setString(3, date.toString());
		pst2.setString(4, "pending");
		pst2.setString(5, options);
		pst2.setString(6, "Manager");
		pst2.executeUpdate();
		String z="<p style=color:blue>Resource is requested</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/requestforadmin'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	public String changepasswordmanager() throws Exception {
		String s="<form action='changepassword1manager' method='post'>";
		s+="<input type='password' name='newpassword' placeholder='Enter password to change' required minlength='8'  maxlength='20' pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}' required>";
		s+="<small>Password must be between 8 and 20 characters, include at least one digit, one lowercase letter, and one uppercase letter.</small>";
        s+="<br>";
        s+="<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
        s+="<button type='submit' >Submit</button>";
        s+="</form>";
        return s;	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//User page functions start
	
	public String myresources(String uname) throws Exception {
		Connection c=DB.connect();
		String query="select resourcename from userresource where user=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, uname);
		ResultSet rs=pst.executeQuery();
		String show="<table border='1'><tr><th>MyResoures</th></tr>";
		while(rs.next()) {
			show+="<tr>";
			show+="<td>"+rs.getString("resourcename")+"</td>";
			show+="</tr>";
		}
		show+="</table>";
		return show;
	}
	
	
	public String requestforresources(String name) throws Exception {
	    Connection c = DB.connect();
	    String query1 = "select resourcename from userresource where user=?";
	    PreparedStatement pst1 = c.prepareStatement(query1);
	    pst1.setString(1, name);
	    ResultSet rs1= pst1.executeQuery();
	    Set<String> hs = new HashSet<>();
	    while (rs1.next()) {
	        hs.add(rs1.getString(1));
	    }
	    String query2 = "select requestname from request where requestedfrom=? and approvalstatus='pending'";
	    PreparedStatement pst2 = c.prepareStatement(query2);
	    pst2.setString(1, name);
	    ResultSet rs2= pst2.executeQuery();
	    Set<String> hs2 = new HashSet<>();
	    while (rs2.next()) {
	        hs2.add(rs2.getString(1));
	    }
	    String query3 = "select resourcename from resource";
	    PreparedStatement pst3 = c.prepareStatement(query3);
	    ResultSet rs3 = pst3.executeQuery();
	    StringBuilder dropDown = new StringBuilder("<form action='requestforresources1' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select resource name to request:</label>");
	    dropDown.append("<select id='dropdown' name='options'>");
	    boolean hasAvailableResources = false;
	    while (rs3.next()) {
	        String resourceName = rs3.getString(1);
	        if (!hs.contains(resourceName) && !hs2.contains(resourceName)) {
	            dropDown.append("<option value='").append(resourceName).append("'>").append(resourceName).append("</option>");
	            hasAvailableResources = true;
	        }
	    }
	    if (!hasAvailableResources) {
	        dropDown.append("<option value=''>No Resources Available</option>");
	        dropDown.append("<button type='submit' disabled>Submit</button>");
	        dropDown.append("</form>");
	        return dropDown.toString();
	    }
	    else {
	    	dropDown.append("</select>");
            dropDown.append("<button type='submit'>Request</button>");
	    	dropDown.append("</form>");
	        return dropDown.toString();
	    }
	}


	public String requestforresource1(String options,String s) throws Exception {
		Connection c=DB.connect();
		String query1="SELECT COUNT(*) FROM request";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs1=pst1.executeQuery();
		int count=0;
		String query2="insert into request(requestid,requestedfrom,dor,approvalstatus,requestname,requesteetype) values (?,?,?,?,?,?)";
		PreparedStatement pst2=c.prepareStatement(query2);
		if(rs1.next()) {
			String query3="SELECT max(requestid) FROM request";
			PreparedStatement pst3=c.prepareStatement(query3);
			ResultSet rs3=pst3.executeQuery();
			if(rs3.next()) {
				count=rs3.getInt(1);
			}
		}
		pst2.setInt(1,count+1);
		pst2.setString(2, s);
		LocalDate date=LocalDate.now();
		pst2.setString(3, date.toString());
		pst2.setString(4, "pending");
		pst2.setString(5, options);
		pst2.setString(6, "User");
		pst2.executeUpdate();
		String z="<p style=color:blue>Resource is requested</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/requestforresources'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	
	public String checkapprovals(String uname) throws Exception {
		Connection c=DB.connect();
		String query="select * from request where requestedfrom=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, uname);
		ResultSet rs=pst.executeQuery();
		String show="<table border='1'><tr><th>RequestId</th><th>DateOfRequesting</th><th>RequestName</th><th>ApprovalStatus</th></tr>";
		while(rs.next()) {
			show+="<tr>";
			show+="<td>"+rs.getInt(1)+"</td>";
    		show+="<td>"+rs.getString(3)+"</td>";
    		show+="<td>"+rs.getString(5)+"</td>";
    		show+="<td>"+rs.getString(4)+"</td>";
    		show+="</tr>";
		}
		show+="</table>";
		return show;
	}
	
	
	
	public String requestfor(String uname) throws Exception {
		Connection c = DB.connect();
	    String queryAdmin = "SELECT 1 FROM request WHERE requestedfrom=? AND requestname=?";
	    String queryManager = "SELECT 1 FROM request WHERE requestedfrom=? AND requestname=?";
	    PreparedStatement pstAdmin = c.prepareStatement(queryAdmin);
	    pstAdmin.setString(1, uname);
	    pstAdmin.setString(2, "Admin");
	    ResultSet rsAdmin = pstAdmin.executeQuery();
	    PreparedStatement pstManager = c.prepareStatement(queryManager);
	    pstManager.setString(1, uname);
	    pstManager.setString(2, "Manager");
	    ResultSet rsManager = pstManager.executeQuery();
	    StringBuilder dropDown = new StringBuilder("<form action='requestfor1' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select resource name to request:</label>");
	    dropDown.append("<select id='dropdown' name='options'>");
	    boolean adminRequested = rsAdmin.next();
	    boolean managerRequested = rsManager.next();
	    if (adminRequested && managerRequested) {
	    	dropDown.append("<option value=''>No Resources Available</option>");
	        dropDown.append("<button type='submit' disabled>Submit</button>");
	        dropDown.append("</form>");
	        return dropDown.toString();
	    } else if (!adminRequested && !managerRequested) {
	        dropDown.append("<option value='Admin'>Admin</option>");
	        dropDown.append("<option value='Manager'>Manager</option>");
	    } else if (adminRequested) {
	        dropDown.append("<option value='Manager'>Manager</option>");
	    } else if (managerRequested) {
	        dropDown.append("<option value='Admin'>Admin</option>");
	    }
	    dropDown.append("</select>");
	    dropDown.append("<button type='submit'>Submit</button>");
	    dropDown.append("</form>");
	    
	    return dropDown.toString();
	}
	
	public String requestfor1(String options,String s) throws Exception {
		Connection c=DB.connect();
		String query1="SELECT COUNT(*) FROM request";
		PreparedStatement pst1=c.prepareStatement(query1);
		ResultSet rs1=pst1.executeQuery();
		int count=0;
		String query2="insert into request(requestid,requestedfrom,dor,approvalstatus,requestname,requesteetype) values (?,?,?,?,?,?)";
		PreparedStatement pst2=c.prepareStatement(query2);
		if(rs1.next()) {
			count=Integer.parseInt(rs1.getString(1));
		}
		pst2.setInt(1,count+1);
		pst2.setString(2, s);
		LocalDate date=LocalDate.now();
		pst2.setString(3, date.toString());
		pst2.setString(4, "pending");
		pst2.setString(5, options);
		pst2.setString(6, "User");
		pst2.executeUpdate();
		String z="<p style=color:blue>Resource is requested</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/requestfor'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	public String removeownresource(String uname) throws Exception {
		Connection c=DB.connect();
		String query="select resourcename from userresource where user=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, uname);
		ResultSet rs=pst.executeQuery();
		String dropDown="<form action='removeresource1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select resourcename to delete:</label>"+
				"<select id='dropdown' name='options'>";
		boolean flag=false;
		while(rs.next()) {
			String value=rs.getString(1);
        	dropDown+="<option value='"+value+"'>"+value+"</option>";
        	flag=true;
		}
		if (!flag) {
	        dropDown+=("<option value=''>No Options Available</option>");
	        dropDown+=("<button type='submit' disabled>Submit</button>");
	        dropDown+=("</form>");
	        return dropDown;
	    }
	    else {
	    	dropDown+=("</select>");
            dropDown+=("<button type='submit'>Request</button>");
	    	dropDown+=("</form>");
	        return dropDown;
	    }
	}
	
	public String removeresource1(String option) throws Exception {
		Connection c=DB.connect();
		String query="delete from userresource where resourcename=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, option);
		pst.executeUpdate();
		String z="<p style=color:blue>Resource is removed successfully</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/webapi/myresource/removeownresource'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	public String changepassword() throws Exception {
		String s="<form action='changepassword1' method='post'>";
		s+="<input type='password' name='newpassword' placeholder='Enter password to change' required minlength='8'  maxlength='20' pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}' required>";
		s+="<small>Password must be between 8 and 20 characters, include at least one digit, one lowercase letter, and one uppercase letter.</small>";
        s+="<br>";
        s+="<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
        s+="<button type='submit' >Submit</button>";
        s+="</form>";
        return s;	
	}
	
	public String changepassword1(String name,String p) throws Exception {
		Connection c=DB.connect();
		String query="update user set pwd=? where username=?";
		PreparedStatement pst=c.prepareStatement(query);
		pst.setString(1, encrypt(p));
		pst.setString(2, name);
		pst.executeUpdate();
		String z="<p style=color:blue>Password successfully updated.</p>";
		return "<html>" +
        "<head><meta http-equiv='refresh' content='2;url=http://localhost:8888/uam_pro/'></head>" +
        "<body>" +
        z +
        "</body>" +
        "</html>";
	}
	
	
	
	
//	public String myresources() throws Exception {
//		Connection c=DB.connect();
//		String query1="SELECT resourcename FROM userresource";
//		PreparedStatement pst1=c.prepareStatement(query1);
//		ResultSet rs1=pst1.executeQuery();
//		String s="";
//	}
	
	
	
//		public String getUsers() throws Exception {
//	    	Connection c=DB.connect();
//	    	PreparedStatement ps=c.prepareStatement("select name from emp");
//	    	ResultSet rs=ps.executeQuery();
//	    	String dropDown="<select name='users'>";
//	    	while(rs.next()) {
//	    		String value=rs.getString(1);
//	    		dropDown+="<option value='"+value+"'>"+value+"</option>";
//	    	}
//	    	dropDown+="</select>";
//	           return dropDown;
//	    }
	
	
	
	
	
}