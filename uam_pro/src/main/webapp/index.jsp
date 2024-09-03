<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <!-- Login Form -->
            <form id="login-form" class="form" action="webapi/myresource/login" method="post">
                <h2>Login</h2>
                <label for="user-name">Username:</label>
                <input type="text" id="username" name="username" required>
                <label for="login-password">Password:</label>
                <input type="password" id="password" name="password" required>
                <button type="submit">Login</button>
                <p>Don't have an account? <a href="registration.html">Register here</a></p>
                <p><a href="ForgotPassword.html">Forgot your password?</a></p>
            </form>
        </div>
    </div>
</body>
</html>
