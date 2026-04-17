import { EmptyState } from "@/shared/components/feedback/empty-state";
import { PageContainer } from "@/shared/components/layout/page-container";

export function AdminOrdersScreen() {
  return (
    <PageContainer title="Admin Orders" description="Order operations will be controlled here.">
      <EmptyState
        title="No order operations yet"
        description="Connect order APIs for status updates and monitoring."
      />
    </PageContainer>
  );
}
