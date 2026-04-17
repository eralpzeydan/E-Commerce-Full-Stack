import axios from "axios";

import type { ApiError } from "@/shared/types/api-types";

export function normalizeApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error)) {
    const responseData = error.response?.data as { message?: string; error?: string } | undefined;

    return {
      message:
        responseData?.message ??
        responseData?.error ??
        error.message ??
        "Unexpected API error",
      status: error.response?.status,
      code: error.code,
      details: error.response?.data,
    };
  }

  return {
    message: "Unexpected error occurred",
  };
}
