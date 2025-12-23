<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Notices</title>
    <style>
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f4f4f4; }
    </style>
</head>
<body>

<h1>All Notices</h1>
<a href="/notices/new">Create New Notice</a>
<br/><br/>

<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Date</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${notices}" var="notice">
            <tr>
                <td>${notice.id}</td>
                <td>${notice.title}</td>
                <td>${notice.createdAt}</td>
                <td>
                    <a href="/notices/${notice.id}">View</a> |
                    <a href="/notices/${notice.id}/edit">Edit</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<br/>
<a href="/notices/dashboard">Back to Dashboard</a>

</body>
</html>
