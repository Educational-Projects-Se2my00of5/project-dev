class ApiClient {
  constructor() {
    this.baseUrl = 'http://localhost:12345';
    this.refreshInProgress = null;
  }

  async request(endpoint, options = {}) {
    // Добавляем токен к запросу
    const accessToken = localStorage.getItem('accessToken');
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
      ...(accessToken && { Authorization: `Bearer ${accessToken}` })
    };

    try {
      let response = await fetch(`${this.baseUrl}${endpoint}`, {
        ...options,
        headers
      });

      // Если токен истек, пробуем обновить
      if (response.status === 401 && !options._retry) {
        const newToken = await this.refreshTokens();
        headers.Authorization = `Bearer ${newToken}`;
        
        response = await fetch(`${this.baseUrl}${endpoint}`, {
          ...options,
          headers,
          _retry: true // Помечаем как повторный запрос
        });
      }

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      if (error.message.includes('401')) {
        // Принудительный выход при неудачном обновлении токена
        this.logout();
      }
      throw error;
    }
  }

  async refreshTokens() {
    // Если уже идет обновление, возвращаем существующий промис
    if (this.refreshInProgress) {
      return this.refreshInProgress;
    }

    try {
      this.refreshInProgress = new Promise(async (resolve, reject) => {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          this.logout();
          reject(new Error('No refresh token available'));
          return;
        }

        const response = await fetch(`${this.baseUrl}/api/new-token-pair`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ refreshToken: refreshToken })
        });

        if (!response.ok) {
          this.logout();
          reject(new Error('Failed to refresh tokens'));
          return;
        }

        const { accessToken, refreshToken: newRefreshToken } = await response.json();
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        resolve(accessToken);
      });

      return await this.refreshInProgress;
    } finally {
      this.refreshInProgress = null;
    }
  }


  logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '../auth.html';
  }

  async getUserProfile() {
    return this.request('/api/me');
  }

  // async getPosts() {
  //   return this.request('/api/posts?page=0&size=20');
  // }

  async createPost(data) {
    return this.request('/api/posts', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  async updatePost(data, id) {
    return this.request(`/api/posts/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async deletePost(id) {
    return this.request(`/api/posts/${id}`, {
      method: 'DELETE',
    });
  }

  async createCategory(data) {
    return this.request('/api/admin/categories', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  // Примеры конкретных методов API
//   async getPosts() {
//     return this.request('/posts');
//   }

//   async createPost(data) {
//     return this.request('/posts', {
//       method: 'POST',
//       body: JSON.stringify(data)
//     });
//   }
}

// Создаем единственный экземпляр
const apiClientInstance = new ApiClient();

// Экспортируем только инстанс, не класс
export default apiClientInstance;