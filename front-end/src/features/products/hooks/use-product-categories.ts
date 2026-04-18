import { useQuery } from "@tanstack/react-query";

import { listCategories } from "@/features/products/api/categories-api";
import { QUERY_KEYS } from "@/shared/constants/query-keys";

export function useProductCategories() {
  return useQuery({
    queryKey: QUERY_KEYS.CATEGORIES,
    queryFn: listCategories,
  });
}
