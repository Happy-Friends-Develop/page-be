package com.example.hello_friends.user.presentation;

import com.example.hello_friends.user.application.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자")
public class UserController {
    private final UserService userService;


}
