<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.zlatamigas.surveyplatform.controller.command.CommandType" %>
<%@ page import="com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder" %>

<fmt:setLocale value="${sessionScope.localisation}" scope="session"/>
<fmt:setBundle basename="localisation.localisedtext"/>

<c:set var="itemsPerPage" value="10" scope="page"/>

<!DOCTYPE html>
<html lang="${sessionScope.localisation}">
<head>
    <title><fmt:message key="title.themes.waiting"/></title>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script src="${pageContext.request.contextPath}/static/js/pagination.js"></script>
</head>

<body>

<jsp:include page="/view/fragment/header.jsp"/>

<div class="container-fluid">

    <div class="row">
        <div class="col-3">
            <jsp:include page="/view/fragment/account_left_navbar.jsp"/>
            <script>
                document.getElementById("collapseTheme").classList.add("show");
                document.getElementById("navThemesWaiting").classList.add("active");
                document.getElementById("navThemes").setAttribute("disabled", "disabled");
            </script>
        </div>
        <div class="col-9">

            <div class="content-container">

                <div class="pagination-page-container">
                    <div id="pagination-page-container">
                        <c:set var="themePage" value="1" scope="page"/>
                        <div id="pagination-page-${themePage}" style="display: none">
                            <c:if test="${requestScope.requested_themes != null && requestScope.requested_themes.size() > 0}">
                            <c:forEach var="themeIndex" begin="0" end="${requestScope.requested_themes.size() - 1}">
                                <c:set var="theme" value="${requestScope.requested_themes.get(themeIndex)}" scope="page"/>
                                <c:if test="${themeIndex / itemsPerPage >= themePage}">
                                    </div>
                                    <c:set var="themePage" value="${themePage + 1}"/>
                                    <div id="pagination-page-${themePage}" style="display: none">
                                </c:if>
                                <div class="card">
                                    <div class="card-header">
                                        <div class="row justify-content-between">
                                            <div class="col">
                                                <h5><c:out value="${theme.themeName}"/></h5>
                                            </div>
                                            <div class="col col-auto">
                                                <form action="controller" method="post">
                                                    <input type="hidden" name="${AttributeParameterHolder.PARAMETER_COMMAND}" value="${CommandType.CONFIRM_THEME}">
                                                    <input type="hidden" name="${AttributeParameterHolder.PARAMETER_THEME_ID}" value="${theme.themeId}">
                                                    <button class="btn btn-success" type="submit"><i class="fas fa-check"></i></button>
                                                </form>
                                            </div>
                                            <div class="col col-auto">
                                                <form action="controller" method="post">
                                                    <input type="hidden" name="${AttributeParameterHolder.PARAMETER_COMMAND}" value="${CommandType.REJECT_THEME}">
                                                    <input type="hidden" name="${AttributeParameterHolder.PARAMETER_THEME_ID}" value="${theme.themeId}">
                                                    <button class="btn btn-danger" type="submit"><i class="fas fa-times"></i></button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <c:remove var="theme" scope="page"/>
                            </c:forEach>
                            </c:if>
                        </div>
                        <c:remove var="themePage" scope="page"/>
                    </div>
                </div>
                <div class="pagination">
                    <ul></ul>
                </div>
                <script>
                    const element = document.querySelector(".pagination ul");
                    let totalPages = Math.max(Math.ceil(${requestScope.requested_themes.size() / itemsPerPage}), 1);
                    let page = 1;
                    element.innerHTML = createPagination(totalPages, page);
                </script>
            </div>
        </div>
    </div>


</div>

<jsp:include page="/view/fragment/footer.jsp"/>
</body>
</html>
