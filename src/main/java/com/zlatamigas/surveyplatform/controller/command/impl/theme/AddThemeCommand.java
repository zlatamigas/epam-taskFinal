package com.zlatamigas.surveyplatform.controller.command.impl.theme;

import com.zlatamigas.surveyplatform.controller.command.Command;
import com.zlatamigas.surveyplatform.controller.navigation.Router;
import com.zlatamigas.surveyplatform.exception.CommandException;
import com.zlatamigas.surveyplatform.exception.ServiceException;
import com.zlatamigas.surveyplatform.model.entity.Theme;
import com.zlatamigas.surveyplatform.model.entity.User;
import com.zlatamigas.surveyplatform.service.ThemeService;
import com.zlatamigas.surveyplatform.service.impl.ThemeServiceImpl;
import com.zlatamigas.surveyplatform.util.validator.FormValidator;
import com.zlatamigas.surveyplatform.util.validator.impl.AddThemeFormValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder.*;
import static com.zlatamigas.surveyplatform.controller.navigation.PageNavigation.URL_REDIRECT_THEMES_CONFIRMED;
import static com.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.FORWARD;
import static com.zlatamigas.surveyplatform.util.locale.LocalisedMessageKey.MESSAGE_INVALID_THEME_EXISTS;
import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_ORDER;
import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_SEARCH_WORDS;

public class AddThemeCommand implements Command {

    @Override
    public Router execute(HttpServletRequest request) throws CommandException {

        HttpSession session = request.getSession();
        String page = (String) session.getAttribute(SESSION_ATTRIBUTE_CURRENT_PAGE);

        String themeName = request.getParameter(PARAMETER_THEME_NAME);

        FormValidator validator = AddThemeFormValidator.getInstance();
        Map<String, String[]> requestParameters = request.getParameterMap();
        Map<String, String> validationFeedback = validator.validateForm(requestParameters);

        ThemeService themeService = ThemeServiceImpl.getInstance();
        try {
            boolean result = !validationFeedback.isEmpty();

            if (validationFeedback.isEmpty()) {
                User user = (User) session.getAttribute(SESSION_ATTRIBUTE_USER);
                if (user != null && user.getRole() != null) {
                    result = switch (user.getRole()) {
                        case ADMIN -> themeService.insertConfirmedTheme(themeName);
                        case USER -> themeService.insertWaitingTheme(themeName);
                        default -> false;
                    };
                    if (!result) {
                        request.setAttribute(REQUEST_ATTRIBUTE_THEME_EXISTS, MESSAGE_INVALID_THEME_EXISTS);
                    } else {
                        page = URL_REDIRECT_THEMES_CONFIRMED;
                    }
                }
            } else {
                request.setAttribute(REQUEST_ATTRIBUTE_FORM_INVALID, validationFeedback);
            }

            if (!result) {

                String searchWordsStr = request.getParameter(REQUEST_ATTRIBUTE_PARAMETER_SEARCH_WORDS);
                if (searchWordsStr == null) {
                    searchWordsStr = DEFAULT_SEARCH_WORDS;
                }
                String orderTypeName = request.getParameter(REQUEST_ATTRIBUTE_PARAMETER_ORDER_TYPE);
                if (orderTypeName == null) {
                    orderTypeName = DEFAULT_ORDER;
                }

                request.setAttribute(REQUEST_ATTRIBUTE_PARAMETER_SEARCH_WORDS, searchWordsStr);
                request.setAttribute(REQUEST_ATTRIBUTE_PARAMETER_ORDER_TYPE, orderTypeName);

                List<Theme> themes = themeService.findConfirmedSearch(searchWordsStr, orderTypeName);
                request.setAttribute(REQUEST_ATTRIBUTE_REQUESTED_THEMES, themes);
            }

        } catch (ServiceException e) {
            throw new CommandException(e);
        }

        return new Router(page, FORWARD);
    }
}
