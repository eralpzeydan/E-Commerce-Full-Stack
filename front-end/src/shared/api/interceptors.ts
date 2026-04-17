import type { AxiosInstance } from "axios";

import { clearAccessToken, getAccessToken } from "@/shared/utils/auth-token";

let interceptorsConfigured = false;

export function setupInterceptors(client: AxiosInstance): void {
  if (interceptorsConfigured) {
    return;
  }

  client.interceptors.request.use((config) => {
    const token = getAccessToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  });

  client.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        clearAccessToken();
      }

      return Promise.reject(error);
    },
  );

  interceptorsConfigured = true;
}
