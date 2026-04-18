import { z } from "zod";

import {
  type Product,
  type ProductsQueryParams,
  productSchema,
} from "@/features/products/types/product-types";
import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";
import { type PagedResponse } from "@/shared/types/paged-response";

const pagedProductsSchema = z.object({
  content: z.array(productSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
  first: z.boolean(),
  last: z.boolean(),
  sortBy: z.string(),
  sortDir: z.string(),
});

export async function listProducts(
  params: ProductsQueryParams,
): Promise<PagedResponse<Product>> {
  const data = await apiRequest<PagedResponse<Product>>({
    method: "GET",
    url: API_ENDPOINTS.PRODUCTS,
    params: {
      ...params,
      name: params.name?.trim() || undefined,
    },
  });

  return pagedProductsSchema.parse(data);
}

export async function getProductById(id: string): Promise<Product> {
  const data = await apiRequest<Product>({
    method: "GET",
    url: `${API_ENDPOINTS.PRODUCTS}/${id}`,
  });

  return productSchema.parse(data);
}
