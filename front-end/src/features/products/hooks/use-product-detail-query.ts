import { useQuery } from "@tanstack/react-query";

import { getProductById } from "@/features/products/api/products-api";
import { QUERY_KEYS } from "@/shared/constants/query-keys";

export function useProductDetailQuery(id: string) {
  return useQuery({
    queryKey: QUERY_KEYS.PRODUCT_DETAIL(id),
    queryFn: () => getProductById(id),
    enabled: Boolean(id),
  });
}
