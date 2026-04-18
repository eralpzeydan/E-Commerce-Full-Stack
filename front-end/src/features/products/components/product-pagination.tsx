import { Button } from "@/shared/components/ui/button";

interface ProductPaginationProps {
  page: number;
  totalPages: number;
  onChange: (page: number) => void;
}

export function ProductPagination({ page, totalPages, onChange }: ProductPaginationProps) {
  if (totalPages <= 1) {
    return null;
  }

  const visiblePages = Array.from({ length: totalPages }, (_, index) => index).slice(
    Math.max(0, page - 2),
    Math.min(totalPages, page + 3),
  );

  return (
    <nav className="flex flex-wrap items-center justify-center gap-2" aria-label="Products pagination">
      <Button variant="secondary" disabled={page <= 0} onClick={() => onChange(page - 1)}>
        Previous
      </Button>

      {visiblePages.map((pageItem) => (
        <Button
          key={pageItem}
          variant={pageItem === page ? "primary" : "secondary"}
          onClick={() => onChange(pageItem)}
          aria-current={pageItem === page ? "page" : undefined}
        >
          {pageItem + 1}
        </Button>
      ))}

      <Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>
        Next
      </Button>
    </nav>
  );
}
