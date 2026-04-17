import { useQuery } from "@tanstack/react-query";

import { checkOrdersAccess } from "@/features/orders/api/orders-api";

export function useOrdersAccessQuery() {
  return useQuery({
    queryKey: ["orders", "access-check"],
    queryFn: checkOrdersAccess,
  });
}
