package session;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private SessionRegistry sessionRegistry;

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addViewController("/dashboard").setViewName("dashboard");
    registry.addViewController("/denied").setViewName("403");
    registry.addViewController("/expired").setViewName("expired");
  }

  @RequestMapping(value = "/logout-users", method = RequestMethod.GET)
  public String logoutUsersGet() {
    return "logout-users";
  }

  @RequestMapping(value = "/logout-users", method = RequestMethod.POST)
  public ModelAndView logoutUsersPost() {
    final List<Object> principals = sessionRegistry.getAllPrincipals();
    for (final Object p : principals) {
      final User user = (User) p;
      // Do not log out admins
      if (userHasAuthority("ROLE_ADMIN", user))
        continue;
      final List<SessionInformation> sessions = sessionRegistry.getAllSessions(p, false);
      for (final SessionInformation si : sessions) {
        si.expireNow();
      }
    }
    final ModelAndView result = new ModelAndView("logout-users");
    result.addObject("logged_out", true);
    return result;
  }

  @ResponseBody
  @RequestMapping(value = "/invalidate", method = RequestMethod.POST)
  public String invalidateSession(final HttpServletRequest request) {
    final HttpSession session = request.getSession();
    if (session != null)
      session.invalidate();
    return "Session invalidated.";
  }

  @ResponseBody
  @RequestMapping(value = "/refresh", method = RequestMethod.POST)
  public String refreshSession() {
    return "Session refreshed.";
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String root(final Principal principal) {
    String result = "login";
    if (principal != null)
      result = "dashboard";
    return "redirect:" + result;
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(final Principal principal) {
    String result = "login";
    if (principal != null)
      result = "redirect:dashboard";
    return result;
  }

  @RequestMapping(value = "/expired", method = RequestMethod.GET)
  public ModelAndView expired() {
    final ModelAndView result = new ModelAndView("login");
    result.addObject("expired", true);
    return result;
  }

  public static boolean userHasAuthority(final String authority, final User user) {
    final Collection<GrantedAuthority> authorities = user.getAuthorities();
    for (final GrantedAuthority grantedAuthority : authorities) {
      if (authority.equals(grantedAuthority.getAuthority())) {
        return true;
      }
    }
    return false;
  }

}
