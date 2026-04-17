import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";
import type { CheckoutPayload } from "@/features/checkout/types/checkout.types";

export async function checkout(payload: CheckoutPayload) {
  return apiRequest({
    method: "POST",
    url: API_ENDPOINTS.CHECKOUT,
    data: payload,
    headers: {
      "Idempotency-Key": crypto.randomUUID(),
    },
  });
}
