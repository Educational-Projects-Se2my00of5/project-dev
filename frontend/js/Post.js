document.addEventListener('DOMContentLoaded', async () => {
    // Получаем ID поста из URL (например: /post.html?id=123)
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');

    if (!postId) {
        alert('ID поста не указан в URL');
        return;
    }

    try {
        // Выполняем запрос к серверу
        const response = await fetch(`http://localhost:12345/api/posts/${postId}`);
        
        if (!response.ok) {
            throw new Error('Ошибка при загрузке поста');
        }

        const post = await response.json();

        // Отображаем данные поста
        document.querySelector('.post-title').textContent = post.title;
        document.querySelector('.post-content').textContent = post.content;
        document.getElementById('post-likes').textContent = post.likes;
        document.querySelector('.post-author').textContent = post.shortUserInfo.username;
        console.log(post.title, post.content, post.likes, post.shortUserInfo.username)

        document.getElementById('comments-count').textContent = post.rootComments.length;
        document.getElementById('comments-num').textContent = post.rootComments.length;

        // Отображаем комментарии
        const commentsContainer = document.getElementById('comments-list');
        commentsContainer.innerHTML = ''; // Очищаем контейнер

        post.rootComments.forEach(comment => {
            const commentElement = document.createElement('div');
            commentElement.className = 'comment';
            commentElement.innerHTML = `
                <p class="comment-author">${comment.authorInfo.username}</p>
                <p class="comment-text">${comment.body}</p>
            `;
            commentsContainer.appendChild(commentElement);
        });

    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось загрузить пост');
    }
});


document.getElementById('post-likes').addEventListener('click', async () =>{

    if (localStorage.getItem('role') === "guest"){
        return
    }
    // Получаем ID поста из URL (например: /post.html?id=123)
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');


    const accessToken = localStorage.getItem('accessToken');

    const response = await fetch(`http://localhost:12345/api/posts/${id}/like`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        }
    });

    // const response = await fetch(`http://localhost:12345/api/posts/${id}/like`);
        
    // if (!response.ok) {
    //     throw new Error('Ошибка при загрузке поста');
    // }

    const post = await response.json();

    document.getElementById('post-likes').textContent = post.likes;

    if (post.hasLiked){
        document.getElementById('post-likes').classList.add('liked')
    } else{
        document.getElementById('post-likes').classList.remove('liked')
    }


})