document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = urlParams.get('id');
    console.log(categoryId)
    const categoryNameElement = document.getElementById('category-name');
    const postsContainer = document.getElementById('posts-container');
    
    if (categoryId) {
        loadCategory(categoryId);
        loadPosts(categoryId);
    } else {
        showError('Категория не найдена');
    }
    
    async function loadCategory(id) {
        try {
            // В реальном приложении заменить на реальный API endpoint
            const response = await fetch(`https://api.example.com/categories/${id}`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки категории');
            }
            
            const category = await response.json();
            categoryNameElement.textContent = category.name;
            
        } catch (error) {
            console.error('Ошибка:', error);
            categoryNameElement.textContent = 'Ошибка загрузки';
            showError('Не удалось загрузить данные категории');
        }
    }
    
    async function loadPosts(categoryId) {
        try {
            showLoading();
            
            // В реальном приложении заменить на реальный API endpoint
            const response = await fetch(`https://api.example.com/categories/${categoryId}/posts`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки постов');
            }
            
            const data = await response.json();
            displayPosts(data.posts);
            
        } catch (error) {
            console.error('Ошибка:', error);
            showError('Не удалось загрузить посты');
        }
    }
    
    function displayPosts(posts) {
        postsContainer.innerHTML = '';
        
        if (!posts || posts.length === 0) {
            postsContainer.innerHTML = '<div class="empty-posts">В этой категории пока нет постов</div>';
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
        postElement.innerHTML = `
            <div class="post-votes">
                <button class="vote-btn">↑</button>
                <div class="vote-count">${post.likesCount || 0}</div>
                <button class="vote-btn">↓</button>
            </div>
            <div class="post-content">
                <h3 class="post-title"><a href="/post.html?id=${post.id}">${post.title}</a></h3>
                <div class="post-meta">
                    Опубликовано пользователем ${post.authorName}, ${post.date}
                </div>
                ${post.description ? `<p class="post-excerpt">${post.description}</p>` : ''}
                <div class="post-tags">
                    ${post.tags.map(tag => `<a href="/tag/${tag}" class="post-tag">#${tag}</a>`).join('')}
                </div>
                <div class="post-actions">
                    <span class="post-action">${post.commentsCount || 0} комментариев</span>
                    <span class="post-action">Поделиться</span>
                    <span class="post-action">Сохранить</span>
                </div>
            </div>
        `;
        
        return postElement;
    }
    
    function showLoading() {
        postsContainer.innerHTML = '<div class="loading">Загрузка постов...</div>';
    }
    
    function showError(message) {
        postsContainer.innerHTML = `<div class="error">${message}</div>`;
    }
    
    // Обработка голосования 
});