/**
 * FinTrack API - Maps all backend controller endpoints
 * Base URL: /api
 * All API calls use fetch with credentials: 'include' for session cookies
 */

const BASE = '/api';

const opts = (method, body) => ({
  method,
  headers: body ? { 'Content-Type': 'application/json' } : {},
  body: body ? JSON.stringify(body) : undefined,
  credentials: 'include'
});

const handleResponse = async (res) => {
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error || err.message || `Request failed: ${res.status}`);
  }
  const contentType = res.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) return res.json();
  return res.text();
};

// ========== AUTH (AuthController) ==========
const AuthAPI = {
  register: (data) => fetch(`${BASE}/auth/reg`, opts('POST', data)).then(handleResponse),
  login: (data) => fetch(`${BASE}/auth/login`, opts('POST', data)).then(handleResponse),
  logout: () => fetch(`${BASE}/auth/logout`, opts('POST')).then(handleResponse),
  getCurrentUser: () => fetch(`${BASE}/auth/me`, opts('GET')).then(handleResponse)
};

// ========== USER (UserController) ==========
const UserAPI = {
  getUserById: (id) => fetch(`${BASE}/user/${id}`, opts('GET')).then(handleResponse),
  updateIncome: (id, salary) => fetch(`${BASE}/user/income/${id}?salary=${encodeURIComponent(salary)}`, opts('PUT')).then(handleResponse),
  updateUser: (id, data) => fetch(`${BASE}/user/${id}`, opts('PUT', data)).then(handleResponse),
  getUserExpenses: (id) => fetch(`${BASE}/user/expenses/${id}`, opts('GET')).then(handleResponse)
};

// ========== EXPENSE (ExpenseController) ==========
const ExpenseAPI = {
  create: (data) => fetch(`${BASE}/expense/create`, opts('POST', data)).then(handleResponse),
  getAll: () => fetch(`${BASE}/expense`, opts('GET')).then(handleResponse),
  getById: (id) => fetch(`${BASE}/expense/${id}`, opts('GET')).then(handleResponse),
  delete: (id) => fetch(`${BASE}/expense/${id}`, opts('DELETE')).then(handleResponse),
  getByCategory: (name) => fetch(`${BASE}/expense/category?name=${encodeURIComponent(name)}`, opts('GET')).then(handleResponse),
  getByDateRange: (start, end) => {
    const params = new URLSearchParams({ start });
    if (end) params.set('end', end);
    return fetch(`${BASE}/expense/date-range?${params}`, opts('GET')).then(handleResponse);
  },
  getByDate: (date) => fetch(`${BASE}/expense/date?date=${date}`, opts('GET')).then(handleResponse),
  getRecentExpenses: (page = 0, size = 5) => fetch(`${BASE}/expense/recent-expenses?page=${page}&size=${size}`, opts('GET')).then(handleResponse),
  getMonthlySummary: () => fetch(`${BASE}/expense/monthly-summary`, opts('GET')).then(handleResponse),
  searchByCategoryTerm: (term) => fetch(`${BASE}/expense/category-term-search?term=${encodeURIComponent(term)}`, opts('GET')).then(handleResponse),
  getByMonth: (year, month) => {
    const params = new URLSearchParams();
    if (year != null) params.set('year', year);
    if (month != null) params.set('month', month);
    return fetch(`${BASE}/expense/month?${params}`, opts('GET')).then(handleResponse);
  }
};

// ========== CATEGORIES (CategoryController) ==========
const CategoryAPI = {
  getAll: () => fetch(`${BASE}/categories`, opts('GET')).then(handleResponse),
  getById: (id) => fetch(`${BASE}/categories/${id}`, opts('GET')).then(handleResponse),
  create: (name) => fetch(`${BASE}/categories`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(name.trim()),
    credentials: 'include'
  }).then(handleResponse),
  search: (keyword) => fetch(`${BASE}/categories/search?keyword=${encodeURIComponent(keyword)}`, opts('GET')).then(handleResponse)
};

// ========== AI (AIController) ==========
const AIAPI = {
  getFinancialNarrative: (year, month) => {
    const params = new URLSearchParams();
    if (year != null) params.set('year', year);
    if (month != null) params.set('month', month);
    return fetch(`${BASE}/ai/financial-narrative?${params}`, opts('GET')).then(handleResponse);
  },
  getRiskDetection: (year, month) => {
    const params = new URLSearchParams();
    if (year != null) params.set('year', year);
    if (month != null) params.set('month', month);
    return fetch(`${BASE}/ai/risk-detection?${params}`, opts('GET')).then(handleResponse);
  },
  getBudgetPlanner: (year, month) => {
    const params = new URLSearchParams();
    if (year != null) params.set('year', year);
    if (month != null) params.set('month', month);
    return fetch(`${BASE}/ai/budget-planner?${params}`, opts('GET')).then(handleResponse);
  },
  getHealthScore: (year, month) => {
    const params = new URLSearchParams();
    if (year != null) params.set('year', year);
    if (month != null) params.set('month', month);
    return fetch(`${BASE}/ai/health-score?${params}`, opts('GET')).then(handleResponse);
  }
};
