<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Notice Detail</title>
</head>
<body>

<h1>Notice Detail</h1>

<div>
    <strong>ID:</strong> ${notice.id}
</div>
<div>
    <strong>Title:</strong> ${notice.title}
</div>
<div>
    <strong>Content:</strong> ${notice.content}
</div>
<div>
    <strong>Date:</strong> ${notice.createdAt}
</div>

<br/>
<a href="/notices">Back to List</a>

</body>
</html>
