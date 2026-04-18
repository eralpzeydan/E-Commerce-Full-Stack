import {
  type ProductSortOption,
  type ProductsFilterState,
  type ProductsQueryParams,
} from "@/features/products/types/product-types";

interface SortConfig {
  sortBy: NonNullable<ProductsQueryParams["sortBy"]>;
  sortDir: NonNullable<ProductsQueryParams["sortDir"]>;
}

const SORT_CONFIG_MAP: Record<ProductSortOption, SortConfig> = {
  newest: { sortBy: "id", sortDir: "desc" },
  "name-asc": { sortBy: "name", sortDir: "asc" },
  "price-asc": { sortBy: "price", sortDir: "asc" },
  "price-desc": { sortBy: "price", sortDir: "desc" },
};

export function resolveSortConfig(sort: ProductSortOption): SortConfig {
  return SORT_CONFIG_MAP[sort];
}

export function buildProductsQueryParams(filters: ProductsFilterState): ProductsQueryParams {
  const sortConfig = resolveSortConfig(filters.sort);

  return {
    page: filters.page,
    size: filters.size,
    sortBy: sortConfig.sortBy,
    sortDir: sortConfig.sortDir,
    name: filters.name.trim() || undefined,
    categoryId: filters.categoryId,
  };
}
