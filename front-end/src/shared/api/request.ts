import { httpClient } from "@/shared/api/http-client";
import { normalizeApiError } from "@/shared/api/api-error";
import { type ApiRequestConfig } from "@/shared/types/api-types";

export async function apiRequest<TResponse>(config: ApiRequestConfig): Promise<TResponse> {
  try {
    const response = await httpClient.request<TResponse>(config);
    return response.data;
  } catch (error) {
    throw normalizeApiError(error);
  }
}
