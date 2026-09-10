package com.example.schedule_arranger.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;
    private final Environment environment;

    public GlobalErrorController(ErrorAttributes errorAttributes, Environment environment) {
        this.errorAttributes = errorAttributes;
        this.environment = environment;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        WebRequest webRequest = new ServletWebRequest(request);
        boolean isDev = environment.matchesProfiles("dev");

        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );
        if (isDev) {
            options = options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }

        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, options);

        Object status = attributes.get("status");
        Object errorName = attributes.get("error");
        Object message = attributes.get("message");
        Object path = attributes.get("path");
        Object trace = attributes.get("trace");

        model.addAttribute("status", status);
        model.addAttribute("error", errorName);
        model.addAttribute("message", message);
        model.addAttribute("path", path);
        model.addAttribute("trace", isDev ? trace : null);
        model.addAttribute("isDev", isDev);

        if (Integer.valueOf(404).equals(status)) {
            return "error/404";
        }
        return "error";
    }
}
