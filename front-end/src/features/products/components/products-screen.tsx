"use client";

import { useMemo } from "react";

import { ProductFilters } from "@/features/products/components/product-filters";
import { ProductPagination } from "@/features/products/components/product-pagination";
import { ProductsEmptyState } from "@/features/products/components/products-empty-state";
import { ProductsErrorState } from "@/features/products/components/products-error-state";
import { ProductsGrid } from "@/features/products/components/products-grid";
import { ProductsLoading } from "@/features/products/components/products-loading";
import { ProductsToolbar } from "@/features/products/components/products-toolbar";
import { useProductCategories } from "@/features/products/hooks/use-product-categories";
import { useProductFilters } from "@/features/products/hooks/use-product-filters";
import { useProducts } from "@/features/products/hooks/use-products";
import { buildProductsQueryParams } from "@/features/products/utils/product-query-params";
import { PageContainer } from "@/shared/components/layout/page-container";

export function ProductsScreen() {
  const { filters, applyFilters, goToPage, resetFilters } = useProductFilters();

  const queryParams = useMemo(() => buildProductsQueryParams(filters), [filters]);

  const {
    data: productsPage,
    isPending: isProductsPending,
    isError: isProductsError,
    isFetching: isProductsFetching,
    refetch,
  } = useProducts(queryParams);

  const { data: categories = [], isPending: isCategoriesPending } = useProductCategories();

  const handleSortChange = (sort: typeof filters.sort) => {
    applyFilters({
      name: filters.name,
      categoryId: filters.categoryId,
      sort,
    });
  };

  const filterKey = `filters-${filters.name}-${filters.categoryId ?? "all"}-${filters.sort}`;

  return (
    <PageContainer
      title="Shop Products"
      description="Browse products with backend-powered filtering, sorting, and pagination."
    >
      {isProductsPending && !productsPage ? (
        <ProductsLoading />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[280px_minmax(0,1fr)]">
          <ProductFilters
            key={filterKey}
            value={filters}
            categories={categories}
            isCategoriesPending={isCategoriesPending}
            onApply={applyFilters}
            onReset={resetFilters}
          />

          <section className="space-y-4">
            <ProductsToolbar
              totalElements={productsPage?.totalElements ?? 0}
              sort={filters.sort}
              onSortChange={handleSortChange}
              isFetching={isProductsFetching}
            />

            {isProductsError ? <ProductsErrorState onRetry={() => void refetch()} /> : null}

            {!isProductsError && (productsPage?.content.length ?? 0) === 0 ? (
              <ProductsEmptyState onClearFilters={resetFilters} />
            ) : null}

            {!isProductsError && (productsPage?.content.length ?? 0) > 0 ? (
              <>
                <ProductsGrid products={productsPage?.content ?? []} />
                <ProductPagination
                  page={productsPage?.page ?? 0}
                  totalPages={productsPage?.totalPages ?? 0}
                  onChange={goToPage}
                />
              </>
            ) : null}
          </section>
        </div>
      )}
    </PageContainer>
  );
}
