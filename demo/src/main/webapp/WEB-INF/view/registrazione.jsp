<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registrazione</title>
</head>
<body>
	<%=request.getAttribute("message") %>
	<form action="/registrazione" method="post">
	    <label for="username">Username:</label>
	    <input type="text" id="username" name="username" required><br><br>
	    
	    <label for="password">Password:</label>
	    <input type="password" id="password" name="password" required><br><br>
	    
	    <a href="login">login</a>
	    
	    <input type="submit" value="Registrati">
	</form>
</body>
</html>