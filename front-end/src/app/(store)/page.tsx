import Link from "next/link";

import { PageContainer } from "@/shared/components/layout/page-container";
import { Button } from "@/shared/components/ui/button";
import { Card } from "@/shared/components/ui/card";

export default function HomePage() {
  return (
    <PageContainer
      title="Storefront"
      description="Production-style route shell with feature-driven pages."
    >
      <Card className="grid gap-5 md:grid-cols-[1fr_auto] md:items-center">
        <div>
          <h2 className="text-xl font-bold text-[var(--color-text)]">
            Start with the products vertical slice
          </h2>
          <p className="mt-2 text-sm text-[var(--color-text-muted)]">
            This route stays thin and delegates business logic to features/products.
          </p>
        </div>
        <Button>
          <Link href="/products">Browse Products</Link>
        </Button>
      </Card>
    </PageContainer>
  );
}
