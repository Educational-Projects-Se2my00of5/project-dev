document.addEventListener('DOMContentLoaded', function() {
    localStorage.setItem('guestMode', false);
    // Элементы страницы
    const tabs = document.querySelectorAll('.auth-tab');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const guestBtn = document.getElementById('guest-btn');

    // Переключение между вкладками
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            loginForm.classList.remove('active');
            registerForm.classList.remove('active');
            
            if (this.dataset.tab === 'login') {
                loginForm.classList.add('active');
            } else {
                registerForm.classList.add('active');
            }
        });
    });

    // Обработка входа
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        if (password == 'admin1'){
            localStorage.setItem('role', "admin")
        } else {
            localStorage.setItem('role', "user")
        }
        
        try {
            // Первый этап: получаем initial refresh token
            const initialResponse = await fetch('http://localhost:12345/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password })
            });
            
            if (!initialResponse.ok) {
                throw new Error('Неверный email или пароль');
            }
            
            const { refreshToken: initialRefreshToken } = await initialResponse.json();
            
            // Второй этап: получаем пару access-refresh токенов
            const tokenResponse = await fetch('http://localhost:12345/api/new-token-pair', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken: initialRefreshToken })
            });
            
            if (!tokenResponse.ok) {
                throw new Error('Ошибка получения токенов');
            }
            
            const { accessToken, refreshToken } = await tokenResponse.json();
            
            // Сохраняем токены
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            
            window.location.href = 'main.html';
            
        } catch (error) {
            alert(error.message);
            console.error('Ошибка входа:', error);
        }
    });

    // Обработка регистрации
    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const username = document.getElementById('register-username').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        
        try {
            // Первый этап: регистрация и получение initial refresh token
            const registerResponse = await fetch('http://localhost:12345/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email, password })
            });
            
            if (!registerResponse.ok) {
                const errorData = await registerResponse.json();
                throw new Error(errorData.message || 'Ошибка регистрации');
            }
            
            const { refreshToken: initialRefreshToken } = await registerResponse.json();
            
            // Второй этап: получаем пару access-refresh токенов
            const tokenResponse = await fetch('http://localhost:12345/api/new-token-pair', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken: initialRefreshToken })
            });
            
            if (!tokenResponse.ok) {
                throw new Error('Ошибка получения токенов после регистрации');
            }
            
            const { accessToken, refreshToken } = await tokenResponse.json();
            
            // Сохраняем токены
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            
            window.location.href = 'main.html';
            
        } catch (error) {
            alert(error.message);
            console.error('Ошибка регистрации:', error);
        }
    });

    // Вход как гость
    guestBtn.addEventListener('click', function() {
        localStorage.setItem('role', "guest");
        window.location.href = 'main.html';
    });

    // Проверка авторизации при загрузке
    if (localStorage.getItem('accessToken')) {
        window.location.href = 'main.html';
    }

    // Функция для обновления токенов (может использоваться в других частях приложения)
    async function refreshTokens() {
        try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (!refreshToken) throw new Error('No refresh token');
            
            const response = await fetch('/api/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refresh_token: refreshToken })
            });
            
            if (!response.ok) {
                throw new Error('Failed to refresh tokens');
            }
            
            const { accessToken, refreshToken: newRefreshToken } = await response.json();
            
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', newRefreshToken);
            
            return accessToken;
            
        } catch (error) {
            console.error('Ошибка обновления токенов:', error);
            // Перенаправляем на страницу входа при ошибке
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = 'auth.html';
            throw error;
        }
    }
});