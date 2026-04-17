import { useMutation } from "@tanstack/react-query";

import { login, register } from "@/features/auth/api/auth-api";
import type { LoginInput, RegisterInput } from "@/features/auth/types/auth.types";
import { setAuthToken } from "@/shared/utils/auth-token";

export function useLoginMutation() {
  return useMutation({
    mutationFn: (input: LoginInput) => login(input),
    onSuccess: (data) => {
      setAuthToken(data.token);
    },
  });
}

export function useRegisterMutation() {
  return useMutation({
    mutationFn: (input: RegisterInput) => register(input),
    onSuccess: (data) => {
      setAuthToken(data.token);
    },
  });
}
