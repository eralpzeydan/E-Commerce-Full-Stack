import { keepPreviousData, useQuery } from "@tanstack/react-query";

import { listProducts } from "@/features/products/api/products-api";
import { type ProductsQueryParams } from "@/features/products/types/product-types";
import { QUERY_KEYS } from "@/shared/constants/query-keys";

export function useProducts(params: ProductsQueryParams) {
  return useQuery({
    queryKey: [...QUERY_KEYS.PRODUCTS, params],
    queryFn: () => listProducts(params),
    placeholderData: keepPreviousData,
  });
}
