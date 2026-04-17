import { EmptyState } from "@/shared/components/feedback/empty-state";
import { PageContainer } from "@/shared/components/layout/page-container";

export function AdminCategoriesScreen() {
  return (
    <PageContainer title="Admin Categories" description="Category management feature space.">
      <EmptyState
        title="No categories yet"
        description="Hook category endpoints and create CRUD UI here."
      />
    </PageContainer>
  );
}
