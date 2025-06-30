import ApiClient from './ApiClient.js';
document.addEventListener('DOMContentLoaded', async () => {
    const postsContainer = document.getElementById('posts-container');
    const editModal = document.getElementById('edit-modal');
    const closeBtn = document.querySelector('.close-btn');
    const editForm = document.getElementById('edit-post-form');

    
    // Загрузка постов при открытии страницы
    async function loadUserPosts() {
        try {
            postsContainer.innerHTML = '<div class="loading">Загрузка ваших постов...</div>';

            const userProfile = await ApiClient.getUserProfile();
            const id = userProfile.id
            
            const response = await fetch(`http://localhost:12345/api/posts?userFilter=${id}&page=0&size=20`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки данных');
            }
            
            const data = await response.json();
            let posts = data.content
            
            if (posts.length === 0) {
                postsContainer.innerHTML = '<div class="empty-message">У вас пока нет постов</div>';
                return;
            }
            
            renderPosts(posts);
        } catch (error) {
            console.error('Ошибка загрузки постов:', error);
            postsContainer.innerHTML = `<div class="error">Ошибка загрузки: ${error.message}</div>`;
        }
    }
    
    // Отображение постов
    function renderPosts(posts) {
        postsContainer.innerHTML = '';
        
        posts.forEach(post => {
            const postElement = document.createElement('div');
            postElement.className = 'post';
            postElement.dataset.postId = post.id;
            postElement.innerHTML = `
                <div class="post-content">
                    <h3 class="post-title">${post.title}</h3>
                    <div class="post-meta">Опубликовано пользователем ${post.shortUserInfo.username}</div>
                    <span class="vote-count">${post.likes}</span>
                    <span>👍</span>
                    <div class="post-actions">
                        <button class="post-action-btn edit-btn" data-id="${post.id}">Редактировать</button>
                        <button class="post-action-btn delete-btn" data-id="${post.id}">Удалить</button>
                    </div>
                </div>
            `;
            postsContainer.appendChild(postElement);
        });
        
        // Добавляем обработчики для кнопок
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', handleEditPost);
        });
        
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', handleDeletePost);
        });
    }
    
    // Редактирование поста
    async function handleEditPost(e) {
        const postId = e.target.dataset.id;
        
        try {
            // Загружаем данные поста для редактирования
            const response = await fetch(`http://localhost:12345/api/posts/${postId}`);
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки данных');
            }
            
            const post = await response.json();
            
            // Заполняем форму редактирования
            document.getElementById('edit-post-id').value = post.id;
            document.getElementById('edit-post-title').value = post.title;
            document.getElementById('edit-post-content').value = post.content;
            
            // Показываем модальное окно
            editModal.style.display = 'block';
        } catch (error) {
            console.error('Ошибка загрузки поста:', error);
            alert('Не удалось загрузить пост для редактирования');
        }
    }
    
    // Удаление поста
    async function handleDeletePost(e) {
        const postId = e.target.dataset.id;
        
        if (!confirm('Вы уверены, что хотите удалить этот пост?')) {
            return;
        }
        
        try {
            await ApiClient.deletePost(postId);
            // Удаляем пост из DOM
            document.querySelector(`.post[data-post-id="${postId}"]`).remove();
            
            // Проверяем, остались ли посты
            if (postsContainer.children.length === 0) {
                postsContainer.innerHTML = '<div class="empty-message">У вас пока нет постов</div>';
            }
        } catch (error) {
            console.error('Ошибка удаления поста:', error);
            alert('Не удалось удалить пост');
        }
    }
    
    // Обработчик сохранения изменений
    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const postId = document.getElementById('edit-post-id').value;
        const title = document.getElementById('edit-post-title').value;
        const content = document.getElementById('edit-post-content').value;

        let categoriesId = [1]

        const postData = {
            title: title,
            content: content,
            categoriesId: [1]
        };
        
        try {
            await ApiClient.updatePost(postData, postId);
            
            // Закрываем модальное окно
            editModal.style.display = 'none';
            
            // Перезагружаем посты
            await loadUserPosts();
        } catch (error) {
            console.error('Ошибка обновления поста:', error);
            alert('Не удалось обновить пост');
        }
    });
    
    // Закрытие модального окна
    closeBtn.addEventListener('click', () => {
        editModal.style.display = 'none';
    });
    
    window.addEventListener('click', (e) => {
        if (e.target === editModal) {
            editModal.style.display = 'none';
        }
    });
    
    // Инициализация
    await loadUserPosts();
});