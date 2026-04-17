import { useQuery } from "@tanstack/react-query";

import { listProducts } from "@/features/products/api/products-api";
import { type ProductsServerFilter } from "@/features/products/types/product.types";
import { QUERY_KEYS } from "@/shared/constants/query-keys";

export function useProductsQuery(filters: ProductsServerFilter) {
  return useQuery({
    queryKey: [...QUERY_KEYS.PRODUCTS, filters.name ?? "", filters.sortDir],
    queryFn: () =>
      listProducts({
        page: 0,
        size: 12,
        sortBy: "id",
        sortDir: filters.sortDir,
        name: filters.name,
      }),
  });
}
