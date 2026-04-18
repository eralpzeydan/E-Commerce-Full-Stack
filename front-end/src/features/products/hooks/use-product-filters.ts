"use client";

import { useMemo } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";

import {
  type ProductSortOption,
  type ProductsFilterState,
  productSortOptionSchema,
} from "@/features/products/types/product-types";

const DEFAULT_SIZE = 12;
const DEFAULT_SORT: ProductSortOption = "newest";

function parsePage(value: string | null): number {
  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed >= 0 ? parsed : 0;
}

function parseSize(value: string | null): number {
  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed > 0 && parsed <= 100 ? parsed : DEFAULT_SIZE;
}

function parseSort(value: string | null): ProductSortOption {
  const result = productSortOptionSchema.safeParse(value);
  return result.success ? result.data : DEFAULT_SORT;
}

function parseCategoryId(value: string | null): number | undefined {
  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed > 0 ? parsed : undefined;
}

export function useProductFilters() {
  const pathname = usePathname();
  const router = useRouter();
  const searchParams = useSearchParams();

  const filters = useMemo<ProductsFilterState>(() => {
    return {
      page: parsePage(searchParams.get("page")),
      size: parseSize(searchParams.get("size")),
      sort: parseSort(searchParams.get("sort")),
      name: searchParams.get("name") ?? "",
      categoryId: parseCategoryId(searchParams.get("categoryId")),
    };
  }, [searchParams]);

  const replaceParams = (next: ProductsFilterState) => {
    const params = new URLSearchParams();

    if (next.page > 0) {
      params.set("page", String(next.page));
    }

    if (next.size !== DEFAULT_SIZE) {
      params.set("size", String(next.size));
    }

    if (next.sort !== DEFAULT_SORT) {
      params.set("sort", next.sort);
    }

    const normalizedName = next.name.trim();
    if (normalizedName) {
      params.set("name", normalizedName);
    }

    if (next.categoryId) {
      params.set("categoryId", String(next.categoryId));
    }

    const query = params.toString();
    router.replace(query ? `${pathname}?${query}` : pathname, { scroll: false });
  };

  const applyFilters = (next: Pick<ProductsFilterState, "name" | "categoryId" | "sort">) => {
    replaceParams({
      ...filters,
      ...next,
      page: 0,
    });
  };

  const goToPage = (page: number) => {
    replaceParams({
      ...filters,
      page,
    });
  };

  const resetFilters = () => {
    replaceParams({
      page: 0,
      size: DEFAULT_SIZE,
      sort: DEFAULT_SORT,
      name: "",
      categoryId: undefined,
    });
  };

  return {
    filters,
    applyFilters,
    goToPage,
    resetFilters,
  };
}
