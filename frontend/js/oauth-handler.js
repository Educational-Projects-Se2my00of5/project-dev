import * as api from './api/client.js';
import * as tokenService from './auth/token.js';
import { redirectTo } from './utils/helpers.js';

document.addEventListener('DOMContentLoaded', async () => {
    const errorEl = document.getElementById('oauth-error');
    const params = new URLSearchParams(window.location.search);
    const refreshToken = params.get('token');
    const isNewUser = params.get('new') === '1';

    if (!refreshToken) {
        errorEl.textContent = 'Ошибка: токен аутентификации не найден. Попробуйте войти снова.';
        return;
    }

    try {
        // Шаг 1: Мы получили refresh токен в URL, теперь обменяем его на пару токенов
        const newTokens = await api.getNewTokenPair(refreshToken);

        // Шаг 2: Сохраняем токены
        tokenService.saveTokens(newTokens.accessToken, newTokens.refreshToken);

        if (isNewUser) {
            // Здесь может быть логика для новых пользователей, 
            // например, перенаправление на страницу завершения регистрации профиля
            alert('Добро пожаловать! Вход через Google успешен.');
            // redirectTo('/complete-profile.html');
        } else {
            alert('Вход через Google успешен!');
            // redirectTo('/dashboard.html');
        }
        // Временное решение, пока нет других страниц
        redirectTo('/frontend/html/index.html');

    } catch (error) {
        errorEl.textContent = `Не удалось завершить вход: ${error.message}`;
        console.error(error);
    }
});