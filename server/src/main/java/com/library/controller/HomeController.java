package com.library.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
            <html>
            <body>
                <h1>Добре дошли в KnowledgeHub!</h1>
                <a href="/oauth2/authorization/google">Вход с Google</a>
            </body>
            </html>
            """;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User user) {
        String name = user.getAttribute("name");
        String email = user.getAttribute("email");

        return """
            <html>
            <body>
                <h1>Здравей, %s!</h1>
                <p>Email: %s</p>
                <a href="/logout">Изход</a>
            </body>
            </html>
            """.formatted(name, email);
    }
}