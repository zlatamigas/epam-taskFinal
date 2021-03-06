package com.zlatamigas.surveyplatform.controller.filter;

import com.zlatamigas.surveyplatform.controller.command.CommandType;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder.REQUEST_ATTRIBUTE_PARAMETER_ORDER_TYPE;
import static com.zlatamigas.surveyplatform.controller.navigation.AttributeParameterHolder.REQUEST_ATTRIBUTE_PARAMETER_SEARCH_WORDS;
import static com.zlatamigas.surveyplatform.controller.navigation.PageNavigation.*;
import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_ORDER;
import static com.zlatamigas.surveyplatform.util.search.SearchParameter.DEFAULT_SEARCH_WORDS;

/**
 * Filter for setting request search parameters before page with confirmed themes.
 * Works on fictive URL after deleting theme.
 */
@WebFilter(filterName = "BeforeThemesConfirmedFilter",
        urlPatterns = URL_REDIRECT_THEMES_CONFIRMED,
        dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.REQUEST})
public class BeforeThemesConfirmedFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String searchWordsStr = request.getParameter(REQUEST_ATTRIBUTE_PARAMETER_SEARCH_WORDS);
        if (searchWordsStr != null) {
            searchWordsStr = URLEncoder.encode(searchWordsStr, StandardCharsets.UTF_8.toString());
        } else {
            searchWordsStr = DEFAULT_SEARCH_WORDS;
        }
        String orderTypeName = request.getParameter(REQUEST_ATTRIBUTE_PARAMETER_ORDER_TYPE);
        if (orderTypeName == null) {
            orderTypeName = DEFAULT_ORDER;
        }
        String page = String.format(URL_REDIRECT_BASE_PATTERN + URL_REDIRECT_PARAMETER_PATTERN.repeat(2),
                CommandType.THEMES_CONFIRMED.name(),
                REQUEST_ATTRIBUTE_PARAMETER_SEARCH_WORDS, searchWordsStr,
                REQUEST_ATTRIBUTE_PARAMETER_ORDER_TYPE, orderTypeName);

        response.sendRedirect(request.getContextPath() + page);
    }
}
