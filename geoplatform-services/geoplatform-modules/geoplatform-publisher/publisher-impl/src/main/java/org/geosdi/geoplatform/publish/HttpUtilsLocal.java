/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2011 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.geoplatform.publish;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Low level HTTP utilities.
 */
public class HttpUtilsLocal {

    private static final Logger LOGGER = Logger.getLogger(HttpUtilsLocal.class);

    /**
     * Performs an HTTP GET on the given URL.
     *
     * @param url The URL where to connect to.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     */
    public static String get(String url) throws MalformedURLException {
        return get(url, null, null);
    }

    /**
     * Performs an HTTP GET on the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     */
    public static String get(String url, String username, String pw) throws MalformedURLException {

        GetMethod httpMethod = null;
        try {
            HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
            httpMethod = new GetMethod(url);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(httpMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream is = httpMethod.getResponseBodyAsStream();
                String response = IOUtils.toString(is);
                if (response.trim().length() == 0) { // sometime gs rest fails
                    LOGGER.warn("ResponseBody is empty");
                    return null;
                } else {
                    return response;
                }
            } else {
                LOGGER.info("(" + status + ") " + HttpStatus.getStatusText(status) + " -- " + url);
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + url + "]");
        } catch (IOException e) {
            LOGGER.info("Error talking to [" + url + "]", e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        return null;
    }

    /**
     * PUTs a File to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param file The File to be sent.
     * @param contentType The content-type to advert in the PUT.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, File file, String contentType, String username, String pw) {
        return put(url, new FileRequestEntity(file, contentType), username, pw);
    }

    /**
     * PUTs a String to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The content to be sent as a String.
     * @param contentType The content-type to advert in the PUT.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, String content, String contentType, String username, String pw) {
        try {
            return put(url, new StringRequestEntity(content, contentType, null), username, pw);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Cannot PUT " + url, ex);
            return null;
        }
    }

    /**
     * PUTs a String representing an XML document to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The XML content to be sent as a String.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String putXml(String url, String content, String username, String pw) {
        return put(url, content, "text/xml", username, pw);
    }

    /**
     * Performs a PUT to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, RequestEntity requestEntity, String username, String pw) {
        return send(new PutMethod(url), url, requestEntity, username, pw);
    }

    /**
     * POSTs a File to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param file The File to be sent.
     * @param contentType The content-type to advert in the POST.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, File file, String contentType, String username, String pw) {
        return post(url, new FileRequestEntity(file, contentType), username, pw);
    }

    /**
     * POSTs a String to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The content to be sent as a String.
     * @param contentType The content-type to advert in the POST.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, String content, String contentType, String username, String pw) {
        try {
            return post(url, new StringRequestEntity(content, contentType, null), username, pw);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Cannot POST " + url, ex);
            return null;
        }
    }

    /**
     * POSTs a String representing an XML document to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The XML content to be sent as a String.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String postXml(String url, String content, String username, String pw) {
        return post(url, content, "text/xml", username, pw);
    }

    /**
     * Performs a POST to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, RequestEntity requestEntity, String username, String pw) {
        return send(new PostMethod(url), url, requestEntity, username, pw);
    }

    /**
     * Send an HTTP request (PUT or POST) to a server.
     * <BR>Basic auth is used if both username and pw are not null.
     * <P>
     * Only <UL>
     * <LI>200: OK</LI>
     * <LI>201: ACCEPTED</LI>
     * <LI>202: CREATED</LI>
     * </UL> are accepted as successful codes; in these cases the response string will be returned.
     *
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    private static String send(final EntityEnclosingMethod httpMethod, String url, RequestEntity requestEntity, String username, String pw) {

        try {
            HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
// httpMethod = new PutMethod(url);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            if (requestEntity != null) {
                httpMethod.setRequestEntity(requestEntity);
            }
            int status = client.executeMethod(httpMethod);

            switch (status) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_ACCEPTED:
                    String response = IOUtils.toString(httpMethod.getResponseBodyAsStream());
// LOGGER.info("================= POST " + url);
                    LOGGER.info("HTTP " + httpMethod.getStatusText() + ": " + response);
                    return response;
                default:
                    LOGGER.warn("Bad response: code[" + status + "]"
                            + " msg[" + httpMethod.getStatusText() + "]"
                            + " url[" + url + "]"
                            + " method[" + httpMethod.getClass().getSimpleName() + "]: "
                            + IOUtils.toString(httpMethod.getResponseBodyAsStream()));
                    return null;
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + url + "]");
            return null;
        } catch (IOException e) {
            LOGGER.error("Error talking to " + url + " : " + e.getLocalizedMessage());
            return null;
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    public static boolean delete(String url, final String user, final String pw) {

        DeleteMethod httpMethod = null;

        try {
            HttpClient client = new HttpClient();
            setAuth(client, url, user, pw);
            httpMethod = new DeleteMethod(url);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(httpMethod);
            String response = "";
            if (status == HttpStatus.SC_OK) {
                InputStream is = httpMethod.getResponseBodyAsStream();
                response = IOUtils.toString(is);
                if (response.trim().equals("")) { // sometimes gs rest fails
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("ResponseBody is empty (this may be not an error since we just performed a DELETE call)");
                    }
                    return true;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("(" + status + ") " + httpMethod.getStatusText() + " -- " + url);
                }
                return true;
            } else {
                LOGGER.info("(" + status + ") " + httpMethod.getStatusText() + " -- " + url);
                LOGGER.info("Response: '" + response + "'");
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + url + "]");
        } catch (IOException e) {
            LOGGER.info("Error talking to [" + url + "]", e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        return false;
    }

    /**
     * @return true if the server response was an HTTP_OK
     */
    public static boolean httpPing(String url) {
        return httpPing(url, null, null);
    }

    public static boolean httpPing(String url, String username, String pw) {

        GetMethod httpMethod = null;

        try {
            HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
            httpMethod = new GetMethod(url);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
            int status = client.executeMethod(httpMethod);
            if (status != HttpStatus.SC_OK) {
                LOGGER.warn("PING failed at '" + url + "': (" + status + ") " + httpMethod.getStatusText());
                return false;
            } else {
                return true;
            }

        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    /**
     * Used to query for REST resources.
     *
     * @param url The URL of the REST resource to query about.
     * @param username
     * @param pw
     * @return true on 200, false on 404.
     * @throws RuntimeException on unhandled status or exceptions.
     */
    public static boolean exists(String url, String username, String pw) {

        GetMethod httpMethod = null;

        try {
            HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
            httpMethod = new GetMethod(url);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
            int status = client.executeMethod(httpMethod);
            switch (status) {
                case HttpStatus.SC_OK:
                    return true;
                case HttpStatus.SC_NOT_FOUND:
                    return false;
                default:
                    throw new RuntimeException("Unhandled response status at '" + url + "': (" + status + ") " + httpMethod.getStatusText());
            }
        } catch (ConnectException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    private static void setAuth(HttpClient client, String url, String username, String pw) throws MalformedURLException {
        URL u = new URL(url);
        if (username != null && pw != null) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, pw);
            client.getState().setCredentials(new AuthScope(u.getHost(), u.getPort()), defaultcreds);
            client.getParams().setAuthenticationPreemptive(true); // GS2 by default always requires authentication
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not setting credentials to access to " + url);
            }
        }
    }
}
