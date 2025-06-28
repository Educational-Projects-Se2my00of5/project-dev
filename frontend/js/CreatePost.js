document.addEventListener('DOMContentLoaded', function() {
    // Элементы формы
    const postForm = document.querySelector('.create-post-form');
    const categorySelect = document.getElementById('category');
    const titleInput = document.getElementById('title');
    const descriptionInput = document.getElementById('description');
    const contentInput = document.getElementById('content');
    const tagsContainer = document.getElementById('tags-container');
    const tagInput = document.getElementById('tag-input');
    const submitBtn = document.querySelector('.comment-submit');
    
    // Массив тегов
    let tags = [];
    
    // Инициализация
    initForm();
    
    function initForm() {
        // Обработчик отправки формы
        // Временно заменил
        //postForm.addEventListener('submit', handleFormSubmit);
        postForm.addEventListener('submit', mockFormSubmit);
        
        // Обработчик добавления тегов
        tagInput.addEventListener('keydown', handleTagInput);
        
        // Загрузка категорий (если нужно)
        // loadCategories();
    }
    
    // Обработка отправки формы
    async function handleFormSubmit(e) {
        e.preventDefault();
        
        // Валидация формы
        if (!validateForm()) {
            return;
        }
        
        // Подготовка данных
        const postData = {
            categoryId: categorySelect.value,
            title: titleInput.value.trim(),
            description: descriptionInput.value.trim(),
            content: contentInput.value.trim(),
            tags: tags
        };
        
        // Отправка данных
        try {
            // Блокируем кнопку отправки
            submitBtn.disabled = true;
            submitBtn.textContent = 'Отправка...';
            
            // Имитация запроса к серверу (в реальном коде заменить на реальный URL)
            const response = await fetch('https://api.example.com/posts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(postData)
            });
            
            if (!response.ok) {
                throw new Error('Ошибка при создании поста');
            }
            
            const result = await response.json();
            
            // Успешное создание поста
            showSuccessMessage(result.message || 'Пост успешно создан!');
            
            // Очищаем форму
            resetForm();
            
        } catch (error) {
            console.error('Ошибка:', error);
            showErrorMessage(error.message || 'Произошла ошибка при создании поста');
        } finally {
            // Разблокируем кнопку
            submitBtn.disabled = false;
            submitBtn.textContent = 'Опубликовать';
        }
    }
    
    // Валидация формы
    function validateForm() {
        // Проверка категории
        if (!categorySelect.value) {
            showFieldError(categorySelect, 'Выберите категорию');
            return false;
        }
        
        // Проверка заголовка
        if (titleInput.value.trim().length < 5) {
            showFieldError(titleInput, 'Заголовок должен содержать минимум 5 символов');
            return false;
        }
        
        // Проверка содержания
        if (contentInput.value.trim().length < 20) {
            showFieldError(contentInput, 'Содержание должно быть не менее 20 символов');
            return false;
        }
        
        return true;
    }
    
    // Обработка ввода тегов
    function handleTagInput(e) {
        if (e.key === 'Enter' || e.key === ',') {
            e.preventDefault();
            const tagText = tagInput.value.trim();
            
            if (tagText && !tags.includes(tagText)) {
                // Добавляем тег
                tags.push(tagText);
                renderTags();
                
                // Очищаем поле ввода
                tagInput.value = '';
            }
        }
    }
    
    // Отображение тегов
    function renderTags() {
        tagsContainer.innerHTML = '';
        
        tags.forEach((tag, index) => {
            const tagElement = document.createElement('div');
            tagElement.className = 'post-tag';
            tagElement.innerHTML = `
                ${tag}
                <span class="tag-remove" data-index="${index}">×</span>
            `;
            tagsContainer.appendChild(tagElement);
        });
        
        // Добавляем обработчики удаления тегов
        document.querySelectorAll('.tag-remove').forEach(btn => {
            btn.addEventListener('click', function() {
                const index = parseInt(this.getAttribute('data-index'));
                tags.splice(index, 1);
                renderTags();
            });
        });
    }
    
    // Показать ошибку для поля
    function showFieldError(field, message) {
        // Удаляем предыдущие ошибки
        hideFieldError(field);
        
        // Создаем элемент ошибки
        const errorElement = document.createElement('div');
        errorElement.className = 'field-error';
        errorElement.textContent = message;
        errorElement.style.color = 'red';
        errorElement.style.fontSize = '12px';
        errorElement.style.marginTop = '5px';
        
        // Вставляем после поля
        field.parentNode.insertBefore(errorElement, field.nextSibling);
        
        // Добавляем стиль ошибки к полю
        field.style.borderColor = 'red';
    }
    
    // Скрыть ошибку поля
    function hideFieldError(field) {
        const errorElement = field.parentNode.querySelector('.field-error');
        if (errorElement) {
            errorElement.remove();
        }
        field.style.borderColor = '';
    }
    
    // Показать сообщение об успехе
    function showSuccessMessage(message) {
        // Удаляем предыдущие сообщения
        const existingMessage = document.querySelector('.form-message');
        if (existingMessage) existingMessage.remove();
        
        // Создаем элемент сообщения
        const messageElement = document.createElement('div');
        messageElement.className = 'form-message';
        messageElement.textContent = message;
        messageElement.style.color = 'green';
        messageElement.style.margin = '15px 0';
        messageElement.style.textAlign = 'center';
        
        // Вставляем перед формой
        postForm.parentNode.insertBefore(messageElement, postForm.nextSibling);
    }
    
    // Показать сообщение об ошибке
    function showErrorMessage(message) {
        // Удаляем предыдущие сообщения
        const existingMessage = document.querySelector('.form-message');
        if (existingMessage) existingMessage.remove();
        
        // Создаем элемент сообщения
        const messageElement = document.createElement('div');
        messageElement.className = 'form-message';
        messageElement.textContent = message;
        messageElement.style.color = 'red';
        messageElement.style.margin = '15px 0';
        messageElement.style.textAlign = 'center';
        
        // Вставляем перед формой
        postForm.parentNode.insertBefore(messageElement, postForm.nextSibling);
    }
    
    // Сброс формы
    function resetForm() {
        postForm.reset();
        tags = [];
        renderTags();
    }
    
    // Демонстрационная функция для тестирования (удалить в реальном приложении)
    function mockFormSubmit() {
        console.log('Форма отправлена с данными:', {
            categoryId: categorySelect.value,
            title: titleInput.value,
            description: descriptionInput.value,
            content: contentInput.value,
            tags: tags
        });
        
        // Имитация успешной отправки
        setTimeout(() => {
            showSuccessMessage('Пост успешно создан! (демо)');
            resetForm();
        }, 1000);
    }
});