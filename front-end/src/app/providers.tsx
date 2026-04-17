"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useEffect, useState, type ReactNode } from "react";

import { setupInterceptors } from "@/shared/api/interceptors";
import { httpClient } from "@/shared/api/http-client";

interface ProvidersProps {
  children: ReactNode;
}

export default function Providers({ children }: ProvidersProps) {
  const [queryClient] = useState(() =>
    new QueryClient({
      defaultOptions: {
        queries: {
          staleTime: 30_000,
          refetchOnWindowFocus: false,
        },
      },
    }),
  );

  useEffect(() => {
    setupInterceptors(httpClient);
  }, []);

  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
