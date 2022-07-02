<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="epam.zlatamigas.surveyplatform.controller.command.CommandType" %>
<%@ page import="epam.zlatamigas.surveyplatform.model.entity.UserRole" %>

<fmt:setLocale value="${sessionScope.localisation}" scope="session"/>
<fmt:setBundle basename="localisation.localisedtext"/>

<html lang="${sessionScope.localisation}">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">


    <a id="navUserAccount" class="nav-link" role="tab" aria-selected="false"
       href="${pageContext.request.contextPath}/controller?command=${CommandType.TO_USER_ACCOUNT}">Account</a>

    <a id="navUserSurveys" class="nav-link" role="tab" aria-selected="false"
       href="${pageContext.request.contextPath}/controller?command=${CommandType.LIST_USER_CREATED_SURVEYS}">
        My surveys</a>

    <c:choose>
        <c:when test="${sessionScope.user.role == UserRole.ADMIN}">
            <button id="navThemes"
                    class="btn btn-link btn-block text-left nav-link"
                    type="button" data-toggle="collapse"
                    data-target="#collapseTheme" aria-expanded="false" aria-controls="collapseTheme">
                Themes
            </button>
            <div id="collapseTheme" class="collapse">
                <div>
                    <a id="navThemesConfirmed" class="nav-link" role="tab" aria-selected="false" href="${pageContext.request.contextPath}/controller?command=${CommandType.TO_THEMES_CONFIRMED}">Confirmed</a>
                    <a id="navThemesWaiting" class="nav-link" role="tab" aria-selected="false" href="${pageContext.request.contextPath}/controller?command=${CommandType.TO_THEMES_WAITING}">Waiting</a>
                </div>
            </div>
            <a id="navUsers" class="nav-link" role="tab" aria-selected="false"
               href="${pageContext.request.contextPath}/controller?command=${CommandType.LIST_USERS}">
                Users</a>
        </c:when>
        <c:when test="${sessionScope.user.role == UserRole.USER}">

        </c:when>
    </c:choose>
</div>
</body>
</html>