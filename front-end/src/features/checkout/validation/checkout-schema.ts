import { z } from "zod";

export const checkoutSchema = z.object({
  addressLine1: z.string().min(5),
  city: z.string().min(2),
  zipCode: z.string().min(3),
});

export type CheckoutSchemaValues = z.infer<typeof checkoutSchema>;
