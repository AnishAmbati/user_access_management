<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
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
        .form-container input[type="text"] {
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
        .show-form {
            display: inline;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <a href="webapi/myresource/listofusers">Users List</a>
        <a href="webapi/myresource/listofresources">Resources List</a>
        <a href="webapi/myresource/listofmanagers">Managers List</a>
        <a href="webapi/myresource/listofrequests">Requests</a>
        <a href="addusers.html">Add Users</a>
        <a href="webapi/myresource/removeusers">Remove Users</a>
        <a href="addresources.html">Add Resources</a>
        <a href="webapi/myresource/changepasswordadmin">Change Password</a>
        <a href="webapi/myresource/removeresources">Remove Resources</a>
        <a href="webapi/myresource/removeresourcefromauser">Remove resource from a user</a>
        <a href="webapi/myresource/checkusersforaresource">Check users for a resource</a>
        <a href="webapi/myresource/checkresourcesofanuser">Check Resources of an user</a>
        <a href="webapi/myresource/assignresourcestoansuser">Assign Resources to an user</a>
        <a href="webapi/myresource/makeasadminormanager">Make user as Admin/Manager</a>
        <a href="webapi/myresource/logout" class="logout">Logout</a>
    </div>
    
    <div class="container">
        <!-- Form Container -->
        <div id="removeForm" class="form-container">
            <h2>Remove Resources</h2>
            <form action="webapi/myresource/removeresources" method="post">
                <input type="text" name="resourcename" placeholder="Enter resource to remove" required>
                <button type="submit">Submit</button>
            </form>
        </div>
        <div class="container">
        <h1>Welcome to Admin Dashboard</h1>
        <%
        String name=session.getAttribute("uname").toString();
        %>
        <h1>Your  username is <%=name%></h1>
        
        <p>Use the navigation bar above to access your resources, profile settings, or log out.</p>
    </div>
    </div>
    
</body>
</html>
