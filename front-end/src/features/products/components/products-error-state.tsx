import { ErrorState } from "@/shared/components/feedback/error-state";

interface ProductsErrorStateProps {
  onRetry: () => void;
}

export function ProductsErrorState({ onRetry }: ProductsErrorStateProps) {
  return (
    <ErrorState
      title="There was an issue loading products"
      message="Please try again. If the issue continues, check products API availability."
      onRetry={onRetry}
    />
  );
}
