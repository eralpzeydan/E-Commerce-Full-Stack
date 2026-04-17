import { type HTMLAttributes } from "react";

import { cn } from "@/shared/utils/cn";

type StoreContainerProps = HTMLAttributes<HTMLDivElement>;

export function StoreContainer({ className, ...props }: StoreContainerProps) {
  return (
    <div
      className={cn("mx-auto w-full max-w-[var(--container-max)] px-4 sm:px-6 lg:px-8", className)}
      {...props}
    />
  );
}
