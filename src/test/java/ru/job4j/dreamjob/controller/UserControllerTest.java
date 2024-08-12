package ru.job4j.dreamjob.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

public class UserControllerTest {

    private UserService userService;

    private HttpServletRequest request;

    private HttpSession session;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        userController = new UserController(userService);
    }

    @Test
    public void testGetRegistrationPage() {
        String viewName = userController.getRegistrationPage();
        assertThat(viewName).isEqualTo("users/register");
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        User user = new User(1, "test@example.com", "Test User", "password");
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        String viewName = userController.register(model, user);

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void testRegisterSuccess() {
        User user = new User(1, "test@example.com", "Test User", "password");
        when(userService.save(any(User.class))).thenReturn(Optional.of(user));

        Model model = new ConcurrentModel();
        String viewName = userController.register(model, user);

        assertThat(viewName).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void testGetLoginPage() {
        String viewName = userController.getLoginPage();
        assertThat(viewName).isEqualTo("users/login");
    }

    @Test
    public void testLoginUserUserNotFound() {
        User user = new User(1, "test@example.com", "Test User", "password");
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        String viewName = userController.loginUser(user, model, request);

        assertThat(viewName).isEqualTo("users/login");
        assertThat(model.getAttribute("error")).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void testLoginUserSuccess() {
        User user = new User(1, "test@example.com", "Test User", "password");
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        Model model = new ConcurrentModel();
        String viewName = userController.loginUser(user, model, request);

        verify(session).setAttribute("user", user);
        assertThat(viewName).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void testLogout() {
        when(request.getSession()).thenReturn(session);

        String viewName = userController.logout(session);

        verify(session).invalidate();
        assertThat(viewName).isEqualTo("redirect:/users/login");
    }
}
