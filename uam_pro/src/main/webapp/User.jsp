<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard</title>
    <link rel="stylesheet" href="styles.css"> <!-- Link to your CSS file -->
    <style>
        /* Simple styling for demonstration */
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .navbar {
            background-color: #333;
            overflow: hidden;
        }
        .navbar a {
            float: left;
            display: block;
            color: #f2f2f2;
            text-align: center;
            padding: 14px 20px;
            text-decoration: none;
        }
        .navbar a:hover {
            background-color: #ddd;
            color: black;
        }
        .navbar .logout {
            float: right;
        }
        .container {
            padding: 20px;
        }
        .container h1 {
            color: #333;
        }
        
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <a href="webapi/myresource/myresources">My Resources</a>
        <a href="webapi/myresource/requestforresources">Request for new resource</a>
        <a href="webapi/myresource/checkapprovals">Check approvals</a>
        <a href="webapi/myresource/requestfor">Request for Manager/Admin</a>
        <a href="webapi/myresource/removeownresource">Remove own resource</a>
        <a href="webapi/myresource/changepassword">Change Password</a>
        <a href="webapi/myresource/logout" class="logout">Logout</a> <!-- Logout link -->
    </div>
    

    <!-- Main Content -->
    <div class="container">
        <h1>Welcome to User Dashboard</h1>
        <%
        String name=session.getAttribute("uname").toString();
        %>
        <h1>Your  username is <%=name%></h1>
        <p>Use the navigation bar above to access your resources, profile settings, or log out.</p>
        
    </div>
    
    
</body>
</html>
