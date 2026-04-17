import Link from "next/link";

import { StoreContainer } from "@/shared/components/layout/store-container";

const storeNavItems = [
  { href: "/", label: "Home" },
  { href: "/products", label: "Shop All" },
  { href: "/cart", label: "Cart" },
  { href: "/checkout", label: "Checkout" },
  { href: "/orders", label: "Orders" },
  { href: "/admin", label: "Admin" },
];

export function StoreNav() {
  return (
    <nav className="border-b border-[var(--color-border)] bg-[var(--color-surface)]">
      <StoreContainer className="flex flex-wrap gap-2 py-2">
        {storeNavItems.map((item) => (
          <Link
            key={item.href}
            href={item.href}
            className="rounded-[var(--radius-sm)] px-3 py-2 text-sm font-semibold text-[var(--color-text-muted)] transition hover:bg-[var(--color-surface-alt)] hover:text-[var(--color-text)]"
          >
            {item.label}
          </Link>
        ))}
      </StoreContainer>
    </nav>
  );
}
