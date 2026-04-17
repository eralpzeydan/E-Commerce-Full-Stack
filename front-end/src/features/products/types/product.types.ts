import { z } from "zod";

export const productSchema = z.object({
  id: z.number(),
  name: z.string(),
  description: z.string(),
  price: z.number(),
  stock: z.number(),
  categoryId: z.number().nullable().optional(),
  categoryName: z.string().nullable().optional(),
});

export const productsFilterSchema = z.object({
  name: z.string().optional(),
  sortDir: z.enum(["asc", "desc"]).default("asc"),
  inStockOnly: z.boolean().default(false),
});

export type Product = z.infer<typeof productSchema>;
export type ProductsFilter = z.infer<typeof productsFilterSchema>;

export type ProductsServerFilter = Pick<ProductsFilter, "name" | "sortDir">;

export interface ListProductsParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: "asc" | "desc";
  name?: string;
  categoryId?: number;
}
