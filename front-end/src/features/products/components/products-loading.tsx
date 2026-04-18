function SkeletonBlock({ className }: { className: string }) {
  return <div className={`animate-pulse rounded-md bg-slate-200/70 ${className}`} />;
}

export function ProductsLoading() {
  return (
    <div className="grid gap-6 lg:grid-cols-[280px_minmax(0,1fr)]" data-testid="products-loading">
      <aside className="rounded-[var(--radius-xl)] border border-[var(--color-border)] bg-[var(--color-surface)] p-4 shadow-[var(--shadow-card)]">
        <SkeletonBlock className="h-4 w-24" />
        <div className="mt-4 space-y-3">
          <SkeletonBlock className="h-10 w-full" />
          <SkeletonBlock className="h-4 w-20" />
          <SkeletonBlock className="h-4 w-28" />
          <SkeletonBlock className="h-4 w-24" />
        </div>
      </aside>

      <section className="space-y-4">
        <SkeletonBlock className="h-14 w-full" />
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div
              key={index}
              className="rounded-[var(--radius-xl)] border border-[var(--color-border)] bg-[var(--color-surface)] p-4"
            >
              <SkeletonBlock className="h-36 w-full" />
              <SkeletonBlock className="mt-4 h-5 w-2/3" />
              <SkeletonBlock className="mt-3 h-4 w-full" />
              <SkeletonBlock className="mt-2 h-4 w-3/4" />
              <SkeletonBlock className="mt-5 h-10 w-full" />
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
