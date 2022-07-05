package epam.zlatamigas.surveyplatform.controller.command.impl.samepage;

import epam.zlatamigas.surveyplatform.controller.command.Command;
import epam.zlatamigas.surveyplatform.controller.navigation.Router;
import epam.zlatamigas.surveyplatform.exception.CommandException;
import epam.zlatamigas.surveyplatform.exception.ServiceException;
import epam.zlatamigas.surveyplatform.model.entity.Theme;
import epam.zlatamigas.surveyplatform.service.ThemeService;
import epam.zlatamigas.surveyplatform.service.impl.ThemeServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static epam.zlatamigas.surveyplatform.controller.navigation.DataHolder.*;
import static epam.zlatamigas.surveyplatform.controller.navigation.PageNavigation.THEMES_CONFIRMED;
import static epam.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.FORWARD;
import static epam.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.REDIRECT;
import static epam.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_ORDER;
import static epam.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_SEARCH_WORDS;

public class DeleteThemeCommand implements Command {
    @Override
    public Router execute(HttpServletRequest request) throws CommandException {

        HttpSession session = request.getSession();
        String page = THEMES_CONFIRMED;

        String searchWordsStr = request.getParameter(REQUEST_PARAMETER_ATTRIBUTE_SEARCH_WORDS);
        if(searchWordsStr == null){
            searchWordsStr = DEFAULT_SEARCH_WORDS;
        }
        String orderTypeName = request.getParameter(REQUEST_PARAMETER_ATTRIBUTE_ORDER_TYPE);
        if(orderTypeName == null){
            orderTypeName = DEFAULT_ORDER;
        }

        request.setAttribute(REQUEST_PARAMETER_ATTRIBUTE_SEARCH_WORDS, searchWordsStr);
        request.setAttribute(REQUEST_PARAMETER_ATTRIBUTE_ORDER_TYPE, orderTypeName);

        String themeIdParameter = request.getParameter(PARAMETER_THEME_ID);
        int themeId = Integer.parseInt(themeIdParameter);

        ThemeService themeService = ThemeServiceImpl.getInstance();
        try {
            themeService.delete(themeId);

            List<Theme> themes = themeService.findConfirmedSearch(searchWordsStr, orderTypeName);
            request.setAttribute(REQUEST_ATTRIBUTE_REQUESTED_THEMES, themes);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }

        session.setAttribute(ATTRIBUTE_CURRENT_PAGE, page);

        return new Router(page, FORWARD);
    }
}
