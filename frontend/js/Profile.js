document.addEventListener('DOMContentLoaded', function() {
            // Элементы профиля
            const nicknameDisplay = document.getElementById('nickname-display');
            const editNicknameBtn = document.getElementById('edit-nickname-btn');
            const nicknameEditForm = document.getElementById('nickname-edit-form');
            const nicknameInput = document.getElementById('nickname-input');
            const saveNicknameBtn = document.getElementById('save-nickname-btn');
            const cancelEditBtn = document.getElementById('cancel-edit-btn');
            const userAvatar = document.getElementById('user-avatar');
            
            // Обработчики событий
            editNicknameBtn.addEventListener('click', showEditForm);
            saveNicknameBtn.addEventListener('click', saveNickname);
            cancelEditBtn.addEventListener('click', cancelEdit);
            
            // Показать форму редактирования
            function showEditForm() {
                nicknameDisplay.style.display = 'none';
                editNicknameBtn.style.display = 'none';
                nicknameEditForm.style.display = 'block';
                nicknameInput.focus();
            }
            
            // Сохранить никнейм
            async function saveNickname() {
                const newNickname = nicknameInput.value.trim();
                
                if (newNickname && newNickname !== nicknameDisplay.textContent) {
                    try {
                        // Имитация запроса к серверу
                        const response = await fetch('https://api.example.com/profile/nickname', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({ nickname: newNickname })
                        });
                        
                        if (!response.ok) {
                            throw new Error('Ошибка при сохранении');
                        }
                        
                        // Обновляем отображение
                        nicknameDisplay.textContent = newNickname;
                        userAvatar.textContent = newNickname.charAt(0).toUpperCase();
                        
                        // Скрываем форму
                        hideEditForm();
                        
                    } catch (error) {
                        console.error('Ошибка:', error);
                        alert('Не удалось сохранить изменения');
                    }
                } else {
                    hideEditForm();
                }
            }
            
            // Отмена редактирования
            function cancelEdit() {
                nicknameInput.value = nicknameDisplay.textContent;
                hideEditForm();
            }
            
            // Скрыть форму редактирования
            function hideEditForm() {
                nicknameDisplay.style.display = 'inline-block';
                editNicknameBtn.style.display = 'inline-block';
                nicknameEditForm.style.display = 'none';
            }
            
            // Загрузка данных профиля (имитация)
            function loadProfileData() {
                // В реальном приложении здесь будет запрос к серверу
                const profileData = {
                    nickname: "User123",
                    email: "user@example.com",
                    role: "Пользователь",
                    postsCount: 24,
                    commentsCount: 128,
                    likesCount: "1.2k"
                };
                
                // Обновляем данные на странице
                nicknameDisplay.textContent = profileData.nickname;
                nicknameInput.value = profileData.nickname;
                userAvatar.textContent = profileData.nickname.charAt(0).toUpperCase();
                
                // Можно добавить обновление других полей
            }
            
            // Инициализация
            loadProfileData();
        });