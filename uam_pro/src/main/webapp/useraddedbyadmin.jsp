<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard</title>
    <style>
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
            max-width: 800px;
            margin: 20px auto;
        }
        .form-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            margin: 20px auto;
            display: none; /* Hidden by default */
        }
        .form-container.active {
            display: block; /* Show when active */
        }
        .form-container input[type="password"] {
            width: calc(100% - 22px); /* Adjust width to account for padding */
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* Ensure padding does not affect width */
        }
        .form-container button {
            background-color: #333;
            color: #f2f2f2;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            display: block;
            width: 100%; /* Full-width button */
            box-sizing: border-box; /* Ensure padding does not affect width */
        }
        .form-container button:hover {
            background-color: #ddd;
            color: black;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <a href="webapi/myresource/logout" class="logout">Logout</a>
    </div>
    <%
        String name=session.getAttribute("uname").toString();
        %>
        <h1>Hello <%=name%></h1>
        <h3>Please change your password and login again to access functionalities.</h3>
    <div class="container">
        <!-- Form Container -->
        <div id="changePasswordForm" class="form-container active">
            <h2>Change Password</h2>
            <form action="webapi/myresource/changepasswordaddeduser1" class="form" method="post">
            	<input type='password' name='newpassword' placeholder='Enter password to change' required minlength='8'  maxlength='20' pattern='(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}' required>
				<small>Password must be between 8 and 20 characters, include at least one digit, one lowercase letter, and one uppercase letter.</small>
                
                <input type="password" name="confirmpassword" placeholder="Confirm Password" required>
                <button type="submit">Submit</button>
            </form>
        </div>
        
        
    </div>
</body>
</html>
