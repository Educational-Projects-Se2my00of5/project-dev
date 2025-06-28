let createPost = document.querySelector('.create-post-btn')

createPost.addEventListener('click', function (){
    window.location.href = '../CreatePost.html'
    console.log(1)
})

document.addEventListener('DOMContentLoaded', function() {
    // Элементы страницы
    const tabs = document.querySelectorAll('.tab');
    const postsContainer = document.querySelector('.posts-container');
    const bestTab = document.querySelector('.tab:nth-child(2)');
    const freshTab = document.querySelector('.tab:nth-child(1)');
    
    // Текущая активная вкладка
    let activeTab = 'fresh';
    
    // Данные постов (кешированные)
    let cachedPosts = {
        fresh: [],
        best: []
    };
    
    // Флаг загрузки
    let isLoading = false;
    
    // Инициализация
    init();
    
    function init() {
        // Обработчики событий для вкладок
        tabs.forEach(tab => {
            tab.addEventListener('click', switchTab);
        });
        
        // Загружаем данные для активной вкладки
        loadPosts(activeTab);
    }
    
    // Переключение между вкладками
    function switchTab(e) {
        const tab = e.target;
        
        // Если клик по уже активной вкладке или идет загрузка
        if (tab.classList.contains('active') || isLoading) return;
        
        // Меняем активную вкладку
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        
        // Определяем какую вкладку выбрали
        activeTab = tab.textContent.toLowerCase() === 'Лучшее' ? 'best' : 'fresh';
        
        // Показываем соответствующие посты
        showPosts(activeTab);
        
        // Если данные еще не загружены - загружаем
        if (cachedPosts[activeTab].length === 0) {
            loadPosts(activeTab);
        }
    }
    
    // Загрузка постов с сервера
    async function loadPosts(type) {
        isLoading = true;
        showLoadingState();
        
        try {
            // Имитация запроса к серверу
            const response = await fetch(`https://api.example.com/posts/${type}`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки данных');
            }
            
            const data = await response.json();
            
            // Сохраняем в кеш
            cachedPosts[type] = data.posts;
            
            // Показываем посты
            showPosts(type);
        } catch (error) {
            console.error('Ошибка:', error);
            showErrorState();
        } finally {
            isLoading = false;
            hideLoadingState();
        }
    }
    
    // Отображение постов
    function showPosts(type) {
        const posts = cachedPosts[type];
        
        // Очищаем контейнер
        postsContainer.innerHTML = '';
        
        if (posts.length === 0) {
            // Если постов нет
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-message';
            emptyMessage.textContent = 'Здесь пока нет постов';
            postsContainer.appendChild(emptyMessage);
            return;
        }
        
        // Создаем элементы постов
        posts.forEach(post => {
            const postElement = createPostElement(post);
            postsContainer.appendChild(postElement);
        });
    }
    
    // Создание элемента поста
    function createPostElement(post) {
        const postElement = document.createElement('div');
        postElement.className = 'post';
        postElement.innerHTML = `
            <div class="post-votes">
                <button class="vote-btn">↑</button>
                <div class="vote-count">${post.likes}</div>
                <button class="vote-btn">↓</button>
            </div>
            <div class="post-content">
                <h3 class="post-title">${post.title}</h3>
                <div class="post-meta">Опубликовано пользователем ${post.author}, ${post.date}</div>
                <div class="post-tags">
                    ${post.tags.map(tag => `<a href="#" class="post-tag">${tag}</a>`).join('')}
                </div>
                <div class="post-actions">
                    <span class="post-action">${post.comments} комментариев</span>
                    <span class="post-action">Поделиться</span>
                    <span class="post-action">Сохранить</span>
                </div>
            </div>
        `;
        
        return postElement;
    }
    
    // Показать состояние загрузки
    function showLoadingState() {
        const loading = document.createElement('div');
        loading.className = 'loading-state';
        loading.textContent = 'Загрузка...';
        postsContainer.innerHTML = '';
        postsContainer.appendChild(loading);
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
        error.textContent = 'Не удалось загрузить данные. Попробуйте позже.';
        postsContainer.innerHTML = '';
        postsContainer.appendChild(error);
    }
    
    // Имитация данных для демонстрации (в реальном коде это будет удалено)
    function mockData() {
        cachedPosts.fresh = [
            {
                title: "Новый фреймворк для фронтенда",
                author: "DevUser",
                likes: 124,
                comments: 42,
                date: "2 часа назад",
                tags: ["#frontend", "#javascript", "#новости"]
            },
            {
                title: "Как я оптимизировал запросы к базе",
                author: "DbMaster",
                likes: 89,
                comments: 15,
                date: "5 часов назад",
                tags: ["#базыданных", "#оптимизация"]
            }
        ];
        
        cachedPosts.best = [
            {
                title: "Лучшие практики React в 2023",
                author: "ReactPro",
                likes: 456,
                comments: 87,
                date: "3 дня назад",
                tags: ["#react", "#frontend", "#советы"]
            },
            {
                title: "Полное руководство по Docker",
                author: "DevOpsGuru",
                likes: 321,
                comments: 54,
                date: "1 неделю назад",
                tags: ["#docker", "#devops"]
            }
        ];
    }
    
    // Для демонстрации - заполняем моковыми данными
    mockData();
});