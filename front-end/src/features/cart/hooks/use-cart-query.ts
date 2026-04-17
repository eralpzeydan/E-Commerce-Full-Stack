import { useQuery } from "@tanstack/react-query";

import { getCart } from "@/features/cart/api/cart-api";

export function useCartQuery() {
  return useQuery({
    queryKey: ["cart"],
    queryFn: getCart,
  });
}
