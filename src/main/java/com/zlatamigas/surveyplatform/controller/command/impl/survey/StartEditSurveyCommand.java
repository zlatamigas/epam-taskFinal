package com.zlatamigas.surveyplatform.controller.command.impl.survey;

import com.zlatamigas.surveyplatform.controller.command.Command;
import com.zlatamigas.surveyplatform.controller.command.CommandType;
import com.zlatamigas.surveyplatform.controller.navigation.Router;
import com.zlatamigas.surveyplatform.exception.CommandException;
import com.zlatamigas.surveyplatform.exception.ServiceException;
import com.zlatamigas.surveyplatform.model.entity.Survey;
import com.zlatamigas.surveyplatform.model.entity.SurveyStatus;
import com.zlatamigas.surveyplatform.model.entity.Theme;
import com.zlatamigas.surveyplatform.model.entity.User;
import com.zlatamigas.surveyplatform.service.SurveyService;
import com.zlatamigas.surveyplatform.service.ThemeService;
import com.zlatamigas.surveyplatform.service.impl.SurveyServiceImpl;
import com.zlatamigas.surveyplatform.service.impl.ThemeServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder.*;
import static com.zlatamigas.surveyplatform.controller.navigation.PageNavigation.*;
import static com.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType;
import static com.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.FORWARD;
import static com.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.REDIRECT;

public class StartEditSurveyCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Router execute(HttpServletRequest request) throws CommandException {

        HttpSession session = request.getSession();
        String page = USER_SURVEYS;
        PageChangeType pageChangeType = REDIRECT;

        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_USER);

        String createNewStr = request.getParameter(PARAMETER_CREATE_NEW_SURVEY);
        if (Boolean.TRUE.toString().equalsIgnoreCase(createNewStr)
                || Boolean.FALSE.toString().equalsIgnoreCase(createNewStr)) {

            boolean surveyCreatedFound = false;
            Survey survey = null;

            boolean createNew = Boolean.parseBoolean(createNewStr);
            if (!createNew) {
                SurveyService surveyService = SurveyServiceImpl.getInstance();

                String surveyIdStr = request.getParameter(PARAMETER_SURVEY_ID);
                try {
                    int surveyId = Integer.parseInt(surveyIdStr);
                    Optional<Survey> creatorSurveyInfo = surveyService.findCreatorSurveyInfo(surveyId, user.getUserId());
                    if (creatorSurveyInfo.isPresent() && creatorSurveyInfo.get().getStatus() == SurveyStatus.NOT_STARTED) {
                        survey = creatorSurveyInfo.get();
                        surveyCreatedFound = true;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Passed invalid {} parameter", PARAMETER_SURVEY_ID);
                } catch (ServiceException e) {
                    throw new CommandException(e);
                }
            } else {
                survey = new Survey();
                surveyCreatedFound = true;
            }

            if (surveyCreatedFound) {
                ThemeService themeService = ThemeServiceImpl.getInstance();
                try {
                    List<Theme> themes = themeService.findAllConfirmed();
                    session.setAttribute(SESSION_ATTRIBUTE_THEMES, themes);
                } catch (ServiceException e) {
                    throw new CommandException(e);
                }

                page = String.format(URL_REDIRECT_BASE_PATTERN, CommandType.EDIT_SURVEY.name());
                pageChangeType = FORWARD;

                session.setAttribute(SESSION_ATTRIBUTE_EDITED_SURVEY, survey);
                session.setAttribute(SESSION_ATTRIBUTE_CURRENT_PAGE,
                        String.format(URL_CONTROLLER_WITH_PARAMETERS_PATTERN, request.getQueryString()));
            }
        }

        return new Router(page, pageChangeType);
    }
}
