import { z } from "zod";

import { type Category, categorySchema } from "@/features/products/types/product-types";
import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";

const categoryListSchema = z.array(categorySchema);

export async function listCategories(): Promise<Category[]> {
  const data = await apiRequest<Category[]>({
    method: "GET",
    url: API_ENDPOINTS.CATEGORIES,
  });

  return categoryListSchema.parse(data);
}
