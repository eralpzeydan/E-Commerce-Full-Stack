import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";
import { cartResponseSchema, type CartResponse } from "@/features/cart/types/cart.types";

export async function getCart(): Promise<CartResponse> {
  const data = await apiRequest<CartResponse>({
    method: "GET",
    url: API_ENDPOINTS.CART,
  });

  return cartResponseSchema.parse(data);
}
