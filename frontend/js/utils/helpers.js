/**
 * Перенаправляет на указанную страницу
 * @param {string} path - Путь для перенаправления (например, '/dashboard.html')
 */
export function redirectTo(path) {
    window.location.href = window.location.origin + path;
}