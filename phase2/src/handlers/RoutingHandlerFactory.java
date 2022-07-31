package handlers;

import io.undertow.server.HttpHandler;
import useCases.ManagerData;

public class RoutingHandlerFactory {

    // TODO possibly move out of handlers package as is not appropriate
    public HttpHandler getHandler(String request, ManagerData managerData) {
        switch (request) {
            case "login":
                return new LoginHandler();
            case "loginRedirect":
                return new LoginRedirectHandler(managerData);
            case "home":
                return new HomeHandler(managerData);
            case "homeRedirect":
                return new HomeRedirectHandler(managerData);
            case "signUp":
                return new SignUpHandler();
            case "signUpRedirect":
                return new SignUpRedirectHandler(managerData);
        }
        return new ErrorMessageHandler("No handler exists in RoutingHandlers factory named: " + request);
    }
}