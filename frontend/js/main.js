import ApiClient from './ApiClient.js';
let createPost = document.querySelector('.create-post-btn')


createPost.addEventListener('click', function (){
    window.location.href = '../CreatePost.html'
    console.log(1)
})

let createCat = document.querySelector('#c-cat')

createCat.addEventListener('click', function (){
    window.location.href = '../CreateCategory.html'
    console.log(1)
})

if(localStorage.getItem('role') === "guest"){
    createCat.style.display = "none"
    createPost.style.display = "none"
    console.log(localStorage.getItem('role'))
}

if(localStorage.getItem('role') === "admin"){
    createCat.style.display = "block"
    createPost.style.display = "block"
    console.log(localStorage.getItem('role'))
}

if(localStorage.getItem('role') === "user"){
    createCat.style.display = "none"
    createPost.style.display = "block"
    console.log(localStorage.getItem('role'))
}


document.addEventListener('DOMContentLoaded', function() {
    console.log(localStorage.getItem('role'))

    const tabs = document.querySelectorAll('.tab');
    const postsContainer = document.getElementById('posts-container');
    let activeTab = 'fresh';
    let cachedPosts = {
        fresh: [],
        best: []
    };

    // Инициализация
    init();
    
    function init() {
        tabs.forEach(tab => {
            tab.addEventListener('click', switchTab);
        });
        
        // Временно заменено -
        loadPosts(activeTab);
        // mockData()
        
        // Обработка кликов по карточкам постов
        postsContainer.addEventListener('click', function(e) {
            const postCard = e.target.closest('.post');
            if (postCard && postCard.dataset.postId) {
                window.location.href = `../Post.html?id=${postCard.dataset.postId}`;
            }
        });
    }
    
    async function loadPosts(type) {
        try {
            showLoading();
            
            // Имитация запроса к серверу
            const response = await fetch(`http://localhost:12345/api/posts?page=0&size=20`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки данных');
            }
            
            const data = await response.json();

            // const data = await ApiClient.getPosts();

            console.log(data)
            console.log(data.content)


            cachedPosts[type] = data.content;
            showPosts(type);
            
        } catch (error) {
            console.error('Ошибка:', error);
            showErrorState();
        } finally {
            hideLoadingState();
        }
    }
    
    function showPosts(type) {
        const posts = cachedPosts[type];
        postsContainer.innerHTML = '';
        
        if (posts.length === 0) {
            postsContainer.innerHTML = '<div class="empty-message">Здесь пока нет постов</div>';
            return;
        }
        
        posts.forEach(post => {
            const postElement = createPostElement(post);
            postsContainer.appendChild(postElement);
        });
    }
    
    function createPostElement(post) {
        const postElement = document.createElement('div');
        postElement.className = 'post';
        postElement.dataset.postId = post.id; // Добавляем ID поста
        postElement.innerHTML = `
            <div class="post-content">
                <h3 class="post-title">${post.title}</h3>
                <div class="post-meta">Опубликовано пользователем ${post.shortUserInfo.username}</div>
                <span class="vote-count">${post.likes}</span>
                <span>👍</span>
            </div>
        `;
        return postElement;
    }
    
    function switchTab(e) {
        const tab = e.target;
        if (tab.classList.contains('active')) return;
        
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        
        activeTab = tab.textContent.toLowerCase() === 'лучшее' ? 'best' : 'fresh';
        showPosts(activeTab);
        
        if (cachedPosts[activeTab].length === 0) {
            loadPosts(activeTab);
        }
    }
    
    function showLoading() {
        postsContainer.innerHTML = '<div class="loading-state">Загрузка...</div>';
    }
    
    function hideLoadingState() {
        const loading = document.querySelector('.loading-state');
        if (loading) loading.remove();
    }
    
    function showErrorState() {
        postsContainer.innerHTML = '<div class="error-state">Не удалось загрузить данные. Попробуйте позже.</div>';
    }
    
    // // Для демонстрации
    // mockData();
    
    // function mockData() {
    //     cachedPosts.fresh = [
    //         {
    //             id: 1,
    //             title: "Новый фреймворк для фронтенда",
    //             author: "DevUser",
    //             likes: 124,
    //             comments: 42,
    //             date: "2 часа назад",
    //             description: "Обзор нового JavaScript-фреймворка, который может изменить ваш подход к разработке...",
    //             tags: ["#frontend", "#javascript"]
    //         },
    //         {
    //             id: 2,
    //             title: "Как я оптимизировал запросы к базе",
    //             author: "DbMaster",
    //             likes: 89,
    //             comments: 15,
    //             date: "5 часов назад",
    //             description: "Рассказываю о методах оптимизации SQL-запросов, которые помогли ускорить наше приложение...",
    //             tags: ["#базыданных", "#оптимизация"]
    //         }
    //     ];
        
    //     cachedPosts.best = [
    //         {
    //             id: 3,
    //             title: "Лучшие практики React в 2023",
    //             author: "ReactPro",
    //             likes: 456,
    //             comments: 87,
    //             date: "3 дня назад",
    //             description: "Современные подходы к разработке на React, которые стоит использовать в ваших проектах...",
    //             tags: ["#react", "#frontend"]
    //         },
    //         {
    //             id: 4,
    //             title: "Полное руководство по Docker",
    //             author: "DevOpsGuru",
    //             likes: 321,
    //             comments: 54,
    //             date: "1 неделю назад",
    //             description: "Все что вам нужно знать о Docker для эффективной работы с контейнерами...",
    //             tags: ["#docker", "#devops"]
    //         }
    //     ];
        
    //     showPosts(activeTab);
    // }
});