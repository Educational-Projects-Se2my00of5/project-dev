document.addEventListener('DOMContentLoaded', function() {
    // Контейнер для категорий
    const categoriesContainer = document.querySelector('.categories-container');
    
    // Загрузка категорий при открытии страницы( временно заменил)
    // loadCategories();

    // Функция загрузки категорий с сервера
    async function loadCategories() {
        try {
            // Показываем состояние загрузки
            showLoadingState();
            
            // Имитация запроса к серверу (в реальном коде заменить на реальный URL)
            const response = await fetch('https://api.example.com/categories');
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки категорий');
            }
            
            const data = await response.json();
            
            // Отображаем полученные категории
            displayCategories(data.categories);
        } catch (error) {
            console.error('Ошибка загрузки категорий:', error);
            showErrorState();
        } finally {
            hideLoadingState();
        }
    }

    // Функция отображения категорий
    function displayCategories(categories) {
        // Очищаем контейнер
        categoriesContainer.innerHTML = '';
        
        if (!categories || categories.length === 0) {
            showEmptyState();
            return;
        }
        
        // Создаем элементы категорий
        categories.forEach(category => {
            const categoryElement = createCategoryElement(category);
            categoriesContainer.appendChild(categoryElement);
        });
    }

    // Создание элемента категории
    function createCategoryElement(category) {
        const categoryCard = document.createElement('div');
        categoryCard.className = 'category-card';
        categoryCard.innerHTML = `
            <div class="category-header">
                <div class="category-icon">${category.name.charAt(0).toUpperCase()}</div>
                <div class="category-name">${category.name}</div>
            </div>
            <p class="category-description">${category.description}</p>
        `;
        
        // Добавляем обработчик клика
        categoryCard.addEventListener('click', () => {
            window.location.href = `Category.html?id=${category.id}`;
        });
        
        return categoryCard;
    }

    // Показать состояние загрузки
    function showLoadingState() {
        const loading = document.createElement('div');
        loading.className = 'loading-state';
        loading.textContent = 'Загрузка категорий...';
        categoriesContainer.innerHTML = '';
        categoriesContainer.appendChild(loading);
    }

    // Скрыть состояние загрузки
    function hideLoadingState() {
        const loading = document.querySelector('.loading-state');
        if (loading) loading.remove();
    }

    // Показать состояние ошибки
    function showErrorState() {
        const error = document.createElement('div');
        error.className = 'error-state';
        error.innerHTML = `
            <p>Не удалось загрузить категории.</p>
            <button id="retry-btn">Попробовать снова</button>
        `;
        categoriesContainer.innerHTML = '';
        categoriesContainer.appendChild(error);
        
        // Добавляем обработчик для кнопки повтора
        document.getElementById('retry-btn').addEventListener('click', loadCategories);
    }

    // Показать состояние "нет категорий"
    function showEmptyState() {
        const empty = document.createElement('div');
        empty.className = 'empty-state';
        empty.textContent = 'Категории не найдены';
        categoriesContainer.innerHTML = '';
        categoriesContainer.appendChild(empty);
    }

    // Демонстрационные данные (удалить в реальном приложении)
    function mockCategories() {
        const mockData = {
            categories: [
                {
                    id: 1,
                    name: "Программирование",
                    description: "Обсуждение языков программирования, фреймворков и алгоритмов",
                    postsCount: "12.5k",
                    subscribers: "56.7k"
                },
                {
                    id: 2,
                    name: "Технологии",
                    description: "Новости и обсуждения гаджетов и IT-инноваций",
                    postsCount: "8.2k",
                    subscribers: "42.3k"
                },
                {
                    id: 3,
                    name: "Наука",
                    description: "Физика, химия, биология и другие научные дисциплины",
                    postsCount: "6.7k",
                    subscribers: "38.1k"
                },
                {
                    id: 4,
                    name: "Искусство",
                    description: "Живопись, музыка, литература и другие виды искусства",
                    postsCount: "5.9k",
                    subscribers: "31.4k"
                },
                {
                    id: 5,
                    name: "Политика",
                    description: "Обсуждение внутренней и международной политики",
                    postsCount: "7.3k",
                    subscribers: "45.2k"
                },
                {
                    id: 6,
                    name: "Спорт",
                    description: "Футбол, хоккей, баскетбол и другие виды спорта",
                    postsCount: "4.8k",
                    subscribers: "29.6k"
                }
            ]
        };
        
        displayCategories(mockData.categories);
    }

    // Для демонстрации используем моковые данные
    // В реальном приложении эту строку нужно удалить
    mockCategories();
});