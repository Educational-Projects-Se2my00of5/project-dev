document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');

    const commentForm = document.getElementById('comment-form');
    const commentInput = document.getElementById('comment-input');
    const commentsList = document.getElementById('comments-list');
    const commentsNum = document.getElementById('comments-num');
    const commentsCount = document.getElementById('comments-count');
    const commentsTitle = document.getElementById('comments-title');

    if (localStorage.getItem('role') === "guest"){
            commentForm.style.display = 'none'
    } else{
        commentForm.style.display = 'block'
    }
    
    // Обработка отправки формы
    commentForm.addEventListener('submit', function(e) {


        e.preventDefault();
        
        const commentText = commentInput.value.trim();
        
        if (commentText) {
            // Создаем новый комментарий
            const newComment = createComment(commentText);
            
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
    async function createComment(text) {
        const postData = {
            body: text
        };

        const accessToken = localStorage.getItem('accessToken');

        const response = await fetch(`http://localhost:12345/api/posts/${postId}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`,
                },
                body: JSON.stringify(postData)
        });


        // const commentDiv = document.createElement('div');
        // commentDiv.className = 'comment';
        // commentDiv.innerHTML = `
        //     <p class="comment-author">${comment.authorInfo.username}</p>
        //     <p class="comment-text">${comment.body}</p>
        // `;
        
        // return commentDiv;
        location.reload()
    }
    
});