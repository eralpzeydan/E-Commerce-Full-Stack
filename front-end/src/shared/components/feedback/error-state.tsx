import { Button } from "@/shared/components/ui/button";

interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = "Something went wrong",
  message = "Please try again.",
  onRetry,
}: ErrorStateProps) {
  return (
    <div className="rounded-[var(--radius-lg)] border border-red-200 bg-red-50 p-6 text-red-900">
      <p className="text-base font-semibold">{title}</p>
      <p className="mt-1 text-sm">{message}</p>
      {onRetry ? (
        <Button className="mt-4" variant="secondary" onClick={onRetry}>
          Retry
        </Button>
      ) : null}
    </div>
  );
}
