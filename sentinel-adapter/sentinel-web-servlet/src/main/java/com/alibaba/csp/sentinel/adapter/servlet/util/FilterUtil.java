package com.alibaba.csp.sentinel.adapter.servlet.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * Util class for web servlet filter.
 *
 * @author youji.zj
 * @author Eric Zhao
 */
public final class FilterUtil {

    public static String filterTarget(HttpServletRequest request) {
        String pathInfo = getResourcePath(request);
        if (!pathInfo.startsWith("/")) {
            pathInfo = "/" + pathInfo;
        }

        if ("/".equals(pathInfo)) {
            return pathInfo;
        }

        // Note: pathInfo should be converted to camelCase style.
        int lastSlashIndex = pathInfo.lastIndexOf("/");

        if (lastSlashIndex >= 0) {
            pathInfo = pathInfo.substring(0, lastSlashIndex) + "/"
                + StringUtil.trim(pathInfo.substring(lastSlashIndex + 1));
        } else {
            pathInfo = "/" + StringUtil.trim(pathInfo);
        }

        return pathInfo;
    }

    public static void blockRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer url = request.getRequestURL();

        if ("GET".equals(request.getMethod()) && StringUtil.isNotBlank(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }

        if (StringUtil.isEmpty(WebServletConfig.getBlockPage())) {
            writeDefaultBlockedPage(response);
        } else {
            String redirectUrl = WebServletConfig.getBlockPage() + "?http_referer=" + url.toString();
            // Redirect to the customized block page.
            response.sendRedirect(redirectUrl);
        }
    }

    private static void writeDefaultBlockedPage(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("Blocked by Sentinel (flow limiting)");
        out.flush();
        out.close();
    }

    private static String getResourcePath(HttpServletRequest request) {
        String pathInfo = normalizeAbsolutePath(request.getPathInfo(), false);
        String servletPath = normalizeAbsolutePath(request.getServletPath(), pathInfo.length() != 0);

        return servletPath + pathInfo;
    }

    private static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalStateException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute, boolean forceRelative,
                                        boolean removeTrailingSlash) throws IllegalStateException {
        char[] pathChars = StringUtil.trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // Check path and slash.
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == '/' || firstChar == '\\';
            endsWithSlash = lastChar == '/' || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append("/");
        }

        while (index < length) {
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex - index);
            index = nextSlashIndex;

            // Ignore "."
            if (".".equals(element)) {
                continue;
            }

            // Backtrack ".."
            if ("..".equals(element)) {
                if (level == 0) {
                    if (isAbsolutePath) {
                        throw new IllegalStateException(path);
                    } else {
                        buf.append("../");
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            pathChars[level++] = (char)buf.length();
            buf.append(element).append('/');
        }

        // remove the last "/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == '/' || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != '/' && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }

    private FilterUtil() {}
}
