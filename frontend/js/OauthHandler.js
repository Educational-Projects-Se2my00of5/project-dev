import * as api from './utils/api.js';
import * as tokenService from './utils/token.js';
import { redirectTo } from './utils/helpers.js';

document.addEventListener('DOMContentLoaded', async () => {
    const errorEl = document.getElementById('oauth-error');
    const params = new URLSearchParams(window.location.search);
    const refreshToken = params.get('token');
    const isNewUser = params.get('new') === '1';

    const statusMessageEl = document.getElementById('status-message');
    const newUserForm = document.getElementById('new-user-form');

    if (!refreshToken) {
        statusMessageEl.classList.add('hidden');
        errorEl.textContent = 'Ошибка: токен аутентификации не найден. Попробуйте войти снова.';
        return;
    }

    try {
        const newTokens = await api.getNewTokenPair(refreshToken);
        tokenService.saveTokens(newTokens.accessToken, newTokens.refreshToken);
        localStorage.setItem('role', "user")
        localStorage.setItem('accessToken', newTokens.accessToken);
        localStorage.setItem('refreshToken', newTokens.refreshToken);    
        const userId = tokenService.getUserIdFromTokenCookie();

        if (isNewUser) {
            //подставим username заданный google
            const userInfo = await api.myProfile();
            document.getElementById('username').value = userInfo.username;

            statusMessageEl.classList.add('hidden');
            newUserForm.classList.remove('hidden');



            newUserForm.addEventListener('submit', async (event) => {
                event.preventDefault(); // Предотвращаем стандартную отправку формы

                const username = document.getElementById('username').value;
                const password = document.getElementById('password').value;

                if (!username || !password) {
                    errorEl.textContent = 'Пожалуйста, заполните все поля.';
                    return;
                }

                errorEl.textContent = ''; // Очищаем старые ошибки
                statusMessageEl.textContent = 'Сохраняем данные...';
                statusMessageEl.classList.remove('hidden');
                newUserForm.classList.add('hidden');

                try {
                    await api.editMyProfile(username);
                    await api.editMyPassword(null, password)

                    statusMessageEl.textContent = 'Регистрация завершена! Перенаправляем...';
                    setTimeout(() =>{window.location.href = 'main.html'}, 1000);

                } catch (e) {
                    statusMessageEl.classList.add('hidden');
                    newUserForm.classList.remove('hidden');
                    errorEl.textContent = `Ошибка при завершении регистрации: ${e.message}. Попробуйте снова.`;
                }
            });
        } else {
            statusMessageEl.textContent = 'Успешный вход! Перенаправляем...';
            setTimeout(() => {window.location.href = 'main.html'}, 1000);
        }
    } catch (e) {
        statusMessageEl.classList.add('hidden');
        errorEl.textContent = `Произошла ошибка аутентификации: ${e.message}. Пожалуйста, попробуйте войти еще раз.`;
    }
});