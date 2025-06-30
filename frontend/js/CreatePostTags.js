// Простой скрипт для работы с тегами
document.addEventListener('DOMContentLoaded', function() {
    const tagInput = document.getElementById('tag-input');
    const tagsContainer = document.getElementById('tags-container');
    const tagSuggestions = document.getElementById('tag-suggestions');

    // Показываем подсказки при фокусе
    tagInput.addEventListener('focus', function() {
        tagSuggestions.style.display = 'block';
    });

    // Скрываем подсказки при потере фокуса
    tagInput.addEventListener('blur', function() {
        setTimeout(() => {
            tagSuggestions.style.display = 'none';
        }, 200);
    });

    // Добавляем тег при нажатии Enter или выборе из подсказок
    tagInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && tagInput.value.trim()) {
            e.preventDefault();
            addTag(tagInput.value.trim());
            tagInput.value = '';
        }
    });

    // Обработка выбора подсказки
    document.querySelectorAll('.tag-suggestion').forEach(item => {
        item.addEventListener('mousedown', function(e) {
            e.preventDefault();
            addTag(this.textContent.trim());
            tagInput.value = '';
            tagInput.focus();
        });
    });

    // Функция добавления тега
    function addTag(tagText) {
        if (!tagText.startsWith('#')) {
            tagText = '#' + tagText;
        }
        
        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.innerHTML = `
            ${tagText}
            <span class="tag-remove">×</span>
        `;
        
        tag.querySelector('.tag-remove').addEventListener('click', function() {
            tag.remove();
        });
        
        tagsContainer.appendChild(tag);
    }
})


let cancel = document.querySelector('.btn-cancel')

cancel.addEventListener('click', function (){
    window.location.href = '../main.html'
    console.log(1)
})