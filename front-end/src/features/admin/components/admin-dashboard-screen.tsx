import { Card } from "@/shared/components/ui/card";
import { PageContainer } from "@/shared/components/layout/page-container";

export function AdminDashboardScreen() {
  return (
    <PageContainer title="Admin Dashboard" description="Management entry point for the store.">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <Card>
          <p className="text-sm text-[var(--color-text-muted)]">Products</p>
          <p className="mt-2 text-2xl font-black text-[var(--color-text)]">-</p>
        </Card>
        <Card>
          <p className="text-sm text-[var(--color-text-muted)]">Categories</p>
          <p className="mt-2 text-2xl font-black text-[var(--color-text)]">-</p>
        </Card>
        <Card>
          <p className="text-sm text-[var(--color-text-muted)]">Orders</p>
          <p className="mt-2 text-2xl font-black text-[var(--color-text)]">-</p>
        </Card>
      </div>
    </PageContainer>
  );
}
