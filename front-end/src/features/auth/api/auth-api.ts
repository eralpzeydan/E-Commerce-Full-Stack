import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";
import {
  authResponseSchema,
  type AuthResponse,
  type LoginInput,
  type RegisterInput,
} from "@/features/auth/types/auth.types";

export async function login(input: LoginInput): Promise<AuthResponse> {
  const data = await apiRequest<AuthResponse>({
    method: "POST",
    url: API_ENDPOINTS.AUTH.LOGIN,
    data: input,
  });

  return authResponseSchema.parse(data);
}

export async function register(input: RegisterInput): Promise<AuthResponse> {
  const data = await apiRequest<AuthResponse>({
    method: "POST",
    url: API_ENDPOINTS.AUTH.REGISTER,
    data: input,
  });

  return authResponseSchema.parse(data);
}
