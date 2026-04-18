import Link from "next/link";

import { type Product } from "@/features/products/types/product-types";
import { formatPrice } from "@/features/products/utils/price";
import { Badge } from "@/shared/components/ui/badge";
import { Button } from "@/shared/components/ui/button";
import { Card } from "@/shared/components/ui/card";

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const isInStock = product.stock > 0;
  const description = product.description?.trim() || "No description available.";

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-[var(--radius-xl)] border-slate-200 p-0 transition hover:-translate-y-0.5 hover:shadow-xl">
      <div className="flex h-44 items-center justify-center bg-gradient-to-br from-slate-100 via-slate-50 to-white">
        <span className="text-5xl font-black text-slate-300">{product.name.charAt(0).toUpperCase()}</span>
      </div>

      <div className="flex grow flex-col p-4">
        <div className="flex items-center justify-between gap-2">
          <Badge variant="primary">{product.categoryName ?? "General"}</Badge>
          <Badge variant={isInStock ? "success" : "neutral"}>
            {isInStock ? `${product.stock} in stock` : "Out of stock"}
          </Badge>
        </div>

        <h3 className="mt-3 line-clamp-2 text-base font-bold text-[var(--color-text)]">{product.name}</h3>
        <p className="mt-2 line-clamp-3 text-sm text-[var(--color-text-muted)]">{description}</p>

        <div className="mt-4 flex items-end justify-between gap-3">
          <p className="text-2xl font-black text-[var(--color-text)]">{formatPrice(product.price)}</p>
          <Link
            href={`/products/${product.id}`}
            className="inline-flex h-9 items-center justify-center rounded-[var(--radius-md)] border border-[var(--color-border)] bg-[var(--color-surface)] px-3 text-sm font-semibold text-[var(--color-text)] transition hover:bg-[var(--color-surface-alt)]"
          >
            Details
          </Link>
        </div>

        <Button className="mt-3 w-full" disabled={!isInStock}>
          Add to Cart
        </Button>
      </div>
    </Card>
  );
}
