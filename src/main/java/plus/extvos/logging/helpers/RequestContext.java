package plus.extvos.logging.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Mingcai SHEN
 */
public class RequestContext {

    private final Logger log = LoggerFactory.getLogger(RequestContext.class);

    private final HttpServletRequest request;

    protected RequestContext(HttpServletRequest req) {
        request = req;
    }

    public static RequestContext probe() {
        return new RequestContext(((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest());
    }

    public String getAgent() {
        return request.getHeader("User-Agent");
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getParams() {
        return request.getQueryString();
    }

    public String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip == null) {
            ip = "";
        }
        return ip;
    }
}
