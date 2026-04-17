"use client";

import { useState } from "react";

import { useProductsQuery } from "@/features/products/hooks/use-products-query";
import { type ProductsFilter, type Product } from "@/features/products/types/product.types";
import { EmptyState } from "@/shared/components/feedback/empty-state";
import { ErrorState } from "@/shared/components/feedback/error-state";
import { Spinner } from "@/shared/components/feedback/spinner";
import { Badge } from "@/shared/components/ui/badge";
import { ProductsFilterPanel } from "./products-filter-panel";
import { ProductsGrid } from "./products-grid";

const defaultFilters: ProductsFilter = {
  name: "",
  sortDir: "asc",
  inStockOnly: false,
};

function applyClientFilters(products: Product[], filters: ProductsFilter): Product[] {
  if (!filters.inStockOnly) {
    return products;
  }

  return products.filter((product) => product.stock > 0);
}

export function ProductsListScreen() {
  const [draftFilters, setDraftFilters] = useState<ProductsFilter>(defaultFilters);
  const [appliedFilters, setAppliedFilters] = useState<ProductsFilter>(defaultFilters);

  const { data, isPending, isError, refetch } = useProductsQuery({
    name: appliedFilters.name,
    sortDir: appliedFilters.sortDir,
  });

  const filteredProducts = data ? applyClientFilters(data.content, appliedFilters) : [];

  const handleApply = () => {
    setAppliedFilters(draftFilters);
  };

  const handleReset = () => {
    setDraftFilters(defaultFilters);
    setAppliedFilters(defaultFilters);
  };

  return (
    <div className="grid gap-6 lg:grid-cols-[280px_minmax(0,1fr)]">
      <ProductsFilterPanel
        value={draftFilters}
        onChange={setDraftFilters}
        onApply={handleApply}
        onReset={handleReset}
      />

      <section className="space-y-4">
        <div className="flex flex-wrap items-center gap-2">
          <Badge>{appliedFilters.sortDir === "asc" ? "Oldest first" : "Newest first"}</Badge>
          {appliedFilters.inStockOnly ? <Badge variant="success">In-stock only</Badge> : null}
          {appliedFilters.name ? <Badge variant="primary">Search: {appliedFilters.name}</Badge> : null}
        </div>

        {isPending ? (
          <div className="flex justify-center py-16" data-testid="products-loading">
            <Spinner />
          </div>
        ) : null}

        {isError ? (
          <ErrorState
            title="Unable to load products"
            message="The products service returned an error."
            onRetry={() => void refetch()}
          />
        ) : null}

        {!isPending && !isError && filteredProducts.length === 0 ? (
          <EmptyState
            title="No products found"
            description="Adjust filters or add products from admin panel."
          />
        ) : null}

        {!isPending && !isError && filteredProducts.length > 0 ? (
          <>
            <p className="text-sm text-[var(--color-text-muted)]" data-testid="products-total">
              Showing {filteredProducts.length} product(s)
            </p>
            <ProductsGrid products={filteredProducts} />
          </>
        ) : null}
      </section>
    </div>
  );
}
