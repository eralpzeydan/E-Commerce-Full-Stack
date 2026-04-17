import { useMutation } from "@tanstack/react-query";

import { checkout } from "@/features/checkout/api/checkout-api";
import type { CheckoutPayload } from "@/features/checkout/types/checkout.types";

export function useCheckoutMutation() {
  return useMutation({
    mutationFn: (payload: CheckoutPayload) => checkout(payload),
  });
}
