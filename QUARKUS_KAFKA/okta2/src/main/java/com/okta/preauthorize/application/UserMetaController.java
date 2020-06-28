package com.okta.preauthorize.application;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * The class-wide request mapping path <code>/user</code> is refined by the
 * the <code>oauthUserInfo()</code> endpoint as <code>/user/oauth2metadata</code>.
 */
@RequestMapping("/user")
@Controller
public class UserMetaController {

  @RequestMapping("/oauth2metadata")
  @ResponseBody
  public String oauthUserInfo(
      @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
      @AuthenticationPrincipal OAuth2User oauth2User) {

    return
      new StringBuilder("<body style= '" + BODY + "'>").

      append(SPANOPEN + "       User Name: " + SPANCLOSE).
      append("<br/>" + oauth2User.getName() + "<br/><br/>").

      append(SPANOPEN + "User Authorities:" + SPANCLOSE).
      append("<br/>" + oauth2User.getAuthorities() + "<br/></br>").

      append(SPANOPEN + "     Client Name: " + SPANCLOSE).
      append("<br/>" + authorizedClient.
        getClientRegistration().
          getClientName()).

      append("<hr style='" + RULE + "'/>").

      append(getAttrs(oauth2User.getAttributes())).toString();
  }

  private String getAttrs(Map<String, Object> values) {

    StringBuilder model =
      new StringBuilder(
        SPANOPEN + "User Attributes" + SPANCLOSE);

    for (String key : values.keySet()) {

      model.append("<br><br>" + SPANOPEN).
        append(key).
        append(": " + align(key) + SPANCLOSE).
        append("<br>" + values.get(key));
    }

    return model.toString();
  }


  private static final String UL_CLOSE =
    "</ul>";

  private static final String UL_OPEN =
    "<br><ul style='list-style-type: none;'>";

  private static final String SPANOPEN =
    "<span style='color:black;font-size:0.8em;'><b>";

  private static final String SPANCLOSE =
    "</b></span>";

  private static final String BODY =
    new StringBuilder(1_000).
      append("color:32aa32;").
      append("font-family:\"Optima\";").
      append("background-image: url(\"/img/linen.png\";").
      append("background-repeat: repeat;").
      append("font-kerning: normal;").
      toString();

  private static final String RULE =
    new StringBuilder(1_000).
      append("border:0;").
      append("height:1px;").
      append("width:97%;").
      append("margin-top:2.0em;").
      append("margin-bottom:1.3em;").
      append("background-image: -webkit-linear-gradient(left,#dedede,#32aa32,#dedede);").
      append("background-image: -moz-linear-gradient(left,#dedede,#32aa32,#dedede);").
      append("background-image: -ms-linear-gradient(left,#dedede,#32aa32,#dedede);").
      append("background-image: -o-linear-gradient(left,#dedede,#32aa32,#dedede);").
      toString();


  private String align(Object value) {

    final int LIMIT = 25;
    final String PAD = "&nbsp;";

    String result = "";


    for (int i=0; i < (LIMIT - value.toString().length()); i++) {
      result += PAD;
    }

    return result;
  }
}
