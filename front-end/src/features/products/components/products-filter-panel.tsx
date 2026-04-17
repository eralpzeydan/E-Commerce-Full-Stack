import { type ChangeEvent } from "react";

import { type ProductsFilter } from "@/features/products/types/product.types";
import { Button } from "@/shared/components/ui/button";
import { Input } from "@/shared/components/ui/input";

interface ProductsFilterPanelProps {
  value: ProductsFilter;
  onChange: (nextValue: ProductsFilter) => void;
  onApply: () => void;
  onReset: () => void;
}

export function ProductsFilterPanel({
  value,
  onChange,
  onApply,
  onReset,
}: ProductsFilterPanelProps) {
  const handleNameChange = (event: ChangeEvent<HTMLInputElement>) => {
    onChange({ ...value, name: event.target.value });
  };

  const handleSortChange = (event: ChangeEvent<HTMLSelectElement>) => {
    onChange({ ...value, sortDir: event.target.value as ProductsFilter["sortDir"] });
  };

  const handleInStockChange = (event: ChangeEvent<HTMLInputElement>) => {
    onChange({ ...value, inStockOnly: event.target.checked });
  };

  return (
    <aside
      className="rounded-[var(--radius-xl)] border border-[var(--color-border)] bg-[var(--color-surface)] p-4 shadow-[var(--shadow-card)]"
      data-testid="products-filter-panel"
    >
      <h2 className="text-base font-bold text-[var(--color-text)]">Filters</h2>
      <div className="mt-4 space-y-4">
        <label className="flex flex-col gap-2">
          <span className="text-xs font-semibold uppercase tracking-wide text-[var(--color-text-muted)]">
            Search
          </span>
          <Input
            value={value.name ?? ""}
            onChange={handleNameChange}
            placeholder="Product name"
            data-testid="products-filter-name"
          />
        </label>

        <label className="flex flex-col gap-2">
          <span className="text-xs font-semibold uppercase tracking-wide text-[var(--color-text-muted)]">
            Sort
          </span>
          <select
            className="h-10 rounded-[var(--radius-md)] border border-[var(--color-border)] bg-[var(--color-surface)] px-3 text-sm"
            value={value.sortDir}
            onChange={handleSortChange}
            data-testid="products-filter-sort"
          >
            <option value="asc">Oldest First</option>
            <option value="desc">Newest First</option>
          </select>
        </label>

        <label className="flex items-center gap-2 text-sm text-[var(--color-text-muted)]">
          <input
            type="checkbox"
            checked={value.inStockOnly}
            onChange={handleInStockChange}
            className="h-4 w-4 rounded border-[var(--color-border)]"
            data-testid="products-filter-instock"
          />
          In-stock only
        </label>
      </div>

      <div className="mt-5 grid grid-cols-2 gap-2">
        <Button onClick={onApply} data-testid="products-filter-apply">
          Apply
        </Button>
        <Button onClick={onReset} variant="secondary" data-testid="products-filter-reset">
          Reset
        </Button>
      </div>
    </aside>
  );
}
