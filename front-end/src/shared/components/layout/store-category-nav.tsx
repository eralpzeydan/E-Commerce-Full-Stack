import Link from "next/link";

import { StoreContainer } from "@/shared/components/layout/store-container";

const categoryItems = [
  "Electronics",
  "Home",
  "Fashion",
  "Books",
  "Beauty",
  "Sports",
  "Toys",
];

export function StoreCategoryNav() {
  return (
    <div className="border-b border-[var(--color-border)] bg-[var(--color-surface-alt)]">
      <StoreContainer className="flex flex-wrap gap-2 py-2">
        {categoryItems.map((category) => (
          <Link
            key={category}
            href="/products"
            className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-1.5 text-xs font-semibold text-[var(--color-text-muted)] transition hover:border-[var(--color-primary)] hover:text-[var(--color-primary)]"
          >
            {category}
          </Link>
        ))}
      </StoreContainer>
    </div>
  );
}
