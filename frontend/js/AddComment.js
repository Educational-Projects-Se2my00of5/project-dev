document.addEventListener('DOMContentLoaded', function() {
    const commentForm = document.getElementById('comment-form');
    const commentInput = document.getElementById('comment-input');
    const commentsList = document.getElementById('comments-list');
    const commentsNum = document.getElementById('comments-num');
    const commentsCount = document.getElementById('comments-count');
    const commentsTitle = document.getElementById('comments-title');
    
    // Обработка отправки формы
    commentForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const commentText = commentInput.value.trim();
        
        if (commentText) {
            // Создаем новый комментарий
            const newComment = createComment('Вы', 'только что', commentText);
            
            // Добавляем в начало списка
            commentsList.insertBefore(newComment, commentsList.firstChild);
            
            // Обновляем счетчик комментариев
            const currentCount = parseInt(commentsNum.textContent);
            commentsNum.textContent = currentCount + 1;
            commentsCount.textContent = (currentCount + 1) + (currentCount === 0 ? ' комментарий' : 
                                      (currentCount < 4 ? ' комментария' : ' комментариев'));
            
            // Очищаем поле ввода
            commentInput.value = '';
        }
    });
    
    // Функция создания HTML для нового комментария
    function createComment(author, time, text) {
        const commentDiv = document.createElement('div');
        commentDiv.className = 'comment';
        commentDiv.innerHTML = `
            <div class="comment-header">
                <span class="comment-author">${author}</span>
                <span class="comment-time">${time}</span>
            </div>
            <div class="comment-text">
                ${text}
            </div>
            <div class="comment-actions">
                <span class="comment-action">Ответить</span>
                <span class="comment-action">👍 0</span>
                <span class="comment-action">👎</span>
            </div>
        `;
        
        return commentDiv;
    }
    
    // Обработка лайков/дизлайков для комментариев
    commentsList.addEventListener('click', function(e) {
        if (e.target.classList.contains('comment-action')) {
            const actionText = e.target.textContent.trim();
            
            if (actionText.startsWith('👍')) {
                // Лайк
                const count = parseInt(actionText.substring(2)) || 0;
                e.target.textContent = `👍 ${count + 1}`;
            } else if (actionText.startsWith('👎')) {
                // Дизлайк
                const count = parseInt(actionText.substring(2)) || 0;
                e.target.textContent = `👎 ${count + 1}`;
            }
        }
    });
});