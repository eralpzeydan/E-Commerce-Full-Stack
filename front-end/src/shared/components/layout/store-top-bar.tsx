import { StoreContainer } from "@/shared/components/layout/store-container";

export function StoreTopBar() {
  return (
    <div className="border-b border-slate-800 bg-slate-950 text-slate-100">
      <StoreContainer className="flex h-9 items-center justify-between text-xs">
        <p>Free shipping for orders over $100</p>
        <p className="text-slate-300">30-day returns on all eligible products</p>
      </StoreContainer>
    </div>
  );
}
