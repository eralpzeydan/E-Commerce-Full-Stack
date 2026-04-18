import { useProducts } from "@/features/products/hooks/use-products";
import { type ProductsQueryParams } from "@/features/products/types/product-types";

export function useProductsQuery(params: ProductsQueryParams) {
  return useProducts(params);
}
