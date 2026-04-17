import Link from "next/link";

import { type Product } from "@/features/products/types/product.types";
import { formatPrice } from "@/features/products/utils/price";
import { Badge } from "@/shared/components/ui/badge";
import { Card } from "@/shared/components/ui/card";

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const isInStock = product.stock > 0;

  return (
    <Card className="flex h-full flex-col justify-between gap-4 rounded-[var(--radius-xl)] p-4">
      <div>
        <div className="flex items-center justify-between gap-2">
          <Badge variant="primary">{product.categoryName ?? "General"}</Badge>
          <Badge variant={isInStock ? "success" : "neutral"}>
            {isInStock ? `${product.stock} in stock` : "Out of stock"}
          </Badge>
        </div>

        <h3 className="mt-3 text-lg font-bold text-[var(--color-text)]">{product.name}</h3>
        <p className="mt-2 line-clamp-3 text-sm text-[var(--color-text-muted)]">
          {product.description}
        </p>
      </div>

      <div className="flex items-center justify-between gap-3">
        <p className="text-xl font-extrabold text-[var(--color-text)]">
          {formatPrice(product.price)}
        </p>
        <Link
          href={`/products/${product.id}`}
          className="inline-flex h-9 items-center justify-center rounded-[var(--radius-md)] bg-[var(--color-primary)] px-3 text-sm font-semibold text-[var(--color-primary-contrast)] transition hover:brightness-95"
        >
          View Details
        </Link>
      </div>
    </Card>
  );
}
