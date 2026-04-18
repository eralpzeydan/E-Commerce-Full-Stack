export const QUERY_KEYS = {
  PRODUCTS: ["products"] as const,
  PRODUCT_DETAIL: (id: string) => ["products", id] as const,
  CATEGORIES: ["categories"] as const,
} as const;
