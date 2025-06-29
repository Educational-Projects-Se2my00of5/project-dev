import { API_BASE_URL } from '../config.js';
import { getToken, saveTokens, clearTokens } from '../auth/token.js';
import { redirectTo } from '../utils/helpers.js';

// Флаг, чтобы предотвратить множественные запросы на обновление токена
let isRefreshing = false;
// Промис, который будет хранить результат запроса на обновление
let refreshPromise = null;

/**
 * Базовая функция для выполнения запросов без автоматической обработки токенов.
 * @param {string} endpoint - Путь к эндпоинту (например, '/api/login')
 * @param {object} options - Опции для fetch
 * @returns {Promise<Response>}
 */
async function baseFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const response = await fetch(url, options);

    if (!response.ok) {
        // Попытаемся извлечь сообщение об ошибке из тела ответа
        let errorMessage;
        try {
            const errorBody = await response.json();
            errorMessage = errorBody.message || JSON.stringify(errorBody);
        } catch (e) {
            errorMessage = `HTTP error! status: ${response.status} ${response.statusText}`;
        }
        throw new Error(errorMessage);
    }

    return response;
}

/**
 * Обновляет пару токенов, используя refresh token.
 * @returns {Promise<object>} - Новая пара токенов { accessToken, refreshToken }
 */
async function refreshToken() {
    const refreshToken = getToken('refreshToken');
    if (!refreshToken) {
        throw new Error('No refresh token available');
    }

    const response = await baseFetch('/api/new-token-pair', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
    });

    const newTokens = await response.json();
    saveTokens(newTokens.accessToken, newTokens.refreshToken);
    return newTokens;
}

/**
 * Основная функция для запросов к API с автоматическим обновлением токена.
 * @param {string} endpoint - Путь к эндпоинту
 * @param {object} options - Опции для fetch
 * @returns {Promise<any>} - Распарсенный JSON-ответ
 */
export async function fetchWithAuth(endpoint, options = {}) {
    try {
        const accessToken = getToken('accessToken');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        };

        if (accessToken) {
            headers['Authorization'] = `Bearer ${accessToken}`;
        }

        const response = await baseFetch(endpoint, { ...options, headers });

        // Если ответ 401, пытаемся обновить токен
        if (response.status === 401) {
            // Чтобы избежать "гонки" запросов, когда несколько запросов получают 401
            if (!isRefreshing) {
                isRefreshing = true;
                refreshPromise = refreshToken().catch(err => {
                    // Если обновление не удалось, чистим все и редиректим на логин
                    console.error('Failed to refresh token:', err);
                    clearTokens();
                    redirectTo('/index.html');
                    return Promise.reject(err);
                }).finally(() => {
                    isRefreshing = false;
                    refreshPromise = null;
                });
            }

            // Ждем завершения запроса на обновление
            await refreshPromise;
            
            // Повторяем исходный запрос с новым токеном
            const newAccessToken = getToken('accessToken');
            if (newAccessToken) {
                headers['Authorization'] = `Bearer ${newAccessToken}`;
            }
            const retryResponse = await baseFetch(endpoint, { ...options, headers });
            return retryResponse.json();
        }

        // Если в ответе нет тела (например, статус 204), возвращаем null
        if (response.status === 204) {
            return null;
        }

        return response.json();
    } catch (error) {
        // Обрабатываем ошибки 401, которые могли произойти на первом запросе
        // до логики обновления (например, если fetch сам выбрасывает ошибку)
        // и направляем на обновление токена.
        const originalResponse = error.response; // Предполагаем, что fetchWithAuth может быть обернут для добавления response в error
        if (originalResponse && originalResponse.status === 401) {
             // ... логика повторного вызова ... (как выше)
             // Для упрощения, просто перехватываем ошибку и выбрасываем ее дальше
             console.error('API call failed:', error.message);
             throw error;
        }
        
        console.error('API call failed:', error.message);
        throw error;
    }
}


// --- Публичные методы API ---

/**
 * Логин пользователя.
 * @param {string} email
 * @param {string} password
 * @returns {Promise<object>} - { refreshToken, id, email }
 */
export async function login(email, password) {
    const response = await baseFetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
    });
    return response.json();
}

/**
 * Регистрация пользователя.
 * @param {string} username
 * @param {string} email
 * @param {string} password
 * @returns {Promise<object>} - { refreshToken, id, email }
 */
export async function register(username, email, password) {
    const response = await baseFetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password }),
    });
    return response.json();
}

/**
 * Выход из системы.
 */
export async function logout() {
    const refreshToken = getToken('refreshToken');
    if (refreshToken) {
        try {
            await fetchWithAuth('/api/logout', {
                method: 'POST',
                body: JSON.stringify({ refreshToken }),
            });
        } catch (error) {
            console.error("Logout failed on server, but clearing tokens locally.", error);
        }
    }
    clearTokens();
    redirectTo('/index.html');
}

/**
 * Обмен refresh-токена на новую пару токенов.
 * @param {string} token - Refresh Token
 * @returns {Promise<object>} - { accessToken, refreshToken }
 */
export async function getNewTokenPair(token) {
    const response = await baseFetch('/api/new-token-pair', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: token }),
    });
    return response.json();
}