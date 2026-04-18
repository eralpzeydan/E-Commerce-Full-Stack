import { z } from "zod";

export const productSchema = z.object({
  id: z.number(),
  name: z.string(),
  description: z.string().nullable().optional(),
  price: z.number(),
  stock: z.number(),
  categoryId: z.number().nullable().optional(),
  categoryName: z.string().nullable().optional(),
});

export const categorySchema = z.object({
  id: z.number(),
  name: z.string(),
  description: z.string().nullable().optional(),
});

export const productSortOptionSchema = z.enum([
  "newest",
  "name-asc",
  "price-asc",
  "price-desc",
]);

export const productsFilterStateSchema = z.object({
  page: z.number().int().min(0).default(0),
  size: z.number().int().min(1).max(100).default(12),
  sort: productSortOptionSchema.default("newest"),
  name: z.string().default(""),
  categoryId: z.number().optional(),
});

export type Product = z.infer<typeof productSchema>;
export type Category = z.infer<typeof categorySchema>;
export type ProductSortOption = z.infer<typeof productSortOptionSchema>;
export type ProductsFilterState = z.infer<typeof productsFilterStateSchema>;

export interface ProductsQueryParams {
  page?: number;
  size?: number;
  sortBy?: "id" | "name" | "price" | "stock";
  sortDir?: "asc" | "desc";
  name?: string;
  categoryId?: number;
}
