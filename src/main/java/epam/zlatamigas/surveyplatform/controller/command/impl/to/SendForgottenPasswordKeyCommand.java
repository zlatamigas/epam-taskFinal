package epam.zlatamigas.surveyplatform.controller.command.impl.to;

import epam.zlatamigas.surveyplatform.controller.command.Command;
import epam.zlatamigas.surveyplatform.controller.navigation.Router;
import epam.zlatamigas.surveyplatform.exception.CommandException;
import epam.zlatamigas.surveyplatform.exception.ServiceException;
import epam.zlatamigas.surveyplatform.service.UserService;
import epam.zlatamigas.surveyplatform.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static epam.zlatamigas.surveyplatform.controller.navigation.DataHolder.*;
import static epam.zlatamigas.surveyplatform.controller.navigation.PageNavigation.FORGOT_PASSWORD_RECEIVE_KEY;
import static epam.zlatamigas.surveyplatform.controller.navigation.Router.PageChangeType.FORWARD;

public class SendForgottenPasswordKeyCommand implements Command {

    @Override
    public Router execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        String page = FORGOT_PASSWORD_RECEIVE_KEY;

        String email = request.getParameter(PARAMETER_EMAIL);
        // TODO: email validation
        // TODO: email db check?

        UserService service = UserServiceImpl.getInstance();
        try {
            int key = service.requestChangePassword(email);
            session.setAttribute(ATTRIBUTE_CHANGE_PASSWORD_KEY, key);
            session.setAttribute(ATTRIBUTE_CHANGE_PASSWORD_EMAIL, email);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }

        session.setAttribute(ATTRIBUTE_CURRENT_PAGE, page);

        return new Router(page, FORWARD);
    }
}
