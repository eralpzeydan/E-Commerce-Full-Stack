"use client";

import { useCartQuery } from "@/features/cart/hooks/use-cart-query";
import { EmptyState } from "@/shared/components/feedback/empty-state";
import { ErrorState } from "@/shared/components/feedback/error-state";
import { Spinner } from "@/shared/components/feedback/spinner";
import { PageContainer } from "@/shared/components/layout/page-container";
import { Card } from "@/shared/components/ui/card";

export function CartScreen() {
  const { data, isPending, isError, refetch } = useCartQuery();

  return (
    <PageContainer title="Cart" description="Review selected products before checkout.">
      {isPending ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : null}

      {isError ? (
        <ErrorState
          title="Unable to load cart"
          message="Please sign in and try again."
          onRetry={() => void refetch()}
        />
      ) : null}

      {!isPending && !isError && data && data.items.length === 0 ? (
        <EmptyState
          title="Cart is empty"
          description="Add a product to begin the checkout flow."
        />
      ) : null}

      {!isPending && !isError && data && data.items.length > 0 ? (
        <div className="space-y-3">
          {data.items.map((item) => (
            <Card key={item.cartItemId} className="flex items-center justify-between gap-3">
              <div>
                <p className="font-semibold text-[var(--color-text)]">{item.productName}</p>
                <p className="text-sm text-[var(--color-text-muted)]">Qty: {item.quantity}</p>
              </div>
              <p className="font-bold text-[var(--color-text)]">${item.subtotal.toFixed(2)}</p>
            </Card>
          ))}
        </div>
      ) : null}
    </PageContainer>
  );
}
