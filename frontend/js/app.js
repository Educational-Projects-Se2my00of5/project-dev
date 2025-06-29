import * as api from './api/client.js';
import * as tokenService from './auth/token.js';
import { redirectTo } from './utils/helpers.js';

document.addEventListener('DOMContentLoaded', () => {
    // Если у пользователя уже есть токен, не показываем ему страницу входа
    // а сразу кидаем на "главную". Замени '/dashboard.html' на свой путь.
    if (tokenService.getToken('refreshToken')) {
        // Для примера, можно сделать тестовый запрос к защищенному эндпоинту
        console.log("Пользователь уже вошел. Перенаправление...");
        // redirectTo('/dashboard.html'); // Раскомментируй, когда будет главная страница
        alert('Вы уже авторизованы!');
        return;
    }

    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    
    const loginErrorEl = document.getElementById('login-error');
    const registerErrorEl = document.getElementById('register-error');

    const loginContainer = document.getElementById('login-form-container');
    const registerContainer = document.getElementById('register-form-container');

    const showRegisterLink = document.getElementById('show-register');
    const showLoginLink = document.getElementById('show-login');

    // Переключение форм
    showRegisterLink.addEventListener('click', (e) => {
        e.preventDefault();
        loginContainer.classList.add('hidden');
        registerContainer.classList.remove('hidden');
    });

    showLoginLink.addEventListener('click', (e) => {
        e.preventDefault();
        registerContainer.classList.add('hidden');
        loginContainer.classList.remove('hidden');
    });

    // Обработчик входа
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        loginErrorEl.textContent = '';
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        try {
            // Шаг 1: Логинимся и получаем refresh token
            const { refreshToken } = await api.login(email, password);

            // Шаг 2: Сразу же обмениваем его на пару токенов
            const newTokens = await api.getNewTokenPair(refreshToken);

            // Шаг 3: Сохраняем оба токена
            tokenService.saveTokens(newTokens.accessToken, newTokens.refreshToken);

            alert('Вход успешен!');
            // redirectTo('/dashboard.html'); // Перенаправление на главную
        } catch (error) {
            loginErrorEl.textContent = `Ошибка входа: ${error.message}`;
        }
    });

    // Обработчик регистрации
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        registerErrorEl.textContent = '';
        const username = document.getElementById('register-username').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;

        try {
            // Логика аналогична логину
            const { refreshToken } = await api.register(username, email, password);
            const newTokens = await api.getNewTokenPair(refreshToken);
            tokenService.saveTokens(newTokens.accessToken, newTokens.refreshToken);

            alert('Регистрация и вход прошли успешно!');
            // redirectTo('/dashboard.html');
        } catch (error) {
            registerErrorEl.textContent = `Ошибка регистрации: ${error.message}`;
        }
    });
});