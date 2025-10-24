package com.bedrock.gatekeeper.commons.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Value;

@Route("login")
@AnonymousAllowed
@PageTitle("Login | Gatekeeper")
@CssImport("./themes/login-view.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  private static final String LOGIN_ACTION = "login";
  private static final String ERROR_PARAM = "error";
  
  private static final String CARD_WIDTH = "400px";
  private static final String CARD_PADDING = "var(--lumo-space-l)";
  private static final String BORDER_RADIUS = "var(--lumo-border-radius-l)";
  
  private final LoginForm loginForm;
  private final String contextPath;
  private final String accessUri;

  public LoginView(@Value("${server.servlet.context-path}") String contextPath,
                   @Value("${gatekeeper.admin-console.init.clients.google.access-uri}") String accessUri) {
    this.contextPath = contextPath;
    this.accessUri = accessUri;
    this.loginForm = new LoginForm();
    
    initView();
  }

  private void initView() {
    setSizeFull();
    setJustifyContentMode(JustifyContentMode.CENTER);
    setAlignItems(Alignment.CENTER);
    addClassName(LumoUtility.Background.CONTRAST_5);

    add(createLoginCard());
  }

  private Component createLoginCard() {
    VerticalLayout card = new VerticalLayout();
    card.setWidth(CARD_WIDTH);
    card.setSpacing(true);
    card.setPadding(true);
    
    card.getStyle()
        .set("padding", CARD_PADDING)
        .set("border-radius", BORDER_RADIUS)
        .set("background", "var(--lumo-base-color)")
        .set("box-shadow", "var(--lumo-box-shadow-m)");

    card.add(
        createTitle(),
        createLoginForm(),
        createSeparator(),
        createGoogleButton()
    );

    return card;
  }

  private Component createTitle() {
    H2 title = new H2("Sign in to Gatekeeper");
    title.addClassName(LumoUtility.FontSize.XLARGE);
    title.addClassName(LumoUtility.FontWeight.LIGHT);
    title.addClassName(LumoUtility.TextAlignment.CENTER);
    title.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
    return title;
  }

  private Component createLoginForm() {
    loginForm.setAction(LOGIN_ACTION);
    loginForm.setI18n(createLoginI18n());
    loginForm.setForgotPasswordButtonVisible(false);
    return loginForm;
  }

  private LoginI18n createLoginI18n() {
    LoginI18n i18n = LoginI18n.createDefault();
    
    LoginI18n.Form form = i18n.getForm();
    form.setUsername("Email address");
    form.setPassword("Password");
    form.setSubmit("Sign in");
    
    LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
    errorMessage.setTitle("Authentication failed");
    errorMessage.setMessage("Please check your credentials and try again.");
    
    return i18n;
  }

  private Component createSeparator() {
    HorizontalLayout separator = new HorizontalLayout();
    separator.setWidthFull();
    separator.setAlignItems(Alignment.CENTER);
    separator.addClassName(LumoUtility.Margin.Vertical.MEDIUM);

    Hr leftLine = new Hr();
    leftLine.getStyle().set("flex-grow", "1");

    Span orText = new Span("or");
    orText.addClassName(LumoUtility.TextColor.SECONDARY);
    orText.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);

    Hr rightLine = new Hr();
    rightLine.getStyle().set("flex-grow", "1");

    separator.add(leftLine, orText, rightLine);
    return separator;
  }

  private Component createGoogleButton() {
    Image googleIcon = createGoogleIcon();
    
    Button button = new Button("Continue with Google", googleIcon);
    button.setWidthFull();
    button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    button.addClickListener(_ -> navigateToGoogleAuth());
    
    return button;
  }

  private Image createGoogleIcon() {
    Image icon = new Image(contextPath + "/google_logo.svg", "Google");
    icon.setWidth("18px");
    icon.setHeight("18px");
    icon.addClassName(LumoUtility.Margin.Right.SMALL);
    return icon;
  }

  private void navigateToGoogleAuth() {
    String googleAuthUrl = contextPath + accessUri;
    UI.getCurrent().getPage().open(googleAuthUrl, "_self");
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (event.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey(ERROR_PARAM)) {
      loginForm.setError(true);
    }
  }
}