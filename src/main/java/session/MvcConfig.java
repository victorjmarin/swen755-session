package session;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
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

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addViewController("/dashboard").setViewName("dashboard");
    registry.addViewController("/admin").setViewName("admin-task");
    registry.addViewController("/denied").setViewName("403");
    registry.addViewController("/expired").setViewName("expired");
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

}
