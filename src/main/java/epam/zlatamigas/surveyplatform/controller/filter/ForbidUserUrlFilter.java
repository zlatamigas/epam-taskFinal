package epam.zlatamigas.surveyplatform.controller.filter;

import epam.zlatamigas.surveyplatform.controller.command.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static epam.zlatamigas.surveyplatform.controller.navigation.PageNavigation.*;

@WebFilter(filterName = "ForbidUserUrlFilter",
        urlPatterns = {"/view/page/*"})
public class ForbidUserUrlFilter implements Filter {

    private static final Logger logger = LogManager.getLogger();

    private static Map<String, String> redirectUrl;

    public void init(FilterConfig config) throws ServletException {

        redirectUrl = new HashMap<>(11);

        redirectUrl.put(DEFAULT, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name()));
        redirectUrl.put(HOME, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name()));
        redirectUrl.put(SIGN_IN, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.START_SIGN_IN.name()));
        redirectUrl.put(SIGN_UP, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.START_SIGN_UP.name()));
        redirectUrl.put(SURVEYS, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.TO_SURVEYS.name()));
        redirectUrl.put(USER_SURVEYS, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.LIST_USER_CREATED_SURVEYS.name()));
        redirectUrl.put(USERS, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.LIST_USERS.name()));
        redirectUrl.put(THEMES_CONFIRMED, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.TO_THEMES_CONFIRMED.name()));
        redirectUrl.put(THEMES_WAITING, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.TO_THEMES_WAITING.name()));
        redirectUrl.put(ACCOUNT, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.TO_USER_ACCOUNT.name()));
        redirectUrl.put(CREATE_USER, String.format(URL_REDIRECT_BASE_PATTERN, CommandType.START_CREATE_USER.name()));

//        FORGOT_PASSWORD,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        FORGOT_PASSWORD_RECEIVE_KEY,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        FORGOT_PASSWORD_CHANGE_PASSWORD ,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        EDIT_SURVEY,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        EDIT_QUESTION,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        SURVEY_ATTEMPT,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        SURVEY_RESULT,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())
//        EDIT_USER,String.format(URL_REDIRECT_BASE_PATTERN, CommandType.HOME.name())

    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String pagePath = request.getServletPath();
        if(redirectUrl.containsKey(pagePath)){
            response.sendRedirect(request.getContextPath() + redirectUrl.get(pagePath));
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
