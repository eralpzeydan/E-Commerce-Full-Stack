import { EmptyState } from "@/shared/components/feedback/empty-state";
import { PageContainer } from "@/shared/components/layout/page-container";

export function CheckoutScreen() {
  return (
    <PageContainer title="Checkout" description="Shipping and payment flow will live in this feature module.">
      <EmptyState
        title="Checkout not started"
        description="Connect cart state and checkout API in the next task."
      />
    </PageContainer>
  );
}
