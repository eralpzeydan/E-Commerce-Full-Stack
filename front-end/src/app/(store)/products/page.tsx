import { Suspense } from "react";

import { ProductsLoading } from "@/features/products/components/products-loading";
import { ProductsScreen } from "@/features/products/components/products-screen";
import { PageContainer } from "@/shared/components/layout/page-container";

function ProductsPageFallback() {
  return (
    <PageContainer
      title="Shop Products"
      description="Browse products with backend-powered filtering, sorting, and pagination."
    >
      <ProductsLoading />
    </PageContainer>
  );
}

export default function ProductsPage() {
  return (
    <Suspense fallback={<ProductsPageFallback />}>
      <ProductsScreen />
    </Suspense>
  );
}
