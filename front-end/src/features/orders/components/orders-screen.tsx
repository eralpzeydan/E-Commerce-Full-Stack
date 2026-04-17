"use client";

import { useOrdersAccessQuery } from "@/features/orders/hooks/use-orders-access-query";
import { EmptyState } from "@/shared/components/feedback/empty-state";
import { ErrorState } from "@/shared/components/feedback/error-state";
import { Spinner } from "@/shared/components/feedback/spinner";
import { PageContainer } from "@/shared/components/layout/page-container";

export function OrdersScreen() {
  const { data, isPending, isError, refetch } = useOrdersAccessQuery();

  return (
    <PageContainer title="Orders" description="Order history and statuses will be rendered here.">
      {isPending ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : null}

      {isError ? (
        <ErrorState
          title="Unable to load orders"
          message="You may need to sign in to access this area."
          onRetry={() => void refetch()}
        />
      ) : null}

      {!isPending && !isError ? (
        <EmptyState
          title="Orders module is ready"
          description={data ?? "Orders endpoint connection established."}
        />
      ) : null}
    </PageContainer>
  );
}
