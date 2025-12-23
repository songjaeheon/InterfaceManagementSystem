<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${notice.id == null ? 'Create' : 'Edit'} Notice</title>
</head>
<body>

<h1>${notice.id == null ? 'Create' : 'Edit'} Notice</h1>

<form:form method="POST" action="/notices" modelAttribute="notice">
    <form:hidden path="id" />
    <form:hidden path="createdAt" />

    <div>
        <label>Title:</label><br/>
        <form:input path="title" />
    </div>
    <br/>
    <div>
        <label>Content:</label><br/>
        <form:textarea path="content" rows="5" cols="30" />
    </div>
    <br/>
    <input type="submit" value="Save" />
</form:form>

<br/>
<a href="/notices">Back to List</a>

</body>
</html>
