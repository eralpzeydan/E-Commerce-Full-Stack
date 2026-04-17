import Link from "next/link";

import { StoreContainer } from "@/shared/components/layout/store-container";
import { Input } from "@/shared/components/ui/input";

export function StoreHeader() {
  return (
    <div className="border-b border-[var(--color-border)] bg-[var(--color-surface)]">
      <StoreContainer className="grid gap-3 py-4 md:grid-cols-[220px_1fr_auto] md:items-center">
        <Link href="/" className="text-xl font-extrabold tracking-tight text-[var(--color-text)]">
          E-Commerce Studio
        </Link>

        <Input placeholder="Search products, categories, brands" aria-label="Search store" />

        <div className="flex items-center gap-2">
          <Link
            href="/cart"
            className="rounded-[var(--radius-sm)] px-3 py-2 text-sm font-medium text-[var(--color-text-muted)] hover:bg-[var(--color-surface-alt)]"
          >
            Cart
          </Link>
          <Link
            href="/login"
            className="rounded-[var(--radius-sm)] bg-[var(--color-primary)] px-3 py-2 text-sm font-semibold text-[var(--color-primary-contrast)]"
          >
            Sign In
          </Link>
        </div>
      </StoreContainer>
    </div>
  );
}
