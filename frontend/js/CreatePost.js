import ApiClient from './ApiClient.js';
document.addEventListener('DOMContentLoaded', function() {
    // Элементы формы
    const postForm = document.querySelector('.create-post-form');
    const categorySelect = document.getElementById('category');
    const titleInput = document.getElementById('title');
    const descriptionInput = document.getElementById('description');
    const contentInput = document.getElementById('content');
    const tagsContainer = document.getElementById('tags-container');
    const tagInput = document.getElementById('tag-input');
    const submitBtn = document.querySelector('.btn-submit');
    
    // Массив тегов
    let tags = [];
    
    // Инициализация
    initForm();
    
    function initForm() {
        // Обработчик отправки формы

        // Временно заменил
        postForm.addEventListener('submit', handleFormSubmit);
        //postForm.addEventListener('submit', mockFormSubmit);
        
        // Обработчик добавления тегов
        //tagInput.addEventListener('keydown', handleTagInput);
        
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
            title: titleInput.value.trim(),
            content: contentInput.value.trim(),
            // временно !!!
            // categoryId: [categorySelect.value]
            categoriesId: [1]
        };
        
        // Отправка данных
        try {
            // Блокируем кнопку отправки
            submitBtn.disabled = true;
            submitBtn.textContent = 'Отправка...';
            
            // Имитация запроса к серверу (в реальном коде заменить на реальный URL)
            // const response = await fetch('https://api.example.com/posts', {
            //     method: 'POST',
            //     headers: {
            //         'Content-Type': 'application/json',
            //     },
            //     body: JSON.stringify(postData)
            // });
            
            // if (!response.ok) {
            //     throw new Error('Ошибка при создании поста');
            // }
            
            // const result = await response.json();

            const result = await ApiClient.createPost(postData);
            
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
    }
    
});