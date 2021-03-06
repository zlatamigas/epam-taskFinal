package com.zlatamigas.surveyplatform.controller.filter;

import com.zlatamigas.surveyplatform.controller.command.CommandType;
import com.zlatamigas.surveyplatform.controller.navigation.PageNavigation;
import com.zlatamigas.surveyplatform.model.entity.User;
import com.zlatamigas.surveyplatform.model.entity.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import static com.zlatamigas.surveyplatform.controller.command.CommandType.*;
import static com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder.*;
import static com.zlatamigas.surveyplatform.model.entity.UserRole.*;

/**
 * Filter for checking user role access to commands.
 */
@WebFilter(
        filterName = "CheckUserRoleFilter",
        dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.REQUEST},
        urlPatterns = {"/controller"})
public class CheckUserRoleFilter implements Filter {

    private Map<UserRole, EnumSet<CommandType>> userCommands;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userCommands = Map.of(
                ADMIN, EnumSet.of(
                        DEFAULT,
                        HOME,
                        CHANGE_LOCALISATION,
                        LOG_OUT,
                        // List data
                        SURVEYS,
                        USERS,
                        USER_SURVEYS,
                        // CRUD survey and its parts
                        START_EDIT_SURVEY,
                        FINISH_EDIT_SURVEY,
                        EDIT_QUESTION,
                        FINISH_EDIT_QUESTION,
                        REMOVE_QUESTION,
                        EDIT_SURVEY,
                        DELETE_SURVEY,
                        CHANGE_SURVEY_STATUS_CLOSED,
                        CHANGE_SURVEY_STATUS_STARTED,
                        RESTART_SURVEY,
                        // Participate in survey
                        SURVEY_ATTEMPT,
                        FINISH_SURVEY_ATTEMPT,
                        SURVEY_RESULT,
                        THEMES_CONFIRMED,
                        THEMES_WAITING,
                        CONFIRM_THEME,
                        REJECT_THEME,
                        DELETE_THEME,
                        ADD_THEME,
                        USER_ACCOUNT,
                        // CRUD user
                        EDIT_USER,
                        FINISH_EDIT_USER,
                        DELETE_USER,
                        CREATE_USER,
                        FINISH_CREATE_USER,
                        ADMIN_DELETE_SURVEY,
                        //Change password
                        CHANGE_PASSWORD,
                        FINISH_CHANGE_PASSWORD
                ),
                USER, EnumSet.of(
                        DEFAULT,
                        HOME,
                        CHANGE_LOCALISATION,
                        LOG_OUT,
                        // List data
                        SURVEYS,
                        USER_SURVEYS,
                        // CRUD survey and its parts
                        START_EDIT_SURVEY,
                        FINISH_EDIT_SURVEY,
                        EDIT_QUESTION,
                        FINISH_EDIT_QUESTION,
                        REMOVE_QUESTION,
                        EDIT_SURVEY,
                        DELETE_SURVEY,
                        CHANGE_SURVEY_STATUS_CLOSED,
                        CHANGE_SURVEY_STATUS_STARTED,
                        RESTART_SURVEY,
                        // Participate in survey
                        SURVEY_ATTEMPT,
                        FINISH_SURVEY_ATTEMPT,
                        SURVEY_RESULT,
                        USER_ACCOUNT,
                        // Themes
                        THEMES_CONFIRMED,
                        ADD_THEME,
                        //Change password
                        CHANGE_PASSWORD,
                        FINISH_CHANGE_PASSWORD
                ),
                GUEST, EnumSet.of(
                        DEFAULT,
                        HOME,
                        CHANGE_LOCALISATION,
                        // User authentication
                        SIGN_IN,
                        FINISH_SIGN_IN,
                        SIGN_UP,
                        FINISH_SIGN_UP,
                        // List data
                        SURVEYS,
                        // Participate in survey
                        SURVEY_ATTEMPT,
                        FINISH_SURVEY_ATTEMPT,
                        //Change password
                        CHANGE_PASSWORD,
                        FINISH_CHANGE_PASSWORD,
                        SEND_KEY,
                        FINISH_SEND_KEY,
                        CONFIRM_KEY,
                        FINISH_CONFIRM_KEY
                )
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String commandStr = request.getParameter(PARAMETER_COMMAND);

        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_USER);
        UserRole userRole = user != null ? user.getRole() : GUEST;

        EnumSet<CommandType> allowedCommands = userCommands.get(userRole);
        CommandType command = CommandType.defineCommandType(commandStr);

        if (!allowedCommands.contains(command)) {
            session.setAttribute(SESSION_ATTRIBUTE_CURRENT_PAGE, PageNavigation.DEFAULT);
            response.sendRedirect(request.getContextPath() + PageNavigation.DEFAULT);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
