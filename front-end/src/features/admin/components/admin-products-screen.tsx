import { EmptyState } from "@/shared/components/feedback/empty-state";
import { PageContainer } from "@/shared/components/layout/page-container";

export function AdminProductsScreen() {
  return (
    <PageContainer title="Admin Products" description="CRUD tables and forms live under features/admin.">
      <EmptyState
        title="No admin product UI yet"
        description="Implement product management forms in the next iteration."
      />
    </PageContainer>
  );
}
