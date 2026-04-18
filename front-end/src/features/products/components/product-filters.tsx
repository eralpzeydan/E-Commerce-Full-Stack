"use client";

import { useState, type FormEvent } from "react";

import { type Category, type ProductsFilterState } from "@/features/products/types/product-types";
import { Button } from "@/shared/components/ui/button";
import { Input } from "@/shared/components/ui/input";

interface ProductFiltersProps {
  value: ProductsFilterState;
  categories: Category[];
  isCategoriesPending: boolean;
  onApply: (next: Pick<ProductsFilterState, "name" | "categoryId" | "sort">) => void;
  onReset: () => void;
}

export function ProductFilters({
  value,
  categories,
  isCategoriesPending,
  onApply,
  onReset,
}: ProductFiltersProps) {
  const [keyword, setKeyword] = useState(() => value.name);
  const [categoryId, setCategoryId] = useState<number | undefined>(() => value.categoryId);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onApply({
      name: keyword,
      categoryId,
      sort: value.sort,
    });
  };

  const handleReset = () => {
    setKeyword("");
    setCategoryId(undefined);
    onReset();
  };

  return (
    <aside
      className="rounded-[var(--radius-xl)] border border-[var(--color-border)] bg-[var(--color-surface)] p-4 shadow-[var(--shadow-card)]"
      data-testid="products-filter-panel"
    >
      <h2 className="text-base font-bold text-[var(--color-text)]">Filters</h2>
      <p className="mt-1 text-xs text-[var(--color-text-muted)]">
        This version uses backend-backed filters only.
      </p>

      <form className="mt-4 space-y-5" onSubmit={handleSubmit}>
        <label className="flex flex-col gap-2">
          <span className="text-xs font-semibold uppercase tracking-wide text-[var(--color-text-muted)]">
            Search
          </span>
          <Input
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="Search by product name"
            data-testid="products-filter-name"
          />
        </label>

        <fieldset className="space-y-2">
          <legend className="text-xs font-semibold uppercase tracking-wide text-[var(--color-text-muted)]">
            Category
          </legend>

          <label className="flex items-center gap-2 text-sm text-[var(--color-text)]">
            <input
              type="radio"
              checked={categoryId === undefined}
              onChange={() => setCategoryId(undefined)}
              name="category"
              className="h-4 w-4 border-[var(--color-border)]"
            />
            All categories
          </label>

          {isCategoriesPending ? (
            <p className="text-xs text-[var(--color-text-muted)]">Loading categories...</p>
          ) : (
            <div className="space-y-2">
              {categories.map((category) => (
                <label key={category.id} className="flex items-center gap-2 text-sm text-[var(--color-text)]">
                  <input
                    type="radio"
                    checked={categoryId === category.id}
                    onChange={() => setCategoryId(category.id)}
                    name="category"
                    className="h-4 w-4 border-[var(--color-border)]"
                  />
                  {category.name}
                </label>
              ))}
            </div>
          )}
        </fieldset>

        <div className="grid grid-cols-2 gap-2">
          <Button type="submit" data-testid="products-filter-apply">
            Apply
          </Button>
          <Button
            type="button"
            onClick={handleReset}
            variant="secondary"
            data-testid="products-filter-reset"
          >
            Reset
          </Button>
        </div>
      </form>
    </aside>
  );
}
