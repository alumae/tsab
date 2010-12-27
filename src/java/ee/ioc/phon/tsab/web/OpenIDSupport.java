package ee.ioc.phon.tsab.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import ee.ioc.phon.tsab.common.TsabException;
import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.User;

public class OpenIDSupport {

  private final static Logger log = Logger.getLogger(OpenIDSupport.class);

  private static ConsumerManager manager;

  public static void requestLogin(HttpServletRequest request, HttpServletResponse response) throws TsabException {
    try {

      StringBuffer url = request.getRequestURL();

      String reqUrl = url.substring(0, url.lastIndexOf("/"));

      String returnURL = reqUrl + "/login";

      String string = "https://www.google.com/accounts/o8/id";

      List discoveries = getManager().discover(string);

      DiscoveryInformation discovered = getManager().associate(discoveries);

      request.getSession().setAttribute("discovered", discovered);

      AuthRequest authReq = getManager().authenticate(discovered, returnURL);

      FetchRequest fetch = FetchRequest.createFetchRequest();
      fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
      fetch.addAttribute("firstName", "http://axschema.org/namePerson/first", true);
      fetch.addAttribute("lastName", "http://axschema.org/namePerson/last", true);
      authReq.addExtension(fetch);

      response.sendRedirect(authReq.getDestinationUrl(true));

    } catch (Exception e) {
      throw new TsabException("Unable to process login!", e);
    }

  }

  public static User verify(HttpServletRequest request, HttpServletResponse response) throws TsabException {
    ParameterList openidResp = new ParameterList(request.getParameterMap());

    DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute("discovered");
    StringBuffer receivingURL = request.getRequestURL();
    String queryString = request.getQueryString();
    if (queryString != null && queryString.length() > 0)
      receivingURL.append("?").append(request.getQueryString());

    VerificationResult verification;
    try {
      verification = getManager().verify(receivingURL.toString(), openidResp, discovered);
    } catch (Exception e) {
      throw new TsabException("Failed to verify authentication response!", e);
    }

    Identifier verified = verification.getVerifiedId();

    if (verified != null) {
      log.info("User identified:" + verified.getIdentifier());
      AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

      if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
        FetchResponse fetchResp;
        try {
          fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
        } catch (MessageException e) {
          throw new TsabException("Failed to load extension properties from the response!", e);
        }

        String email = (String) fetchResp.getAttributeValues("email").iterator().next();
        
        String firstName = (String) fetchResp.getAttributeValues("firstName").iterator().next();
        String lastName = (String) fetchResp.getAttributeValues("lastName").iterator().next();
        
        String fullname = firstName+" "+lastName;

        log.info("Authenticated user. E-mail:"+email+"; Name:"+fullname);
        
        return TsabDaoService.getDao().fetchUser(email, fullname);
        
      }

      return null;
      
    } else {
      // OpenID authentication failed
      log.info("Authentication failed!");
      return null;
    }

  }

  private static ConsumerManager getManager() {
    if (manager == null) {
      try {
        manager = new ConsumerManager();
      } catch (ConsumerException e) {
        log.error("Failed to initialize Consumer Manager!", e);
      }
    }
    return manager;
  }
}
