# project-dev
Учебный проект по предмету проектная разработка

Проект по разработке веб-сайта для публикации пользовательского контента по типу социальных сайтов, таких как Reddit или Pikabu.

Фундаментальная механика системы заключается в том, что весь контент создается и управляется самими пользователями.

Основной функционал включает регистрацию, создание постов, комментирование и систему рейтинга, которая определяет порядок отображения публикаций в лентах «свежего» и «лучшего».

# Dafault
Пользователь по умолчанию с правами админа:
```
{
  "email": "admin@example.com",
  "password": "admin1"
}
```

---
# Install
1.  Клонировать репозиторий (или скачать архив и распаковать) и перейти в папку
    ```shell
    git clone https://github.com/jcc/blog.git
    cd project-dev
    ```
2.  Запустить docker-compose (на windows требуется запущенный docker desktop)
    1.  Для работы oauth понадобятся секреты google. После их получения помещаем в .env файл или подставляем в docker-compose.yml
    ```shell
    docker-compose up --build
    ```
---
# Install Dev
Для запуска из IDE нужно:
1.  Запустить redis - ```docker-compose -f docker-compose.dev.yml up```
2.  Добавить в профиль запуска секреты `oauth google`
---
# Регистрация Oauth google
1.  Переходим в [Google Cloud Consol](https://console.cloud.google.com/welcome), входим в аккаунт и создаём проект.
    `Вверху страницы, рядом с логотипом "Google Cloud" будет кнопка  "New Project"`
2.  В меню навигации (☰) выберите "APIs & Services" -> "OAuth consent screen".
    Нажмите "Get started"
3.  Введите все необходимые поля и в "Audience" выберите "External" `Для всех пользователей`
    Сохраните
4.  
    *  В меню навигации (☰) выберите "APIs & Services" -> "Credentials".
    *  Нажмите "+ Create Credentials" и выберите "OAuth client ID".
    *  В поле "Application type" выберите "Web application"
    *  В разделе "Authorized redirect URIs" добавьте следующий URI - ```http://localhost:12345/login/oauth2/code/google```
    *  Сохраните и появится окно с "Client ID" и "Client secret"