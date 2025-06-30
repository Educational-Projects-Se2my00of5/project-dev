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
 * Разбирает JWT токен и возвращает его полезную нагрузку (payload).
 * @param {string} token - JWT токен.
 * @returns {object|null} - Разобранный объект payload или null в случае ошибки.
 */

function parseJwtPayload(token) {
    if (!token) {
        return null;
    }
    try {
        // JWT состоит из 3 частей, разделенных точкой: Header.Payload.Signature
        // Нам нужна вторая часть - Payload.
        const base64Url = token.split('.')[1];
        if (!base64Url) {
            throw new Error("Invalid JWT: Missing payload part.");
        }

        // Заменяем символы, специфичные для Base64Url, на стандартные Base64 символы
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        
        // Декодируем строку из Base64 и парсим как JSON
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error("Failed to parse JWT token:", error);
        return null;
    }
}


/**
 * Получает ID пользователя из accessToken, хранящегося в cookie.
 * Предполагается, что ID пользователя хранится в поле 'sub' (Subject).
 * @returns {string|number|null} - ID пользователя или null, если токен не найден или некорректен.
 */
export function getUserIdFromTokenCookie() {
    // 1. Получаем токен из cookie
    const accessToken = getToken('accessToken');
    if (!accessToken) {
        console.log("Access token not found in cookies.");
        return null;
    }
    
    // 2. Разбираем токен, чтобы получить payload
    const payload = parseJwtPayload(accessToken);
    if (!payload) {
        return null;
    }
    
    // 3. Возвращаем ID пользователя.
    // Стандартно ID пользователя хранится в поле 'sub' (subject).
    // Если у вас другое поле (например, 'id' или 'userId'), измените 'sub' на ваше поле.
    return payload.userId || null;
}

/**
 * Удаляет все токены аутентификации
 */
export function clearTokens() {
    document.cookie = 'accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.cookie = 'refreshToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}