package epam.zlatamigas.surveyplatform.service;

import epam.zlatamigas.surveyplatform.exception.ServiceException;
import epam.zlatamigas.surveyplatform.model.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * Authenticate user.
     *
     * @param email Email.
     * @param password Not encrypted password.
     * @return User, if exists in db, otherwise - Optional.empty().
     * @throws ServiceException
     */
    Optional<User> authenticate(String email, String password) throws ServiceException;

    /**
     * Change password for user in DB with requested email.
     *
     * @param email User email.
     * @param password New password. Not encrypted.
     * @return True, if user exists in DB and password was changed, otherwise - false.
     * @throws ServiceException
     */
    boolean changePassword(String email, String password) throws ServiceException;

    /**
     * Send key for changing password to email.
     *
     * @param email Email to send key to.
     * @return Sent email.
     * @throws ServiceException Thrown when key was not sent.
     */
    int requestChangePassword(String email) throws ServiceException;

    /**
     * Insert new user into DB with default role of USER.
     *
     * @param email User email.
     * @param password Not encrypted password.
     * @return True, if user was added to DB, otherwise - false.
     * @throws ServiceException
     */
    boolean insertNewUser(String email, String password) throws ServiceException;
}