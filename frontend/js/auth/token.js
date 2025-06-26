/**
 * Сохраняет токены в cookie
 * @param {string} accessToken - Токен доступа
 * @param {string} refreshToken - Токен обновления
 */
export function saveTokens(accessToken, refreshToken) {
    // Access token на 5 минут
    const accessExpires = new Date(Date.now() + 5 * 60 * 1000);
    document.cookie = `accessToken=${accessToken}; expires=${accessExpires.toUTCString()}; path=/; SameSite=Lax`;

    // Refresh token на 10 дней
    const refreshExpires = new Date(Date.now() + 10 * 24 * 60 * 60 * 1000);
    document.cookie = `refreshToken=${refreshToken}; expires=${refreshExpires.toUTCString()}; path=/; SameSite=Lax`;
}

/**
 * Получает токен из cookie по имени
 * @param {string} name - Имя токена (accessToken или refreshToken)
 * @returns {string|null} - Значение токена или null
 */
export function getToken(name) {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) {
        return match[2];
    }
    return null;
}

/**
 * Удаляет все токены аутентификации
 */
export function clearTokens() {
    document.cookie = 'accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.cookie = 'refreshToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}