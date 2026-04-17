import { ProductDetailScreen } from "@/features/products/components/product-detail-screen";
import { PageContainer } from "@/shared/components/layout/page-container";

interface ProductDetailPageProps {
  params: Promise<{ id: string }>;
}

export default async function ProductDetailPage({ params }: ProductDetailPageProps) {
  const { id } = await params;

  return (
    <PageContainer title="Product Detail" description="Single product view with actionable state.">
      <ProductDetailScreen id={id} />
    </PageContainer>
  );
}
