package jakarta.servlet.http;

import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public interface HttpServletResponse extends ServletResponse {

    public static final int SC_CONTINUE = 100;
    public static final int SC_SWITCHING_PROTOCOLS = 101;
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_RESET_CONTENT = 205;
    public static final int SC_PARTIAL_CONTENT = 206;
    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final int SC_MOVED_PERMANENTLY = 301;
    public static final int SC_MOVED_TEMPORARILY = 302;
    public static final int SC_FOUND = 302;
    public static final int SC_SEE_OTHER = 303;
    public static final int SC_NOT_MODIFIED = 304;
    public static final int SC_USE_PROXY = 305;
    public static final int SC_TEMPORARY_REDIRECT = 307;
    public static final int SC_PERMANENT_REDIRECT = 308;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_PAYMENT_REQUIRED = 402;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_METHOD_NOT_ALLOWED = 405;
    public static final int SC_NOT_ACCEPTABLE = 406;
    public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int SC_REQUEST_TIMEOUT = 408;
    public static final int SC_CONFLICT = 409;
    public static final int SC_GONE = 410;
    public static final int SC_LENGTH_REQUIRED = 411;
    public static final int SC_PRECONDITION_FAILED = 412;
    public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int SC_REQUEST_URI_TOO_LONG = 414;
    public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int SC_EXPECTATION_FAILED = 417;
    public static final int SC_MISDIRECTED_REQUEST = 421;
    public static final int SC_UNPROCESSABLE_CONTENT = 422;
    public static final int SC_UPGRADE_REQUIRED = 426;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    public static final int SC_GATEWAY_TIMEOUT = 504;
    public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;

    public void addCookie(Cookie cookie);

    public boolean containsHeader(String string);

    public String encodeURL(String string);

    public String encodeRedirectURL(String string);

    public void sendError(int i, String string) throws IOException;

    public void sendError(int i) throws IOException;

    public default void sendRedirect(String location) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aload_0
         * 1: aload_1
         * 2: sipush        302
         * 5: iconst_1
         * 6: invokeinterface #2,  4            // InterfaceMethod sendRedirect:(Ljava/lang/String;IZ)V
         * 11: return
         *  */
        // </editor-fold>
    }

    public default void sendRedirect(String location, boolean clearBuffer) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aload_0
         * 1: aload_1
         * 2: sipush        302
         * 5: iload_2
         * 6: invokeinterface #2,  4            // InterfaceMethod sendRedirect:(Ljava/lang/String;IZ)V
         * 11: return
         *  */
        // </editor-fold>
    }

    public default void sendRedirect(String location, int sc) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aload_0
         * 1: aload_1
         * 2: iload_2
         * 3: iconst_1
         * 4: invokeinterface #2,  4            // InterfaceMethod sendRedirect:(Ljava/lang/String;IZ)V
         * 9: return
         *  */
        // </editor-fold>
    }

    public void sendRedirect(String string, int i, boolean bln) throws IOException;

    public void setDateHeader(String string, long l);

    public void addDateHeader(String string, long l);

    public void setHeader(String string, String string1);

    public void addHeader(String string, String string1);

    public void setIntHeader(String string, int i);

    public void addIntHeader(String string, int i);

    public void setStatus(int i);

    public int getStatus();

    public String getHeader(String string);

    public Collection<String> getHeaders(String string);

    public Collection<String> getHeaderNames();

    public default void setTrailerFields(Supplier<Map<String, String>> supplier) {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: return
         *  */
        // </editor-fold>
    }

    public default Supplier<Map<String, String>> getTrailerFields() {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aconst_null
         * 1: areturn
         *  */
        // </editor-fold>
    }
}
