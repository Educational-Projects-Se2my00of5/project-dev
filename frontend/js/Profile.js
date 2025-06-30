import ApiClient from './ApiClient.js';
document.addEventListener('DOMContentLoaded', function() {

            // Элементы профиля
            const nicknameDisplay = document.getElementById('nickname-display');
            const editNicknameBtn = document.getElementById('edit-nickname-btn');
            const nicknameEditForm = document.getElementById('nickname-edit-form');
            const nicknameInput = document.getElementById('nickname-input');
            const saveNicknameBtn = document.getElementById('save-nickname-btn');
            const cancelEditBtn = document.getElementById('cancel-edit-btn');
            const userAvatar = document.getElementById('user-avatar');

            let profileEmail = document.querySelector('.profile-email')
            let profileRole = document.querySelector('.profile-role')
            
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
                        const response = await fetch('http://localhost:12345/api/me', {
                            method: 'PUT',
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
            async function loadProfileData() {

                try {
                    // Показываем состояние загрузки
                    //showLoadingState();
                    
                    // Имитация запроса к серверу (в реальном коде заменить на реальный URL)
                    // const response = await fetch('http://localhost:12345/api/me');
                    
                    // if (!response.ok) {
                    //     throw new Error('Ошибка загрузки категорий');
                    // }
                    const data = await ApiClient.getUserProfile();
                    
                    // const data = await response.json();
                    console.log(data)
                    
                    // Обновляем данные на странице
                    nicknameDisplay.textContent = data.username;
                    nicknameInput.value = data.username;
                    userAvatar.textContent = data.username.charAt(0).toUpperCase();

                    profileEmail.textContent = data.email;
                    profileRole.textContent = data.roles[0].name;

                } catch (error) {
                    console.error('Ошибка загрузки категорий:', error);
                }
                
                // Можно добавить обновление других полей
            }
            
            // Инициализация
            loadProfileData();
        });