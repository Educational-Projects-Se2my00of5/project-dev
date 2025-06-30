import { getToken, saveTokens, clearTokens } from './token.js';
import { redirectTo } from './helpers.js';


const API_BASE_URL = 'http://localhost:12345';



// --- Публичные методы API ---

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
    return await response.json();
}

/**
 * Логин пользователя.
 * @param {string} email
 * @param {string} password
 * @returns {Promise<object>} - { refreshToken, id, email }
 */
export async function login(email, password) {
    const response = await baseFetch('/api/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
    });
    return await response.json();
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
        body: JSON.stringify({ username, email, password }),
    });
    return await response.json();
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
 * Получение своего профиля.
 */
export async function myProfile() {

    const response = await fetchWithAuth('/api/me', {
        method: 'GET',
    });

    return await response.json();
}

/**
 * Изменение своего профиля.
 */
export async function editMyProfile(username) {
    const response = await fetchWithAuth('/api/me', {
        method: 'PUT',
        body: JSON.stringify({ username }),
    });
    return  await response.json();
}

/**
 * Изменение своего пароля.
 */
export async function editMyPassword(oldPassword, password) {
    const response = await fetchWithAuth('/api/me/password', {
        method: 'PUT',
        body: JSON.stringify({ oldPassword, password }),
    });
    return await response.json();
}



// Флаг, чтобы предотвратить множественные запросы на обновление токена
let isRefreshing = false;
// Промис, который будет хранить результат запроса на обновление
let refreshPromise = null;
/**
 * Основная функция для запросов к защищённым API с автоматическим обновлением токена.
 * @param {string} endpoint - Путь к эндпоинту
 * @param {object} options - Опции для fetch
 * @returns {Promise<any>} - Распарсенный JSON-ответ
 */
export async function fetchWithAuth(endpoint, options = {}) {
    try {
        const accessToken = getToken('accessToken');
        const headers = {
            ...options.headers
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
                    // Если обновление не удалось, чистим все и редиректим на главную
                    console.error('Failed to refresh token:', err);
                    clearTokens();
                    redirectTo('/main.html');
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

            if (!retryResponse.ok) {
                const errorBody = await retryResponse.json();
                throw new Error(errorBody.message);
            }

            return retryResponse;
        }

        return response;
    } catch (error) {
        throw error;
    }
}


/**
 * Базовая функция для выполнения запросов без автоматической обработки токенов.
 * @param {string} endpoint - Путь к эндпоинту (например, '/api/login')
 * @param {object} options - Опции для fetch
 * @returns {Promise<Response>}
 */
async function baseFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    const response = await fetch(url, { ...options, headers });

    if (!response.ok && response.status !== 401) {
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
        body: JSON.stringify({ refreshToken }),
    });

    const newTokens = await response.json();
    saveTokens(newTokens.accessToken, newTokens.refreshToken);
    return newTokens;
}