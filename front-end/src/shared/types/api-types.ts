import { type AxiosRequestConfig } from "axios";

export type ApiRequestConfig = AxiosRequestConfig;

export interface ApiError {
  message: string;
  status?: number;
  code?: string;
  details?: unknown;
}
