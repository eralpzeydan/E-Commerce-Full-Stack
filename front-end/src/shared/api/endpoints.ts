export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: "/api/v1/auth/login",
    REGISTER: "/api/v1/auth/register",
    ME: "/api/v1/auth/me",
  },
  PRODUCTS: "/api/v1/products",
  CATEGORIES: "/api/v1/categories",
  CART: "/api/v1/cart",
  CHECKOUT: "/api/v1/orders/checkout",
  ORDERS: "/api/v1/orders",
  ADMIN: "/api/admin",
} as const;
