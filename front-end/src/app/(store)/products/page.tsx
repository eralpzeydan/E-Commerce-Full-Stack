import { ProductsListScreen } from "@/features/products/components/products-list-screen";
import { PageContainer } from "@/shared/components/layout/page-container";

export default function ProductsPage() {
  return (
    <PageContainer title="Products" description="All products from the backend API.">
      <ProductsListScreen />
    </PageContainer>
  );
}
