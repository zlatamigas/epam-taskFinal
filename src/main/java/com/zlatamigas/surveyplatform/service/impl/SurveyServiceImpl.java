package com.zlatamigas.surveyplatform.service.impl;

import com.zlatamigas.surveyplatform.exception.DaoException;
import com.zlatamigas.surveyplatform.exception.ServiceException;
import com.zlatamigas.surveyplatform.model.dao.DbOrderType;
import com.zlatamigas.surveyplatform.model.dao.impl.SurveyDaoImpl;
import com.zlatamigas.surveyplatform.model.entity.*;
import com.zlatamigas.surveyplatform.service.SurveyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_FILTER_STR_ALL;
import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_SEARCH_WORDS_DELIMITER;

public class SurveyServiceImpl implements SurveyService {

    private static final Logger logger = LogManager.getLogger();

    private static final String DEFAULT_DESCRIPTION = "";

    private static SurveyServiceImpl instance = new SurveyServiceImpl();
    private final SurveyDaoImpl surveyDao;

    private SurveyServiceImpl() {
        surveyDao = SurveyDaoImpl.getInstance();
    }

    public static SurveyServiceImpl getInstance() {
        return instance;
    }

    @Override
    public boolean insert(Survey survey) throws ServiceException {

        if (survey != null) {

            if (survey.getCreator() == null
                    || survey.getName() == null) {
                logger.error("Passed invalid Survey data: creator = {}, name = {}, status = {}",
                        survey.getCreator(), survey.getName(), survey.getStatus());
                return false;
            }

            survey.setStatus(SurveyStatus.NOT_STARTED);

            if (survey.getQuestions() != null) {
                survey.getQuestions().forEach(q -> q.getAnswers().forEach(a -> a.setSelectedCount(0)));
            } else {
                survey.setQuestions(new ArrayList<>());
            }

            if (survey.getDescription() == null) {
                survey.setDescription(DEFAULT_DESCRIPTION);
            }

            if (survey.getTheme() == null) {
                survey.setTheme(new Theme.ThemeBuilder().setThemeId(-1).getTheme());
                logger.warn("Set default Theme as Survey theme object");
            }

            try {
                return surveyDao.insert(survey);
            } catch (DaoException e) {
                throw new ServiceException(e);
            }
        } else {
            logger.error("Passed null as Survey object");
            return false;
        }
    }

    @Override
    public boolean delete(int surveyId, int creatorId) throws ServiceException {

        try {
            Optional<Survey> survey = surveyDao.findById(surveyId);
            return survey.isPresent()
                    && survey.get().getCreator().getUserId() == creatorId
                    && surveyDao.delete(surveyId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Survey> findParticipantSurveysCommonInfoSearch(int filterThemeId,
                                                               String searchWordsStr,
                                                               String orderTypeName) throws ServiceException {

        String[] searchWords;
        if (searchWordsStr != null && !searchWordsStr.isBlank()) {
            searchWords = Arrays.stream(searchWordsStr
                    .split(DEFAULT_SEARCH_WORDS_DELIMITER))
                    .filter(s -> !s.isBlank())
                    .toArray(String[]::new);
        } else {
            searchWords = new String[]{};
            logger.warn("Set default searchWords parameter");
        }

        DbOrderType orderType;
        try {
            if (orderTypeName != null) {
                orderType = DbOrderType.valueOf(orderTypeName.toUpperCase());
            } else {
                orderType = DbOrderType.ASC;
                logger.warn("Set default orderType parameter");
            }
        } catch (IllegalArgumentException e) {
            orderType = DbOrderType.ASC;
            logger.warn("Set default orderType parameter");
        }

        try {
            return surveyDao.findParticipantSurveysCommonInfoSearch(filterThemeId, searchWords, orderType);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Survey> findCreatorSurveysCommonInfoSearch(int filterThemeId,
                                                           String searchWordsStr,
                                                           String orderTypeName,
                                                           String surveyStatusName,
                                                           int userId) throws ServiceException {

        String[] searchWords;
        if (searchWordsStr != null && !searchWordsStr.isBlank()) {
            searchWords = Arrays.stream(searchWordsStr
                    .split(DEFAULT_SEARCH_WORDS_DELIMITER))
                    .filter(s -> !s.isBlank())
                    .toArray(String[]::new);
        } else {
            searchWords = new String[]{};
            logger.warn("Set default searchWords parameter");
        }

        DbOrderType orderType;
        try {
            if (orderTypeName != null) {
                orderType = DbOrderType.valueOf(orderTypeName.toUpperCase());
            } else {
                orderType = DbOrderType.ASC;
                logger.warn("Set default orderType parameter");
            }
        } catch (IllegalArgumentException e) {
            orderType = DbOrderType.ASC;
            logger.warn("Set default orderType parameter");
        }

        Optional<SurveyStatus> surveyStatus;
        if (!surveyStatusName.equals(DEFAULT_FILTER_STR_ALL)) {
            try {
                surveyStatus = Optional.of(SurveyStatus.valueOf(surveyStatusName));
            } catch (IllegalArgumentException e) {
                surveyStatus = Optional.empty();
                logger.warn("Set default surveyStatus parameter");
            }
        } else {
            surveyStatus = Optional.empty();
        }

        try {
            return surveyDao.findCreatorSurveysCommonInfoSearch(filterThemeId, searchWords, orderType, surveyStatus, userId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Survey> findParticipantSurveyInfo(int surveyId) throws ServiceException {

        try {
            return surveyDao.findParticipantSurveyInfo(surveyId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Survey> findCreatorSurveyInfo(int surveyId, int creatorId) throws ServiceException {

        try {
            Optional<Survey> survey = surveyDao.findCreatorSurveyInfo(surveyId);
            return (survey.isPresent() && survey.get().getCreator().getUserId() == creatorId)
                    ? survey
                    : Optional.empty();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean updateParticipantSurveyResult(SurveyUserAttempt surveyAttempt) throws ServiceException {

        if (surveyAttempt != null) {

            if (surveyAttempt.getSurvey() == null) {
                logger.error("Passed null as Survey object");
                return false;
            }

            if (surveyAttempt.getFinishedDate() == null) {
                surveyAttempt.setFinishedDate(LocalDateTime.now());
                logger.warn("Set current time for SurveyUserAttempt object");
            }

            if (surveyAttempt.getUser() == null) {
                surveyAttempt.setUser(new User.UserBuilder().setUserId(-1).getUser());
                logger.warn("Set guest user as participant for SurveyUserAttempt object");
            }

            try {
                return surveyDao.updateParticipantSurveyResult(surveyAttempt);
            } catch (DaoException e) {
                throw new ServiceException(e);
            }

        } else {
            logger.error("Passed null as SurveyUserAttempt object");
            return false;
        }
    }

    @Override
    public boolean updateSurveyStatus(int surveyId, SurveyStatus status, int creatorId) throws ServiceException {

        LocalDateTime dateTime = LocalDateTime.now();

        if (status == null) {
            logger.error("Passed null as SurveyStatus object");
            return false;
        }


        try {
            Optional<Survey> survey = surveyDao.findById(surveyId);
            return survey.isPresent()
                    && survey.get().getCreator().getUserId() == creatorId
                    && switch (status) {
                        case STARTED -> surveyDao.updateSurveyStarted(surveyId, dateTime);
                        case CLOSED -> surveyDao.updateSurveyClosed(surveyId, dateTime);
                        default -> surveyDao.updateSurveyStatus(surveyId, status);
            };
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean update(Survey survey, int creatorId) throws ServiceException {

        if (survey != null) {

            if (survey.getCreator() == null
                    || survey.getName() == null
                    || survey.getStatus() == null) {
                logger.error("Passed invalid Survey data: creator = {}, name = {}, status = {}",
                        survey.getCreator(), survey.getName(), survey.getStatus());
                return false;
            }

            try {
                Optional<Survey> surveyOptional = surveyDao.findById(survey.getSurveyId());
                if(surveyOptional.isPresent()
                        && surveyOptional.get().getCreator().getUserId() == survey.getCreator().getUserId()) {

                    if (survey.getQuestions() == null) {
                        survey.setQuestions(new ArrayList<>());
                    }

                    if (survey.getDescription() == null) {
                        survey.setDescription(DEFAULT_DESCRIPTION);
                    }

                    if (survey.getTheme() == null) {
                        survey.setTheme(new Theme.ThemeBuilder().setThemeId(-1).getTheme());
                        logger.warn("Set default Theme as Survey theme object");
                    }

                    return surveyDao.update(survey);
                }
            } catch (DaoException e) {
                throw new ServiceException(e);
            }
        } else {
            logger.error("Passed null as Survey object");
        }

        return false;
    }

    @Override
    public Optional<Integer> countSurveyAttempts(int surveyId) throws ServiceException {
        try {
            return surveyDao.countSurveyAttempts(surveyId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
