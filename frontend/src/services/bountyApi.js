import { apiClient, clearStoredSession, publicClient, saveStoredSession } from "./apiClient.js";

export const authApi = {
  async login(payload) {
    const { data } = await publicClient.post("/auth/login", payload);
    saveStoredSession(data);
    return data;
  },

  async register(payload) {
    const { data } = await publicClient.post("/auth/register", payload);
    saveStoredSession(data);
    return data;
  },

  async logout(refreshToken) {
    if (refreshToken) {
      await publicClient.post("/auth/logout", { refreshToken });
    }

    clearStoredSession();
  },

  async me() {
    const { data } = await apiClient.get("/auth/me");
    return data;
  },
};

export const dictionaryApi = {
  async all() {
    const [factions, sectors, planets, categories, currencies, skills] =
      await Promise.all([
        apiClient.get("/dictionary/factions"),
        apiClient.get("/dictionary/sectors"),
        apiClient.get("/dictionary/planets"),
        apiClient.get("/dictionary/order-categories"),
        apiClient.get("/dictionary/currencies"),
        apiClient.get("/dictionary/skills"),
      ]);

    return {
      factions: factions.data,
      sectors: sectors.data,
      planets: planets.data,
      categories: categories.data,
      currencies: currencies.data,
      skills: skills.data,
    };
  },

  async createFaction(payload) {
    const { data } = await apiClient.post("/dictionary/factions", payload);
    return data;
  },

  async createSector(payload) {
    const { data } = await apiClient.post("/dictionary/sectors", payload);
    return data;
  },

  async createPlanet(payload) {
    const { data } = await apiClient.post("/dictionary/planets", payload);
    return data;
  },

  async createCategory(payload) {
    const { data } = await apiClient.post("/dictionary/order-categories", payload);
    return data;
  },

  async createCurrency(payload) {
    const { data } = await apiClient.post("/dictionary/currencies", payload);
    return data;
  },

  async createSkill(payload) {
    const { data } = await apiClient.post("/dictionary/skills", payload);
    return data;
  },
};

export const adminApi = {
  async users() {
    const { data } = await apiClient.get("/users");
    return data;
  },

  async roles() {
    const { data } = await apiClient.get("/roles");
    return data;
  },

  async createUser(payload) {
    const { data } = await apiClient.post("/users", payload);
    return data;
  },

  async updateUserStatus(userId, status) {
    const { data } = await apiClient.patch(`/users/${userId}/status`, { status });
    return data;
  },

  async assignRole(userId, roleName) {
    const { data } = await apiClient.post(`/users/${userId}/roles`, { roleName });
    return data;
  },

  async removeRole(userId, roleName) {
    const { data } = await apiClient.delete(`/users/${userId}/roles/${roleName}`);
    return data;
  },

  async deleteUser(userId) {
    await apiClient.delete(`/users/${userId}`);
  },
};

export const moderationApi = {
  async users() {
    const { data } = await apiClient.get("/moderation/users");
    return data;
  },

  async updateUserStatus(userId, status) {
    const { data } = await apiClient.patch(`/moderation/users/${userId}/status`, { status });
    return data;
  },
};

export const ordersApi = {
  async publicBoard(params) {
    const { data } = await apiClient.get("/orders", { params });
    return data;
  },

  async publicOrder(orderId) {
    const { data } = await apiClient.get(`/orders/${orderId}`);
    return data;
  },

  async createDraft(payload) {
    const { data } = await apiClient.post("/orders", payload);
    return data;
  },

  async publish(orderId) {
    const { data } = await apiClient.post(`/orders/${orderId}/publish`);
    return data;
  },

  async cancel(orderId) {
    const { data } = await apiClient.post(`/orders/${orderId}/cancel`);
    return data;
  },

  async start(orderId) {
    const { data } = await apiClient.post(`/orders/${orderId}/start`);
    return data;
  },

  async submit(orderId) {
    const { data } = await apiClient.post(`/orders/${orderId}/submit`);
    return data;
  },

  async complete(orderId) {
    const { data } = await apiClient.post(`/orders/${orderId}/complete`);
    return data;
  },

  async myClient(params) {
    const { data } = await apiClient.get("/orders/my/client", { params });
    return data;
  },

  async myHunter(params) {
    const { data } = await apiClient.get("/orders/my/hunter", { params });
    return data;
  },
};

export const applicationsApi = {
  async apply(orderId, payload) {
    const { data } = await apiClient.post(`/orders/${orderId}/applications`, payload);
    return data;
  },

  async myHunter() {
    const { data } = await apiClient.get("/applications/my/hunter");
    return data;
  },

  async withdraw(applicationId) {
    const { data } = await apiClient.post(`/applications/${applicationId}/withdraw`);
    return data;
  },

  async forClientOrder(orderId) {
    const { data } = await apiClient.get(`/orders/my/client/${orderId}/applications`);
    return data;
  },

  async accept(applicationId) {
    const { data } = await apiClient.post(`/applications/${applicationId}/accept`);
    return data;
  },

  async reject(applicationId) {
    const { data } = await apiClient.post(`/applications/${applicationId}/reject`);
    return data;
  },
};

export const profilesApi = {
  async me() {
    const { data } = await apiClient.get("/profiles/me");
    return data;
  },

  async hunters() {
    const { data } = await apiClient.get("/profiles/hunters");
    return data;
  },

  async clients() {
    const { data } = await apiClient.get("/profiles/clients");
    return data;
  },

  async createClient(payload) {
    const { data } = await apiClient.post("/profiles/client", payload);
    return data;
  },

  async createHunter(payload) {
    const { data } = await apiClient.post("/profiles/hunter", payload);
    return data;
  },

  async addHunterSkill(payload) {
    const { data } = await apiClient.post("/profiles/hunter/skills", payload);
    return data;
  },

  async removeHunterSkill(skillId) {
    const { data } = await apiClient.delete(`/profiles/hunter/skills/${skillId}`);
    return data;
  },
};
