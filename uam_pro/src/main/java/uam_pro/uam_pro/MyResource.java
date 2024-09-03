package uam_pro.uam_pro;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    //Database Connection
    @GET
    @Path("db")
    public String connectionDB() throws Exception {
    	Connection c=DB.connect();
    	if(c!=null) {
    		return "connected";
    	}
    	else {
    		return "not connected";
    	}
    }
    
    
    //registration
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String registration_page(@FormParam("firstname")String firstname,
    		@FormParam("lastname")String lastname,
    		@FormParam("email")String email,
    		@FormParam("password")String password,
    		@FormParam("confirmpassword")String confirmpassword,
    		@Context HttpServletRequest req) throws Exception {
    	user obj=new user(firstname,lastname,email,password,confirmpassword);
    	FileUtils fobj=new FileUtils();
    	
    	if (obj.password_match()) {
            // Passwords do not match
    		return fobj.addDataAfter(108, "Passwords did not match Try to register again", "webapp/registration.html", req);
        }
    	String s=obj.createUser();
    	return fobj.addDataAfter(108, "Your username is: "+s+" Click on login here", "webapp/registration.html", req);
    }
	
	
    //login
	@Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces(MediaType.TEXT_PLAIN)
    public void login_page(@FormParam("username") String username,
                                     @FormParam("password") String password,
                                     @Context HttpServletRequest req,
                                     @Context HttpServletResponse response) throws Exception {
            user obj=new user();
    		HttpSession session=req.getSession();
    		session.setAttribute("uname", username);
    		String p=obj.checkforuseradd(username);
    		
    		String pq=user.encrypt(p);
    		
    		if(pq.equals(user.encrypt(password))) {
    			String s="http://localhost:8888/uam_pro/useraddedbyadmin.jsp";
    			response.sendRedirect(s);
    		}
    		else {
    			if(obj.authenticate(username, password)) {
                	String query = "SELECT * FROM user WHERE username = ?";
                    Connection con=DB.connect();
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setString(1, username);
                    ResultSet rs = pst.executeQuery();
                    if(rs.next()) {
                    	if("Admin".equals(rs.getString("usertype"))) {
                    		String s="http://localhost:8888/uam_pro/Admin.jsp";
                    		response.sendRedirect(s);
                    	}
                    	else if("User".equals(rs.getString("usertype"))){
                    		String s="http://localhost:8888/uam_pro/User.jsp";
                    		response.sendRedirect(s);
                    	}
                    	else {
                    		String s="http://localhost:8888/uam_pro/Manager.jsp";
                    		response.sendRedirect(s);
                    		
                    	}
                    }
                }
                else {
                	
                	response.sendRedirect("http://localhost:8888/uam_pro/usernotfound.html");
                }
    		}
            
    }
	
	@GET
	@Path("logout")
    public void logout(@Context HttpServletRequest request,@Context HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        session.removeAttribute("uname");
        response.sendRedirect("http://localhost:8888/uam_pro/index.jsp");
    }

	@POST
	@Path("resetpassword")
	public String reset_password(@Context HttpServletRequest req,@FormParam("password")String password,@FormParam("cpassword")String cpassword,@FormParam("username")String username,@FormParam("email")String email) throws Exception {
		user obj=new user(password,cpassword);
		FileUtils fobj=new FileUtils();
		if(obj.userExists(username)) {
			if(obj.emailExists(username).equals(email)) {
				if(obj.password_match()) {
					return fobj.addDataAfter(104, "<p style='color:red'> Passwords did not match </p>", "webapp/ForgotPassword.html", req);
				}
				String s=obj.resetpassword(username,password);
				return fobj.addDataAfter(104, s, "webapp/ForgotPassword.html", req);
			}
			else {
				return fobj.addDataAfter(104, "<p style='color:red'>User Found but Entered wrong email.Try with correct email</p>", "webapp/ForgotPassword.html", req);
			}
		}
		else {
			return fobj.addDataAfter(104, "<p style='color:red'>User Not Found Try with correct username</p>", "webapp/ForgotPassword.html", req);
		}
	}
    
    @Path("changepasswordaddeduser1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_password_added_user1(@FormParam("newpassword")String p,@FormParam("confirmpassword")String q,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	if(p.equals(q)) {
    		//HttpSession session=req.getSession();
    		String name=session.getAttribute("uname").toString();
        	return obj.changepassword1(name,p)+"<br><a href='http://localhost:8888/uam_pro/index.jsp'>Login Again</a>";
    	}
    	return "<h1>Passwords did not match</h1>"+"<br><a href='http://localhost:8888/uam_pro/webapi/myresource/changepasswordaddeduser1'>Try again</a>";
    	
    }
	
	
	
	
	
	
	
	//Admin functions
	
	
	
	
	
	
    @Path("listofusers")
    @GET
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces(MediaType.TEXT_PLAIN)
    public String list_of_users(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.listofusers();
		return fobj.addDataAfter(103, "<h2>List of Users and Their usertypes:</h2>"+s, "webapp/Adminbase.html", req);
	}
    
    @Path("listofresources")
    @GET
    public String list_of_resources(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.listofresources();
		return fobj.addDataAfter(103, "<h2>List of resources:</h2>"+s, "webapp/Adminbase.html", req);
	}
    
    @Path("listofmanagers")
    @GET
    public String list_of_managers(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.listofmanagers();
		return fobj.addDataAfter(103, "<h2>List of Managers:</h2>"+s, "webapp/Adminbase.html", req);
		
	}
    
    @Path("listofrequests")
    @GET
    public String list_of_requests(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.listofrequests();
		return fobj.addDataAfter(103, "<h2>List of Requests:</h2>"+s, "webapp/Adminbase.html", req);
		
	}
    
    @Path("accept")
    @POST
    public Response accept(@FormParam("requestId")String id,@FormParam("requestName")String request,@FormParam("requestedFrom")String name,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return Response
    	            .status(Response.Status.UNAUTHORIZED)
    	            .entity("<h1>You have logged out. To log in again click below link</h1>" +
    	                    "<br>" +
    	                    "<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>")
    	            .build();
    	}
    	Connection c=DB.connect();
    	if(!request.equals("Manager") && !request.equals("Admin")) {
    		String query1="insert into userresource(user,resourcename) values (?,?)";
        	PreparedStatement pst1=c.prepareStatement(query1);
        	pst1.setString(1, name);
        	pst1.setString(2, request);
        	pst1.executeUpdate();
    	}
    	if(request.equals("Manager") || request.equals("Admin")) {
    		String query3="update user set usertype=? where username=?";
    		PreparedStatement pst3=c.prepareStatement(query3);
    		pst3.setString(1, request);
    		pst3.setString(2, name);
    		pst3.executeUpdate();
    		String query4="select username from user where priority=1";
    		PreparedStatement pst4=c.prepareStatement(query4);
    		ResultSet rs=pst4.executeQuery();
    		String rootadmin="";
    		while(rs.next()) {
    			rootadmin=rs.getString("username");
    		}
    		String query5="update user set managername=? where username=?";
    		PreparedStatement pst5=c.prepareStatement(query5);
    		pst5.setString(1, rootadmin);
    		pst5.setString(2, name);
    		pst5.executeUpdate();     
    	}
    	String query2="delete from request where requestid=?";
    	PreparedStatement pst2=c.prepareStatement(query2);
    	pst2.setString(1, id);
    	pst2.executeUpdate();
    	return Response
    	        .seeOther(URI.create("http://localhost:8888/uam_pro/webapi/myresource/listofrequests"))
    	        .entity("<h1>Request is Accepted</h1>")
    	        .build();
    }
    
    
    @Path("reject")
    @POST
    public Response reject(@FormParam("requestId")String id,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return Response
    	            .status(Response.Status.UNAUTHORIZED)
    	            .entity("<h1>You have logged out. To log in again click below link</h1>" +
    	                    "<br>" +
    	                    "<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>")
    	            .build();
    	}
    	Connection c=DB.connect();
    	String query2="delete from request where requestid=?";
    	PreparedStatement pst2=c.prepareStatement(query2);
    	pst2.setString(1, id);
    	pst2.executeUpdate();
    	return Response
    	        .seeOther(URI.create("http://localhost:8888/uam_pro/webapi/myresource/listofrequests"))
    	        .entity("<h1>Request is Rejected</h1>")
    	        .build();
    }
    
    
    @Path("addusers")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String add_users(@FormParam("firstname")String firstname,
    		@FormParam("lastname")String lastname,
    		@FormParam("email")String email,
    		@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user(firstname,lastname,email);
    	String s="<p style=color:blue>User is added and his name is :</p>";
    	s+=obj.createUser();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(88, s, "webapp/addusers.html", req);
    }
    
    @Path("removeusers")
    @GET
    public String remove_users(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	String s=obj.removeusers(name);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("removeusers1")
    @POST
    public String remove_users1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	return obj.removeusers1(options);
    }
    
    
    @Path("addresources")
    @POST
    public String add_resources(@FormParam("resourcename")String resourcename,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String t;
		if(obj.addresources(resourcename)) {
			t="<p style=color:blue>Resource is added successfully</p>";
		}
		else {
			t="<p style=color:red>Resource already exists</p>";
		}
		
		return fobj.addDataAfter(84, t, "webapp/addresources.html", req);
	}
    
    
    
    
    @Path("changepasswordadmin")
    @GET
    public String change_password_admin(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.changepasswordadmin();
		return  fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("changepassword1admin")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_password1_admin(@FormParam("newpassword")String p,@FormParam("confirmnewpassword")String q,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
		String s=obj.changepasswordadmin();
    	if(p.equals(q)) {
    		//HttpSession session=req.getSession();
    		String name=session.getAttribute("uname").toString();
        	return obj.changepassword1(name,p)+"<br><a href='http://localhost:8888/uam_pro/index.jsp'>Login Again</a>";
    	}
    	return fobj.addDataAfter(103,s+"<p style=color:red>Passwords did not match</p>", "webapp/Adminbase.html", req);
    }
    
    @Path("removeresources")
    @GET
    public String remove_resources(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.removeresources();
		return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
	}
    
    
    @Path("removeresources1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resources1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.removeresources();
		String t=obj.removeresources1(options);
		return fobj.addDataAfter(103, s+t, "webapp/Adminbase.html", req);
		
	}
    
    @Path("removeresourcefromauser")
    @GET
    public String remove_resource_from_a_user(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	String s=obj.removeresourcefromauser();
    	return fobj.addDataAfter(103, s,"webapp/Adminbase.html", req);
    }
    
    @Path("removeresourcefromauser1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resource_from_a_user1(@FormParam("options1")String options1,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	String s=obj.removeresourcefromauser1(options1);
    	return fobj.addDataAfter(103, s,"webapp/Adminbase.html", req);
    }
    
    @Path("removeresourcefromauser2")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resource_from_a_user2(@FormParam("options2")String options2,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	String s=obj.removeresourcefromauser();
    	String t=obj.removeresourcefromauser2(options2);
    	return fobj.addDataAfter(103, s+t, "webapp/Adminbase.html", req);
    }
    
    @Path("checkusersforaresource")
    @GET
    public String check_users_for_a_resource(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.checkusersforaresource();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("checkusersforaresource1")
    @POST
    public String check_users_for_a_resource1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s= obj.checkusersforaresource1(options);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("checkresourcesofanuser")
    @GET
    public String check_resources_of_an_user(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.checkresourcesofanuser();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("checkresourcesofanuser1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String check_resources_of_an_user1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.checkresourcesofanuser1(options);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    
    @Path("assignresourcestoansuser")
    @GET
    public String assign_resources_to_an_suser(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.assignresourcestoanuser();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    
    @Path("assignresourcestoanuser1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String assign_resources_to_an_user1(@FormParam("options1")String options1,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.assignresourcestoanuser1(options1);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("assignresourcestoanuser2")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String assign_resources_to_an_user2(@FormParam("options1")String options1,@FormParam("options2")String options2,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String r=obj.assignresourcestoanuser();
    	String t=obj.assignresourcestoanuser2(options1,options2);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, r+t, "webapp/Adminbase.html", req);
    }
    
    
    
    @Path("makeasadminormanager")
    @GET
    public String make_as_admin_or_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.makeasadminormanager();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("makeasadminormanager1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String make_as_admin_or_manager1(@FormParam("options1")String options1,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.makeasadminormanager1(options1);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s, "webapp/Adminbase.html", req);
    }
    
    @Path("makeasadminormanager2")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String make_as_admin_or_manager2(@FormParam("options1")String options1,@FormParam("options2")String options2,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.makeasadminormanager();
    	String t=obj.makeasadminormanager2(options1,options2);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(103, s+t, "webapp/Adminbase.html", req);
    }
    //Admin functions End
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Manager functions Start
    @Path("showteam")
    @GET
    public String show_team(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	String s=obj.showteam(name);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("getateammember")
    @GET
    public String get_a_team_member(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	String s=obj.getateammember();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("getateammember1")
    @POST
    public String get_a_team_member1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	String s=obj.getateammember();
    	String t=obj.getateammember1(options,name);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(102, s+t, "webapp/Managerbase.html", req);
    }
    
    @Path("requestforadmin")
    @GET
    public String request_for_admin(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	String s=obj.requestforadmin(name);
	    FileUtils fobj=new FileUtils();
	    return fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("requestforadmin1")
    @POST
    public String request_for_admin1(@FormParam("options")String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	String s=obj.requestforadmin(name);
    	String t=obj.requestformanager(options, name);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(102, s+t, "webapp/Managerbase.html", req);
    }
    
    @Path("myresourcesmanager")
    @GET
    public String my_resources_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.myresources(name);
    	FileUtils fobj=new FileUtils();
		return  fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("requestforresourcesmanager")
    @GET
    public String request_for_resource_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		//HttpSession session=req.getSession();
		String name=(String) session.getAttribute("uname");
		FileUtils fobj=new FileUtils();
		String s=obj.requestforresourcesmanager(name);
		return  fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
	}
    
    
    @Path("requestforresources1manager")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String request_for_resource1_manager(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		//HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		String s=obj.requestforresourcesmanager(name);
		String t=obj.requestforresource1manager(options,name);
		FileUtils fobj=new FileUtils();
		return fobj.addDataAfter(102, s+t, "webapp/Managerbase.html", req);
	}
    
    @Path("checkapprovalsmanager")
    @GET
    public String check_approvals_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	FileUtils fobj=new FileUtils();
    	String s=obj.checkapprovals(name);
    	return fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("removeownresourcesmanager")
    @GET
    public String remove_own_resource_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.removeownresourcesmanager(name);
    	return fobj.addDataAfter(102, s,"webapp/Managerbase.html" , req);	
    }
    
    @Path("removeresource1manager")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resource1_manager(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
//		HttpSession session=req.getSession();
//		String uname=session.getAttribute("uname").toString();
		return  obj.removeresource1(options)+"<br><a href='http://localhost:8888/uam_pro/webapi/myresource/removeownresourcesmanager'>Back</a>";
	}
    
    
    
    @Path("changepasswordmanager")
    @GET
    public String change_password_manager(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.changepasswordmanager();
		return  fobj.addDataAfter(102, s, "webapp/Managerbase.html", req);
    }
    
    @Path("changepassword1manager")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_password1_manager(@FormParam("newpassword")String p,@FormParam("confirmnewpassword")String q,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	if(p.equals(q)) {
    		//HttpSession session=req.getSession();
    		String name=session.getAttribute("uname").toString();
        	return obj.changepassword1(name,p)+"<br><a href='http://localhost:8888/uam_pro/index.jsp'>Login Again</a>";
    	}
    	return "<h1>Passwords did not match</h1>"+"<br><a href='http://localhost:8888/uam_pro/webapi/myresource/changepasswordmanager'>Try again</a>";
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    //User Functions Start
    @Path("myresources")
    @GET
    public String my_resources(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.myresources(name);
    	FileUtils fobj=new FileUtils();
		return  fobj.addDataAfter(104, s, "webapp/UserBase.html", req);
    }
    
    
    @Path("requestforresources")
    @GET
    public String request_for_resource(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		//HttpSession session=req.getSession();
		String name=(String) session.getAttribute("uname");
		FileUtils fobj=new FileUtils();
		String s=obj.requestforresources(name);
		return  fobj.addDataAfter(104, s, "webapp/UserBase.html", req);
	}
    
    
    @Path("requestforresources1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String request_for_resource1(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
		//HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		String s=obj.requestforresources(name);
		String t=obj.requestforresource1(options,name);
		FileUtils fobj=new FileUtils();
		return fobj.addDataAfter(104, s+t, "webapp/UserBase.html", req);
	}
    
    
    @Path("checkapprovals")
    @GET
    public String check_approvals(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	FileUtils fobj=new FileUtils();
    	String s=obj.checkapprovals(name);
    	return fobj.addDataAfter(104, s, "webapp/UserBase.html", req);
    }
    
    
    @Path("removeownresource")
    @GET
    public String remove_own_resource(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	//HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.removeownresource(name);
    	return fobj.addDataAfter(104, s,"webapp/UserBase.html" , req);
    	
    }
    
    @Path("removeresource1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resource1(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
		user obj=new user();
//		HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		String s=obj.removeownresource(name);
		String t=obj.removeresource1(options);
		FileUtils fobj=new FileUtils();
		return fobj.addDataAfter(104, s+t, "webapp/UserBase.html", req);
	}
    
    @Path("requestfor")
    @GET
    public String request_for(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		FileUtils fobj=new FileUtils();
		String s=obj.requestfor(name);
		return  fobj.addDataAfter(104, s, "webapp/UserBase.html", req);
    }
    
    @Path("requestfor1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String request_for1(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	//HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		String s=obj.requestfor(name);
    	String t=obj.requestfor1(options, name);
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(104, s+t, "webapp/UserBase.html", req);
    }
    
    @Path("changepassword")
    @GET
    public String change_password(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
		FileUtils fobj=new FileUtils();
		String s=obj.changepassword();
		return  fobj.addDataAfter(104, s, "webapp/UserBase.html", req);
    }
    
    @Path("changepassword1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_password1(@FormParam("newpassword")String p,@FormParam("confirmnewpassword")String q,@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String fool=(String) session.getAttribute("uname");
    	if(fool==null) {
    		return "<h1>You have logged out.To login again click below link</h1>"+"<br>"+"<a href='http://localhost:8888/uam_pro/index.jsp'>Login</a>";
    	}
    	user obj=new user();
    	FileUtils fobj=new FileUtils();
    	String s=obj.changepassword();
    	if(p.equals(q)) {
    		//HttpSession session=req.getSession();
    		String name=session.getAttribute("uname").toString();
        	String t=obj.changepassword1(name,p);
        	return fobj.addDataAfter(104, s+t, "webapp/UserBase.html", req);
        	
    	}
    	return fobj.addDataAfter(104, s+"<p style=color:red>Passwords did not match</p>", "webapp/UserBase.html", req);
    	
    }
    
    
    
//    @Path("myresourcesresources")
//    @POST
//    public String my_resource1() throws Exception {
//		user obj=new user();
//		return  obj.myresources()+"<br><a href='http://localhost:8888/uam_pro/webapi/myresource/requestforresources'>Home</a>";
//	}
//    @GET
//    @Path("dropdown")
//    public String getUsers() throws Exception {
//    	Connection c=DB.connect();
//    	PreparedStatement ps=c.prepareStatement("select name from emp");
//    	ResultSet rs=ps.executeQuery();
//    	String dropDown="<select name='users'>";
//    	while(rs.next()) {
//    		String value=rs.getString(1);
//    		dropDown+="<option value='"+value+"'>"+value+"</option>";
//    	}
//    	dropDown+="</select>";
//           return dropDown;
//    }
    
    
    
    
    
    
}
