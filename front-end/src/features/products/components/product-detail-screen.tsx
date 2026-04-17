"use client";

import { useProductDetailQuery } from "@/features/products/hooks/use-product-detail-query";
import { formatPrice } from "@/features/products/utils/price";
import { EmptyState } from "@/shared/components/feedback/empty-state";
import { ErrorState } from "@/shared/components/feedback/error-state";
import { Spinner } from "@/shared/components/feedback/spinner";
import { Button } from "@/shared/components/ui/button";
import { Card } from "@/shared/components/ui/card";

interface ProductDetailScreenProps {
  id: string;
}

export function ProductDetailScreen({ id }: ProductDetailScreenProps) {
  const { data, isPending, isError, refetch } = useProductDetailQuery(id);

  if (isPending) {
    return (
      <div className="flex justify-center py-16">
        <Spinner />
      </div>
    );
  }

  if (isError) {
    return (
      <ErrorState
        title="Unable to load product"
        message="Try refreshing this page or checking the product id."
        onRetry={() => void refetch()}
      />
    );
  }

  if (!data) {
    return <EmptyState title="Product not found" description="This product does not exist." />;
  }

  return (
    <Card className="grid gap-8 md:grid-cols-[1fr_320px]">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
          {data.categoryName ?? "General"}
        </p>
        <h1 className="mt-2 text-3xl font-bold text-[var(--color-text)]">{data.name}</h1>
        <p className="mt-4 text-sm leading-7 text-[var(--color-text-muted)]">{data.description}</p>
      </div>
      <aside className="rounded-[var(--radius-md)] border border-[var(--color-border)] bg-[var(--color-surface-alt)] p-4">
        <p className="text-sm text-[var(--color-text-muted)]">Price</p>
        <p className="mt-1 text-2xl font-black text-[var(--color-text)]">{formatPrice(data.price)}</p>
        <p className="mt-3 text-sm text-[var(--color-text-muted)]">In stock: {data.stock}</p>
        <Button className="mt-5 w-full">Add to Cart</Button>
      </aside>
    </Card>
  );
}
