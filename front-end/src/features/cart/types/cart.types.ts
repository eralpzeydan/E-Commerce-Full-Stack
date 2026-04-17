import { z } from "zod";

export const cartItemSchema = z.object({
  cartItemId: z.number(),
  productId: z.number(),
  productName: z.string(),
  quantity: z.number(),
  unitPrice: z.number(),
  subtotal: z.number(),
});

export const cartResponseSchema = z.object({
  cartId: z.number(),
  userId: z.number(),
  items: z.array(cartItemSchema),
  total: z.number(),
});

export type CartItem = z.infer<typeof cartItemSchema>;
export type CartResponse = z.infer<typeof cartResponseSchema>;
