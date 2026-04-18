import { EmptyState } from "@/shared/components/feedback/empty-state";
import { Button } from "@/shared/components/ui/button";

interface ProductsEmptyStateProps {
  onClearFilters: () => void;
}

export function ProductsEmptyState({ onClearFilters }: ProductsEmptyStateProps) {
  return (
    <div className="space-y-4">
      <EmptyState
        title="No products matched these filters"
        description="Try clearing filters or selecting another category."
      />
      <div className="flex justify-center">
        <Button variant="secondary" onClick={onClearFilters}>
          Clear filters
        </Button>
      </div>
    </div>
  );
}
