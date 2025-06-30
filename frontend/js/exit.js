
// Обработчик кнопки выхода
document.getElementById('logout-btn').addEventListener('click', function(e) {
    e.preventDefault();
    console.log(1)
    logoutUser();
});
// Функция выхода из системы
function logoutUser() {
    // 1. Отправляем запрос на сервер для недействительности токенов (если нужно)
    const res = fetch('http://localhost:12345/api/logout', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ refreshToken: localStorage.getItem('refreshToken') })
    }).catch(error => {
        console.error('Ошибка при выходе:', error);
    });

    console.log(res)
    // 2. Удаляем токены и данные пользователя
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('role');
    
    // 3. Перенаправляем на страницу входа
    window.location.href = 'auth.html';
}
// Проверка авторизации при загрузке
if (!localStorage.getItem('accessToken') && !localStorage.getItem('guestMode')) {
    window.location.href = 'auth.html';
}
