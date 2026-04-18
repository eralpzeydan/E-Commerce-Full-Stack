import { type ChangeEvent } from "react";

import { type ProductSortOption } from "@/features/products/types/product-types";

interface ProductsToolbarProps {
  totalElements: number;
  sort: ProductSortOption;
  onSortChange: (sort: ProductSortOption) => void;
  isFetching?: boolean;
}

export function ProductsToolbar({
  totalElements,
  sort,
  onSortChange,
  isFetching = false,
}: ProductsToolbarProps) {
  const handleSortChange = (event: ChangeEvent<HTMLSelectElement>) => {
    onSortChange(event.target.value as ProductSortOption);
  };

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 rounded-[var(--radius-lg)] border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3">
      <div>
        <p className="text-sm font-semibold text-[var(--color-text)]">{totalElements} products found</p>
        <p className="text-xs text-[var(--color-text-muted)]">{isFetching ? "Updating listing..." : ""}</p>
      </div>

      <label className="flex items-center gap-2 text-sm text-[var(--color-text-muted)]">
        Sort
        <select
          className="h-10 min-w-48 rounded-[var(--radius-md)] border border-[var(--color-border)] bg-[var(--color-surface)] px-3 text-sm text-[var(--color-text)]"
          value={sort}
          onChange={handleSortChange}
          data-testid="products-sort-select"
        >
          <option value="newest">Newest</option>
          <option value="name-asc">Name A-Z</option>
          <option value="price-asc">Price low to high</option>
          <option value="price-desc">Price high to low</option>
        </select>
      </label>
    </div>
  );
}
