import { z } from "zod";

export const authResponseSchema = z.object({
  userId: z.number(),
  name: z.string(),
  email: z.string().email(),
  token: z.string(),
  message: z.string().optional(),
});

export type AuthResponse = z.infer<typeof authResponseSchema>;

export interface LoginInput {
  email: string;
  password: string;
}

export interface RegisterInput {
  name: string;
  email: string;
  password: string;
}
